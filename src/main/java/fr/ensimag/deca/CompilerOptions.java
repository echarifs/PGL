package fr.ensimag.deca;

import java.io.File;
//import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

/**
 * User-specified options influencing the compilation.
 *
 * @author gl01
 * @date 01/01/2022
 */
public class CompilerOptions {
    public static final int QUIET = 0;
    public static final int INFO  = 1;
    public static final int DEBUG = 2;
    public static final int TRACE = 3;
    public static final int MAX_REGISTERS = 16;
    public static final int MIN_REGISTERS = 4;
    
    
    public int getDebug() {
        return debug;
    }

    public boolean getParallel() {
        return parallel;
    }

    public boolean getPrintBanner() {
        return printBanner;
    }
    
    public boolean getParse() {
        return parse;
    }
    
    public boolean getVerification() {
        return verification;
    }
    
    public boolean getNoCheck() {
        return noCheck;
    }
    
    public boolean getRegisters() {
        return registers;
    }

    public int getNbRegisters() {
        return nbRegisters;
    }
    
    public List<File> getSourceFiles() {
        return Collections.unmodifiableList(sourceFiles);
    }

    private int debug = 0;
    private int nbRegisters = 16;
    private boolean parallel = false;
    private boolean printBanner = false;
    private boolean parse = false;
    private boolean verification = false;
    private boolean noCheck = false;
    private boolean registers = false;
    private List<File> sourceFiles = new ArrayList<File>();

    
    public void parseArgs(String[] args) throws CLIException {
        // A FAIRE : parcourir args pour positionner les options correctement.\
        //if (printBanner)
        //    throw new CLIException("L’option ’-b’ ne peut être utilisée que sans autre option, et sans fichier source");
        for (String arg : args){
            if(!registers){
                switch(arg){
                    case "-b":
                        this.printBanner = true;
                        if(args.length > 1){
                            throw new CLIException("The option ’-b’ should be used without any other option or source file.");
                        }
                        break;
                    case "-p":
                        this.parse = true;
                        break;
                    case "-v":
                        this.verification = true;
                        break;
                    case "-n":
                        this.noCheck = true;
                        break;
                    case "-r":
                        this.registers = true;
                    case "-d":
                        this.debug++;
                        break;
                    case "-P":
                        this.parallel = true;
                        break;
                    default:
                        File f = new File(arg);
                        if (!sourceFiles.contains(f)){
                            sourceFiles.add(f);
                        }
                        break;
                }
            }else{
                nbRegisters = Integer.parseInt(arg);
                if (nbRegisters > MAX_REGISTERS || nbRegisters < MIN_REGISTERS ){
                    throw new UnsupportedOperationException("The number of registers is between 4 and 16.");
                }
                registers = false;
            } 
                    

            
        }
        
        
        //IMPLEMENTATION SANS OPTIONS FIRST! OPTIONS A IMPLEMENTER!
        //File f0 = new File(args[0]); //On accepte un seul fichier pour l'instant!
        //this.sourceFiles.add(f0);
        
        Logger logger = Logger.getRootLogger();
        // map command-line debug option to log4j's level.
        //System.out.println("bugs number: " + getDebug());
        switch (getDebug()) {
        case QUIET: break; // keep default
        case INFO:
            logger.setLevel(Level.INFO); break;
        case DEBUG:
            logger.setLevel(Level.DEBUG); break;
        case TRACE:
            logger.setLevel(Level.TRACE); break;
        default:
            logger.setLevel(Level.ALL); break;
        }
        logger.info("Application-wide trace level set to " + logger.getLevel());

        boolean assertsEnabled = false;
        assert assertsEnabled = true; // Intentional side effect!!!
        if (assertsEnabled) {
            logger.info("Java assertions enabled");
        } else {
            logger.info("Java assertions disabled");
        }

        //throw new UnsupportedOperationException("not yet implemented");
    }

    protected void displayUsage() {
        System.out.println("Usage: \n decac [[-p | -v] [-n] [-r X] [-d]* [-P] [-w] <fichier deca>...] | [-b]");
        System.out.println("-b (banner) : affiche une bannière indiquant le nom de l’équipe");
        System.out.println("-p (parse) : arrête decac après l’étape de construction de l’arbre, et affiche la décompilation de ce dernier (i.e. s’il n’y a qu’un fichier source à compiler, la sortie doit être un programme deca syntaxiquement correct)");
        System.out.println("-v (verification) : arrête decac après l’étape de vérifications (ne produit aucune sortie en l’absence d’erreur)");
        System.out.println("-n (no check) : supprime les tests à l'exécution des programmes incorrect et des programmes dont l’exécution dépasse les limites de la machine");
        System.out.println("-r X (registers) : limite les registres banalisés disponibles à R0 ... R{X-1}, avec 4 <= X <= 16");
        System.out.println("-d (debug) : active les traces de debug. Répéter l’option plusieurs fois pour avoir plus de traces. ");
        System.out.println("-P (parallel) : s’il y a plusieurs fichiers sources, lance la compilation des fichiers en parallèle (pour accélérer la compilation)");
        System.out.println("ATTENTION: Les options ’-p’ et ’-v’ sont incompatibles.");
    }
}
