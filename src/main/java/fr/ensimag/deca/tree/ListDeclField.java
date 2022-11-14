/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package fr.ensimag.deca.tree;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.ContextualError;
//import fr.ensimag.deca.context.EnvironmentExp;
//import fr.ensimag.deca.context.EnvironmentExp.DoubleDefException;
import fr.ensimag.deca.tools.IndentPrintStream;

/**
 *
 * @author gl01
 */
public class ListDeclField extends TreeList<AbstractDeclField> {

    @Override
    public void decompile(IndentPrintStream s) {
        for (AbstractDeclField declField : getList()) {
            declField.decompile(s);
            s.println();
        }
        
    }
    
    public  void verifyListDeclField(DecacCompiler compiler, AbstractIdentifier superClass,
                AbstractIdentifier currentClass) throws ContextualError {
        //EnvironmentExp AttributsEnv = new EnvironmentExp(null);
        //Identifier tmp;
        //System.out.println(superClass.getName().getName() + " : " + superClass.getClassDefinition().getNumberOfFields());
        currentClass.getClassDefinition().setNumberOfFields(superClass.getClassDefinition().getNumberOfFields());
        //System.out.println(currentClass.getName().getName() + " : " +currentClass.getClassDefinition().getNumberOfFields());
        for (AbstractDeclField f : this.getList())
            f.verifyDeclField(compiler, superClass, currentClass);
            /*tmp = ((DeclField)f).getFielName();
            try {
                AttributsEnv.declare(tmp.getName(), tmp.getFieldDefinition());
            }
            catch (DoubleDefException e) {
                System.out.println("This exception will never be raised.");
            }*/
        //System.out.println(currentClass.getName().getName() + " : " +currentClass.getClassDefinition().getNumberOfFields());
        //return AttributsEnv;
    }
    
}
