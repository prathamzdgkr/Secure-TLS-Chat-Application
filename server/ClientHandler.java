package server;

import java.io.*;
import java.net.*;

public class ClientHandler implements Runnable {

    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;
    private String username;
    private long connectionTime;

    public ClientHandler(Socket socket) {
        this.socket = socket;
        this.connectionTime = System.currentTimeMillis();
    }

    public String getUsername() {
        return username;
    }

    public Socket getSocket() {
        return socket;
    }

    public long getUptimeSeconds() {
        return (System.currentTimeMillis() - connectionTime) / 1000;
    }

    @Override
    public void run() {
        try {

            in = new BufferedReader(
                    new InputStreamReader(socket.getInputStream()));

            out = new PrintWriter(
                    socket.getOutputStream(), true);

            /* USER LOGIN */
            username = in.readLine();

            System.out.println(username +
                    " joined from " +
                    socket.getInetAddress().getHostAddress());

            ChatServer.broadcast(
                    "[System] : " + username + " joined the chat",
                    this);

            ChatServer.broadcastUserList();

            /* MESSAGE LOOP */
            String message;

            while ((message = in.readLine()) != null) {

                /* RTT PING SUPPORT */
                if (message.equals("[PING]")) {
                    sendMessage("[PONG]");
                    continue;
                }

                /* PRIVATE MESSAGE */
                if (message.startsWith("@")) {

                    int space = message.indexOf(" ");

                    if (space > 1) {

                        String target =
                                message.substring(1, space);

                        String actualMsg =
                                message.substring(space + 1);

                        ChatServer.sendPrivateMessage(
                                target,
                                actualMsg,
                                this);

                    } else {
                        sendMessage(
                                "[System] : Invalid format. Use @username message");
                    }

                }
                /* BROADCAST MESSAGE */
                else {

                    String fullMsg =
                            username + " : " + message;

                    ChatServer.broadcast(fullMsg, this);
                }
            }

        } catch (Exception e) {

            System.out.println(username + " disconnected");

        } finally {

            ChatServer.remove(this);

            if (username != null) {
                ChatServer.broadcast(
                        "[System] : " + username + " left the chat",
                        this);
            }

            try {
                socket.close();
            } catch (Exception ignored) {}
        }
    }

    /* SEND MESSAGE SAFELY */
    public synchronized void sendMessage(String msg) {

        if (out != null) {
            out.println(msg);
        }
    }
}