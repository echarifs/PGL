package fr.ensimag.deca.tree;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.tools.IndentPrintStream;
import fr.ensimag.ima.pseudocode.ImmediateInteger;
import fr.ensimag.ima.pseudocode.ImmediateString;
import fr.ensimag.ima.pseudocode.Label;
import fr.ensimag.ima.pseudocode.LabelOperand;
import fr.ensimag.ima.pseudocode.NullOperand;
import fr.ensimag.ima.pseudocode.Register;
import fr.ensimag.ima.pseudocode.RegisterOffset;
import fr.ensimag.ima.pseudocode.instructions.*;
import java.io.PrintStream;
import org.apache.commons.lang.Validate;
import org.apache.log4j.Logger;

/**
 * Deca complete program (class definition plus main block)
 *
 * @author gl01
 * @date 01/01/2022
 */
public class Program extends AbstractProgram {
    private static final Logger LOG = Logger.getLogger(Program.class);
    
    
    public Program(ListDeclClass classes, AbstractMain main) {
        Validate.notNull(classes);
        Validate.notNull(main);
        this.classes = classes;
        this.main = main;
    }
    public ListDeclClass getClasses() {
        return classes;
    }
    public AbstractMain getMain() {
        return main;
    }
    private ListDeclClass classes;
    private AbstractMain main;

    @Override
    public void verifyProgram(DecacCompiler compiler) throws ContextualError {
        LOG.debug("verify program: start");
        this.classes.verifyListClass(compiler);
        this.classes.verifyListClassMembers(compiler);
        this.classes.verifyListClassBody(compiler);
        this.main.verifyMain(compiler);
        LOG.debug("verify program: end");
    }

    @Override
    public void codeGenProgram(DecacCompiler compiler) {
        // A FAIRE: compléter ce squelette très rudimentaire de code
        compiler.addComment("Main program");
        compiler.getRegisterGestion().incADDSP(3);
        compiler.addInstruction(new LOAD(new NullOperand(), Register.R0));
        compiler.addInstruction(new STORE(Register.R0, new RegisterOffset(1, Register.GB)));
        compiler.addInstruction(new LOAD(new LabelOperand(new Label("code.Object.equals")), Register.R0));
        compiler.addInstruction(new STORE(Register.R0, new RegisterOffset(2, Register.GB)));
        for (AbstractDeclClass index : this.classes.getList())
            ((DeclClass)index).codeGenPasseFirst(compiler);

        this.main.codeGenMain(compiler);
        compiler.addInstruction(new HALT());

        compiler.addLabel(new Label("code.Object.equals"));
        Label FalseLabel = new Label("code.Object.equals.false"), EndCMPLabel = new Label("code.Object.equals.end");
        compiler.addInstruction(new PUSH(Register.getR(2)));
        compiler.addInstruction(new LOAD(new RegisterOffset(-3, Register.LB), Register.getR(2)));
        compiler.addInstruction(new CMP(new RegisterOffset(-2, Register.LB), Register.getR(2)));
        compiler.addInstruction(new BNE(FalseLabel));
        compiler.addInstruction(new LOAD(new ImmediateInteger(1), Register.R0));
        compiler.addInstruction(new BRA(EndCMPLabel));
        compiler.addLabel(FalseLabel);
        compiler.addInstruction(new LOAD(new ImmediateInteger(0), Register.R0));
        compiler.addLabel(EndCMPLabel);
        compiler.addInstruction(new POP(Register.getR(2)));
        compiler.addInstruction(new RTS());

        for (AbstractDeclClass index : this.classes.getList())
            index.codeGenDeclClass(compiler);

        compiler.addIndex(new TSTO(compiler.getRegisterGestion().getADDSP()), 2);
        compiler.addIndex(new BOV(new Label("StackOverflowError")), 3);
        compiler.addIndex(new ADDSP(compiler.getRegisterGestion().getADDSP()), 4);
        compiler.addLabel(new Label("ZeroDivisionError"));
	    compiler.addInstruction(new WSTR(new ImmediateString("Error: Use zero as division")));
        compiler.addInstruction(new ERROR());
        compiler.addLabel(new Label("StackOverflowError"));
	    compiler.addInstruction(new WSTR(new ImmediateString("Error: Stack Overflow")));
        compiler.addInstruction(new ERROR());
        compiler.addLabel(new Label("HeapOverflowError"));
        compiler.addInstruction(new WSTR(new ImmediateString("Error: Heap Overflow")));
        compiler.addInstruction(new ERROR());
        compiler.addLabel(new Label("NullObjectError"));
        compiler.addInstruction(new WSTR(new ImmediateString("Error : Object is null")));
        compiler.addInstruction(new ERROR());
        compiler.addComment("End main program");
    }

    @Override
    public void decompile(IndentPrintStream s) {
        getClasses().decompile(s);
        getMain().decompile(s);
    }
    
    @Override
    protected void iterChildren(TreeFunction f) {
        classes.iter(f);
        main.iter(f);
    }
    @Override
    protected void prettyPrintChildren(PrintStream s, String prefix) {
        classes.prettyPrint(s, prefix, false);
        main.prettyPrint(s, prefix, true);
    }
}
