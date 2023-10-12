package app.controllers.popUp;

import app.Models.Abonnee;
import app.Models.Emprunt;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.stage.Stage;

import java.sql.*;

public class ValidationRestitutionController {
    @FXML
    public Label valeurAbonnee;
    public Label valeurLivre1;
    Emprunt emprunt;
    public void onClickRestitue(ActionEvent actionEvent) throws SQLException {
        Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/library", "root", "");

        // prepare the SQL statement
        String sql = "DELETE FROM emprunt WHERE id_abonnee = ? and ref_ouvrage = ?";
        PreparedStatement stmt = conn.prepareStatement(sql);
        stmt.setInt(1, Integer.parseInt(emprunt.abonnee));
        stmt.setInt(2, Integer.parseInt(emprunt.titreOuvrage));

        // execute the statement
        int rowsAffected = stmt.executeUpdate();
        if (rowsAffected > 0) {
            System.out.println("Emprunt record deleted successfully.");
        } else {
            System.out.println("Emprunt record not found.");
        }

        // close resources
        stmt.close();

        String sql1 = "UPDATE ouvrages SET disponibilite = disponibilite + 1 WHERE Ref = ?";
        PreparedStatement pstmt = conn.prepareStatement(sql1);
        pstmt.setString(1, emprunt.titreOuvrage); // assuming the emprunt object has a method to retrieve the titreOuvrage

        // execute the statement
        int rowsAffected1 = pstmt.executeUpdate();
        if (rowsAffected1 > 0) {
            System.out.println("Disponibilite incremented successfully.");
        } else {
            System.out.println("No matching record found in ouvrages table.");
        }

        // close resources
        pstmt.close();
        conn.close();
        Node source = (Node)  actionEvent.getSource();
        Stage stage  = (Stage) source.getScene().getWindow();
        stage.close();
    }

    public void onClickAnnuler(ActionEvent actionEvent) {
        Node source = (Node)  actionEvent.getSource();
        Stage stage  = (Stage) source.getScene().getWindow();
        stage.close();

    }

    public void setEmprunt(Emprunt emprunt) {
        this.emprunt = emprunt;
        try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/library", "root", "")) {
            String query = "SELECT * FROM utilisateur where id = ?";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setInt(1, Integer.parseInt(emprunt.abonnee));
            ResultSet rs = statement.executeQuery();
            while (rs.next()) {
                valeurAbonnee.setText(rs.getString("nom")+ " " + rs.getString("prenom"));
            }
            statement.close();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/library", "root", "")) {
            String query = "SELECT * FROM ouvrages where Ref = ?";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setInt(1, Integer.parseInt(emprunt.titreOuvrage));
            ResultSet rs = statement.executeQuery();
            while (rs.next()) {
                valeurLivre1.setText(rs.getString("titre"));
            }
            statement.close();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }
}
