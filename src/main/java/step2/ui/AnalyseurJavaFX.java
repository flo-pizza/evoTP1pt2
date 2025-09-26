package step2.ui;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import step2.core.AnalyseurProjet;
import step2.core.StatistiquesProjet;

import java.io.File;

public class AnalyseurJavaFX extends Application {

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Analyseur de projet Java");

        Label instruction = new Label("Veuillez choisir le projet Java à analyser :");
        TextField cheminProjet = new TextField();
        cheminProjet.setEditable(false);
        Button parcourirBtn = new Button("Parcourir…");

        VBox centerBox = new VBox(10, instruction, parcourirBtn, cheminProjet);
        centerBox.setAlignment(Pos.CENTER);
        centerBox.setPadding(new Insets(20));

        Button analyserBtn = new Button("Analyser");
        analyserBtn.setDisable(true);
        Button quitterBtn = new Button("Quitter");

        HBox buttonBox = new HBox(10, analyserBtn, quitterBtn);
        buttonBox.setAlignment(Pos.CENTER_RIGHT);
        buttonBox.setPadding(new Insets(10));

        BorderPane root = new BorderPane();
        root.setCenter(centerBox);
        root.setBottom(buttonBox);

        final File[] projetChoisi = {null};

        parcourirBtn.setOnAction(e -> {
            DirectoryChooser chooser = new DirectoryChooser();
            chooser.setTitle("Sélectionner le projet Java");
            File selectedDirectory = chooser.showDialog(primaryStage);
            if (selectedDirectory != null) {
                cheminProjet.setText(selectedDirectory.getAbsolutePath());
                projetChoisi[0] = selectedDirectory;
                analyserBtn.setDisable(false);
            }
        });

        quitterBtn.setOnAction(e -> primaryStage.close());

        analyserBtn.setOnAction(e -> {
            AnalyseurProjet analyseur = new AnalyseurProjet();
            StatistiquesProjet stats = analyseur.analyser(projetChoisi[0]);

            // On passe la fenêtre principale ET les stats
            ResultatsView.afficher(primaryStage, stats);
        });

        Scene scene = new Scene(root, 600, 250);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
