package step2.ui;

import javafx.embed.swing.SwingNode;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.graphstream.graph.Graph;
import org.graphstream.graph.implementations.SingleGraph;
import org.graphstream.ui.fx_viewer.FxViewer;
import org.graphstream.ui.view.Viewer;
import org.graphstream.ui.view.View;
import step2.core.StatistiquesProjet;
import javax.swing.JPanel;

public class GrapheView { // Note : Changé de ResultatsView à GrapheView

    public void afficher(StatistiquesProjet stats, Stage stage) {
        // Racine
        BorderPane root = new BorderPane();

        // ----- Partie stats (gauche) -----
        VBox statsBox = new VBox(10);
        statsBox.setStyle("-fx-padding: 10; -fx-background-color: #f0f0f0;");
        statsBox.setPadding(new Insets(10));
        statsBox.getChildren().addAll(
                new Label("Nombre de classes: " + stats.getNbClasses()),
                new Label("Nombre de méthodes: " + stats.getNbMethodes()),
                new Label("Nombre d'appels: " + stats.getNbAppels())
        );

        // ----- Partie graphe (droite) -----
        Graph graph = new SingleGraph("Projet");
        // Exemple de noeuds et arêtes
        graph.addNode("A").setAttribute("ui.label", "A");
        graph.addNode("B").setAttribute("ui.label", "B");
        graph.addEdge("AB", "A", "B");

        // Création du viewer
        Viewer viewer = new FxViewer(graph, Viewer.ThreadingModel.GRAPH_IN_ANOTHER_THREAD);
        viewer.enableAutoLayout();
        View view = viewer.addDefaultView(false);

        // Utiliser SwingNode pour intégrer la vue GraphStream
        SwingNode swingNode = new SwingNode();
        swingNode.setContent((JPanel) view); // Cast en JPanel pour Swing

        // Ajouter le SwingNode au centre
        root.setCenter(swingNode);

        // ----- Assemblage -----
        root.setLeft(statsBox);

        // ----- Scene -----
        Scene scene = new Scene(root, 800, 400);
        stage.setTitle("Résultats Projet");
        stage.setScene(scene);
        stage.show();
    }
}