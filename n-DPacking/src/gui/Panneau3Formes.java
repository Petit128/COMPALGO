package gui;

import algorithms.Paquetage3Formes;
import models.*;
import models.Rectangle;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Panneau pour le paquetage 2D avec trois formes (Rectangle, Cercle, Triangle)
 * Permet de saisir des formes, choisir un algorithme, et visualiser graphiquement le résultat
 */
public class Panneau3Formes extends JPanel {
    private static final long serialVersionUID = 1L;
    
    // Types de formes
    private enum TypeForme { RECTANGLE, CERCLE, TRIANGLE }
    
    // Composants
    private JTextField champLargeurConteneur;
    private JTextField champHauteurConteneur;
    private JTable tableFormes;
    private DefaultTableModel modeleTable;
    private JPanel panneauDessin;
    private JTextArea zoneResultats;
    private List<Paquetage3Formes.FormePlacee> placementCourant;
    
    // Champs de saisie pour ajouter des formes
    private JComboBox<TypeForme> comboType;
    private JTextField champValeur1;
    private JTextField champValeur2;
    private JLabel lblValeur1;
    private JLabel lblValeur2;
    
    // Contre-exemple
    private JButton btnContreExemple;
    
    public Panneau3Formes() {
        setLayout(new BorderLayout());
        
        // ========== PANEL DE CONTRÔLE ==========
        JPanel panelControle = new JPanel();
        panelControle.setLayout(new BoxLayout(panelControle, BoxLayout.Y_AXIS));
        
        // Panneau du conteneur
        JPanel panelConteneur = new JPanel(new FlowLayout());
        panelConteneur.add(new JLabel("Largeur conteneur :"));
        champLargeurConteneur = new JTextField("500", 5);
        panelConteneur.add(champLargeurConteneur);
        panelConteneur.add(new JLabel("Hauteur conteneur :"));
        champHauteurConteneur = new JTextField("500", 5);
        panelConteneur.add(champHauteurConteneur);
        
        // Panneau de saisie des formes
        JPanel panelSaisieForme = new JPanel(new FlowLayout());
        
        comboType = new JComboBox<>(TypeForme.values());
        comboType.addActionListener(e -> mettreAJourChampsSaisie());
        panelSaisieForme.add(new JLabel("Type :"));
        panelSaisieForme.add(comboType);
        
        lblValeur1 = new JLabel("Largeur :");
        champValeur1 = new JTextField("50", 5);
        panelSaisieForme.add(lblValeur1);
        panelSaisieForme.add(champValeur1);
        
        lblValeur2 = new JLabel("Hauteur :");
        champValeur2 = new JTextField("50", 5);
        panelSaisieForme.add(lblValeur2);
        panelSaisieForme.add(champValeur2);
        
        JButton btnAjouter = new JButton("Ajouter Forme");
        JButton btnSupprimer = new JButton("Supprimer sélection");
        
        panelSaisieForme.add(btnAjouter);
        panelSaisieForme.add(btnSupprimer);
        
        // Panneau des algorithmes
        JPanel panelAlgorithmes = new JPanel(new FlowLayout());
        JButton btnHeuristique = new JButton("Heuristique BFDA");
        JButton btnForceBrute = new JButton("Force Brute");
        JButton btnEffacer = new JButton("Effacer tout");
        
        btnContreExemple = new JButton("Afficher Contre-Exemple");
        
        panelAlgorithmes.add(btnHeuristique);
        panelAlgorithmes.add(btnForceBrute);
        panelAlgorithmes.add(btnEffacer);
        panelAlgorithmes.add(btnContreExemple);
        
        panelControle.add(panelConteneur);
        panelControle.add(panelSaisieForme);
        panelControle.add(panelAlgorithmes);
        
        // ========== TABLE DES FORMES ==========
        modeleTable = new DefaultTableModel(new String[]{"Type", "Paramètre 1", "Paramètre 2", "Aire"}, 0);
        tableFormes = new JTable(modeleTable);
        JScrollPane defilementTable = new JScrollPane(tableFormes);
        defilementTable.setPreferredSize(new Dimension(300, 300));
        defilementTable.setBorder(BorderFactory.createTitledBorder("Formes à placer"));
        
        // ========== ZONE DE DESSIN ==========
        panneauDessin = new JPanel() {
            private static final long serialVersionUID = 1L;
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                dessinerConteneur(g);
            }
        };
        panneauDessin.setBackground(Color.WHITE);
        panneauDessin.setPreferredSize(new Dimension(550, 550));
        panneauDessin.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        
        // ========== ZONE DES RESULTATS ==========
        zoneResultats = new JTextArea(8, 30);
        zoneResultats.setEditable(false);
        zoneResultats.setFont(new Font("Monospaced", Font.PLAIN, 11));
        JScrollPane defilementResultats = new JScrollPane(zoneResultats);
        
