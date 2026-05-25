package algorithms;

import models.*;
import java.util.*;

/**
 * Algorithmes de bin packing 2D pour trois types de formes :
 *   • Cercle     — défini par son rayon
 *   • Rectangle  — défini par largeur × hauteur
 *   • Triangle isocèle — défini par base × hauteur
 *
 * Rotations autorisées : 90° et 180°.
 *
 * Approche heuristique : Best-Fit Decreasing Area (BFDA)
 *   1. Trier les formes par aire décroissante.
 *   2. Maintenir une liste d'espaces libres (guillotine).
 *   3. Pour chaque forme, tenter toutes les rotations autorisées et choisir
 *      l'espace laissant la plus petite perte.
 *
 * Approche force brute :
 *   Parcourt toutes les permutations et orientations sur une grille discrète.
 */
public class Paquetage3Formes {

    // ===================================================================
    //  STRUCTURE DE BASE
    // ===================================================================

    /** Forme placée dans le conteneur, avec sa position et son orientation. */
    public static class FormePlacee {
        public final Forme forme;
        public final double x, y;

        public FormePlacee(Forme f, double x, double y) {
            this.forme = f.copier();
            this.x = x; 
            this.y = y;
        }

        /**
         * Test d'intersection basé sur les boîtes englobantes (bounding-box).
         */
        public boolean intersecteBB(double x2, double y2, Forme autre) {
            return !(x + forme.getLargeur() <= x2 + 1e-9 ||
                     x2 + autre.getLargeur() <= x + 1e-9 ||
                     y + forme.getHauteur() <= y2 + 1e-9 ||
                     y2 + autre.getHauteur() <= y + 1e-9);
        }
        
        /**
         * Vérifie si une forme à une position donnée entre en collision avec cette forme
         */
        public boolean collisionAvec(Forme autre, double autreX, double autreY) {
            return intersecteBB(autreX, autreY, autre);
        }
    }

    /** Espace libre rectangulaire (guillotine). */
    private static class Espace {
        double x, y, largeur, hauteur;

        Espace(double x, double y, double l, double h) {
            this.x = x; 
            this.y = y; 
            this.largeur = l; 
            this.hauteur = h;
        }

        double aire() { return largeur * hauteur; }

        boolean peutContenir(Forme f) {
            return f.getLargeur() <= largeur + 1e-9 && f.getHauteur() <= hauteur + 1e-9;
        }
        
        Espace copier() {
            return new Espace(x, y, largeur, hauteur);
        }
    }

    // ===================================================================
    //  HEURISTIQUE : Best-Fit Decreasing Area
    // ===================================================================

    /**
     * Algorithme heuristique BFDA (Best-Fit Decreasing Area).
     *
     * Contre-exemple (non-optimal) :
     *   Conteneur 100×100, formes : Cercle(r=40), 8 rectangles(8×1)
     *   BFDA place le cercle en premier (aire ≈ 5026 > 64),
     *   laisse des espaces difficiles à utiliser pour les rectangles
     *   → optimal place d'abord les rectangles.
     *
     * @param formes     formes à placer
     * @param W          largeur du conteneur
     * @param H          hauteur du conteneur
     * @return liste des formes placées
     */
    public static List<FormePlacee> heuristique(List<Forme> formes, double W, double H) {
        // Trier par aire décroissante
        List<Forme> triees = new ArrayList<>(formes);
        triees.sort((a, b) -> Double.compare(b.getAire(), a.getAire()));

        List<FormePlacee> places = new ArrayList<>();
        List<Espace> espaces = new ArrayList<>();
        espaces.add(new Espace(0, 0, W, H));

        for (Forme f : triees) {
            Forme meilleureForme = null;
            int meilleurEspaceIdx = -1;
            int meilleurOrientation = -1;
            double perteMin = Double.MAX_VALUE;

            // Tester chaque espace × chaque rotation
            for (int ei = 0; ei < espaces.size(); ei++) {
                Espace e = espaces.get(ei);
                List<Forme> rotations = rotations(f);
                for (int ri = 0; ri < rotations.size(); ri++) {
                    Forme candidate = rotations.get(ri);
                    if (e.peutContenir(candidate)) {
                        double perte = e.aire() - candidate.getAire();
                        if (perte < perteMin - 1e-9) {
                            perteMin = perte;
                            meilleureForme = candidate;
                            meilleurEspaceIdx = ei;
                            meilleurOrientation = ri;
                        }
                    }
                }
            }

            if (meilleurEspaceIdx == -1) continue; // ne rentre nulle part

            Espace e = espaces.remove(meilleurEspaceIdx);
            places.add(new FormePlacee(meilleureForme, e.x, e.y));

            // Découpe guillotine
            double dw = e.largeur - meilleureForme.getLargeur();
            double dh = e.hauteur - meilleureForme.getHauteur();
            
            if (dw > 1e-9) {
                espaces.add(new Espace(e.x + meilleureForme.getLargeur(), e.y,
                                       dw, meilleureForme.getHauteur()));
            }
            if (dh > 1e-9) {
                espaces.add(new Espace(e.x, e.y + meilleureForme.getHauteur(),
                                       e.largeur, dh));
            }

            // Trier les espaces par aire décroissante
            espaces.sort((a, b) -> Double.compare(b.aire(), a.aire()));
        }
        return places;
    }

