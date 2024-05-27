import controller.ServerController;
import model.SessionScheduler;

public class ServerApplication {
    public static void main(String[] args) {
        SessionScheduler scheduler = new SessionScheduler();
        ServerController serverController = new ServerController(1234, scheduler);

        System.out.println("Starting server...");
        serverController.start();
    }
}
