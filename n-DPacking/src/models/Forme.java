package models;

import java.awt.*;
import java.io.Serializable;

/**
 * Interface représentant une forme géométrique bidimensionnelle
 * Toutes les formes (Rectangle, Cercle, Triangle) doivent implémenter cette interface
 */
public interface Forme extends Serializable {
    
    /**
     * Calcule l'aire de la forme
     * @return l'aire en unités carrées
     */
    double getAire();
    
    /**
     * Retourne la largeur actuelle de la forme (en tenant compte des rotations)
     * @return la largeur
     */
    double getLargeur();
    
    /**
     * Retourne la hauteur actuelle de la forme (en tenant compte des rotations)
     * @return la hauteur
     */
    double getHauteur();
    
    /**
     * Dessine la forme sur un contexte graphique
     * @param g contexte graphique
     * @param x position X du coin supérieur gauche
     * @param y position Y du coin supérieur gauche
     * @param couleur couleur de remplissage
     */
    void dessiner(Graphics g, int x, int y, Color couleur);
    
    /**
     * Crée une copie indépendante de la forme
     * @return une nouvelle instance identique
     */
    Forme copier();
    
    /**
     * Retourne le type de la forme (Rectangle, Cercle, Triangle)
     * @return le type sous forme de chaîne
     */
    String getType();
    
    /**
     * Effectue une rotation de 90 degrés (π/2 radians)
     */
    void pivoter90();
    
    /**
     * Effectue une rotation de 180 degrés (π radians)
     */
    void pivoter180();
}