package com.example.greenleaf.Model;

import java.util.Date;

public class Livre {

    String bookId;
    String categorie;
    String date;
    String image;
    String prix;
    String time;
    String titre;
    String auteur;
    String userId;
    String acheteurId;
    String audio;

    public Livre(){

    }

    public Livre(String bookId, String categorie, String date, String image, String prix, String time, String titre, String auteur, String userId, String acheteurId, String audio) {
        this.bookId = bookId;
        this.categorie = categorie;
        this.date = date;
        this.image = image;
        this.prix = prix;
        this.time = time;
        this.titre = titre;
        this.auteur = auteur;
        this.audio = audio;
    }

    public String getAuteur() {
        return auteur;
    }

    public void setAuteur(String auteur) {
        this.auteur = auteur;
    }

    public String getBookId() {
        return bookId;
    }

    public void setBookId(String bookId) {
        this.bookId = bookId;
    }

    public String getCategorie() {
        return categorie;
    }

    public void setCategorie(String categorie) {
        this.categorie = categorie;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getImage() { return image; }

    public void setImage(String image) {
        this.image = image;
    }

    public String getPrix() {
        return prix;
    }

    public void setPrix(String prix) {
        this.prix = prix;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getTitre() {
        return titre;
    }

    public void setTitre(String titre) {
        this.titre = titre;
    }

    public String getUserId() { return userId; }

    public void setUserId(String userId) { this.userId = userId; }

    public String getAcheteurId() {
        return acheteurId;
    }

    public void setAcheteurId(String acheteurId) {
        this.acheteurId = acheteurId;
    }

    public String getAudio() { return audio; }

    public void setAudio(String audio) { this.audio = audio; }
}
