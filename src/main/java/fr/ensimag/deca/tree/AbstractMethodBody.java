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

/**
 *
 * @author gl01
 */
public abstract class AbstractMethodBody extends Tree
{
    protected abstract void verifyMethodBody(DecacCompiler compiler, ClassDefinition currentClass,
                EnvironmentExp envMethod, Type typRetour) throws ContextualError;

    protected abstract void codeGenMethodBody(DecacCompiler compiler);
}
