package step2.ui;

import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import step2.core.StatistiquesProjet;

import java.util.List;

public class ResultatsView {

    public static void afficher(Stage primaryStage, StatistiquesProjet stats) {
        primaryStage.setTitle("Résultats de l'analyse");

        // Zone centrale : VBox
        VBox contenu = new VBox(15);
        contenu.setPadding(new Insets(20));

        // 1️⃣ Stats globales
        GridPane statsGrid = new GridPane();
        statsGrid.setVgap(8);
        statsGrid.setHgap(15);

        statsGrid.addRow(0, new Label("📦 Nombre de packages:"), new Label(String.valueOf(stats.getNbPackages())));
        statsGrid.addRow(1, new Label("🏛️ Nombre de classes:"), new Label(String.valueOf(stats.getNbClasses())));
        statsGrid.addRow(2, new Label("🔧 Nombre de méthodes:"), new Label(String.valueOf(stats.getNbMethodes())));
        statsGrid.addRow(3, new Label("📊 Nombre de lignes de code:"), new Label(String.valueOf(stats.getNbLignes())));
        statsGrid.addRow(4, new Label("📈 Moyenne méthodes/classe:"), new Label(String.format("%.2f", stats.getMoyMethodesParClasse())));
        statsGrid.addRow(5, new Label("📈 Moyenne attributs/classe:"), new Label(String.format("%.2f", stats.getMoyAttributsParClasse())));
        statsGrid.addRow(6, new Label("📈 Moyenne lignes/méthode:"), new Label(String.format("%.2f", stats.getMoyLignesParMethode())));
        statsGrid.addRow(7, new Label("🔢 Nb max de paramètres:"), new Label(String.valueOf(stats.getNbMaxParametres())));

        contenu.getChildren().add(statsGrid);

        // 2️⃣ Table pour Top 10% classes par méthodes
        contenu.getChildren().add(createTable("🏆 Top 10% classes par méthodes", stats.getTop10pcClassesParMethodes()));

        // 3️⃣ Table pour Top 10% classes par attributs
        contenu.getChildren().add(createTable("🏆 Top 10% classes par attributs", stats.getTop10pcClassesParAttributs()));

        // 4️⃣ Table pour Top 10% méthodes par lignes
        contenu.getChildren().add(createTable("🏆 Top 10% méthodes par lignes", stats.getTop10pcMethodesParLignes()));

        // 5️⃣ Table pour classes avec +X méthodes
        contenu.getChildren().add(createTable("🔍 Classes avec +10 méthodes", stats.getClassesAvecXMethodes()));

        // 6️⃣ Table pour classes communes
        contenu.getChildren().add(createTable("⚡ Classes communes aux deux top 10%", List.copyOf(stats.getClassesCommunes())));

        // Bouton quitter
        Button quitterBtn = new Button("Quitter");
        quitterBtn.setOnAction(e -> primaryStage.close());

        BorderPane root = new BorderPane();
        root.setCenter(new ScrollPane(contenu)); // scroll si beaucoup de résultats
        root.setBottom(quitterBtn);
        BorderPane.setMargin(quitterBtn, new Insets(10));

        Scene scene = new Scene(root, 800, 600);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    // Méthode utilitaire pour créer un TableView simple
    private static TableView<String> createTable(String titre, List<String> data) {
        TableView<String> table = new TableView<>();
        table.setPrefHeight(Math.min(200, data.size() * 25 + 30)); // ajustement hauteur

        TableColumn<String, String> col = new TableColumn<>(titre);
        col.setCellValueFactory(param -> new javafx.beans.property.SimpleStringProperty(param.getValue()));
        col.setPrefWidth(700);

        table.getColumns().add(col);
        table.getItems().addAll(data);

        return table;
    }
}
