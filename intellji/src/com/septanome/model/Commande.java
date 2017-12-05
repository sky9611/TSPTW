package com.septanome.model;

import java.io.IOException;
import java.util.*;

import static com.septanome.util.TSPTW.deepCopy;

public class Commande {
    private int heureDeDepart = 0;
    private Point entrepot;
    private List<Livraison> livraisons;

    public Commande(int heureDeDepart, Point entrepot, List<Livraison> livraisons) {
        this.heureDeDepart = heureDeDepart;
        this.entrepot = entrepot;
        this.livraisons = livraisons;
    }

    public Commande(Commande c) throws IOException, ClassNotFoundException {
        this.heureDeDepart = c.getHeureDeDepart();
        this.entrepot = new Point(c.getEntrepot());
        List<Livraison> ll= new ArrayList<>();
        for(Livraison l:c.getListLivraison()){
            Livraison l2=new Livraison(l);
            ll.add(l2);
        }
        this.livraisons = ll;
    }

    public Commande() {
        // TODO Auto-generated constructor stub
    }

    public int getHeureDeDepart() {
            return heureDeDepart;
    }

    public void setHeureDeDepart(int heureDeDepart) {
        this.heureDeDepart = heureDeDepart;
    }

    public Point getEntrepot() {
        return entrepot;
    }

    public void setEntrepot(Point entrepot) {
        this.entrepot = entrepot;
    }

    public List<Livraison> getListLivraison(){
        return livraisons;
    }

    public void addLivraison(Livraison l) {
        this.livraisons.add(l);
    }


    public void setLivraisons(List<Livraison> livraisons) {
        this.livraisons = livraisons;
    }
}

