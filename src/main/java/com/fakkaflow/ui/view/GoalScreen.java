package com.fakkaflow.ui.view;

import com.fakkaflow.data.model.Goal;
import com.fakkaflow.logic.service.*;
import com.fakkaflow.ui.util.UIFactory;
import javafx.geometry.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;

import java.util.List;
/**
 * Screen for managing financial goals.
 * Allows users to:
 * - Create goals
 * - Track progress
 * - Add contributions
 */
public class GoalScreen {
    private final GoalService goalService = new GoalService();
    private final int userId = SessionManager.getInstance().getCurrentUserId();

    /**
     * Builds main layout.
     */
    public Pane build() {
        HBox root = new HBox();
        root.getStyleClass().add("app-root");
        VBox nav = new SideNav().build(SideNav.NavItem.GOALS);
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

        Label heading = UIFactory.heading("Financial Goals");
        Label sub = UIFactory.subheading("Track your savings targets and milestones");

        HBox topRow = new HBox();
        Button addBtn = UIFactory.primaryBtn("+ Add Goal");
        addBtn.setMaxWidth(160);
        addBtn.setOnAction(e -> showAddGoalDialog());
        topRow.getChildren().add(addBtn);

        VBox goalsSection = buildGoalsList();

        content.getChildren().addAll(heading, sub, topRow, goalsSection);
        return content;
    }
    /**
     * Builds goals list section.
     */
    private VBox buildGoalsList() {
        VBox section = new VBox(16);
        List<Goal> goals = goalService.getGoals(userId);

        if (goals.isEmpty()) {
            VBox empty = UIFactory.card(null);
            Label msg = new Label("🎯 No goals yet. Create your first savings goal!");
            msg.getStyleClass().add("muted-text");
            msg.setStyle("-fx-font-size: 15px;");
            empty.setAlignment(Pos.CENTER);
            empty.setPadding(new Insets(40));
            empty.getChildren().add(msg);
            section.getChildren().add(empty);
            return section;
        }

        for (Goal goal : goals) {
            section.getChildren().add(buildGoalCard(goal));
        }
        return section;
    }
    /**
     * Builds a single goal card UI.
     *
     * @param goal goal object
     */

