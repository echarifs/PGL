/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package fr.ensimag.deca.tree;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.EnvironmentExp;
import fr.ensimag.deca.context.ParamDefinition;
import fr.ensimag.deca.context.Type;
import fr.ensimag.deca.tools.IndentPrintStream;
import java.io.PrintStream;
//import java.util.logging.Level;
//import java.util.logging.Logger;

/**
 *
 * @author gl01
 */
public class DeclParam extends AbstractDeclParam{
    
    private AbstractIdentifier type;
    private AbstractIdentifier name;

    public DeclParam(AbstractIdentifier type, AbstractIdentifier name) {
	    this.type = type;
	    this.name = name;
    }

    // I added this one v

    public Identifier getName()
    {
        return (Identifier)this.name;
    }

    // I added this one ^

    @Override
    public void decompile(IndentPrintStream s) {
        type.decompile(s);
        s.print(" ");
        name.decompile(s);
    }

    @Override
    protected void prettyPrintChildren(PrintStream s, String prefix) {
        this.type.prettyPrint(s, prefix, false);
	    this.name.prettyPrint(s, prefix, true);
    }

    @Override
    protected void iterChildren(TreeFunction f) {
        this.type.iter(f);
	    this.name.iter(f);
    }

    @Override
    protected Type verifyDeclParam(DecacCompiler compiler, EnvironmentExp envLocal) throws ContextualError {
        Type typeParam = this.type.verifyType(compiler);
        if (typeParam.isVoid()) {
            throw new ContextualError("Contextual error : The type of parameter can't be void (regle 2.9)", getLocation());
        }
        ParamDefinition defParam = new ParamDefinition(typeParam, this.getLocation());
        try {
            // declare parameter in local environment
            envLocal.declare(this.name.getName(), defParam);
        }
        catch (EnvironmentExp.DoubleDefException ex) {
            //Logger.getLogger(DeclParam.class.getName()).log(Level.SEVERE, null, ex);
            throw new ContextualError("Contextual error : The parameter is already defined in this class (regle 2.9)", this.name.getLocation());
        }
        this.name.setType(typeParam);
        this.name.setDefinition(defParam);

        return typeParam;
    }
    
}
