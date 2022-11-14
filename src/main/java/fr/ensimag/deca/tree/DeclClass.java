package fr.ensimag.deca.tree;

import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.ClassType;
import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.EnvironmentExp;
import fr.ensimag.deca.context.ExpDefinition;
//import fr.ensimag.deca.context.FieldDefinition;
//import fr.ensimag.deca.context.EnvironmentExp;
import fr.ensimag.deca.context.MethodDefinition;
//import fr.ensimag.deca.context.EnvironmentExp;
//import fr.ensimag.deca.context.TypeDefinition;
import fr.ensimag.deca.tools.IndentPrintStream;
import fr.ensimag.deca.tools.SymbolTable.Symbol;
import fr.ensimag.ima.pseudocode.DVal;
import fr.ensimag.ima.pseudocode.GPRegister;
import fr.ensimag.ima.pseudocode.ImmediateFloat;
import fr.ensimag.ima.pseudocode.ImmediateInteger;
import fr.ensimag.ima.pseudocode.Label;
import fr.ensimag.ima.pseudocode.LabelOperand;
import fr.ensimag.ima.pseudocode.Register;
import fr.ensimag.ima.pseudocode.RegisterOffset;
import fr.ensimag.ima.pseudocode.instructions.BSR;
//import fr.ensimag.ima.pseudocode.instructions.BOV;
import fr.ensimag.ima.pseudocode.instructions.LEA;
import fr.ensimag.ima.pseudocode.instructions.LOAD;
import fr.ensimag.ima.pseudocode.instructions.POP;
import fr.ensimag.ima.pseudocode.instructions.PUSH;
import fr.ensimag.ima.pseudocode.instructions.RTS;
//import fr.ensimag.ima.pseudocode.Register;
//import fr.ensimag.ima.pseudocode.RegisterOffset;
//import fr.ensimag.ima.pseudocode.instructions.RTS;
import fr.ensimag.ima.pseudocode.instructions.STORE;
//import fr.ensimag.ima.pseudocode.instructions.TSTO;

//import static org.mockito.ArgumentMatchers.nullable;

import java.io.PrintStream;
//import java.lang.reflect.Method;
import java.util.Map;

//import javax.management.ImmutableDescriptor;


/**
 * Declaration of a class (<code>class name extends superClass {members}<code>).
 * 
 * @author gl01
 * @date 01/01/2022
 */
public class DeclClass extends AbstractDeclClass {

    private AbstractIdentifier name;
    private AbstractIdentifier superClass;
    private ListDeclField listField;
    private ListDeclMethod listMtd;

    @Override
    public void decompile(IndentPrintStream s) {
        s.print("class ");
        name.decompile(s);
        s.print(" extends ");
        superClass.decompile(s);
        s.print(" {");
        listField.decompile(s);
        listMtd.decompile(s);
        s.print("}");

    }

    public DeclClass(AbstractIdentifier name, AbstractIdentifier superClass, ListDeclField listField,
            ListDeclMethod listMtd) {
        this.name = name;
        this.superClass = superClass;
        this.listField = listField;
        this.listMtd = listMtd;
    }

    @Override
    protected void verifyClass(DecacCompiler compiler) throws ContextualError {
        // throw new UnsupportedOperationException("not yet implemented");
        if (!compiler.getEnvironmentType().isdeclared(superClass.getName())) {
            throw new ContextualError("Super Class " + this.superClass.getName().getName() + " should be declared first", getLocation());
        }
        else {
            if (!compiler.getEnvironmentType().get(superClass.getName()).isClass()) {
                throw new ContextualError("extends can be applied only to classes ", getLocation());
            }
            ClassDefinition defsuperclass = (ClassDefinition)compiler.getEnvironmentType().get(superClass.getName());
            ClassType superclasstype = new ClassType(this.superClass.getName(), this.superClass.getLocation(), defsuperclass);
            superClass.setDefinition(compiler.getEnvironmentType().get(superClass.getName()));
            superClass.setType(superclasstype);
            if (compiler.getEnvironmentType().isdeclared(this.name.getName())) {
                throw new ContextualError("Class " + this.name.getName().getName() + " already declared", getLocation());
            }
            ClassType classtype = new ClassType(this.name.getName(), this.name.getLocation(), defsuperclass);
            ClassDefinition classdef = classtype.getDefinition();
            compiler.getEnvironmentType().declare(this.name.getName(), classdef);
            this.name.setDefinition(classdef);
            this.name.setType(classtype);
        }
    }

