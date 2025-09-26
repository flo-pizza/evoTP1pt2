package step2;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import com.github.javaparser.resolution.declarations.ResolvedMethodDeclaration;
import com.github.javaparser.symbolsolver.JavaSymbolSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.CombinedTypeSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.JavaParserTypeSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.ReflectionTypeSolver;
import com.github.javaparser.symbolsolver.utils.SymbolSolverCollectionStrategy;
import com.github.javaparser.utils.ProjectRoot;
import com.github.javaparser.utils.SourceRoot;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class OOAnalyzer {

    // Structures pour stocker les données
    private List<CompilationUnit> compilationUnits = new ArrayList<>();
    private Map<String, Integer> classMethods = new HashMap<>();
    private Map<String, Integer> classAttributes = new HashMap<>();
    private Map<String, Integer> methodLines = new HashMap<>();
    private Map<String, Integer> methodParams = new HashMap<>();
    private Set<String> packages = new HashSet<>();
    private int totalLines = 0;
    private int totalMethods = 0;
    private int totalClasses = 0;
    private int totalAttributes = 0;
    private int totalCalls = 0;
    private Map<String, Set<String>> callGraph = new HashMap<>();

    private String srcPath; // Chemin sélectionné

    public void analyze(String srcPath) throws IOException {
        this.srcPath = srcPath;
        compilationUnits.clear();
        classMethods.clear();
        classAttributes.clear();
        methodLines.clear();
        methodParams.clear();
        packages.clear();
        totalLines = 0;
        totalMethods = 0;
        totalClasses = 0;
        totalAttributes = 0;
        totalCalls = 0;
        callGraph.clear();

        // Utiliser SymbolSolverCollectionStrategy pour le projet entier
        ProjectRoot projectRoot = new SymbolSolverCollectionStrategy().collect(Paths.get(srcPath));

        for (SourceRoot sourceRoot : projectRoot.getSourceRoots()) {
            // Parser les fichiers avec la config du solver
            sourceRoot.tryToParse();
            compilationUnits.addAll(sourceRoot.getCompilationUnits());

            // Compter les lignes manuellement (pas géré par SourceRoot)
            try (Stream<Path> walk = Files.walk(sourceRoot.getRoot())) {
                totalLines += walk.filter(p -> p.toString().endsWith(".java"))
                        .mapToLong(p -> {
                            try {
                                return Files.lines(p).count();
                            } catch (IOException e) {
                                return 0;
                            }
                        }).sum();
            }
        }

        analyzeMetrics();
        analyzeCallGraph();
    }

    private void analyzeMetrics() {
        for (CompilationUnit cu : compilationUnits) {
            cu.accept(new VoidVisitorAdapter<Void>() {
                @Override
                public void visit(ClassOrInterfaceDeclaration n, Void arg) {
                    String className = n.getFullyQualifiedName().orElse(n.getNameAsString());
                    totalClasses++;
                    classMethods.put(className, n.getMethods().size());
                    int attrs = n.getFields().stream().mapToInt(field -> field.getVariables().size()).sum();
                    classAttributes.put(className, attrs);
                    totalAttributes += attrs;
                    totalMethods += n.getMethods().size();
                    super.visit(n, arg);
                }

                @Override
                public void visit(MethodDeclaration n, Void arg) {
                    String methodName = n.getDeclarationAsString(true, false, false);
                    int lines = n.getEnd().map(end -> end.line - n.getBegin().get().line + 1).orElse(0);
                    methodLines.put(methodName, lines);
                    methodParams.put(methodName, n.getParameters().size());
                    super.visit(n, arg);
                }
            }, null);
        }
    }

    private void analyzeCallGraph() {
        for (CompilationUnit cu : compilationUnits) {
            cu.accept(new VoidVisitorAdapter<Void>() {
                @Override
                public void visit(MethodCallExpr n, Void arg) {
                    try {
                        ResolvedMethodDeclaration resolved = n.resolve();
                        String calledMethod = resolved.getQualifiedSignature();
                        n.findAncestor(MethodDeclaration.class).ifPresent(caller -> {
                            String callerMethod = caller.getDeclarationAsString(true, false, false);
                            callGraph.computeIfAbsent(callerMethod, k -> new HashSet<>()).add(calledMethod);
                            totalCalls++;
                            System.out.println("Resolved call: " + callerMethod + " -> " + calledMethod);
                        });
                    } catch (Exception e) {
                        System.err.println("Failed to resolve call: " + n + " - Error: " + e.getMessage());
                    }
                    super.visit(n, arg);
                }
            }, null);
        }
    }

    public Map<String, String> getStatisticsMap(int X) {
        // Identique à avant (stats)
        Map<String, String> stats = new LinkedHashMap<>();
        stats.put("1. Nombre de classes", String.valueOf(totalClasses));
        stats.put("2. Nombre de lignes de code", String.valueOf(totalLines));
        stats.put("3. Nombre total de méthodes", String.valueOf(totalMethods));
        stats.put("4. Nombre total de packages", String.valueOf(packages.size()));
        stats.put("5. Nombre moyen de méthodes par classe", String.format("%.2f", totalClasses > 0 ? (double) totalMethods / totalClasses : 0));
        stats.put("6. Nombre moyen de lignes par méthode", String.format("%.2f", methodLines.values().stream().mapToInt(Integer::intValue).average().orElse(0)));
        stats.put("7. Nombre moyen d’attributs par classe", String.format("%.2f", totalClasses > 0 ? (double) totalAttributes / totalClasses : 0));

        int topPercent = Math.max(1, (int) Math.ceil(totalClasses * 0.1));
        List<String> topMethodsClasses = classMethods.entrySet().stream()
                .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
                .limit(topPercent)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
        stats.put("8. Top 10% classes par nombre de méthodes", topMethodsClasses.toString());

        List<String> topAttrsClasses = classAttributes.entrySet().stream()
                .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
                .limit(topPercent)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
        stats.put("9. Top 10% classes par nombre d’attributs", topAttrsClasses.toString());

        Set<String> both = new HashSet<>(topMethodsClasses);
        both.retainAll(topAttrsClasses);
        stats.put("10. Classes dans les deux catégories", both.toString());

        List<String> overX = classMethods.entrySet().stream()
                .filter(e -> e.getValue() > X)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
        stats.put("11. Classes avec plus de " + X + " méthodes", overX.toString());

        int topMethodsPercent = Math.max(1, (int) Math.ceil(totalMethods * 0.1));
        List<String> topMethods = methodLines.entrySet().stream()
                .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
                .limit(topMethodsPercent)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
        stats.put("12. Top 10% méthodes par lignes de code", topMethods.toString());

        int maxParams = methodParams.values().stream().mapToInt(Integer::intValue).max().orElse(0);
        stats.put("13. Nombre maximal de paramètres", String.valueOf(maxParams));

        stats.put("Nombre d'appels détectés", String.valueOf(totalCalls));

        return stats;
    }

    public static class SimpleGraphPanel extends JPanel implements MouseWheelListener {
        private final Map<String, Set<String>> callGraph;
        private Map<String, Point> nodePositions = new HashMap<>();
        private double scale = 1.0;

        public SimpleGraphPanel(Map<String, Set<String>> callGraph) {
            this.callGraph = callGraph;
            setPreferredSize(new Dimension(800, 600));
            addMouseWheelListener(this);
            updateNodePositions();
        }

        public void updateNodePositions() {
            nodePositions.clear();
            Set<String> allNodes = new HashSet<>(callGraph.keySet());
            callGraph.values().forEach(allNodes::addAll);
            if (allNodes.isEmpty()) return;

            int x = 50, y = 50, col = 0, maxCols = 4;
            for (String node : allNodes) {
                nodePositions.put(node, new Point(x, y));
                col++;
                if (col >= maxCols) {
                    x = 50;
                    y += 80; // Réduction de l'espacement vertical pour mieux s'afficher
                    col = 0;
                } else {
                    x += 200;
                }
            }
            setPreferredSize(new Dimension(800, y + 100)); // Ajuste la taille préférée dynamiquement
        }

        @Override
        public void mouseWheelMoved(MouseWheelEvent e) {
            scale += e.getWheelRotation() * -0.1;
            scale = Math.max(0.5, Math.min(3.0, scale));
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g;
            g2d.scale(scale, scale);

            // Arêtes
            g2d.setColor(Color.GRAY);
            for (Map.Entry<String, Set<String>> entry : callGraph.entrySet()) {
                Point from = nodePositions.get(entry.getKey());
                if (from == null) continue;
                for (String toKey : entry.getValue()) {
                    Point to = nodePositions.get(toKey);
                    if (to != null) {
                        g2d.drawLine(from.x + 25, from.y + 15, to.x + 25, to.y + 15);
                    }
                }
            }

            // Nœuds
            g2d.setColor(Color.BLUE);
            for (Map.Entry<String, Point> entry : nodePositions.entrySet()) {
                Point p = entry.getValue();
                g2d.fillOval(p.x, p.y, 50, 30);
                g2d.setColor(Color.WHITE);
                String label = entry.getKey().substring(0, Math.min(20, entry.getKey().length()));
                g2d.drawString(label, p.x + 5, p.y + 20);
                g2d.setColor(Color.BLUE);
            }
        }
    }

    public void displayGUI() {
        JFrame frame = new JFrame("Analyse Statique et Dynamique OO");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1000, 700);
        frame.setMinimumSize(new Dimension(600, 400));
        frame.setLayout(new BorderLayout());

        // Panneau supérieur avec GridBagLayout
        JPanel topPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JButton selectButton = new JButton("Sélectionner le dossier");
        selectButton.setToolTipText("Cliquez pour choisir un dossier contenant des fichiers Java");
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0.1;
        topPanel.add(selectButton, gbc);

        JLabel pathLabel = new JLabel("Dossier sélectionné : Aucun");
        gbc.gridx = 1;
        gbc.weightx = 0.6;
        topPanel.add(pathLabel, gbc);

        JLabel xLabel = new JLabel("Valeur de X :");
        gbc.gridx = 2;
        gbc.weightx = 0.1;
        topPanel.add(xLabel, gbc);

        JTextField xField = new JTextField("5", 5);
        xField.setToolTipText("Seuil pour le nombre de méthodes (point 11 de l'énoncé)");
        gbc.gridx = 3;
        gbc.weightx = 0.1;
        topPanel.add(xField, gbc);

        JButton analyzeButton = new JButton("Analyser");
        analyzeButton.setToolTipText("Cliquez pour lancer l'analyse sur le dossier sélectionné");
        gbc.gridx = 4;
        gbc.weightx = 0.1;
        gbc.anchor = GridBagConstraints.EAST;
        topPanel.add(analyzeButton, gbc);

        frame.add(topPanel, BorderLayout.NORTH);

        // Tabs pour résultats
        JTabbedPane tabbedPane = new JTabbedPane();
        JPanel metricsPanel = new JPanel(new GridLayout(0, 1));
        JScrollPane metricsScroll = new JScrollPane(metricsPanel);
        tabbedPane.addTab("Métriques", metricsScroll);

        SimpleGraphPanel graphPanel = new SimpleGraphPanel(callGraph);
        JScrollPane graphScroll = new JScrollPane(graphPanel);
        tabbedPane.addTab("Graphe d'Appel", graphScroll);

        frame.add(tabbedPane, BorderLayout.CENTER);

        // Action sélection dossier
        selectButton.addActionListener(e -> {
            JFileChooser chooser = new JFileChooser();
            chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            chooser.setDialogTitle("Sélectionnez le dossier source Java");
            if (chooser.showOpenDialog(frame) == JFileChooser.APPROVE_OPTION) {
                srcPath = chooser.getSelectedFile().getAbsolutePath();
                pathLabel.setText("Dossier sélectionné : " + srcPath);
            }
        });

        // Action analyser
        analyzeButton.addActionListener(e -> {
            if (srcPath == null || srcPath.isEmpty()) {
                JOptionPane.showMessageDialog(frame, "Veuillez sélectionner un dossier d'abord !", "Erreur", JOptionPane.ERROR_MESSAGE);
                return;
            }
            try {
                int X = Integer.parseInt(xField.getText());
                analyze(srcPath);
                Map<String, String> stats = getStatisticsMap(X);
                metricsPanel.removeAll();
                for (Map.Entry<String, String> entry : stats.entrySet()) {
                    JLabel label = new JLabel(entry.getKey() + ": " + entry.getValue());
                    label.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
                    metricsPanel.add(label);
                }
                metricsPanel.revalidate();
                metricsPanel.repaint();

                graphPanel.updateNodePositions();
                graphPanel.repaint();
                graphScroll.revalidate();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(frame, "X doit être un entier valide !", "Erreur", JOptionPane.ERROR_MESSAGE);
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(frame, "Erreur lors de l'analyse : " + ex.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
            }
        });

        frame.setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new OOAnalyzer().displayGUI());
    }
}