package fr.ensimag.deca;

import fr.ensimag.deca.codegen.LabelGestion;
import fr.ensimag.deca.codegen.RegisterGestion;
import fr.ensimag.deca.context.BooleanType;
import fr.ensimag.deca.context.ClassDefinition;
//import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.ClassType;
//import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.EnvironmentExp;
import fr.ensimag.deca.context.EnvironmentType;
import fr.ensimag.deca.context.ExpDefinition;
import fr.ensimag.deca.context.FloatType;
import fr.ensimag.deca.context.IntType;
import fr.ensimag.deca.context.MethodDefinition;
import fr.ensimag.deca.context.Signature;
//import fr.ensimag.deca.context.MethodDefinition;
//import fr.ensimag.deca.context.Signature;
//import fr.ensimag.deca.context.MethodDefinition;
//import fr.ensimag.deca.context.Signature;
//import fr.ensimag.deca.context.StringType;
import fr.ensimag.deca.context.TypeDefinition;
import fr.ensimag.deca.context.VoidType;
import fr.ensimag.deca.context.EnvironmentExp.DoubleDefException;
//import fr.ensimag.deca.context.EnvironmentExp.DoubleDefException;
import fr.ensimag.deca.syntax.DecaLexer;
import fr.ensimag.deca.syntax.DecaParser;
import fr.ensimag.deca.tools.DecacInternalError;
//import fr.ensimag.deca.tools.IndentPrintStream;
import fr.ensimag.deca.tools.SymbolTable;
import fr.ensimag.deca.tools.SymbolTable.Symbol;
//import fr.ensimag.deca.tree.AbstractExpr;
import fr.ensimag.deca.tree.AbstractProgram;
import fr.ensimag.deca.tree.Identifier;
import fr.ensimag.deca.tree.Location;
import fr.ensimag.deca.tree.LocationException;
import fr.ensimag.ima.pseudocode.AbstractLine;
//import fr.ensimag.ima.pseudocode.DAddr;
//import fr.ensimag.ima.pseudocode.DVal;
import fr.ensimag.ima.pseudocode.IMAProgram;
import fr.ensimag.ima.pseudocode.Instruction;
import fr.ensimag.ima.pseudocode.Label;
import fr.ensimag.ima.pseudocode.RegisterOffset;
import fr.ensimag.ima.pseudocode.Register;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.LinkedHashMap;
import java.util.Map;

import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.apache.log4j.Logger;

/**
 * Decac compiler instance.
 *
 * This class is to be instantiated once per source file to be compiled. It
 * contains the meta-data used for compiling (source file name, compilation
 * options) and the necessary utilities for compilation (symbol tables, abstract
 * representation of target file, ...).
 *
 * It contains several objects specialized for different tasks. Delegate methods
 * are used to simplify the code of the caller (e.g. call
 * compiler.addInstruction() instead of compiler.getProgram().addInstruction()).
 *
 * @author gl01
 * @date 01/01/2022
 */
public class DecacCompiler {
    private static final Logger LOG = Logger.getLogger(DecacCompiler.class);

    SymbolTable Comp_Symbols;

    EnvironmentType envTypePredef;

    private LabelGestion labGestion; // label manager
    private RegisterGestion registerGestion; // register manager

    // I added this one v

    private EnvironmentExp environment = new EnvironmentExp(null);

    private Map<Label, EnvironmentExp> metEnv = new LinkedHashMap<Label, EnvironmentExp>();

    private boolean AmIInAMethod = false;

    private Label methodLabel;

    private int firstLineOfMethod;

    public void addIndex(Instruction i, int index)
    {
        this.program.addIndex(i, index);
    }

    public int getfirstLineOfMethod()
    {
        return this.firstLineOfMethod;
    }

    public boolean getStatus()
    {
        return AmIInAMethod;
    }

    public void set(Label methodLabel)
    {
        this.AmIInAMethod = true;
        this.methodLabel = methodLabel;
        this.firstLineOfMethod = this.program.getLastLineIndex();
    }

    public Label getMethLabel()
    {
        return this.methodLabel;
    }

    public void reset()
    {
        this.AmIInAMethod = false;
        this.methodLabel = null;
        this.registerGestion.freeStack();
        this.registerGestion.resetRegisters();
    }

    public void declareEnv(Label label, EnvironmentExp environment)
    {
        this.metEnv.put(label, environment);
    }

    public void declare(EnvironmentExp environment) {
        this.environment = environment;
    }

    public ExpDefinition getDefinition(Identifier identifier)
    {
        if (this.AmIInAMethod)
        {
            for (Map.Entry<Label, EnvironmentExp> index : this.metEnv.entrySet())
                if (index.getKey().toString().equals(this.methodLabel.toString()))
                    return index.getValue().get(identifier.getName());
            return null;
        }
        else
            return this.environment.get(identifier.getName());
    }

