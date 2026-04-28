package com.fakkaflow.ui.view;

import com.fakkaflow.logic.service.AuthService;
import com.fakkaflow.logic.service.SessionManager;
import com.fakkaflow.ui.util.SceneManager;
import javafx.geometry.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.shape.*;

public class SideNav {
    public enum NavItem { DASHBOARD, TRANSACTIONS, BUDGET, GOALS, REPORTS }

    public VBox build(NavItem active) {
        VBox nav = new VBox(4);
        nav.getStyleClass().add("sidenav");
        nav.setPrefWidth(220);
        nav.setPadding(new Insets(24, 12, 24, 12));

        // ── Brand ──────────────────────────────────────────────────────────
        HBox brand = makeRow(dot(ACCENT), "FakkaFlow", "nav-brand");
        brand.setPadding(new Insets(0, 0, 20, 8));
        nav.getChildren().add(brand);

        // ── User ───────────────────────────────────────────────────────────
        String userName = SessionManager.getInstance().getCurrentUser().getName();
        HBox userRow = makeRow(circle(), userName, "nav-user");
        userRow.setPadding(new Insets(0, 0, 16, 8));
        nav.getChildren().add(userRow);

        nav.getChildren().add(new Separator());
        nav.getChildren().add(spacer(8));

        // ── Nav Buttons ────────────────────────────────────────────────────
        addNavBtn(nav, "—", "Dashboard",    NavItem.DASHBOARD,    active);
        addNavBtn(nav, "—", "Transactions", NavItem.TRANSACTIONS, active);
        addNavBtn(nav, "—", "Budgets",      NavItem.BUDGET,       active);
        addNavBtn(nav, "—", "Goals",        NavItem.GOALS,        active);
        addNavBtn(nav, "—", "Reports",      NavItem.REPORTS,      active);

        Region filler = new Region();
        VBox.setVgrow(filler, Priority.ALWAYS);
        nav.getChildren().add(filler);

        nav.getChildren().add(buildLogoutBtn());
        return nav;
    }

    // ── Small colored dot used as icon ─────────────────────────────────────
    private static final String ACCENT  = "#6366f1";
    private static final String MUTED   = "#64748b";

    private Circle dot(String hex) {
        Circle c = new Circle(4);
        c.setFill(javafx.scene.paint.Color.web(hex));
        return c;
    }

    private Circle circle() {
        Circle c = new Circle(5);
        c.setFill(javafx.scene.paint.Color.web("#334155"));
        c.setStroke(javafx.scene.paint.Color.web("#6366f1"));
        c.setStrokeWidth(1.5);
        return c;
    }

    private Rectangle activePip() {
        Rectangle r = new Rectangle(3, 14);
        r.setFill(javafx.scene.paint.Color.web(ACCENT));
        r.setArcWidth(3);
        r.setArcHeight(3);
        return r;
    }

    private Rectangle mutedPip() {
        Rectangle r = new Rectangle(3, 14);
        r.setFill(javafx.scene.paint.Color.web("#2d3748"));
        r.setArcWidth(3);
        r.setArcHeight(3);
        return r;
    }

    // ── Generic row: shape + label ─────────────────────────────────────────
    private HBox makeRow(javafx.scene.Node icon, String text, String styleClass) {
        HBox row = new HBox(10);
        row.setAlignment(Pos.CENTER_LEFT);
        Label lbl = new Label(text);
        lbl.getStyleClass().add(styleClass);
        row.getChildren().addAll(icon, lbl);
        return row;
    }

    // ── Nav button with colored pip as icon ────────────────────────────────
    private void addNavBtn(VBox nav, String ignored, String text, NavItem item, NavItem active) {
        boolean isActive = item == active;

        HBox content = new HBox(10);
        content.setAlignment(Pos.CENTER_LEFT);

        javafx.scene.Node pip = isActive ? activePip() : mutedPip();

        Label textLbl = new Label(text);
        textLbl.getStyleClass().add(isActive ? "nav-btn-text-active" : "nav-btn-text");

        content.getChildren().addAll(pip, textLbl);

        Button btn = new Button();
        btn.setGraphic(content);
        btn.getStyleClass().add("nav-btn");
        if (isActive) btn.getStyleClass().add("nav-btn-active");
        btn.setMaxWidth(Double.MAX_VALUE);
        btn.setAlignment(Pos.CENTER_LEFT);
        btn.setOnAction(e -> navigate(item));
        nav.getChildren().add(btn);
    }

    // ── Logout button ──────────────────────────────────────────────────────
    private Button buildLogoutBtn() {
        Rectangle pip = new Rectangle(3, 14);
        pip.setFill(javafx.scene.paint.Color.web("#7f1d1d"));
        pip.setArcWidth(3);
        pip.setArcHeight(3);

        Label textLbl = new Label("Sign Out");
        textLbl.getStyleClass().add("nav-logout-text");

        HBox content = new HBox(10);
        content.setAlignment(Pos.CENTER_LEFT);
        content.getChildren().addAll(pip, textLbl);

        Button btn = new Button();
        btn.setGraphic(content);
        btn.getStyleClass().add("nav-logout");
        btn.setMaxWidth(Double.MAX_VALUE);
        btn.setAlignment(Pos.CENTER_LEFT);
        btn.setOnAction(e -> {
            new AuthService().logout();
            SceneManager.getInstance().navigateTo(new LoginScreen().build(), "Sign In");
        });
        return btn;
    }

    private void navigate(NavItem item) {
        SceneManager sm = SceneManager.getInstance();
        switch (item) {
            case DASHBOARD    -> sm.navigateTo(new DashboardScreen().build(),   "Dashboard");
            case TRANSACTIONS -> sm.navigateTo(new TransactionScreen().build(), "Transactions");
            case BUDGET       -> sm.navigateTo(new BudgetScreen().build(),      "Budgets");
            case GOALS        -> sm.navigateTo(new GoalScreen().build(),        "Goals");
            case REPORTS      -> sm.navigateTo(new ReportScreen().build(),      "Reports");
        }
    }

    private Region spacer(double h) {
        Region r = new Region();
        r.setPrefHeight(h);
        return r;
    }
}
