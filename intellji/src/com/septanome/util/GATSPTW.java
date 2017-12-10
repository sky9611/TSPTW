package com.septanome.util;

import com.septanome.model.*;

import java.io.*;
import java.util.*;

/**
 * genetic algorithm for TSP with time window
 */
public class GATSPTW {
    private PlanLivraison planLivraison = new PlanLivraison();
    private HashMap<Long, Livraison> livraisonsMap;
    private final double vitesse = 15000 / 3600;
    private Commande commande = new Commande();
    private long idEntrepot;
    private List<Long> idLivraisons = new ArrayList<>();
    private int populationSize = 15;
    private HashMap<Integer, List<Long>> population = new HashMap<>();
    private double pc = 0.75;
    private double pe = 0.3;
    private int M = 10000;
    private int OBC_number = 4;
    private Tournee tournee = new Tournee();
    private int bestIndex = 0;


    public Tournee getTournee() {
        return tournee;
    }

    /**
     * constructeur
     *
     * @param planLivraison le plan de livraison
     * @param commande le commande
     */
    public GATSPTW(PlanLivraison planLivraison, Commande commande) {
        this.planLivraison = planLivraison;
        this.commande = commande;
        for (Livraison l : commande.getListLivraison()) {
            idLivraisons.add(l.getId());
        }
        idEntrepot = commande.getEntrepot().getId();
        livraisonsMap = planLivraison.getLivraisonsMap();
        //OBC_number = (int) commande.getListLivraison().size() / 2;
        //pe = 1/livraisonsMap.size();
    }

    /**
     * fonction pour realiser une copie de profondeur
     *
     * @param src la liste a copier
     * @return la liste copié
     * @throws IOException
     * @throws ClassNotFoundException
     */
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

    /**
     * generer une liste d'ID livraisons aléatoire
     *
     * @return une liste d'ID livraisons aléatoire
     * @throws ClassNotFoundException
     * @throws IOException
     */
    private List<Long> randomSolution() throws ClassNotFoundException, IOException {
        List<Long> listIdLivraisons = new ArrayList<>();
        listIdLivraisons.add(idEntrepot);
        List<Long> l = deepCopy(idLivraisons);
        Collections.shuffle(l);
        listIdLivraisons.addAll(l);

        return listIdLivraisons;
    }

    /**
     *  verifier si la solution correspond aux plages horaires
     *
     * @param l liste de points de livraisons
     * @return retourne vrai si la solution correspond aux plages horaires
     */
    private boolean isFleasible(List<Long> l) {
        for (int i = 1; i < l.size(); i++) {
            if (isViolated(l, i))
                return false;
        }
        return true;
    }

    /**
     *  vérifier si la plage horaire est réspecté
     *
     * @param l liste de points de livraisons
     * @param index index d'un point de livraison
     * @return retourne vrai si la contrainte sur la plage horaire de ce point n'est pas réspecté
     */
    private boolean isViolated(List<Long> l, int index) {
        //long idStart = l.get(index);
        long idDes = l.get(index);
        //double duree = planLivraison.getCheminsMap().get(idStart).get(idDes).getLongeur()/vitesse;
        double arrivalTime = calculeArrivalTime(l)[index];
        double b = livraisonsMap.get(idDes).getHeureDeFin();
        if (arrivalTime > b)
            return true;
        return false;
    }

    /**
     * calculer le temps d'arrive pour chauqe point de livraison
     *
     * @param l liste de points de livraisons
     * @return retourne le temps d'arrive pour chaque point de livraison
     */
    private double[] calculeArrivalTime(List<Long> l) {
        //System.out.println("enter calculeArrivalTime");
        double[] arrivalTimes = new double[l.size()];
        double[] leaveTimes = new double[l.size()];
        arrivalTimes[0] = commande.getHeureDeDepart();
        leaveTimes[0] = commande.getHeureDeDepart();
        for (int i = 1; i < l.size(); i++) {
            long idStart = l.get(i - 1);
            double duree = livraisonsMap.get(idStart).getDuree();
            long idDes = l.get(i);
            double longeur = planLivraison.getCheminsMap().get(idStart).get(idDes).getLongeur();
            //System.out.println(idStart+"-->"+idDes+" "+longeur);
            arrivalTimes[i] = leaveTimes[i - 1] + duree + longeur / vitesse;
            if (arrivalTimes[i] > livraisonsMap.get(idDes).getHeureDeDebut()) {
                leaveTimes[i] = arrivalTimes[i] + duree;
            } else {
                leaveTimes[i] = livraisonsMap.get(idDes).getHeureDeDebut() + duree;
            }
        }
        return arrivalTimes;
    }

