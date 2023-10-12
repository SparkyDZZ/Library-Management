package app.controllers;

import app.Models.Abonnee;
import app.Models.Ouvrage;
import app.controllers.popUp.ValidationEmpruntController;
import com.itextpdf.text.pdf.BarcodePDF417;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.text.Font;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;
import java.net.URL;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ResourceBundle;

public class GestionEmpruntController implements Initializable {


    @FXML
    public Label identifiantValeur;
    @FXML
    public Label roleValeur;
    @FXML
    public Label statutValeur;
    @FXML
    public Label nomValeur;
    @FXML
    public Label prenomValeur;
    @FXML
    public Label typeValeur;
    @FXML
    private TableView<Abonnee> empruntTableView;

    @FXML
    private TableColumn<Abonnee, String> identifiantColumn;
    @FXML
    private TableColumn<Abonnee, String> nomColumn;
    @FXML
    private TableColumn<Abonnee, String> prenomColumn;
    @FXML
    private TextField rechercheEdit;
    @FXML
    private TextField rechercheOuvrageEdit;
    @FXML
    private TableColumn<Abonnee, String> statutColumn;
    @FXML
    private TableColumn<Abonnee, String> roleColumn;
    @FXML
    private TableColumn<Abonnee, String> typeColumn;
    private ObservableList<Abonnee> abonneeList = FXCollections.observableArrayList();
    @FXML
    private HBox hbox;

    @FXML
    private TableView<Ouvrage> empruntOuvrageTableView;

    @FXML
    private TableColumn<Ouvrage, String> refColumn;
    @FXML
    private TableColumn<Ouvrage, String> titreColumn;
    @FXML
    private TableColumn<Ouvrage, String> auteurColumn;
    @FXML
    private TableColumn<Ouvrage, String> rayonColumn;
    @FXML
    private TableColumn<Ouvrage, String> disponibleColumn;

    @FXML
    private Label valeurNbLivre;


    private ObservableList<Ouvrage> ouvrageList = FXCollections.observableArrayList();


    @FXML
    private ComboBox rechercheComboBox;
    private final String[] RECHERCHEPAR_ITEM = {"Tout", "Identifiant", "Nom", "Prenom", "Statut", "Role"};
    private final ObservableList<String> RECHERCHEPAR_LIST = FXCollections.observableArrayList(RECHERCHEPAR_ITEM);
    @FXML
    private ComboBox rechercheOuvrageComboBox;
    private final String[] RECHERCHEOUVRAGE_ITEM = {"Tout", "Identifiant", "Titre", "Auteur", "Catégorie", "Exemplaire"};
    private final ObservableList<String> RECHERCHEOUVRAGE_LIST = FXCollections.observableArrayList(RECHERCHEOUVRAGE_ITEM);

    List<Ouvrage> listOuvrage = new ArrayList<>(3);
    Ouvrage ouvrage1;
    Abonnee abonnee1;
    Pane plusPane;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        rechercheComboBox.setItems(RECHERCHEPAR_LIST);
        rechercheComboBox.setValue(RECHERCHEPAR_ITEM[0]);
        rechercheOuvrageComboBox.setItems(RECHERCHEOUVRAGE_LIST);
        rechercheOuvrageComboBox.setValue(RECHERCHEOUVRAGE_ITEM[0]);

        identifiantColumn.setCellValueFactory(new PropertyValueFactory<>("identifiant"));
        nomColumn.setCellValueFactory(new PropertyValueFactory<>("nom"));
        prenomColumn.setCellValueFactory(new PropertyValueFactory<>("prenom"));
        roleColumn.setCellValueFactory(new PropertyValueFactory<>("role"));
        statutColumn.setCellValueFactory(new PropertyValueFactory<>("statut"));
        typeColumn.setCellValueFactory(new PropertyValueFactory<>("type"));

