//
//
//
//
//
// Cette class n'est plus utilise
//
//
//
//
//
package com.septanome.util;

import com.septanome.model.*;

import java.io.*;
import java.util.*;

public class TSPTW {
    private PlanLivraison planLivraison = new PlanLivraison();
    private HashMap<Long,Livraison> livraisonsMap;
    private final double vitesse = 15000/3600;
    private Commande commande = new Commande();
    private long idEntrepot;
    private List<Long> idLivraisons = new ArrayList<>();
    private int levelMax = 10;
    private int levelMaxGVNS = 100;

    public static <T> List<T> deepCopy(List<T> src) throws IOException, ClassNotFoundException {
        ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
        ObjectOutputStream out = new ObjectOutputStream(byteOut);
        out.writeObject(src);

        ByteArrayInputStream byteIn = new ByteArrayInputStream(byteOut.toByteArray());
        ObjectInputStream in = new ObjectInputStream(byteIn);
        @SuppressWarnings("unchecked")
        List<T> dest = (List<T>) in.readObject();
        return dest;
    }

    public static <T> void printList(List<T> list){
        System.out.println("---begin---");
        for(T t : list){
            System.out.println(t);
        }
        System.out.println("---end---");
    }

    public TSPTW(PlanLivraison planLivraison, Commande commande) {
        this.planLivraison = planLivraison;
        this.commande = commande;
        for(Livraison l: commande.getListLivraison()) {
            idLivraisons.add(l.getId());
        }
        idEntrepot = commande.getEntrepot().getId();
        livraisonsMap = planLivraison.getLivraisonsMap();
    }

    public Tournee findSolution(int iterMax) throws ClassNotFoundException, IOException {
        int i = 0;
        List<Long> orderOfLivraison = null;
        List<Long> bestOrder = null;
        Tournee t = new Tournee();
        bestOrder = VNS();
        //System.out.println("asdasdasdadasdasdasd");
        bestOrder = GVNS(bestOrder);
        //printList(bestOrder);
        while(i<iterMax) {
            orderOfLivraison = VNS();
            orderOfLivraison = GVNS(orderOfLivraison);
            if (TSPObjectiveFunction(orderOfLivraison)<TSPObjectiveFunction(bestOrder)) {
                bestOrder = orderOfLivraison;
            }
            i++;
        }

        System.out.println(isFleasible(bestOrder));

        List<Chemin> chemins = new ArrayList<>();
        for(int j=1;j<bestOrder.size();j++) {
            Chemin c = planLivraison.getCheminsMap().get(bestOrder.get(j-1)).get(bestOrder.get(j));
            chemins.add(c);
        }
        Chemin lastChemin = planLivraison.getCheminsMap().get(bestOrder.get(bestOrder.size()-1)).get(idEntrepot);
        chemins.add(lastChemin);
        t.setChemins(chemins);
        return t;
    }

    private List<Long> GVNS(List<Long> l) throws ClassNotFoundException, IOException {
        int level = 1;
        l = VND(l);
        while (level<=levelMaxGVNS) {
            //System.out.println("level="+level);
            //System.out.println("Before perturbation");
            //printList(l);
            List<Long> l2 = perturbation(l,level);
            //System.out.println("After perturbation");
            //printList(l2);
            l2 = VND(l2);
            if (TSPObjectiveFunction(l2)<TSPObjectiveFunction(l)) {
                level=1;
                l = deepCopy(l2);
            } else {
                level++;
            }
        }
        return l;
    }

