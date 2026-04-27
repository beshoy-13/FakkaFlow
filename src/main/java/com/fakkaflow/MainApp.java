package com.fakkaflow;

import com.fakkaflow.data.repository.SQLiteDatabase;
import com.fakkaflow.ui.util.SceneManager;
import com.fakkaflow.ui.view.LoginScreen;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

public class MainApp extends Application {

    @Override
    public void start(Stage primaryStage) {
        SQLiteDatabase.getInstance().initializeSchema();

        SceneManager sm = SceneManager.getInstance();
        sm.setPrimaryStage(primaryStage);

        primaryStage.setTitle("FakkaFlow — Personal Budgeting");
        primaryStage.setMinWidth(900);
        primaryStage.setMinHeight(600);

        Pane loginRoot = new LoginScreen().build();
        Scene scene = new Scene(loginRoot, 1100, 720);
        scene.getStylesheets().add(getClass().getResource("/styles.css").toExternalForm());
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