    private VBox buildGoalCard(Goal goal) {
        VBox card = UIFactory.card(null);
        card.getStyleClass().add("goal-card");

        HBox header = new HBox();
        header.setAlignment(Pos.CENTER_LEFT);

        Label nameLbl = new Label("🎯 " + goal.getName());
        nameLbl.getStyleClass().add("goal-name");
        HBox.setHgrow(nameLbl, Priority.ALWAYS);

        Button contributeBtn = UIFactory.secondaryBtn("+ Contribute");
        contributeBtn.setOnAction(e -> showContributeDialog(goal));

        Button deleteBtn = UIFactory.dangerBtn("Delete");
        deleteBtn.setOnAction(e -> {
            goalService.deleteGoal(goal.getId());
            com.fakkaflow.ui.util.SceneManager.getInstance().navigateTo(new GoalScreen().build(), "Goals");
        });

        header.getChildren().addAll(nameLbl, contributeBtn, new Region() {{ setPrefWidth(8); }}, deleteBtn);
        card.getChildren().add(header);

        HBox statsRow = new HBox(40);
        statsRow.getChildren().addAll(
            infoItem("Target", String.format("%.2f EGP", goal.getTargetAmount())),
            infoItem("Saved", String.format("%.2f EGP", goal.getSavedAmount())),
            infoItem("Remaining", String.format("%.2f EGP", goal.getRemaining())),
            infoItem("Deadline", goal.getDeadline() != null ? goal.getDeadline() : "—"),
            infoItem("Monthly Needed", String.format("%.2f EGP", goalService.calculateMonthlySavingsNeeded(goal)))
        );
        card.getChildren().add(statsRow);

        double pct = goal.getProgressPercent();
        ProgressBar pb = new ProgressBar(pct / 100.0);
        pb.setMaxWidth(Double.MAX_VALUE);
        pb.getStyleClass().add("goal-progress");
        if (pct >= 100) pb.getStyleClass().add("progress-ok");
        else if (pct >= 60) pb.getStyleClass().add("progress-warning");
        else pb.getStyleClass().add("progress-danger");

        Label pctLbl = new Label(String.format("%.1f%% complete", pct));
        pctLbl.getStyleClass().add("muted-text");

        card.getChildren().addAll(pb, pctLbl);
        return card;
    }
    /**
     * Shows dialog to create new goal.
     */
    private void showAddGoalDialog() {
        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("Create Financial Goal");
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        VBox form = new VBox(14);
        form.setPadding(new Insets(20));
        form.setPrefWidth(400);

        TextField nameField = UIFactory.styledField("Goal name (e.g. New Car)");
        TextField targetField = UIFactory.styledField("Target amount (e.g. 50000.00)");
        TextField savedField = UIFactory.styledField("Initial saved amount (optional, 0)");
        savedField.setText("0");
        DatePicker deadlinePicker = new DatePicker();
        deadlinePicker.setPromptText("Deadline (optional)");
        deadlinePicker.setMaxWidth(Double.MAX_VALUE);

        Label errLbl = UIFactory.errorLabel();

        form.getChildren().addAll(
            new Label("Goal Name:"), nameField,
            new Label("Target Amount (EGP):"), targetField,
            new Label("Initial Saved (EGP):"), savedField,
            new Label("Deadline:"), deadlinePicker,
            errLbl
        );

        dialog.getDialogPane().setContent(form);

        Button okBtn = (Button) dialog.getDialogPane().lookupButton(ButtonType.OK);
        okBtn.addEventFilter(javafx.event.ActionEvent.ACTION, e -> {
            errLbl.setText("");
            try {
                String name = nameField.getText().trim();
                float target = Float.parseFloat(targetField.getText().trim());
                float saved = savedField.getText().trim().isEmpty() ? 0 : Float.parseFloat(savedField.getText().trim());
                String deadline = deadlinePicker.getValue() != null ? deadlinePicker.getValue().toString() : null;

                Goal goal = new Goal(userId, name, target, saved, deadline);
                goalService.saveGoal(goal);
                com.fakkaflow.ui.util.SceneManager.getInstance().navigateTo(new GoalScreen().build(), "Goals");
            } catch (NumberFormatException ex) {
                errLbl.setText("Please enter valid numbers for amounts.");
                e.consume();
            } catch (Exception ex) {
                errLbl.setText(ex.getMessage());
                e.consume();
            }
        });

        dialog.showAndWait();
    }
    /**
     * Shows dialog to contribute to goal.
     *
     * @param goal goal object
     */
    private void showContributeDialog(Goal goal) {
        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("Add Contribution to: " + goal.getName());
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        VBox form = new VBox(14);
        form.setPadding(new Insets(20));
        form.setPrefWidth(340);

        Label infoLbl = new Label(String.format("Remaining: %.2f EGP", goal.getRemaining()));
        infoLbl.getStyleClass().add("muted-text");

        TextField amountField = UIFactory.styledField("Contribution amount (e.g. 500.00)");
        Label errLbl = UIFactory.errorLabel();

        form.getChildren().addAll(infoLbl, new Label("Amount (EGP):"), amountField, errLbl);
        dialog.getDialogPane().setContent(form);

        Button okBtn = (Button) dialog.getDialogPane().lookupButton(ButtonType.OK);
        okBtn.addEventFilter(javafx.event.ActionEvent.ACTION, e -> {
            errLbl.setText("");
            try {
                float amount = Float.parseFloat(amountField.getText().trim());
                goalService.addContribution(goal.getId(), amount);
                com.fakkaflow.ui.util.SceneManager.getInstance().navigateTo(new GoalScreen().build(), "Goals");
            } catch (NumberFormatException ex) {
                errLbl.setText("Please enter a valid amount.");
                e.consume();
            } catch (Exception ex) {
                errLbl.setText(ex.getMessage());
                e.consume();
            }
        });

        dialog.showAndWait();
    }
    /**
     * Helper info item.
     */
    private VBox infoItem(String label, String value) {
        VBox box = new VBox(4);
        Label lbl = new Label(label);
        lbl.getStyleClass().add("muted-text");
        Label val = new Label(value);
        val.getStyleClass().add("info-value");
        box.getChildren().addAll(lbl, val);
        return box;
    }
}
