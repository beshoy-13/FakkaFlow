package com.fakkaflow.ui.view;

import com.fakkaflow.data.model.User;
import com.fakkaflow.data.model.UserSettings;
import com.fakkaflow.data.repository.UserSettingsRepository;
import com.fakkaflow.logic.service.SessionManager;
import com.fakkaflow.ui.util.SceneManager;
import com.fakkaflow.ui.util.UIFactory;
import javafx.geometry.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.shape.Circle;
import javafx.scene.paint.Color;
/**
 * Profile screen for displaying user information and managing settings.
 * Allows users to:
 * - View profile details
 * - Update preferences (currency, language, notifications)
 */
public class ProfileScreen {
    private final UserSettingsRepository settingsRepo = new UserSettingsRepository();
    private final int userId = SessionManager.getInstance().getCurrentUserId();
    private final User user  = SessionManager.getInstance().getCurrentUser();

    /**
     * Builds main layout.
     *
     * @return root Pane
     */
    public Pane build() {
        HBox root = new HBox();
        root.getStyleClass().add("app-root");
        VBox nav     = new SideNav().build(SideNav.NavItem.PROFILE);
        VBox content = buildContent();
        HBox.setHgrow(content, Priority.ALWAYS);
        root.getChildren().addAll(nav, content);
        return root;
    }
    /**
     * Builds main content.
     */
    private VBox buildContent() {
        VBox content = new VBox(24);
        content.getStyleClass().add("content-area");
        content.setPadding(new Insets(32));

        Label heading = UIFactory.heading("Profile & Settings");
        Label sub     = UIFactory.subheading("Manage your account and preferences");

        content.getChildren().addAll(heading, sub, buildProfileCard(), buildSettingsCard());
        return content;
    }
    /**
     * Builds profile card with user info.
     */
    private VBox buildProfileCard() {
        VBox card = UIFactory.card("Account");

        Circle avatar = new Circle(36);
        avatar.setFill(Color.web("#1e2433"));
        avatar.setStroke(Color.web("#6366f1"));
        avatar.setStrokeWidth(2);

        Label initials = new Label(getInitials(user.getName()));
        initials.setStyle("-fx-text-fill: #a5b4fc; -fx-font-size: 20px; -fx-font-weight: bold;");

        StackPane avatarStack = new StackPane(avatar, initials);

        Label nameLbl  = new Label(user.getName());
        nameLbl.setStyle("-fx-text-fill: #f1f5f9; -fx-font-size: 18px; -fx-font-weight: bold;");

        Label emailLbl = new Label(user.getEmail());
        emailLbl.getStyleClass().add("muted-text");

        VBox info = new VBox(4, nameLbl, emailLbl);
        info.setAlignment(Pos.CENTER_LEFT);

        HBox row = new HBox(20, avatarStack, info);
        row.setAlignment(Pos.CENTER_LEFT);

        card.getChildren().add(row);
        return card;
    }
    /**
     * Builds settings card for user preferences.
     */
    private VBox buildSettingsCard() {
        UserSettings settings = settingsRepo.load(userId);
        VBox card = UIFactory.card("Preferences");

        Label currencyLbl = new Label("Currency");
        currencyLbl.getStyleClass().add("muted-text");

        ComboBox<String> currencyCombo = new ComboBox<>();
        currencyCombo.getItems().addAll("EGP", "USD", "EUR", "GBP", "SAR", "AED");
        currencyCombo.setValue(settings.getCurrency());
        currencyCombo.getStyleClass().add("styled-combo");
        currencyCombo.setMaxWidth(Double.MAX_VALUE);

        Label langLbl = new Label("Language");
        langLbl.getStyleClass().add("muted-text");

        ComboBox<String> langCombo = new ComboBox<>();
        langCombo.getItems().addAll("English", "Arabic");
        langCombo.setValue(settings.getLanguage());
        langCombo.getStyleClass().add("styled-combo");
        langCombo.setMaxWidth(Double.MAX_VALUE);

        Label notifsLbl = new Label("Budget Notifications");
        notifsLbl.getStyleClass().add("muted-text");

        CheckBox notifsCheck = new CheckBox("Enable budget alerts");
        notifsCheck.setSelected(settings.isNotificationsEnabled());
        notifsCheck.setStyle("-fx-text-fill: #e2e8f0;");

        Label errLbl    = UIFactory.errorLabel();
        Label successLbl = new Label();
        successLbl.setStyle("-fx-text-fill: #22c55e; -fx-font-size: 13px;");

        Button saveBtn = UIFactory.primaryBtn("Save Settings");
        saveBtn.setMaxWidth(200);
        saveBtn.setOnAction(e -> {
            errLbl.setText("");
            successLbl.setText("");
            try {
                settings.setCurrency(currencyCombo.getValue());
                settings.setLanguage(langCombo.getValue());
                settings.setNotificationsEnabled(notifsCheck.isSelected());
                settingsRepo.save(settings);
                successLbl.setText("Settings saved successfully.");
            } catch (Exception ex) {
                errLbl.setText("Failed to save settings.");
            }
        });

        GridPane grid = new GridPane();
        grid.setHgap(16);
        grid.setVgap(14);
        grid.addRow(0, currencyLbl, currencyCombo);
        grid.addRow(1, langLbl,     langCombo);
        grid.addRow(2, notifsLbl,   notifsCheck);
        grid.getColumnConstraints().addAll(
            colConstraint(120),
            colConstraint(240)
        );

        card.getChildren().addAll(grid, errLbl, successLbl, saveBtn);
        return card;
    }

    /**
     * Creates column constraint helper.
     */
    private ColumnConstraints colConstraint(double w) {
        ColumnConstraints c = new ColumnConstraints();
        c.setPrefWidth(w);
        return c;
    }
    /**
     * Extracts initials from user name.
     */

    private String getInitials(String name) {
        if (name == null || name.isEmpty()) return "?";
        String[] parts = name.trim().split(" ");
        if (parts.length == 1) return parts[0].substring(0, 1).toUpperCase();
        return (parts[0].substring(0, 1) + parts[1].substring(0, 1)).toUpperCase();
    }
}