        // Retrieve abonnée data from database
        try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/library", "root", "")) {
            String query = "SELECT utilisateur.id, utilisateur.nom, utilisateur.prenom, utilisateur.Role, utilisateur.Statut, utilisateur.Type " +
                    "FROM utilisateur " +
                    "LEFT JOIN emprunt ON utilisateur.id = emprunt.id_abonnee " +
                    "WHERE utilisateur.Role = 'Abonnee' " +
                    "GROUP BY utilisateur.id " +
                    "HAVING COUNT(emprunt.id) < 3";
            PreparedStatement statement = connection.prepareStatement(query);
            ResultSet rs = statement.executeQuery();
            while (rs.next()) {
                Abonnee abonnee = new Abonnee(rs.getString("id"), rs.getString("nom"), rs.getString("prenom"), null, rs.getString("Role"), rs.getString("Statut"), rs.getString("Type"));
                abonneeList.add(abonnee);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        empruntTableView.setItems(abonneeList);

        refColumn.setCellValueFactory(new PropertyValueFactory<>("ref"));
        titreColumn.setCellValueFactory(new PropertyValueFactory<>("titre"));
        auteurColumn.setCellValueFactory(new PropertyValueFactory<>("auteur"));
        rayonColumn.setCellValueFactory(new PropertyValueFactory<>("rayon"));
        disponibleColumn.setCellValueFactory(new PropertyValueFactory<>("disponible"));
        // Retrieve abonnée data from database
        try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/library", "root", "")) {
            String query = "SELECT * FROM ouvrages where Disponibilite != 0";
            PreparedStatement statement = connection.prepareStatement(query);
            ResultSet rs = statement.executeQuery();
            while (rs.next()) {
                ouvrageList.add(new Ouvrage(rs.getInt("Ref"), rs.getString("titre"), rs.getString("auteur"),rs.getString("rayon"),rs.getInt("Disponibilite")));
                empruntOuvrageTableView.setItems(ouvrageList);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        empruntTableView.setOnMouseClicked(event -> {
            if (event.getClickCount() == 1) {
                Abonnee abonnee = empruntTableView.getSelectionModel().getSelectedItem();

                if (abonnee != null) {

                    identifiantValeur.setText(abonnee.getIdentifiant());
                    nomValeur.setText(abonnee.getNom());
                    prenomValeur.setText(abonnee.getPrenom());
                    roleValeur.setText(abonnee.getRole());
                    statutValeur.setText(abonnee.getStatut());
                    typeValeur.setText(abonnee.getType());
                    abonnee1 = abonnee;
                }
            }
        });
        empruntOuvrageTableView.setOnMouseClicked(event -> {
            if (event.getClickCount() == 1) {
                Ouvrage ouvrage = empruntOuvrageTableView.getSelectionModel().getSelectedItem();

                if (ouvrage != null) {

                    ouvrage1 = ouvrage;
                }
            }
        });



        plusPane = createPlusPane();
        HBox.setMargin(plusPane, new Insets(0, 5, 0, 5));
        hbox.getChildren().add(plusPane);



    }



    public void onClickRecherche(ActionEvent actionEvent) {

        String selectedItem = (String) rechercheComboBox.getSelectionModel().getSelectedItem();
        String searchText = rechercheEdit.getText();

        ObservableList<Abonnee> filteredList = FXCollections.observableArrayList();

        if (selectedItem.equals("Tout")) {
            filteredList.addAll(abonneeList.filtered(abonnee ->
                    abonnee.getIdentifiant().toLowerCase().contains(searchText.toLowerCase()) ||
                            abonnee.getNom().toLowerCase().contains(searchText.toLowerCase()) ||
                            abonnee.getPrenom().toLowerCase().contains(searchText.toLowerCase()) ||
                            abonnee.getDateNaissance().toString().toLowerCase().contains(searchText.toLowerCase()) ||
                            abonnee.getStatut().toString().toLowerCase().contains(searchText.toLowerCase()) ||
                            abonnee.getRole().toLowerCase().contains(searchText.toLowerCase()) ||
                            abonnee.getStatut().toLowerCase().contains(searchText.toLowerCase()) ||
                            abonnee.getType().toLowerCase().contains(searchText.toLowerCase())


            ));
        } else if (selectedItem.equals("Identifiant")) {
            filteredList.addAll(abonneeList.filtered(abonnee ->
                    abonnee.getIdentifiant().toLowerCase().contains(searchText.toLowerCase())
            ));
        } else if (selectedItem.equals("Nom")) {
            filteredList.addAll(abonneeList.filtered(abonnee ->
                    abonnee.getNom().toLowerCase().contains(searchText.toLowerCase())
            ));
        } else if (selectedItem.equals("Prenom")) {
            filteredList.addAll(abonneeList.filtered(abonnee ->
                    abonnee.getPrenom().toLowerCase().contains(searchText.toLowerCase())
            ));
        } else if (selectedItem.equals("Date de naissance")) {
            filteredList.addAll(abonneeList.filtered(abonnee ->
                    abonnee.getDateNaissance().toString().toLowerCase().contains(searchText.toLowerCase())
            ));
        } else if (selectedItem.equals("Role")) {
            filteredList.addAll(abonneeList.filtered(abonnee ->
                    abonnee.getRole().toLowerCase().contains(searchText.toLowerCase())
            ));
        } else if (selectedItem.equals("Statut")) {
            filteredList.addAll(abonneeList.filtered(abonnee ->
                    abonnee.getStatut().toLowerCase().contains(searchText.toLowerCase())
            ));
        }else if (selectedItem.equals("Type")) {
            filteredList.addAll(abonneeList.filtered(abonnee ->
                    abonnee.getType().toLowerCase().contains(searchText.toLowerCase())
            ));
        }

        empruntTableView.setItems(filteredList);
    }

    public void onClickRechercheOuvrage(ActionEvent actionEvent) {

        String selectedItem = (String) rechercheOuvrageComboBox.getSelectionModel().getSelectedItem();
        String searchText = rechercheOuvrageEdit.getText();

        ObservableList<Ouvrage> filteredList = FXCollections.observableArrayList();

        if (selectedItem.equals("Tout")) {
            filteredList.addAll(ouvrageList.filtered(ouvrage ->
                    String.valueOf(ouvrage.getRef()).toLowerCase().contains(searchText.toLowerCase()) ||
                            ouvrage.getTitre().toLowerCase().contains(searchText.toLowerCase()) ||
                            ouvrage.getAuteur().toLowerCase().contains(searchText.toLowerCase()) ||
                            ouvrage.getRayon().toLowerCase().contains(searchText.toLowerCase())

            ));
        } else if (selectedItem.equals("Identifiant")) {
            filteredList.addAll(ouvrageList.filtered(ouvrage ->
                    String.valueOf(ouvrage.getRef()).toLowerCase().contains(searchText.toLowerCase())
            ));
        } else if (selectedItem.equals("Titre")) {
            filteredList.addAll(ouvrageList.filtered(ouvrage ->
                    ouvrage.getTitre().toLowerCase().contains(searchText.toLowerCase())
            ));
        } else if (selectedItem.equals("Auteur")) {
            filteredList.addAll(ouvrageList.filtered(ouvrage ->
                    ouvrage.getAuteur().toLowerCase().contains(searchText.toLowerCase())
            ));
        }else if (selectedItem.equals("Rayon")) {
            filteredList.addAll(ouvrageList.filtered(ouvrage ->
                    ouvrage.getRayon().toLowerCase().contains(searchText.toLowerCase())
            ));
        }
        empruntOuvrageTableView.setItems(filteredList);
    }


    private void addNewPane(Ouvrage ouvrage) {
        int nombrePane = hbox.getChildren().size();

        int nb = listOuvrage.indexOf(ouvrage) ;
        if (nb == -1){
            if (nombrePane >= 3){
                hbox.getChildren().remove(plusPane);
            }
            Pane newPane = createNewPane(ouvrage);
            HBox.setMargin(newPane, new Insets(0, 5, 0, 5));
            hbox.getChildren().add(hbox.getChildren().size()-1,newPane);
            listOuvrage.add(ouvrage);
        }else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erreur");
            alert.setHeaderText(null);
            alert.setContentText("Vous avez deja ajouter le livre");
            alert.showAndWait();
        }

    }