    /**
     * calculer la duree totale du livraison
     *
     * @param l liste de points de livraisons
     * @return retourne la duree totale du livraison en seconde
     */
    public double Cout(List<Long> l) {
        int length = l.size();
        double[] arrivalTimes = calculeArrivalTime(l);
        double lastArrivalTime = arrivalTimes[length - 1];
        double lastServiceTime = livraisonsMap.get(l.get(l.size() - 1)).getDuree();
        long lastLivraisonID = l.get(length - 1);
        double lastLeavingTime;
        if (lastArrivalTime > livraisonsMap.get(lastLivraisonID).getHeureDeDebut()) {
            lastLeavingTime = lastArrivalTime + lastServiceTime;
        } else {
            lastLeavingTime = livraisonsMap.get(lastLivraisonID).getHeureDeDebut() + lastServiceTime;
        }
        Chemin lastChemin = planLivraison.getCheminsMap().get(l.get(length - 1)).get(l.get(0));

        return lastLeavingTime + lastChemin.getLongeur() / vitesse;
    }

    /**
     * desorganiser une liste sauf l'element qui represente la meilleur solution
     *
     * @param index l'index du meilleur solution
     * @return retourne la liste desorganisé
     */
    public List<Integer> generateRandomOrder(int index) {
        List<Integer> list = new ArrayList<>();
        for (int i = 0; i < populationSize; i++) {
            if (i != index)
                list.add(i);
        }
        Collections.shuffle(list);
        return list;
    }

    /**
     *  calculer la solution
     *
     * @param iterMax nombre d'iteration maximum
     * @return retourne vrai si la solution est trouvé
     * @throws ClassNotFoundException
     * @throws IOException
     */
    public boolean findSolution(int iterMax) throws ClassNotFoundException, IOException {
        initPopulation();
        int iter = 0;
        List<Long> bestOrder = new ArrayList<>();
        List<Long> best = new ArrayList<>();
        double bestCout = 100000;
//        getBestSolution();
        int bestKeepTime = 0;
        while (iter < iterMax) {
//            System.out.println("iter=" + iter);
            HashMap<Integer, List<Long>> newPopulation = roulette();
            List<Integer> list = generateRandomOrder(bestIndex);
            for (int i = 0; i < (populationSize - 1) / 2; i++) {
                int index1 = list.get(2 * i);
                int index2 = list.get(2 * i + 1);
                if (Math.random() < pc) {
                    List<Long> s1 = newPopulation.get(index1);
                    List<Long> s2 = newPopulation.get(index2);
                    newPopulation.remove(index1);
                    newPopulation.remove(index2);
                    HashMap<Integer, List<Long>> children = orderBasedCrossOver(s1, s2);
                    if (Math.random() < pe) {
                        newPopulation.put(index1, localTwoOpt(children.get(1)));
                    } else {
                        newPopulation.put(index1, children.get(1));
                    }
                    if (Math.random() < pe) {
                        newPopulation.put(index2, localTwoOpt(children.get(2)));
                    } else {
                        newPopulation.put(index2, children.get(2));
                    }
//                    newPopulation.put(index1, children.get(1));
//                    newPopulation.put(index2, children.get(2));
                }
                if (Math.random() < pe) {
                    newPopulation.put(index1, localTwoOpt(newPopulation.get(index1)));
                }
                if (Math.random() < pe) {
                    newPopulation.put(index2, localTwoOpt(newPopulation.get(index1)));
                }
            }
//            for (Map.Entry<Integer, List<Long>> entry : newPopulation.entrySet()) {
//                double possibility = Math.random();
//                if (possibility < pe) {
//                    List<Long> mutationSolution = localTwoOpt(entry.getValue());
//                    newPopulation.put(entry.getKey(), mutationSolution);
//                }
//            }
            population = newPopulation;

            iter++;
            double[] fitnesses = new double[population.size()];
            for (Map.Entry<Integer, List<Long>> entry : population.entrySet()) {
//                System.out.println(entry.getKey()+" "+entry.getValue());
                fitnesses[entry.getKey()] = fitness(entry.getValue());
            }
            List<Long> newBestOrder = getBestSolution();
            if (newBestOrder.equals(bestOrder)) bestKeepTime++;
            else bestKeepTime = 0;
            bestOrder = newBestOrder;
            if (Cout(bestOrder) < bestCout) {
                best = bestOrder;
                bestCout = Cout(bestOrder);
            }
            //System.out.println(maxOf(fitnesses));
            if (isFleasible(bestOrder)) {
                if (averageOf(fitnesses) > 0.99999 * maxOf(fitnesses)) break;
                if (bestKeepTime > 14) break;
            }
        }
        bestOrder = best;
        //bestOrder = getBestSolution();
//        printList(bestOrder);
//        System.out.println(isFleasible(bestOrder));
        System.out.println("iter=" + iter + " keep time=" + bestKeepTime);
        System.out.println(Cout(bestOrder));
        //Tournee t = new Tournee();
        List<Chemin> chemins = new ArrayList<>();
        for (int j = 1; j < bestOrder.size(); j++) {
            Chemin c = planLivraison.getCheminsMap().get(bestOrder.get(j - 1)).get(bestOrder.get(j));
            chemins.add(c);
        }

        Chemin lastChemin = planLivraison.getCheminsMap().get(bestOrder.get(bestOrder.size() - 1)).get(idEntrepot);
        chemins.add(lastChemin);
        tournee.setChemins(chemins);
        return isFleasible(bestOrder);
    }

