package app.controllers;

import app.Models.Abonnee;
import app.Models.Sanction;
import app.controllers.popUp.ValideModificationController;
import app.controllers.popUp.ProlongerDateController;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
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
import java.time.LocalDateTime;
import java.util.ResourceBundle;

public class GestionSanctionController implements Initializable {

    @FXML
    private TableView<Sanction> sanctionTableView;

    @FXML
    private TableColumn<Sanction, String> identifiantColumn;
    @FXML
    private TableColumn<Sanction, String> nomColumn;
    @FXML
    private TableColumn<Sanction, String> livreColumn;
    @FXML
    private TableColumn<Sanction, String> dpColumn;
    @FXML
    private TableColumn<Sanction, String> fpColumn;

    private ObservableList<Sanction> sanctionList = FXCollections.observableArrayList();

    @FXML
    private ComboBox rechercheComboBox;
    private final String[] RECHERCHEPAR_ITEM = {"Tout", "Identifiant", "Nom", "Prenom","Statue", "Role"};
    private final ObservableList<String> RECHERCHEPAR_LIST = FXCollections.observableArrayList(RECHERCHEPAR_ITEM);

    @FXML
    private Button rechercheButton;

    @FXML
    private Button enleveButton;

    @FXML
    private Button sanctionnnerButton;
    @FXML
    private Button prolongerButton;
    @FXML
    private RadioButton Limit;

    @FXML
    private RadioButton Nsanction;

    @FXML
    private RadioButton sanction_R;

    @FXML
    private ToggleGroup table;

    @FXML
    private TextField rechercheEdit;

    private Sanction sanction1;
    Connection conn = null;
    String id;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        rechercheComboBox.setItems(RECHERCHEPAR_LIST);
        rechercheComboBox.setValue(RECHERCHEPAR_ITEM[0]);

        identifiantColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        nomColumn.setCellValueFactory(new PropertyValueFactory<>("id_emprunt"));
        livreColumn.setCellValueFactory(new PropertyValueFactory<>("id_livre"));
        dpColumn.setCellValueFactory(new PropertyValueFactory<>("dateDebut"));
        fpColumn.setCellValueFactory(new PropertyValueFactory<>("dateFin"));
        livreColumn.setVisible(false);
        sanctionnnerButton.setVisible(false);
        prolongerButton.setVisible(false);
        enleveButton.setVisible(true);
        try {
            conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/library", "root", "");
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT u.id, CONCAT(u.nom, ' ', u.prenom) AS abonnee, p.debutP, p.finP FROM penaliser p INNER JOIN utilisateur u ON p.id_abonnee = u.id");
            while (rs.next()) {
                sanctionList.add(new Sanction(rs.getString("id"), rs.getString("abonnee"), "", rs.getDate("debutP").toLocalDate(), rs.getDate("finP").toLocalDate()));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }


        sanctionTableView.setItems(sanctionList);

        sanctionTableView.setOnMouseClicked(event -> {
            if (event.getClickCount() == 1) {
                Sanction sanction = sanctionTableView.getSelectionModel().getSelectedItem();

                if (sanction != null) {
                    sanction1 = sanction;
                    id = sanction.getId();
                    System.out.println(sanction.getId() + " ");
                }
            }
        });

    }

    public void onClickRecherche(ActionEvent actionEvent) {
        String selectedItem = (String) rechercheComboBox.getSelectionModel().getSelectedItem();
        String searchText = rechercheEdit.getText();

        ObservableList<Sanction> filteredList = FXCollections.observableArrayList();

        if(selectedItem.equals("Tout")) {
            filteredList.addAll(sanctionList.filtered(sanction ->
            String.valueOf(sanction.getId()).toLowerCase().contains(searchText.toLowerCase()) ||
            String.valueOf(sanction.getId_emprunt()).toLowerCase().contains(searchText.toLowerCase()) ||
            String.valueOf(sanction.getDateDebut()).toLowerCase().contains(searchText.toLowerCase()) ||
            String.valueOf(sanction.getDateFin()).toLowerCase().contains(searchText.toLowerCase())


            ));
        } else if(selectedItem.equals("Identifiant")) {
            filteredList.addAll(sanctionList.filtered(sanction ->
                    String.valueOf(sanction.getId()).toLowerCase().contains(searchText.toLowerCase())
            ));
        } else if(selectedItem.equals("Nom")) {
            filteredList.addAll(sanctionList.filtered(sanction ->
                    String.valueOf(sanction.getId_emprunt()).toLowerCase().contains(searchText.toLowerCase())
            ));
        } else if(selectedItem.equals("Prenom")) {
            filteredList.addAll(sanctionList.filtered(sanction ->
                    String.valueOf(sanction.getDateDebut()).toLowerCase().contains(searchText.toLowerCase())
            ));
        } else if(selectedItem.equals("Date de naissance")) {
            filteredList.addAll(sanctionList.filtered(sanction ->
                    String.valueOf(sanction.getDateDebut()).toLowerCase().contains(searchText.toLowerCase())
            ));
        } else if(selectedItem.equals("Role")) {
            filteredList.addAll(sanctionList.filtered(sanction ->
                    String.valueOf(sanction.getDateFin()).toLowerCase().contains(searchText.toLowerCase())
            ));
        }

        sanctionTableView.setItems(filteredList);
    }

