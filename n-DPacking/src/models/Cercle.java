package models;

import java.awt.*;
import java.io.Serializable;

/**
 * Classe représentant un cercle
 * Un cercle est invariant par rotation (peu importe l'angle, il reste identique)
 */
public class Cercle implements Forme, Serializable {
    private static final long serialVersionUID = 1L;
    
    private double rayon;  // Rayon du cercle
    
    /**
     * Constructeur d'un cercle
     * @param rayon rayon du cercle
     */
    public Cercle(double rayon) {
        this.rayon = rayon;
    }
    
    /**
     * Retourne le rayon du cercle
     */
    public double getRayon() { return rayon; }
    
    /**
     * Retourne le diamètre du cercle (largeur)
     */
    @Override
    public double getLargeur() { return 2 * rayon; }
    
    /**
     * Retourne le diamètre du cercle (hauteur)
     */
    @Override
    public double getHauteur() { return 2 * rayon; }
    
    /**
     * Calcule l'aire du cercle : π × r²
     */
    @Override
    public double getAire() {
        return Math.PI * rayon * rayon;
    }
    
    /**
     * Rotation de 90° - sans effet pour un cercle
     */
    @Override
    public void pivoter90() {
        // Un cercle est invariant par rotation
    }
    
    /**
     * Rotation de 180° - sans effet pour un cercle
     */
    @Override
    public void pivoter180() {
        // Un cercle est invariant par rotation
    }
    
    /**
     * Dessine le cercle sur le panneau graphique
     */
    @Override
    public void dessiner(Graphics g, int x, int y, Color couleur) {
        g.setColor(couleur);
        g.fillOval(x, y, (int)getLargeur(), (int)getHauteur());
        g.setColor(Color.BLACK);
        g.drawOval(x, y, (int)getLargeur(), (int)getHauteur());
    }
    
    /**
     * Crée une copie indépendante du cercle
     */
    @Override
    public Forme copier() {
        return new Cercle(rayon);
    }
    
    @Override
    public String getType() {
        return "Cercle";
    }
    
    @Override
    public String toString() {
        return String.format("Cercle (r = %.1f)", rayon);
    }
}