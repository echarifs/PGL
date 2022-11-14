package fr.ensimag.deca.tree;

import fr.ensimag.deca.context.Type;
import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.codegen.LabelGestion;
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
import fr.ensimag.ima.pseudocode.instructions.BNE;
import fr.ensimag.ima.pseudocode.instructions.BRA;
import fr.ensimag.ima.pseudocode.instructions.CMP;
import fr.ensimag.ima.pseudocode.instructions.LOAD;
import java.io.PrintStream;
import org.apache.commons.lang.Validate;

/**
 *
 * @author gl01
 * @date 01/01/2022
 */
public class While extends AbstractInst {
    private AbstractExpr condition;
    private ListInst body;

    public AbstractExpr getCondition() {
        return condition;
    }

    public ListInst getBody() {
        return body;
    }

    public While(AbstractExpr condition, ListInst body) {
        Validate.notNull(condition);
        Validate.notNull(body);
        this.condition = condition;
        this.body = body;
    }

    @Override
    protected void codeGenInst(DecacCompiler compiler) {
        GPRegister tmp = null;
        LabelGestion labelHandler = compiler.getLabGestion();
        DVal rg;
        Label WhileLabel = labelHandler.getWhileLabel();
        Label EndWhileLabel = labelHandler.getEndWhileLabel();
        compiler.addLabel(WhileLabel);
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
        compiler.addInstruction(new BNE(EndWhileLabel));
        compiler.getRegisterGestion().freeGPRegister(tmprg);
        if (rg.isR())
            compiler.getRegisterGestion().freeGPRegister((GPRegister)rg);//compiler.freeR();
        if (tmp != null)
            compiler.getRegisterGestion().freeGPRegister(tmp);
        body.codeGenListInst(compiler);
	    compiler.addInstruction(new BRA(WhileLabel));
        compiler.addLabel(EndWhileLabel);
        compiler.getRegisterGestion().freeGPRegister(tmprg);
    }

    @Override
    protected void verifyInst(DecacCompiler compiler, EnvironmentExp localEnv,
            ClassDefinition currentClass, Type returnType)
            throws ContextualError {
        this.condition.verifyCondition(compiler, localEnv, currentClass);
        this.body.verifyListInst(compiler, localEnv, currentClass, returnType);
    }

    @Override
    public void decompile(IndentPrintStream s) {
        s.print("while (");
        getCondition().decompile(s);
        s.println(") {");
        s.indent();
        getBody().decompile(s);
        s.unindent();
        s.print("}");
    }

    @Override
    protected void iterChildren(TreeFunction f) {
        condition.iter(f);
        body.iter(f);
    }

    @Override
    protected void prettyPrintChildren(PrintStream s, String prefix) {
        condition.prettyPrint(s, prefix, false);
        body.prettyPrint(s, prefix, true);
    }

}
