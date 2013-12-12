package tests.integration;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

import client.ClientApplication;

public class ClientIntegrationUtil {

    private static ServerSocket serverSocket;
    private static Socket socket;
    private static Thread listener;
    private static ClientApplication client;

    public static void start(int port) throws IOException {
        serverSocket = new ServerSocket(port);
        client = new ClientApplication("127.0.0.1", port);
        listener = new Thread(new Runnable() {

            @Override
            public void run() {
                try {
                    socket = serverSocket.accept();
                } catch (IOException e) {
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
            e.printStackTrace();
            throw new RuntimeException();
        }

        PrintWriter outputStream;
        try {
            outputStream = new PrintWriter(socket.getOutputStream(), true);
            outputStream.println(message);
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException();
        }
    }

    public static String nextNonEmptyLine() throws IOException {

        BufferedReader in = new BufferedReader(new InputStreamReader(
                socket.getInputStream()));
        while (true) {
            String ret = in.readLine();
            if (ret == null || !ret.equals(""))
                return ret;
        }
    }
}