    // ===================================================================
    //  FORCE BRUTE
    // ===================================================================

    /**
     * Force brute : explore toutes les permutations et orientations sur une
     * grille discrète de pas {@code pas}.
     *
     * Complexité : O(n! × rotations^n × (W/pas × H/pas)^n)
     * Pratiquement limité à n ≤ 6-7 avec pas raisonnable.
     *
     * @param formes formes à placer
     * @param W      largeur du conteneur
     * @param H      hauteur du conteneur
     * @param pas    pas de grille (1.0 = exact, plus grand = plus rapide)
     * @return la meilleure solution trouvée
     */
    public static List<FormePlacee> forceBrute(List<Forme> formes, double W, double H, double pas) {
        List<Forme> copie = new ArrayList<>();
        for (Forme f : formes) copie.add(f.copier());

        List<FormePlacee> meilleure = new ArrayList<>();
        double[] meilleureAire = { -1 };

        // Générer toutes les permutations
        List<List<Forme>> perms = permutations(copie);
        
        for (List<Forme> perm : perms) {
            fbRecursif(perm, 0, new ArrayList<>(), W, H, meilleure, meilleureAire, pas);
            // Option pour améliorer les performances : arrêter si on a trouvé une solution parfaite
            if (Math.abs(meilleureAire[0] - sommeAires(formes)) < 1e-6) {
                break;
            }
        }
        return meilleure;
    }
    
    /**
     * Calcule la somme des aires d'une liste de formes
     */
    private static double sommeAires(List<Forme> formes) {
        double somme = 0;
        for (Forme f : formes) somme += f.getAire();
        return somme;
    }

