package step2.core;

import spoon.Launcher;
import spoon.reflect.CtModel;
import spoon.reflect.declaration.*;
import java.io.File;
import java.util.*;
import java.util.stream.Collectors;

public class AnalyseurProjet {

    public StatistiquesProjet analyser(File projet) {
        StatistiquesProjet stats = new StatistiquesProjet();

        // 1️⃣ Configurer Spoon
        Launcher launcher = new Launcher();
        launcher.addInputResource(projet.getAbsolutePath());
        launcher.getEnvironment().setNoClasspath(true); // analyse même sans dépendances
        launcher.buildModel();

        CtModel model = launcher.getModel();

        // 2️⃣ Récupérer les classes et packages
        List<CtClass<?>> classes = model.getElements(ct -> ct instanceof CtClass<?>)
                .stream()
                .map(ct -> (CtClass<?>) ct)
                .collect(Collectors.toList());
        List<CtPackage> packages = model.getAllPackages().stream().toList();

        stats.setNbClasses(classes.size());
        stats.setNbPackages(packages.size());

        // 3️⃣ Compter méthodes, attributs, lignes, max paramètres
        int nbMethodes = 0;
        int nbAttributs = 0;
        int nbLignes = 0;
        int nbMaxParametres = 0;

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

            for (CtMethod<?> method : cl.getMethods()) {
                int lignes = method.getBody() == null ? 0 : method.getBody().getStatements().size();
                nbLignes += lignes;
                mapLignesParMethode.put(method, lignes);
                nbMaxParametres = Math.max(nbMaxParametres, method.getParameters().size());
            }
        }

        stats.setNbMethodes(nbMethodes);
        stats.setNbAttributs(nbAttributs);
        stats.setNbLignes(nbLignes);
        stats.setNbMaxParametres(nbMaxParametres);

        stats.setMoyMethodesParClasse(classes.isEmpty() ? 0 : (double) nbMethodes / classes.size());
        stats.setMoyAttributsParClasse(classes.isEmpty() ? 0 : (double) nbAttributs / classes.size());
        stats.setMoyLignesParMethode(nbMethodes == 0 ? 0 : (double) nbLignes / nbMethodes);

        // 4️⃣ Top 10% classes par méthodes
        int topSize = Math.max(1, classes.size() / 10);
        stats.setTop10pcClassesParMethodes(
                mapMethodes.entrySet().stream()
                        .sorted((e1, e2) -> Integer.compare(e2.getValue(), e1.getValue()))
                        .limit(topSize)
                        .map(e -> e.getKey().getSimpleName() + " (" + e.getValue() + " méthodes)")
                        .collect(Collectors.toList())
        );

        // 5️⃣ Top 10% classes par attributs
        stats.setTop10pcClassesParAttributs(
                mapAttributs.entrySet().stream()
                        .sorted((e1, e2) -> Integer.compare(e2.getValue(), e1.getValue()))
                        .limit(topSize)
                        .map(e -> e.getKey().getSimpleName() + " (" + e.getValue() + " attributs)")
                        .collect(Collectors.toList())
        );

        // 6️⃣ Classes communes dans les deux top 10%
        Set<String> communes = new HashSet<>(stats.getTop10pcClassesParMethodes());
        communes.retainAll(stats.getTop10pcClassesParAttributs());
        stats.setClassesCommunes(communes);

        // 7️⃣ Top 10% méthodes par lignes
        int topMethodeSize = Math.max(1, mapLignesParMethode.size() / 10);
        stats.setTop10pcMethodesParLignes(
                mapLignesParMethode.entrySet().stream()
                        .sorted((e1, e2) -> Integer.compare(e2.getValue(), e1.getValue()))
                        .limit(topMethodeSize)
                        .map(e -> e.getKey().getSimpleName() + " (" + e.getValue() + " lignes)")
                        .collect(Collectors.toList())
        );

        // 8️⃣ Classes avec plus de X méthodes (ex : > 10)
        stats.setClassesAvecXMethodes(
                mapMethodes.entrySet().stream()
                        .filter(e -> e.getValue() > 10)
                        .map(e -> e.getKey().getSimpleName() + " (" + e.getValue() + " méthodes)")
                        .collect(Collectors.toList())
        );

        return stats;
    }
}
