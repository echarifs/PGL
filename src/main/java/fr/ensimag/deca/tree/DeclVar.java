package fr.ensimag.deca.tree;

import fr.ensimag.deca.context.Type;
import fr.ensimag.deca.context.VariableDefinition;
import fr.ensimag.deca.context.EnvironmentExp.DoubleDefException;
import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.EnvironmentExp;
//import fr.ensimag.deca.context.ExpDefinition;
import fr.ensimag.deca.tools.IndentPrintStream;
import fr.ensimag.deca.tools.SymbolTable.Symbol;

//import static org.mockito.ArgumentMatchers.nullable;

import java.io.PrintStream;
import org.apache.commons.lang.Validate;

/**
 * @author gl01
 * @date 01/01/2022
 */
public class DeclVar extends AbstractDeclVar {

    
    final private AbstractIdentifier type;
    final private AbstractIdentifier varName;
    final private AbstractInitialization initialization;

    public DeclVar(AbstractIdentifier type, AbstractIdentifier varName, AbstractInitialization initialization) {
        Validate.notNull(type);
        Validate.notNull(varName);
        Validate.notNull(initialization);
        this.type = type;
        this.varName = varName;
        this.initialization = initialization;
    }

    /* I added this one v */

    public AbstractIdentifier getType()
    {
        return this.type;
    }

    public AbstractIdentifier getvarName()
    {
        return this.varName;
    }

    public AbstractInitialization getInit()
    {
        return this.initialization;
    }

    /* I added this one ^ */

    @Override
    protected void verifyDeclVar(DecacCompiler compiler,
            EnvironmentExp localEnv, ClassDefinition currentClass)
            throws ContextualError {
        Symbol name = this.getvarName().getName();
        //ExpDefinition def = new VariableDefinition(this.getType().getType(), this.getLocation());
        //this.getvarName().setDefinition(def);
        Type typev = this.type.verifyType(compiler);
        VariableDefinition vdef = new VariableDefinition(typev, this.getLocation());
        //condition 3.17
        if (typev.isVoid()){
            throw new ContextualError("type of a variable can't be void", getLocation());
        }
        this.varName.setType(typev);
        this.varName.setDefinition(vdef);
        try {
            localEnv.declare(name, vdef);
        }
        catch (DoubleDefException e) {
            throw new ContextualError("redeclaration of \'" + this.getvarName().getName() + "\'", this.getLocation());
        }
        this.initialization.verifyInitialization(compiler, typev, localEnv, currentClass);
        /*if (this.initialization.isInitialized())
        {
            localEnv.get(name).setInitialization();
        }*/
    }

    
    @Override
    public void decompile(IndentPrintStream s) {
        this.type.decompile(s);
        s.print(" ");
        this.varName.decompile(s);
        this.initialization.decompile(s);
        s.println(";");
    }

    @Override
    protected
    void iterChildren(TreeFunction f) {
        type.iter(f);
        varName.iter(f);
        initialization.iter(f);
    }
    
    @Override
    protected void prettyPrintChildren(PrintStream s, String prefix) {
        type.prettyPrint(s, prefix, false);
        varName.prettyPrint(s, prefix, false);
        initialization.prettyPrint(s, prefix, true);
    }
}
