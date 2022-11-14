/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package fr.ensimag.deca.tree;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.EnvironmentExp;
import fr.ensimag.deca.context.Type;
import fr.ensimag.deca.tools.IndentPrintStream;

import java.io.PrintStream;

/**
 *
 * @author gl01
 */
public class MethodAsmBody extends AbstractMethodBody{
    
    private String code;
    private Location location;

    public MethodAsmBody(String code, Location location) {
	    this.code = code;
        this.location = location;
    }

    public Location getLocation()
    {
        return this.location;
    }

    @Override
    public void decompile(IndentPrintStream s) {
        s.println("asm(" + code + ");");
    }

    @Override
    protected void prettyPrintChildren(PrintStream s, String prefix) {
	//code.prettyPrint(s, prefix, true);
        s.print(prefix + code);
	    s.println();
    }

    @Override
    protected void iterChildren(TreeFunction f) {
        
    }

    @Override
    protected void codeGenMethodBody(DecacCompiler compiler) {
        throw new UnsupportedOperationException("not yet implemented");
        /*compiler.addInstruction(instruction);
        compiler.add(line);*/
    }

    @Override
    protected void verifyMethodBody(DecacCompiler compiler, ClassDefinition currentClass, EnvironmentExp envMethod, Type typRetour) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }
    
}
