package com.fakkaflow.ui.view;

import com.fakkaflow.data.model.*;
import com.fakkaflow.data.repository.CategoryRepository;
import com.fakkaflow.logic.service.*;
import com.fakkaflow.ui.util.UIFactory;
import javafx.collections.*;
import javafx.geometry.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;

import java.time.LocalDate;
import java.util.List;

public class BudgetScreen {
    private final BudgetService budgetService = new BudgetService();
    private final AlertingService alertingService = new AlertingService();
    private final CategoryRepository categoryRepository = new CategoryRepository();
    private final int userId = SessionManager.getInstance().getCurrentUserId();

    public Pane build() {
        HBox root = new HBox();
        root.getStyleClass().add("app-root");
        VBox nav = new SideNav().build(SideNav.NavItem.BUDGET);
        VBox content = buildContent();
        HBox.setHgrow(content, Priority.ALWAYS);
        root.getChildren().addAll(nav, content);
        return root;
    }

    private VBox buildContent() {
        VBox content = new VBox(24);
        content.getStyleClass().add("content-area");
        content.setPadding(new Insets(32));

        Label heading = UIFactory.heading("Budgets");
        Label sub = UIFactory.subheading("Set spending limits and track your cycle");

        VBox cycleSection = buildCycleSection();
        VBox alertsSection = buildAlertsSection();
        VBox budgetsSection = buildBudgetsSection();

        content.getChildren().addAll(heading, sub, cycleSection, alertsSection, budgetsSection);
        return content;
    }

    private VBox buildCycleSection() {
        VBox card = UIFactory.card("📅 Budget Cycle");
        BudgetCycle cycle = budgetService.getCurrentCycle(userId);

        if (cycle != null) {
            HBox info = new HBox(40);
            info.getChildren().addAll(
                infoItem("Allowance", String.format("%.2f EGP", cycle.getTotalAllowance())),
                infoItem("Remaining", String.format("%.2f EGP", budgetService.calculateRemainingBalance(userId))),
                infoItem("Daily Limit", String.format("%.2f EGP", budgetService.calculateSafeDailyLimit(userId))),
                infoItem("Period", cycle.getStartDate() + " → " + cycle.getEndDate())
            );
            card.getChildren().add(info);
        }

        Button setupBtn = UIFactory.secondaryBtn(cycle == null ? "Setup Budget Cycle" : "Edit Cycle");
        setupBtn.setOnAction(e -> showCycleDialog(cycle));
        card.getChildren().add(setupBtn);
        return card;
    }

    private VBox buildAlertsSection() {
        List<Budget> budgets = budgetService.getBudgets(userId);
        List<AlertingService.BudgetAlert> alerts = alertingService.checkBudgets(budgets);
        if (alerts.isEmpty()) return new VBox();

        VBox section = UIFactory.card("🔔 Budget Alerts");
        for (AlertingService.BudgetAlert alert : alerts) {
            String level = alert.level() == AlertingService.AlertLevel.EXCEEDED ? "exceeded" : "warning";
            String icon = alert.level() == AlertingService.AlertLevel.EXCEEDED ? "🔴" : "🟡";
            String msg = String.format("%s %s: %.1f%% (%.2f / %.2f EGP)",
                icon, alert.category(), alert.percent(), alert.spent(), alert.limit());
            section.getChildren().add(UIFactory.alertBadge(msg, level));
        }
        return section;
    }

    private VBox buildBudgetsSection() {
        VBox section = UIFactory.card("💳 Category Budgets");

        HBox topRow = new HBox(12);
        topRow.setAlignment(Pos.CENTER_LEFT);
        Button addBtn = UIFactory.primaryBtn("+ Add Budget");
        addBtn.setMaxWidth(160);
        addBtn.setOnAction(e -> showAddBudgetDialog(section));
        topRow.getChildren().add(addBtn);
        section.getChildren().add(topRow);

        List<Budget> budgets = budgetService.getBudgets(userId);
        if (budgets.isEmpty()) {
            Label msg = new Label("No category budgets set. Add one to start tracking!");
            msg.getStyleClass().add("muted-text");
            section.getChildren().add(msg);
        } else {
            for (Budget b : budgets) {
                section.getChildren().add(buildBudgetRow(b, section));
            }
        }
        return section;
    }