    public void declareClass(Identifier identifier, ClassDefinition definition) {
        this.envTypePredef.declare(identifier.getName(), definition);
    }

    public ClassDefinition getClassDefinition(Identifier identifier)
    {
        return (ClassDefinition)this.envTypePredef.get(identifier.getName());
    }

    // I added this one ^
    private boolean toReturn = false;

    public boolean getReturn() {
        return toReturn;
    }
    public void setReturn(boolean toReturn) {
        this.toReturn = toReturn;
    }


    /**
     * Portable newline character.
     */
    private static final String nl = System.getProperty("line.separator", "\n");

    public DecacCompiler(CompilerOptions compilerOptions, File source) {
        super();
        this.compilerOptions = compilerOptions;
        this.source = source;
        this.Comp_Symbols = new SymbolTable();
        this.declareEnvTypes();
        this.labGestion = new LabelGestion();
        if (compilerOptions != null) {
            this.registerGestion = new RegisterGestion(compilerOptions.getNbRegisters(), this);
        }
    }

    public LabelGestion getLabGestion() {
        return labGestion;
    }

    public RegisterGestion getRegisterGestion() {
        return registerGestion;
    }
    
    public EnvironmentType getEnvironmentType() {
        return envTypePredef;
    }

    public void declareEnvTypes() {
        this.envTypePredef = new EnvironmentType();
        // Object part
        Symbol[] newVoidSymb = { this.Comp_Symbols.create("void"), this.Comp_Symbols.create("int"),
                this.Comp_Symbols.create("float"), this.Comp_Symbols.create("boolean"), this.Comp_Symbols.create("Object") };
        
        this.envTypePredef.declare(newVoidSymb[0], new TypeDefinition(new VoidType(newVoidSymb[0]), Location.BUILTIN));
        this.envTypePredef.declare(newVoidSymb[1], new TypeDefinition(new IntType(newVoidSymb[1]), Location.BUILTIN));
        this.envTypePredef.declare(newVoidSymb[2], new TypeDefinition(new FloatType(newVoidSymb[2]), Location.BUILTIN));
        this.envTypePredef.declare(newVoidSymb[3], new TypeDefinition(new BooleanType(newVoidSymb[3]), Location.BUILTIN));
        //this.envTypePredef.declare(newVoidSymb[4], new TypeDefinition(new StringType(newVoidSymb[4]), null));
        ClassType objType = new ClassType(newVoidSymb[4], Location.BUILTIN, null);
        ClassDefinition objClassDef = new ClassDefinition(objType, Location.BUILTIN, null);
        Signature objEqSig = new Signature();
        objEqSig.add(objType);
        MethodDefinition methodDef = new MethodDefinition(new BooleanType(this.Comp_Symbols.create("boolean")), Location.BUILTIN, objEqSig, 1);
        methodDef.setLabel(new Label("Object.equals"));
        try {
            objClassDef.getMembers().declare(this.Comp_Symbols.create("equals"), methodDef);
        }
        catch (DoubleDefException e) {
            System.out.println("This exception will never be raised.");
        }
        objClassDef.setOperand(new RegisterOffset(1, Register.GB));
        objClassDef.setNumberOfMethods(1);
        this.envTypePredef.declare(newVoidSymb[4], objClassDef);
        //for methode equals Object part

    }

    /**
     * Source file associated with this compiler instance.
     */
    public File getSource() {
        return source;
    }

    /**
     * Compilation options (e.g. when to stop compilation, number of registers
     * to use, ...).
     */
    public CompilerOptions getCompilerOptions() {
        return compilerOptions;
    }

    public SymbolTable getSymbols() {
        return this.Comp_Symbols;
    }

    /**
     * @see
     *      fr.ensimag.ima.pseudocode.IMAProgram#add(fr.ensimag.ima.pseudocode.AbstractLine)
     */
    public void add(AbstractLine line) {
        program.add(line);
    }

    /**
     * @see fr.ensimag.ima.pseudocode.IMAProgram#addComment(java.lang.String)
     */
    public void addComment(String comment) {
        program.addComment(comment);
    }

    /**
     * @see
     *      fr.ensimag.ima.pseudocode.IMAProgram#addLabel(fr.ensimag.ima.pseudocode.Label)
     */
    public void addLabel(Label label) {
        program.addLabel(label);
    }

    /**
     * @see
     *      fr.ensimag.ima.pseudocode.IMAProgram#addInstruction(fr.ensimag.ima.pseudocode.Instruction)
     */
    public void addInstruction(Instruction instruction) {
        program.addInstruction(instruction);
    }

