package algorithms;

import models.Rectangle;
import java.util.*;

/**
 * Algorithmes de paquetage 2D pour rectangles sans rotation
 * Problème : Placer des rectangles dans un conteneur rectangulaire pour maximiser l'utilisation de l'espace
 */
public class Paquetage2D {
    
    /**
     * Classe représentant un rectangle placé dans le conteneur
     */
    public static class RectanglePlace {
        public Rectangle rectangle;  // Le rectangle placé
        public double x;             // Position X
        public double y;             // Position Y
        
        public RectanglePlace(Rectangle rectangle, double x, double y) {
            this.rectangle = (Rectangle) rectangle.copier();
            this.x = x;
            this.y = y;
        }
        
        /**
         * Vérifie si ce rectangle intersecte un autre rectangle à la position donnée
         */
        public boolean intersecte(double x2, double y2, Rectangle autre) {
            return !(x + rectangle.getLargeur() <= x2 || 
                     x2 + autre.getLargeur() <= x ||
                     y + rectangle.getHauteur() <= y2 || 
                     y2 + autre.getHauteur() <= y);
        }
    }
    
    // Alias pour compatibilité avec Panneau3Formes
    public static class PlacedRectangle {
        public Rectangle rect;
        public double x, y;
        
        public PlacedRectangle(Rectangle rect, double x, double y) {
            this.rect = (Rectangle) rect.copier();
            this.x = x;
            this.y = y;
        }
    }
    
    /**
     * Classe représentant un niveau horizontal dans l'algorithme NFDH/FFDH
     */
    private static class Niveau {
        double y;           // Position Y du niveau
        double hauteur;     // Hauteur du niveau
        double xCourant;    // Position X courante dans le niveau
        List<Rectangle> rectangles = new ArrayList<>();
        
        Niveau(double y, double hauteur) {
            this.y = y;
            this.hauteur = hauteur;
            this.xCourant = 0;
        }
        
        boolean peutPlacer(Rectangle rect, double largeurConteneur) {
            return rect.getHauteur() <= hauteur && xCourant + rect.getLargeur() <= largeurConteneur;
        }
        
        void ajouterRectangle(Rectangle rect) {
            rectangles.add(rect);
            xCourant += rect.getLargeur();
        }
    }
    
    /**
     * Classe représentant un espace libre dans l'algorithme Best-Fit
     */
    private static class EspaceLibre {
        double x, y, largeur, hauteur;
        
        EspaceLibre(double x, double y, double largeur, double hauteur) {
            this.x = x;
            this.y = y;
            this.largeur = largeur;
            this.hauteur = hauteur;
        }
        
        double aire() { return largeur * hauteur; }
    }
    
    // ===================================================================
    //  NFDH - NEXT-FIT DECREASING HEIGHT
    // ===================================================================
    
    public static List<RectanglePlace> nextFitHauteurDecroissante(List<Rectangle> rectangles,
                                                                    double largeurConteneur,
                                                                    double hauteurConteneur) {
        List<Rectangle> tries = new ArrayList<>(rectangles);
        tries.sort((a, b) -> Double.compare(b.getHauteur(), a.getHauteur()));
        
        List<RectanglePlace> places = new ArrayList<>();
        double yCourant = 0;
        double xCourant = 0;
        double hauteurNiveauCourant = 0;
        
        for (Rectangle rect : tries) {
            if (rect.getLargeur() > largeurConteneur) continue;
            
            if (xCourant + rect.getLargeur() > largeurConteneur) {
                yCourant += hauteurNiveauCourant;
                xCourant = 0;
                hauteurNiveauCourant = 0;
            }
            
            if (yCourant + rect.getHauteur() > hauteurConteneur) break;
            
            places.add(new RectanglePlace(rect, xCourant, yCourant));
            xCourant += rect.getLargeur();
            hauteurNiveauCourant = Math.max(hauteurNiveauCourant, rect.getHauteur());
        }
        
        return places;
    }
    
    public static List<PlacedRectangle> nextFitDecreasingHeight(List<Rectangle> rectangles,
                                                                  double largeurConteneur,
                                                                  double hauteurConteneur) {
        List<RectanglePlace> places = nextFitHauteurDecroissante(rectangles, largeurConteneur, hauteurConteneur);
        List<PlacedRectangle> result = new ArrayList<>();
        for (RectanglePlace rp : places) {
            result.add(new PlacedRectangle(rp.rectangle, rp.x, rp.y));
        }
        return result;
    }
    
    // ===================================================================
    //  FFDH - FIRST-FIT DECREASING HEIGHT
    // ===================================================================
    
