package com.fakkaflow.ui.view;

import com.fakkaflow.data.model.*;
import com.fakkaflow.logic.service.*;
import com.fakkaflow.ui.util.UIFactory;
import javafx.geometry.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;

import java.util.List;
/**
 * Dashboard screen displaying financial overview.
 * Shows:
 * - Balance and stats
 * - Current budget cycle
 * - Alerts
 * - Recent transactions
 */
public class DashboardScreen {
    private final BudgetService budgetService = new BudgetService();
    private final AlertingService alertingService = new AlertingService();
    private final int userId = SessionManager.getInstance().getCurrentUserId();
    /**
     * Builds the main layout.
     */
    public Pane build() {
        HBox root = new HBox();
        root.getStyleClass().add("app-root");

        VBox nav = new SideNav().build(SideNav.NavItem.DASHBOARD);
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

        Label heading = UIFactory.heading("Dashboard");
        Label sub = UIFactory.subheading("Your financial overview at a glance");

        HBox statsRow = buildStatsRow();
        VBox alertsSection = buildAlertsSection();
        VBox cycleCard = buildCycleCard();
        VBox recentSection = buildRecentTransactions();

        content.getChildren().addAll(heading, sub, statsRow, cycleCard, alertsSection, recentSection);
        return content;
    }
    /**
     * Builds statistics row.
     */
    private HBox buildStatsRow() {
        HBox row = new HBox(16);

        float income = budgetService.getTotalIncome(userId);
        float expenses = budgetService.getTotalExpenses(userId);
        float balance = income - expenses;

        row.getChildren().addAll(
            buildStatCard("💵 Balance", String.format("%.2f EGP", balance), balance >= 0 ? "positive" : "negative"),
            buildStatCard("📥 Total Income", String.format("%.2f EGP", income), "neutral"),
            buildStatCard("📤 Total Expenses", String.format("%.2f EGP", expenses), "neutral"),
            buildStatCard("📊 Daily Limit", String.format("%.2f EGP", budgetService.calculateSafeDailyLimit(userId)), "neutral")
        );

        for (javafx.scene.Node n : row.getChildren()) HBox.setHgrow(n, Priority.ALWAYS);
        return row;
    }

    /**
     * Creates a stat card.
     */
    private VBox buildStatCard(String label, String value, String style) {
        VBox card = UIFactory.card(null);
        card.setAlignment(Pos.CENTER_LEFT);
        card.getStyleClass().add("stat-card");

        Label lbl = new Label(label);
        lbl.getStyleClass().add("stat-label");

        Label val = new Label(value);
        val.getStyleClass().addAll("stat-value", "stat-" + style);

        card.getChildren().addAll(lbl, val);
        return card;
    }
    /**
     * Builds current cycle card.
     */
    private VBox buildCycleCard() {
        VBox card = UIFactory.card("📅 Current Budget Cycle");
        BudgetCycle cycle = budgetService.getCurrentCycle(userId);
        if (cycle == null) {
            Label msg = new Label("No active budget cycle. Set one up in Budgets.");
            msg.getStyleClass().add("muted-text");
            card.getChildren().add(msg);
        } else {
            float remaining = budgetService.calculateRemainingBalance(userId);
            float pct = cycle.getTotalAllowance() > 0 ? (remaining / cycle.getTotalAllowance()) * 100 : 0;

            HBox row = new HBox(40);
            row.getChildren().addAll(
                infoItem("Allowance", String.format("%.2f EGP", cycle.getTotalAllowance())),
                infoItem("Remaining", String.format("%.2f EGP", remaining)),
                infoItem("Start", cycle.getStartDate().toString()),
                infoItem("End", cycle.getEndDate().toString())
            );

            ProgressBar pb = new ProgressBar(Math.max(0, Math.min(1.0 - (remaining / cycle.getTotalAllowance()), 1.0)));
            pb.setMaxWidth(Double.MAX_VALUE);
            pb.getStyleClass().add("cycle-progress");
            if (pct < 30) pb.getStyleClass().add("progress-danger");
            else if (pct < 60) pb.getStyleClass().add("progress-warning");

            card.getChildren().addAll(row, pb);
        }
        return card;
    }

    /**
     * Helper method to display info item.
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
    /**
     * Builds alerts section.
     */
    private VBox buildAlertsSection() {
        List<Budget> budgets = budgetService.getBudgets(userId);
        List<AlertingService.BudgetAlert> alerts = alertingService.checkBudgets(budgets);
        if (alerts.isEmpty()) return new VBox();

        VBox section = UIFactory.card("🔔 Budget Alerts");
        for (AlertingService.BudgetAlert alert : alerts) {
            String level = alert.level() == AlertingService.AlertLevel.EXCEEDED ? "exceeded" : "warning";
            String icon = alert.level() == AlertingService.AlertLevel.EXCEEDED ? "🔴" : "🟡";
            String msg = String.format("%s %s: %.1f%% used (%.2f / %.2f EGP)",
                icon, alert.category(), alert.percent(), alert.spent(), alert.limit());
            section.getChildren().add(UIFactory.alertBadge(msg, level));
        }
        return section;
    }
    /**
     * Builds recent transactions list.
     */
    private VBox buildRecentTransactions() {
        VBox section = UIFactory.card("🕐 Recent Transactions");
        com.fakkaflow.data.repository.TransactionRepository repo = new com.fakkaflow.data.repository.TransactionRepository();
        List<com.fakkaflow.data.model.Transaction> transactions = repo.findAll(userId);

        if (transactions.isEmpty()) {
            Label msg = new Label("No transactions yet. Add your first one!");
            msg.getStyleClass().add("muted-text");
            section.getChildren().add(msg);
            return section;
        }

        int limit = Math.min(5, transactions.size());
        for (int i = 0; i < limit; i++) {
            com.fakkaflow.data.model.Transaction t = transactions.get(i);
            HBox row = new HBox();
            row.getStyleClass().add("transaction-row");
            row.setAlignment(Pos.CENTER_LEFT);

            String icon = "expense".equals(t.getType()) ? "📤" : "📥";
            Label iconLbl = new Label(icon);
            iconLbl.setStyle("-fx-font-size: 18px;");

            VBox info = new VBox(2);
            Label catLbl = new Label(t.getCategoryName() != null ? t.getCategoryName() : "Other");
            catLbl.getStyleClass().add("tx-category");
            Label noteLbl = new Label(t.getNote() != null ? t.getNote() : "");
            noteLbl.getStyleClass().add("tx-note");
            info.getChildren().addAll(catLbl, noteLbl);

            Region spacer = new Region();
            HBox.setHgrow(spacer, Priority.ALWAYS);

            Label amountLbl = new Label(String.format("%s %.2f EGP",
                "expense".equals(t.getType()) ? "-" : "+", t.getAmount()));
            amountLbl.getStyleClass().addAll("tx-amount",
                "expense".equals(t.getType()) ? "tx-expense" : "tx-income");

            Label dateLbl = new Label(t.getTimestamp() != null ? t.getTimestamp().substring(0, 10) : "");
            dateLbl.getStyleClass().add("muted-text");
            dateLbl.setPadding(new Insets(0, 0, 0, 12));

            row.getChildren().addAll(iconLbl, new Region() {{ setPrefWidth(12); }}, info, spacer, amountLbl, dateLbl);
            section.getChildren().add(row);
        }
        return section;
    }
}
