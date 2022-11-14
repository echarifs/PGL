package fr.ensimag.deca.tree;

import fr.ensimag.deca.context.Type;
import fr.ensimag.ima.pseudocode.DAddr;
import fr.ensimag.ima.pseudocode.DVal;
import fr.ensimag.ima.pseudocode.GPRegister;
import fr.ensimag.ima.pseudocode.ImmediateFloat;
import fr.ensimag.ima.pseudocode.ImmediateInteger;
import fr.ensimag.ima.pseudocode.Register;
import fr.ensimag.ima.pseudocode.RegisterOffset;
import fr.ensimag.ima.pseudocode.instructions.LOAD;
import fr.ensimag.ima.pseudocode.instructions.STORE;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.ContextualError;
//import fr.ensimag.deca.context.Definition;
import fr.ensimag.deca.context.EnvironmentExp;

/**
 * Assignment, i.e. lvalue = expr.
 *
 * @author gl01
 * @date 01/01/2022
 */
public class Assign extends AbstractBinaryExpr {

    @Override
    public AbstractLValue getLeftOperand() {
        // The cast succeeds by construction, as the leftOperand has been set
        // as an AbstractLValue by the constructor.
        return (AbstractLValue)super.getLeftOperand();
    }

    public Assign(AbstractLValue leftOperand, AbstractExpr rightOperand) {
        super(leftOperand, rightOperand);
    }

    @Override
    public Type verifyExpr(DecacCompiler compiler, EnvironmentExp localEnv,
            ClassDefinition currentClass) throws ContextualError {
        //throw new UnsupportedOperationException("not yet implemented");
        Type LeftOperandType = this.getLeftOperand().verifyExpr(compiler, localEnv, currentClass);
        AbstractExpr RightOperand = this.getRightOperand().verifyRValue(compiler, localEnv, currentClass, LeftOperandType);
	    this.setRightOperand(RightOperand);
	    this.setType(LeftOperandType);
        //localEnv.get(((Identifier)this.getLeftOperand()).getName()).setInitialization();
        return LeftOperandType;
    }

    // I added this one v

    @Override
    public void codeGenInst(DecacCompiler compiler)
    {
        GPRegister tmp1 = null, tmp2 = null;
        DVal rg1, rg2;
        if (this.getLeftOperand().isIdentifier())
        {
            rg1 = compiler.getDefinition((Identifier)this.getLeftOperand()).getOperand();
            if (rg1 == null)
            {
                tmp1 = compiler.getRegisterGestion().getGPRegisterLibre();
                compiler.addInstruction(new LOAD(new RegisterOffset(-2, Register.LB), tmp1));
                rg1 = new RegisterOffset(((Identifier)this.getLeftOperand()).getFieldDefinition().getIndex(), tmp1);
            }
        }
        else
            rg1 = ((Selection)this.getLeftOperand()).getStorage(compiler);
        if (this.getRightOperand().isIntLiteral())
            rg2 = new ImmediateInteger(((IntLiteral)this.getRightOperand()).getValue());
        else if (this.getRightOperand().isFloatLiteral())
            rg2 = new ImmediateFloat(((FloatLiteral)this.getRightOperand()).getValue());
        else if (this.getRightOperand().isBooleanLiteral())
        {
            Boolean value = ((BooleanLiteral)this.getRightOperand()).getValue();
            rg2 = value ? new ImmediateInteger(1) : new ImmediateInteger(0);
        }
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
                    rg2 = new RegisterOffset(0, Register.SP);
            }
        }
        GPRegister tmprg = compiler.getRegisterGestion().getGPRegisterLibre();//Register.getR(compiler.useR());
        compiler.addInstruction(new LOAD(rg2, tmprg));
        compiler.addInstruction(new STORE(tmprg, (DAddr)rg1));
        //compiler.getRegisterGestion().freeGPRegister(tmprg);
        if (rg2.isR())
            compiler.getRegisterGestion().freeGPRegister((GPRegister)rg2);//compiler.freeR();
        if (tmp1 != null)
            compiler.getRegisterGestion().freeGPRegister(tmp1);
        if (tmp2 != null)
            compiler.getRegisterGestion().freeGPRegister(tmp2);
    }

    // I added this one ^


    @Override
    protected String getOperatorName() {
        return "=";
    }

}
