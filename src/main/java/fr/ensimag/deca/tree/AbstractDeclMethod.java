/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package fr.ensimag.deca.tree;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.ContextualError;
//import fr.ensimag.deca.context.EnvironmentExp;
//import fr.ensimag.deca.tools.IndentPrintStream;
//import java.io.PrintStream;
import fr.ensimag.deca.context.EnvironmentExp;

/**
 *
 * @author gl01
 */
public abstract class AbstractDeclMethod extends Tree
{

    protected abstract void verifyDeclMethod(DecacCompiler compiler,
            ClassDefinition classDefinition, ClassDefinition classDefinitionSuper) throws ContextualError;
    
    protected abstract void verifyMethodBody(DecacCompiler compiler, EnvironmentExp localEnv,
            ClassDefinition currentClass) throws ContextualError;

    protected abstract void codeGenDeclMethod(DecacCompiler compiler);

}
