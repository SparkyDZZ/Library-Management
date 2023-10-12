package app.controllers.popUp;

import app.Models.Abonnee;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDate;

public class ModificationController {

    @FXML
    private TextField identifiantEdit;
    @FXML
    private TextField nomEdit;
    @FXML
    private TextField prenomEdit;
    @FXML
    private DatePicker dateNaissanceDatePicker;
    @FXML
    private ComboBox statutComboBox;
    private final String[] STATUT_ITEM = {"Interne","Externe"};
    private final ObservableList<String> STATUT_LIST = FXCollections.observableArrayList(STATUT_ITEM);
    @FXML
    private ComboBox roleComboBox;
    private final String[] ROLE_ITEM = {"Etudiant", "Enseignant"};
    private final ObservableList<String> ROLE_LIST = FXCollections.observableArrayList(ROLE_ITEM);

    public void onClickAjouteExemplaire(ActionEvent actionEvent) {
        String type = (String) roleComboBox.getSelectionModel().getSelectedItem();
        String statut = (String) statutComboBox.getSelectionModel().getSelectedItem();
        LocalDate dateNaissance = dateNaissanceDatePicker.getValue();

        // Connect to database and insert data
        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/library", "root", "");
             PreparedStatement stmt = conn.prepareStatement("UPDATE `utilisateur` SET `id`=?,`nom`=?,`prenom`=?,`DateN`=?,`MotDePasse`=?,`Role`=?,`Statut`=?,`Type`=? WHERE id = ?")) {
            stmt.setString(1, identifiantEdit.getText());
            stmt.setString(2, nomEdit.getText());
            stmt.setString(3, prenomEdit.getText());
            stmt.setString(4, String.valueOf(dateNaissance));
            stmt.setString(5, String.valueOf(dateNaissance.getDayOfMonth())+"/"+String.valueOf(dateNaissance.getYear()));
            stmt.setString(6, "Abonnee");
            stmt.setString(7, statut);
            stmt.setString(8, type);
            stmt.setString(9, identifiantEdit.getText());
            stmt.executeUpdate();

            Node source = (Node)  actionEvent.getSource();
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

    public void setAbonnee(Abonnee abonne) {

        statutComboBox.setItems(STATUT_LIST);
        roleComboBox.setItems(ROLE_LIST);
        identifiantEdit.setText(abonne.getIdentifiant());
        nomEdit.setText(abonne.getNom());
        prenomEdit.setText(abonne.getPrenom());
        dateNaissanceDatePicker.setValue(abonne.getDateNaissance());
        roleComboBox.setValue(abonne.getType());
        statutComboBox.setValue(abonne.getStatut());


    }
}
