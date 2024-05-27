package controller;
import model.SessionScheduler;
import java.io.*;
import java.net.*;
public class ServerController {
    private final ServerSocket serverSocket;
    private final SessionScheduler scheduler;
    private volatile boolean running = true;

    /**
     * Constructor for the ServerController class
     * @param port int port
     * @param scheduler SessionScheduler scheduler
     */
    public ServerController(int port, SessionScheduler scheduler) {
        try {
            this.serverSocket = new ServerSocket(port);
            this.scheduler = scheduler;
        } catch (IOException e) {
            throw new RuntimeException("Failed to create server socket", e);
        }
    }

    /**
     * Start method which establishes a new Thread for each client connection
     */
    public void start() {
        while (running) {
            try {
                Socket clientSocket = serverSocket.accept();
                new Thread(new ConnectionHandler(clientSocket, new SessionController(scheduler))).start();
            } catch (IOException e) {
                System.out.println("Error accepting client connection: " + e.getMessage());
            }
        }
    }
    public void stop() {
        running = false;
        try {
            if (serverSocket != null) {
                serverSocket.close();
            }
        } catch (IOException e) {
            System.out.println("Error closing server socket: " + e.getMessage());
        }
    }
}
