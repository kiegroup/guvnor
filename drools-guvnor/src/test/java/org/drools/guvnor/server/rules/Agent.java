package org.drools.guvnor.server.rules;

import java.util.Date;

/**
 * Created by IntelliJ IDEA.
 * User: Guillaume
 * Date: 18 mars 2010
 * Time: 11:05:26
 * To change this template use File | Settings | File Templates.
 */
class Agent {
    private Integer numeroAgent;
    private String nom;
    private String prenom;
    private Date dateDeNaissance;
    private String sexe;
    private String numeroINSEE;
    private String cleINSEE;

    private boolean stagiaire;

    public boolean isStagiaire() {
        return stagiaire;
    }

    public void setStagiaire(boolean stagiaire) {
        this.stagiaire = stagiaire;
    }

    public String getCleINSEE() {
        return cleINSEE;
    }

    public void setCleINSEE(String cleINSEE) {
        this.cleINSEE = cleINSEE;
    }

    public Date getDateDeNaissance() {
        return dateDeNaissance;
    }

    public void setDateDeNaissance(Date dateDeNaissance) {
        this.dateDeNaissance = dateDeNaissance;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public Integer getNumeroAgent() {
        return numeroAgent;
    }

    public void setNumeroAgent(Integer numeroAgent) {
        this.numeroAgent = numeroAgent;
    }

    public String getNumeroINSEE() {
        return numeroINSEE;
    }

    public void setNumeroINSEE(String numeroINSEE) {
        this.numeroINSEE = numeroINSEE;
    }

    public String getPrenom() {
        return prenom;
    }

    public void setPrenom(String prenom) {
        this.prenom = prenom;
    }

    public String getSexe() {
        return sexe;
    }

    public void setSexe(String sexe) {
        this.sexe = sexe;
    }
}