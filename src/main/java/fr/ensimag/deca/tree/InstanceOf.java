/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package fr.ensimag.deca.tree;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.EnvironmentExp;
import fr.ensimag.deca.context.Type;
import fr.ensimag.deca.tools.IndentPrintStream;
import java.io.PrintStream;

/**
 *
 * @author gl01
 */
public class InstanceOf extends AbstractExpr{
    
    private AbstractExpr e;
    private AbstractIdentifier type;

    public InstanceOf(AbstractExpr e, AbstractIdentifier type) {
	this.e = e;
	this.type = type;
    }

    @Override
    public Type verifyExpr(DecacCompiler compiler, EnvironmentExp localEnv, ClassDefinition currentClass) throws ContextualError {
        Type typInstance = this.e.verifyExpr(compiler, localEnv, currentClass);
        Type typClass = this.type.verifyType(compiler);
        if (!(typInstance.isClassOrNull())) {
            throw new ContextualError("Contextual error : 'instanceof' expected class or null for instance but got " + typInstance + " (regle 3.40) ", getLocation());
        }
        if (!(typClass.isClass())) {
            throw new ContextualError("Contextual error : 'instanceof' expected class but got " + typClass + " (regle 3.40) ", getLocation());
        }
        
        this.setType(compiler.getEnvironmentType().get(compiler.getSymbols().create("boolean")).getType());
        return (this.getType());
    }

    @Override
    public void decompile(IndentPrintStream s) {
        s.print("(");
        e.decompile(s);
	s.print(" instanceof ");
	type.decompile(s);
        s.print(")");
    }

    @Override
    protected void prettyPrintChildren(PrintStream s, String prefix) {
        e.prettyPrint(s, prefix, false);
	type.prettyPrint(s, prefix, true);
        
    }

    @Override
    protected void iterChildren(TreeFunction f) {
        // do nothing
    }
    
}
