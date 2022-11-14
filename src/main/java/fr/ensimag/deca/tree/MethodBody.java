/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package fr.ensimag.deca.tree;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.EnvironmentExp;
//import fr.ensimag.deca.context.ExpDefinition;
import fr.ensimag.deca.context.Type;
import fr.ensimag.deca.tools.IndentPrintStream;
import fr.ensimag.ima.pseudocode.DVal;
import fr.ensimag.ima.pseudocode.GPRegister;
import fr.ensimag.ima.pseudocode.ImmediateFloat;
import fr.ensimag.ima.pseudocode.ImmediateInteger;
import fr.ensimag.ima.pseudocode.Register;
import fr.ensimag.ima.pseudocode.RegisterOffset;
import fr.ensimag.ima.pseudocode.instructions.LOAD;
import fr.ensimag.ima.pseudocode.instructions.STORE;

import java.io.PrintStream;
//import java.util.logging.Level;
//import java.util.logging.Logger;

/**
 *
 * @author gl01
 */
public class MethodBody extends AbstractMethodBody
{
    private ListDeclVar vars;
    private ListInst insts;
    
    public MethodBody(ListDeclVar vars, ListInst insts) {
        this.vars = vars;
        this.insts = insts;
    }

    @Override
    public void decompile(IndentPrintStream s) {
        s.print(" {");
        vars.decompile(s);
        insts.decompile(s);
        s.println("}");
    }

    @Override
    protected void prettyPrintChildren(PrintStream s, String prefix) {
        this.vars.prettyPrint(s, prefix, false);
	    this.insts.prettyPrint(s, prefix, true);
    }

    @Override
    protected void iterChildren(TreeFunction f) {
        this.vars.iter(f);
	    this.insts.iter(f);
    }


    @Override
    protected void codeGenMethodBody(DecacCompiler compiler)
    {
        int integer = 0;
        for (AbstractDeclVar index : this.vars.getList())
        {
            GPRegister tmp = null;
            DeclVar tmpv = (DeclVar)index;
            RegisterOffset rgtmp = null;
            if (tmpv.getInit().isInitialized())
            {
                Initialization tmpInit = (Initialization)tmpv.getInit();
                DVal rg;
                if (tmpInit.getExpression().isIntLiteral())
                    rg = new ImmediateInteger(((IntLiteral)tmpInit.getExpression()).getValue());
                else if (tmpInit.getExpression().isFloatLiteral())
                    rg = new ImmediateFloat(((FloatLiteral)tmpInit.getExpression()).getValue());
                else if (tmpInit.getExpression().isBooleanLiteral())
                {
                    Boolean value = ((BooleanLiteral)tmpInit.getExpression()).getValue();
                    rg = value ? new ImmediateInteger(1) : new ImmediateInteger(0);
                }
                else if (tmpInit.getExpression().isIdentifier())
                {
                    rg = compiler.getDefinition((Identifier)tmpInit.getExpression()).getOperand();
                    if (rg == null)
                    {
                        tmp = compiler.getRegisterGestion().getGPRegisterLibre();
                        compiler.addInstruction(new LOAD(new RegisterOffset(-2, Register.LB), tmp));
                        rg = new RegisterOffset(((Identifier)tmpInit.getExpression()).getFieldDefinition().getIndex(), tmp);
                    }
                }
                else if (tmpInit.getExpression().isSelection())
                    rg = ((Selection)tmpInit.getExpression()).getStorage(compiler);
                else
                {
                    tmpInit.getExpression().codeGenInst(compiler);
                    if (tmpInit.getExpression().isMethodCall())
                        rg = Register.R0;
                    else
                    {
                        rg = compiler.getRegisterGestion().getGPRegister();
                        if (rg.equals(Register.getR(compiler.getCompilerOptions().getNbRegisters() - 1)) && !compiler.getRegisterGestion().containsGPRegisterLibre())
                            rg = new RegisterOffset(0, Register.SP);
                    }
                }
                rgtmp = new RegisterOffset(compiler.getRegisterGestion().getLocalOffset() - integer, Register.LB);
                GPRegister tmprg = compiler.getRegisterGestion().getGPRegisterLibre();
                compiler.addInstruction(new LOAD(rg, tmprg));
                compiler.addInstruction(new STORE(tmprg, rgtmp));
                compiler.getRegisterGestion().freeGPRegister(tmprg);
                if (rg.isR())
                    compiler.getRegisterGestion().freeGPRegister((GPRegister)rg);
                if (tmp != null)
                    compiler.getRegisterGestion().freeGPRegister(tmp);
            }
            if (rgtmp == null)
                rgtmp = new RegisterOffset(compiler.getRegisterGestion().getLocalOffset() - integer, Register.LB);
            compiler.getDefinition((Identifier)tmpv.getvarName()).setOperand(rgtmp);
            /*Identifier x = (Identifier)tmp.getvarName();
            ExpDefinition xdef = x.getExpDefinition();
            xdef.setOperand(rgtmp);
            compiler.declare(x, xdef);*/
            integer++;
        }
        this.insts.codeGenListInst(compiler);
    }


    @Override
    protected void verifyMethodBody(DecacCompiler compiler, ClassDefinition currentClass,
                EnvironmentExp envMethod, Type typRetour) throws ContextualError {
        //throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
        // TO DEFINE RETURN
        /*
        this.vars.verifyListDeclVariable(compiler, envMethod, currentClass);
        this.insts.verifyListInst(compiler, envMethod, currentClass, typRetour);d
        int integer = 0;
        for (AbstractInst index : this.insts.getList())
            if (index.isReturn())
            {
                integer++;
                break;
            }
        if (integer < 1 && !typRetour.isVoid())
            throw new ContextualError("Contextual error : Return value is expected is this method", this.getLocation());
        */
        
        compiler.setReturn(false);
        this.vars.verifyListDeclVariable(compiler, envMethod, currentClass);
        this.insts.verifyListInst(compiler, envMethod, currentClass, typRetour);
        
        // if(!compiler.getReturn() && !typRetour.isVoid()){
        //     throw new ContextualError("Contextual error : Return value is expected in this method (regle 3.24) ", this.getLocation());
        // }

    }
}
