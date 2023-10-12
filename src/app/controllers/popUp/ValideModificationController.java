package app.controllers.popUp;

import app.Models.Abonnee;
import app.Models.Sanction;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.stage.Stage;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDateTime;

public class ValideModificationController {

    @FXML
    private Label valeurIdentifiant;
    @FXML
    private Label valeurNom;
    @FXML
    private Label valeurPrenom;
    @FXML
    private Label valeurDateNaissance;
    @FXML
    private Label valeurRole;
    @FXML
    private Label valeurStatut;

    @FXML
    private Label valeurTitrePopupValidation;

    @FXML
    private Label valeurQuestionPopup;

    Sanction sanction;
    @FXML
    void onClicksanctionButton(ActionEvent event) {
        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/library", "root", "");
             PreparedStatement stmt = conn.prepareStatement("INSERT INTO `penaliser`(`id_abonnee`, `debutP`, `finP`) VALUES ((SELECT `id_abonnee` FROM `emprunt` WHERE `id`=?),NOW(),?)")) {
            stmt.setString(1, sanction.getId());
            stmt.setString(2, String.valueOf(LocalDateTime.now().plusWeeks(3)));
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

    public void setSanction(Sanction sanction, String titre, String question) {
        this.sanction = sanction;
        valeurIdentifiant.setText(String.valueOf(sanction.getId()));
        valeurNom.setText(String.valueOf(sanction.getId_emprunt()));
        valeurPrenom.setText(String.valueOf(sanction.getDateFin()));
        valeurTitrePopupValidation.setText(titre);
        valeurQuestionPopup.setText(question);

    }
}
