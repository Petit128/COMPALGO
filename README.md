# COMPALGO
complexiter algorithmique


# 📊 PROJET 2D PACKING - RÉCAPITULATIF COMPLET

## 1. RÉSUMÉ DU PROJET

Ce projet implémente des algorithmes de **paquetage (packing)** en 1D et 2D pour résoudre des problèmes d'optimisation de placement d'objets dans des conteneurs.

### Objectifs principaux :
- Minimiser le nombre de bacs utilisés (1D)
- Maximiser l'utilisation de l'espace d'un conteneur (2D)
- Gérer trois types de formes géométriques avec rotations

---

## 2. STRUCTURE DU PROJET

```
📂 src/
│
├── 📜 Main.java                           # Point d'entrée
│
├── 📂 algorithms/                         # Algorithmes de packing
│   ├── 📜 Paquetage1D.java               # Bin packing 1D (FF, BF, WF, BruteForce)
│   ├── 📜 Paquetage2D.java               # Packing rectangles (NFDH, FFDH, BestFit, BruteForce)
│   └── 📜 Paquetage3Formes.java          # Packing 3 formes (BFDA heuristique + BruteForce)
│
├── 📂 gui/                                # Interface graphique
│   ├── 📜 FenetrePrincipale.java         # Fenêtre principale (onglets)
│   ├── 📜 Panneau1D.java                 # Onglet 1D
│   ├── 📜 Panneau2D.java                 # Onglet 2D rectangles
│   └── 📜 Panneau3Formes.java            # Onglet 3 formes (cercle, rectangle, triangle)
│
└── 📂 models/                             # Modèles de formes géométriques
    ├── 📜 Forme.java                      # Interface commune
    ├── 📜 Rectangle.java                  # Rectangle (avec rotation)
    ├── 📜 Cercle.java                     # Cercle (invariant par rotation)
    └── 📜 Triangle.java                   # Triangle isocèle (rotations 0°,90°,180°,270°)
```

---

## 3. ÉTAT DÉTAILLÉ PAR PARTIE

### 🟢 PARTIE 1 : 1D PACKING - COMPLÈTE

| Algorithme | Implémenté | Complexité | Contre-exemple |
|------------|------------|------------|----------------|
| First-Fit (FF) | ✅ `premierAdapte()` | O(n×m) | ✅ |
| Best-Fit (BF) | ✅ `meilleurAdapte()` | O(n×m) | ✅ (via FF) |
| Worst-Fit (WF) | ✅ `pireAdapte()` | O(n×m) | ✅ (via FF) |
| Force Brute | ✅ `forceBrute()` | O(n! × 2^n) | - |

**Interface** : `Panneau1D.java` - saisie CSV, tableaux, résultats textuels

---

### 🟢 PARTIE 2 : 2D RECTANGLES - COMPLÈTE

| Algorithme | Implémenté | Complexité | Contre-exemple |
|------------|------------|------------|----------------|
| NFDH (Next-Fit Decreasing Height) | ✅ `nextFitHauteurDecroissante()` | O(n log n) | ✅ |
| FFDH (First-Fit Decreasing Height) | ✅ `premierFitHauteurDecroissante()` | O(n²) | ✅ |
| Best-Fit 2D | ✅ `meilleurFit2D()` | O(n²) | ✅ |
| Force Brute | ✅ `forceBruteAvecRotation()` | O(n! × (W×H)ⁿ) | - |

**Interface** : `Panneau2D.java` - saisie tableau, dessin des rectangles, rotations autorisées

---

### 🟢 PARTIE 3 : 3 FORMES + ROTATIONS - COMPLÈTE

| Algorithme | Implémenté | Complexité | Contre-exemple |
|------------|------------|------------|----------------|
| Heuristique BFDA | ✅ `heuristique()` | O(n²) | ✅ (cercle + rectangles) |
| Force Brute | ✅ `forceBrute()` | O(n! × 4ⁿ × (W×H)ⁿ) | - |

**Formes gérées** :
- ✅ **Rectangle** (rotations 0°, 90°)
- ✅ **Cercle** (invariant)
- ✅ **Triangle isocèle** (rotations 0°, 90°, 180°, 270°)

**Interface** : `Panneau3Formes.java` - sélection du type, saisie adaptée, dessin des 3 formes

---

## 4. FICHIERS ET LEUR STATUT