    private HBox buildBudgetRow(Budget b, VBox parent) {
        HBox row = new HBox(16);
        row.setAlignment(Pos.CENTER_LEFT);
        row.getStyleClass().add("budget-row");

        VBox info = new VBox(4);
        Label catLbl = new Label(b.getCategoryName());
        catLbl.getStyleClass().add("budget-cat");
        Label periodLbl = new Label(b.getPeriod() + " · Limit: " + String.format("%.2f EGP", b.getAmountLimit()));
        periodLbl.getStyleClass().add("muted-text");
        info.getChildren().addAll(catLbl, periodLbl);
        HBox.setHgrow(info, Priority.ALWAYS);

        VBox progressBox = new VBox(4);
        progressBox.setMinWidth(200);
        double pct = b.getUsagePercent();
        ProgressBar pb = new ProgressBar(Math.min(pct / 100.0, 1.0));
        pb.setPrefWidth(200);
        pb.getStyleClass().add("budget-progress");
        if (pct >= 100) pb.getStyleClass().add("progress-danger");
        else if (pct >= 80) pb.getStyleClass().add("progress-warning");
        else pb.getStyleClass().add("progress-ok");
        Label pctLbl = new Label(String.format("%.1f%% (%.2f / %.2f EGP)", pct, b.getSpent(), b.getAmountLimit()));
        pctLbl.getStyleClass().add("muted-text");
        progressBox.getChildren().addAll(pb, pctLbl);

        Button delBtn = UIFactory.dangerBtn("Delete");
        delBtn.setOnAction(e -> {
            budgetService.deleteBudget(b.getId());
            com.fakkaflow.ui.util.SceneManager.getInstance().navigateTo(new BudgetScreen().build(), "Budgets");
        });

        row.getChildren().addAll(info, progressBox, delBtn);
        return row;
    }

    private void showCycleDialog(BudgetCycle existing) {
        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("Budget Cycle Setup");
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        VBox form = new VBox(14);
        form.setPadding(new Insets(20));
        form.setPrefWidth(400);

        TextField allowanceField = UIFactory.styledField("Total allowance (e.g. 5000.00)");
        if (existing != null) allowanceField.setText(String.valueOf(existing.getTotalAllowance()));

        DatePicker startPicker = new DatePicker(existing != null ? existing.getStartDate() : LocalDate.now());
        startPicker.setMaxWidth(Double.MAX_VALUE);
        DatePicker endPicker = new DatePicker(existing != null ? existing.getEndDate() : LocalDate.now().plusMonths(1));
        endPicker.setMaxWidth(Double.MAX_VALUE);

        Label errLbl = UIFactory.errorLabel();

        form.getChildren().addAll(
            new Label("Total Allowance (EGP):"), allowanceField,
            new Label("Start Date:"), startPicker,
            new Label("End Date:"), endPicker,
            errLbl
        );

        dialog.getDialogPane().setContent(form);

        Button okBtn = (Button) dialog.getDialogPane().lookupButton(ButtonType.OK);
        okBtn.addEventFilter(javafx.event.ActionEvent.ACTION, e -> {
            errLbl.setText("");
            try {
                float amount = Float.parseFloat(allowanceField.getText().trim());
                LocalDate start = startPicker.getValue();
                LocalDate end = endPicker.getValue();
                budgetService.startCycle(userId, amount, start, end);
                com.fakkaflow.ui.util.SceneManager.getInstance().navigateTo(new BudgetScreen().build(), "Budgets");
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

    private void showAddBudgetDialog(VBox parent) {
        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("Add Category Budget");
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        VBox form = new VBox(14);
        form.setPadding(new Insets(20));
        form.setPrefWidth(380);

        ComboBox<Category> catCombo = new ComboBox<>();
        catCombo.getItems().addAll(categoryRepository.findAll());
        if (!catCombo.getItems().isEmpty()) catCombo.setValue(catCombo.getItems().get(0));
        catCombo.setMaxWidth(Double.MAX_VALUE);
        catCombo.getStyleClass().add("styled-combo");

        TextField limitField = UIFactory.styledField("Spending limit (e.g. 1000.00)");

        ComboBox<String> periodCombo = new ComboBox<>();
        periodCombo.getItems().addAll("Monthly", "Weekly", "Daily");
        periodCombo.setValue("Monthly");
        periodCombo.setMaxWidth(Double.MAX_VALUE);
        periodCombo.getStyleClass().add("styled-combo");

        Label errLbl = UIFactory.errorLabel();
        form.getChildren().addAll(
            new Label("Category:"), catCombo,
            new Label("Spending Limit (EGP):"), limitField,
            new Label("Period:"), periodCombo,
            errLbl
        );

        dialog.getDialogPane().setContent(form);

        Button okBtn = (Button) dialog.getDialogPane().lookupButton(ButtonType.OK);
        okBtn.addEventFilter(javafx.event.ActionEvent.ACTION, e -> {
            errLbl.setText("");
            try {
                if (catCombo.getValue() == null) throw new Exception("Please select a category.");
                float limit = Float.parseFloat(limitField.getText().trim());
                if (limit <= 0) throw new Exception("Limit must be greater than zero.");
                Budget b = new Budget(userId, catCombo.getValue().getCategoryId(), limit, periodCombo.getValue());
                budgetService.saveBudget(b);
                com.fakkaflow.ui.util.SceneManager.getInstance().navigateTo(new BudgetScreen().build(), "Budgets");
            } catch (NumberFormatException ex) {
                errLbl.setText("Please enter a valid number.");
                e.consume();
            } catch (Exception ex) {
                errLbl.setText(ex.getMessage());
                e.consume();
            }
        });

        dialog.showAndWait();
    }

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