    private static void fbRecursif(List<Forme> formes, int idx,
                                    List<FormePlacee> courant,
                                    double W, double H,
                                    List<FormePlacee> meilleure, double[] meilleureAire,
                                    double pas) {
        double aire = courant.stream().mapToDouble(fp -> fp.forme.getAire()).sum();
        
        // Si on a déjà atteint l'aire maximale théorique, on peut arrêter
        double aireRestanteTheorique = aire + sommeAiresRestantes(formes, idx);
        if (aireRestanteTheorique <= meilleureAire[0]) {
            return; // Impossible de battre la meilleure solution
        }
        
        if (aire > meilleureAire[0] + 1e-9) {
            meilleureAire[0] = aire;
            meilleure.clear();
            for (FormePlacee fp : courant) {
                meilleure.add(new FormePlacee(fp.forme, fp.x, fp.y));
            }
        }
        
        if (idx == formes.size()) return;

        Forme f = formes.get(idx);
        for (Forme candidate : rotations(f)) {
            double fw = candidate.getLargeur(), fh = candidate.getHauteur();
            
            // Optimisation : ignorer si la forme est trop grande
            if (fw > W + 1e-9 || fh > H + 1e-9) continue;
            
            // Calculer les positions possibles de manière plus intelligente
            Set<String> positionsTestees = new HashSet<>();
            
            for (double x = 0; x <= W - fw + 1e-9; x += pas) {
                for (double y = 0; y <= H - fh + 1e-9; y += pas) {
                    // Élagage : positions symétriques
                    String key = String.format("%.1f,%.1f", x, y);
                    if (positionsTestees.contains(key)) continue;
                    positionsTestees.add(key);
                    
                    boolean ok = true;
                    for (FormePlacee p : courant) {
                        if (p.collisionAvec(candidate, x, y)) {
                            ok = false;
                            break;
                        }
                    }
                    if (ok) {
                        courant.add(new FormePlacee(candidate, x, y));
                        fbRecursif(formes, idx + 1, courant, W, H, meilleure, meilleureAire, pas);
                        courant.remove(courant.size() - 1);
                    }
                }
            }
        }
        // Option de sauter cette forme (pour maximiser l'aire avec un sous-ensemble)
        fbRecursif(formes, idx + 1, courant, W, H, meilleure, meilleureAire, pas);
    }
    
    /**
     * Calcule la somme des aires des formes restantes
     */
    private static double sommeAiresRestantes(List<Forme> formes, int idx) {
        double somme = 0;
        for (int i = idx; i < formes.size(); i++) {
            somme += formes.get(i).getAire();
        }
        return somme;
    }

    // ===================================================================
    //  UTILITAIRES
    // ===================================================================

    /**
     * Retourne toutes les orientations distinctes d'une forme.
     * Cercle      → [original] (rotation sans effet)
     * Rectangle   → [original, +90°]  (si non carré)
     * Triangle    → [original, +90°, +180°, +270°]
     */
    public static List<Forme> rotations(Forme f) {
        List<Forme> variants = new ArrayList<>();
        
        if (f instanceof Cercle) {
            variants.add(f.copier());
        } else if (f instanceof Rectangle) {
            Rectangle r = (Rectangle) f;
            variants.add(r.copier());
            if (Math.abs(r.getLargeurOriginale() - r.getHauteurOriginale()) > 1e-9) {
                Forme r90 = r.copier();
                r90.pivoter90();
                variants.add(r90);
            }
        } else if (f instanceof Triangle) {
            Triangle t = (Triangle) f;
            // Rotation 0°
            variants.add(t.copier());
            // Rotation 90°
            Forme t90 = t.copier();
            t90.pivoter90();
            variants.add(t90);
            // Rotation 180°
            Forme t180 = t.copier();
            t180.pivoter180();
            variants.add(t180);
            // Rotation 270° (équivalent à -90°)
            Forme t270 = t.copier();
            t270.pivoter90();
            t270.pivoter90();
            t270.pivoter90();
            // Vérifier si elle est différente des autres
            boolean deja = false;
            for (Forme ex : variants) {
                if (Math.abs(ex.getLargeur() - t270.getLargeur()) < 1e-9 &&
                    Math.abs(ex.getHauteur() - t270.getHauteur()) < 1e-9) {
                    deja = true;
                    break;
                }
            }
            if (!deja) variants.add(t270);
        }
        return variants;
    }

    /** Génère toutes les permutations d'une liste. */
    private static <T> List<List<T>> permutations(List<T> liste) {
        List<List<T>> result = new ArrayList<>();
        if (liste.isEmpty()) { 
            result.add(new ArrayList<>()); 
            return result; 
        }
        T first = liste.get(0);
        List<T> rest = new ArrayList<>(liste.subList(1, liste.size()));
        for (List<T> perm : permutations(rest)) {
            for (int i = 0; i <= perm.size(); i++) {
                List<T> p = new ArrayList<>(perm);
                p.add(i, first);
                result.add(p);
            }
        }
        return result;
    }
}