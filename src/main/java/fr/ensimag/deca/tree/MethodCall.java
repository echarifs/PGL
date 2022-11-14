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
//import fr.ensimag.deca.context.ExpDefinition;
import fr.ensimag.deca.context.MethodDefinition;
import fr.ensimag.deca.context.Signature;
import fr.ensimag.deca.context.Type;
import fr.ensimag.deca.tools.IndentPrintStream;
//import fr.ensimag.deca.tools.SymbolTable.Symbol;
import fr.ensimag.ima.pseudocode.DVal;
import fr.ensimag.ima.pseudocode.GPRegister;
import fr.ensimag.ima.pseudocode.ImmediateFloat;
import fr.ensimag.ima.pseudocode.ImmediateInteger;
import fr.ensimag.ima.pseudocode.Label;
import fr.ensimag.ima.pseudocode.NullOperand;
//import fr.ensimag.ima.pseudocode.LabelOperand;
import fr.ensimag.ima.pseudocode.Register;
import fr.ensimag.ima.pseudocode.RegisterOffset;
//import fr.ensimag.ima.pseudocode.instructions.ADDSP;
import fr.ensimag.ima.pseudocode.instructions.BEQ;
//import fr.ensimag.ima.pseudocode.instructions.BOV;
import fr.ensimag.ima.pseudocode.instructions.BSR;
import fr.ensimag.ima.pseudocode.instructions.CMP;
import fr.ensimag.ima.pseudocode.instructions.LOAD;
//import fr.ensimag.ima.pseudocode.instructions.RTS;
import fr.ensimag.ima.pseudocode.instructions.STORE;
//import fr.ensimag.ima.pseudocode.instructions.TSTO;

import java.io.PrintStream;
//import java.util.Map;

/**
 *
 * @author gl01
 */
public class MethodCall extends AbstractExpr
{
    private AbstractExpr obj;
    private AbstractIdentifier meth;
    private ListExpr params;

    public MethodCall(AbstractExpr obj, AbstractIdentifier meth, ListExpr params) {
        this.obj = obj;
	    this.meth = meth;
	    this.params = params;
    }

    @Override
    public Type verifyExpr(DecacCompiler compiler, EnvironmentExp localEnv, ClassDefinition currentClass) throws ContextualError {
        Type typ = this.obj.verifyExpr(compiler, localEnv, currentClass);
        ClassType typeObj = typ.asClassType("Contextual error : The object part should be as type ClassType while calling a method", getLocation());
	    EnvironmentExp envObj = typeObj.getDefinition().getMembers();
        this.meth.verifyExpr(compiler, envObj, currentClass);
        MethodDefinition definitionMtd = envObj.get(this.meth.getName()).asMethodDefinition("Contextual error : signature", getLocation());
        
        Signature sigature = definitionMtd.getSignature();
        
        // TO FINISH
        
        if (sigature.size() != params.getList().size()) {
            throw new ContextualError("Contextual error : The number of arguments for the method is not correct (regle 3.71)", getLocation());
        }
        for (int i = 0; i < sigature.size(); i++) {
            Type typeSignature = sigature.getArgs().get(i);
            Type typeParams = params.getList().get(i).verifyExpr(compiler, localEnv, currentClass);
            if (!typeSignature.sameType(typeParams)) {
                throw new ContextualError("Contextual error : he type of parameter dont conrespond to the signature of method (regle 3.71)", getLocation());
            }
            // when we handle class, they dont need to be the same signature but subclass
            if (typeSignature.isClass() && 
                    !typeParams.asClassType("Contextual error : The type of paramater is not a class ", getLocation())
                        .isSubClassOf(typeSignature.asClassType("Contextual error : The type of signature is not a class ", getLocation()))) {
                throw new ContextualError("Contextual error : The type of parameter dont conrespond to the signature of method (regle 3.71)",getLocation());
            } 
        }
        this.setType(envObj.get(this.meth.getName()).getType());
        return (this.getType());        
    }

    @Override
    public void decompile(IndentPrintStream s) {
        obj.decompile(s);
        s.print(".");
        meth.decompile(s);
        s.print("(");
        params.decompile(s);
        s.print(")");
    }

