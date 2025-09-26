package step2.core;

import java.util.List;
import java.util.Set;

public class StatistiquesProjet {

    // 1️⃣ Totaux
    private int nbClasses;
    private int nbLignes;
    private int nbMethodes;
    private int nbAttributs;
    private int nbPackages;

    // 2️⃣ Moyennes
    private double moyMethodesParClasse;
    private double moyLignesParMethode;
    private double moyAttributsParClasse;

    // 3️⃣ Top et filtres
    private List<String> top10pcClassesParMethodes;
    private List<String> top10pcClassesParAttributs;
    private Set<String> classesCommunes;          // Classes dans les deux top 10%
    private List<String> classesAvecXMethodes;    // Classes avec > X méthodes
    private List<String> top10pcMethodesParLignes;
    private int nbMaxParametres;

    // ---------------- Getters & Setters ----------------

    public int getNbClasses() { return nbClasses; }
    public void setNbClasses(int nbClasses) { this.nbClasses = nbClasses; }

    public int getNbLignes() { return nbLignes; }
    public void setNbLignes(int nbLignes) { this.nbLignes = nbLignes; }

    public int getNbMethodes() { return nbMethodes; }
    public void setNbMethodes(int nbMethodes) { this.nbMethodes = nbMethodes; }

    public int getNbAttributs() { return nbAttributs; }
    public void setNbAttributs(int nbAttributs) { this.nbAttributs = nbAttributs; }

    public int getNbPackages() { return nbPackages; }
    public void setNbPackages(int nbPackages) { this.nbPackages = nbPackages; }

    public double getMoyMethodesParClasse() { return moyMethodesParClasse; }
    public void setMoyMethodesParClasse(double moyMethodesParClasse) { this.moyMethodesParClasse = moyMethodesParClasse; }

    public double getMoyLignesParMethode() { return moyLignesParMethode; }
    public void setMoyLignesParMethode(double moyLignesParMethode) { this.moyLignesParMethode = moyLignesParMethode; }

    public double getMoyAttributsParClasse() { return moyAttributsParClasse; }
    public void setMoyAttributsParClasse(double moyAttributsParClasse) { this.moyAttributsParClasse = moyAttributsParClasse; }

    public List<String> getTop10pcClassesParMethodes() { return top10pcClassesParMethodes; }
    public void setTop10pcClassesParMethodes(List<String> top10pcClassesParMethodes) { this.top10pcClassesParMethodes = top10pcClassesParMethodes; }

    public List<String> getTop10pcClassesParAttributs() { return top10pcClassesParAttributs; }
    public void setTop10pcClassesParAttributs(List<String> top10pcClassesParAttributs) { this.top10pcClassesParAttributs = top10pcClassesParAttributs; }

    public Set<String> getClassesCommunes() { return classesCommunes; }
    public void setClassesCommunes(Set<String> classesCommunes) { this.classesCommunes = classesCommunes; }

    public List<String> getClassesAvecXMethodes() { return classesAvecXMethodes; }
    public void setClassesAvecXMethodes(List<String> classesAvecXMethodes) { this.classesAvecXMethodes = classesAvecXMethodes; }

    public List<String> getTop10pcMethodesParLignes() { return top10pcMethodesParLignes; }
    public void setTop10pcMethodesParLignes(List<String> top10pcMethodesParLignes) { this.top10pcMethodesParLignes = top10pcMethodesParLignes; }

    public int getNbMaxParametres() { return nbMaxParametres; }
    public void setNbMaxParametres(int nbMaxParametres) { this.nbMaxParametres = nbMaxParametres; }

    // ---------------- toString pour affichage simple ----------------
    @Override
    public String toString() {
        return """
            Résultats de l'analyse :
            1. Nombre de classes : %d
            2. Nombre de lignes de code : %d
            3. Nombre total de méthodes : %d
            4. Nombre total d'attributs : %d
            5. Nombre total de packages : %d
            6. Moyenne de méthodes par classe : %.2f
            7. Moyenne de lignes par méthode : %.2f
            8. Moyenne d’attributs par classe : %.2f
            9. Top 10%% classes par méthodes : %s
            10. Top 10%% classes par attributs : %s
            11. Classes communes aux deux top 10%% : %s
            12. Classes avec plus de X méthodes : %s
            13. Top 10%% méthodes par lignes : %s
            14. Nombre max de paramètres : %d
            """.formatted(
                nbClasses,
                nbLignes,
                nbMethodes,
                nbAttributs,
                nbPackages,
                moyMethodesParClasse,
                moyLignesParMethode,
                moyAttributsParClasse,
                top10pcClassesParMethodes,
                top10pcClassesParAttributs,
                classesCommunes,
                classesAvecXMethodes,
                top10pcMethodesParLignes,
                nbMaxParametres
        );
    }
}
