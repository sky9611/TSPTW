package com.septanome.model;

public class Troncon {
    private long destinationID;
    private double longeur;
    private String nom;
    private long origineID;


    public long getDestinationID() {
        return destinationID;
    }
    public void setDestinationID(long destinationID) {
        this.destinationID = destinationID;
    }
    public double getLongeur() {
        return longeur;
    }
    public void setLongeur(double longeur) {
        this.longeur = longeur;
    }
    public String getNom() {
        return nom;
    }
    public void setNom(String nom) {
        this.nom = nom;
    }
    public long getOrigineID() {
        return origineID;
    }
    public void setOrigineID(long origineID) {
        this.origineID = origineID;
    }

    public Troncon(long destination, double longeur, String nomDeRue, long origine) {
        this.setDestinationID(destination);
        this.setLongeur(longeur);
        this.setNom(nomDeRue);
        this.setOrigineID(origine);
    }

    @Override
    public String toString() {
        return origineID+"->"+destinationID+"("+nom+", "+longeur+")";
    }
}
