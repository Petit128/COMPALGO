package gui;

import javax.swing.*;
import java.awt.*;

/**
 * Fenêtre principale de l'application 2D Packing.
 * Contient trois onglets correspondant aux trois parties du projet.
 */
public class FenetrePrincipale extends JFrame {
    private static final long serialVersionUID = 1L;

    public FenetrePrincipale() {
        setTitle("Projet 2D Packing — Algorithmes de Placement");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1400, 900);
        setMinimumSize(new Dimension(1000, 700));
        setLocationRelativeTo(null);

        // En-tête
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(new Color(30, 50, 90));
        header.setBorder(BorderFactory.createEmptyBorder(8, 14, 8, 14));
        JLabel titre = new JLabel("2D Packing — Algorithmes de Placement", SwingConstants.LEFT);
        titre.setForeground(Color.WHITE);
        titre.setFont(new Font("SansSerif", Font.BOLD, 17));


        // Onglets
        JTabbedPane onglets = new JTabbedPane();
        onglets.setFont(new Font("SansSerif", Font.BOLD, 13));
        onglets.addTab("① 1D — Bin Packing",        new Panneau1D());
        onglets.addTab("② 2D — Rectangles",          new Panneau2D());
        onglets.addTab("③ 3 Formes + Rotations",     new Panneau3Formes());

        add(header, BorderLayout.NORTH);
        add(onglets, BorderLayout.CENTER);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ignored) {}
            new FenetrePrincipale().setVisible(true);
        });
    }
}