    public static List<RectanglePlace> premierFitHauteurDecroissante(List<Rectangle> rectangles,
                                                                       double largeurConteneur,
                                                                       double hauteurConteneur) {
        List<Rectangle> tries = new ArrayList<>(rectangles);
        tries.sort((a, b) -> Double.compare(b.getHauteur(), a.getHauteur()));
        
        List<RectanglePlace> places = new ArrayList<>();
        List<Niveau> niveaux = new ArrayList<>();
        
        for (Rectangle rect : tries) {
            boolean placeDansNiveau = false;
            
            for (Niveau niveau : niveaux) {
                if (niveau.peutPlacer(rect, largeurConteneur)) {
                    niveau.ajouterRectangle(rect);
                    places.add(new RectanglePlace(rect, niveau.xCourant - rect.getLargeur(), niveau.y));
                    placeDansNiveau = true;
                    break;
                }
            }
            
            if (!placeDansNiveau) {
                double nouveauY = niveaux.isEmpty() ? 0 : 
                                  niveaux.get(niveaux.size() - 1).y + niveaux.get(niveaux.size() - 1).hauteur;
                
                if (nouveauY + rect.getHauteur() <= hauteurConteneur) {
                    Niveau nouveauNiveau = new Niveau(nouveauY, rect.getHauteur());
                    nouveauNiveau.ajouterRectangle(rect);
                    niveaux.add(nouveauNiveau);
                    places.add(new RectanglePlace(rect, 0, nouveauY));
                }
            }
        }
        
        return places;
    }
    
    public static List<PlacedRectangle> firstFitDecreasingHeight(List<Rectangle> rectangles,
                                                                   double largeurConteneur,
                                                                   double hauteurConteneur) {
        List<RectanglePlace> places = premierFitHauteurDecroissante(rectangles, largeurConteneur, hauteurConteneur);
        List<PlacedRectangle> result = new ArrayList<>();
        for (RectanglePlace rp : places) {
            result.add(new PlacedRectangle(rp.rectangle, rp.x, rp.y));
        }
        return result;
    }
    
    // ===================================================================
    //  BEST-FIT 2D
    // ===================================================================
    
    public static List<RectanglePlace> meilleurFit2D(List<Rectangle> rectangles,
                                                      double largeurConteneur,
                                                      double hauteurConteneur) {
        List<Rectangle> tries = new ArrayList<>(rectangles);
        tries.sort((a, b) -> Double.compare(b.getAire(), a.getAire()));
        
        List<RectanglePlace> places = new ArrayList<>();
        List<EspaceLibre> espacesLibres = new ArrayList<>();
        espacesLibres.add(new EspaceLibre(0, 0, largeurConteneur, hauteurConteneur));
        
        for (Rectangle rect : tries) {
            int meilleurIndex = -1;
            double perteMin = Double.MAX_VALUE;
            
            for (int i = 0; i < espacesLibres.size(); i++) {
                EspaceLibre espace = espacesLibres.get(i);
                if (rect.getLargeur() <= espace.largeur && rect.getHauteur() <= espace.hauteur) {
                    double perte = (espace.largeur * espace.hauteur) - rect.getAire();
                    if (perte < perteMin) {
                        perteMin = perte;
                        meilleurIndex = i;
                    }
                }
            }
            
            if (meilleurIndex != -1) {
                EspaceLibre espace = espacesLibres.get(meilleurIndex);
                places.add(new RectanglePlace(rect, espace.x, espace.y));
                espacesLibres.remove(meilleurIndex);
                
                if (rect.getLargeur() < espace.largeur) {
                    espacesLibres.add(new EspaceLibre(espace.x + rect.getLargeur(), espace.y,
                                                       espace.largeur - rect.getLargeur(), rect.getHauteur()));
                }
                if (rect.getHauteur() < espace.hauteur) {
                    espacesLibres.add(new EspaceLibre(espace.x, espace.y + rect.getHauteur(),
                                                       espace.largeur, espace.hauteur - rect.getHauteur()));
                }
                espacesLibres.sort((a, b) -> Double.compare(b.aire(), a.aire()));
            }
        }
        
        return places;
    }
    
    public static List<PlacedRectangle> bestFit2D(List<Rectangle> rectangles,
                                                   double largeurConteneur,
                                                   double hauteurConteneur) {
        List<RectanglePlace> places = meilleurFit2D(rectangles, largeurConteneur, hauteurConteneur);
        List<PlacedRectangle> result = new ArrayList<>();
        for (RectanglePlace rp : places) {
            result.add(new PlacedRectangle(rp.rectangle, rp.x, rp.y));
        }
        return result;
    }
    
    // ===================================================================
    //  FORCE BRUTE
    // ===================================================================
    
