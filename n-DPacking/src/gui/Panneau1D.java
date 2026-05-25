package gui;

import algorithms.Paquetage1D;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

/**
 * Panneau pour le paquetage 1D (bin packing)
 * Permet de saisir des objets, choisir un algorithme, et visualiser les résultats
 */
public class Panneau1D extends JPanel {
    private static final long serialVersionUID = 1L;
    
    // Composants de l'interface
    private JTextField champCapacite;      // Capacité des bacs
    private JTextField champObjets;         // Objets (séparés par des virgules)
    private JTextArea zoneResultats;        // Affichage des résultats
    private JTable tableObjets;             // Tableau des objets
    private DefaultTableModel modeleTable;  // Modèle de la table
    
    public Panneau1D() {
        setLayout(new BorderLayout());
        
        // ========== PANEL DE SAISIE ==========
        JPanel panelSaisie = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        
        // Capacité des bacs
        gbc.gridx = 0; gbc.gridy = 0;
        panelSaisie.add(new JLabel("Capacité du bac :"), gbc);
        gbc.gridx = 1;
        champCapacite = new JTextField("1.0", 10);
        panelSaisie.add(champCapacite, gbc);
        
        // Objets à placer
        gbc.gridx = 0; gbc.gridy = 1;
        panelSaisie.add(new JLabel("Objets (séparés par des virgules) :"), gbc);
        gbc.gridx = 1;
        champObjets = new JTextField("0.6,0.6,0.6,0.6,0.4,0.4,0.4", 20);
        panelSaisie.add(champObjets, gbc);
        
        // ========== PANEL DES BOUTONS ==========
        JPanel panelBoutons = new JPanel(new FlowLayout());
        
        JButton btnPremierAdapte = new JButton("First-Fit (Premier Adapté)");
        JButton btnMeilleurAdapte = new JButton("Best-Fit (Meilleur Adapté)");
        JButton btnPireAdapte = new JButton("Worst-Fit (Pire Adapté)");
        JButton btnForceBrute = new JButton("Force Brute (Optimal)");
        JButton btnContreExemple = new JButton("Afficher Contre-Exemple");
        
        // Ajouter les écouteurs d'événements
        btnPremierAdapte.addActionListener(e -> lancerAlgorithme("FF"));
        btnMeilleurAdapte.addActionListener(e -> lancerAlgorithme("BF"));
        btnPireAdapte.addActionListener(e -> lancerAlgorithme("WF"));
        btnForceBrute.addActionListener(e -> lancerAlgorithme("BRUTE"));
        btnContreExemple.addActionListener(e -> afficherContreExemple());
        
        panelBoutons.add(btnPremierAdapte);
        panelBoutons.add(btnMeilleurAdapte);
        panelBoutons.add(btnPireAdapte);
        panelBoutons.add(btnForceBrute);
        panelBoutons.add(btnContreExemple);
        
        // ========== TABLE DES OBJETS ==========
        modeleTable = new DefaultTableModel(new String[]{"N°", "Taille"}, 0);
        tableObjets = new JTable(modeleTable);
        JScrollPane defilementTable = new JScrollPane(tableObjets);
        defilementTable.setPreferredSize(new Dimension(250, 300));
        defilementTable.setBorder(BorderFactory.createTitledBorder("Objets à placer"));
        
        // ========== ZONE DES RESULTATS ==========
        zoneResultats = new JTextArea();
        zoneResultats.setEditable(false);
        zoneResultats.setFont(new Font("Monospaced", Font.PLAIN, 12));
        JScrollPane defilementResultats = new JScrollPane(zoneResultats);
        defilementResultats.setBorder(BorderFactory.createTitledBorder("Résultats"));
        
        // ========== ORGANISATION DU PANNEAU ==========
        JPanel panelHaut = new JPanel(new BorderLayout());
        panelHaut.add(panelSaisie, BorderLayout.NORTH);
        panelHaut.add(panelBoutons, BorderLayout.CENTER);
        
        add(panelHaut, BorderLayout.NORTH);
        
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
                                               defilementTable, defilementResultats);
        splitPane.setDividerLocation(300);
        add(splitPane, BorderLayout.CENTER);
        
