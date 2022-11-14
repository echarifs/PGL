package fr.ensimag.deca.tree;

import fr.ensimag.deca.context.Type;
import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.codegen.LabelGestion;
//import fr.ensimag.deca.codegen.RegisterGestion;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.EnvironmentExp;
import fr.ensimag.deca.tools.IndentPrintStream;
import fr.ensimag.ima.pseudocode.DVal;
import fr.ensimag.ima.pseudocode.GPRegister;
import fr.ensimag.ima.pseudocode.ImmediateInteger;
import fr.ensimag.ima.pseudocode.Label;
import fr.ensimag.ima.pseudocode.Register;
import fr.ensimag.ima.pseudocode.RegisterOffset;
import fr.ensimag.ima.pseudocode.instructions.*;
import java.io.PrintStream;
import org.apache.commons.lang.Validate;

/**
 * Full if/else if/else statement.
 *
 * @author gl01
 * @date 01/01/2022
 */
public class IfThenElse extends AbstractInst {
    
    private final AbstractExpr condition; 
    private final ListInst thenBranch;
    private ListInst elseBranch;

    public IfThenElse(AbstractExpr condition, ListInst thenBranch, ListInst elseBranch) {
        Validate.notNull(condition);
        Validate.notNull(thenBranch);
        Validate.notNull(elseBranch);
        this.condition = condition;
        this.thenBranch = thenBranch;
        this.elseBranch = elseBranch;
    }
    
    @Override
    protected void verifyInst(DecacCompiler compiler, EnvironmentExp localEnv,
            ClassDefinition currentClass, Type returnType)
            throws ContextualError {
        this.condition.verifyCondition(compiler, localEnv, currentClass);
        this.thenBranch.verifyListInst(compiler, localEnv, currentClass, returnType);
        this.elseBranch.verifyListInst(compiler, localEnv, currentClass, returnType);
    }

    @Override
    protected void codeGenInst(DecacCompiler compiler)
    {
        GPRegister tmp = null;
        DVal rg;
        LabelGestion labelHandler = compiler.getLabGestion();
        Label EndIfLabel = labelHandler.getEndIfLabel();
        Label ElseLabel = labelHandler.getElseLabel();
        if (this.condition.isBooleanLiteral())
        {
            Boolean value = ((BooleanLiteral)this.condition).getValue();
            rg = value ? new ImmediateInteger(1) : new ImmediateInteger(0);
        }
        else if (this.condition.isIdentifier())
        {
            rg = compiler.getDefinition((Identifier)this.condition).getOperand();
            if (rg == null)
            {
                tmp = compiler.getRegisterGestion().getGPRegisterLibre();
                compiler.addInstruction(new LOAD(new RegisterOffset(-2, Register.LB), tmp));
                rg = new RegisterOffset(((Identifier)this.condition).getFieldDefinition().getIndex(), tmp);
            }
        }
        else if (this.condition.isSelection())
            rg = ((Selection)this.condition).getStorage(compiler);
        else
        {
            this.condition.codeGenInst(compiler);
            if (this.condition.isMethodCall())
                rg = Register.R0;
            else
            {
                rg = compiler.getRegisterGestion().getGPRegister();
                if (rg.equals(Register.getR(compiler.getCompilerOptions().getNbRegisters() - 1)) && !compiler.getRegisterGestion().containsGPRegisterLibre())
                    rg = new RegisterOffset(0, Register.SP);
            }
        }
        GPRegister tmprg = compiler.getRegisterGestion().getGPRegisterLibre();//Register.getR(compiler.useR());
        compiler.addInstruction(new LOAD(new ImmediateInteger(1), tmprg));
        compiler.addInstruction(new CMP(rg, tmprg));
        compiler.addInstruction(new BNE(ElseLabel));
        compiler.getRegisterGestion().freeGPRegister(tmprg);
        if (rg.isR())
            compiler.getRegisterGestion().freeGPRegister((GPRegister)rg);//compiler.freeR();
        if (tmp != null)
            compiler.getRegisterGestion().freeGPRegister(tmp);
        this.thenBranch.codeGenListInst(compiler);
        compiler.addInstruction(new BRA(EndIfLabel));
        compiler.addLabel(ElseLabel);
        this.elseBranch.codeGenListInst(compiler);
        compiler.addLabel(EndIfLabel);
        compiler.getRegisterGestion().freeGPRegister(tmprg);
    }

    /*@Override
    protected void codeGenInst(DecacCompiler compiler) {
        LabelGestion labHandler = compiler.getLabGestion();
        RegisterGestion regHandler = compiler.getRegisterGestion();
        
        compiler.addLabel(labHandler.getLabelIf());
        Label labelHandler.getLabelEndIf() = labHandler.getLabellabelHandler.getLabelEndIf()();
        
        if (elseBranch.getList().isEmpty()) {
            if (regHandler.containsGPRegisterLibre()){
                GPRegister reg = regHandler.getGPRegisterLibre();
		        compiler.addInstruction(new CMP(new ImmediateInteger(1), reg));
		        compiler.addInstruction(new BNE(labelHandler.getLabelEndIf()));
		        regHandler.freeGPRegister(reg);
            }
            else
            {
                GPRegister register = Register.getR(regHandler.getNbRegisters());
                compiler.addInstruction(new PUSH(register));
                compiler.addInstruction(new CMP(new ImmediateInteger(1), register));
                compiler.addInstruction(new POP(register));
                compiler.addInstruction(new BNE(labelHandler.getLabelEndIf()));
            }
            thenBranch.codeGenListInst(compiler);
            compiler.addLabel(labelHandler.getLabelEndIf());
            

        }*/
        
                    /*
            Label startElse = compiler.getLabGestion().getLabelElse();
            Label endElse = compiler.getLabGestion().getLabelEndElse();
            GPRegister register = Register.getR(compiler.getRegisterGestion().getNbRegisters());
            compiler.addInstruction(new PUSH(register));
            compiler.addInstruction(new CMP(new ImmediateInteger(1), register));
            compiler.addInstruction(new POP(register));
            compiler.addInstruction(new BNE(startElse));
            thenBranch.codeGenListInst(compiler);
            compiler.addInstruction(new BRA(endElse));
            compiler.addLabel(labelHandler.getLabelEndIf());
            compiler.addLabel(startElse);
            elseBranch.codeGenListInst(compiler);
            compiler.addLabel(endElse);

}*/

    @Override
    public void decompile(IndentPrintStream s) {
        s.print("if(");
        this.condition.decompile(s);
        s.print("){");
        this.thenBranch.decompile(s);
        s.print("} else {");
        this.elseBranch.decompile(s);
        s.print("}");
    }

    @Override
    protected
    void iterChildren(TreeFunction f) {
        condition.iter(f);
        thenBranch.iter(f);
        elseBranch.iter(f);
    }

    @Override
    protected void prettyPrintChildren(PrintStream s, String prefix) {
        condition.prettyPrint(s, prefix, false);
        thenBranch.prettyPrint(s, prefix, false);
        elseBranch.prettyPrint(s, prefix, true);
    }
}
