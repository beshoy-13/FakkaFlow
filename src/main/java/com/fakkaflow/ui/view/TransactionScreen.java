package com.fakkaflow.ui.view;

import com.fakkaflow.data.model.*;
import com.fakkaflow.data.repository.*;
import com.fakkaflow.logic.service.*;
import com.fakkaflow.ui.util.UIFactory;
import javafx.collections.*;
import javafx.geometry.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;

import java.util.List;
/**
 * Screen for managing transactions.
 * Provides features to:
 * - Add new transactions
 * - Filter transactions
 * - View transaction table
 * - Delete transactions
 */
public class TransactionScreen {
    private final TransactionRepository transactionRepository = new TransactionRepository();
    private final CategoryRepository categoryRepository = new CategoryRepository();
    private final ValidationService validationService = new ValidationService();
    private final int userId = SessionManager.getInstance().getCurrentUserId();
    private final ObservableList<Transaction> txList = FXCollections.observableArrayList();
    private TableView<Transaction> table;
    private ComboBox<String> typeFilter;
    private ComboBox<Category> catFilter;
    /**
     * Builds main layout.
     *
     * @return root Pane
     */
    public Pane build() {
        HBox root = new HBox();
        root.getStyleClass().add("app-root");
        VBox nav = new SideNav().build(SideNav.NavItem.TRANSACTIONS);
        VBox content = buildContent();
        HBox.setHgrow(content, Priority.ALWAYS);
        root.getChildren().addAll(nav, content);
        return root;
    }

