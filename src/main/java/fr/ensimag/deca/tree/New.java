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
import fr.ensimag.ima.pseudocode.GPRegister;
import fr.ensimag.ima.pseudocode.ImmediateInteger;
import fr.ensimag.ima.pseudocode.Label;
import fr.ensimag.ima.pseudocode.LabelOperand;
import fr.ensimag.ima.pseudocode.Register;
import fr.ensimag.ima.pseudocode.RegisterOffset;
import fr.ensimag.ima.pseudocode.instructions.BOV;
import fr.ensimag.ima.pseudocode.instructions.BSR;
import fr.ensimag.ima.pseudocode.instructions.LEA;
import fr.ensimag.ima.pseudocode.instructions.NEW;
import fr.ensimag.ima.pseudocode.instructions.POP;
import fr.ensimag.ima.pseudocode.instructions.PUSH;
import fr.ensimag.ima.pseudocode.instructions.STORE;

import java.io.PrintStream;

/**
 *
 * @author gl01
 */
public class New extends AbstractExpr{
    
    AbstractIdentifier idenClass;
    
    public New(AbstractIdentifier idenClass) {
	    this.idenClass = idenClass;
    }

    @Override
    public Type verifyExpr(DecacCompiler compiler, EnvironmentExp localEnv, ClassDefinition currentClass) throws ContextualError {
        Type typ = this.idenClass.verifyType(compiler);
        // if is class
        if (!typ.isClass()) {
            throw new ContextualError("Contextual error : The identifier should be a class", this.idenClass.getLocation());
        }
        this.setType(typ);
        return typ;
    }

    @Override
    public void decompile(IndentPrintStream s) {
        s.print("new ");
        s.print(this.idenClass.getName().getName()); 
        s.print("()");
    }

    @Override
    public void codeGenInst(DecacCompiler compiler)
    {
        ClassDefinition def = this.idenClass.getClassDefinition();
        //def.setInstance();
        GPRegister tmprg = compiler.getRegisterGestion().getGPRegisterLibre();
        compiler.addInstruction(new NEW(new ImmediateInteger(def.getNumberOfFields() + 1), tmprg));
        compiler.addInstruction(new BOV(new Label("HeapOverflowError")));
        compiler.addInstruction(new LEA(def.getOperand(), Register.R0));
        compiler.addInstruction(new STORE(Register.R0, new RegisterOffset(0, tmprg)));
        compiler.addInstruction(new PUSH(tmprg));
        compiler.addInstruction(new BSR(new LabelOperand(new Label("init." + this.idenClass.getName().getName()))));
        compiler.addInstruction(new POP(tmprg));
    }

    @Override
    protected void prettyPrintChildren(PrintStream s, String prefix) {
        idenClass.prettyPrint(s, prefix, true);
    }

    @Override
    protected void iterChildren(TreeFunction f) {
       // do nothing
    }
    
}
