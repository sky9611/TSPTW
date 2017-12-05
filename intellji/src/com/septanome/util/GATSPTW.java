package com.septanome.util;

import com.septanome.model.*;

import java.io.*;
import java.util.*;

import static com.septanome.util.TSPTW.printList;

public class GATSPTW {
    private PlanLivraison planLivraison = new PlanLivraison();
    private HashMap<Long, Livraison> livraisonsMap;
    private final double vitesse = 15000 / 3600;
    private Commande commande = new Commande();
    private long idEntrepot;
    private List<Long> idLivraisons = new ArrayList<>();
    private int populationSize = 20;
    private HashMap<Integer, List<Long>> population = new HashMap<>();
    private double pc = 0.8;
    private double pe = 0.2;
    private int M = 1000;
    private int OBC_number;
    private Tournee tournee = new Tournee();

    public Tournee getTournee() {
        return tournee;
    }



    public GATSPTW(PlanLivraison planLivraison, Commande commande) {
        this.planLivraison = planLivraison;
        this.commande = commande;
        for (Livraison l : commande.getListLivraison()) {
            idLivraisons.add(l.getId());
        }
        idEntrepot = commande.getEntrepot().getId();
        livraisonsMap = planLivraison.getLivraisonsMap();
        OBC_number = (int)commande.getListLivraison().size()/2;
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

    private boolean isFleasible(List<Long> l) {
        for (int i=1;i<l.size();i++) {
            if (isViolated(l,i))
                return false;
        }
        return true;
    }

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

    public double Cout(List<Long> l) {
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

    public boolean findSolution(int iterMax) throws ClassNotFoundException, IOException {
        initPopulation();
        int iter = 0;
        List<Long> bestOrder = new ArrayList<>();
        int bestKeepTime = 0;
        while (iter < iterMax) {
            //System.out.println("iter=" + iter);
            HashMap<Integer, List<Long>> newPopulation = roulette();
            int n = (int) pc * population.size();
            for (int i = 0; i < n; i = i + 2) {
                int index1 = (int) (Math.random() * newPopulation.size());
                int index2 = (int) (Math.random() * newPopulation.size());
                List<Long> s1 = newPopulation.get(index1);
                List<Long> s2 = newPopulation.get(index2);
                newPopulation.remove(index1);
                newPopulation.remove(index2);
                HashMap<Integer, List<Long>> children = orderBasedCrossOver(s1, s2);
                newPopulation.put(newPopulation.size(), children.get(1));
                if (newPopulation.size() < population.size())
                    newPopulation.put(newPopulation.size(), children.get(2));
            }
            for (Map.Entry<Integer, List<Long>> entry : newPopulation.entrySet()) {
                double possibility = Math.random();
                if (possibility < pe) {
                    List<Long> mutationSolution = localTwoOpt(entry.getValue());
                    newPopulation.put(entry.getKey(), mutationSolution);
                }
            }
            population = newPopulation;
            List<Long> newBestOrder = getBestSolution();
            if (newBestOrder.equals(bestOrder)) bestKeepTime++;
            else bestKeepTime = 0;
            bestOrder = newBestOrder;


            iter++;
            double[] fitnesses = new double[population.size()];
            for (Map.Entry<Integer, List<Long>> entry : population.entrySet()) {
                fitnesses[entry.getKey()] = fitness(entry.getValue());
            }
            if (averageOf(fitnesses) > 0.99999 * maxOf(fitnesses)) break;
            if (bestKeepTime > 10) break;
        }

        //bestOrder = getBestSolution();
//        printList(bestOrder);
//        System.out.println(isFleasible(bestOrder));
        System.out.println("iter="+iter+" keep time="+bestKeepTime);
        System.out.println(Cout(bestOrder));
        //Tournee t = new Tournee();
        List<Chemin> chemins = new ArrayList<>();
        for (
                int j = 1; j < bestOrder.size(); j++)

        {
            Chemin c = planLivraison.getCheminsMap().get(bestOrder.get(j - 1)).get(bestOrder.get(j));
            chemins.add(c);
        }

        Chemin lastChemin = planLivraison.getCheminsMap().get(bestOrder.get(bestOrder.size() - 1)).get(idEntrepot);
        chemins.add(lastChemin);
        tournee.setChemins(chemins);
        return isFleasible(bestOrder);
    }

    private void initPopulation() throws IOException, ClassNotFoundException {
        for (int i = 0; i < populationSize; i++) {
            List<Long> l = randomSolution();
            population.put(i, l);
        }

    }

    private double fitness(List<Long> solution) {
        int length = solution.size();
        double[] arrivalTimes = new double[solution.size()];
        double[] leaveTimes = new double[solution.size()];
        arrivalTimes[0] = commande.getHeureDeDepart();
        leaveTimes[0] = commande.getHeureDeDepart();
        for (int i = 1; i < solution.size(); i++) {
            long idStart = solution.get(i - 1);
            double duree = livraisonsMap.get(idStart).getDuree();
            long idDes = solution.get(i);
            double longeur = planLivraison.getCheminsMap().get(idStart).get(idDes).getLongeur();
            //System.out.println(idStart+"-->"+idDes+" "+longeur);
            arrivalTimes[i] = leaveTimes[i - 1] + duree + longeur / vitesse;
            if (arrivalTimes[i] > livraisonsMap.get(idDes).getHeureDeDebut()) {
                leaveTimes[i] = arrivalTimes[i] + duree;
            } else {
                leaveTimes[i] = livraisonsMap.get(idDes).getHeureDeDebut() + duree;
            }
        }
        double lastLeavingTime = leaveTimes[length - 1];
        Chemin lastChemin = planLivraison.getCheminsMap().get(solution.get(length - 1)).get(solution.get(0));
        double totalTime = lastLeavingTime + lastChemin.getLongeur() / vitesse - commande.getHeureDeDepart();

        long[] idArray = new long[length];
        for (int i = 0; i < length; i++)
            idArray[i] = solution.get(i);
        double result = 0;
        double[] dueTimes = new double[arrivalTimes.length];
        dueTimes[0] = commande.getHeureDeDepart();
        for (int i = 1; i < arrivalTimes.length; i++) {
            dueTimes[i] = livraisonsMap.get(solution.get(i)).getHeureDeFin();
            result += (arrivalTimes[i] - dueTimes[i] > 0) ? (arrivalTimes[i] - dueTimes[i]) : 0;
        }

        return 1 / (totalTime + result * M);
    }

    private HashMap<Integer, List<Long>> roulette() {
        double[] fitnesses = new double[population.size()];
        for (Map.Entry<Integer, List<Long>> entry : population.entrySet()) {
            fitnesses[entry.getKey()] = fitness(entry.getValue());
        }
        double sum = 0;
        for (double tempDouble : fitnesses)
            sum += tempDouble;
        double[] possibilities = new double[fitnesses.length];
        HashMap<Integer, List<Long>> newPopulation = new HashMap<>();
        for (int i = 0; i < fitnesses.length; i++) {
            if (i == 0) possibilities[0] = fitnesses[0] / sum;
            else possibilities[i] = possibilities[i - 1] + fitnesses[i] / sum;
        }

        for (int i = 0; i < fitnesses.length; i++) {
            double p = Math.random();
            if (p < possibilities[0]) {
                newPopulation.put(newPopulation.size(), population.get(0));
            } else {
                int j = 1;
                while (p > possibilities[j]) j++;
                newPopulation.put(newPopulation.size(), population.get(j));
            }
        }

        return newPopulation;
    }

    private HashMap<Integer, List<Long>> orderBasedCrossOver(List<Long> s1, List<Long> s2) {
        List<Long> child1;
        List<Long> child2;
        Long[] child1_array = new Long[s1.size()];
        Long[] child2_array = new Long[s2.size()];
        HashMap<Integer, List<Long>> result = new HashMap<>();
        HashSet<Integer> index = new HashSet<Integer>();
        long[] idTosBeCrossed = new long[OBC_number];
        while (index.size() < OBC_number) {
            index.add((int) Math.random() * s1.size());

        }
        Integer[] indexArray_Integer = index.toArray(new Integer[]{});
        int[] indexArray_int = new int[indexArray_Integer.length];
        for (int i = 0; i < indexArray_Integer.length; i++) {
            indexArray_int[i] = indexArray_Integer[i].intValue();
            idTosBeCrossed[i] = s1.get(indexArray_int[i]);
        }
        for (int j = 0; j < s2.size(); j++) {
            if (!contains(idTosBeCrossed, s2.get(j)))
                child1_array[j] = s2.get(j);
        }
        int n = 0;
        for (int k = 0; k < child1_array.length; k++) {
            if (child1_array[k] == 0) child1_array[k] = idTosBeCrossed[n];
            n++;
        }
        child1 = Arrays.asList(child1_array);
        result.put(1, child1);

        for (int i = 0; i < indexArray_Integer.length; i++) {
            idTosBeCrossed[i] = s2.get(indexArray_int[i]);
        }
        for (int j = 0; j < s1.size(); j++) {
            if (!contains(idTosBeCrossed, s1.get(j)))
                child2_array[j] = s1.get(j);
        }
        int n2 = 0;
        for (int k = 0; k < child1_array.length; k++) {
            if (child2_array[k] == 0) child2_array[k] = idTosBeCrossed[n];
            n2++;
        }
        child2 = Arrays.asList(child2_array);
        result.put(2, child2);
        return result;
    }

    private boolean contains(long[] a, long id) {
        for (long temp : a) if (temp == id) return true;
        return false;
    }

    private List<Long> localTwoOpt(List<Long> l) throws IOException, ClassNotFoundException {
        int length = l.size();
        for (int i = 1; i < length; i++) {
            for (int j = i + 1; j < length; j++) {
                List<Long> l2 = deepCopy(l);
                List<Long> reverse = l2.subList(i, j + 1);
                Collections.reverse(reverse);
                if (fitness(l2) > fitness(l))
                    return l2;
            }
        }

        return l;
    }

    private List<Long> getBestSolution() {
        double maxFitness = 0;
        int index = 0;
        for (Map.Entry<Integer, List<Long>> entry : population.entrySet()) {
            double fitness = fitness(entry.getValue());
            if (fitness > maxFitness) {
                maxFitness = fitness;
                index = entry.getKey();
            }
            ;
        }

        return population.get(index);
    }

    private double maxOf(double[] l) {
        double max = 0;
        for (double temp : l) if (temp > max) max = temp;
        return max;
    }

    private double averageOf(double[] l) {
        double sum = 0;
        for (double temp : l) sum += temp;
        return sum / l.length;
    }
}
