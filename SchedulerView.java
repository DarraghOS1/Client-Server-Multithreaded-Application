package view;
import controller.Controller;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.VPos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.geometry.Pos;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import model.Session;
import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
public class SchedulerView {
    private Stage stage;
    private Scene mainScene;
    private Controller controller;

    /**
     * Constructor for ScheduleView class
     * @param stage Stage used for ScheduleView
     */
    public SchedulerView(Stage stage) {
        this.stage = stage;
        createMainScene();
    }

    /**
     * Creates the main scene of the GUI containing the users main option buttons
     */
    private void createMainScene() {
        HBox buttonBox = new HBox(10);
        buttonBox.setAlignment(Pos.CENTER);

        Button addButton = new Button("Add");
        Button removeButton = new Button("Remove");
        Button displayButton = new Button("Display");
        Button stopButton = new Button("Stop");
        Button earlyLecturesButton = new Button("Early Lectures");

        addButton.setOnAction(e -> controller.onAddSession());
        removeButton.setOnAction(e -> controller.onRemoveSession());
        displayButton.setOnAction(e -> controller.onDisplayOption());
        stopButton.setOnAction(e -> controller.onStopServer());
        earlyLecturesButton.setOnAction(e -> controller.onEarlyLectures());

        buttonBox.getChildren().addAll(addButton, removeButton, displayButton, earlyLecturesButton, stopButton);

        this.mainScene = new Scene(buttonBox, 600, 300);

        this.stage.setTitle("Class Scheduler");
        this.stage.setScene(this.mainScene);
        this.stage.show();
    }

    /**
     * Displays the main scene
     */
    public void showMainScene() {
        Platform.runLater(() -> {
            stage.setScene(mainScene);
            stage.show();
        });
    }

    /**
     * Displays a new scene giving the user the option of selecting all or a specific class name
     * @param identifier
     */
    public void allOrClassScene(String identifier){
        Button allButton = new Button("All");
        TextField classTextField = new TextField();
        classTextField.setPromptText("Enter class name...");

        Button submitButton = new Button("Submit");

        HBox classInputBox = new HBox(10);
        classInputBox.setAlignment(Pos.CENTER);
        classInputBox.getChildren().addAll(new Label("Class:"), classTextField, submitButton);

        classInputBox.setVisible(false);

        allButton.setOnAction(e -> {
            controller.onAllOrClassMessage(identifier + " ALL");
            classInputBox.setVisible(false);
        });

        Button classButton = new Button("Class");
        classButton.setOnAction(e -> {
            classInputBox.setVisible(true);
        });

        submitButton.setOnAction(e -> {
            String className = classTextField.getText();
            if (!className.isEmpty()) {
                controller.onAllOrClassMessage(identifier + " " + className.toUpperCase());
            }
        });


        VBox layout = new VBox(20);
        layout.setAlignment(Pos.CENTER);

        HBox topBox = new HBox(10, allButton, classButton);
        topBox.setAlignment(Pos.CENTER);

        layout.getChildren().addAll(topBox, classInputBox);

        updateScene(new Scene(layout, 600, 300));
    }

