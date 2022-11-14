/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package fr.ensimag.deca.tree;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.ClassDefinition;
//import fr.ensimag.deca.context.ClassType;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.EnvironmentExp;
import fr.ensimag.deca.context.EnvironmentType;
import fr.ensimag.deca.context.ExpDefinition;
import fr.ensimag.deca.context.MethodDefinition;
import fr.ensimag.deca.context.Signature;
import fr.ensimag.deca.context.Type;
import fr.ensimag.deca.context.EnvironmentExp.DoubleDefException;
import fr.ensimag.deca.tools.IndentPrintStream;
import fr.ensimag.ima.pseudocode.Label;
import fr.ensimag.ima.pseudocode.Register;
import fr.ensimag.ima.pseudocode.RegisterOffset;
import fr.ensimag.ima.pseudocode.instructions.RTS;

import java.io.PrintStream;
//import java.util.logging.Level;
//import java.util.logging.Logger;

/**
 *
 * @author gl01
 */
public class DeclMethod extends AbstractDeclMethod{
    private AbstractIdentifier type;
    private AbstractIdentifier name;
    private ListDeclParam params;
    private AbstractMethodBody body;

    public DeclMethod(AbstractIdentifier type, AbstractIdentifier name, ListDeclParam params,
			AbstractMethodBody body) {
        this.type = type;
        this.name = name;
        this.params = params;
        this.body = body;
	}

    @Override
    public void decompile(IndentPrintStream s) {
        type.decompile(s);
        s.print(" ");
        name.decompile(s);
        s.print("(");
        params.decompile(s);
        s.println(")");
        body.decompile(s);
    }

    /* I added this one v */

    public AbstractIdentifier getType()
    {
        return this.type;
    }

    public AbstractIdentifier getMethodName()
    {
        return this.name;
    }

    /* I added this one ^ */

    @Override
    protected void prettyPrintChildren(PrintStream s, String prefix) {
        this.type.prettyPrint(s, prefix, false);
        this.name.prettyPrint(s, prefix, false);
        this.params.prettyPrint(s, prefix, false);
        this.body.prettyPrint(s, prefix, true);
    }

    @Override
    protected void iterChildren(TreeFunction f) {
        this.type.iter(f);
        this.name.iter(f);
        this.params.iter(f);
        this.body.iter(f);
    }

    @Override
    protected void codeGenDeclMethod(DecacCompiler compiler) {
        compiler.addLabel(new Label("code." + this.name.getMethodDefinition().getLabel().toString()));
        compiler.set(this.name.getMethodDefinition().getLabel());
        int counter = 0;
        for (AbstractDeclParam index : this.params.getList())
            compiler.getDefinition(((DeclParam)index).getName()).setOperand(new RegisterOffset(compiler.getRegisterGestion().getLocalOffset() - counter++, Register.LB));
        compiler.getRegisterGestion().incLocalOffset(counter);
        this.body.codeGenMethodBody(compiler);
        compiler.getRegisterGestion().resetLocalOffset();
        compiler.addLabel(new Label("end." + this.name.getMethodDefinition().getLabel().toString()));
        compiler.reset();
        compiler.addInstruction(new RTS());
        //compiler.add
        //throw new UnsupportedOperationException("not yet implemented");
    }

