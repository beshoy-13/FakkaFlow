package com.fakkaflow.ui.view;

import com.fakkaflow.logic.service.AuthService;
import com.fakkaflow.logic.service.SessionManager;
import com.fakkaflow.ui.util.SceneManager;
import javafx.geometry.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;

public class SideNav {
    public enum NavItem { DASHBOARD, TRANSACTIONS, BUDGET, GOALS, REPORTS }

    public VBox build(NavItem active) {
        VBox nav = new VBox(4);
        nav.getStyleClass().add("sidenav");
        nav.setPrefWidth(220);
        nav.setPadding(new Insets(24, 12, 24, 12));

        Label brand = new Label("💰 FakkaFlow");
        brand.getStyleClass().add("nav-brand");
        brand.setPadding(new Insets(0, 0, 20, 8));

        nav.getChildren().add(brand);

        String user = SessionManager.getInstance().getCurrentUser().getName();
        Label userLbl = new Label("👤 " + user);
        userLbl.getStyleClass().add("nav-user");
        userLbl.setPadding(new Insets(0, 0, 16, 8));
        nav.getChildren().add(userLbl);

        nav.getChildren().add(new Separator());
        nav.getChildren().add(spacer(8));

        addNavBtn(nav, "🏠  Dashboard", NavItem.DASHBOARD, active);
        addNavBtn(nav, "💳  Transactions", NavItem.TRANSACTIONS, active);
        addNavBtn(nav, "📊  Budgets", NavItem.BUDGET, active);
        addNavBtn(nav, "🎯  Goals", NavItem.GOALS, active);
        addNavBtn(nav, "📈  Reports", NavItem.REPORTS, active);

        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);
        nav.getChildren().add(spacer);

        Button logoutBtn = new Button("🚪  Sign Out");
        logoutBtn.getStyleClass().add("nav-logout");
        logoutBtn.setMaxWidth(Double.MAX_VALUE);
        logoutBtn.setOnAction(e -> {
            new AuthService().logout();
            SceneManager.getInstance().navigateTo(new LoginScreen().build(), "Sign In");
        });
        nav.getChildren().add(logoutBtn);

        return nav;
    }

    private void addNavBtn(VBox nav, String label, NavItem item, NavItem active) {
        Button btn = new Button(label);
        btn.getStyleClass().add("nav-btn");
        if (item == active) btn.getStyleClass().add("nav-btn-active");
        btn.setMaxWidth(Double.MAX_VALUE);
        btn.setAlignment(Pos.CENTER_LEFT);
        btn.setOnAction(e -> navigate(item));
        nav.getChildren().add(btn);
    }

    private void navigate(NavItem item) {
        SceneManager sm = SceneManager.getInstance();
        switch (item) {
            case DASHBOARD -> sm.navigateTo(new DashboardScreen().build(), "Dashboard");
            case TRANSACTIONS -> sm.navigateTo(new TransactionScreen().build(), "Transactions");
            case BUDGET -> sm.navigateTo(new BudgetScreen().build(), "Budgets");
            case GOALS -> sm.navigateTo(new GoalScreen().build(), "Goals");
            case REPORTS -> sm.navigateTo(new ReportScreen().build(), "Reports");
        }
    }

    private Region spacer(double h) {
        Region r = new Region();
        r.setPrefHeight(h);
        return r;
    }
}
