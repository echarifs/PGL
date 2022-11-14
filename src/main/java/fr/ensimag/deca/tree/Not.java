package fr.ensimag.deca.tree;

import fr.ensimag.deca.context.Type;
import fr.ensimag.ima.pseudocode.DVal;
import fr.ensimag.ima.pseudocode.GPRegister;
import fr.ensimag.ima.pseudocode.ImmediateInteger;
import fr.ensimag.ima.pseudocode.Label;
import fr.ensimag.ima.pseudocode.Register;
import fr.ensimag.ima.pseudocode.RegisterOffset;
import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.codegen.LabelGestion;
import fr.ensimag.deca.codegen.RegisterGestion;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.EnvironmentExp;
import fr.ensimag.ima.pseudocode.instructions.*;


/**
 *
 * @author gl01
 * @date 01/01/2022
 */
public class Not extends AbstractUnaryExpr {

    public Not(AbstractExpr operand) {
        super(operand);
    }

    @Override
    public Type verifyExpr(DecacCompiler compiler, EnvironmentExp localEnv,
            ClassDefinition currentClass) throws ContextualError {
        //throw new UnsupportedOperationException("not yet implemented");
        Type type = getOperand().verifyExpr(compiler, localEnv, currentClass);
        if(!type.isBoolean()){
            throw new ContextualError("can't apply 'not' to a " + type.getName(), getLocation());
        }
        this.setType(type);
        return type;
    }

    // I added this one v

    @Override
    public void codeGenInst(DecacCompiler compiler)
    {
        GPRegister tmp = null;
        LabelGestion labelHandler = compiler.getLabGestion();
        RegisterGestion registerHandler = compiler.getRegisterGestion();
        DVal rg;
        if (this.getOperand().isBooleanLiteral())
        {
            Boolean value = ((BooleanLiteral)this.getOperand()).getValue();
            rg = value ? new ImmediateInteger(1) : new ImmediateInteger(0);      
        }
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
        GPRegister tmprg = registerHandler.getGPRegisterLibre();
        Label FalseLabel = labelHandler.getFalseLabel();
        Label EndCMPLabel = labelHandler.getEndCMPLabel();
        compiler.addInstruction(new LOAD(rg, tmprg));
        compiler.addInstruction(new CMP(1, tmprg));
        compiler.addInstruction(new BEQ(FalseLabel));
        compiler.addInstruction(new LOAD(new ImmediateInteger(1), tmprg));
        compiler.addInstruction(new BRA(EndCMPLabel));
        compiler.addLabel(FalseLabel);
        compiler.addInstruction(new LOAD(new ImmediateInteger(0), tmprg));
        compiler.addLabel(EndCMPLabel);
        if (rg.isR())
            registerHandler.freeGPRegister((GPRegister)rg);
        if (tmp != null)
            compiler.getRegisterGestion().freeGPRegister(tmp);
    }

    // I added this one ^

    @Override
    protected String getOperatorName() {
        return "!";
    }
}
