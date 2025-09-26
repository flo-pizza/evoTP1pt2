package step2.core;

import spoon.Launcher;
import spoon.reflect.CtModel;
import spoon.reflect.code.CtInvocation;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtMethod;

import java.io.File;
import java.util.*;

public class GrapheAppel {

    public Map<String, Set<String>> construireGraphe(File projet) {
        Launcher launcher = new Launcher();
        launcher.addInputResource(projet.getAbsolutePath());
        launcher.getEnvironment().setNoClasspath(true);
        launcher.buildModel();

        CtModel model = launcher.getModel();
        Map<String, Set<String>> graphe = new HashMap<>();

        for (CtClass<?> classe : model.getElements(ct -> ct instanceof CtClass<?>).stream()
                .map(ct -> (CtClass<?>) ct).toList()) {

            for (CtMethod<?> methode : classe.getMethods()) {
                String nomMethode = classe.getSimpleName() + "." + methode.getSimpleName();
                graphe.putIfAbsent(nomMethode, new HashSet<>());

                // Correctif pour le bug de type
                List<CtInvocation<?>> invocations = new ArrayList<>();
                methode.getElements(ct -> ct instanceof CtInvocation<?>)
                        .forEach(ct -> invocations.add((CtInvocation<?>) ct));

                for (CtInvocation<?> invocation : invocations) {
                    if (invocation.getExecutable() != null && invocation.getExecutable().getDeclaringType() != null) {
                        String cible = invocation.getExecutable().getDeclaringType().getSimpleName()
                                + "." + invocation.getExecutable().getSimpleName();
                        graphe.get(nomMethode).add(cible);
                    }
                }
            }
        }

        return graphe;
    }
}