    /**
     * Displays scene that allows a user to input a Session in the correct format that they want to be added
     */
    public void addSessionScene() {
        Label dayLabel = new Label("Day:");
        ChoiceBox<String> dayChoiceBox = new ChoiceBox<>();
        dayChoiceBox.getItems().addAll("Monday", "Tuesday", "Wednesday", "Thursday", "Friday");
        dayChoiceBox.setValue("Monday");

        Label startTimeLabel = new Label("Start Time (HH:mm):");
        TextField startTimeField = new TextField();

        Label endTimeLabel = new Label("End Time (HH:mm):");
        TextField endTimeField = new TextField();

        Label roomLabel = new Label("Room No.:");
        TextField roomField = new TextField();

        Label classLabel = new Label("Class Name:");
        TextField classField = new TextField();

        Label descriptionLabel = new Label("Description:");
        ChoiceBox<String> descriptionChoiceBox = new ChoiceBox<>();
        descriptionChoiceBox.getItems().addAll("Lecture","Tutorial","Lab");
        descriptionChoiceBox.setValue("Lecture");

        Button submitButton = new Button("Submit");

        submitButton.setOnAction(e -> {
            String message = "ADD " + dayChoiceBox.getValue() + " " +
                    startTimeField.getText() + " " + endTimeField.getText() + " " +
                    roomField.getText() + " " + classField.getText() + " " + descriptionChoiceBox.getValue();

            controller.onSessionSend(message);

            showMainScene();
        });

        GridPane gridPane = new GridPane();
        gridPane.setPadding(new Insets(10));
        gridPane.setHgap(10);
        gridPane.setVgap(10);
        gridPane.setAlignment(Pos.CENTER);

        gridPane.add(dayLabel, 0, 0);
        gridPane.add(dayChoiceBox, 1, 0);
        gridPane.add(startTimeLabel, 0, 1);
        gridPane.add(startTimeField, 1, 1);
        gridPane.add(endTimeLabel, 0, 2);
        gridPane.add(endTimeField, 1, 2);
        gridPane.add(roomLabel, 0, 3);
        gridPane.add(roomField, 1, 3);
        gridPane.add(classLabel, 0, 4);
        gridPane.add(classField, 1, 4);
        gridPane.add(descriptionLabel,0,5);
        gridPane.add(descriptionChoiceBox,1,5);


        gridPane.add(submitButton, 1, 6);

        updateScene( new Scene(gridPane, 600, 300));
    }

    /**
     * Displays scene that allows a user to input a Session in the correct format that they want to be removed
     */
    public void removeSessionScene() {
        Label dayLabel = new Label("Day:");
        ChoiceBox<String> dayChoiceBox = new ChoiceBox<>();
        dayChoiceBox.getItems().addAll("Monday", "Tuesday", "Wednesday", "Thursday", "Friday");
        dayChoiceBox.setValue("Monday");

        Label startTimeLabel = new Label("Start Time (HH:mm):");
        TextField startTimeField = new TextField();

        Label endTimeLabel = new Label("End Time (HH:mm):");
        TextField endTimeField = new TextField();

        Label roomLabel = new Label("Room No.:");
        TextField roomField = new TextField();


        Button submitButton = new Button("Submit");

        submitButton.setOnAction(e -> {
            String message = "REMOVE " + dayChoiceBox.getValue() + " " +
                    startTimeField.getText() + " " + endTimeField.getText() + " " +
                    roomField.getText();

            controller.onSessionSend(message);

            showMainScene();
        });

        GridPane gridPane = new GridPane();
        gridPane.setPadding(new Insets(10));
        gridPane.setHgap(10);
        gridPane.setVgap(10);
        gridPane.setAlignment(Pos.CENTER);

        gridPane.add(dayLabel, 0, 0);
        gridPane.add(dayChoiceBox, 1, 0);
        gridPane.add(startTimeLabel, 0, 1);
        gridPane.add(startTimeField, 1, 1);
        gridPane.add(endTimeLabel, 0, 2);
        gridPane.add(endTimeField, 1, 2);
        gridPane.add(roomLabel, 0, 3);
        gridPane.add(roomField, 1, 3);

        gridPane.add(submitButton, 1, 6);

        updateScene( new Scene(gridPane, 600, 300));
    }

    public void setController(Controller controller) {
        this.controller = controller;
    }

    /**
     * Updates the scene showing to a different scene
     * @param scene Scene that will be displayed
     */
    public void updateScene(Scene scene) {
        Platform.runLater(() -> {
            stage.setScene(scene);
            stage.show();
        });
    }

    public Scene getMainScene() {
        return this.mainScene;
    }
    public Stage getStage(){
        return this.stage;
    }

