package app.Models;

public class Utilisateur {
    private String identifiant;
    private String nom;
    private String prenom;
    private String motdepasse;
    private String Role;

    public Utilisateur(String identifiant, String nom, String prenom, String motdepasse, String Role) {
        this.identifiant = identifiant;
        this.nom = nom;
        this.prenom = prenom;
        this.motdepasse = motdepasse;
        this.Role = Role;
    }

    public String getRole() {
        return Role;
    }

    public void setRole(String role) {
        Role = role;
    }
}