| Fichier | Lignes | Statut | Rôle |
|---------|--------|--------|------|
| `Main.java` | 10 | ✅ OK | Point d'entrée |
| `Paquetage1D.java` | 180 | ✅ OK | Algorithmes 1D |
| `Paquetage2D.java` | 380 | ✅ OK | Algorithmes 2D + contre-exemples |
| `Paquetage3Formes.java` | 280 | ✅ OK | Algorithmes 3 formes |
| `FenetrePrincipale.java` | 45 | ✅ OK | Fenêtre avec onglets |
| `Panneau1D.java` | 200 | ✅ OK | Interface 1D |
| `Panneau2D.java` | 320 | ✅ OK | Interface 2D + contre-exemples |
| `Panneau3Formes.java` | 380 | ✅ OK | Interface 3 formes |
| `Forme.java` | 35 | ✅ OK | Interface |
| `Rectangle.java` | 85 | ✅ OK | Modèle rectangle |
| `Cercle.java` | 60 | ✅ OK | Modèle cercle |
| `Triangle.java` | 95 | ✅ OK | Modèle triangle |

**Total : environ 2 070 lignes de code**

---

## 5. FONCTIONNALITÉS IMPLÉMENTÉES

### ✅ Interface graphique
- Fenêtre principale avec 3 onglets
- Saisie interactive des données
- Tableaux dynamiques
- Zone de dessin pour visualisation 2D
- Résultats textuels détaillés

### ✅ Algorithmes
- 3 algorithmes pour le 1D (FF, BF, WF)
- 3 algorithmes pour le 2D rectangles (NFDH, FFDH, BestFit)
- 1 heuristique pour 3 formes (BFDA)
- Force brute pour les 3 parties

### ✅ Contre-exemples
- 1 pour le 1D (First-Fit non optimal)
- 3 pour le 2D (NFDH, FFDH, BestFit)
- 1 pour le 3 formes (BFDA)

### ✅ Rotations
- π/2 (90°) et π (180°) pour rectangles et triangles
- Gérée via `pivoter90()` et `pivoter180()`

### ✅ Visualisation graphique
- Dessin du conteneur à l'échelle
- Couleurs distinctes par objet
- Affichage des dimensions

---

## 6. COMPLEXITÉS DES ALGORITHMES

| Algorithme | Complexité temporelle | Complexité spatiale |
|------------|----------------------|---------------------|
| First-Fit 1D | O(n × m) | O(n) |
| Best-Fit 1D | O(n × m) | O(n) |
| Worst-Fit 1D | O(n × m) | O(n) |
| Force brute 1D | O(n! × 2ⁿ) | O(n) |
| NFDH | O(n log n) | O(n) |
| FFDH | O(n²) | O(n) |
| Best-Fit 2D | O(n²) | O(n) |
| BFDA heuristique | O(n²) | O(n) |
| Force brute 2D | O(n! × (W×H)ⁿ) | O(n) |

---

## 7. ÉTAT GÉNÉRAL : ✅ 100% COMPLET

| Critère | État |
|---------|------|
| Code compile sans erreur | ✅ |
| Toutes les exigences du sujet | ✅ |
| Interface graphique fonctionnelle | ✅ |
| Algorithmes implémentés | ✅ |
| Contre-exemples présents | ✅ |
| Rotations gérées | ✅ |
| Force brute implémentée | ✅ |
| Documentation incluse | ✅ |

---

## 8. CAPTURES D'ÉCRAN (description)

```
┌─────────────────────────────────────────────────────────────┐
│  2D Packing — Algorithmes de Placement                      │
├─────────────┬─────────────┬─────────────────────────────────┤
│ ① 1D Packing │ ② 2D Rectangles │ ③ 3 Formes + Rotations    │
├─────────────┴─────────────┴─────────────────────────────────┤
│                                                             │
│  [Zone de saisie]              [Visualisation graphique]    │
│                                                             │
│  [Tableau des objets]          [Dessin du conteneur]        │
│                                                             │
│  [Résultats textuels]                                       │
│                                                             │
└─────────────────────────────────────────────────────────────┘
```

---

# 2D Packing - Projet COMPALGO

## Description
Implémentation d'algorithmes de paquetage (packing) en 1D et 2D.

## Algorithmes implémentés

### 1D Bin Packing
- First-Fit (FF)
- Best-Fit (BF)  
- Worst-Fit (WF)
- Force Brute

### 2D Rectangles
- NFDH (Next-Fit Decreasing Height)
- FFDH (First-Fit Decreasing Height)
- Best-Fit 2D
- Force Brute

### 3 Formes (Rectangle, Cercle, Triangle)
- Heuristique BFDA
- Force Brute avec rotations (90°, 180°)

## Exécution
```bash
javac src/Main.java
java src/Main