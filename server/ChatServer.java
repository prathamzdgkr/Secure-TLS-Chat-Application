package server;

import java.net.*;
import java.io.*;
import java.util.*;
import java.util.concurrent.*;
import javax.net.ssl.*;

public class ChatServer {

    private static final int PORT = 1222;
    private static final int MAX_THREADS = 50;

    private static ExecutorService pool =
            Executors.newFixedThreadPool(MAX_THREADS);

    static List<ClientHandler> clients =
            new CopyOnWriteArrayList<>();

    private static long serverStartTime;

    private static int totalConnections = 0;
    private static int totalDisconnections = 0;

    private static int broadcastMessages = 0;
    private static int privateMessages = 0;
    private static int failedPrivateMessages = 0;

    private static int secureSessions = 0;
    private static String tlsVersion = "";
    private static String cipherSuite = "";

    public static void main(String[] args) {

        System.setProperty("javax.net.ssl.keyStore", "serverkeystore.jks");
        System.setProperty("javax.net.ssl.keyStorePassword", System.getenv("KEYSTORE_PASSWORD"));

        try {
            SSLServerSocketFactory ssf =
                    (SSLServerSocketFactory) SSLServerSocketFactory.getDefault();

            SSLServerSocket serverSocket =
                    (SSLServerSocket) ssf.createServerSocket(PORT);

            System.out.println("Secure TLS Chat Server Started on Port " + PORT);

            serverStartTime = System.currentTimeMillis();
            startDashboard();

            while (true) {

                SSLSocket socket = (SSLSocket) serverSocket.accept();
                socket.startHandshake();

                secureSessions++;
                totalConnections++;

                SSLSession session = socket.getSession();
                tlsVersion = session.getProtocol();
                cipherSuite = session.getCipherSuite();

                ClientHandler client = new ClientHandler(socket);
                clients.add(client);

                pool.execute(client);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void startDashboard() {

        new Thread(() -> {
            while (true) {
                try {
                    Thread.sleep(10000);
                    printDashboard();
                } catch (Exception ignored) {}
            }
        }).start();
    }

    private static void printDashboard() {

        long uptime =
                (System.currentTimeMillis() - serverStartTime) / 1000;

        Runtime runtime = Runtime.getRuntime();

        long totalMem = runtime.totalMemory() / (1024 * 1024);
        long freeMem = runtime.freeMemory() / (1024 * 1024);
        long usedMem = totalMem - freeMem;

        int activeThreads =
                ((ThreadPoolExecutor) pool).getActiveCount();

        System.out.println("\n====================================================");
        System.out.println("           SECURE TLS SERVER DASHBOARD              ");
        System.out.println("====================================================");

        System.out.println("\nSERVER STATUS");
        System.out.println("Uptime                 : " + uptime + " sec");
        System.out.println("Active Connections     : " + clients.size());

        System.out.println("\nTCP CONNECTION STATISTICS");
        System.out.println("Total Connections      : " + totalConnections);
        System.out.println("Disconnections         : " + totalDisconnections);

        System.out.println("\nMESSAGE STATISTICS");
        System.out.println("Broadcast Messages     : " + broadcastMessages);
        System.out.println("Private Messages       : " + privateMessages);
        System.out.println("Failed Private Msg     : " + failedPrivateMessages);

        System.out.println("\nTLS SECURITY STATUS");
        System.out.println("TLS Version            : " + tlsVersion);
        System.out.println("Cipher Suite           : " + cipherSuite);
        System.out.println("Secure Sessions        : " + secureSessions);

        System.out.println("\nPERFORMANCE METRICS");
        System.out.println("Active Threads         : " + activeThreads + "/" + MAX_THREADS);
        System.out.println("Memory Usage           : " + usedMem + "MB / " + totalMem + "MB");

        System.out.println("\nACTIVE TCP ROUTING TABLE");
        System.out.println("----------------------------------------------------");
        System.out.printf("%-12s | %-15s | %-6s | %-8s\n",
                "User", "IP", "Port", "Uptime");
        System.out.println("----------------------------------------------------");

        for (ClientHandler c : clients) {

            Socket s = c.getSocket();

            if (s != null && !s.isClosed()) {

                String user =
                        (c.getUsername() == null)
                                ? "Connecting"
                                : c.getUsername();

                System.out.printf("%-12s | %-15s | %-6d | %-8d\n",
                        user,
                        s.getInetAddress().getHostAddress(),
                        s.getPort(),
                        c.getUptimeSeconds());
            }
        }

        System.out.println("====================================================\n");
    }

    public static synchronized void broadcast(String msg, ClientHandler sender) {
        broadcastMessages++;

        for (ClientHandler c : clients) {
            if (c != sender)
                c.sendMessage(msg);
        }
    }

    public static synchronized void sendPrivateMessage(
            String targetUser,
            String message,
            ClientHandler sender) {

        boolean found = false;

        for (ClientHandler c : clients) {
            if (c.getUsername() != null &&
                c.getUsername().equals(targetUser)) {

                c.sendMessage("[Private from "
                        + sender.getUsername() + "] : " + message);

                found = true;
                privateMessages++;
                break;
            }
        }

        if (found)
            sender.sendMessage("[Private to "
                    + targetUser + "] : " + message);
        else {
            sender.sendMessage("[System] : User not online");
            failedPrivateMessages++;
        }
    }

    public static synchronized void broadcastUserList() {

        StringBuilder list = new StringBuilder("[USERS]");

        for (ClientHandler c : clients)
            if (c.getUsername() != null)
                list.append(c.getUsername()).append(",");

        for (ClientHandler c : clients)
            c.sendMessage(list.toString());
    }

    public static synchronized void remove(ClientHandler client) {
        clients.remove(client);
        totalDisconnections++;
        broadcastUserList();
    }
}