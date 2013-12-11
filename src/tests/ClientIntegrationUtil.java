package tests;

import java.io.BufferedInputStream;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

import client.ClientApplication;

public class ClientIntegrationUtil {

    private static final int port = 4444;
    private static ServerSocket serverSocket;
    private static Socket socket;
    private static Thread listener;
    private static ClientApplication client;

    public static void start() throws IOException {
        serverSocket = new ServerSocket(port);
        client = new ClientApplication("127.0.0.1", 4444);
        listener = new Thread(new Runnable() {

            @Override
            public void run() {
                try {
                    socket = serverSocket.accept();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        });
        listener.start();
    }

    public static ClientApplication getClient() {
        return client;
    }

    public static void send(String message) {

        try {
            listener.join();
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            throw new RuntimeException();
        }

        PrintWriter outputStream;
        try {
            outputStream = new PrintWriter(socket.getOutputStream(), true);
            outputStream.println(message);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            throw new RuntimeException();
        }
    }
}