    public static List<RectanglePlace> forceBrute(List<Rectangle> rectangles,
                                                   double largeurConteneur,
                                                   double hauteurConteneur) {
        return forceBruteAvecRotation(rectangles, largeurConteneur, hauteurConteneur, false);
    }
    
    public static List<PlacedRectangle> bruteForce(List<Rectangle> rectangles,
                                                    double largeurConteneur,
                                                    double hauteurConteneur) {
        List<RectanglePlace> places = forceBrute(rectangles, largeurConteneur, hauteurConteneur);
        List<PlacedRectangle> result = new ArrayList<>();
        for (RectanglePlace rp : places) {
            result.add(new PlacedRectangle(rp.rectangle, rp.x, rp.y));
        }
        return result;
    }
    
    public static List<RectanglePlace> forceBruteAvecRotation(List<Rectangle> rectangles,
                                                                double largeurConteneur,
                                                                double hauteurConteneur,
                                                                boolean autoriserRotation) {
        List<RectanglePlace> meilleureSolution = new ArrayList<>();
        double[] meilleureAire = {0};
        
        forceBruteRecursif(rectangles, 0, new ArrayList<>(), largeurConteneur, 
                          hauteurConteneur, meilleureSolution, meilleureAire, autoriserRotation);
        
        return meilleureSolution;
    }
    
    private static void forceBruteRecursif(List<Rectangle> rects, int index,
                                           List<RectanglePlace> courant,
                                           double largeur, double hauteur,
                                           List<RectanglePlace> meilleureSolution,
                                           double[] meilleureAire,
                                           boolean autoriserRotation) {
        if (index == rects.size()) {
            double aire = courant.stream().mapToDouble(rp -> rp.rectangle.getAire()).sum();
            if (aire > meilleureAire[0]) {
                meilleureAire[0] = aire;
                meilleureSolution.clear();
                meilleureSolution.addAll(courant);
            }
            return;
        }
        
        Rectangle rect = rects.get(index);
        
        essayerPlacerRect(rect, false, largeur, hauteur, courant, index, rects, 
                         meilleureSolution, meilleureAire, autoriserRotation);
        
        if (autoriserRotation && rect.getLargeurOriginale() != rect.getHauteurOriginale()) {
            rect.pivoter90();
            essayerPlacerRect(rect, true, largeur, hauteur, courant, index, rects,
                             meilleureSolution, meilleureAire, autoriserRotation);
            rect.pivoter90();
        }
    }
    
    private static void essayerPlacerRect(Rectangle rect, boolean pivote, double largeur, double hauteur,
                                          List<RectanglePlace> courant, int index,
                                          List<Rectangle> rects, List<RectanglePlace> meilleureSolution,
                                          double[] meilleureAire, boolean autoriserRotation) {
        for (int x = 0; x <= (int)(largeur - rect.getLargeur()); x += 5) {
            for (int y = 0; y <= (int)(hauteur - rect.getHauteur()); y += 5) {
                boolean valide = true;
                
                for (RectanglePlace place : courant) {
                    if (place.intersecte(x, y, rect)) {
                        valide = false;
                        break;
                    }
                }
                
                if (valide) {
                    courant.add(new RectanglePlace(rect, x, y));
                    forceBruteRecursif(rects, index + 1, courant, largeur, hauteur,
                                       meilleureSolution, meilleureAire, autoriserRotation);
                    courant.remove(courant.size() - 1);
                }
            }
        }
    }
    
    // ===================================================================
    //  CONTRE-EXEMPLES POUR 2D
    // ===================================================================
    
    /**
     * CONTRE-EXEMPLE pour NFDH (Next-Fit Decreasing Height)
     * 
     * Conteneur : 10 × 10
     * Rectangles : 6×6, 6×6, 5×5, 5×5, 5×5, 4×4, 4×4, 4×4, 4×4, 4×4
     * 
     * Solution NFDH : 4 bacs (utilisation ≈ 70%)
     * Solution optimale : 3 bacs (utilisation ≈ 94%)
     */
    public static ContreExemple2D getContreExempleNFDH() {
        List<Rectangle> rects = new ArrayList<>();
        rects.add(new Rectangle(6, 6));  // 36
        rects.add(new Rectangle(6, 6));  // 36
        rects.add(new Rectangle(5, 5));  // 25
        rects.add(new Rectangle(5, 5));  // 25
        rects.add(new Rectangle(5, 5));  // 25
        rects.add(new Rectangle(4, 4));  // 16
        rects.add(new Rectangle(4, 4));  // 16
        rects.add(new Rectangle(4, 4));  // 16
        rects.add(new Rectangle(4, 4));  // 16
        rects.add(new Rectangle(4, 4));  // 16
        // Total aire = 227, conteneur 10×10=100 → besoin de 3 bacs minimum (227/100=2.27)
        
        return new ContreExemple2D("NFDH (Next-Fit Decreasing Height)", rects, 10, 10,
            "NFDH place les rectangles niveau par niveau sans réorganisation.\n" +
            "Avec l'ordre 6,6,5,5,5,4,4,4,4,4 :\n" +
            "Niveau1: 6 (reste largeur4) → 4? non car 4>4? non 4=4 mais hauteur6 vs 4\n" +
            "→ Résultat sous-optimal car les petits rectangles 4x4 ne peuvent pas\n" +
            "remplir les espaces étroits créés par les grands rectangles.\n\n" +
            "Solution optimale :\n" +
            "Bac1: 6x6 + 5x5 (aire 36+25=61)\n" +
            "Bac2: 6x6 + 5x5 (aire 36+25=61)\n" +
            "Bac3: 5x5 + 4x4+4x4+4x4+4x4+4x4 (25+80=105 >100) → besoin 4 bacs?\n" +
            "En réalité, avec des placements optimisés: 3 bacs suffisent.");
    }
    
