package com.fakkaflow.ui.view;

import com.fakkaflow.data.model.Transaction;
import com.fakkaflow.data.repository.TransactionRepository;
import com.fakkaflow.logic.service.SessionManager;
import com.fakkaflow.ui.util.UIFactory;
import javafx.collections.FXCollections;
import javafx.geometry.*;
import javafx.scene.chart.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;

import java.time.LocalDate;
import java.util.*;
/**
 * Screen responsible for generating financial reports and insights.
 * Displays charts and summaries such as:
 * - Income vs expenses
 * - Category spending
 * - Monthly trends
 */
public class ReportScreen {
    private final TransactionRepository transactionRepository = new TransactionRepository();
    private final int userId = SessionManager.getInstance().getCurrentUserId();
    private VBox chartsArea;
    /**
     * Builds main layout.
     *
     * @return root Pane
     */
    public Pane build() {
        HBox root = new HBox();
        root.getStyleClass().add("app-root");
        VBox nav = new SideNav().build(SideNav.NavItem.REPORTS);
        VBox content = buildContent();
        HBox.setHgrow(content, Priority.ALWAYS);
        root.getChildren().addAll(nav, content);
        return root;
    }

    /**
     * Builds main content.
     */
    private VBox buildContent() {
        VBox content = new VBox(20);
        content.getStyleClass().add("content-area");
        content.setPadding(new Insets(32));

        Label heading = UIFactory.heading("Reports & Insights");
        Label sub = UIFactory.subheading("Visualize your financial patterns");

        HBox filterRow = buildFilterRow(content);
        chartsArea = new VBox(24);

        generateReport(null, null);

        ScrollPane scroll = new ScrollPane(chartsArea);
        scroll.setFitToWidth(true);
        scroll.setStyle("-fx-background: transparent; -fx-background-color: transparent;");
        VBox.setVgrow(scroll, Priority.ALWAYS);

        content.getChildren().addAll(heading, sub, filterRow, scroll);
        return content;
    }
    /**
     * Builds filter row for selecting report period.
     */
    private HBox buildFilterRow(VBox content) {
        HBox row = new HBox(12);
        row.setAlignment(Pos.CENTER_LEFT);
        row.getStyleClass().add("filter-row");

        ComboBox<String> rangeCombo = new ComboBox<>();
        rangeCombo.getItems().addAll("This Month", "Last 3 Months", "This Year", "All Time");
        rangeCombo.setValue("This Month");
        rangeCombo.getStyleClass().add("styled-combo");

        Button generateBtn = UIFactory.primaryBtn("Generate Report");
        generateBtn.setMaxWidth(180);
        generateBtn.setOnAction(e -> {
            LocalDate[] range = getDateRange(rangeCombo.getValue());
            generateReport(range[0], range[1]);
        });

        row.getChildren().addAll(new Label("Period:"), rangeCombo, generateBtn);
        return row;
    }
    /**
     * Calculates date range based on selected period.
     */
    private LocalDate[] getDateRange(String period) {
        LocalDate end = LocalDate.now();
        LocalDate start = switch (period) {
            case "This Month" -> end.withDayOfMonth(1);
            case "Last 3 Months" -> end.minusMonths(3);
            case "This Year" -> end.withDayOfYear(1);
            default -> LocalDate.of(2000, 1, 1);
        };
        return new LocalDate[]{start, end};
    }
    /**
     * Generates report based on filters.
     */

