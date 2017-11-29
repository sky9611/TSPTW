package com.septanome.model;

import java.util.ArrayList;
import java.util.List;

public class Tournee {
    private List<Chemin> chemins;

    public Tournee(List<Chemin> chemins) {
        this.chemins = chemins;
    }

    public Tournee() {
        // TODO Auto-generated constructor stub
    }

    public List<Chemin> getChemins(){
        return chemins;
    }

    public void setChemins(List<Chemin> cheminList) {
        chemins = new ArrayList<Chemin>(cheminList);
    }

    @Override
    public String toString() {
        String s ="";
        for (Chemin chemin:chemins) {
            s += chemin + "\r\n";
        }
        return s;

    }
}