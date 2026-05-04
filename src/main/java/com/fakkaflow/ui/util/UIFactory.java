package com.fakkaflow.ui.util;

import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Text;
import javafx.scene.paint.Color;
/**
 * Utility class for creating styled UI components.
 * Provides reusable UI elements such as buttons, fields, and labels.
 */
public class UIFactory {
    /**
     * Creates a primary styled button.
     *
     * @param text button text
     * @return styled Button
     */
    public static Button primaryBtn(String text) {
        Button btn = new Button(text);
        btn.getStyleClass().add("btn-primary");
        btn.setMaxWidth(Double.MAX_VALUE);
        return btn;
    }
    /**
     * Creates a secondary styled button.
     *
     * @param text button text
     * @return styled Button
     */

    public static Button secondaryBtn(String text) {
        Button btn = new Button(text);
        btn.getStyleClass().add("btn-secondary");
        return btn;
    }
    /**
     * Creates a danger styled button.
     *
     * @param text button text
     * @return styled Button
     */
    public static Button dangerBtn(String text) {
        Button btn = new Button(text);
        btn.getStyleClass().add("btn-danger");
        return btn;
    }
    /**
     * Creates a styled text field.
     *
     * @param prompt placeholder text
     * @return TextField
     */
    public static TextField styledField(String prompt) {
        TextField tf = new TextField();
        tf.setPromptText(prompt);
        tf.getStyleClass().add("styled-field");
        return tf;
    }
    /**
     * Creates a styled password field.
     *
     * @param prompt placeholder text
     * @return PasswordField
     */
    public static PasswordField styledPassword(String prompt) {
        PasswordField pf = new PasswordField();
        pf.setPromptText(prompt);
        pf.getStyleClass().add("styled-field");
        return pf;
    }

    /**
     * Creates an error label.
     *
     * @return Label styled for errors
     */
    public static Label errorLabel() {
        Label lbl = new Label();
        lbl.getStyleClass().add("error-label");
        lbl.setWrapText(true);
        return lbl;
    }
    /**
     * Creates a heading label.
     *
     * @param text heading text
     * @return Label
     */
    public static Label heading(String text) {
        Label lbl = new Label(text);
        lbl.getStyleClass().add("heading");
        return lbl;
    }
    /**
     * Creates a subheading label.
     *
     * @param text subheading text
     * @return Label
     */

    public static Label subheading(String text) {
        Label lbl = new Label(text);
        lbl.getStyleClass().add("subheading");
        return lbl;
    }
    /**
     * Creates a statistic display component.
     *
     * @param value displayed value
     * @param label description label
     * @return Label representing value
     */
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
    /**
     * Creates a styled card container.
     *
     * @param title card title
     * @return VBox container
     */
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
    /**
     * Creates an alert badge label.
     *
     * @param text badge text
     * @param level alert level (e.g., warning, danger)
     * @return Label
     */
    public static Label alertBadge(String text, String level) {
        Label lbl = new Label(text);
        lbl.getStyleClass().addAll("alert-badge", "alert-" + level);
        lbl.setWrapText(true);
        return lbl;
    }
}