        // Charger les objets par défaut
        chargerObjets();
        champObjets.addActionListener(e -> chargerObjets());
    }
    
    /**
     * Charge les objets depuis le champ de texte vers la table
     */
    private void chargerObjets() {
        modeleTable.setRowCount(0);
        String[] objetsStr = champObjets.getText().split(",");
        for (int i = 0; i < objetsStr.length; i++) {
            try {
                double taille = Double.parseDouble(objetsStr[i].trim());
                modeleTable.addRow(new Object[]{i + 1, taille});
            } catch (NumberFormatException e) {
                // Ignorer les valeurs invalides
            }
        }
    }
    
    /**
     * Lance l'algorithme sélectionné
     * @param algorithme "FF", "BF", "WF" ou "BRUTE"
     */
    private void lancerAlgorithme(String algorithme) {
        chargerObjets();
        
        double capacite;
        try {
            capacite = Double.parseDouble(champCapacite.getText());
        } catch (NumberFormatException e) {
            zoneResultats.setText("Erreur : Capacité invalide !");
            return;
        }
        
        // Récupérer les objets
        double[] objets = new double[modeleTable.getRowCount()];
        for (int i = 0; i < objets.length; i++) {
            objets[i] = (double) modeleTable.getValueAt(i, 1);
        }
        
        if (objets.length == 0) {
            zoneResultats.setText("Erreur : Aucun objet à placer !");
            return;
        }
        
        // Exécuter l'algorithme
        List<List<Double>> resultat;
        long debut = System.nanoTime();
        
        switch (algorithme) {
            case "FF":
                resultat = Paquetage1D.premierAdapte(objets, capacite);
                break;
            case "BF":
                resultat = Paquetage1D.meilleurAdapte(objets, capacite);
                break;
            case "WF":
                resultat = Paquetage1D.pireAdapte(objets, capacite);
                break;
            case "BRUTE":
                if (objets.length > 12) {
                    int reponse = JOptionPane.showConfirmDialog(this,
                        "La force brute est très lente pour plus de 12 objets. Continuer ?",
                        "Attention", JOptionPane.YES_NO_OPTION);
                    if (reponse != JOptionPane.YES_OPTION) return;
                }
                resultat = Paquetage1D.forceBrute(objets, capacite);
                break;
            default:
                return;
        }
        
        long fin = System.nanoTime();
        double tempsExecution = (fin - debut) / 1_000_000.0;
        
        afficherResultats(resultat, algorithme, tempsExecution);
    }
    
    /**
     * Affiche les résultats dans la zone de texte
     */
    private void afficherResultats(List<List<Double>> bacs, String algorithme, double temps) {
        StringBuilder sb = new StringBuilder();
        
        // En-tête
        String nomAlgo = "";
        switch (algorithme) {
            case "FF": nomAlgo = "First-Fit (Premier Adapté)"; break;
            case "BF": nomAlgo = "Best-Fit (Meilleur Adapté)"; break;
            case "WF": nomAlgo = "Worst-Fit (Pire Adapté)"; break;
            case "BRUTE": nomAlgo = "Force Brute (Solution Optimale)"; break;
        }
        
        sb.append("══════════════════════════════════════════════════════════════\n");
        sb.append("  ALGORITHME : ").append(nomAlgo).append("\n");
        sb.append("══════════════════════════════════════════════════════════════\n\n");
        sb.append(String.format("⏱️  Temps d'exécution : %.3f ms\n", temps));
        sb.append(String.format("📦 Nombre de bacs utilisés : %d\n\n", bacs.size()));
        
        // Détail des bacs
        for (int i = 0; i < bacs.size(); i++) {
            List<Double> bac = bacs.get(i);
            double somme = bac.stream().mapToDouble(Double::doubleValue).sum();
            sb.append(String.format("Bac %d : ", i + 1));
            for (int j = 0; j < bac.size(); j++) {
                sb.append(bac.get(j));
                if (j < bac.size() - 1) sb.append(" + ");
            }
            sb.append(String.format(" = %.2f / %.2f (remplissage: %.1f%%)\n", 
                     somme, Double.parseDouble(champCapacite.getText()),
                     (somme / Double.parseDouble(champCapacite.getText())) * 100));
        }
        
        zoneResultats.setText(sb.toString());
    }
    
    /**
     * Affiche un contre-exemple montrant que First-Fit n'est pas optimal
     */
    private void afficherContreExemple() {
        StringBuilder sb = new StringBuilder();
        sb.append("══════════════════════════════════════════════════════════════\n");
        sb.append("  CONTRE-EXEMPLE : First-Fit n'est PAS optimal\n");
        sb.append("══════════════════════════════════════════════════════════════\n\n");
        
        sb.append("📋 Données d'entrée :\n");
        sb.append("   • Capacité du bac : 1.0\n");
        sb.append("   • Objets : 0.6, 0.6, 0.6, 0.6, 0.4, 0.4, 0.4\n\n");
        
        sb.append("✨ SOLUTION OPTIMALE (3 bacs) :\n");
        sb.append("   Bac 1 : 0.6 + 0.4 = 1.0\n");
        sb.append("   Bac 2 : 0.6 + 0.4 = 1.0\n");
        sb.append("   Bac 3 : 0.6 + 0.4 = 1.0\n\n");
        
        sb.append("❌ SOLUTION DE FIRST-FIT (4 bacs) :\n");
        sb.append("   Bac 1 : 0.6 + 0.4 = 1.0\n");
        sb.append("   Bac 2 : 0.6 + 0.4 = 1.0\n");
        sb.append("   Bac 3 : 0.6        = 0.6\n");
        sb.append("   Bac 4 : 0.4 + 0.4 = 0.8\n\n");
        
        sb.append("💡 Explication :\n");
        sb.append("   First-Fit place chaque objet dans le premier bac disponible.\n");
        sb.append("   Avec l'ordre donné, le troisième bac reçoit un 0.6, forçant\n");
        sb.append("   les deux 0.4 restants à utiliser un bac supplémentaire.\n");
        
        zoneResultats.setText(sb.toString());
        
        // Mettre à jour les champs avec le contre-exemple
        champObjets.setText("0.6,0.6,0.6,0.6,0.4,0.4,0.4");
        champCapacite.setText("1.0");
        chargerObjets();
    }
}