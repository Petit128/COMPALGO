package algorithms;

import java.util.*;

/**
 * Algorithmes de paquetage 1D (bin packing)
 * Problème : Placer des objets de tailles différentes dans un nombre minimal de bacs de capacité fixe
 */
public class Paquetage1D {
    
    /**
     * ALGORITHME FIRST-FIT (FF)
     * Principe : Pour chaque objet, on le place dans le PREMIER bac où il peut rentrer
     * Complexité : O(n × m) où n = nb objets, m = nb bacs
     * 
     * @param objets tableau des tailles des objets
     * @param capacite capacite de chaque bac
     * @return liste des bacs avec leurs contenus
     */
    public static List<List<Double>> premierAdapte(double[] objets, double capacite) {
        List<List<Double>> bacs = new ArrayList<>();
        
        for (double objet : objets) {
            boolean place = false;
            
            // Parcourir les bacs existants
            for (List<Double> bac : bacs) {
                double espaceRestant = capacite - somme(bac);
                if (objet <= espaceRestant) {
                    bac.add(objet);
                    place = true;
                    break;
                }
            }
            
            // Si aucun bac ne peut accueillir l'objet, créer un nouveau bac
            if (!place) {
                List<Double> nouveauBac = new ArrayList<>();
                nouveauBac.add(objet);
                bacs.add(nouveauBac);
            }
        }
        return bacs;
    }
    
    /**
     * ALGORITHME BEST-FIT (BF)
     * Principe : Pour chaque objet, on le place dans le bac qui laissera le MOINS d'espace restant
     * Complexité : O(n × m) où n = nb objets, m = nb bacs
     * 
     * @param objets tableau des tailles des objets
     * @param capacite capacite de chaque bac
     * @return liste des bacs avec leurs contenus
     */
    public static List<List<Double>> meilleurAdapte(double[] objets, double capacite) {
        List<List<Double>> bacs = new ArrayList<>();
        List<Double> espacesRestants = new ArrayList<>();
        
        for (double objet : objets) {
            int meilleurIndex = -1;
            double plusPetitRestant = capacite;
            
            // Chercher le bac qui laissera le moins d'espace libre après insertion
            for (int i = 0; i < bacs.size(); i++) {
                double restant = espacesRestants.get(i);
                if (objet <= restant && (restant - objet) < plusPetitRestant) {
                    plusPetitRestant = restant - objet;
                    meilleurIndex = i;
                }
            }
            
            if (meilleurIndex != -1) {
                bacs.get(meilleurIndex).add(objet);
                espacesRestants.set(meilleurIndex, espacesRestants.get(meilleurIndex) - objet);
            } else {
                List<Double> nouveauBac = new ArrayList<>();
                nouveauBac.add(objet);
                bacs.add(nouveauBac);
                espacesRestants.add(capacite - objet);
            }
        }
        return bacs;
    }
    
    /**
     * ALGORITHME WORST-FIT (WF)
     * Principe : Pour chaque objet, on le place dans le bac qui laissera le PLUS d'espace restant
     * Complexité : O(n × m) où n = nb objets, m = nb bacs
     * 
     * @param objets tableau des tailles des objets
     * @param capacite capacite de chaque bac
     * @return liste des bacs avec leurs contenus
     */
    public static List<List<Double>> pireAdapte(double[] objets, double capacite) {
        List<List<Double>> bacs = new ArrayList<>();
        List<Double> espacesRestants = new ArrayList<>();
        
        for (double objet : objets) {
            int pireIndex = -1;
            double plusGrandRestant = -1;
            
            // Chercher le bac qui laissera le plus d'espace libre après insertion
            for (int i = 0; i < bacs.size(); i++) {
                double restant = espacesRestants.get(i);
                if (objet <= restant && restant > plusGrandRestant) {
                    plusGrandRestant = restant;
                    pireIndex = i;
                }
            }
            
            if (pireIndex != -1) {
                bacs.get(pireIndex).add(objet);
                espacesRestants.set(pireIndex, espacesRestants.get(pireIndex) - objet);
            } else {
                List<Double> nouveauBac = new ArrayList<>();
                nouveauBac.add(objet);
                bacs.add(nouveauBac);
                espacesRestants.add(capacite - objet);
            }
        }
        return bacs;
    }
    