    private List<Long> VNS() throws ClassNotFoundException, IOException {
        List<Long> orderOfLivraison = null;
        //int n = 0;
        do {
            orderOfLivraison = randomSolution();
            //printList(orderOfLivraison);
            //System.out.println(isFleasible(orderOfLivraison));
            orderOfLivraison = localOneOpt(orderOfLivraison);

//            double[] a = calculeArrivalTime(orderOfLivraison);
//            for(int i=0;i<a.length;i++){
//                System.out.println(orderOfLivraison.get(i)+" - "+a[i]);
//            }

            List<Long> newOrderOfLivraison;
            int level = 1;
            while (!isFleasible(orderOfLivraison)&&level<=levelMax) {
                //System.out.println("level="+level);
                newOrderOfLivraison = perturbation(orderOfLivraison,level);
                newOrderOfLivraison = localOneOpt(newOrderOfLivraison);
//                double[] a = calculeArrivalTime(orderOfLivraison);
//                double[] b = calculeArrivalTime(orderOfLivraison);
//                System.out.println("orderOfLivraison: ");
//                for (int i = 0; i < a.length; i++) {
//                    System.out.println(orderOfLivraison.get(i) + " - " + a[i]);
//                }
//                System.out.println("newOrderOfLivraison: ");
//                for (int i = 0; i < a.length; i++) {
//                    System.out.println(newOrderOfLivraison.get(i) + " - " + b[i]);
//                }
                double evaluation = VNSObjectiveFunction(orderOfLivraison);
                double newEvaluation = VNSObjectiveFunction(newOrderOfLivraison);
//                System.out.println(evaluation+" : "+newEvaluation);
//                System.out.println();
                if (evaluation<newEvaluation) {
                    level++;
                } else {
                    //printList(orderOfLivraison);
                    //printList(newOrderOfLivraison);
                    orderOfLivraison = deepCopy(newOrderOfLivraison);
                    level = 1;
                }
            }
        } while (!isFleasible(orderOfLivraison));

        if(!isFleasible(orderOfLivraison))
            System.out.println("Solution not found");
        return orderOfLivraison;
    }

    private List<Long> randomSolution() throws ClassNotFoundException, IOException {
        List<Long> listIdLivraisons = new ArrayList<>();
        listIdLivraisons.add(idEntrepot);
        List<Long> l = deepCopy(idLivraisons);
        Collections.shuffle(l);
        listIdLivraisons.addAll(l);

        return listIdLivraisons;
    }

    private List<Long> VND(List<Long> l) throws ClassNotFoundException, IOException{
        //System.out.println("Enter VND");
        //printList(l);
        List<Long> l2 = new ArrayList<>();
        do {
            l = localOneOpt(l);
            l2 = deepCopy(l);
            l = localTwoOpt(l);
        } while (!l2.equals(l));
        return l;
    }

    private boolean isFleasible(List<Long> l) {
//        for (int i=1;i<l.size();i++) {
//            if (isViolated(l,i))
//                return false;
//        }
        double cout = VNSObjectiveFunction(l);
        //System.out.println(cout);
        if (cout==0)
            return true;
        return false;
    }

//    public boolean between(double v, double a, double b) {
//        return v>a&&v<b;
//    }

    private boolean isViolated(List<Long> l, int index) {
        //long idStart = l.get(index);
        long idDes = l.get(index);
        //double duree = planLivraison.getCheminsMap().get(idStart).get(idDes).getLongeur()/vitesse;
        double arrivalTime =  calculeArrivalTime(l)[index];
        double b = livraisonsMap.get(idDes).getHeureDeFin();
        if (arrivalTime>b)
            return true;
        return false;
    }

    private double[] calculeArrivalTime(List<Long> l) {
        //System.out.println("enter calculeArrivalTime");
        double[] arrivalTimes = new double[l.size()];
        double[] leaveTimes = new double[l.size()];
        arrivalTimes[0] = commande.getHeureDeDepart();
        leaveTimes[0] = commande.getHeureDeDepart();
        for (int i=1;i<l.size();i++) {
            long idStart = l.get(i-1);
            double duree = livraisonsMap.get(idStart).getDuree();
            long idDes = l.get(i);
            double longeur = planLivraison.getCheminsMap().get(idStart).get(idDes).getLongeur();
            //System.out.println(idStart+"-->"+idDes+" "+longeur);
            arrivalTimes[i] = leaveTimes[i-1] + duree + longeur/vitesse;
            if (arrivalTimes[i]>livraisonsMap.get(idDes).getHeureDeDebut()) {
                leaveTimes[i] = arrivalTimes[i] + duree;
            } else {
                leaveTimes[i] = livraisonsMap.get(idDes).getHeureDeDebut()+duree;
            }
        }
        return arrivalTimes;
    }

