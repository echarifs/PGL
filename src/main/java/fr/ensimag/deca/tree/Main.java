package fr.ensimag.deca.tree;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.ContextualError;
//import fr.ensimag.deca.context.Definition;
import fr.ensimag.deca.context.EnvironmentExp;
//import fr.ensimag.deca.context.EnvironmentType;
//import fr.ensimag.deca.context.ExpDefinition;
//import fr.ensimag.deca.context.VariableDefinition;
import fr.ensimag.deca.context.VoidType; //added
//import fr.ensimag.deca.context.EnvironmentExp.DoubleDefException;
import fr.ensimag.deca.tools.IndentPrintStream;
//import fr.ensimag.deca.tools.SymbolTable.Symbol;
import fr.ensimag.ima.pseudocode.DVal;
import fr.ensimag.ima.pseudocode.GPRegister;
import fr.ensimag.ima.pseudocode.ImmediateFloat;
import fr.ensimag.ima.pseudocode.ImmediateInteger;
//import fr.ensimag.ima.pseudocode.Label;
import fr.ensimag.ima.pseudocode.Register;
import fr.ensimag.ima.pseudocode.RegisterOffset;
//import fr.ensimag.ima.pseudocode.instructions.ADDSP;
//import fr.ensimag.ima.pseudocode.instructions.BOV;
import fr.ensimag.ima.pseudocode.instructions.LOAD;
import fr.ensimag.ima.pseudocode.instructions.STORE;
//import fr.ensimag.ima.pseudocode.instructions.TSTO;

import java.io.PrintStream;
//import java.util.List;

import org.apache.commons.lang.Validate;
import org.apache.log4j.Logger;

/**
 * @author gl01
 * @date 01/01/2022
 */
public class Main extends AbstractMain {
    private static final Logger LOG = Logger.getLogger(Main.class);
    
    private ListDeclVar declVariables;
    private ListInst insts;
    public Main(ListDeclVar declVariables,
            ListInst insts) {
        Validate.notNull(declVariables);
        Validate.notNull(insts);
        this.declVariables = declVariables;
        this.insts = insts;
    }

    @Override
    protected void verifyMain(DecacCompiler compiler) throws ContextualError {
        LOG.debug("verify Main: start");
        // A FAIRE: Appeler méthodes "verify*" de ListDeclVarSet et ListInst.
        // Vous avez le droit de changer le profil fourni pour ces méthodes
        // (mais ce n'est à priori pas nécessaire).
        EnvironmentExp env = new EnvironmentExp(null);

        this.declVariables.verifyListDeclVariable(compiler, env, null);
        this.insts.verifyListInst(compiler, env, null, new VoidType(compiler.getSymbols().create("void")));
        compiler.declare(env);
        LOG.debug("verify Main: end");
        //throw new UnsupportedOperationException("not yet implemented");
    }

    @Override
    protected void codeGenMain(DecacCompiler compiler) {
        // A FAIRE: traiter les déclarations de variables.
        compiler.addComment("Beginning of main instructions:");
        int integer = 0;
        compiler.getRegisterGestion().incADDSP(this.declVariables.size());
        /*compiler.addIndex(new TSTO(compiler.getRegisterGestion().getADDSP()), 2);
        compiler.addIndex(new BOV(new Label("StackOverflowError")), 3);
        compiler.addIndex(new ADDSP(compiler.getRegisterGestion().getADDSP()), 4);*/
        for (AbstractDeclVar index : this.declVariables.getList())
        {
            DeclVar tmp = (DeclVar)index;
            RegisterOffset rgtmp = null;
            if (tmp.getInit().isInitialized())
            {
                Initialization tmpInit = (Initialization)tmp.getInit();
                DVal rg;
                if (tmpInit.getExpression().isIntLiteral())
                    rg = new ImmediateInteger(((IntLiteral)tmpInit.getExpression()).getValue());
                else if (tmpInit.getExpression().isFloatLiteral())
                    rg = new ImmediateFloat(((FloatLiteral)tmpInit.getExpression()).getValue());
                else if (tmpInit.getExpression().isBooleanLiteral())
                {
                    Boolean value = ((BooleanLiteral)tmpInit.getExpression()).getValue();
                    rg = value ? new ImmediateInteger(1) : new ImmediateInteger(0);
                }
                else if (tmpInit.getExpression().isIdentifier())
                    rg = compiler.getDefinition((Identifier)tmpInit.getExpression()).getOperand();
                else if (tmpInit.getExpression().isSelection())
                    rg = ((Selection)tmpInit.getExpression()).getStorage(compiler);
                else
                {
                    tmpInit.getExpression().codeGenInst(compiler);
                    if (tmpInit.getExpression().isMethodCall())
                        rg = Register.R0;
                    else
                    {
                        rg = compiler.getRegisterGestion().getGPRegister();
                        if (rg.equals(Register.getR(compiler.getCompilerOptions().getNbRegisters() - 1)) && !compiler.getRegisterGestion().containsGPRegisterLibre())
                            rg = new RegisterOffset(0, Register.SP);
                    }
                }
                rgtmp = new RegisterOffset(compiler.getRegisterGestion().getOffset() + integer, Register.GB);
                GPRegister tmprg = compiler.getRegisterGestion().getGPRegisterLibre();
                compiler.addInstruction(new LOAD(rg, tmprg));
                compiler.addInstruction(new STORE(tmprg, rgtmp));
                compiler.getRegisterGestion().freeGPRegister(tmprg);
                if (rg.isR())
                    compiler.getRegisterGestion().freeGPRegister((GPRegister)rg);
            }
            if (rgtmp == null)
                rgtmp = new RegisterOffset(compiler.getRegisterGestion().getOffset() + integer, Register.GB);
            compiler.getDefinition((Identifier)tmp.getvarName()).setOperand(rgtmp);
            integer++;
        }
        insts.codeGenListInst(compiler);
    }
    
    @Override
    public void decompile(IndentPrintStream s) {
        s.println("{");
        s.indent();
        declVariables.decompile(s);
        insts.decompile(s);
        s.unindent();
        s.println("}");
    }

    @Override
    protected void iterChildren(TreeFunction f) {
        declVariables.iter(f);
        insts.iter(f);
    }
 
    @Override
    protected void prettyPrintChildren(PrintStream s, String prefix) {
        declVariables.prettyPrint(s, prefix, false);
        insts.prettyPrint(s, prefix, true);
    }
}
