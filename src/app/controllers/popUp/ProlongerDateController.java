package app.controllers.popUp;

import app.Models.Sanction;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.stage.Stage;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class ProlongerDateController {
    @FXML
    public DatePicker newDateLimiteDatePicker;
    @FXML
    public Label valeurIdentifiant;
    @FXML
    public Label DateLimit;
    @FXML
    public Label Nomvaleur;

    Connection conn = null;
    Sanction sanction;
    @FXML
    void onClickprolongerButton(ActionEvent event) {
        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/library", "root", "");
             PreparedStatement stmt = conn.prepareStatement("UPDATE `emprunt`" +
                     "SET `date_limit`= ?" +
                     "WHERE `id`= ?")) {
            stmt.setString(1, String.valueOf(newDateLimiteDatePicker.getValue()));
            stmt.setString(2, sanction.getId());
            stmt.executeUpdate();
            stmt.close();
            conn.close();
            Node source = (Node)  event.getSource();
            Stage stage  = (Stage) source.getScene().getWindow();
            stage.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void onClickAnnuler(ActionEvent actionEvent) {
        Node source = (Node)  actionEvent.getSource();
        Stage stage  = (Stage) source.getScene().getWindow();
        stage.close();
    }

    public void setSanction(Sanction sanction) {
        this.sanction = sanction;
        valeurIdentifiant.setText(String.valueOf(sanction.getId()));
        Nomvaleur.setText(String.valueOf(sanction.getId_emprunt()));
        DateLimit.setText(String.valueOf(sanction.getDateFin()));

    }
}
