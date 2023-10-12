package app.controllers;

import app.Models.Abonnee;
import app.Models.Ouvrage;
import app.controllers.popUp.AjouteExemplaireController;
import app.controllers.popUp.ValidationOuvrageController;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;
import java.net.URL;
import java.sql.*;
import java.util.ResourceBundle;



public class GestionAcquisitionController implements Initializable {



    public TextField rayonEdit;
    public TextField titreEdit;
    public TextField refEdit;
    public TextField auteurEdit;
    public Button ajouteButton;
    public Button editButton;
    public Button deleteButton;
    @FXML
    private ComboBox rechercheAvecComboBox;
    private final String[] RECHERCHEPAR_ITEM = {"Tout", "Réference", "Titre" ,"Auteur","Rayon" };
    private final ObservableList<String> RECHERCHEPAR_LIST = FXCollections.observableArrayList(RECHERCHEPAR_ITEM);

    @FXML
    private TextField rechercheEdit;

    @FXML
    private Button rechercheButton;
    @FXML
    private TableView<Ouvrage> acquisitionTableView;

    @FXML
    private TableColumn<Ouvrage, String> referenceColumn;
    @FXML
    private TableColumn<Ouvrage, String> titreColumn;
    @FXML
    private TableColumn<Ouvrage, String> auteurColumn;
    @FXML
    private TableColumn<Ouvrage, String> RayonColumn;
    @FXML
    private TableColumn<Ouvrage, String> DisponibleColumn;
    private ObservableList<Ouvrage> ouvrageList = FXCollections.observableArrayList();
    private Ouvrage exemplaire = null;
    private int id;
    Connection conn = null;
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        rechercheAvecComboBox.setItems(RECHERCHEPAR_LIST);
        rechercheAvecComboBox.setValue(RECHERCHEPAR_ITEM[0]);

        referenceColumn.setCellValueFactory(new PropertyValueFactory<>("Ref"));
        titreColumn.setCellValueFactory(new PropertyValueFactory<>("Titre"));
        auteurColumn.setCellValueFactory(new PropertyValueFactory<>("Auteur"));
        RayonColumn.setCellValueFactory(new PropertyValueFactory<>("Rayon"));
        DisponibleColumn.setCellValueFactory(new PropertyValueFactory<>("disponible"));

        try {
            conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/library", "root", "");
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM ouvrages");
            while (rs.next()) {
                ouvrageList.add(new Ouvrage(rs.getInt("Ref"), rs.getString("titre"), rs.getString("auteur"),  rs.getString("rayon"), rs.getInt("Disponibilite")));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        acquisitionTableView.setItems(ouvrageList);


        acquisitionTableView.setOnMouseClicked(event -> {
            if (event.getClickCount() == 1) {
                Ouvrage ouvrage = acquisitionTableView.getSelectionModel().getSelectedItem();

                if (ouvrage != null) {
                    exemplaire = ouvrage;
                    id = ouvrage.getRef();
                    System.out.println(exemplaire.auteur + " ");
                }
            }
        });


    }


    public void onClickRecherche(ActionEvent actionEvent) {
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
        }else if(selectedItem.equals("Rayon")) {
            filteredList.addAll(ouvrageList.filtered(ouvrage ->
                    ouvrage.getRayon().toLowerCase().contains(searchText.toLowerCase())
            ));
        }

        acquisitionTableView.setItems(filteredList);

    }

    private void update(){
        ouvrageList.clear();
        try {
            conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/library", "root", "");
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM ouvrages");
            while (rs.next()) {
                ouvrageList.add(new Ouvrage(rs.getInt("Ref"), rs.getString("titre"), rs.getString("auteur"),  rs.getString("rayon"), rs.getInt("Disponibilite")));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    public void onClickAjouteOuvrage(ActionEvent actionEvent) {

        if (verifieEditNonVide()) {
            Ouvrage ouvrage = new Ouvrage(Integer.parseInt(refEdit.getText()), titreEdit.getText(), auteurEdit.getText(), rayonEdit.getText(),1);

            try {
                // create a database connection
                Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/library", "root", "");

                // create a PreparedStatement with a SELECT statement to check if the reference already exists
                String sql = "SELECT COUNT(*) FROM ouvrages WHERE ref = ?";
                PreparedStatement stmt = conn.prepareStatement(sql);
                stmt.setInt(1, ouvrage.getRef());
                ResultSet rs = stmt.executeQuery();

                if (rs.next() && rs.getInt(1) > 0) {
                    // the reference already exists, show an error message and return
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Erreur");
                    alert.setHeaderText(null);
                    alert.setContentText("La référence existe déjà dans la base de données.");
                    alert.showAndWait();
                    return;
                }
                

                // close the ResultSet and PreparedStatement
                rs.close();
                stmt.close();

                // create a PreparedStatement with an INSERT statement
                sql = "INSERT INTO ouvrages (ref, titre, auteur, rayon, disponibilite) VALUES (?, ?, ?, ?, ?)";
                stmt = conn.prepareStatement(sql);
                stmt.setInt(1, ouvrage.getRef());
                stmt.setString(2, ouvrage.getTitre());
                stmt.setString(3, ouvrage.getAuteur());
                stmt.setString(4, ouvrage.getRayon());
                stmt.setInt(5, 1);

                // execute the insert statement
                int rowsInserted = stmt.executeUpdate();
                if (rowsInserted > 0) {
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Succès");
                    alert.setHeaderText(null);
                    alert.setContentText("Le nouvel ouvrage a été ajouté avec succès!");
                    alert.showAndWait();
                }

                // close the database connection and statement
                stmt.close();
                conn.close();


            } catch (SQLException e) {
                e.printStackTrace();
            }
            update();
        }
    }



    public void onClickAjouteExemplaire(ActionEvent actionEvent) {
        if(exemplaire != null){
            try {

                FXMLLoader loader = new FXMLLoader(getClass().getResource("/app/views/popUp/popUp_ajouteExemplaire.fxml"));
                Parent root = loader.load();

                AjouteExemplaireController controller = loader.getController();
                controller.setOuvrage(exemplaire);

                Stage popupStage = new Stage();
                Scene scene = new Scene(root);
                popupStage.setScene(scene);
                popupStage.initModality(Modality.APPLICATION_MODAL);
                popupStage.initStyle(StageStyle.UNDECORATED);

                popupStage.showAndWait();


            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        update();
    }

    @FXML
    void onClickModifierExemplaire(ActionEvent event) {

    }

    @FXML
    void onClickSupprimerExemplaire(ActionEvent event) {
        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/library", "root", "");
             PreparedStatement stmt = conn.prepareStatement("DELETE FROM ouvrages WHERE ref = ?")) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        // Show a success message
        Alert successAlert = new Alert(Alert.AlertType.INFORMATION);
        successAlert.setTitle("Succès");
        successAlert.setHeaderText(null);
        successAlert.setContentText("L'ouvrage a été supprimé avec succès.");
        successAlert.showAndWait();

    }


    public Boolean verifieEditNonVide(){

        if (titreEdit.getText().isEmpty()){
            return false;
        }else if(auteurEdit.getText().isEmpty()){
            return false;
        }else if(refEdit.getText().isEmpty()){
            return false;
        }else if(rayonEdit.getText().isEmpty()){
            return false;
        }
        return true;
    }
}