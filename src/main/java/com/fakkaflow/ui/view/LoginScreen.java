package com.fakkaflow.ui.view;

import com.fakkaflow.logic.service.AuthService;
import com.fakkaflow.ui.util.SceneManager;
import com.fakkaflow.ui.util.UIFactory;
import javafx.geometry.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.TextAlignment;

public class LoginScreen {
    private final AuthService authService = new AuthService();

    public Pane build() {
        HBox root = new HBox();
        root.getStyleClass().add("auth-root");

        VBox leftPanel = buildLeftPanel();
        VBox formPanel = buildFormPanel();

        HBox.setHgrow(leftPanel, Priority.ALWAYS);
        HBox.setHgrow(formPanel, Priority.ALWAYS);
        root.getChildren().addAll(leftPanel, formPanel);
        return root;
    }

    private VBox buildLeftPanel() {
        VBox panel = new VBox(20);
        panel.getStyleClass().add("auth-left");
        panel.setAlignment(Pos.CENTER);
        panel.setPadding(new Insets(60));

        Label icon = new Label("💰");
        icon.setStyle("-fx-font-size: 72px;");

        Label title = new Label("FakkaFlow");
        title.getStyleClass().add("brand-title");

        Label tagline = new Label("Welcome back!\nYour finances are waiting.");
        tagline.getStyleClass().add("brand-tagline");
        tagline.setTextAlignment(TextAlignment.CENTER);
        tagline.setWrapText(true);

        panel.getChildren().addAll(icon, title, tagline);
        return panel;
    }

    private VBox buildFormPanel() {
        VBox panel = new VBox(20);
        panel.getStyleClass().add("auth-right");
        panel.setAlignment(Pos.CENTER);
        panel.setPadding(new Insets(60, 80, 60, 80));
        panel.setMaxWidth(480);

        Label heading = UIFactory.heading("Sign In");
        Label sub = UIFactory.subheading("Enter your credentials to continue");

        TextField emailField = UIFactory.styledField("Email address");
        PasswordField passField = UIFactory.styledPassword("Password");
        Label errorLbl = UIFactory.errorLabel();

        Button loginBtn = UIFactory.primaryBtn("Sign In");
        loginBtn.setOnAction(e -> {
            errorLbl.setText("");
            try {
                authService.loginUser(emailField.getText(), passField.getText());
                SceneManager.getInstance().navigateTo(new DashboardScreen().build(), "Dashboard");
            } catch (Exception ex) {
                errorLbl.setText(ex.getMessage());
            }
        });

        passField.setOnAction(e -> loginBtn.fire());

        Separator sep = new Separator();

        HBox signupRow = new HBox(8);
        signupRow.setAlignment(Pos.CENTER);
        Label signupTxt = new Label("Don't have an account?");
        signupTxt.getStyleClass().add("muted-text");
        Button signupLink = new Button("Create Account");
        signupLink.getStyleClass().add("link-btn");
        signupLink.setOnAction(e -> SceneManager.getInstance().navigateTo(new SignUpScreen().build(), "Sign Up"));
        signupRow.getChildren().addAll(signupTxt, signupLink);

        panel.getChildren().addAll(heading, sub, emailField, passField, errorLbl, loginBtn, sep, signupRow);
        return panel;
    }
}
