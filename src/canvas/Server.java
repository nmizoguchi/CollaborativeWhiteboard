package canvas;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.atomic.AtomicInteger;

public class Server {
	
    private final ServerSocket serverSocket;
    private static Canvas servercanvas;
    private AtomicInteger numofCustomer = new AtomicInteger(0);

    private Server() throws IOException {
        serverSocket = new ServerSocket(12345);
        System.out.println("socket ready!");
    }
    
    /*
     * The basic function to handle client connection
     */
    public void serve() throws IOException, InterruptedException {
    	
    	while (true) {
            // block until a client connects
            final Socket socket = serverSocket.accept();
            
            System.out.println("connection ready!");
            
            Thread thread = new Thread(new Client());
            numofCustomer.incrementAndGet();
            thread.start();
        }
    }
    
    /**
     * Handle a single client connection. Returns when client disconnects.
     * 
     * @param socket socket where the client is connected
     * @throws IOException if connection has an error or terminates unexpectedly
     * @throws InterruptedException 
     */
    private void handleConnection(Socket socket) throws IOException, InterruptedException {
        BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
        
        out.println("Welcome to Whiteboard. "  + numofCustomer + " people are drawing including you.");
        
        ObjectOutputStream a = new ObjectOutputStream(new FileOutputStream("E:\\mitjdw-nickmm-rcha.whiteboard"));
        
        System.out.println("Output ready!");
                
        try {
            for (String line = in.readLine(); line != null; line = in.readLine()) {
                a.writeObject(handleRequest(line));
            }
        } finally {
            out.close();
            in.close();
        }
    }
    
    /*
     * The server side parser
     */
    private Canvas handleRequest(String input) throws InterruptedException {
        String regex = "(line -?\\d+ -?\\d+ -?\\d+ -?\\d+)|(curve -?\\d+ -?\\d+)";
        if ( ! input.matches(regex)) {
            // invalid input
            return null;
        }
        String[] tokens = input.split(" ");
        int x1 = Integer.parseInt(tokens[1]);
        int y1 = Integer.parseInt(tokens[2]);
        int x2 = Integer.parseInt(tokens[3]);
        int y2 = Integer.parseInt(tokens[4]);
        if (tokens[0].equals("line")) {
        	servercanvas.drawLineSegment(x1, y1, x2, y2);
        }
        /*} else if (tokens[0].equals("curve")) {
            return(servercanvas.add(comp, index));
        }*/
        // Should never get here--make sure to return in each of the valid cases above.
		return servercanvas;
    }
    
/*
    public static void main(String[] args) throws IOException, InterruptedException {
    	servercanvas = new Canvas(500, 600, true);
    	
    	Server myServer = new Server();
    	System.out.println("Server ready!");
    	myServer.serve();
    }
*/   
}
