package app.controllers.popUp;

import app.Models.Ouvrage;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.stage.Stage;

import java.sql.*;

public class AjouteExemplaireController {

    @FXML
    public Label valeurTitre;
    @FXML
    public Label valeurAuteur;
    @FXML
    public Label valeurDisponible;
    @FXML
    public Label valeurRayon;

    @FXML
    private ComboBox nbrExemplaireComboBox;
    private final String[] RECHERCHEPAR_ITEM = {"1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20"};
    private final ObservableList<String> RECHERCHEPAR_LIST = FXCollections.observableArrayList(RECHERCHEPAR_ITEM);
    private Ouvrage ouvrage;

    public void setOuvrage(Ouvrage ouvrage) {
        this.ouvrage = ouvrage;
        valeurTitre.setText(ouvrage.titre);
        valeurAuteur.setText(ouvrage.auteur);
        valeurRayon.setText(ouvrage.rayon);
        valeurDisponible.setText(String.valueOf(ouvrage.disponible));

        nbrExemplaireComboBox.setItems(RECHERCHEPAR_LIST);
        nbrExemplaireComboBox.setValue(RECHERCHEPAR_ITEM[0]);
    }
    public void AjouteExemplaire(ActionEvent actionEvent) {
        try {
            // establish a connection to the database

            Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/library", "root", "");

            // get the current value of "Disponibilite" from the database
            String sqlSelect = "SELECT Disponibilite FROM ouvrages WHERE Ref = ?";
            PreparedStatement statementSelect = connection.prepareStatement(sqlSelect);
            statementSelect.setInt(1, ouvrage.ref);
            ResultSet rs = statementSelect.executeQuery();
            rs.next();
            int currentDisponibilite = rs.getInt("Disponibilite");

            // calculate the new value of "Disponibilite" by adding the selected value to the current value
            int newDisponibilite = currentDisponibilite + Integer.parseInt(nbrExemplaireComboBox.getValue().toString());

            // create a SQL statement that updates the data
            String sqlUpdate = "UPDATE ouvrages SET Disponibilite = ? WHERE Ref = ?";
            PreparedStatement statementUpdate = connection.prepareStatement(sqlUpdate);
            statementUpdate.setInt(1, newDisponibilite);
            statementUpdate.setInt(2, ouvrage.ref);

            // execute the statement
            statementUpdate.executeUpdate();

            // update the value of the "disponible" field in the ouvrage object
            ouvrage.disponible = newDisponibilite;
            valeurDisponible.setText(String.valueOf(ouvrage.disponible));

            // close the connection
            connection.close();
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

}
