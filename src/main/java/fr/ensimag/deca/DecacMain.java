package fr.ensimag.deca;

import java.io.File;
import org.apache.log4j.Logger;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.*;

/**
 * Main class for the command-line Deca compiler.
 *
 * @author gl01
 * @date 04/01/2022
 */
public class DecacMain {
    private static Logger LOG = Logger.getLogger(DecacMain.class);
    
    public static void main(String[] args) {
        // example log4j message.
        LOG.info("Decac compiler started");
        boolean error = false;
        final CompilerOptions options = new CompilerOptions();
        try {
            options.parseArgs(args);
        } catch (CLIException e) {
            System.err.println("Error during option parsing:\n"
                    + e.getMessage());
            options.displayUsage();
            System.exit(1);
        }
        if (options.getPrintBanner()) {
            System.out.println("Projet_GL équipe gl01");
        }
        if (args.length == 0) {
            options.displayUsage();
        }
        if (options.getSourceFiles().isEmpty() && !options.getPrintBanner()) {
            System.err.println("Fichier source vide!");
        }
        if (options.getParallel()) {
            // A FAIRE : instancier DecacCompiler pour chaque fichier à
            // compiler, et lancer l'exécution des méthodes compile() de chaque
            // instance en parallèle. Il est conseillé d'utiliser
            // java.util.concurrent de la bibliothèque standard Java.

            List<Future<Boolean>> valeurs = new LinkedList<>();
            ExecutorService compilerParallel = Executors.
                    newFixedThreadPool(java.lang.Runtime.getRuntime().availableProcessors());

            for (File f : options.getSourceFiles()) {
                valeurs.add(
                        compilerParallel.submit(() ->
                        {
                            DecacCompiler decaCompiler = new DecacCompiler(options, f);
                            return decaCompiler.compile();
                        })
                );

            }

            for (Future<Boolean> v : valeurs) {
                try {
                    if (v.get()) {
                        error = true; // detect error
                        break;
                    }
                } catch (ExecutionException | InterruptedException e) {
                    error = true;
                }
            }
            

            
           

            //throw new UnsupportedOperationException("Parallel build not yet implemented");
        } else {
            for (File source : options.getSourceFiles()) {
                DecacCompiler compiler = new DecacCompiler(options, source);
                if (compiler.compile()) {
                    error = true;
                }
            }
        }
        System.exit(error ? 1 : 0);
    }
}