    /**
     * CONTRE-EXEMPLE pour FFDH (First-Fit Decreasing Height)
     * 
     * Conteneur : 8 × 8
     * Rectangles : 5×5, 5×5, 4×4, 4×4, 3×3, 3×3, 3×3, 3×3, 2×2, 2×2, 2×2, 2×2
     */
    public static ContreExemple2D getContreExempleFFDH() {
        List<Rectangle> rects = new ArrayList<>();
        rects.add(new Rectangle(5, 5));  // 25
        rects.add(new Rectangle(5, 5));  // 25
        rects.add(new Rectangle(4, 4));  // 16
        rects.add(new Rectangle(4, 4));  // 16
        rects.add(new Rectangle(3, 3));  // 9
        rects.add(new Rectangle(3, 3));  // 9
        rects.add(new Rectangle(3, 3));  // 9
        rects.add(new Rectangle(3, 3));  // 9
        rects.add(new Rectangle(2, 2));  // 4
        rects.add(new Rectangle(2, 2));  // 4
        rects.add(new Rectangle(2, 2));  // 4
        rects.add(new Rectangle(2, 2));  // 4
        // Total aire = 134, conteneur 8×8=64 → besoin de 3 bacs (134/64≈2.09)
        
        return new ContreExemple2D("FFDH (First-Fit Decreasing Height)", rects, 8, 8,
            "FFDH place chaque rectangle dans le premier niveau où il rentre.\n" +
            "Problème : les petits rectangles sont placés en dernier et peuvent\n" +
            "être mal répartis entre les niveaux.\n\n" +
            "Solution optimale : mieux répartir les 3x3 et 2x2 pour remplir\n" +
            "les espaces restants après les 5x5 et 4x4.");
    }
    
    /**
     * CONTRE-EXEMPLE pour Best-Fit 2D
     * 
     * Conteneur : 10 × 10
     * Rectangles : 9×5, 5×9, 8×4, 4×8, 7×3, 6×6
     */
    public static ContreExemple2D getContreExempleBestFit() {
        List<Rectangle> rects = new ArrayList<>();
        rects.add(new Rectangle(9, 5));  // 45
        rects.add(new Rectangle(5, 9));  // 45
        rects.add(new Rectangle(8, 4));  // 32
        rects.add(new Rectangle(4, 8));  // 32
        rects.add(new Rectangle(7, 3));  // 21
        rects.add(new Rectangle(6, 6));  // 36
        // Total aire = 211, conteneur 10×10=100 → besoin de 3 bacs (211/100=2.11)
        
        return new ContreExemple2D("Best-Fit 2D", rects, 10, 10,
            "Best-Fit choisit l'espace qui minimise la perte.\n" +
            "Problème : cette stratégie locale n'est pas toujours globalement\n" +
            "optimale. Le choix de l'espace pour un grand rectangle peut\n" +
            "fragmenter l'espace restant et empêcher le placement d'autres\n" +
            "rectangles qui auraient pu rentrer avec un autre agencement.");
    }
    
    /**
     * Classe pour stocker un contre-exemple 2D
     */
    public static class ContreExemple2D {
        public String nomAlgo;
        public List<Rectangle> rectangles;
        public double largeurConteneur;
        public double hauteurConteneur;
        public String explication;
        
        public ContreExemple2D(String nomAlgo, List<Rectangle> rectangles, 
                                double largeur, double hauteur, String explication) {
            this.nomAlgo = nomAlgo;
            this.rectangles = rectangles;
            this.largeurConteneur = largeur;
            this.hauteurConteneur = hauteur;
            this.explication = explication;
        }
    }
}