    public void onClickSanctionner(ActionEvent actionEvent) {


            try {

                FXMLLoader loader = new FXMLLoader(getClass().getResource("/app/views/PopUp/popUp_valideModification.fxml"));
                Parent root = loader.load();

                ValideModificationController controller = loader.getController();
                controller.setSanction(sanction1,"Sanction abonné", "Voulez-vous sanctionner cet abonné ?");

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

    public void onClickProlonger(ActionEvent actionEvent) {

            try {

                FXMLLoader loader = new FXMLLoader(getClass().getResource("/app/views/popUp/popUp_prolongerDate.fxml"));
                Parent root = loader.load();

                ProlongerDateController controller = loader.getController();
                controller.setSanction(sanction1);

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

    @FXML
    void onClickEnleve(ActionEvent event) {
        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/library", "root", "");
             PreparedStatement stmt = conn.prepareStatement("DELETE FROM penaliser WHERE id_abonnee = ?")) {
            stmt.setInt(1, Integer.parseInt(id));
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Confirmation");
        alert.setHeaderText(null);
        alert.setContentText("Vous avez enlevé la penalisation.");
        alert.showAndWait();

    }

    private void update(String sql){
        try {
            conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/library", "root", "");
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next()) {
                sanctionList.add(new Sanction(rs.getString("id"), rs.getString("abonnee"), rs.getString("livre"),rs.getDate("debutP").toLocalDate(), rs.getDate("finP").toLocalDate()));
                System.out.println(rs.getString("livre"));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    void onClickSanction(ActionEvent event) {
        sanctionList.clear();
        dpColumn.setText("Debut de penalisation");
        fpColumn.setText("Fin de penalisation");
        livreColumn.setVisible(false);
        sanctionnnerButton.setVisible(false);
        prolongerButton.setVisible(false);
        enleveButton.setVisible(true);
        try {
            conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/library", "root", "");
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT u.id, CONCAT(u.nom, ' ', u.prenom) AS abonnee, p.debutP, p.finP FROM penaliser p INNER JOIN utilisateur u ON p.id_abonnee = u.id");
            while (rs.next()) {
                sanctionList.add(new Sanction(rs.getString("id"), rs.getString("abonnee"), "",rs.getDate("debutP").toLocalDate(), rs.getDate("finP").toLocalDate()));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    void onClickLimit(ActionEvent event) {
        sanctionList.clear();
        String sql = "SELECT e.id, CONCAT(u.nom, ' ', u.prenom) AS abonnee, o.titre AS livre, e.date_emprunt AS debutP, e.date_limit AS finP FROM emprunt e INNER JOIN utilisateur u ON e.id_abonnee = u.id JOIN ouvrages o ON e.ref_ouvrage = o.Ref WHERE e.date_limit < NOW()";
        dpColumn.setText("Date d'emprunt");
        fpColumn.setText("Date limite");
        livreColumn.setVisible(true);
        sanctionnnerButton.setVisible(true);
        prolongerButton.setVisible(true);
        enleveButton.setVisible(false);
        update(sql);
    }

    @FXML
    void onClickNSanction(ActionEvent event) {
        sanctionList.clear();
        String sql = "SELECT e.id AS id, CONCAT(a.nom,' ',a.prenom) AS abonnee, o.titre AS livre, e.date_emprunt AS debutP, e.date_limit AS finP " +
                "FROM emprunt e " +
                "INNER JOIN utilisateur a ON e.id_abonnee = a.id " +
                "INNER JOIN ouvrages o ON e.ref_ouvrage = o.Ref WHERE e.date_limit > NOW()";
        dpColumn.setText("Date d'emprunt");
        fpColumn.setText("Date limite");
        livreColumn.setVisible(true);
        sanctionnnerButton.setVisible(true);
        prolongerButton.setVisible(true);
        enleveButton.setVisible(false);
        update(sql);
    }
}
