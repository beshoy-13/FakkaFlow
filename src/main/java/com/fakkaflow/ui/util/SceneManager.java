package com.fakkaflow.ui.util;

import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.layout.Pane;
/**
 * Singleton class responsible for managing scene navigation in the application.
 * Handles switching between different UI screens.
 */
public class SceneManager {
    private static SceneManager instance;
    private Stage primaryStage;
    /**
     * Private constructor to enforce singleton pattern.
     */
    private SceneManager() {}

    /**
     * Returns the single instance of SceneManager.
     *
     * @return SceneManager instance
     */
    public static SceneManager getInstance() {
        if (instance == null) instance = new SceneManager();
        return instance;
    }
    /**
     * Sets the primary stage of the application.
     *
     * @param stage JavaFX primary stage
     */
    public void setPrimaryStage(Stage stage) { this.primaryStage = stage; }
    /**
     * Returns the primary stage.
     *
     * @return Stage object
     */
    public Stage getPrimaryStage() { return primaryStage; }
    /**
     * Navigates to a new scene.
     *
     * @param root root layout pane
     * @param title window title
     */
    public void navigateTo(Pane root, String title) {
        Scene scene = new Scene(root, 1100, 720);
        scene.getStylesheets().add(getClass().getResource("/styles.css").toExternalForm());
        primaryStage.setScene(scene);
        primaryStage.setTitle("FakkaFlow — " + title);
        primaryStage.centerOnScreen();
    }
}
