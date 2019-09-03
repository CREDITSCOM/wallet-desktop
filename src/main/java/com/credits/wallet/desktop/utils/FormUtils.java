package com.credits.wallet.desktop.utils;

import com.credits.general.util.Constants;
import com.credits.client.node.pojo.TransactionData;
import com.credits.general.util.GeneralConverter;
import com.credits.wallet.desktop.struct.CoinTabRow;
import javafx.application.Platform;
import javafx.scene.control.*;
import javafx.stage.StageStyle;
import javafx.util.Callback;

import java.util.concurrent.atomic.AtomicBoolean;

import static com.credits.general.util.Utils.calculateActualFee;


public class FormUtils {

    public static void showError(String text) {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.initStyle(StageStyle.UTILITY);
            alert.setTitle("Error");
            alert.setHeaderText("Error!");
            alert.setContentText(cutMessage(text));
            alert.showAndWait();
        });
    }

    public static void showInfo(String text) {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.initStyle(StageStyle.UTILITY);
            alert.setTitle("Information");
            alert.setHeaderText("Information");
            alert.setContentText(cutMessage(text));
            alert.showAndWait();
        });
    }

    public static void showPlatformWarning(String content) {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.initStyle(StageStyle.UTILITY);
            alert.setTitle("Warning");
            alert.setHeaderText("Warning");
            alert.setContentText(cutMessage(content));
            alert.showAndWait();
        });
    }

    public static void showPlatformError(String message) {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.initStyle(StageStyle.UTILITY);
            alert.setTitle("Error");
            alert.setHeaderText("Error");
            alert.setContentText(cutMessage(message));
            alert.showAndWait();
        });
    }

    public static void showPlatformInfo(String message) {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.initStyle(StageStyle.UTILITY);
            alert.setTitle("Info");
            alert.setHeaderText("Info");
            alert.setContentText(cutMessage(message));
            alert.showAndWait();
        });
    }

    private static String cutMessage(String message) {
        if (message.trim().length() > 250) {
            message = message.substring(0, 250) + "...";
        }
        return message;
    }

    public static <V, T> void addTooltipToColumnCells(TableColumn<V, T> column) {

        Callback<TableColumn<V, T>, TableCell<V, T>> existingCellFactory = column.getCellFactory();

        column.setCellFactory(c -> {
            TableCell<V, T> cell = existingCellFactory.call(c);
            Tooltip tooltip = new Tooltip();
            tooltip.textProperty().bind(cell.itemProperty().asString());
            cell.setTooltip(tooltip);
            return cell;
        });
    }


    public static void showErrorLabelAndPaintField(
            TextField textField,
            Label errorLabel,
            String errorText,
            AtomicBoolean validationFlag) {
        errorLabel.setText(errorText);
        setErrorStyle(textField);
        validationFlag.set(false);
    }

    public static void setErrorStyle(TextField textField) {
        textField.setStyle(textField.getStyle().replace("-fx-border-color: #ececec", "-fx-border-color: red"));
    }

    public static void validateTable(
        TableView<CoinTabRow> tableView, Label errorLabel, String errorText,
        AtomicBoolean validationFlag) {
        errorLabel.setText(errorText);
        tableView.getStyleClass().add("credits-border-red");
        validationFlag.set(false);
    }

    public static void clearErrorOnTable(TableView<CoinTabRow> tableView, Label errorLabel) {
        tableView.getStyleClass().remove("credits-border-red");
        errorLabel.setText("");
    }

    public static void clearErrorOnField(TextField textField, Label errorLabel) {
        if (errorLabel != null) errorLabel.setText("");
        clearErrorStyle(textField);
    }

    public static void clearErrorStyle(TextField textField) {
        textField.setStyle(textField.getStyle().replace("-fx-border-color: red", "-fx-border-color: #ececec"));
    }

    public static short getActualOfferedMaxFee16Bits(TextField feeField) {
        if (feeField.getText().isEmpty()) throw new IllegalArgumentException("fee text field can't be empty");
        var fee = GeneralConverter.toDouble(feeField.getText(), Constants.LOCALE);
        var actualOfferedMaxFeePair = calculateActualFee(fee);
        return actualOfferedMaxFeePair.getRight();
    }

    public static void initFeeField(TextField feeField, Label actualOfferedMaxFeeLabel) {
        feeField.textProperty().addListener((observable, oldValue, newValue) -> {
            try {
                newValue = NumberUtils.getCorrectNum(newValue);
                refreshOfferedMaxFeeValues(feeField, actualOfferedMaxFeeLabel, newValue);
            } catch (Exception e) {
                //FormUtils.showError("Error. Reason: " + e.getMessage());
                refreshOfferedMaxFeeValues(feeField, actualOfferedMaxFeeLabel, oldValue);
            }
        });
    }

    public static String getTransactionDescType(TransactionData transactionData) {
        switch (transactionData.getType()){
            case TT_SmartDeploy: return "Deploy";
            case TT_SmartState: return "State";
            case TT_SmartExecute: return "Execute";
            case TT_Normal: return "CS Transfer";
            default: return "Unknown";
        }
    }

    private static void refreshOfferedMaxFeeValues(TextField feeField, Label actualOfferedMaxFeeLabel, String value) {
        if (value.isEmpty()) {
            actualOfferedMaxFeeLabel.setText("");
            feeField.setText("");
        } else {
            feeField.setStyle(feeField.getStyle().replace("-fx-prompt-text-fill: red;", ""));
            feeField.setStyle(feeField.getStyle().replace("-fx-border-color: red;", "-fx-border-color: black;"));
            var actualOfferedMaxFeePair = calculateActualFee(GeneralConverter.toDouble(value, Constants.LOCALE));
            actualOfferedMaxFeeLabel.setText(GeneralConverter.toString(actualOfferedMaxFeePair.getLeft()));
            feeField.setText(value);
        }
    }
}

