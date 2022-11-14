package fr.ensimag.deca.tree;

import fr.ensimag.deca.context.Type;
import fr.ensimag.ima.pseudocode.DVal;
import fr.ensimag.ima.pseudocode.GPRegister;
import fr.ensimag.ima.pseudocode.ImmediateInteger;
import fr.ensimag.ima.pseudocode.Register;
import fr.ensimag.ima.pseudocode.RegisterOffset;
import fr.ensimag.ima.pseudocode.instructions.FLOAT;
import fr.ensimag.ima.pseudocode.instructions.LOAD;
import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.EnvironmentExp;
import fr.ensimag.deca.context.FloatType;
//import fr.ensimag.deca.context.IntType;

/**
 * Conversion of an int into a float. Used for implicit conversions.
 * 
 * @author gl01
 * @date 01/01/2022
 */
public class ConvFloat extends AbstractUnaryExpr {
    public ConvFloat(AbstractExpr operand) {
        super(operand);
    }

    @Override
    public Type verifyExpr(DecacCompiler compiler, EnvironmentExp localEnv,
            ClassDefinition currentClass) {
                
        Type new_float = new FloatType(compiler.getSymbols().create("float"));
        this.setType(new_float);
        return new_float;
         //throw new UnsupportedOperationException("not yet implemented");
    }

    // I added this one v

    @Override
    public void codeGenInst(DecacCompiler compiler)
    {
        GPRegister tmp = null;
        DVal rg;
        if (this.getOperand().isIntLiteral())
            rg = new ImmediateInteger(((IntLiteral)this.getOperand()).getValue());
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
        GPRegister tmprg = compiler.getRegisterGestion().getGPRegisterLibre();
        compiler.addInstruction(new FLOAT(rg, tmprg));
        if (rg.isR())
            compiler.getRegisterGestion().freeGPRegister((GPRegister)rg);
        if (tmp != null)
            compiler.getRegisterGestion().freeGPRegister(tmp);
    }

    // I added this one ^


    @Override
    protected String getOperatorName() {
        return "/* conv float */";
    }

}