    /**
     * Displays a Scene that displays all the Sessions on the server
     * @param sessions List of all the Sessions on the server
     */
    public void displayAllSessions(List<Session> sessions) {
        TableView<Session> table = new TableView<>();

        TableColumn<Session, DayOfWeek> dayCol = new TableColumn<>("Day");
        dayCol.setCellValueFactory(new PropertyValueFactory<>("day"));

        TableColumn<Session, LocalTime> startTimeCol = new TableColumn<>("Start Time");
        startTimeCol.setCellValueFactory(new PropertyValueFactory<>("startTime"));

        TableColumn<Session, LocalTime> endTimeCol = new TableColumn<>("End Time");
        endTimeCol.setCellValueFactory(new PropertyValueFactory<>("endTime"));

        TableColumn<Session, String> roomCol = setTableColumn("Room", "room");
        TableColumn<Session, String> classCol = setTableColumn("Class Name", "className");
        TableColumn<Session, String> descriptionCol = setTableColumn("Description", "description");

        TableColumn<Session, Void> buttonCol = new TableColumn<>("Action");
        buttonCol.setCellFactory(param -> new TableCell<Session, Void>() {
            private final Button button = new Button("Return");

            {
                button.setOnAction(event -> {
                    stage.setScene(mainScene);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(button);
                }
            }
        });

        table.getColumns().addAll(dayCol, startTimeCol, endTimeCol, roomCol, classCol,descriptionCol,buttonCol);

        ObservableList<Session> sessionList = FXCollections.observableArrayList(sessions);
        table.setItems(sessionList);

        StackPane stackPane = new StackPane(table);
        updateScene(new Scene(stackPane, 600, 300));
    }
    private TableColumn<Session, String> setTableColumn(String title, String property) {
        TableColumn<Session, String> col = new TableColumn<>(title);
        col.setCellValueFactory(new PropertyValueFactory<>(property));
        return col;
    }

    /**
     * Displays a Scene that displays all the Sessions of a specific class in a grid pane style
     * @param sessions List of Sessions for a specific class
     */
    public void displayClassSessions(List<Session> sessions) {
        GridPane grid = new GridPane();
        grid.setGridLinesVisible(true);

        int numColumns = 6;
        int numRows = 11;

        for (int i = 0; i < numColumns; i++) {
            ColumnConstraints colConst = new ColumnConstraints();
            colConst.setPercentWidth(100.0 / (numColumns - 1));
            grid.getColumnConstraints().add(colConst);
        }

        for (int i = 0; i < numRows; i++) {
            RowConstraints rowConst = new RowConstraints();
            rowConst.setPercentHeight(100.0 / (numRows - 1));
            grid.getRowConstraints().add(rowConst);
        }

        DayOfWeek[] daysOfWeek = {DayOfWeek.MONDAY, DayOfWeek.TUESDAY, DayOfWeek.WEDNESDAY,
                DayOfWeek.THURSDAY, DayOfWeek.FRIDAY};

        for (int i = 0; i < daysOfWeek.length; i++) {
            Label dayLabel = new Label(daysOfWeek[i].name());
            grid.add(dayLabel, i + 1, 0);
            GridPane.setHalignment(dayLabel, HPos.CENTER);
        }

        LocalTime startTime = LocalTime.of(9, 0);
        Map<LocalTime, Integer> timeIndexMap = new HashMap<>();
        for (int i = 0; i < 10; i++) {
            LocalTime time = startTime.plusHours(i);
            timeIndexMap.put(time, i + 1);
            Label timeLabel = new Label(time.toString());
            grid.add(timeLabel, 0, i + 1);
            GridPane.setHalignment(timeLabel, HPos.CENTER);
        }

        for (Session session : sessions) {
            DayOfWeek day = session.getDay();
            LocalTime start = session.getStartTime();
            int durationInHours = session.getSessionLength().toHoursPart();
            int col = day.ordinal();

            for(int i=0;i<durationInHours;i++){
                Integer row = timeIndexMap.get(start.plusHours(i));
                if (row != null) {
                    Text sessionText = new Text(session.getDescription() + "\nRoom: " + session.getRoom());
                    sessionText.setWrappingWidth(80);

                    grid.add(sessionText, col + 1, row);
                    GridPane.setHalignment(sessionText, HPos.CENTER);
                    GridPane.setValignment(sessionText, VPos.CENTER);
                }
            }

        }
        String className = sessions.get(0).getClassName();
        Text titleText = new Text("Class Schedule: " + className);
        titleText.setFont(new Font(18));

        Button returnButton = new Button("Return");
        returnButton.setOnAction(e -> showMainScene());

        VBox container = new VBox(10, titleText, grid, returnButton);
        container.setAlignment(Pos.CENTER);

        StackPane stackPane = new StackPane(container);
        Scene scene = new Scene(stackPane, 600, 500);


        updateScene(scene);
    }

    /**
     * Displays an alert of the server response
     * @param title
     * @param message
     */
    public void showAlert(String title, String message) {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.INFORMATION, message, ButtonType.OK);
            alert.setTitle(title);
            alert.showAndWait();
        });
    }
}
