/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package fr.ensimag.deca.tree;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.ContextualError;
//import fr.ensimag.deca.DecacCompiler;
//import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.tools.IndentPrintStream;

/**
 *
 * @author gl01
 */
public class ListDeclMethod extends TreeList<AbstractDeclMethod> {

    @Override
    public void decompile(IndentPrintStream s) {
        for (AbstractDeclMethod declMethod : getList()) {
            declMethod.decompile(s);
            s.println();
        }
    }

    public  void verifyListDeclMethod(DecacCompiler compiler, ClassDefinition currentClass,
    ClassDefinition superClass) throws ContextualError {
        for (AbstractDeclMethod m : this.getList())
            m.verifyDeclMethod(compiler, currentClass, superClass);
    }
}
