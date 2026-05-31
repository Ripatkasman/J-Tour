package j.tour;

import j.tour.view.LoginView;
import javafx.application.Application;
import javafx.stage.Stage;

public class App extends Application {
    @Override
    public void start(Stage primaryStage) {
        new LoginView().show(primaryStage);
    }

    public static void main(String[] args) {
        launch(args);
    }
}