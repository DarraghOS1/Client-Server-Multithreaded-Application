package controller;
import java.io.*;
import java.net.*;
public class ConnectionHandler implements Runnable {
    private final Socket link;
    private final SessionController sessionController;

    /**
     * Constructor for the ConnectionHandler class
     * @param link Socket link
     * @param sessionController SessionController sessionController
     */
    public ConnectionHandler(Socket link, SessionController sessionController) {
        this.link = link;
        this.sessionController = sessionController;
    }

    /**
     * The run() method of the ConnectionHandler. Establishes connections to clients reading in messages and sending responses
     */
    @Override
    public void run() {
        try (BufferedReader in = new BufferedReader(new InputStreamReader(link.getInputStream()));
             PrintWriter out = new PrintWriter(link.getOutputStream(), true)) {

            String message = in.readLine();
            String[] parts = message.split("\\s+", 2);
            String identifier = parts[0];
            String remainder = parts.length > 1 ? parts[1] : "";

            try {
                String response = sessionController.handleIdentifier(identifier, remainder);
                System.out.println(response);
                out.println(response);
            } catch (IncorrectActionException e) {
                out.println("ERROR " + e.getMessage());
            }

        } catch (IOException e) {
            System.out.println("Connection error: " + e.getMessage());
        }
    }
}