    /**
     * ALGORITHME DE FORCE BRUTE (recherche exhaustive)
     * Principe : Essayer TOUTES les combinaisons possibles pour trouver la solution optimale
     * Complexité : O(n! × 2^n) - Très lent, utilisable uniquement pour de petits ensembles (n ≤ 10)
     * 
     * @param objets tableau des tailles des objets
     * @param capacite capacite de chaque bac
     * @return la meilleure solution trouvée
     */
    public static List<List<Double>> forceBrute(double[] objets, double capacite) {
        // Trier les objets par taille décroissante pour meilleur élagage
        double[] objetsTries = objets.clone();
        Arrays.sort(objetsTries);
        inverser(objetsTries);
        
        List<List<Double>> meilleureSolution = new ArrayList<>();
        int[] meilleurNbBacs = {objets.length + 1};
        
        forceBruteRecursif(objetsTries, capacite, 0, new ArrayList<>(), meilleureSolution, meilleurNbBacs);
        
        return meilleureSolution;
    }
    
    /**
     * Fonction récursive pour la force brute
     * Explore toutes les affectations possibles des objets aux bacs
     */
    private static void forceBruteRecursif(double[] objets, double capacite, int index,
                                           List<List<Double>> solutionActuelle,
                                           List<List<Double>> meilleureSolution,
                                           int[] meilleurNbBacs) {
        // Si tous les objets sont placés, évaluer la solution
        if (index == objets.length) {
            if (solutionActuelle.size() < meilleurNbBacs[0]) {
                meilleurNbBacs[0] = solutionActuelle.size();
                meilleureSolution.clear();
                for (List<Double> bac : solutionActuelle) {
                    meilleureSolution.add(new ArrayList<>(bac));
                }
            }
            return;
        }
        
        double objet = objets[index];
        
        // Essayer de placer l'objet dans chaque bac existant
        for (int i = 0; i < solutionActuelle.size(); i++) {
            double reste = capacite - somme(solutionActuelle.get(i));
            if (objet <= reste) {
                solutionActuelle.get(i).add(objet);
                forceBruteRecursif(objets, capacite, index + 1, solutionActuelle, 
                                  meilleureSolution, meilleurNbBacs);
                solutionActuelle.get(i).remove(solutionActuelle.get(i).size() - 1);
            }
        }
        
        // Essayer de placer l'objet dans un nouveau bac
        List<Double> nouveauBac = new ArrayList<>();
        nouveauBac.add(objet);
        solutionActuelle.add(nouveauBac);
        forceBruteRecursif(objets, capacite, index + 1, solutionActuelle, 
                          meilleureSolution, meilleurNbBacs);
        solutionActuelle.remove(solutionActuelle.size() - 1);
    }
    
    /**
     * Calcule la somme des éléments d'une liste
     */
    private static double somme(List<Double> liste) {
        double somme = 0;
        for (double d : liste) somme += d;
        return somme;
    }
    
    /**
     * Inverse l'ordre d'un tableau
     */
    private static void inverser(double[] tableau) {
        for (int i = 0; i < tableau.length / 2; i++) {
            double temp = tableau[i];
            tableau[i] = tableau[tableau.length - 1 - i];
            tableau[tableau.length - 1 - i] = temp;
        }
    }
    
    /**
     * EXEMPLE CONTRE-EXEMPLE : PremierAdapte n'est pas optimal
     * Capacité = 1.0, Objets = [0.6, 0.6, 0.6, 0.6, 0.4, 0.4, 0.4]
     * Solution optimale : 3 bacs ([0.6,0.4], [0.6,0.4], [0.6,0.4])
     * PremierAdapte : 4 bacs ([0.6,0.4], [0.6,0.4], [0.6], [0.4,0.4])
     */
    public static double[] getContreExemple() {
        return new double[]{0.6, 0.6, 0.6, 0.6, 0.4, 0.4, 0.4};
    }
}