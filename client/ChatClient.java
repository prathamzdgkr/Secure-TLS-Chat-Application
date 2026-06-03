package client;

import java.net.*;
import java.io.*;
import javax.net.ssl.SSLSocketFactory;

public class ChatClient {

    Socket socket;
    BufferedReader in;
    PrintWriter out;
    private long pingStartTime;

    public ChatClient(String username, ChatFrame frame) throws Exception {

        SSLSocketFactory ssf = (SSLSocketFactory) SSLSocketFactory.getDefault();
        socket = ssf.createSocket("serverIP", 1222);

        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        out = new PrintWriter(socket.getOutputStream(), true);

        out.println(username);

        new Thread(() -> {
            try {
                String msg;
                while ((msg = in.readLine()) != null) {

                    if (msg.equals("[PONG]")) {
                        long rtt = System.currentTimeMillis() - pingStartTime;
                        frame.appendMessage("[System] : Latency (RTT) to secure server is " + rtt + " ms", false);
                    }
                    else if (msg.startsWith("[USERS]")) {
                        String userString = msg.substring(7);
                        String[] users = userString.split(",");
                        frame.updateActiveUsers(users);
                    }
                    else if (msg.contains(" : [FILE]")) {
                        if (msg.startsWith("[Private to ")) {
                            String toUser = msg.substring(12, msg.indexOf("]"));
                            String fileDataStr = msg.substring(msg.indexOf(" : [FILE]") + 9);
                            int pipeIndex = fileDataStr.indexOf("|");
                            if(pipeIndex != -1) {
                                String fileName = fileDataStr.substring(0, pipeIndex);
                                frame.appendMessage("[Private to " + toUser + "] : Sent file " + fileName, true);
                            }
                            continue;
                        }

                        int fileTagIndex = msg.indexOf(" : [FILE]");
                        String senderName = msg.substring(0, fileTagIndex);
                        String fileDataStr = msg.substring(fileTagIndex + 9);
                        int pipeIndex = fileDataStr.indexOf("|");

                        if (pipeIndex != -1) {
                            String fileName = fileDataStr.substring(0, pipeIndex);
                            String base64Data = fileDataStr.substring(pipeIndex + 1);
                            frame.receiveFile(senderName, fileName, base64Data);
                        }
                    } else {
                        msg = msg.replace("[NL]", "\n");
                        frame.appendMessage(msg, false);
                    }
                }
            } catch (Exception e) {
                System.out.println("Secure connection lost.");
            }
        }).start();
    }

    public void send(String msg) {
        msg = msg.replace("\n", "[NL]");
        out.println(msg);
    }

    public void sendPing() {
        pingStartTime = System.currentTimeMillis();
        out.println("[PING]");
    }

    public Socket getSocket() {
        return socket;
    }
}