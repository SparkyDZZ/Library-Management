package app.controllers;

import app.Models.Ouvrage;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;

import java.net.URL;
import java.sql.*;
import java.util.ResourceBundle;

public class CatalogueController implements Initializable {

    @FXML
    private TableView<Ouvrage> OuvrageTableView;

    @FXML
    private TableColumn<Ouvrage, String> auteurColumn;

    @FXML
    private TableColumn<Ouvrage, String> disponibleColumn;

    @FXML
    private Button rechercheButton;

    @FXML
    private ComboBox rechercheAvecComboBox;
    private final String[] RECHERCHEPAR_ITEM = {"Tout", "Réference", "Titre" ,"Auteur"};
    private final ObservableList<String> RECHERCHEPAR_LIST = FXCollections.observableArrayList(RECHERCHEPAR_ITEM);

    @FXML
    private TextField rechercheEdit;

    @FXML
    private TableColumn<Ouvrage, String> refColumn;

    @FXML
    private TableColumn<Ouvrage, String> titreColumn;
    private Ouvrage exemplaire = null;
    private ObservableList<Ouvrage> ouvrageList = FXCollections.observableArrayList();
    Connection conn = null;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        rechercheAvecComboBox.setItems(RECHERCHEPAR_LIST);
        rechercheAvecComboBox.setValue(RECHERCHEPAR_ITEM[0]);

        refColumn.setCellValueFactory(new PropertyValueFactory<>("Ref"));
        titreColumn.setCellValueFactory(new PropertyValueFactory<>("Titre"));
        auteurColumn.setCellValueFactory(new PropertyValueFactory<>("Auteur"));
        disponibleColumn.setCellValueFactory(new PropertyValueFactory<>("disponible"));

        try {
            conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/library", "root", "");
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM ouvrages");
            while (rs.next()) {
                ouvrageList.add(new Ouvrage(rs.getInt("Ref"), rs.getString("titre"), rs.getString("auteur"),  rs.getString("Rayon"), rs.getInt("Disponibilite")));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        OuvrageTableView.setItems(ouvrageList);


        OuvrageTableView.setOnMouseClicked(event -> {
            if (event.getClickCount() == 1) {
                Ouvrage ouvrage = OuvrageTableView.getSelectionModel().getSelectedItem();

                if (ouvrage != null) {
                    exemplaire = ouvrage;
                    int id = ouvrage.getRef();
                    System.out.println(exemplaire.auteur + " ");
                }
            }
        });


    }

    @FXML
    void onClickRecherche(ActionEvent event) {
        String selectedItem = (String) rechercheAvecComboBox.getSelectionModel().getSelectedItem();
        String searchText = rechercheEdit.getText();
        System.out.println("Recherche effectuée avec : " + selectedItem + " et " + searchText);

        ObservableList<Ouvrage> filteredList = FXCollections.observableArrayList();

        if(selectedItem.equals("Tout")) {
            filteredList.addAll(ouvrageList.filtered(ouvrage ->
                    String.valueOf(ouvrage.getRef()).toLowerCase().contains(searchText.toLowerCase()) ||
                            ouvrage.getTitre().toLowerCase().contains(searchText.toLowerCase()) ||
                            ouvrage.getAuteur().toLowerCase().contains(searchText.toLowerCase())
            ));
        } else if(selectedItem.equals("Réference")) {
            filteredList.addAll(ouvrageList.filtered(ouvrage ->
                    String.valueOf(ouvrage.getRef()).toLowerCase().contains(searchText.toLowerCase())
            ));
        } else if(selectedItem.equals("Titre")) {
            filteredList.addAll(ouvrageList.filtered(ouvrage ->
                    ouvrage.getTitre().toLowerCase().contains(searchText.toLowerCase())
            ));
        } else if(selectedItem.equals("Auteur")) {
            filteredList.addAll(ouvrageList.filtered(ouvrage ->
                    ouvrage.getAuteur().toLowerCase().contains(searchText.toLowerCase())
            ));
        }

        OuvrageTableView.setItems(filteredList);
    }

}
