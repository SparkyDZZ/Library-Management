package app.controllers.popUp;

import app.Models.Abonnee;
import app.Models.Ouvrage;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfWriter;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.List;

public class ValidationEmpruntController {
    @FXML
    public Label identifiantValeur;
    @FXML

    public Label nomValeur;
    @FXML

    public Label dateEmpruntValeur;
    @FXML

    public Label prenomValeur;
    @FXML

    public Label refLivre1Valeur;
    @FXML

    public Label refLivre2Valeur;
    @FXML

    public Label refLivre3Valeur;
    @FXML

    public Label dateLimiteValeur;

    @FXML
    private Button printButton;

    private Ouvrage o1 = null;
    private Ouvrage o2 = null;
    private Ouvrage o3 = null;

    Abonnee abonnee;
    List<Ouvrage> listOuvrage;

    public void setAbonnee(Abonnee abonnee, List<Ouvrage> listOuvrage) {
        this.abonnee = abonnee;
        this.listOuvrage = listOuvrage;
        this.o1 = listOuvrage.get(0);
        System.out.println(o1);
        if (listOuvrage.size() >= 2) {
            this.o2 = listOuvrage.get(1);
        }
        if (listOuvrage.size() >= 3) {
            this.o3 = listOuvrage.get(2);
        }


        identifiantValeur.setText(abonnee.getIdentifiant());
        nomValeur.setText(abonnee.getNom());
        prenomValeur.setText(abonnee.getPrenom());
        refLivre1Valeur.setText(String.valueOf(listOuvrage.get(0).getRef()));
        if (listOuvrage.size() > 1) {
            refLivre2Valeur.setText(String.valueOf(listOuvrage.get(1).getRef()));
        }
        if (listOuvrage.size() > 2) {
            refLivre3Valeur.setText(String.valueOf(listOuvrage.get(2).getRef()));
        }
        dateEmpruntValeur.setText(LocalDate.now().toString());
        dateLimiteValeur.setText(LocalDate.now().plusDays(15).toString());

    }

    public void onClickAnnuler(ActionEvent actionEvent) {
        Node source = (Node) actionEvent.getSource();
        Stage stage = (Stage) source.getScene().getWindow();
        stage.close();
    }

