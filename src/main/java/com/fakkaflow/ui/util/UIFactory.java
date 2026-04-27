package com.fakkaflow.ui.util;

import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Text;
import javafx.scene.paint.Color;

public class UIFactory {

    public static Button primaryBtn(String text) {
        Button btn = new Button(text);
        btn.getStyleClass().add("btn-primary");
        btn.setMaxWidth(Double.MAX_VALUE);
        return btn;
    }

    public static Button secondaryBtn(String text) {
        Button btn = new Button(text);
        btn.getStyleClass().add("btn-secondary");
        return btn;
    }

    public static Button dangerBtn(String text) {
        Button btn = new Button(text);
        btn.getStyleClass().add("btn-danger");
        return btn;
    }

    public static TextField styledField(String prompt) {
        TextField tf = new TextField();
        tf.setPromptText(prompt);
        tf.getStyleClass().add("styled-field");
        return tf;
    }

    public static PasswordField styledPassword(String prompt) {
        PasswordField pf = new PasswordField();
        pf.setPromptText(prompt);
        pf.getStyleClass().add("styled-field");
        return pf;
    }

    public static Label errorLabel() {
        Label lbl = new Label();
        lbl.getStyleClass().add("error-label");
        lbl.setWrapText(true);
        return lbl;
    }

    public static Label heading(String text) {
        Label lbl = new Label(text);
        lbl.getStyleClass().add("heading");
        return lbl;
    }

    public static Label subheading(String text) {
        Label lbl = new Label(text);
        lbl.getStyleClass().add("subheading");
        return lbl;
    }

    public static Label cardStat(String value, String label) {
        VBox box = new VBox(4);
        box.setAlignment(Pos.CENTER);
        Label valLbl = new Label(value);
        valLbl.getStyleClass().add("stat-value");
        Label nameLbl = new Label(label);
        nameLbl.getStyleClass().add("stat-label");
        box.getChildren().addAll(valLbl, nameLbl);
        return valLbl;
    }

    public static VBox card(String title) {
        VBox card = new VBox(12);
        card.getStyleClass().add("card");
        if (title != null && !title.isEmpty()) {
            Label lbl = new Label(title);
            lbl.getStyleClass().add("card-title");
            card.getChildren().add(lbl);
        }
        return card;
    }

    public static Label alertBadge(String text, String level) {
        Label lbl = new Label(text);
        lbl.getStyleClass().addAll("alert-badge", "alert-" + level);
        lbl.setWrapText(true);
        return lbl;
    }
}
