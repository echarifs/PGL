/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package fr.ensimag.deca.tree;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.ClassType;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.EnvironmentExp;
import fr.ensimag.deca.context.EnvironmentType;
import fr.ensimag.deca.context.Type;
import fr.ensimag.deca.tools.IndentPrintStream;
import fr.ensimag.ima.pseudocode.DVal;
import fr.ensimag.ima.pseudocode.GPRegister;
import fr.ensimag.ima.pseudocode.ImmediateFloat;
import fr.ensimag.ima.pseudocode.ImmediateInteger;
import fr.ensimag.ima.pseudocode.Label;
import fr.ensimag.ima.pseudocode.Register;
import fr.ensimag.ima.pseudocode.RegisterOffset;
import fr.ensimag.ima.pseudocode.instructions.BOV;
import fr.ensimag.ima.pseudocode.instructions.FLOAT;
import fr.ensimag.ima.pseudocode.instructions.INT;
import fr.ensimag.ima.pseudocode.instructions.LEA;
import fr.ensimag.ima.pseudocode.instructions.LOAD;
import fr.ensimag.ima.pseudocode.instructions.NEW;
import fr.ensimag.ima.pseudocode.instructions.STORE;

import java.io.PrintStream;

/**
 *
 * @author gl01
 */
public class Cast extends AbstractExpr{
    
    private AbstractIdentifier type;
    private AbstractExpr e;

    public Cast(AbstractIdentifier type, AbstractExpr e) {
        this.type = type;
        this.e = e;
    }

    @Override
    public Type verifyExpr(DecacCompiler compiler, EnvironmentExp localEnv, ClassDefinition currentClass) throws ContextualError {
        Type typeToCast = type.verifyType(compiler);
        Type typeVar = e.verifyExpr(compiler, localEnv, currentClass);
        EnvironmentType typeEnv = compiler.getEnvironmentType();
        if (typeVar.isVoid()) {
            throw new ContextualError("Contextual error : A type to cast can't be void (regle 3.39) ", getLocation());
	    }
        if (!(assignCompatible(typeEnv, typeToCast, typeVar) || assignCompatible(typeEnv, typeVar, typeToCast))) {
            throw new ContextualError("Contextual error : The two types are not compatible to cast.", getLocation());
	    }
	    this.setType(typeToCast);
        return (this.getType());
    }

    @Override
    public void decompile(IndentPrintStream s) {
        s.print("(");
        type.decompile(s);
        s.print(") (");
        e.decompile(s);
        s.print(")");
    }

    @Override
    public void codeGenInst(DecacCompiler compiler)
    {
        GPRegister tmp = null;
        DVal rg;
        if (this.e.isIntLiteral())
            rg = new ImmediateInteger(((IntLiteral)this.e).getValue());
        else if (this.e.isFloatLiteral())
            rg = new ImmediateFloat(((FloatLiteral)this.e).getValue());
        else if (this.e.isIdentifier())
        {
            rg = compiler.getDefinition((Identifier)this.e).getOperand();
            if (rg == null)
            {
                tmp = compiler.getRegisterGestion().getGPRegisterLibre();
                compiler.addInstruction(new LOAD(new RegisterOffset(-2, Register.LB), tmp));
                rg = new RegisterOffset(((Identifier)this.e).getFieldDefinition().getIndex(), tmp);
            }
        }
        else if (this.e.isSelection())
            rg = ((Selection)this.e).getStorage(compiler);
        else
        {
            this.e.codeGenInst(compiler);
            if (this.e.isMethodCall())
                rg = Register.R0;
            else
            {
                rg = compiler.getRegisterGestion().getGPRegister();
                if (rg.equals(Register.getR(compiler.getCompilerOptions().getNbRegisters() - 1)) && !compiler.getRegisterGestion().containsGPRegisterLibre())
                    rg = new RegisterOffset(0, Register.SP);
            }
        }
        GPRegister tmprg = compiler.getRegisterGestion().getGPRegisterLibre();//Register.getR(compiler.useR());
        if (this.e.getType().isInt() && this.type.getType().isFloat())
            compiler.addInstruction(new FLOAT(rg, tmprg));
        else if (this.e.getType().isFloat() && this.type.getType().isInt())
            compiler.addInstruction(new INT(rg, tmprg));
        else
        {
            if (!this.e.getType().isClass())
                compiler.addInstruction(new LOAD(rg, tmprg));
            else
            {
                compiler.addInstruction(new LOAD(rg, Register.R1));
                compiler.addInstruction(new NEW(new ImmediateInteger(((ClassType)this.e.getType()).getDefinition().getNumberOfFields() + 1), tmprg));
                compiler.addInstruction(new BOV(new Label("HeapOverflowError")));
                compiler.addInstruction(new LEA(((Identifier)this.type).getClassDefinition().getOperand(), Register.R0));
                compiler.addInstruction(new STORE(Register.R0, new RegisterOffset(0, tmprg)));
                for (int index = 0; index < ((ClassType)this.e.getType()).getDefinition().getNumberOfFields(); index++)
                {
                    compiler.addInstruction(new LOAD(new RegisterOffset(index + 1, Register.R1), Register.R0));
                    compiler.addInstruction(new STORE(Register.R0, new RegisterOffset(index + 1, tmprg)));
                }
            }
        }
        if (rg.isR())
            compiler.getRegisterGestion().freeGPRegister((GPRegister)rg);//compiler.freeR();
        if (tmp != null)
            compiler.getRegisterGestion().freeGPRegister(tmp);
    }

    @Override
    protected void prettyPrintChildren(PrintStream s, String prefix) {
        type.prettyPrint(s, prefix, false);
        e.prettyPrint(s, prefix, true);
    }

    @Override
    protected void iterChildren(TreeFunction f) {
        // do nothing
    }
    
    public boolean assignCompatible(EnvironmentType env, Type T1, Type T2) throws ContextualError {
        boolean compatible = false;
        if (T1.sameType(T2)) {
            compatible = true;
        }
        else if (T1.isFloat() && T2.isInt()) {
            compatible = true;
        }
        else if (!T1.isClass() || !T2.isClass()) { // the coparasion should be between class
                compatible = false;
        }
        else {
            ClassType classT2 = env.get(T2.getName()).getType().asClassType("Contextual Error : type T2 can't be used as classType ", getLocation());
            ClassType classT1 = env.get(T1.getName()).getType().asClassType("Contextual Error : type T1 can't be used as classType ", getLocation());
            compatible = classT2.isSubClassOf(classT1);
        }
        return compatible;
    }
    
}