    @FXML
    void onClickPrint(ActionEvent event) {
        System.out.println("debut");
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Enregistrer le rapport");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("PDF files (*.pdf)", "*.pdf"));
        File file = fileChooser.showSaveDialog(null);
        if (file == null) {
            return;
        }

        Document document = new Document(PageSize.A6);

        try {

            PdfWriter.getInstance(document, new FileOutputStream(file));


            document.open();
            document.setMargins(50, 50, 30, 30);
            Font fontTitre = new Font(Font.FontFamily.HELVETICA, 13, Font.BOLD);
            Font fontSousTitre = new Font(Font.FontFamily.HELVETICA, 10, Font.BOLD);
            Font font = new Font(Font.FontFamily.HELVETICA, 10);


            Paragraph titre = new Paragraph("Reçu d'emprunt", fontTitre);
            titre.setAlignment(Element.ALIGN_CENTER);
            titre.setSpacingAfter(30);
            document.add(titre);

            Paragraph sectionEmprunt = new Paragraph("Informations sur l'emprunt", fontSousTitre);
            document.add(sectionEmprunt);

            Paragraph dateEmprunt = new Paragraph("Date d'emprunt : " + dateEmpruntValeur.getText(), font);
            Paragraph dateLimite = new Paragraph("Date limite : " + dateLimiteValeur.getText(), font);
            dateLimite.setSpacingAfter(15);
            document.add(dateEmprunt);
            document.add(dateLimite);

            Paragraph sectionAbonne = new Paragraph("Informations sur l'abonné", fontSousTitre);
            document.add(sectionAbonne);

            Paragraph abonne = new Paragraph(abonnee.getIdentifiant() + " " + abonnee.getNom() + " " + abonnee.getPrenom(), font);
            abonne.setSpacingAfter(15);
            document.add(abonne);


            Paragraph sectionOuvrages = new Paragraph("Ouvrages empruntés", fontSousTitre);
            document.add(sectionOuvrages);

            for (int i = 0; i < listOuvrage.size(); i++) {
                Paragraph ouvrage = new Paragraph("Ouvrage " + (i + 1) + ": " + listOuvrage.get(i).getTitre(), font);
                Paragraph exemplaire = new Paragraph("Référence : " + listOuvrage.get(i).getRef(), font);
                exemplaire.setSpacingAfter(10);
                document.add(ouvrage);
                document.add(exemplaire);
            }


            Paragraph sanction = new Paragraph("En cas de retard, vous risquez une sanction d'au minimum 3 semaines.", fontSousTitre);
            sanction.setAlignment(Element.ALIGN_CENTER);
            sanction.setSpacingBefore(5);
            document.add(sanction);


        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (DocumentException e) {
            e.printStackTrace();
        } finally {
            document.close();
        }
        System.out.println("fin");

}

    public void onClickConfirme(ActionEvent actionEvent) {

        try {
            Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/library", "root", "");
            String abonneeSql = "SELECT COUNT(*) FROM penaliser WHERE id_abonnee = ?";
            PreparedStatement abonneeStmt = conn.prepareStatement(abonneeSql);
            abonneeStmt.setInt(1, Integer.parseInt(abonnee.getIdentifiant()));
            ResultSet abonneeRs = abonneeStmt.executeQuery();
            abonneeRs.next();
            int s = abonneeRs.getInt(1);
            abonneeStmt.close();
            if (s!=0){
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Utilisateur penalisé.");
                alert.setHeaderText("L'utilisateur que vous avez selectionnez est penalisé.");
                alert.setContentText("Veuillez attendre jusqu'à la fin de la penalisations.");
                alert.showAndWait();
            }else{
            String countSql = "SELECT COUNT(*) FROM emprunt WHERE id_abonnee = ?";

            String insertSql = "INSERT INTO emprunt (id_abonnee, ref_ouvrage, date_emprunt, date_limit) VALUES (?, ?, ?, ?)";

            String updateSql = "UPDATE ouvrages SET Disponibilite = Disponibilite - 1 WHERE ref = ?";

            PreparedStatement countStmt = conn.prepareStatement(countSql);
            countStmt.setInt(1, Integer.parseInt(abonnee.getIdentifiant()));
            ResultSet countRs = countStmt.executeQuery();
            countRs.next();
            int numLoans = countRs.getInt(1);
            countRs.close();
            countStmt.close();

            if (numLoans < 3) {
                PreparedStatement stmt1 = conn.prepareStatement(insertSql);
                stmt1.setInt(1, Integer.parseInt(abonnee.getIdentifiant()));
                stmt1.setInt(2, o1.getRef());
                stmt1.setString(3, LocalDate.now().toString());
                stmt1.setString(4, LocalDate.now().plusDays(15).toString());
                stmt1.executeUpdate();
                stmt1.close();
                PreparedStatement stmt1Update = conn.prepareStatement(updateSql);
                stmt1Update.setInt(1, o1.getRef());
                stmt1Update.executeUpdate();
                stmt1Update.close();
                numLoans++;
            }else{
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Nombre maximum de prêts atteint.");
                alert.setHeaderText("Vous avez déjà emprunté le nombre maximal de livres.");
                alert.setContentText("Veuillez retourner un livre avant d'en emprunter un nouveau.");
                alert.showAndWait();
            }

            if (o2 != null && numLoans < 3) {
                PreparedStatement stmt2 = conn.prepareStatement(insertSql);
                stmt2.setInt(1, Integer.parseInt(abonnee.getIdentifiant()));
                stmt2.setInt(2, o2.getRef());
                stmt2.setString(3, LocalDate.now().toString());
                stmt2.setString(4, LocalDate.now().plusDays(15).toString());
                stmt2.executeUpdate();
                stmt2.close();
                PreparedStatement stmt2Update = conn.prepareStatement(updateSql);
                stmt2Update.setInt(1, o2.getRef());
                stmt2Update.executeUpdate();
                stmt2Update.close();
                numLoans++;
            }else{
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Nombre maximum de prêts atteint.");
                alert.setHeaderText("Vous avez déjà emprunté le nombre maximal de livres.");
                alert.setContentText("Veuillez retourner un livre avant d'en emprunter un nouveau.");
                alert.showAndWait();
            }

            if (o3 != null && numLoans < 3) {
                PreparedStatement stmt3 = conn.prepareStatement(insertSql);
                stmt3.setInt(1, Integer.parseInt(abonnee.getIdentifiant()));
                stmt3.setInt(2, o3.getRef());
                stmt3.setString(3, LocalDate.now().toString());
                stmt3.setString(4, LocalDate.now().plusDays(15).toString());
                stmt3.executeUpdate();
                stmt3.close();
                PreparedStatement stmt3Update = conn.prepareStatement(updateSql);
                stmt3Update.setInt(1, o3.getRef());
                stmt3Update.executeUpdate();
                stmt3Update.close();
            }else{
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Nombre maximum de prêts atteint.");
                alert.setHeaderText("Vous avez déjà emprunté le nombre maximal de livres.");
                alert.setContentText("Veuillez retourner un livre avant d'en emprunter un nouveau.");
                alert.showAndWait();
            }
            }
            conn.close();

            Node source = (Node) actionEvent.getSource();
            Stage stage = (Stage) source.getScene().getWindow();
            stage.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

}
