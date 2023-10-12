package app.Models;

public class Ouvrage {

    public void setRef(int ref) {
        this.ref = ref;
    }


    public int getRef() {
        return ref;
    }

    public int ref;

    public String titre;
    public String auteur;
    public String rayon;

    public int getDisponible() {
        return disponible;
    }

    public int disponible;

    public Ouvrage(int ref, String titre, String auteur, String rayon, int disponible) {
        this.ref = ref;
        this.titre = titre;
        this.auteur = auteur;
        this.rayon = rayon;
        this.disponible = disponible;
    }



    public String getTitre() {
        return titre;
    }

    public void setTitre(String titre) {
        this.titre = titre;
    }

    public String getAuteur() {
        return auteur;
    }

    public void setAuteur(String auteur) {
        this.auteur = auteur;
    }

    public void setDisponible(int disponible) {
        this.disponible = disponible;
    }
    public String getRayon() {
        return rayon;
    }


}