    /**
     * @see
     *      fr.ensimag.ima.pseudocode.IMAProgram#addInstruction(fr.ensimag.ima.pseudocode.Instruction,
     *      java.lang.String)
     */
    public void addInstruction(Instruction instruction, String comment) {
        program.addInstruction(instruction, comment);
    }

    /**
     * @see
     *      fr.ensimag.ima.pseudocode.IMAProgram#display()
     */
    public String displayIMAProgram() {
        return program.display();
    }

    private final CompilerOptions compilerOptions;
    private final File source;
    /**
     * The main program. Every instruction generated will eventually end up here.
     */
    private final IMAProgram program = new IMAProgram();

    /**
     * Run the compiler (parse source file, generate code)
     *
     * @return true on error
     */
    public boolean compile() {
        String sourceFile = source.getAbsolutePath();
        String destFile = sourceFile.substring(0, sourceFile.lastIndexOf('.')) + ".ass";
        // A FAIRE: calculer le nom du fichier .ass à partir du nom du
        // A FAIRE: fichier .deca.
        PrintStream err = System.err;
        PrintStream out = System.out;
        LOG.debug("Compiling file " + sourceFile + " to assembly file " + destFile);
        try {
            return doCompile(sourceFile, destFile, out, err);
        } catch (LocationException e) {
            e.display(err);
            return true;
        } catch (DecacFatalError e) {
            err.println(e.getMessage());
            return true;
        } catch (StackOverflowError e) {
            LOG.debug("stack overflow", e);
            err.println("Stack overflow while compiling file " + sourceFile + ".");
            return true;
        } catch (Exception e) {
            LOG.fatal("Exception raised while compiling file " + sourceFile
                    + ":", e);
            err.println("Internal compiler error while compiling file " + sourceFile + ", sorry.");
            return true;
        } catch (AssertionError e) {
            LOG.fatal("Assertion failed while compiling file " + sourceFile
                    + ":", e);
            err.println("Internal compiler error while compiling file " + sourceFile + ", sorry.");
            return true;
        }
    }

    /**
     * Internal function that does the job of compiling (i.e. calling lexer,
     * verification and code generation).
     *
     * @param sourceName name of the source (deca) file
     * @param destName   name of the destination (assembly) file
     * @param out        stream to use for standard output (output of decac -p)
     * @param err        stream to use to display compilation errors
     *
     * @return true on error
     */
    private boolean doCompile(String sourceName, String destName,
            PrintStream out, PrintStream err)
            throws DecacFatalError, LocationException {
        AbstractProgram prog = doLexingAndParsing(sourceName, err);

        if (prog == null) {
            LOG.info("Parsing failed");
            return true;
        }
        assert (prog.checkAllLocations());

        if (this.compilerOptions.getParse()) {
            System.out.print(prog.decompile());
            return false;
        }

        if (this.compilerOptions.getDebug() != 0) {

        }

        prog.verifyProgram(this);

        if (this.compilerOptions.getVerification()) {
            return false;
        }

        assert (prog.checkAllDecorations());

        addComment("start main program");
        prog.codeGenProgram(this);
        addComment("end main program");
        LOG.debug("Generated assembly code:" + nl + program.display());
        LOG.info("Output file assembly file is: " + destName);

        FileOutputStream fstream = null;
        try {
            fstream = new FileOutputStream(destName);
        } catch (FileNotFoundException e) {
            throw new DecacFatalError("Failed to open output file: " + e.getLocalizedMessage());
        }

        LOG.info("Writing assembler file ...");

        program.display(new PrintStream(fstream));
        LOG.info("Compilation of " + sourceName + " successful.");
        return false;
    }

    /**
     * Build and call the lexer and parser to build the primitive abstract
     * syntax tree.
     *
     * @param sourceName Name of the file to parse
     * @param err        Stream to send error messages to
     * @return the abstract syntax tree
     * @throws DecacFatalError    When an error prevented opening the source file
     * @throws DecacInternalError When an inconsistency was detected in the
     *                            compiler.
     * @throws LocationException  When a compilation error (incorrect program)
     *                            occurs.
     */
    protected AbstractProgram doLexingAndParsing(String sourceName, PrintStream err)
            throws DecacFatalError, DecacInternalError {
        DecaLexer lex;
        try {
            lex = new DecaLexer(CharStreams.fromFileName(sourceName));
        } catch (IOException ex) {
            throw new DecacFatalError("Failed to open input file: " + ex.getLocalizedMessage());
        }
        lex.setDecacCompiler(this);
        CommonTokenStream tokens = new CommonTokenStream(lex);
        DecaParser parser = new DecaParser(tokens);
        parser.setDecacCompiler(this);
        return parser.parseProgramAndManageErrors(err);
    }

}