        // ========== ORGANISATION ==========
        JPanel panelGauche = new JPanel(new BorderLayout());
        panelGauche.add(defilementTable, BorderLayout.CENTER);
        panelGauche.add(defilementResultats, BorderLayout.SOUTH);
        
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
                                               panelGauche, panneauDessin);
        splitPane.setDividerLocation(350);
        
        add(panelControle, BorderLayout.NORTH);
        add(splitPane, BorderLayout.CENTER);
        
        // ========== ÉCOUTEURS D'ÉVÉNEMENTS ==========
        btnAjouter.addActionListener(e -> ajouterForme());
        btnSupprimer.addActionListener(e -> {
            int ligne = tableFormes.getSelectedRow();
            if (ligne != -1) {
                modeleTable.removeRow(ligne);
            }
        });
        
        btnEffacer.addActionListener(e -> {
            modeleTable.setRowCount(0);
            placementCourant = null;
            panneauDessin.repaint();
            zoneResultats.setText("");
        });
        
        btnHeuristique.addActionListener(e -> lancerHeuristique());
        btnForceBrute.addActionListener(e -> lancerForceBrute());
        btnContreExemple.addActionListener(e -> afficherContreExemple());
        
        // Initialiser les champs
        mettreAJourChampsSaisie();
        
        // Ajouter des formes par défaut pour démonstration
        ajouterFormesParDefaut();
    }
    
    private void mettreAJourChampsSaisie() {
        TypeForme type = (TypeForme) comboType.getSelectedItem();
        if (type == TypeForme.RECTANGLE) {
            lblValeur1.setText("Largeur :");
            lblValeur2.setText("Hauteur :");
            champValeur1.setText("50");
            champValeur2.setText("50");
        } else if (type == TypeForme.CERCLE) {
            lblValeur1.setText("Rayon :");
            lblValeur2.setText("(ignoré)");
            champValeur1.setText("30");
            champValeur2.setText("0");
            champValeur2.setEnabled(false);
            return;
        } else if (type == TypeForme.TRIANGLE) {
            lblValeur1.setText("Base :");
            lblValeur2.setText("Hauteur :");
            champValeur1.setText("60");
            champValeur2.setText("50");
        }
        champValeur2.setEnabled(true);
    }
    
    private void ajouterForme() {
        try {
            TypeForme type = (TypeForme) comboType.getSelectedItem();
            double v1 = Double.parseDouble(champValeur1.getText());
            double v2 = Double.parseDouble(champValeur2.getText());
            
            if (v1 <= 0) throw new NumberFormatException();
            
            Forme forme = null;
            String param1 = null, param2 = null;
            double aire = 0;
            
            switch (type) {
                case RECTANGLE:
                    if (v2 <= 0) throw new NumberFormatException();
                    forme = new Rectangle(v1, v2);
                    param1 = String.format("%.1f", v1);
                    param2 = String.format("%.1f", v2);
                    aire = v1 * v2;
                    break;
                case CERCLE:
                    forme = new Cercle(v1);
                    param1 = String.format("r=%.1f", v1);
                    param2 = "-";
                    aire = Math.PI * v1 * v1;
                    break;
                case TRIANGLE:
                    if (v2 <= 0) throw new NumberFormatException();
                    forme = new Triangle(v1, v2);
                    param1 = String.format("b=%.1f", v1);
                    param2 = String.format("h=%.1f", v2);
                    aire = (v1 * v2) / 2;
                    break;
            }
            
            if (forme != null) {
                modeleTable.addRow(new Object[]{type.toString(), param1, param2, String.format("%.1f", aire)});
            }
            
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Veuillez entrer des dimensions valides (nombres positifs) !");
        }
    }
    
    private void ajouterFormesParDefaut() {
        // Ajouter quelques formes par défaut pour démonstration
        modeleTable.addRow(new Object[]{"RECTANGLE", "80", "60", "4800.0"});
        modeleTable.addRow(new Object[]{"CERCLE", "r=40", "-", "5026.5"});
        modeleTable.addRow(new Object[]{"TRIANGLE", "b=70", "h=50", "1750.0"});
        modeleTable.addRow(new Object[]{"RECTANGLE", "60", "40", "2400.0"});
    }
    
    /**
     * Récupère la liste des formes saisies
     */
    private List<Forme> getFormes() {
        List<Forme> formes = new ArrayList<>();
        for (int i = 0; i < modeleTable.getRowCount(); i++) {
            String type = (String) modeleTable.getValueAt(i, 0);
            String param1 = (String) modeleTable.getValueAt(i, 1);
            String param2 = (String) modeleTable.getValueAt(i, 2);
            
            try {
                if (type.equals("RECTANGLE")) {
                    double w = Double.parseDouble(param1);
                    double h = Double.parseDouble(param2);
                    formes.add(new Rectangle(w, h));
                } else if (type.equals("CERCLE")) {
                    double r = Double.parseDouble(param1.substring(2));
                    formes.add(new Cercle(r));
                } else if (type.equals("TRIANGLE")) {
                    double b = Double.parseDouble(param1.substring(2));
                    double h = Double.parseDouble(param2.substring(2));
                    formes.add(new Triangle(b, h));
                }
            } catch (Exception e) {
                // Ignorer
            }
        }
        return formes;
    }
    
    /**
     * Lance l'algorithme heuristique
     */
    private void lancerHeuristique() {
        try {
            double largeur = Double.parseDouble(champLargeurConteneur.getText());
            double hauteur = Double.parseDouble(champHauteurConteneur.getText());
            List<Forme> formes = getFormes();
            
            if (formes.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Veuillez ajouter des formes !");
                return;
            }
            
            long debut = System.nanoTime();
            placementCourant = Paquetage3Formes.heuristique(formes, largeur, hauteur);
            long fin = System.nanoTime();
            double tempsExecution = (fin - debut) / 1_000_000.0;
            
            afficherResultats("Heuristique BFDA", tempsExecution, largeur, hauteur);
            panneauDessin.repaint();
            
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Dimensions du conteneur invalides !");
        }
    }
    
    /**
     * Lance l'algorithme de force brute
     */
    private void lancerForceBrute() {
        try {
            double largeur = Double.parseDouble(champLargeurConteneur.getText());
            double hauteur = Double.parseDouble(champHauteurConteneur.getText());
            List<Forme> formes = getFormes();
            
            if (formes.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Veuillez ajouter des formes !");
                return;
            }
            
            if (formes.size() > 6) {
                int reponse = JOptionPane.showConfirmDialog(this,
                    "La force brute est très lente pour plus de 6 formes (≈ " + 
                    calculerComplexite(formes.size()) + " combinaisons). Continuer ?",
                    "Attention", JOptionPane.YES_NO_OPTION);
                if (reponse != JOptionPane.YES_OPTION) return;
            }
            
            long debut = System.nanoTime();
            placementCourant = Paquetage3Formes.forceBrute(formes, largeur, hauteur, 10.0);
            long fin = System.nanoTime();
            double tempsExecution = (fin - debut) / 1_000_000.0;
            
            afficherResultats("Force Brute", tempsExecution, largeur, hauteur);
            panneauDessin.repaint();
            
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Dimensions du conteneur invalides !");
        } catch (OutOfMemoryError e) {
            JOptionPane.showMessageDialog(this, "Trop de combinaisons ! Essayez avec moins de formes.");
        }
    }
    
    private long calculerComplexite(int n) {
        // Approximation: n! * 4^n
        long result = 1;
        for (int i = 2; i <= n; i++) result *= i;
        for (int i = 0; i < n; i++) result *= 4;
        return result;
    }
    
    /**
     * Affiche les résultats
     */
    private void afficherResultats(String algorithme, double temps, double largeur, double hauteur) {
        double aireTotale = 0;
        if (placementCourant != null) {
            aireTotale = placementCourant.stream()
                .mapToDouble(fp -> fp.forme.getAire()).sum();
        }
        double aireConteneur = largeur * hauteur;
        double utilisation = (aireTotale / aireConteneur) * 100;
        
        StringBuilder sb = new StringBuilder();
        sb.append("══════════════════════════════════════════════════════════════\n");
        sb.append("  ALGORITHME : ").append(algorithme).append("\n");
        sb.append("══════════════════════════════════════════════════════════════\n\n");
        sb.append(String.format("⏱️  Temps d'exécution : %.3f ms\n", temps));
        sb.append(String.format("📐 Formes placées : %d / %d\n", 
                 placementCourant != null ? placementCourant.size() : 0, 
                 modeleTable.getRowCount()));
        sb.append(String.format("📏 Aire placée : %.1f / %.1f\n", aireTotale, aireConteneur));
        sb.append(String.format("📊 Utilisation : %.1f%%\n\n", utilisation));
        
        if (placementCourant != null && !placementCourant.isEmpty()) {
            sb.append("Détail du placement :\n");
            for (int i = 0; i < placementCourant.size(); i++) {
                Paquetage3Formes.FormePlacee fp = placementCourant.get(i);
                sb.append(String.format("  %d. %s à (%.0f, %.0f) [%.0f×%.0f]\n",
                         i + 1, fp.forme.getType(), fp.x, fp.y,
                         fp.forme.getLargeur(), fp.forme.getHauteur()));
            }
        }
        
        zoneResultats.setText(sb.toString());
    }
    
    /**
     * Affiche un contre-exemple montrant que l'heuristique n'est pas optimale
     */
    private void afficherContreExemple() {
        StringBuilder sb = new StringBuilder();
        sb.append("══════════════════════════════════════════════════════════════\n");
        sb.append("  CONTRE-EXEMPLE : L'heuristique BFDA n'est PAS optimale\n");
        sb.append("══════════════════════════════════════════════════════════════\n\n");
        
        sb.append("📋 Données d'entrée :\n");
        sb.append("   • Conteneur : 100 × 100\n");
        sb.append("   • Formes :\n");
        sb.append("     - 1 cercle (r = 40)      → aire ≈ 5026.5\n");
        sb.append("     - 8 rectangles (8×1)    → aire = 8 chacun\n\n");
        
        sb.append("✨ SOLUTION OPTIMALE :\n");
        sb.append("   Placer les 8 rectangles en bas (8×8×1) et le cercle au-dessus\n");
        sb.append("   → Utilisation ≈ 98%\n\n");
        
        sb.append("❌ SOLUTION DE BFDA :\n");
        sb.append("   BFDA place d'abord le cercle (plus grande aire),\n");
        sb.append("   puis essaie de placer les rectangles dans l'espace restant.\n");
        sb.append("   Le cercle prend beaucoup de place et laisse des espaces\n");
        sb.append("   difficiles à utiliser pour les rectangles.\n\n");
        
        sb.append("💡 Explication :\n");
        sb.append("   L'heuristique BFDA trie par aire décroissante, ce qui n'est\n");
        sb.append("   pas toujours optimal. Parfois, placer des petites formes\n");
        sb.append("   d'abord permet un meilleur remplissage global.\n\n");
        
        sb.append("🔧 Solution :\n");
        sb.append("   Utiliser la force brute pour trouver la solution optimale,\n");
        sb.append("   ou implémenter une heuristique plus sophistiquée comme\n");
        sb.append("   l'algorithme génétique ou le recuit simulé.\n");
        
        zoneResultats.setText(sb.toString());
        
        // Charger le contre-exemple
        modeleTable.setRowCount(0);
        modeleTable.addRow(new Object[]{"CERCLE", "r=40", "-", "5026.5"});
        for (int i = 0; i < 8; i++) {
            modeleTable.addRow(new Object[]{"RECTANGLE", "8", "1", "8.0"});
        }
        champLargeurConteneur.setText("100");
        champHauteurConteneur.setText("100");
    }
    
    /**
     * Dessine le conteneur et les formes placées
     */
    private void dessinerConteneur(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        try {
            double largeur = Double.parseDouble(champLargeurConteneur.getText());
            double hauteur = Double.parseDouble(champHauteurConteneur.getText());
            
            // Calculer l'échelle
            double echelle = Math.min(
                panneauDessin.getWidth() / largeur,
                panneauDessin.getHeight() / hauteur
            ) * 0.9;
            
            int decalageX = (int)((panneauDessin.getWidth() - largeur * echelle) / 2);
            int decalageY = (int)((panneauDessin.getHeight() - hauteur * echelle) / 2);
            
            // Dessiner le conteneur
            g2d.setColor(Color.BLACK);
            g2d.drawRect(decalageX, decalageY,
                         (int)(largeur * echelle),
                         (int)(hauteur * echelle));
            
            // Dessiner les formes placées
            if (placementCourant != null && !placementCourant.isEmpty()) {
                Color[] couleurs = {
                    new Color(255, 100, 100, 180), new Color(100, 255, 100, 180),
                    new Color(100, 100, 255, 180), new Color(255, 255, 100, 180),
                    new Color(255, 100, 255, 180), new Color(100, 255, 255, 180),
                    new Color(255, 200, 150, 180), new Color(150, 200, 255, 180),
                    new Color(200, 150, 255, 180), new Color(255, 150, 200, 180)
                };
                int indexCouleur = 0;
                
                for (Paquetage3Formes.FormePlacee fp : placementCourant) {
                    int x = decalageX + (int)(fp.x * echelle);
                    int y = decalageY + (int)(fp.y * echelle);
                    
                    if (fp.forme instanceof Rectangle) {
                        int w = (int)(fp.forme.getLargeur() * echelle);
                        int h = (int)(fp.forme.getHauteur() * echelle);
                        if (w < 1) w = 1;
                        if (h < 1) h = 1;
                        g2d.setColor(couleurs[indexCouleur % couleurs.length]);
                        g2d.fillRect(x, y, w, h);
                        g2d.setColor(Color.BLACK);
                        g2d.drawRect(x, y, w, h);
                        g2d.setFont(new Font("Arial", Font.PLAIN, 9));
                        g2d.drawString(String.format("%.0fx%.0f", 
                            fp.forme.getLargeur(), fp.forme.getHauteur()), x + 2, y + 12);
                    } else if (fp.forme instanceof Cercle) {
                        int diametre = (int)(fp.forme.getLargeur() * echelle);
                        if (diametre < 1) diametre = 1;
                        g2d.setColor(couleurs[indexCouleur % couleurs.length]);
                        g2d.fillOval(x, y, diametre, diametre);
                        g2d.setColor(Color.BLACK);
                        g2d.drawOval(x, y, diametre, diametre);
                        g2d.setFont(new Font("Arial", Font.PLAIN, 9));
                        g2d.drawString(String.format("r=%.0f", 
                            ((Cercle)fp.forme).getRayon()), x + 2, y + 12);
                    } else if (fp.forme instanceof Triangle) {
                        int w = (int)(fp.forme.getLargeur() * echelle);
                        int h = (int)(fp.forme.getHauteur() * echelle);
                        if (w < 1) w = 1;
                        if (h < 1) h = 1;
                        
                        Triangle t = (Triangle) fp.forme;
                        int[] pointsX, pointsY;
                        
                        if (t.getRotation() == 0) {
                            pointsX = new int[]{x, x + w/2, x + w};
                            pointsY = new int[]{y + h, y, y + h};
                        } else if (t.getRotation() == 90) {
                            pointsX = new int[]{x, x + h, x};
                            pointsY = new int[]{y, y + w/2, y + w};
                        } else {
                            pointsX = new int[]{x, x + w/2, x + w};
                            pointsY = new int[]{y, y + h, y};
                        }
                        
                        g2d.setColor(couleurs[indexCouleur % couleurs.length]);
                        g2d.fillPolygon(pointsX, pointsY, 3);
                        g2d.setColor(Color.BLACK);
                        g2d.drawPolygon(pointsX, pointsY, 3);
                        g2d.setFont(new Font("Arial", Font.PLAIN, 8));
                        g2d.drawString("Δ", x + w/2 - 3, y + h/2);
                    }
                    
                    indexCouleur++;
                }
            }
            
            // Message si aucun placement
            if (placementCourant == null || placementCourant.isEmpty()) {
                g2d.setColor(Color.GRAY);
                g2d.setFont(new Font("Arial", Font.ITALIC, 14));
                String msg = "Lancez un algorithme pour voir le résultat";
                FontMetrics fm = g2d.getFontMetrics();
                int msgX = (panneauDessin.getWidth() - fm.stringWidth(msg)) / 2;
                int msgY = panneauDessin.getHeight() / 2;
                g2d.drawString(msg, msgX, msgY);
            }
            
        } catch (NumberFormatException e) {
            g2d.drawString("Dimensions invalides", 10, 20);
        }
    }
}