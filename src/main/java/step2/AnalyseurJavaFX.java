package step2;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;

import java.io.File;

public class AnalyseurJavaFX extends Application {

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Analyseur de projet Java");

        // Zone centrale
        Label instruction = new Label("Veuillez choisir le projet Java à analyser :");
        TextField cheminProjet = new TextField();
        cheminProjet.setEditable(false);
        Button parcourirBtn = new Button("Parcourir…");

        VBox centerBox = new VBox(10, instruction, parcourirBtn, cheminProjet);
        centerBox.setAlignment(Pos.CENTER);
        centerBox.setPadding(new Insets(20));

        // Zone inférieure avec boutons
        Button suivantBtn = new Button("Suivant");
        suivantBtn.setDisable(true); // désactivé tant qu'aucun projet n'est choisi
        Button quitterBtn = new Button("Quitter");

        HBox buttonBox = new HBox(10, suivantBtn, quitterBtn);
        buttonBox.setAlignment(Pos.CENTER_RIGHT);
        buttonBox.setPadding(new Insets(10));

        BorderPane root = new BorderPane();
        root.setCenter(centerBox);
        root.setBottom(buttonBox);

        // Actions
        parcourirBtn.setOnAction(e -> {
            DirectoryChooser chooser = new DirectoryChooser();
            chooser.setTitle("Sélectionner le projet Java");
            File selectedDirectory = chooser.showDialog(primaryStage);
            if (selectedDirectory != null) {
                cheminProjet.setText(selectedDirectory.getAbsolutePath());
                suivantBtn.setDisable(false); // active le bouton suivant
            }
        });

        quitterBtn.setOnAction(e -> primaryStage.close());

        suivantBtn.setOnAction(e -> {
            // Ici on pourrait lancer l'analyse du projet avec Spoon
            System.out.println("Projet à analyser : " + cheminProjet.getText());
        });

        Scene scene = new Scene(root, 600, 250);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
