package step2.core;

import java.util.List;
import java.util.Set;

public class StatistiquesProjet {
    private int nbClasses;
    private int nbMethodes;
    private int nbAttributs;
    private int nbLignes;
    private int nbMaxParametres;
    private double moyMethodesParClasse;
    private double moyAttributsParClasse;
    private double moyLignesParMethode;
    private List<String> top10pcClassesParMethodes;
    private List<String> top10pcClassesParAttributs;
    private Set<String> classesCommunes;
    private List<String> top10pcMethodesParLignes;
    private List<String> classesAvecXMethodes;
    private int nbPackages;
    private int nbAppels; // Nouveau champ pour le nombre d'appels

    // Getters et setters existants
    public int getNbClasses() {
        return nbClasses;
    }

    public void setNbClasses(int nbClasses) {
        this.nbClasses = nbClasses;
    }

    public int getNbMethodes() {
        return nbMethodes;
    }

    public void setNbMethodes(int nbMethodes) {
        this.nbMethodes = nbMethodes;
    }

    public int getNbAttributs() {
        return nbAttributs;
    }

    public void setNbAttributs(int nbAttributs) {
        this.nbAttributs = nbAttributs;
    }

    public int getNbLignes() {
        return nbLignes;
    }

    public void setNbLignes(int nbLignes) {
        this.nbLignes = nbLignes;
    }

    public int getNbMaxParametres() {
        return nbMaxParametres;
    }

    public void setNbMaxParametres(int nbMaxParametres) {
        this.nbMaxParametres = nbMaxParametres;
    }

    public double getMoyMethodesParClasse() {
        return moyMethodesParClasse;
    }

    public void setMoyMethodesParClasse(double moyMethodesParClasse) {
        this.moyMethodesParClasse = moyMethodesParClasse;
    }

    public double getMoyAttributsParClasse() {
        return moyAttributsParClasse;
    }

    public void setMoyAttributsParClasse(double moyAttributsParClasse) {
        this.moyAttributsParClasse = moyAttributsParClasse;
    }

    public double getMoyLignesParMethode() {
        return moyLignesParMethode;
    }

    public void setMoyLignesParMethode(double moyLignesParMethode) {
        this.moyLignesParMethode = moyLignesParMethode;
    }

    public List<String> getTop10pcClassesParMethodes() {
        return top10pcClassesParMethodes;
    }

    public void setTop10pcClassesParMethodes(List<String> top10pcClassesParMethodes) {
        this.top10pcClassesParMethodes = top10pcClassesParMethodes;
    }

    public List<String> getTop10pcClassesParAttributs() {
        return top10pcClassesParAttributs;
    }

    public void setTop10pcClassesParAttributs(List<String> top10pcClassesParAttributs) {
        this.top10pcClassesParAttributs = top10pcClassesParAttributs;
    }

    public Set<String> getClassesCommunes() {
        return classesCommunes;
    }

    public void setClassesCommunes(Set<String> classesCommunes) {
        this.classesCommunes = classesCommunes;
    }

    public List<String> getTop10pcMethodesParLignes() {
        return top10pcMethodesParLignes;
    }

    public void setTop10pcMethodesParLignes(List<String> top10pcMethodesParLignes) {
        this.top10pcMethodesParLignes = top10pcMethodesParLignes;
    }

    public List<String> getClassesAvecXMethodes() {
        return classesAvecXMethodes;
    }

    public void setClassesAvecXMethodes(List<String> classesAvecXMethodes) {
        this.classesAvecXMethodes = classesAvecXMethodes;
    }

    public int getNbPackages() {
        return nbPackages;
    }

    public void setNbPackages(int nbPackages) {
        this.nbPackages = nbPackages;
    }

    // Nouvelles méthodes pour nbAppels
    public int getNbAppels() {
        return nbAppels;
    }

    public void setNbAppels(int nbAppels) {
        this.nbAppels = nbAppels;
    }
}