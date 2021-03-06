package com.septanome.model;

import java.io.Serializable;

public class Livraison extends Point{
    private int heureDeDebut;
    private int heureDeFin;
    private int duree;

    public Livraison(Livraison l){
        this.id=l.getId();
        this.coordX = l.getCoordX();
        this.coordY = l.getCoordY();
        this.heureDeDebut = l.getHeureDeDebut();
        this.heureDeFin = l.getHeureDeFin();
        this.duree = l.getDuree();
    }

    public Livraison(long id, int coordX, int coordY, int duree, int heureDeDebut, int heureDeFin) {
        super(id, coordX, coordY);
        this.duree = duree;
        this.heureDeDebut = heureDeDebut;
        this.heureDeFin = heureDeFin;
    }

    public Livraison(long id, int coordX, int coordY, int duree) {
        super(id, coordX, coordY);
        this.duree = duree;
        this.heureDeDebut = 0;
        this.heureDeFin = Integer.MAX_VALUE;
    }

    public int getDuree() {
        return duree;
    }

    public int getHeureDeDebut() {
        return heureDeDebut;
    }

    public int getHeureDeFin() {
        return heureDeFin;
    }

    public void setHeureDeDebut(int heureDeDebut) {
        this.heureDeDebut = heureDeDebut;
    }
    public void setHeureDeFin(int heureDeFin) {
        this.heureDeFin = heureDeFin;
    }
    @Override
    public String toString() {
        return "("+id+", "+coordX+", "+coordY+", "+heureDeDebut+", "+heureDeFin+")";
    }
}