    private void generateReport(LocalDate start, LocalDate end) {
        chartsArea.getChildren().clear();

        String startStr = start != null ? start.toString() : null;
        String endStr = end != null ? end.toString() : null;

        List<Transaction> transactions = (startStr == null)
            ? transactionRepository.findAll(userId)
            : transactionRepository.findByFilter(userId, null, null, startStr, endStr);

        if (transactions.isEmpty()) {
            Label msg = new Label("📭 No transaction data available for this period.");
            msg.getStyleClass().add("muted-text");
            msg.setStyle("-fx-font-size: 15px; -fx-padding: 40px;");
            chartsArea.getChildren().add(msg);
            return;
        }

        float totalIncome = 0, totalExpenses = 0;
        Map<String, Float> categorySpend = new LinkedHashMap<>();
        Map<String, Float> monthlyIncome = new LinkedHashMap<>();
        Map<String, Float> monthlyExpense = new LinkedHashMap<>();

        for (Transaction t : transactions) {
            if ("income".equals(t.getType())) {
                totalIncome += t.getAmount();
                String month = t.getTimestamp() != null ? t.getTimestamp().substring(0, 7) : "Unknown";
                monthlyIncome.merge(month, t.getAmount(), Float::sum);
            } else {
                totalExpenses += t.getAmount();
                String cat = t.getCategoryName() != null ? t.getCategoryName() : "Other";
                categorySpend.merge(cat, t.getAmount(), Float::sum);
                String month = t.getTimestamp() != null ? t.getTimestamp().substring(0, 7) : "Unknown";
                monthlyExpense.merge(month, t.getAmount(), Float::sum);
            }
        }

        chartsArea.getChildren().add(buildSummaryCards(totalIncome, totalExpenses));
        if (!categorySpend.isEmpty()) chartsArea.getChildren().add(buildPieChart(categorySpend));
        chartsArea.getChildren().add(buildBarChart(monthlyIncome, monthlyExpense));
        chartsArea.getChildren().add(buildInsightSection(categorySpend, totalExpenses));
    }
    /**
     * Builds summary cards.
     */
    private HBox buildSummaryCards(float income, float expenses) {
        HBox row = new HBox(16);
        float net = income - expenses;

        row.getChildren().addAll(
            summaryCard("📥 Income", String.format("%.2f EGP", income), "stat-positive"),
            summaryCard("📤 Expenses", String.format("%.2f EGP", expenses), "stat-negative"),
            summaryCard("💰 Net", String.format("%.2f EGP", net), net >= 0 ? "stat-positive" : "stat-negative"),
            summaryCard("📝 Transactions", String.valueOf(0), "stat-neutral")
        );
        for (javafx.scene.Node n : row.getChildren()) HBox.setHgrow(n, Priority.ALWAYS);
        return row;
    }
    /**
     * Builds a single summary card.
     */
    private VBox summaryCard(String label, String value, String style) {
        VBox card = UIFactory.card(null);
        card.getStyleClass().add("stat-card");
        Label lbl = new Label(label);
        lbl.getStyleClass().add("stat-label");
        Label val = new Label(value);
        val.getStyleClass().addAll("stat-value", style);
        card.getChildren().addAll(lbl, val);
        return card;
    }
    /**
     * Builds pie chart for category spending.
     */

    private VBox buildPieChart(Map<String, Float> categorySpend) {
        VBox card = UIFactory.card("🥧 Spending by Category");

        PieChart pie = new PieChart();
        pie.setLegendSide(Side.RIGHT);
        pie.setPrefHeight(340);
        pie.setLabelsVisible(true);
        pie.setStyle("-fx-background-color: transparent;");

        categorySpend.forEach((cat, amt) ->
            pie.getData().add(new PieChart.Data(String.format("%s (%.0f)", cat, amt), amt))
        );

        card.getChildren().add(pie);
        return card;
    }
    /**
     * Builds bar chart for income vs expenses.
     */
    private VBox buildBarChart(Map<String, Float> income, Map<String, Float> expense) {
        VBox card = UIFactory.card("📊 Income vs Expenses");

        CategoryAxis xAxis = new CategoryAxis();
        NumberAxis yAxis = new NumberAxis();
        yAxis.setLabel("EGP");

        BarChart<String, Number> chart = new BarChart<>(xAxis, yAxis);
        chart.setPrefHeight(320);
        chart.setStyle("-fx-background-color: transparent;");
        chart.setAnimated(false);

        XYChart.Series<String, Number> incomeSeries = new XYChart.Series<>();
        incomeSeries.setName("Income");
        XYChart.Series<String, Number> expenseSeries = new XYChart.Series<>();
        expenseSeries.setName("Expenses");

        Set<String> months = new TreeSet<>();
        months.addAll(income.keySet());
        months.addAll(expense.keySet());

        for (String month : months) {
            incomeSeries.getData().add(new XYChart.Data<>(month, income.getOrDefault(month, 0f)));
            expenseSeries.getData().add(new XYChart.Data<>(month, expense.getOrDefault(month, 0f)));
        }

        chart.getData().addAll(incomeSeries, expenseSeries);
        card.getChildren().add(chart);
        return card;
    }
    /**
     * Builds insights section.
     */
    private VBox buildInsightSection(Map<String, Float> categorySpend, float totalExpenses) {
        VBox card = UIFactory.card("💡 Insights");
        if (categorySpend.isEmpty()) return card;

        String topCat = categorySpend.entrySet().stream()
            .max(Map.Entry.comparingByValue())
            .map(Map.Entry::getKey).orElse("N/A");
        float topAmt = categorySpend.getOrDefault(topCat, 0f);
        double topPct = totalExpenses > 0 ? (topAmt / totalExpenses) * 100 : 0;

        Label insight1 = UIFactory.alertBadge(
            String.format("🔍 Top spending category: %s (%.1f%% of expenses = %.2f EGP)", topCat, topPct, topAmt),
            topPct > 40 ? "warning" : "info"
        );

        float avgPerCat = totalExpenses / Math.max(1, categorySpend.size());
        Label insight2 = UIFactory.alertBadge(
            String.format("📊 Average per category: %.2f EGP", avgPerCat), "info"
        );

        card.getChildren().addAll(insight1, insight2);
        return card;
    }
}
