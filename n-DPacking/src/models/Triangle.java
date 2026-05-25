package models;

import java.awt.*;
import java.io.Serializable;

/**
 * Classe représentant un triangle isocèle
 * Peut être pivoté de 90° ou 180°
 */
public class Triangle implements Forme, Serializable {
    private static final long serialVersionUID = 1L;
    
    private double baseOriginale;   // Base originale du triangle
    private double hauteurOriginale; // Hauteur originale du triangle
    private int rotation;            // État de rotation : 0, 90, ou 180 degrés
    
    /**
     * Constructeur d'un triangle isocèle
     * @param base la base du triangle
     * @param hauteur la hauteur du triangle
     */
    public Triangle(double base, double hauteur) {
        this.baseOriginale = base;
        this.hauteurOriginale = hauteur;
        this.rotation = 0;
    }
    
    /**
     * Retourne la largeur actuelle en tenant compte de la rotation
     */
    @Override
    public double getLargeur() {
        if (rotation == 90) {
            return hauteurOriginale;  // Rotation 90° : la hauteur devient largeur
        }
        return baseOriginale;  // Rotation 0° ou 180° : la base reste la largeur
    }
    
    /**
     * Retourne la hauteur actuelle en tenant compte de la rotation
     */
    @Override
    public double getHauteur() {
        if (rotation == 90) {
            return baseOriginale;  // Rotation 90° : la base devient hauteur
        }
        return hauteurOriginale;   // Rotation 0° ou 180° : la hauteur reste la hauteur
    }
    
    /**
     * Retourne la base originale
     */
    public double getBaseOriginale() { return baseOriginale; }
    
    /**
     * Retourne la hauteur originale
     */
    public double getHauteurOriginale() { return hauteurOriginale; }
    
    /**
     * Calcule l'aire du triangle : (base × hauteur) / 2
     */
    @Override
    public double getAire() {
        return (baseOriginale * hauteurOriginale) / 2;
    }
    
    /**
     * Pivote le triangle de 90 degrés
     */
    @Override
    public void pivoter90() {
        rotation = (rotation + 90) % 180;
    }
    
    /**
     * Pivote le triangle de 180 degrés
     */
    @Override
    public void pivoter180() {
        rotation = (rotation + 180) % 180;
    }
    
    /**
     * Retourne l'angle de rotation actuel
     */
    public int getRotation() { return rotation; }
    
    /**
     * Dessine le triangle sur le panneau graphique
     * Gère les différentes orientations
     */
    @Override
    public void dessiner(Graphics g, int x, int y, Color couleur) {
        g.setColor(couleur);
        
        int[] pointsX, pointsY;
        int largeur = (int)getLargeur();
        int hauteur = (int)getHauteur();
        
        if (rotation == 0) {
            // Triangle pointe vers le haut
            pointsX = new int[]{x, x + largeur/2, x + largeur};
            pointsY = new int[]{y + hauteur, y, y + hauteur};
        } else if (rotation == 90) {
            // Triangle pointe vers la droite
            pointsX = new int[]{x, x + hauteur, x};
            pointsY = new int[]{y, y + largeur/2, y + largeur};
        } else {
            // Triangle pointe vers le bas (rotation 180°)
            pointsX = new int[]{x, x + largeur/2, x + largeur};
            pointsY = new int[]{y, y + hauteur, y};
        }
        
        g.fillPolygon(pointsX, pointsY, 3);
        g.setColor(Color.BLACK);
        g.drawPolygon(pointsX, pointsY, 3);
    }
    
    /**
     * Crée une copie indépendante du triangle
     */
    @Override
    public Forme copier() {
        Triangle t = new Triangle(baseOriginale, hauteurOriginale);
        t.rotation = this.rotation;
        return t;
    }
    
    @Override
    public String getType() {
        return "Triangle";
    }
    
    @Override
    public String toString() {
        return String.format("Triangle (base = %.1f, hauteur = %.1f)", baseOriginale, hauteurOriginale);
    }
}