    private Pane createPlusPane(){

        Pane livrePane = new Pane();
        livrePane.setPrefSize(207.0, 175.0);
        livrePane.setStyle("-fx-background-color: white;");

        Button duplicateButton = new Button();
        duplicateButton.setLayoutX(63.0);
        duplicateButton.setLayoutY(51.0);
        duplicateButton.setPrefSize(34.0, 34.0);
        duplicateButton.setStyle("-fx-background-color: white;");
        duplicateButton.setOnAction(e -> {
            if (ouvrage1 != null){
                addNewPane(ouvrage1);
            }
        });

        ImageView imageView = new ImageView();
        imageView.setFitHeight(67.0);
        imageView.setFitWidth(65.0);
        imageView.setPickOnBounds(true);
        imageView.setPreserveRatio(true);
        Image image = new Image("app/images/icon_plus.png");
        imageView.setImage(image);

        duplicateButton.setGraphic(imageView);
        livrePane.getChildren().add(duplicateButton);



        return livrePane;
    }

    private Pane createNewPane(Ouvrage ouvrage) {
        Pane pane = new Pane();
        pane.setPrefSize(210.0, 170.0);
        pane.setStyle("-fx-background-color: white;");

        Label referenceLabel = new Label("Reference");
        referenceLabel.setLayoutX(26.0);
        referenceLabel.setLayoutY(38.0);
        referenceLabel.setFont(new Font(14.0));
        referenceLabel.setId("referenceLabelId"); // ajout de l'ID pour le label de référence
        pane.getChildren().add(referenceLabel);
        referenceLabel.setText(String.valueOf(ouvrage.getRef()));

        Label titleLabel = new Label("Titre");
        titleLabel.setLayoutX(26.0);
        titleLabel.setLayoutY(65.0);
        titleLabel.setFont(new Font(14.0));
        pane.getChildren().add(titleLabel);
        titleLabel.setText(ouvrage.getTitre());

        Label auteurLabel = new Label("Auteur");
        auteurLabel.setLayoutX(26.0);
        auteurLabel.setLayoutY(92.0);
        auteurLabel.setFont(new Font(14.0));
        pane.getChildren().add(auteurLabel);
        auteurLabel.setText(ouvrage.auteur);

        Label rayonLabel = new Label("Rayon");
        rayonLabel.setLayoutX(26.0);
        rayonLabel.setLayoutY(119.0);
        rayonLabel.setFont(new Font(14.0));
        pane.getChildren().add(rayonLabel);
        rayonLabel.setText(ouvrage.rayon);

        Button closeButton = new Button();
        closeButton.setLayoutX(170.0);
        closeButton.setLayoutY(2.0);
        closeButton.setPrefSize(34.0, 34.0);
        closeButton.setStyle("-fx-background-color: white;");
        ImageView closeIcon = new ImageView(new Image(getClass().getResourceAsStream("../images/icon_closeElement.png")));
        closeIcon.setFitHeight(20.0);
        closeIcon.setFitWidth(20.0);
        closeButton.setGraphic(closeIcon);
        closeButton.setOnAction(e -> {



            Node parent = closeButton.getParent();
            Label labelInParent = (Label) parent.lookup("#referenceLabelId");
            String referenceValue = labelInParent.getText();

            Iterator<Ouvrage> iter = listOuvrage.iterator();
            while (iter.hasNext()) {
                Ouvrage ouv = iter.next();
                if (String.valueOf(ouv.getRef()).equals(referenceValue)) {
                    iter.remove();
                    break;
                }
            }

            if (plusPane == null || !hbox.getChildren().contains(plusPane)) {
                plusPane = createPlusPane();
                hbox.setMargin(plusPane, new Insets(0, 5, 0, 5));
                hbox.getChildren().add(plusPane);
            }
            hbox.getChildren().remove(pane);

        });
        pane.getChildren().add(closeButton);


        return pane;
    }

