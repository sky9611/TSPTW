package com.septanome.util;

import com.septanome.model.Commande;
import com.septanome.model.Livraison;
import com.septanome.model.PlanLivraison;
import com.septanome.model.Tournee;

import java.io.*;
import java.util.*;

import static javafx.scene.input.KeyCode.T;

public class GATSPTW {
    private PlanLivraison planLivraison = new PlanLivraison();
    private HashMap<Long,Livraison> livraisonsMap;
    private final double vitesse = 15000/3600;
    private Commande commande = new Commande();
    private long idEntrepot;
    private List<Long> idLivraisons = new ArrayList<>();
    private int populationSize = 10;

    public GATSPTW(PlanLivraison planLivraison, Commande commande) {
        this.planLivraison = planLivraison;
        this.commande = commande;
        for(Livraison l: commande.getListLivraison()) {
            idLivraisons.add(l.getId());
        }
        idEntrepot = commande.getEntrepot().getId();
        livraisonsMap = planLivraison.getLivraisonsMap();
    }

    private static <T> List<T> deepCopy(List<T> src) throws IOException, ClassNotFoundException {
        ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
        ObjectOutputStream out = new ObjectOutputStream(byteOut);
        out.writeObject(src);

        ByteArrayInputStream byteIn = new ByteArrayInputStream(byteOut.toByteArray());
        ObjectInputStream in = new ObjectInputStream(byteIn);
        @SuppressWarnings("unchecked")
        List<T> dest = (List<T>) in.readObject();
        return dest;
    }

    private List<Long> randomSolution() throws ClassNotFoundException, IOException {
        List<Long> listIdLivraisons = new ArrayList<>();
        listIdLivraisons.add(idEntrepot);
        List<Long> l = deepCopy(idLivraisons);
        Collections.shuffle(l);
        listIdLivraisons.addAll(l);

        return listIdLivraisons;
    }

    public Tournee findSolution(int iterMax) throws ClassNotFoundException, IOException {
        HashMap<Integer,List<Long>> listMap = new HashMap<>();
        for (int i=0;i<populationSize;i++){
            List<Long> l = randomSolution();
        }
        return null;
    }
}
