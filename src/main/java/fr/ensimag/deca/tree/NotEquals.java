package fr.ensimag.deca.tree;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.codegen.LabelGestion;
import fr.ensimag.deca.codegen.RegisterGestion;
import fr.ensimag.ima.pseudocode.DVal;
import fr.ensimag.ima.pseudocode.GPRegister;
import fr.ensimag.ima.pseudocode.ImmediateFloat;
import fr.ensimag.ima.pseudocode.ImmediateInteger;
import fr.ensimag.ima.pseudocode.Label;
import fr.ensimag.ima.pseudocode.Register;
import fr.ensimag.ima.pseudocode.RegisterOffset;
import fr.ensimag.ima.pseudocode.instructions.BEQ;
import fr.ensimag.ima.pseudocode.instructions.BRA;
import fr.ensimag.ima.pseudocode.instructions.CMP;
import fr.ensimag.ima.pseudocode.instructions.LOAD;

/**
 *
 * @author gl01
 * @date 01/01/2022
 */
public class NotEquals extends AbstractOpExactCmp {

    public NotEquals(AbstractExpr leftOperand, AbstractExpr rightOperand) {
        super(leftOperand, rightOperand);
    }


    @Override
    protected String getOperatorName() {
        return "!=";
    }

    // I added this one v

    @Override
    public void codeGenInst(DecacCompiler compiler)
    {
        GPRegister tmp1 = null, tmp2 = null;
        int value1 = 0, value2 = 0;
        LabelGestion labelHandler = compiler.getLabGestion();
        RegisterGestion registerHandler = compiler.getRegisterGestion();
        DVal rg1, rg2;
        if (this.getLeftOperand().isIntLiteral())
            rg1 = new ImmediateInteger(((IntLiteral)this.getLeftOperand()).getValue());
        else if (this.getLeftOperand().isFloatLiteral())
            rg1 = new ImmediateFloat(((FloatLiteral)this.getLeftOperand()).getValue());
        else if (this.getLeftOperand().isIdentifier())
        {
            rg1 = compiler.getDefinition((Identifier)this.getLeftOperand()).getOperand();
            if (rg1 == null)
            {
                tmp1 = compiler.getRegisterGestion().getGPRegisterLibre();
                compiler.addInstruction(new LOAD(new RegisterOffset(-2, Register.LB), tmp1));
                rg1 = new RegisterOffset(((Identifier)this.getLeftOperand()).getFieldDefinition().getIndex(), tmp1);
            }
        }
        else if (this.getLeftOperand().isSelection())
            rg1 = ((Selection)this.getLeftOperand()).getStorage(compiler);
        else
        {
            this.getLeftOperand().codeGenInst(compiler);
            if (this.getLeftOperand().isMethodCall())
                rg1 = Register.R0;
            else
            {
                rg1 = compiler.getRegisterGestion().getGPRegister();
                if (rg1.equals(Register.getR(compiler.getCompilerOptions().getNbRegisters() - 1)) && !compiler.getRegisterGestion().containsGPRegisterLibre())
                    value1++;
            }
        }
        if (this.getRightOperand().isIntLiteral())
            rg2 = new ImmediateInteger(((IntLiteral)this.getRightOperand()).getValue());
        else if (this.getRightOperand().isFloatLiteral())
            rg2 = new ImmediateFloat(((FloatLiteral)this.getRightOperand()).getValue());
        else if (this.getRightOperand().isIdentifier())
        {
            rg2 = compiler.getDefinition((Identifier)this.getRightOperand()).getOperand();
            if (rg2 == null)
            {
                tmp2 = compiler.getRegisterGestion().getGPRegisterLibre();
                compiler.addInstruction(new LOAD(new RegisterOffset(-2, Register.LB), tmp2));
                rg2 = new RegisterOffset(((Identifier)this.getRightOperand()).getFieldDefinition().getIndex(), tmp2);
            }
        }
        else if (this.getRightOperand().isSelection())
            rg2 = ((Selection)this.getRightOperand()).getStorage(compiler);
        else
        {
            this.getRightOperand().codeGenInst(compiler);
            if (this.getRightOperand().isMethodCall())
                rg2 = Register.R0;
            else
            {
                rg2 = compiler.getRegisterGestion().getGPRegister();
                if (rg2.equals(Register.getR(compiler.getCompilerOptions().getNbRegisters() - 1)) && !compiler.getRegisterGestion().containsGPRegisterLibre())
                    value2++;
            }
        }
        if (value1 + value2 == 2)
        {
            rg2 = new RegisterOffset(0, Register.SP);
            rg1 = new RegisterOffset(-1, Register.SP);
        }
        else if (value1 == 1)
            rg1 = new RegisterOffset(0, Register.SP);
        else if (value2 == 1)
            rg2 = new RegisterOffset(0, Register.SP);
        GPRegister tmprg = registerHandler.getGPRegisterLibre();//Register.getR(compiler.useR());
        Label FalseLabel = labelHandler.getFalseLabel();
        Label EndCMPLabel = labelHandler.getEndCMPLabel();
        compiler.addInstruction(new LOAD(rg1, tmprg));
        compiler.addInstruction(new CMP(rg2, tmprg));
        compiler.addInstruction(new BEQ(FalseLabel));
        compiler.addInstruction(new LOAD(new ImmediateInteger(1), tmprg));
        compiler.addInstruction(new BRA(EndCMPLabel));
        compiler.addLabel(FalseLabel);
        compiler.addInstruction(new LOAD(new ImmediateInteger(0), tmprg));
        compiler.addLabel(EndCMPLabel);
        if (rg1.isR())
            compiler.getRegisterGestion().freeGPRegister((GPRegister)rg1);//compiler.freeR();
        if (rg2.isR())
            compiler.getRegisterGestion().freeGPRegister((GPRegister)rg2);//compiler.freeR();
        if (tmp1 != null)
            compiler.getRegisterGestion().freeGPRegister(tmp1);
        if (tmp2 != null)
            compiler.getRegisterGestion().freeGPRegister(tmp2);
    }

    // I added this one ^

}
