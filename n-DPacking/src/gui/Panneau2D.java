package gui;

import algorithms.Paquetage2D;
import models.Rectangle;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Panneau pour le paquetage 2D de rectangles
 * Permet de saisir des rectangles, choisir un algorithme, et visualiser graphiquement le résultat
 */
public class Panneau2D extends JPanel {
    private static final long serialVersionUID = 1L;
    
    // Composants
    private JTextField champLargeurConteneur;
    private JTextField champHauteurConteneur;
    private JTable tableRectangles;
    private DefaultTableModel modeleTable;
    private JPanel panneauDessin;
    private JTextArea zoneResultats;
    private List<Paquetage2D.RectanglePlace> placementCourant;
    private JCheckBox chkRotation;
    
    public Panneau2D() {
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
        
        // Panneau de saisie des rectangles
        JPanel panelSaisieRect = new JPanel(new FlowLayout());
        JLabel lblLargeur = new JLabel("Largeur:");
        JTextField champLargeurRect = new JTextField("50", 5);
        JLabel lblHauteur = new JLabel("Hauteur:");
        JTextField champHauteurRect = new JTextField("50", 5);
        JButton btnAjouter = new JButton("Ajouter Rectangle");
        JButton btnSupprimer = new JButton("Supprimer sélection");
        
        panelSaisieRect.add(lblLargeur);
        panelSaisieRect.add(champLargeurRect);
        panelSaisieRect.add(lblHauteur);
        panelSaisieRect.add(champHauteurRect);
        panelSaisieRect.add(btnAjouter);
        panelSaisieRect.add(btnSupprimer);
        
        // Panneau des algorithmes
        JPanel panelAlgorithmes = new JPanel(new FlowLayout());
        JButton btnNFDH = new JButton("NFDH (Next-Fit)");
        JButton btnFFDH = new JButton("FFDH (First-Fit)");
        JButton btnBestFit = new JButton("Best-Fit 2D");
        JButton btnForceBrute = new JButton("Force Brute");
        JButton btnEffacer = new JButton("Effacer tout");
        JButton btnContreExemple = new JButton("Contre-Exemples 2D");
        
        chkRotation = new JCheckBox("Autoriser les rotations (90°)");
        chkRotation.setSelected(true);
        
        panelAlgorithmes.add(btnNFDH);
        panelAlgorithmes.add(btnFFDH);
        panelAlgorithmes.add(btnBestFit);
        panelAlgorithmes.add(btnForceBrute);
        panelAlgorithmes.add(btnEffacer);
        panelAlgorithmes.add(chkRotation);
        panelAlgorithmes.add(btnContreExemple);
        
        panelControle.add(panelConteneur);
        panelControle.add(panelSaisieRect);
        panelControle.add(panelAlgorithmes);
        
        // ========== TABLE DES RECTANGLES ==========
        modeleTable = new DefaultTableModel(new String[]{"Largeur", "Hauteur", "Aire"}, 0);
        tableRectangles = new JTable(modeleTable);
        JScrollPane defilementTable = new JScrollPane(tableRectangles);
        defilementTable.setPreferredSize(new Dimension(250, 300));
        defilementTable.setBorder(BorderFactory.createTitledBorder("Rectangles à placer"));
        
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
        splitPane.setDividerLocation(300);
        
        add(panelControle, BorderLayout.NORTH);
        add(splitPane, BorderLayout.CENTER);
        
        // ========== ÉCOUTEURS D'ÉVÉNEMENTS ==========
        btnAjouter.addActionListener(e -> {
            try {
                double w = Double.parseDouble(champLargeurRect.getText());
                double h = Double.parseDouble(champHauteurRect.getText());
                if (w > 0 && h > 0) {
                    modeleTable.addRow(new Object[]{w, h, String.format("%.1f", w * h)});
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Dimensions invalides !");
            }
        });
        
        btnSupprimer.addActionListener(e -> {
            int ligne = tableRectangles.getSelectedRow();
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
        
        btnNFDH.addActionListener(e -> lancerAlgorithme("NFDH"));
        btnFFDH.addActionListener(e -> lancerAlgorithme("FFDH"));
        btnBestFit.addActionListener(e -> lancerAlgorithme("BESTFIT"));
        btnForceBrute.addActionListener(e -> lancerAlgorithme("BRUTE"));
        btnContreExemple.addActionListener(e -> afficherContreExemples());
        
        // Ajouter des rectangles par défaut
        ajouterRectanglesParDefaut();
    }
    
    private void ajouterRectanglesParDefaut() {
        modeleTable.addRow(new Object[]{80, 60, "4800.0"});
        modeleTable.addRow(new Object[]{60, 40, "2400.0"});
        modeleTable.addRow(new Object[]{50, 50, "2500.0"});
        modeleTable.addRow(new Object[]{30, 80, "2400.0"});
    }
    
    private List<Rectangle> getRectangles() {
        List<Rectangle> rects = new ArrayList<>();
        for (int i = 0; i < modeleTable.getRowCount(); i++) {
            double w = (double) modeleTable.getValueAt(i, 0);
            double h = (double) modeleTable.getValueAt(i, 1);
            rects.add(new Rectangle(w, h));
        }
        return rects;
    }
    
    private void lancerAlgorithme(String algorithme) {
        try {
            double largeur = Double.parseDouble(champLargeurConteneur.getText());
            double hauteur = Double.parseDouble(champHauteurConteneur.getText());
            List<Rectangle> rects = getRectangles();
            
            if (rects.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Veuillez ajouter des rectangles !");
                return;
            }
            
            long debut = System.nanoTime();
            boolean avecRotation = chkRotation.isSelected();
            
            switch (algorithme) {
                case "NFDH":
                    placementCourant = Paquetage2D.nextFitHauteurDecroissante(rects, largeur, hauteur);
                    break;
                case "FFDH":
                    placementCourant = Paquetage2D.premierFitHauteurDecroissante(rects, largeur, hauteur);
                    break;
                case "BESTFIT":
                    placementCourant = Paquetage2D.meilleurFit2D(rects, largeur, hauteur);
                    break;
                case "BRUTE":
                    if (rects.size() > 6) {
                        int reponse = JOptionPane.showConfirmDialog(this,
                            "La force brute est très lente pour plus de 6 rectangles. Continuer ?",
                            "Attention", JOptionPane.YES_NO_OPTION);
                        if (reponse != JOptionPane.YES_OPTION) return;
                    }
                    placementCourant = Paquetage2D.forceBruteAvecRotation(rects, largeur, hauteur, avecRotation);
                    break;
            }
            
            long fin = System.nanoTime();
            double tempsExecution = (fin - debut) / 1_000_000.0;
            
            double aireTotale = 0;
            if (placementCourant != null) {
                aireTotale = placementCourant.stream()
                    .mapToDouble(rp -> rp.rectangle.getAire()).sum();
            }
            double aireConteneur = largeur * hauteur;
            double utilisation = (aireTotale / aireConteneur) * 100;
            
            StringBuilder sb = new StringBuilder();
            sb.append("═══════════════════════════════════════════\n");
            sb.append("  ALGORITHME : ").append(algorithme).append("\n");
            sb.append("  Rotation : ").append(avecRotation ? "autorisée" : "interdite").append("\n");
            sb.append("═══════════════════════════════════════════\n\n");
            sb.append(String.format("⏱️  Temps : %.3f ms\n", tempsExecution));
            sb.append(String.format("📐 Rectangles placés : %d / %d\n", 
                     placementCourant != null ? placementCourant.size() : 0, rects.size()));
            sb.append(String.format("📏 Aire placée : %.1f / %.1f\n", aireTotale, aireConteneur));
            sb.append(String.format("📊 Utilisation : %.1f%%\n", utilisation));
            
            zoneResultats.setText(sb.toString());
            panneauDessin.repaint();
            
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Dimensions du conteneur invalides !");
        }
    }
    
    private void afficherContreExemples() {
        // Créer un dialogue avec des onglets pour chaque contre-exemple
        JDialog dialog = new JDialog();
        dialog.setTitle("Contre-Exemples pour les algorithmes 2D");
        dialog.setSize(700, 500);
        dialog.setModal(true);
        dialog.setLocationRelativeTo(this);
        
        JTabbedPane onglets = new JTabbedPane();
        
        // Contre-exemple NFDH
        Paquetage2D.ContreExemple2D ceNFDH = Paquetage2D.getContreExempleNFDH();
        onglets.addTab("NFDH", creerPanneauContreExemple(ceNFDH));
        
        // Contre-exemple FFDH
        Paquetage2D.ContreExemple2D ceFFDH = Paquetage2D.getContreExempleFFDH();
        onglets.addTab("FFDH", creerPanneauContreExemple(ceFFDH));
        
        // Contre-exemple Best-Fit
        Paquetage2D.ContreExemple2D ceBestFit = Paquetage2D.getContreExempleBestFit();
        onglets.addTab("Best-Fit", creerPanneauContreExemple(ceBestFit));
        
        // Bouton pour charger un contre-exemple
        JPanel panelBas = new JPanel();
        JButton btnCharger = new JButton("Charger ce contre-exemple");
        JButton btnFermer = new JButton("Fermer");
        panelBas.add(btnCharger);
        panelBas.add(btnFermer);
        
        dialog.add(onglets, BorderLayout.CENTER);
        dialog.add(panelBas, BorderLayout.SOUTH);
        
        btnFermer.addActionListener(e -> dialog.dispose());
        btnCharger.addActionListener(e -> {
            int idx = onglets.getSelectedIndex();
            Paquetage2D.ContreExemple2D ce = null;
            if (idx == 0) ce = ceNFDH;
            else if (idx == 1) ce = ceFFDH;
            else ce = ceBestFit;
            
            if (ce != null) {
                chargerContreExemple(ce);
                dialog.dispose();
            }
        });
        
        dialog.setVisible(true);
    }
    
    private JPanel creerPanneauContreExemple(Paquetage2D.ContreExemple2D ce) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Zone de texte pour l'explication
        JTextArea texte = new JTextArea();
        texte.setEditable(false);
        texte.setFont(new Font("Monospaced", Font.PLAIN, 12));
        texte.setLineWrap(true);
        texte.setWrapStyleWord(true);
        
        StringBuilder sb = new StringBuilder();
        sb.append("══════════════════════════════════════════════════════════════\n");
        sb.append("  CONTRE-EXEMPLE POUR ").append(ce.nomAlgo).append("\n");
        sb.append("══════════════════════════════════════════════════════════════\n\n");
        
        sb.append("📋 DONNÉES D'ENTRÉE :\n");
        sb.append(String.format("   • Conteneur : %.0f × %.0f\n", ce.largeurConteneur, ce.hauteurConteneur));
        sb.append("   • Rectangles :\n");
        
        double aireTotale = 0;
        for (int i = 0; i < ce.rectangles.size(); i++) {
            Rectangle r = ce.rectangles.get(i);
            aireTotale += r.getAire();
            sb.append(String.format("     %d) %.0f × %.0f (aire = %.1f)\n", 
                      i+1, r.getLargeurOriginale(), r.getHauteurOriginale(), r.getAire()));
        }
        sb.append(String.format("\n   • Aire totale : %.1f\n", aireTotale));
        sb.append(String.format("   • Aire conteneur : %.0f\n", ce.largeurConteneur * ce.hauteurConteneur));
        sb.append(String.format("   • Nombre minimum de bacs théorique : %.0f\n", Math.ceil(aireTotale / (ce.largeurConteneur * ce.hauteurConteneur))));
        
        sb.append("\n💡 EXPLICATION :\n");
        sb.append(ce.explication);
        
        sb.append("\n\n🔧 POUR OBTENIR LA SOLUTION OPTIMALE :\n");
        sb.append("   Utilisez la Force Brute (mais attention, elle est très lente\n");
        sb.append("   pour beaucoup de rectangles). Pour ce cas précis, la solution\n");
        sb.append("   optimale utilise moins de conteneurs/bacs.\n");
        
        texte.setText(sb.toString());
        
        JScrollPane scroll = new JScrollPane(texte);
        panel.add(scroll, BorderLayout.CENTER);
        
        return panel;
    }
    
    private void chargerContreExemple(Paquetage2D.ContreExemple2D ce) {
        modeleTable.setRowCount(0);
        champLargeurConteneur.setText(String.valueOf((int)ce.largeurConteneur));
        champHauteurConteneur.setText(String.valueOf((int)ce.hauteurConteneur));
        
        for (Rectangle r : ce.rectangles) {
            modeleTable.addRow(new Object[]{
                r.getLargeurOriginale(), 
                r.getHauteurOriginale(), 
                String.format("%.1f", r.getAire())
            });
        }
        
        zoneResultats.setText("Contre-exemple chargé ! Lancez l'algorithme " + ce.nomAlgo + 
                              " pour voir son comportement non-optimal.");
        placementCourant = null;
        panneauDessin.repaint();
    }
    
    private void dessinerConteneur(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        
        try {
            double largeur = Double.parseDouble(champLargeurConteneur.getText());
            double hauteur = Double.parseDouble(champHauteurConteneur.getText());
            
            double echelle = Math.min(
                panneauDessin.getWidth() / largeur,
                panneauDessin.getHeight() / hauteur
            ) * 0.9;
            
            int decalageX = (int)((panneauDessin.getWidth() - largeur * echelle) / 2);
            int decalageY = (int)((panneauDessin.getHeight() - hauteur * echelle) / 2);
            
            g2d.setColor(Color.BLACK);
            g2d.drawRect(decalageX, decalageY,
                         (int)(largeur * echelle),
                         (int)(hauteur * echelle));
            
            if (placementCourant != null && !placementCourant.isEmpty()) {
                Color[] couleurs = {
                    new Color(255, 100, 100), new Color(100, 255, 100),
                    new Color(100, 100, 255), new Color(255, 255, 100),
                    new Color(255, 100, 255), new Color(100, 255, 255),
                    new Color(255, 200, 150), new Color(150, 200, 255)
                };
                int indexCouleur = 0;
                
                for (Paquetage2D.RectanglePlace rp : placementCourant) {
                    int x = decalageX + (int)(rp.x * echelle);
                    int y = decalageY + (int)(rp.y * echelle);
                    int w = (int)(rp.rectangle.getLargeur() * echelle);
                    int h = (int)(rp.rectangle.getHauteur() * echelle);
                    
                    if (w < 1) w = 1;
                    if (h < 1) h = 1;
                    
                    g2d.setColor(couleurs[indexCouleur % couleurs.length]);
                    g2d.fillRect(x, y, w, h);
                    g2d.setColor(Color.BLACK);
                    g2d.drawRect(x, y, w, h);
                    
                    g2d.setFont(new Font("Arial", Font.PLAIN, 9));
                    String dims = String.format("%.0fx%.0f",
                        rp.rectangle.getLargeur(), rp.rectangle.getHauteur());
                    g2d.drawString(dims, x + 2, y + 12);
                    
                    indexCouleur++;
                }
            }
            
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