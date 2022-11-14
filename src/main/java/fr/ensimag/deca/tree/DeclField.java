/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package fr.ensimag.deca.tree;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.EnvironmentExp;
import fr.ensimag.deca.context.ExpDefinition;
import fr.ensimag.deca.context.FieldDefinition;
//import fr.ensimag.deca.context.Type;
import fr.ensimag.deca.context.EnvironmentExp.DoubleDefException;
import fr.ensimag.deca.tools.IndentPrintStream;
import java.io.PrintStream;
//import org.apache.commons.lang.Validate;
//import java.lang.reflect.Field;

/**
 *
 * @author gl01
 */
public class DeclField extends AbstractDeclField
{
    private Visibility visib;

    private AbstractIdentifier type;
    private AbstractIdentifier field;
    private AbstractInitialization init;

    public DeclField(Visibility visib, AbstractIdentifier type, AbstractIdentifier field, AbstractInitialization init) {
        this.visib = visib;
        this.type = type;
        this.field = field;
        this.init = init;
	}

    @Override
    public void decompile(IndentPrintStream s) {
        if (visib == Visibility.PROTECTED) {
            s.print("protected ");
	    }
        type.decompile(s);
        s.print(" ");
        field.decompile(s);
        init.decompile(s);
        s.print(";");

    }

    @Override
    protected void prettyPrintChildren(PrintStream s, String prefix) {

        type.prettyPrint(s, prefix, false);
        field.prettyPrint(s, prefix, false);
        init.prettyPrint(s, prefix, true);
    }

    @Override
    protected void iterChildren(TreeFunction f) {
        type.iter(f);
        field.iter(f);
        init.iter(f);
    }

    @Override
    protected void verifyDeclField(DecacCompiler compiler, AbstractIdentifier superClass,
                    AbstractIdentifier currentClass) throws ContextualError {
        int p;
        this.type.setType(this.type.verifyType(compiler));
        if (this.type.getType().isVoid()) {
            throw new ContextualError("type of attribute cannot be void (regle 2.5)", getLocation());
        }
        ClassDefinition classDefSuper = superClass.getClassDefinition();
        EnvironmentExp superenvexp = classDefSuper.getMembers();
        ExpDefinition superfield = superenvexp.get(this.field.getName());
        if (superfield != null && superfield.isField())
            p = ((FieldDefinition)superfield).getIndex();
        else
            p = currentClass.getClassDefinition().incNumberOfFields();
        if (superfield != null && !superfield.isField())
            throw new ContextualError(this.field.getName().getName() + " must be have a declaration as a field (regle 3.66)", getLocation());
        FieldDefinition newfield = new FieldDefinition(this.type.getType(), getLocation(), this.visib, currentClass.getClassDefinition(), p);
        this.field.setType(this.type.getType());
        this.field.setDefinition(newfield);
        try {
            currentClass.getClassDefinition().getMembers().declare(this.field.getName(), newfield);
        }
        catch (DoubleDefException e) {
            throw new ContextualError(this.field.getName().getName() + " already declared", getLocation());
        }
        this.init.verifyInitialization(compiler, this.type.getType(), superenvexp, currentClass.getClassDefinition());
    }

    // I added this one v

    public Identifier getFieldName()
    {
        return (Identifier)this.field;
    }

    public AbstractInitialization getInit()
    {
        return this.init;
    }

    // I added this one ^

    @Override
    protected void codeGenDeclField(DecacCompiler compiler) {
        // TODO Auto-generated method stub

    }

     /**
     * add the visibility to the node
     *
     * @return visibility and DeclField
     */
    @Override
    String prettyPrintNode() {
        return ("[visibility=" + this.visib.name() + "] " + this.getClass().getSimpleName());
    }


}