    @Override
    protected void verifyDeclMethod(DecacCompiler compiler, ClassDefinition classDefinition,
            ClassDefinition classDefinitionSuper) throws ContextualError {
        Type typeRetour = this.type.verifyType(compiler);
        Signature signature = this.params.verifyListDeclParam(compiler, new EnvironmentExp(classDefinition.getMembers()));
        ExpDefinition defExp = classDefinitionSuper.getMembers().get(this.name.getName());
        int p;
        if (defExp != null) {
            if (!defExp.isMethod()) {
                throw new ContextualError(name.getName().getName() + " must have a declaration as a method in super class", getLocation());
            }
            MethodDefinition mthddef = (MethodDefinition) defExp;
            Signature supersigmthd = mthddef.getSignature();
            if (!supersigmthd.same(signature)) {
                throw new ContextualError(name.getName().getName() + " must have the same signature as in superclass (regle 2.7) ", getLocation());
            }
            Type type2 = mthddef.getType();
            //System.out.println(type2.equals(typeRetour));
            if (!subtType(compiler.getEnvironmentType(), typeRetour, type2)) {
                throw new ContextualError(name.getName().getName() + " type of return of method must be same or a subtype of type of return of method in superclass ", getLocation());
            }
            p = mthddef.getIndex();
        }
        else
            p = classDefinition.incNumberOfMethods();
        MethodDefinition def = new MethodDefinition(typeRetour, getLocation(), signature, p);
        def.setLabel(new Label((classDefinition.getType().getName().getName()) + "." + this.name.getName().getName()));
        this.name.setDefinition(def);
        this.name.setType(typeRetour);
        try {
            classDefinition.getMembers().declare(name.getName(), def);
        } catch (DoubleDefException e) {
            throw new ContextualError("already declared", getLocation());
        }
        //System.out.println(classDefinition.getMembers().get(name.getName()));
        //System.out.println(p);
        /*
         * //verify if method in environment
         * EnvironmentExp classMembers = classDefinition.getMembers();
         * ExpDefinition defExp = classMembers.get(this.name.getName());
         * // verify the signature
         * EnvironmentExp envLocal = new EnvironmentExp(classMembers);
         * Signature sigs = this.params.verifyListDeclParam(compiler, envLocal);
         * int indexDefExp;
         * MethodDefinition redefMtd;
         * if (defExp != null && defExp.isMethod()) {
         * // if we already got this method, get the index
         * indexDefExp = ((MethodDefinition) defExp).getIndex();
         * redefMtd = new MethodDefinition(typeRetour, this.type.getLocation(), sigs,
         * indexDefExp);
         * }
         * else {
         * // if the method is new , we calculate the index
         * indexDefExp = classDefinition.incNumberOfMethods() +
         * classDefinitionSuper.getNumberOfMethods();
         * redefMtd = new MethodDefinition(typeRetour, this.type.getLocation(), sigs,
         * indexDefExp);
         * }
         * // set label of new method
         * redefMtd.setLabel(new Label((classDefinition.getType().getName().getName()) +
         * "." + this.name.getName().getName()));
         * try {
         * // declare new method in the environment
         * classMembers.declare(name.getName(), redefMtd);
         * }
         * catch (EnvironmentExp.DoubleDefException ex) {
         * //Logger.getLogger(DeclMethod.class.getName()).log(Level.SEVERE, null, ex);
         * throw new
         * ContextualError("Contextual error : The method is already defined in this class (regle 2.7)"
         * , this.name.getLocation());
         * }
         * // - add the method to methods table (MT)
         * //current.getMT().putInMT(redefMtd);
         * // set type and definition
         * this.name.setType(typeRetour);
         * this.name.setDefinition(redefMtd);
         *
         * //doit avoir la même signature que la méthode héritée ;
         * EnvironmentExp classSuperMembers =
         * classDefinition.getSuperClass().getMembers();
         * ExpDefinition defSuperMembers = classSuperMembers.get(this.name.getName());
         * if (defSuperMembers != null && defSuperMembers.isMethod()) {
         * MethodDefinition definitionInSuper = (MethodDefinition) defSuperMembers;
         * Type typeMtd = redefMtd.getType();
         * Type typeSuper = definitionInSuper.getType();
         * //verify types and signatures
         * if (typeMtd.isClass() && typeSuper.isClass()) {
         * if
         * (!(typeMtd.asClassType("Contextual error : current class not a class type",
         * getLocation())).
         * isSubClassOf(typeSuper.
         * asClassType("Contextual error : super class not a class type",
         * getLocation()))) {
         * throw new
         * ContextualError("Contextual error : The type of return must be a subtype of method from super class (regle (2.7))"
         * , getLocation());
         * }
         * }
         * else if (!redefMtd.getType().sameType(definitionInSuper.getType())) {
         * throw new
         * ContextualError("Contextual error : The type of return must be a subtype of method from super class (regle (2.7))"
         * , getLocation());
         * }
         * Signature sigNewMtd = redefMtd.getSignature();
         * Signature sigSuper = definitionInSuper.getSignature();
         * if (sigNewMtd.size() != sigSuper.size()) {
         * throw new
         * ContextualError("Contextual error : The redefinition of method must have the same signature "
         * , getLocation());
         * }
         * else {
         * // verify every argument in methods
         * for (int i = 0; i < sigNewMtd.size(); i++) {
         * if (!sigNewMtd.paramNumber(i).sameType(sigSuper.paramNumber(i))) {
         * throw new
         * ContextualError("Contextual error : The redefinition of method must have the same signature "
         * , getLocation());
         * }
         * }
         * }
         * }
         */
    }


    @Override
    protected void verifyMethodBody(DecacCompiler compiler, EnvironmentExp localEnv,
                ClassDefinition currentClass) throws ContextualError {
        EnvironmentExp envMethod = new EnvironmentExp(localEnv);
        this.params.verifyListDeclParam(compiler, envMethod);
        Type typRetour = this.type.verifyType(compiler);

        // verify body of the method
        this.body.verifyMethodBody(compiler, currentClass, envMethod, typRetour);
        compiler.declareEnv(((Identifier)this.name).getMethodDefinition().getLabel(), envMethod);
    }


    public boolean subtType(EnvironmentType env_types, Type type1, Type type2) {
        if (type1 == null) {
            return false; //gotta reconsider that and reconsider definition of object's env_exp
        }
        if (type1.equals(type2)) {
            return true;
        }

        if (type1.isClass() && type2.isClass()) {
            ClassDefinition typec1 = (ClassDefinition) env_types.get(type1.getName());
            ClassDefinition typec2 = (ClassDefinition) env_types.get(type2.getName());
            if (typec2.getSuperClass() == null) {
                return true;
            }

            if (typec2.equals(typec1.getSuperClass())) {
                //System.out.println(typec2.getType().getName());
                //System.out.println(typec1.getSuperClass().getType().getName());
                return true;
            }
            ClassDefinition C = typec1.getSuperClass();
            Type c = C.getType();

            if (subtType(env_types, c, type2)) {
                return true;
            }
        }
        return false;
    }
}