    /**
     * Builds main content area.
     */
    private VBox buildContent() {
        VBox content = new VBox(20);
        content.getStyleClass().add("content-area");
        content.setPadding(new Insets(32));
        VBox.setVgrow(content, Priority.ALWAYS);

        Label heading = UIFactory.heading("Transactions");

        HBox topRow = new HBox(12);
        topRow.setAlignment(Pos.CENTER_LEFT);
        Button addBtn = UIFactory.primaryBtn("+ Add Transaction");
        addBtn.setMaxWidth(180);
        addBtn.setOnAction(e -> showAddDialog());
        topRow.getChildren().add(addBtn);

        HBox filterRow = buildFilterRow();
        table = buildTable();
        VBox.setVgrow(table, Priority.ALWAYS);

        loadTransactions(null, null, null, null);
        content.getChildren().addAll(heading, topRow, filterRow, table);
        return content;
    }
    /**
     * Builds filter row UI.
     */
    private HBox buildFilterRow() {
        HBox row = new HBox(12);
        row.setAlignment(Pos.CENTER_LEFT);
        row.getStyleClass().add("filter-row");

        typeFilter = new ComboBox<>();
        typeFilter.getItems().addAll("All", "income", "expense");
        typeFilter.setValue("All");
        typeFilter.getStyleClass().add("styled-combo");

        catFilter = new ComboBox<>();
        catFilter.getItems().add(new Category(0, "All Categories"));
        catFilter.getItems().addAll(categoryRepository.findAll());
        catFilter.setValue(catFilter.getItems().get(0));
        catFilter.getStyleClass().add("styled-combo");

        Button applyBtn = UIFactory.secondaryBtn("Apply Filter");
        applyBtn.setOnAction(e -> applyFilters());

        Button clearBtn = new Button("Clear");
        clearBtn.getStyleClass().add("btn-ghost");
        clearBtn.setOnAction(e -> {
            typeFilter.setValue("All");
            catFilter.setValue(catFilter.getItems().get(0));
            loadTransactions(null, null, null, null);
        });

        Label totalLbl = new Label();
        totalLbl.getStyleClass().add("muted-text");
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        row.getChildren().addAll(new Label("Type:"), typeFilter, new Label("Category:"), catFilter, applyBtn, clearBtn, spacer);
        return row;
    }
    /**
     * Builds transactions table.
     */
    private TableView<Transaction> buildTable() {
        TableView<Transaction> tv = new TableView<>(txList);
        tv.getStyleClass().add("styled-table");
        tv.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        TableColumn<Transaction, String> dateCol = new TableColumn<>("Date");
        dateCol.setCellValueFactory(d -> new javafx.beans.property.SimpleStringProperty(
            d.getValue().getTimestamp() != null ? d.getValue().getTimestamp().substring(0, 10) : ""
        ));
        dateCol.setPrefWidth(100);

        TableColumn<Transaction, String> typeCol = new TableColumn<>("Type");
        typeCol.setCellValueFactory(d -> new javafx.beans.property.SimpleStringProperty(
            d.getValue().getType()
        ));
        typeCol.setCellFactory(col -> new TableCell<>() {
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) { setText(null); setStyle(""); return; }
                setText("expense".equals(item) ? "📤 Expense" : "📥 Income");
                setStyle("expense".equals(item) ? "-fx-text-fill: #ef4444;" : "-fx-text-fill: #22c55e;");
            }
        });
        typeCol.setPrefWidth(100);

        TableColumn<Transaction, String> catCol = new TableColumn<>("Category");
        catCol.setCellValueFactory(d -> new javafx.beans.property.SimpleStringProperty(d.getValue().getCategoryName()));
        catCol.setPrefWidth(120);

        TableColumn<Transaction, String> noteCol = new TableColumn<>("Note");
        noteCol.setCellValueFactory(d -> new javafx.beans.property.SimpleStringProperty(d.getValue().getNote()));
        noteCol.setPrefWidth(200);

        TableColumn<Transaction, String> amtCol = new TableColumn<>("Amount (EGP)");
        amtCol.setCellValueFactory(d -> new javafx.beans.property.SimpleStringProperty(
            String.format("%.2f", d.getValue().getAmount())
        ));
        amtCol.setCellFactory(col -> new TableCell<>() {
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) { setText(null); return; }
                Transaction t = getTableRow().getItem();
                if (t != null) {
                    setText(("expense".equals(t.getType()) ? "- " : "+ ") + item);
                    setStyle("expense".equals(t.getType()) ? "-fx-text-fill: #ef4444; -fx-font-weight: bold;" : "-fx-text-fill: #22c55e; -fx-font-weight: bold;");
                }
            }
        });
        amtCol.setPrefWidth(130);

        TableColumn<Transaction, Void> actionCol = new TableColumn<>("Actions");
        actionCol.setCellFactory(col -> new TableCell<>() {
            final Button del = UIFactory.dangerBtn("Delete");
            { del.setOnAction(e -> {
                Transaction t = getTableRow().getItem();
                if (t != null) {
                    transactionRepository.delete(t.getTransactionId());
                    loadTransactions(null, null, null, null);
                }
            }); }
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : del);
            }
        });
        actionCol.setPrefWidth(90);

        tv.getColumns().addAll(dateCol, typeCol, catCol, noteCol, amtCol, actionCol);
        tv.setPlaceholder(new Label("No transactions found."));
        return tv;
    }
    /**
     * Applies selected filters.
     */
    private void applyFilters() {
        String type = "All".equals(typeFilter.getValue()) ? null : typeFilter.getValue();
        Category cat = catFilter.getValue();
        Integer catId = (cat != null && cat.getCategoryId() > 0) ? cat.getCategoryId() : null;
        loadTransactions(type, catId, null, null);
    }
    /**
     * Loads transactions based on filters.
     */
    private void loadTransactions(String type, Integer catId, String start, String end) {
        List<Transaction> list = (type == null && catId == null && start == null && end == null)
            ? transactionRepository.findAll(userId)
            : transactionRepository.findByFilter(userId, type, catId, start, end);
        txList.setAll(list);
    }
    /**
     * Shows dialog to add new transaction.
     */
    private void showAddDialog() {
        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("Add Transaction");
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        VBox form = new VBox(14);
        form.setPadding(new Insets(20));
        form.setPrefWidth(380);

        ComboBox<String> typeCombo = new ComboBox<>();
        typeCombo.getItems().addAll("expense", "income");
        typeCombo.setValue("expense");
        typeCombo.getStyleClass().add("styled-combo");
        typeCombo.setMaxWidth(Double.MAX_VALUE);

        TextField amountField = UIFactory.styledField("Amount (e.g. 150.00)");

        ComboBox<Category> catCombo = new ComboBox<>();
        catCombo.getItems().addAll(categoryRepository.findAll());
        if (!catCombo.getItems().isEmpty()) catCombo.setValue(catCombo.getItems().get(0));
        catCombo.getStyleClass().add("styled-combo");
        catCombo.setMaxWidth(Double.MAX_VALUE);

        TextField noteField = UIFactory.styledField("Note (optional)");
        Label errLbl = UIFactory.errorLabel();

        form.getChildren().addAll(
            new Label("Type:"), typeCombo,
            new Label("Amount:"), amountField,
            new Label("Category:"), catCombo,
            new Label("Note:"), noteField,
            errLbl
        );

        dialog.getDialogPane().setContent(form);

        Button okBtn = (Button) dialog.getDialogPane().lookupButton(ButtonType.OK);
        okBtn.addEventFilter(javafx.event.ActionEvent.ACTION, e -> {
            errLbl.setText("");
            if (!validationService.validateAmountString(amountField.getText())) {
                errLbl.setText("Please enter a valid amount greater than 0.");
                e.consume();
                return;
            }
            if (catCombo.getValue() == null) {
                errLbl.setText("Please select a category.");
                e.consume();
                return;
            }
            float amount = Float.parseFloat(amountField.getText().trim());
            Transaction t = new Transaction(userId, amount, typeCombo.getValue(),
                catCombo.getValue().getCategoryId(), noteField.getText().trim());
            transactionRepository.save(t);
            loadTransactions(null, null, null, null);
        });

        dialog.showAndWait();
    }
}
