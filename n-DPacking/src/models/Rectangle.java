package models;

import java.awt.*;
import java.io.Serializable;

/**
 * Classe représentant un rectangle
 * Peut être pivoté de 90 degrés
 */
public class Rectangle implements Forme, Serializable {
    private static final long serialVersionUID = 1L;
    
    private double largeurOriginale;  // Largeur d'origine du rectangle
    private double hauteurOriginale;   // Hauteur d'origine du rectangle
    private int rotation;              // État de rotation : 0 ou 90 degrés
    
    /**
     * Constructeur d'un rectangle
     * @param largeur largeur du rectangle
     * @param hauteur hauteur du rectangle
     */
    public Rectangle(double largeur, double hauteur) {
        this.largeurOriginale = largeur;
        this.hauteurOriginale = hauteur;
        this.rotation = 0;  // Pas de rotation initiale
    }
    
    /**
     * Retourne la largeur actuelle en tenant compte de la rotation
     */
    @Override
    public double getLargeur() {
        // Si rotation de 90°, on échange largeur et hauteur
        return (rotation == 90) ? hauteurOriginale : largeurOriginale;
    }
    
    /**
     * Retourne la hauteur actuelle en tenant compte de la rotation
     */
    @Override
    public double getHauteur() {
        // Si rotation de 90°, on échange largeur et hauteur
        return (rotation == 90) ? largeurOriginale : hauteurOriginale;
    }
    
    /**
     * Retourne la largeur originale (sans rotation)
     */
    public double getLargeurOriginale() { return largeurOriginale; }
    
    /**
     * Retourne la hauteur originale (sans rotation)
     */
    public double getHauteurOriginale() { return hauteurOriginale; }
    
    /**
     * Calcule l'aire du rectangle (indépendante de la rotation)
     */
    @Override
    public double getAire() {
        return largeurOriginale * hauteurOriginale;
    }
    
    /**
     * Pivote le rectangle de 90 degrés
     */
    @Override
    public void pivoter90() {
        rotation = (rotation + 90) % 180;  // Modulo 180 car 180° = retour à l'original
    }
    
    /**
     * Pivote le rectangle de 180 degrés (identique à 0° pour un rectangle)
     */
    @Override
    public void pivoter180() {
        rotation = (rotation + 180) % 180;
    }
    
    /**
     * Dessine le rectangle sur le panneau graphique
     */
    @Override
    public void dessiner(Graphics g, int x, int y, Color couleur) {
        g.setColor(couleur);
        g.fillRect(x, y, (int)getLargeur(), (int)getHauteur());
        g.setColor(Color.BLACK);
        g.drawRect(x, y, (int)getLargeur(), (int)getHauteur());
    }
    
    /**
     * Crée une copie indépendante du rectangle
     */
    @Override
    public Forme copier() {
        Rectangle r = new Rectangle(largeurOriginale, hauteurOriginale);
        if (rotation == 90) r.pivoter90();
        return r;
    }
    
    @Override
    public String getType() {
        return "Rectangle";
    }
    
    @Override
    public String toString() {
        return String.format("Rectangle (%.1f x %.1f)", largeurOriginale, hauteurOriginale);
    }
}