    private List<Long> localOneOpt(List<Long> l) throws ClassNotFoundException, IOException {
        //System.out.println("Enter localOneOpt");
        int length = l.size();
        //System.out.println("length = "+length);
        //printList(l);
        for (int i=length-1;i>0;i--) {
            //System.out.println("i:" + i);
            //printList(l);
            if (isViolated(l, i)) {
                for (int j = 1; j < i; j++) {
                    List<Long> l2 = deepCopy(l);
                    l2.remove(i);
                    l2.add(j, l.get(i));
                    //System.out.println("l2: ");
                    //printList(l2);
                    if (VNSObjectiveFunction(l) > VNSObjectiveFunction(l2))
                        return l2;
                }
            }
        }
        for (int i=length-1;i>0;i--) {
            //System.out.println("i:" + i);
            //printList(l);
            if(!isViolated(l,i)) {
                for (int j=i+1;j<length;j++) {
                    List<Long> l2 = deepCopy(l);
                    l2.remove(i);
                    l2.add(j, l.get(i));
                    if( VNSObjectiveFunction(l)>VNSObjectiveFunction(l2))
                        return l2;
                }
            }
        }
        for (int i=length-1;i>0;i--) {
            //System.out.println("i:" + i);
            //printList(l);
            if(!isViolated(l,i)) {
                for (int j=1;j<i;j++) {
                    List<Long> l2 = deepCopy(l);
                    l2.remove(i);
                    l2.add(j, l.get(i));
                    if( VNSObjectiveFunction(l)>VNSObjectiveFunction(l2))
                        return l2;
                }
            }
        }
        for (int i=length-1;i>0;i--) {
            //System.out.println("i:" + i);
            //printList(l);
            if(isViolated(l,i)) {
                for (int j=i+1;j<length;j++) {
                    List<Long> l2 = deepCopy(l);
                    l2.remove(i);
                    l2.add(j, l.get(i));
                    if( VNSObjectiveFunction(l)>VNSObjectiveFunction(l2))
                        return l2;
                }
            }
        }

        return l;
    }

    private List<Long> localTwoOpt(List<Long> l) throws ClassNotFoundException, IOException {
        int length = l.size();
        for (int i=1;i<length;i++) {
            for (int j=i+1;j<length;j++) {
                List<Long> l2 = deepCopy(l);
                List<Long> reverse = l2.subList(i, j+1);
                Collections.reverse(reverse);
                if( VNSObjectiveFunction(l)>VNSObjectiveFunction(l2))
                    return l2;
            }
        }

        return l;
    }

    private double VNSObjectiveFunction(List<Long> l) {
        //System.out.println("enter VNSObjectiveFunction");
        long[] idArray = new long[l.size()];
        for (int i=0;i<l.size();i++)
            idArray[i]=l.get(i);
        double result = 0;
        double[] arrivalTimes = calculeArrivalTime(l);
        double[] dueTimes = new double[arrivalTimes.length];
        dueTimes[0] = commande.getHeureDeDepart();
        for (int i=1;i<arrivalTimes.length;i++) {
            dueTimes[i] = livraisonsMap.get(l.get(i)).getHeureDeFin();
            result += (arrivalTimes[i]-dueTimes[i]>0)?(arrivalTimes[i]-dueTimes[i]):0;
        }

        return result;
    }

    public double TSPObjectiveFunction(List<Long> l) {
        int length = l.size();
        double[] arrivalTimes = calculeArrivalTime(l);
        double lastArrivalTime = arrivalTimes[length-1];
        double lastServiceTime = livraisonsMap.get(l.get(l.size()-1)).getDuree();
        long lastLivraisonID = l.get(length-1);
        double lastLeavingTime;
        if (lastArrivalTime>livraisonsMap.get(lastLivraisonID).getHeureDeDebut()) {
            lastLeavingTime = lastArrivalTime + lastServiceTime;
        } else {
            lastLeavingTime = livraisonsMap.get(lastLivraisonID).getHeureDeDebut()+lastServiceTime;
        }
        Chemin lastChemin = planLivraison.getCheminsMap().get(l.get(length-1)).get(l.get(0));

        return lastLeavingTime+lastChemin.getLongeur()/vitesse;
    }

    public List<Long> perturbation(List<Long> l, int level) throws ClassNotFoundException, IOException{
        List<Long> result = deepCopy(l);
        List<Long> before;
        int length = result.size();
        Random random = new Random();
        for(int i=0;i<level;i++) {
            before = deepCopy(result);
            int index1=random.nextInt(length-1)+1;
            int index2=random.nextInt(length-1)+1;
            result.remove(index2);
            result.add(index1, before.get(index2));
        }

        return result;
    }
}