    @Override
    protected void prettyPrintChildren(PrintStream s, String prefix) {
       	obj.prettyPrint(s, prefix, false);
        meth.prettyPrint(s, prefix, false);
        params.prettyPrint(s, prefix, true);
    }

    // I added this one v

    @Override
    public boolean isMethodCall()
    {
        return true;
    }

    @Override
    protected void codeGenInst(DecacCompiler compiler)
    {
        int counter = 1;
        GPRegister tmp = null;
        DVal rg, CallerOperand;
        compiler.getRegisterGestion().incADDSP(this.params.size() + 1);
        /*compiler.addInstruction(new TSTO(new ImmediateInteger(this.params.size() + 1)));
        compiler.addInstruction(new BOV(new Label("StackOverflowError")));
        compiler.addInstruction(new ADDSP(new ImmediateInteger(this.params.size() + 1)));*/
        GPRegister tmprg = compiler.getRegisterGestion().getGPRegisterLibre();
        if (this.obj.isIdentifier())
        {
            CallerOperand = compiler.getDefinition(((Identifier)this.obj)).getOperand();
            if (CallerOperand == null)
            {
                tmp = compiler.getRegisterGestion().getGPRegisterLibre();
                compiler.addInstruction(new LOAD(new RegisterOffset(-2, Register.LB), tmp));
                CallerOperand = new RegisterOffset(((Identifier)this.obj).getFieldDefinition().getIndex(), tmp);
            }
        }
        else if (this.obj.isThis())
            CallerOperand = new RegisterOffset(-2, Register.LB);
        else if (this.obj.isSelection())
            CallerOperand = ((Selection)this.obj).getStorage(compiler);
        else
        {
            this.obj.codeGenInst(compiler);
            if (this.obj.isMethodCall())
                CallerOperand = Register.R0;
            else
            {
                CallerOperand = compiler.getRegisterGestion().getGPRegister();
                if (CallerOperand.equals(Register.getR(compiler.getCompilerOptions().getNbRegisters() - 1)) && !compiler.getRegisterGestion().containsGPRegisterLibre())
                    CallerOperand = new RegisterOffset(0, Register.SP);
            }
        }
        compiler.addInstruction(new LOAD(CallerOperand, tmprg));
        compiler.addInstruction(new STORE(tmprg, new RegisterOffset(0, Register.SP)));
        if (CallerOperand.isR())
            compiler.getRegisterGestion().freeGPRegister((GPRegister)CallerOperand);
        if (tmp != null)
            compiler.getRegisterGestion().freeGPRegister(tmp);
        for (AbstractExpr index : this.params.getList())
        {
            if (index.isIntLiteral())
                rg = new ImmediateInteger(((IntLiteral)index).getValue());
            else if (index.isFloatLiteral())
                rg = new ImmediateFloat(((FloatLiteral)index).getValue());
            else if (index.isBooleanLiteral())
            {
                Boolean value = ((BooleanLiteral)index).getValue();
                rg = value ? new ImmediateInteger(1) : new ImmediateInteger(0);
            }
            else if (index.isIdentifier())
                rg = compiler.getDefinition((Identifier)index).getOperand();
            else
            {
                index.codeGenInst(compiler);
                rg = compiler.getRegisterGestion().getGPRegister();
                if (rg.equals(Register.getR(compiler.getCompilerOptions().getNbRegisters() - 1)) && !compiler.getRegisterGestion().containsGPRegisterLibre())
                    rg = new RegisterOffset(0, Register.SP);
            }
            compiler.addInstruction(new LOAD(rg, tmprg));
            compiler.addInstruction(new STORE(tmprg, new RegisterOffset(-counter++, Register.SP)));
            if (rg.isR())
                compiler.getRegisterGestion().freeGPRegister((GPRegister)rg);//compiler.freeR();
        }
        compiler.addInstruction(new LOAD(new RegisterOffset(0, Register.SP), tmprg));
        compiler.addInstruction(new CMP(new NullOperand(), tmprg));
        compiler.addInstruction(new BEQ(new Label("NullObjectError")));
        compiler.addInstruction(new LOAD(new RegisterOffset(0, tmprg), tmprg));
        compiler.addInstruction(new BSR(new RegisterOffset(this.meth.getMethodDefinition().getIndex(), tmprg)));
    }

    // I added this one ^

    @Override
    protected void iterChildren(TreeFunction f) {
        
    }

    
}
