import controller.Controller;
import javafx.application.Application;
import javafx.stage.Stage;
import view.SchedulerView;

public class ClientApp extends Application {
    @Override
    public void start(Stage primaryStage) {
        // Initialize the view
        SchedulerView view = new SchedulerView(primaryStage);

        Controller controller = new Controller(view);

        view.setController(controller);

        view.showMainScene();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
