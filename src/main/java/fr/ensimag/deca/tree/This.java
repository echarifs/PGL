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
public class This extends AbstractLValue
{
    @Override
    public Type verifyExpr(DecacCompiler compiler, EnvironmentExp localEnv, ClassDefinition currentClass) throws ContextualError {
        if (currentClass == null) {
            throw new ContextualError("Contextual error: 'this' can't be applied in main (règle 3.43) ", this.getLocation());
        }
        if (!currentClass.isClass()){
            throw new ContextualError("Contextual error: 'this' can only be applied in class (règle 3.43)", this.getLocation());
        }
        Type typ = currentClass.getType();
        this.setType(typ);
        return this.getType();
    }

    @Override
    public void decompile(IndentPrintStream s) {
       s.print("this");
    }

    @Override
    public boolean isThis()
    {
        return true;
    }

    @Override
    protected void prettyPrintChildren(PrintStream s, String prefix) {
       // do nothing
    }

    @Override
    protected void iterChildren(TreeFunction f) {
        // do nothing
    }
    
}
