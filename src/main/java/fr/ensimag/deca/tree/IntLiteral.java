package fr.ensimag.deca.tree;

import fr.ensimag.deca.context.Type;
import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.EnvironmentExp;
import fr.ensimag.deca.tools.IndentPrintStream;
import fr.ensimag.deca.tools.SymbolTable;
//import fr.ensimag.ima.pseudocode.ImmediateInteger;
//import fr.ensimag.ima.pseudocode.Register;
//import fr.ensimag.ima.pseudocode.instructions.LOAD;
//import fr.ensimag.ima.pseudocode.instructions.WINT;
import fr.ensimag.deca.context.IntType;

import java.io.PrintStream;

/**
 * Integer literal
 *
 * @author gl01
 * @date 01/01/2022
 */
public class IntLiteral extends AbstractExpr {
    public int getValue() {
        return value;
    }

    private int value;

    public IntLiteral(int value) {
        this.value = value;
        this.setType(new IntType(new SymbolTable().create("int")));
    }

    @Override
    public Type verifyExpr(DecacCompiler compiler, EnvironmentExp localEnv,
            ClassDefinition currentClass) throws ContextualError {
        return this.getType();
    }


    @Override
    String prettyPrintNode() {
        return "Int (" + getValue() + ")";
    }

    @Override
    public void decompile(IndentPrintStream s) {
        //if (value > Integer.MAX_VALUE)
        s.print(Integer.toString(value));
    }

    @Override
    protected void iterChildren(TreeFunction f) {
        // leaf node => nothing to do
    }

    @Override
    protected void prettyPrintChildren(PrintStream s, String prefix) {
        // leaf node => nothing to do
    }
    
    @Override
    public boolean isIntLiteral()
    {
        return true;
    }

}
