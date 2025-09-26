package step2.analyse;

import spoon.Launcher;
import spoon.reflect.CtModel;
import spoon.reflect.declaration.*;
import step2.core.StatistiquesProjet;

import java.io.File;
import java.util.*;
import java.util.stream.Collectors;

public class AnalyseurSpoon {

    public StatistiquesProjet analyserProjet(File dossier) {
        StatistiquesProjet stats = new StatistiquesProjet();

        // 1. Préparer Spoon
        Launcher launcher = new Launcher();
        launcher.addInputResource(dossier.getAbsolutePath()); // chemin du projet à analyser
        launcher.getEnvironment().setNoClasspath(true); // pour analyser même sans dépendances
        launcher.buildModel();

        CtModel model = launcher.getModel();

        // 2. Récupérer les classes
        List<CtClass<?>> classes = model.getElements(ct -> ct instanceof CtClass<?>)
                .stream()
                .map(ct -> (CtClass<?>) ct)
                .collect(Collectors.toList());

        stats.setNbClasses(classes.size());

        // 3. Compter les méthodes et attributs
        int nbMethodes = 0;
        int nbAttributs = 0;
        int nbLignes = 0;
        Map<CtClass<?>, Integer> mapMethodes = new HashMap<>();
        Map<CtClass<?>, Integer> mapAttributs = new HashMap<>();
        Map<CtMethod<?>, Integer> mapLignesParMethode = new HashMap<>();

        for (CtClass<?> cl : classes) {
            int m = cl.getMethods().size();
            int a = cl.getFields().size();

            nbMethodes += m;
            nbAttributs += a;
            mapMethodes.put(cl, m);
            mapAttributs.put(cl, a);

            // compter les lignes par méthode (approx : nb statements)
            for (CtMethod<?> method : cl.getMethods()) {
                int lignes = method.getBody() == null ? 0 : method.getBody().getStatements().size();
                nbLignes += lignes;
                mapLignesParMethode.put(method, lignes);
            }
        }

        // 4. Remplir les stats globales
        stats.setNbMethodes(nbMethodes);
        stats.setNbLignes(nbLignes);
        stats.setNbPackages(model.getAllPackages().size());

        stats.setMoyMethodesParClasse(classes.isEmpty() ? 0 : (double) nbMethodes / classes.size());
        stats.setMoyAttributsParClasse(classes.isEmpty() ? 0 : (double) nbAttributs / classes.size());
        stats.setMoyLignesParMethode(nbMethodes == 0 ? 0 : (double) nbLignes / nbMethodes);

        // 5. Top 10% classes par méthodes
        int topSize = Math.max(1, classes.size() / 10);
        stats.setTop10pcClassesParMethodes(
                mapMethodes.entrySet().stream()
                        .sorted((e1, e2) -> Integer.compare(e2.getValue(), e1.getValue()))
                        .limit(topSize)
                        .map(e -> e.getKey().getSimpleName() + " (" + e.getValue() + " méthodes)")
                        .collect(Collectors.toList())
        );

        // 6. Top 10% classes par attributs
        stats.setTop10pcClassesParAttributs(
                mapAttributs.entrySet().stream()
                        .sorted((e1, e2) -> Integer.compare(e2.getValue(), e1.getValue()))
                        .limit(topSize)
                        .map(e -> e.getKey().getSimpleName() + " (" + e.getValue() + " attributs)")
                        .collect(Collectors.toList())
        );

        // 7. Top 10% méthodes par lignes
        int topMethodeSize = Math.max(1, mapLignesParMethode.size() / 10);
        stats.setTop10pcMethodesParLignes(
                mapLignesParMethode.entrySet().stream()
                        .sorted((e1, e2) -> Integer.compare(e2.getValue(), e1.getValue()))
                        .limit(topMethodeSize)
                        .map(e -> e.getKey().getSimpleName() + " (" + e.getValue() + " lignes)")
                        .collect(Collectors.toList())
        );

        // 8. Classe avec X méthodes (par ex. > 10)
        stats.setClassesAvecXMethodes(
                mapMethodes.entrySet().stream()
                        .filter(e -> e.getValue() > 10)
                        .map(e -> e.getKey().getSimpleName() + " (" + e.getValue() + " méthodes)")
                        .collect(Collectors.toList())
        );

        // 9. Classes communes (top méthodes ∩ top attributs)
        Set<String> communes = new HashSet<>(stats.getTop10pcClassesParMethodes());
        communes.retainAll(stats.getTop10pcClassesParAttributs());
        stats.setClassesCommunes(communes);

        // 10. Nb max de paramètres dans une méthode
        int maxParams = mapLignesParMethode.keySet().stream()
                .mapToInt(m -> m.getParameters().size())
                .max().orElse(0);
        stats.setNbMaxParametres(maxParams);

        return stats;
    }
}