    private void update(){
        ouvrageList.clear();
        abonneeList.clear();
        try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/library", "root", "")) {
            String query = "SELECT utilisateur.id, utilisateur.nom, utilisateur.prenom, utilisateur.Role, utilisateur.Statut, utilisateur.Type " +
                    "FROM utilisateur " +
                    "LEFT JOIN emprunt ON utilisateur.id = emprunt.id_abonnee " +
                    "WHERE utilisateur.Role = 'Abonnee' " +
                    "GROUP BY utilisateur.id " +
                    "HAVING COUNT(emprunt.id) < 3";
            PreparedStatement statement = connection.prepareStatement(query);
            ResultSet rs = statement.executeQuery();
            while (rs.next()) {
                Abonnee abonnee = new Abonnee(rs.getString("id"), rs.getString("nom"), rs.getString("prenom"), null, rs.getString("Role"), rs.getString("Statut"), rs.getString("Type"));
                abonneeList.add(abonnee);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/library", "root", "")) {
            String query = "SELECT * FROM ouvrages where Disponibilite != 0";
            PreparedStatement statement = connection.prepareStatement(query);
            ResultSet rs = statement.executeQuery();
            while (rs.next()) {
                ouvrageList.add(new Ouvrage(rs.getInt("Ref"), rs.getString("titre"), rs.getString("auteur"),rs.getString("rayon"),rs.getInt("Disponibilite")));
                empruntOuvrageTableView.setItems(ouvrageList);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    public void onClickAjouteEmprunt(ActionEvent actionEvent) {


        if(abonnee1 == null){
            System.err.println("vous avez pas sectionner l'abonne");
        }else if(listOuvrage.size() == 0){
            System.err.println("vous avez pas selectionner un livre");
        }else {
            try {

                FXMLLoader loader = new FXMLLoader(getClass().getResource("/app/views/PopUp/popUp_validationEmprunt.fxml"));
                Parent root = loader.load();

                ValidationEmpruntController controller = loader.getController();
                controller.setAbonnee(abonnee1,listOuvrage);
                createWindow(root);
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
        update();


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

