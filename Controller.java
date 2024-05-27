package controller;
import javafx.application.Platform;
import javafx.concurrent.Task;
import model.Session;
import view.SchedulerView;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
public class Controller {
    private static final int PORT = 1234;
    private SchedulerView view;
    private InetAddress host;

    /**
     *Constructor for Controller of Client Application
     * @param view SchedulerView parameter
     */

    public Controller(SchedulerView view) {
        this.view = view;
    }

    /**
     * Handles the case where the 'ADD' button is pressed
     */
    public void onAddSession() {
        view.addSessionScene();
    }

    /**
     * Handles the case where the 'REMOVE' button is pressed
     */
    public void onRemoveSession() {
        view.removeSessionScene();
    }

    /**
     * Handles the case where the 'DISPLAY' button is pressed
     */
    public void onDisplayOption() {
        view.allOrClassScene("DISPLAY");
    }

    /**
     * Handles the case where the 'EARLY LECTURES' button is pressed
     */
    public void onEarlyLectures() {
        view.allOrClassScene("EARLY_LECTURES");
    }

    /**
     *Calls method to send a message to the server
     * @param message String consisting of an identifier and a session to be added or removed
     */
    public void onSessionSend(String message){
        sendRequest(message);
    }

    /**
     * Handles the case where the 'STOP' button is pressed
     */
    public void onStopServer() {
        sendRequest("STOP");
    }

    /**
     * Calls method to send method to the server
     * @param message String consisting of identifier and either 'ALL' or class name
     */
    public void onAllOrClassMessage(String message){
        sendRequest(message);
    }

    /**
     * Sends message to the server
     * @param message String being sent to the server
     */
    private void sendRequest(String message) {
        Task<String> task = new Task<>() {
            @Override
            protected String call(){
                try {
                    host = InetAddress.getLocalHost();
                    Socket link = new Socket(host, PORT);
                    BufferedReader in = new BufferedReader(new InputStreamReader(link.getInputStream()));
                    PrintWriter out = new PrintWriter(link.getOutputStream(), true);

                    out.println(message);
                    String response = in.readLine();

                    link.close();
                    return response;
                } catch (UnknownHostException ex) {
                    return "Host ID not found!";
                } catch (IOException ex) {
                    return "Error communicating with server!";
                }
            }
        };

        task.setOnSucceeded(event -> {
            handleResponse(task.getValue());
        });

        task.setOnFailed(event -> {
            Throwable cause = task.getException();
            String errorMessage = "Error connecting to server.";
            if (cause != null) {
                errorMessage += " Reason: " + cause.getMessage();
            }
            final String errorFinalMessage = errorMessage;

            Platform.runLater(() -> {
                view.showAlert("Communication Error", errorFinalMessage);
            });
        });


        Thread thread = new Thread(task);
        thread.setDaemon(true);
        thread.start();
    }

    /**
     * Handles the server's response and calls the appropriate GUI method to display it
     * @param response
     */
    private void handleResponse(String response) {
        if (response.equals("TERMINATE")) {
            Platform.runLater(() -> {
                view.showAlert("Server Terminated", "The server has terminated.");
                view.getStage().close();
            });
            return;
        }


        if (response.startsWith("SUCCESS") || response.startsWith("ERROR")) {
            String prefix = response.startsWith("SUCCESS") ? "SUCCESS" : "ERROR";
            String message = response.substring(prefix.length()).trim();
            view.showAlert(prefix, message);
            view.showMainScene();
            return;
        }

        String cleanResponse = removeDisplayPrefix(response);
        ArrayList<Session> sessions = parseSessions(cleanResponse);
        if (isDisplayingAll(response)) {
            Platform.runLater(()->view.displayAllSessions(sessions));
        } else {
            Platform.runLater(()->view.displayClassSessions(sessions));
        }
    }

    /**
     * Handles the case where the response is for either 'DISPLAY ALL' or 'DISPLAY classname'
     * and removes this prefix from the response
     * @param response String response from the server
     * @return String response with prefix removed
     */
    private String removeDisplayPrefix(String response) {
        String[] splitResponse = response.split(";");
        if (splitResponse.length < 2) {
            return "";
        }
        return splitResponse[1];
    }

    /**
     * Check to see if the response starts with 'DISPLAY ALL'
     * @param response String response from the server
     * @return true or false depending on if the response starts with 'DISPLAY ALL'
     */
    private boolean isDisplayingAll(String response) {
        return response.startsWith("DISPLAY ALL");
    }

    /**
     * Parses the sever response into an ArrayList of Sessions
     * @param response String response from server consisting of Sessions
     * @return ArrayList<Session> which can be sent to view method
     */
    private ArrayList<Session> parseSessions(String response) {
        ArrayList<Session> sessions = new ArrayList<>();
        String[] sessionDescriptions = response.split(",\\s*");

        for (String description : sessionDescriptions) {
            try {
                String[] parts = description.trim().split(" ");
                if (parts.length < 5) {
                    throw new IllegalArgumentException("Session data is incomplete: " + description);
                }

                Session session = new Session(description.trim());
                sessions.add(session);
            } catch (IllegalArgumentException e) {
                view.showAlert("Skipping invalid session: " + description," Error: " + e.getMessage());
            } catch (DateTimeParseException e) {
                view.showAlert("Date/Time parsing error in session: " + description, "Error: " + e.getMessage());
            }
        }

        return sessions;
    }
}
