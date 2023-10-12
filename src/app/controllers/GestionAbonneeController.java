package app.controllers;

import app.Models.Abonnee;
import app.controllers.popUp.ModificationController;
import app.controllers.popUp.ValideModificationController;
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
import java.time.LocalDate;
import java.util.ResourceBundle;

public class GestionAbonneeController implements Initializable {
    @FXML
    public TextField identifiantEdit;
    @FXML
    public DatePicker dateNaissanceEdit;
    @FXML
    public TextField nomEdit;
    @FXML
    public TextField prenomEdit;
    @FXML
    private TableView<Abonnee> abonneeTableView;

    @FXML
    private TableColumn<Abonnee, String> identifiantColumn;
    @FXML
    private TableColumn<Abonnee, String> nomColumn;
    @FXML
    private TableColumn<Abonnee, String> prenomColumn;
    @FXML
    private TableColumn<Abonnee, String> dateNaissanceColumn;
    @FXML
    private TableColumn<Abonnee, String> statutColumn;
    @FXML
    private TableColumn<Abonnee, String> roleColumn;
    @FXML
    private TableColumn<Abonnee, String> typeColumn;

    private ObservableList<Abonnee> abonneeList = FXCollections.observableArrayList();

    @FXML
    private ComboBox rechercheComboBox;
    private final String[] RECHERCHEPAR_ITEM = {"Tout", "Identifiant", "Nom", "Prenom","Date de naissance","Statue", "Role"};
    private final ObservableList<String> RECHERCHEPAR_LIST = FXCollections.observableArrayList(RECHERCHEPAR_ITEM);
    @FXML
    private ComboBox statutComboBox;
    private final String[] STATUT_ITEM = {"Interne","Externe"};
    private final ObservableList<String> STATUT_LIST = FXCollections.observableArrayList(STATUT_ITEM);
    @FXML
    private ComboBox roleComboBox;
    private final String[] ROLE_ITEM = {"Etudiant", "Enseignant"};
    private final ObservableList<String> ROLE_LIST = FXCollections.observableArrayList(ROLE_ITEM);

    @FXML
    private Button rechercheButton;

    @FXML
    private TextField rechercheEdit;



    private Abonnee abonnee1;

    private String id;

    Connection conn = null;
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {


        statutComboBox.setItems(STATUT_LIST);
        roleComboBox.setItems(ROLE_LIST);
        rechercheComboBox.setItems(RECHERCHEPAR_LIST);
        rechercheComboBox.setValue(RECHERCHEPAR_ITEM[0]);

        identifiantColumn.setCellValueFactory(new PropertyValueFactory<>("identifiant"));
        nomColumn.setCellValueFactory(new PropertyValueFactory<>("nom"));
        prenomColumn.setCellValueFactory(new PropertyValueFactory<>("prenom"));
        dateNaissanceColumn.setCellValueFactory(new PropertyValueFactory<>("dateNaissance"));
        roleColumn.setCellValueFactory(new PropertyValueFactory<>("role"));
        statutColumn.setCellValueFactory(new PropertyValueFactory<>("statut"));
        typeColumn.setCellValueFactory(new PropertyValueFactory<>("type"));

        try {
            conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/library", "root", "");
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM utilisateur WHERE Role = 'Abonnee'");
            while (rs.next()) {
                abonneeList.add(new Abonnee(rs.getString("id"), rs.getString("nom"), rs.getString("prenom"), rs.getDate("DateN").toLocalDate(), rs.getString("Role"), rs.getString("Statut"),rs.getString("Type")));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        roleComboBox.setItems(ROLE_LIST);
        roleComboBox.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null && newVal.equals("Enseignant")) {
                statutComboBox.setValue(null);
                statutComboBox.setDisable(true);
            } else {
                statutComboBox.setDisable(false);
            }
        });
        statutComboBox.setItems(STATUT_LIST);

        abonneeTableView.setItems(abonneeList);

