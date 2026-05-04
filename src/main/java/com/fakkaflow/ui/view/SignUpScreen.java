package com.fakkaflow.ui.view;

import com.fakkaflow.logic.service.AuthService;
import com.fakkaflow.ui.util.SceneManager;
import com.fakkaflow.ui.util.UIFactory;
import javafx.geometry.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.TextAlignment;
/**
 * Sign-up screen for creating a new user account.
 * Includes validation and navigation to dashboard upon success.
 */
public class SignUpScreen {
    private final AuthService authService = new AuthService();
    /**
     * Builds the main layout.
     *
     * @return root Pane
     */
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
    /**
     * Builds left branding and features panel.
     */

    private VBox buildLeftPanel() {
        VBox panel = new VBox(20);
        panel.getStyleClass().add("auth-left");
        panel.setAlignment(Pos.CENTER);
        panel.setPadding(new Insets(60));

        Label icon = new Label("💰");
        icon.setStyle("-fx-font-size: 72px;");

        Label title = new Label("FakkaFlow");
        title.getStyleClass().add("brand-title");

        Label tagline = new Label("Smart money management\nfor everyday life");
        tagline.getStyleClass().add("brand-tagline");
        tagline.setTextAlignment(TextAlignment.CENTER);
        tagline.setWrapText(true);

        VBox features = new VBox(14);
        features.setAlignment(Pos.CENTER_LEFT);
        features.setPadding(new Insets(30, 0, 0, 0));
        for (String f : new String[]{"📊 Track income & expenses", "🎯 Set financial goals", "📈 Visual reports & insights", "🔔 Smart budget alerts"}) {
            Label feat = new Label(f);
            feat.getStyleClass().add("feature-item");
            features.getChildren().add(feat);
        }

        panel.getChildren().addAll(icon, title, tagline, features);
        return panel;
    }
    /**
     * Builds the registration form panel.
     * Handles input validation and account creation.
     */
    private VBox buildFormPanel() {
        VBox panel = new VBox(20);
        panel.getStyleClass().add("auth-right");
        panel.setAlignment(Pos.CENTER);
        panel.setPadding(new Insets(60, 80, 60, 80));
        panel.setMaxWidth(480);

        Label heading = UIFactory.heading("Create Account");
        Label sub = UIFactory.subheading("Start managing your finances today");

        TextField nameField = UIFactory.styledField("Full name");
        TextField emailField = UIFactory.styledField("Email address");
        PasswordField passField = UIFactory.styledPassword("Password (min 6 chars)");
        PasswordField confirmField = UIFactory.styledPassword("Confirm password");

        Label errorLbl = UIFactory.errorLabel();

        Button signUpBtn = UIFactory.primaryBtn("Create Account");
        signUpBtn.setOnAction(e -> {
            errorLbl.setText("");
            String name = nameField.getText().trim();
            String email = emailField.getText().trim();
            String pass = passField.getText();
            String confirm = confirmField.getText();

            if (!pass.equals(confirm)) {
                errorLbl.setText("Passwords do not match.");
                return;
            }
            try {
                authService.registerUser(name, email, pass);
                SceneManager.getInstance().navigateTo(new DashboardScreen().build(), "Dashboard");
            } catch (Exception ex) {
                errorLbl.setText(ex.getMessage());
            }
        });

        Separator sep = new Separator();

        HBox loginRow = new HBox(8);
        loginRow.setAlignment(Pos.CENTER);
        Label loginTxt = new Label("Already have an account?");
        loginTxt.getStyleClass().add("muted-text");
        Button loginLink = new Button("Sign In");
        loginLink.getStyleClass().add("link-btn");
        loginLink.setOnAction(e -> SceneManager.getInstance().navigateTo(new LoginScreen().build(), "Sign In"));
        loginRow.getChildren().addAll(loginTxt, loginLink);

        panel.getChildren().addAll(heading, sub, nameField, emailField, passField, confirmField, errorLbl, signUpBtn, sep, loginRow);
        return panel;
    }
}
