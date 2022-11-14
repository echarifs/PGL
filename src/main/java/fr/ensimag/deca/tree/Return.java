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
import fr.ensimag.ima.pseudocode.DVal;
import fr.ensimag.ima.pseudocode.GPRegister;
import fr.ensimag.ima.pseudocode.ImmediateFloat;
import fr.ensimag.ima.pseudocode.ImmediateInteger;
import fr.ensimag.ima.pseudocode.Label;
import fr.ensimag.ima.pseudocode.Register;
import fr.ensimag.ima.pseudocode.RegisterOffset;
import fr.ensimag.ima.pseudocode.instructions.BRA;
import fr.ensimag.ima.pseudocode.instructions.LOAD;
//import fr.ensimag.ima.pseudocode.instructions.RTS;

import java.io.PrintStream;


/**
 *
 * @author gl01
 */
public class Return extends AbstractInst
{
    
    private AbstractExpr e;
    
    public Return(AbstractExpr e) {
        this.e = e;
    }

    @Override
    protected void verifyInst(DecacCompiler compiler, EnvironmentExp localEnv, ClassDefinition currentClass, Type returnType) throws ContextualError {
        if (returnType.isVoid()) {
            throw new ContextualError("Contextual error: Can't return in a function void (regle 3.24)", getLocation());
        }
        Type typ = this.e.verifyExpr(compiler, localEnv, currentClass);
        if (!typ.sameType(returnType)) {
            throw new ContextualError("Contextual error: The type of 'return' in current class doesn't match the type demanded ",this.e.getLocation());
        }
        
        //Type typ = this.e.verifyRValue(compiler, localEnv, currentClass, returnType).getType();
        //e.setType(typ);
        compiler.setReturn(true);
        //System.out.println("To add a notice showing that we should return sth in method.");
    }

    @Override
    protected void codeGenInst(DecacCompiler compiler) {
        GPRegister tmp = null;
        DVal rg;
        if (this.e.isIntLiteral())
            rg = new ImmediateInteger(((IntLiteral)this.e).getValue());
        else if (this.e.isFloatLiteral())
            rg = new ImmediateFloat(((FloatLiteral)this.e).getValue());
        else if (this.e.isBooleanLiteral())
        {
            Boolean value = ((BooleanLiteral)this.e).getValue();
            rg = value ? new ImmediateInteger(1) : new ImmediateInteger(0);
        }
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
        else if (e.isSelection())
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
        compiler.addInstruction(new LOAD(rg, Register.R0));
        compiler.addInstruction(new BRA(new Label("end." + compiler.getMethLabel().toString())));
        if (rg.isR())
            compiler.getRegisterGestion().freeGPRegister((GPRegister)rg);
        if (tmp != null)
            compiler.getRegisterGestion().freeGPRegister(tmp);
    }

    @Override
    public void decompile(IndentPrintStream s) {
        s.print("return ");
        e.decompile(s);
        s.print(";");
    }

    @Override
    protected void prettyPrintChildren(PrintStream s, String prefix) {
        e.prettyPrint(s, prefix, true);
    }

    @Override
    protected void iterChildren(TreeFunction f) {
        // do nothing
    }

    // I added this one v

    @Override
    public boolean isReturn()
    {
        return true;
    }

    // I added this one ^
    
}
