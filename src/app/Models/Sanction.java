package app.Models;

import java.time.LocalDate;

public class Sanction {
    private  String id;

    private String id_emprunt;

    public String getId_livre() {
        return id_livre;
    }

    public void setId_livre(String id_livre) {
        this.id_livre = id_livre;
    }

    private String id_livre;
    private LocalDate dateDebut;
    private LocalDate dateFin;

    public Sanction(String id, String id_emprunt, String id_livre,LocalDate dateDebut, LocalDate dateFin) {
        this.id = id;
        this.id_emprunt = id_emprunt;
        this.id_livre = id_livre;
        this.dateDebut = dateDebut;
        this.dateFin = dateFin;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getId_emprunt() {
        return id_emprunt;
    }

    public void setId_emprunt(String id_emprunt) {
        this.id_emprunt = id_emprunt;
    }

    public LocalDate getDateDebut() {
        return dateDebut;
    }

    public void setDateDebut(LocalDate dateDebut) {
        this.dateDebut = dateDebut;
    }

    public LocalDate getDateFin() {
        return dateFin;
    }

    public void setDateFin(LocalDate dateFin) {
        this.dateFin = dateFin;
    }
}