    @Override
    protected void verifyClassMembers(DecacCompiler compiler)
            throws ContextualError {
        this.name.getClassDefinition().setNumberOfFields(this.superClass.getClassDefinition().getNumberOfFields());
        // Verify fields
        this.listField.verifyListDeclField(compiler, this.superClass, this.name);
        this.name.getClassDefinition().setNumberOfMethods(this.superClass.getClassDefinition().getNumberOfMethods());
        // Verify methods
        this.listMtd.verifyListDeclMethod(compiler, this.name.getClassDefinition(), this.superClass.getClassDefinition());

    }
    

    @Override
    protected void verifyClassBody(DecacCompiler compiler) throws ContextualError {
        // throw new UnsupportedOperationException("not yet implemented");
        compiler.declareEnv(new Label("init." + this.name.getName().getName()), new EnvironmentExp(this.name.getClassDefinition().getMembers()));
        for (AbstractDeclMethod m : this.listMtd.getList())
            m.verifyMethodBody(compiler, this.name.getClassDefinition().getMembers(), this.name.getClassDefinition());
    }

    @Override
    protected void prettyPrintChildren(PrintStream s, String prefix) {
        this.name.prettyPrint(s, prefix, false);
        this.superClass.prettyPrint(s, prefix, false);
        this.listField.prettyPrint(s, prefix, false);
        this.listMtd.prettyPrint(s, prefix, false);
    }

    @Override
    protected void iterChildren(TreeFunction f) {
        this.name.iter(f);
        this.superClass.iter(f);
        this.listField.iter(f);
        this.listMtd.iter(f);
    }

    @Override
    protected void codeGenDeclClass(DecacCompiler compiler) {
        Label tmpl = new Label("init." + this.name.getName().getName());
        compiler.addLabel(tmpl);
        compiler.set(tmpl);
        String superName = this.superClass.getName().getName();
        if (!superName.equals("Object"))
        {
            compiler.addInstruction(new LOAD(new RegisterOffset(-2, Register.LB), Register.R1));
            compiler.addInstruction(new PUSH(Register.R1));
            compiler.addInstruction(new BSR(new LabelOperand(new Label("init." + superName))));
            compiler.addInstruction(new POP(Register.R1));
        }
        for (AbstractDeclField index : this.listField.getList())
        {
            DeclField tmpvar = (DeclField)index;
            if (tmpvar.getInit().isInitialized())
            {
                GPRegister tmp = null;
                Initialization tmpInit = (Initialization)tmpvar.getInit();
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
                RegisterOffset tmprg = new RegisterOffset(tmpvar.getFieldName().getFieldDefinition().getIndex(), Register.R1);
                if (!rg.equals(Register.R0))
                    compiler.addInstruction(new LOAD(rg, Register.R0));
                compiler.addInstruction(new LOAD(new RegisterOffset(-2, Register.LB), Register.R1));
                compiler.addInstruction(new STORE(Register.R0, tmprg));
                if (rg.isR())
                    compiler.getRegisterGestion().freeGPRegister((GPRegister)rg);
                if (tmp != null)
                    compiler.getRegisterGestion().freeGPRegister(tmp);
            }
        }
        compiler.reset();
        compiler.addInstruction(new RTS());
        for (AbstractDeclMethod index : this.listMtd.getList())
            index.codeGenDeclMethod(compiler);
    }

    protected void codeGenPasseFirst(DecacCompiler compiler) {
        ClassDefinition def1 = this.name.getClassDefinition();
        int number = def1.getNumberOfMethods();
        Label[] labtab = new Label[number];
        while (def1 != null)
        {
            for (Map.Entry<Symbol, ExpDefinition> subindex : def1.getMembers().entrySet())
                if (!subindex.getValue().isField() && labtab[((MethodDefinition)subindex.getValue()).getIndex() - 1] == null)
                    labtab[((MethodDefinition)subindex.getValue()).getIndex() - 1] = ((MethodDefinition)subindex.getValue()).getLabel();
            def1 = def1.getSuperClass();
        }
        compiler.getRegisterGestion().incADDSP(number + 1);
        compiler.addInstruction(new LEA(compiler.getClassDefinition((Identifier)this.superClass).getOperand(), Register.R0));
        compiler.addInstruction(new STORE(Register.R0, new RegisterOffset(compiler.getRegisterGestion().getOffset(), Register.GB)));
        for (int index = 0; index < number; index++)
        {
            RegisterOffset tmprg = new RegisterOffset(compiler.getRegisterGestion().getOffset() + index + 1, Register.GB);
            compiler.addInstruction(new LOAD(new LabelOperand(new Label("code." + labtab[index].toString())), Register.R0));
            compiler.addInstruction(new STORE(Register.R0, tmprg));
        }
        compiler.getClassDefinition((Identifier)this.name).setOperand(new RegisterOffset(compiler.getRegisterGestion().getOffset(), Register.GB));
        compiler.getRegisterGestion().incOffset(number + 1);
    }

}