    /**
     * initialiser la premiere génération de la population
     *
     * @throws IOException
     * @throws ClassNotFoundException
     */
    private void initPopulation() throws IOException, ClassNotFoundException {
        for (int i = 0; i < populationSize; i++) {
            List<Long> l = randomSolution();
            population.put(i, l);
        }

    }

    /**
     * fonction pour evaluer si une solution est bonne, plus la valeur de retour de grande plus la solution est meilleur
     *
     * @param solution la solution
     * @return retourne une valeur pour judger si la solution est bonne
     */
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

    /**
     * operateur de selection
     *
     * @return retourne une population qui survit
     */
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

    /**
     * operateur de cross over
     *
     * @param s1 une solution
     * @param s2 une autre solution
     * @return retourne la liste hybridé
     */
    private HashMap<Integer, List<Long>> orderBasedCrossOver(List<Long> s1, List<Long> s2) {
        List<Long> child1 = new ArrayList<>();
        List<Long> child2 = new ArrayList<>();
        long[] child1_array = new long[s1.size()];
        long[] child2_array = new long[s2.size()];
        HashMap<Integer, List<Long>> result = new HashMap<>();
        HashSet<Integer> index = new HashSet<Integer>();
        long[] idTosBeCrossed = new long[OBC_number];
        while (index.size() < OBC_number) {
            int a = (int) (Math.random() * s1.size());
            index.add(a);
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
            if (child1_array[k] == 0) {
                child1_array[k] = idTosBeCrossed[n];
                n++;
            }

        }

        for (int i = 0; i < child1_array.length; i++)
            child1.add(child1_array[i]);

        result.put(1, child1);

        for (int i = 0; i < indexArray_Integer.length; i++) {
            idTosBeCrossed[i] = s2.get(indexArray_int[i]);
        }
        for (int j = 0; j < s1.size(); j++) {
            if (!contains(idTosBeCrossed, s1.get(j)))
                child2_array[j] = s1.get(j);
        }
        int n2 = 0;
        for (int k = 0; k < child2_array.length; k++) {
            if (child2_array[k] == 0) {
                child2_array[k] = idTosBeCrossed[n2];
                n2++;
            }

        }
//        child2 = Arrays.asList(child2_array);
        for (int i = 0; i < child2_array.length; i++)
            child2.add(child2_array[i]);
        result.put(2, child2);
        return result;

    }

    private boolean contains(long[] a, long id) {
        for (long temp : a) if (temp == id) return true;
        return false;
    }

    /**
     * operateur de mutation
     *
     * @param l une solution
     * @return retourne la solution varié
     * @throws IOException
     * @throws ClassNotFoundException
     */
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
//        List<Long> l2 = deepCopy(l);
//        int i = (int) (Math.random() * l.size());
//        int j = (int) (Math.random() * l.size());
//        List<Long> reverse;
//        if (i < j) {
//            reverse = l2.subList(i, j);
//        } else{
//            reverse = l2.subList(j, i);
//        }
//
//        Collections.reverse(reverse);
//        return l2;

        return l;
    }

    /**
     * trouver la meilleur solution
     * @return retourne la meilleur solution
     */
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
        bestIndex = index;
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
