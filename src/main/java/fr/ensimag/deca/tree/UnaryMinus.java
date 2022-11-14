package fr.ensimag.deca.tree;

import fr.ensimag.deca.context.Type;
import fr.ensimag.ima.pseudocode.DVal;
import fr.ensimag.ima.pseudocode.GPRegister;
import fr.ensimag.ima.pseudocode.ImmediateFloat;
import fr.ensimag.ima.pseudocode.ImmediateInteger;
import fr.ensimag.ima.pseudocode.Register;
import fr.ensimag.ima.pseudocode.RegisterOffset;
import fr.ensimag.ima.pseudocode.instructions.LOAD;
import fr.ensimag.ima.pseudocode.instructions.SUB;
import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.EnvironmentExp;

/**
 * @author gl01
 * @date 01/01/2022
 */
public class UnaryMinus extends AbstractUnaryExpr {

    public UnaryMinus(AbstractExpr operand) {
        super(operand);
    }

    @Override
    public Type verifyExpr(DecacCompiler compiler, EnvironmentExp localEnv,
            ClassDefinition currentClass) throws ContextualError {
        //throw new UnsupportedOperationException("not yet implemented");
        Type type = getOperand().verifyExpr(compiler, localEnv, currentClass);
        if(!(type.isInt() || type.isFloat())){
            throw new ContextualError("can't apply 'minus' to a " + type.getName(), getLocation());
        }
        this.setType(type);
        return type;
    }


    @Override
    protected String getOperatorName() {
        return "-";
    }

    // I added this one v

    @Override
    public void codeGenInst(DecacCompiler compiler)
    {
        GPRegister tmp = null;
        DVal rg;
        if (this.getOperand().isIntLiteral())
            rg = new ImmediateInteger(((IntLiteral)this.getOperand()).getValue());
        else if (this.getOperand().isFloatLiteral())
            rg = new ImmediateFloat(((FloatLiteral)this.getOperand()).getValue());
        else if (this.getOperand().isIdentifier())
        {
            rg = compiler.getDefinition((Identifier)this.getOperand()).getOperand();
            if (rg == null)
            {
                tmp = compiler.getRegisterGestion().getGPRegisterLibre();
                compiler.addInstruction(new LOAD(new RegisterOffset(-2, Register.LB), tmp));
                rg = new RegisterOffset(((Identifier)this.getOperand()).getFieldDefinition().getIndex(), tmp);
            }
        }
        else if (this.getOperand().isSelection())
            rg = ((Selection)this.getOperand()).getStorage(compiler);
        else
        {
            this.getOperand().codeGenInst(compiler);
            if (this.getOperand().isMethodCall())
                rg = Register.R0;
            else
            {
                rg = compiler.getRegisterGestion().getGPRegister();
                if (rg.equals(Register.getR(compiler.getCompilerOptions().getNbRegisters() - 1)) && !compiler.getRegisterGestion().containsGPRegisterLibre())
                    rg = new RegisterOffset(0, Register.SP);
            }
        }
        GPRegister tmprg = compiler.getRegisterGestion().getGPRegisterLibre();//Register.getR(compiler.useR());
        if (this.getOperand().getType().isInt())
            compiler.addInstruction(new LOAD(new ImmediateInteger(0), tmprg));
        else
            compiler.addInstruction(new LOAD(new ImmediateFloat(0), tmprg));
        compiler.addInstruction(new SUB(rg, tmprg));
        if (rg.isR())
            compiler.getRegisterGestion().freeGPRegister((GPRegister)rg);//compiler.freeR();
        if (tmp != null)
            compiler.getRegisterGestion().freeGPRegister(tmp);
    }

    // I added this one ^

}
