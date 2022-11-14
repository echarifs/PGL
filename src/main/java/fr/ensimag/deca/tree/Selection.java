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
import fr.ensimag.deca.context.ExpDefinition;
import fr.ensimag.deca.context.FieldDefinition;
import fr.ensimag.deca.context.Type;
import fr.ensimag.deca.tools.IndentPrintStream;
import fr.ensimag.ima.pseudocode.DVal;
import fr.ensimag.ima.pseudocode.GPRegister;
//import fr.ensimag.ima.pseudocode.GPRegister;
import fr.ensimag.ima.pseudocode.Register;
import fr.ensimag.ima.pseudocode.RegisterOffset;
import fr.ensimag.ima.pseudocode.instructions.LOAD;

import java.io.PrintStream;

/**
 *
 * @author gl01
 */
public class Selection extends AbstractLValue {
    
    private AbstractExpr obj;
    private AbstractIdentifier field;

    public Selection(AbstractExpr obj, AbstractIdentifier field) {
        this.obj = obj;
        this.field = field;
    }

    @Override
    public Type verifyExpr(DecacCompiler compiler, EnvironmentExp localEnv, ClassDefinition currentClass) throws ContextualError {
        // ExpDefinition fd1 = localEnv.get(field.getName());
        // if(fd1.isMethod()){
        //     throw new ContextualError("3.67, .368, 3.69", getLocation());
        // }
        Type typ = this.obj.verifyExpr(compiler, localEnv, currentClass);
        ClassType typeObj = typ.asClassType("Contextual error : The object part should be as type ClassType", getLocation());
	    EnvironmentExp envObj = typeObj.getDefinition().getMembers();
        ExpDefinition fd = envObj.get(field.getName());
        // check les contraintes relatives à la visibilité des champs protégé
        if (fd == null || !fd.isField()){
            throw new ContextualError("Cannot find field \'" + this.field + "\'", getLocation());
        }
        FieldDefinition ffd = (FieldDefinition) fd;
        Type typeField = field.verifyExpr(compiler, envObj, currentClass);
	    if (ffd.getVisibility() == Visibility.PROTECTED) {
            if (currentClass == null)
                throw new ContextualError(field.getName().getName() + " is protected ", this.getLocation());
            else {
                if (!typeObj.subType(compiler.getEnvironmentType(), currentClass.getType()))
                    throw new ContextualError("type of " + field.getName().getName() + " must be subtype of " + currentClass.getType().getName().getName(), getLocation());
                ClassType classfield = field.getFieldDefinition().getContainingClass().getType();
                if (!(currentClass.getType()).subType(compiler.getEnvironmentType(), classfield))
                    throw new ContextualError(field.getName().getName() + " is protected ", this.getLocation());
            }

        }
	    this.setType(typeField);
	    return (this.getType());
    }

    @Override
    public void decompile(IndentPrintStream s) {
        obj.decompile(s);
        s.print(".");
        field.decompile(s);
    }

    // I added this one v

    @Override
    public boolean isSelection()
    {
        return true;
    }

    public AbstractExpr getObj()
    {
        return this.obj;
    }

    public Identifier getField()
    {
        return (Identifier)this.field;
    }

    public RegisterOffset getStorage(DecacCompiler compiler)
    {
        GPRegister tmp = null;
        DVal temprg;
        if (this.obj.isIdentifier())
        {
            temprg = compiler.getDefinition((Identifier)this.obj).getOperand();
            if (temprg == null)
            {
                tmp = compiler.getRegisterGestion().getGPRegisterLibre();
                compiler.addInstruction(new LOAD(new RegisterOffset(-2, Register.LB), tmp));
                temprg = new RegisterOffset(((Identifier)this.obj).getFieldDefinition().getIndex(), tmp);
            }
        }
        else if (this.obj.isThis())
            temprg = new RegisterOffset(-2, Register.LB);
        else if (this.obj.isSelection())
            temprg = ((Selection)this.obj).getStorage(compiler);
        else
        {
            this.obj.codeGenInst(compiler);
            if (this.obj.isMethodCall())
                temprg = Register.R0;
            else
                temprg = compiler.getRegisterGestion().getGPRegister();
        }
        GPRegister tmprg = compiler.getRegisterGestion().getGPRegisterLibre();
        compiler.addInstruction(new LOAD(temprg, tmprg));
        if (temprg.isR())
            compiler.getRegisterGestion().freeGPRegister((GPRegister)temprg);
        if (tmp != null)
            compiler.getRegisterGestion().freeGPRegister(tmp);
        return new RegisterOffset(this.field.getFieldDefinition().getIndex(), tmprg);
    }

    // I added this one ^

    @Override
    protected void prettyPrintChildren(PrintStream s, String prefix) {
        obj.prettyPrint(s, prefix, false);
	    field.prettyPrint(s, prefix, true);
    }

    @Override
    protected void iterChildren(TreeFunction f) {
        // do nothing
    }
    
}
