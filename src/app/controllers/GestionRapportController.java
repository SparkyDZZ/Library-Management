package app.controllers;

import app.Models.Emprunt;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.net.URL;
import java.sql.*;
import java.time.LocalDate;
import java.util.ResourceBundle;

public class GestionRapportController implements Initializable {


    @FXML
    private DatePicker empruntJour_datePick;



    @FXML
    private TableView<Emprunt> rapportTableView;

    @FXML
    private TableColumn<Emprunt, String> codeColumn;
    @FXML
    private TableColumn<Emprunt, String> abonneColumn;
    @FXML
    private TableColumn<Emprunt, String> titreColumn;
    @FXML
    private TableColumn<Emprunt, String> dateEmpruntColumn;
    @FXML
    private TableColumn<Emprunt, String> dateLimiteColumn;
    private ObservableList<Emprunt> empruntList = FXCollections.observableArrayList();


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        empruntJour_datePick.setValue(LocalDate.now());

        codeColumn.setCellValueFactory(new PropertyValueFactory<>("code"));
        abonneColumn.setCellValueFactory(new PropertyValueFactory<>("abonnee"));
        titreColumn.setCellValueFactory(new PropertyValueFactory<>("titreOuvrage"));
        dateEmpruntColumn.setCellValueFactory(new PropertyValueFactory<>("dateEmprunt"));
        dateLimiteColumn.setCellValueFactory(new PropertyValueFactory<>("dateLimite"));

        try {
            Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/library", "root", "");
            PreparedStatement stmt = conn.prepareStatement("SELECT * FROM emprunt");
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Emprunt emprunt = new Emprunt(rs.getString("id"), rs.getString("id_abonnee"), rs.getString("ref_ouvrage"), rs.getDate("date_emprunt").toLocalDate(), rs.getDate("date_limit").toLocalDate());
                empruntList.add(emprunt);
            }
            rs.close();
            stmt.close();
            conn.close();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        rapportTableView.setItems(empruntList);

    }

    public void onClickImprimeTout(ActionEvent actionEvent) {

        try {
            Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/library", "root", "");


            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT emprunt.id, emprunt.date_emprunt, emprunt.date_limit " +
                    "FROM emprunt " +
                    "JOIN utilisateur ON utilisateur.id = emprunt.id_abonnee where emprunt.date_emprunt = DATE(NOW())");

            Document document = new Document(PageSize.A4);

            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Enregistrer le rapport");
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("PDF files (*.pdf)", "*.pdf"));
            Stage stage = (Stage) rapportTableView.getScene().getWindow();
            File file = fileChooser.showSaveDialog(stage);
            if (file == null) {
                return;
            }

            PdfWriter.getInstance(document, new FileOutputStream(file));

            document.open();
            Font font = new Font(Font.FontFamily.HELVETICA, 13, Font.BOLD);
            Paragraph titre = new Paragraph("Rapport de l'ensemble des emprunts", font);
            titre.setIndentationLeft(20);
            titre.setSpacingAfter(40);
            document.add(titre);


            PdfPTable table = new PdfPTable(3);
            table.setWidths(new float[]{1, 1, 1});
            Font fontTable = new Font(Font.FontFamily.HELVETICA, 9);

            table.addCell(new PdfPCell(new Paragraph("Code",fontTable)));
            table.addCell(new PdfPCell(new Paragraph("Date d'emprunt",fontTable)));
            table.addCell(new PdfPCell(new Paragraph("Date limite",fontTable)));

            while (rs.next()) {
                table.addCell(new PdfPCell(new Paragraph(rs.getString("id"),fontTable)));
                table.addCell(new PdfPCell(new Paragraph(rs.getString("date_emprunt"),fontTable)));
                table.addCell(new PdfPCell(new Paragraph(rs.getString("date_limit"),fontTable)));
            }

            document.add(table);

            document.close();

            System.out.println("PDF généré avec succès !");
        } catch (Exception e) {
            e.printStackTrace();
        }

    }








    public void onClickImprimeJour(ActionEvent actionEvent) {

        try {
            Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/library", "root", "");


            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT emprunt.id, CONCAT(utilisateur.nom,' ',utilisateur.prenom) AS abonnee, ouvrages.titre AS livre, emprunt.date_emprunt, emprunt.date_limit " +
                    "FROM emprunt " +
                    "JOIN utilisateur ON utilisateur.id = emprunt.id_abonnee JOIN ouvrages ON ouvrages.Ref = emprunt.ref_ouvrage where emprunt.date_emprunt = DATE(NOW())");

            Document document = new Document(PageSize.A4);

            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Enregistrer le rapport");
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("PDF files (*.pdf)", "*.pdf"));
            Stage stage = (Stage) rapportTableView.getScene().getWindow();
            File file = fileChooser.showSaveDialog(stage);
            if (file == null) {
                return;
            }

            PdfWriter.getInstance(document, new FileOutputStream(file));

            document.open();
            Font font = new Font(Font.FontFamily.HELVETICA, 13, Font.BOLD);
            Paragraph titre = new Paragraph("Rapport de l'ensemble des emprunts", font);
            titre.setIndentationLeft(20);
            titre.setSpacingAfter(40);
            document.add(titre);


            PdfPTable table = new PdfPTable(5);
            table.setWidths(new float[]{1, 1, 1, 1, 1});
            Font fontTable = new Font(Font.FontFamily.HELVETICA, 9);

            table.addCell(new PdfPCell(new Paragraph("Code",fontTable)));
            table.addCell(new PdfPCell(new Paragraph("Abonné(e)",fontTable)));
            table.addCell(new PdfPCell(new Paragraph("Livre",fontTable)));
            table.addCell(new PdfPCell(new Paragraph("Date d'emprunt",fontTable)));
            table.addCell(new PdfPCell(new Paragraph("Date limite",fontTable)));

            while (rs.next()) {
                table.addCell(new PdfPCell(new Paragraph(rs.getString("id"),fontTable)));
                table.addCell(new PdfPCell(new Paragraph(rs.getString("abonnee"),fontTable)));
                table.addCell(new PdfPCell(new Paragraph(rs.getString("livre"),fontTable)));
                table.addCell(new PdfPCell(new Paragraph(rs.getString("date_emprunt"),fontTable)));
                table.addCell(new PdfPCell(new Paragraph(rs.getString("date_limit"),fontTable)));
            }

            document.add(table);

            document.close();

            System.out.println("PDF généré avec succès !");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void onClickRecherche(ActionEvent actionEvent) {
        LocalDate dateEmprunt = empruntJour_datePick.getValue();
        System.out.println("Date sélectionnée : " + dateEmprunt);
        String searchText = dateEmprunt.toString();

        ObservableList<Emprunt> filteredList = FXCollections.observableArrayList();
        filteredList.addAll(empruntList.filtered(emprunt ->
                emprunt.getDateEmprunt().toString().toLowerCase().contains(searchText.toLowerCase())
        ));
        rapportTableView.setItems(filteredList);



    }


}