        abonneeTableView.setOnMouseClicked(event -> {
            if (event.getClickCount() == 1) {
                Abonnee abonnee = abonneeTableView.getSelectionModel().getSelectedItem();

                if (abonnee != null) {
                    abonnee1 = abonnee;
                    id = abonnee.getIdentifiant();
                    System.out.println(abonnee.getNom() + " ");
                }
            }
        });

    }

    public void onClickModifierAbonnee(ActionEvent actionEvent) {
        try {

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/app/views/PopUp/popUp_modifierAbonne.fxml"));
            Parent root = loader.load();

            ModificationController controller = loader.getController();
            controller.setAbonnee(abonnee1);

            createWindow(root);


        } catch (IOException e) {
            e.printStackTrace();
        }
    }



    public void onClickSupprimerAbonnee(ActionEvent actionEvent) {

        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/library", "root", "");
             PreparedStatement stmt = conn.prepareStatement("DELETE FROM utilisateur WHERE id = ?")) {
            stmt.setString(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation");
        alert.setHeaderText(null);
        alert.setContentText("Supprimez l'utilisateur.");
        alert.showAndWait();
    }

    private void update(){
        abonneeList.clear();
        try {
            conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/library", "root", "");
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM utilisateur WHERE Role = 'Abonnee'");
            while (rs.next()) {
                abonneeList.add(new Abonnee(rs.getString("id"), rs.getString("nom"), rs.getString("prenom"), rs.getDate("DateN").toLocalDate(), rs.getString("Role"), rs.getString("Statut"),rs.getString("Type")));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void onClickAjouteAbonnee(ActionEvent actionEvent) {

        String type = (String) roleComboBox.getSelectionModel().getSelectedItem();
        String statut = (String) statutComboBox.getSelectionModel().getSelectedItem();
        LocalDate dateNaissance = dateNaissanceEdit.getValue();

        // Check if ID already exists
        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/library", "root", "");
             PreparedStatement stmt = conn.prepareStatement("SELECT COUNT(*) FROM utilisateur WHERE id = ?")) {
            stmt.setString(1, identifiantEdit.getText());
            ResultSet rs = stmt.executeQuery();
            if (rs.next() && rs.getInt(1) > 0) {
                // ID already exists, show error message and return
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Erreur");
                alert.setHeaderText(null);
                alert.setContentText("Cet identifiant existe déjà dans la base de données.");
                alert.showAndWait();
                return;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
          if(statut == "Externe") {
            // Check if the maximum number of externs has been reached
            try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/library", "root", "");
                 PreparedStatement stmt = conn.prepareStatement("SELECT COUNT(*) FROM utilisateur WHERE Role = 'Abonnee' AND Statut = 'Interne'");
                 PreparedStatement stmt2 = conn.prepareStatement("SELECT COUNT(*) FROM utilisateur WHERE Role = 'Abonnee' AND Statut = 'Externe'")) {
                ResultSet rs = stmt.executeQuery();
                rs.next();
                int nbInterns = rs.getInt(1);

                ResultSet rs2 = stmt2.executeQuery();
                rs2.next();
                int nbExterns = rs2.getInt(1);
                if (nbExterns >= (int)(nbInterns * 0.1)) {
                    System.out.println(nbExterns+" "+nbInterns);
                    // Maximum number of externs has been reached, show error message and return
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Erreur");
                    alert.setHeaderText(null);
                    alert.setContentText("Le nombre maximal d'externes a été atteint.");
                    alert.showAndWait();
                    return;
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        // Connect to database and insert data
        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/library", "root", "");
             PreparedStatement stmt = conn.prepareStatement("INSERT INTO utilisateur (id, nom, prenom, DateN, MotDePasse, Role, Statut, Type) VALUES (?, ?, ?, ?, ?, ?, ?, ?)")) {
            stmt.setString(1, identifiantEdit.getText());
            stmt.setString(2, nomEdit.getText());
            stmt.setString(3, prenomEdit.getText());
            stmt.setString(4, String.valueOf(dateNaissance));
            stmt.setString(5, String.valueOf(dateNaissance.getDayOfMonth())+"/"+String.valueOf(dateNaissance.getYear()));
            stmt.setString(6, "Abonnee");
            stmt.setString(7, statut);
            stmt.setString(8, type);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        // Clear the input fields and create a

        update();
        // Clear the input fields and create a new Abonnee object
        Abonnee abonneAAjouter = new Abonnee(identifiantEdit.getText(), nomEdit.getText(), prenomEdit.getText(), dateNaissance, "Abonnee", statut, type);
        identifiantEdit.clear();
        nomEdit.clear();
        prenomEdit.clear();
        dateNaissanceEdit.setValue(null);
        roleComboBox.setValue(null);
        statutComboBox.setValue(null);
    }





    public void onClickRecherche(ActionEvent actionEvent) {
        String selectedItem = (String) rechercheComboBox.getSelectionModel().getSelectedItem();
        String searchText = rechercheEdit.getText();

        ObservableList<Abonnee> filteredList = FXCollections.observableArrayList();

        if(selectedItem.equals("Tout")) {
            filteredList.addAll(abonneeList.filtered(abonnee ->
                    abonnee.getIdentifiant().toLowerCase().contains(searchText.toLowerCase()) ||
                            abonnee.getNom().toLowerCase().contains(searchText.toLowerCase()) ||
                            abonnee.getPrenom().toLowerCase().contains(searchText.toLowerCase()) ||
                            abonnee.getDateNaissance().toString().toLowerCase().contains(searchText.toLowerCase()) ||
                            abonnee.getStatut().toString().toLowerCase().contains(searchText.toLowerCase()) ||
                            abonnee.getRole().toLowerCase().contains(searchText.toLowerCase())||
                            abonnee.getType().toString().toLowerCase().contains(searchText.toLowerCase())


            ));
        } else if(selectedItem.equals("Identifiant")) {
            filteredList.addAll(abonneeList.filtered(abonnee ->
                    abonnee.getIdentifiant().toLowerCase().contains(searchText.toLowerCase())
            ));
        } else if(selectedItem.equals("Nom")) {
            filteredList.addAll(abonneeList.filtered(abonnee ->
                    abonnee.getNom().toLowerCase().contains(searchText.toLowerCase())
            ));
        } else if(selectedItem.equals("Prenom")) {
            filteredList.addAll(abonneeList.filtered(abonnee ->
                    abonnee.getPrenom().toLowerCase().contains(searchText.toLowerCase())
            ));
        } else if(selectedItem.equals("Date de naissance")) {
            filteredList.addAll(abonneeList.filtered(abonnee ->
                    abonnee.getDateNaissance().toString().toLowerCase().contains(searchText.toLowerCase())
            ));
        } else if(selectedItem.equals("Role")) {
            filteredList.addAll(abonneeList.filtered(abonnee ->
                    abonnee.getRole().toLowerCase().contains(searchText.toLowerCase())
            ));
        } else if(selectedItem.equals("Statut")) {
            filteredList.addAll(abonneeList.filtered(abonnee ->
                    abonnee.getStatut().toLowerCase().contains(searchText.toLowerCase())
            ));
        }else if(selectedItem.equals("Type")) {
            filteredList.addAll(abonneeList.filtered(abonnee ->
                    abonnee.getType().toLowerCase().contains(searchText.toLowerCase())
            ));
        }

        abonneeTableView.setItems(filteredList);
    }

    public void createWindow(Parent root){

        Stage popupStage = new Stage();
        Scene scene = new Scene(root);
        popupStage.setScene(scene);
        popupStage.initModality(Modality.APPLICATION_MODAL);
        popupStage.initStyle(StageStyle.UNDECORATED);

        popupStage.showAndWait();
    }



}
