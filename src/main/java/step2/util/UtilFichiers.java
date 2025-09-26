package step2.util;

import java.io.*;

public class UtilFichiers {

    public static int compterFichiersJava(File dossier) {
        int count = 0;
        File[] fichiers = dossier.listFiles();
        if (fichiers != null) {
            for (File f : fichiers) {
                if (f.isDirectory()) {
                    count += compterFichiersJava(f);
                } else if (f.getName().endsWith(".java")) {
                    count++;
                }
            }
        }
        return count;
    }

    public static int compterLignes(File dossier) {
        int lignes = 0;
        File[] fichiers = dossier.listFiles();
        if (fichiers != null) {
            for (File f : fichiers) {
                if (f.isDirectory()) {
                    lignes += compterLignes(f);
                } else if (f.getName().endsWith(".java")) {
                    try (BufferedReader br = new BufferedReader(new FileReader(f))) {
                        while (br.readLine() != null) {
                            lignes++;
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        return lignes;
    }
}
