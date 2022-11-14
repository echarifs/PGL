package fr.ensimag.deca.tree;

import fr.ensimag.deca.context.Type;
import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.ClassType;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.EnvironmentExp;
import fr.ensimag.deca.tools.DecacInternalError;
import fr.ensimag.deca.tools.IndentPrintStream;
//import fr.ensimag.deca.tools.SymbolTable.Symbol;

import fr.ensimag.ima.pseudocode.*;
import fr.ensimag.ima.pseudocode.instructions.*;
import java.io.PrintStream;

import org.apache.commons.lang.Validate;

/**
 * Expression, i.e. anything that has a value.
 *
 * @author gl01
 * @date 01/01/2022
 */
public abstract class AbstractExpr extends AbstractInst {
    /**
     * @return true if the expression does not correspond to any concrete token
     * in the source code (and should be decompiled to the empty string).
     */
    boolean isImplicit() {
        return false;
    }

    /**
     * Get the type decoration associated to this expression (i.e. the type computed by contextual verification).
     */
    public Type getType() {
        return type;
    }

    protected void setType(Type type) {
        Validate.notNull(type);
        this.type = type;
    }
    private Type type;

    @Override
    protected void checkDecoration() {
        if (getType() == null) {
            throw new DecacInternalError("Expression " + decompile() + " has no Type decoration");
        }
    }

    /**
     * Verify the expression for contextual error.
     * 
     * implements non-terminals "expr" and "lvalue" 
     *    of [SyntaxeContextuelle] in pass 3
     *
     * @param compiler  (contains the "env_types" attribute)
     * @param localEnv
     *            Environment in which the expression should be checked
     *            (corresponds to the "env_exp" attribute)
     * @param currentClass
     *            Definition of the class containing the expression
     *            (corresponds to the "class" attribute)
     *             is null in the main bloc.
     * @return the Type of the expression
     *            (corresponds to the "type" attribute)
     */
    public abstract Type verifyExpr(DecacCompiler compiler,
            EnvironmentExp localEnv, ClassDefinition currentClass)
            throws ContextualError;
    
    //public abstract void codeGenExpr(DecacCompiler compiler, GPRegister register);

    /**
     * Verify the expression in right hand-side of (implicit) assignments 
     * 
     * implements non-terminal "rvalue" of [SyntaxeContextuelle] in pass 3
     *
     * @param compiler  contains the "env_types" attribute
     * @param localEnv corresponds to the "env_exp" attribute
     * @param currentClass corresponds to the "class" attribute
     * @param expectedType corresponds to the "type1" attribute            
     * @return this with an additional ConvFloat if needed...
     */
    public AbstractExpr verifyRValue(DecacCompiler compiler,
            EnvironmentExp localEnv, ClassDefinition currentClass, 
            Type expectedType)
            throws ContextualError {
        // A FAIRE!!! HASNT FINISHED YET
        Type rtype = this.verifyExpr(compiler, localEnv, currentClass);
        //assigncompatibl : should be adde in another file later maybe EnvType
        
        if (rtype.isClass()) {
            if (!expectedType.isClass()) {
                throw new ContextualError("Contextual error : The left value should be a class type ", this.getLocation());
            }
            else {
                ClassType classTypeR = rtype.asClassType("Contextual error : current class not a class type", getLocation());
                ClassType classTypeExp = expectedType.asClassType("Contextual error : expected class not a class type", getLocation());
                // if subclass
                if (!classTypeR.equals(classTypeExp) && !classTypeR.isSubClassOf(classTypeExp)) {
                    throw new ContextualError("Contextual error :  The two classes are not compatible (regle 3.28)", this.getLocation());
                }
            }

            // if not the same type
        }
        else if (!rtype.sameType(expectedType)){
            if (rtype.isInt() && expectedType.isFloat()){
                ConvFloat convf = new ConvFloat(this);
                convf.setType(expectedType);
                return convf;
            }
            throw new ContextualError("the following types are not compatible for assignement : " + expectedType.toString() + " and " + rtype.toString(), getLocation());
        }
        //if (!this.getType().getName().getName().equals(expectedType.getName().getName()))
            //throw new ContextualError("invalid conversion from \'" + this.getType() + "\' to \'" + expectedType + "\'.", this.getLocation());
        return this;
        //throw new UnsupportedOperationException("not yet implemented");
    }
    
    public boolean isIdentifier()
    {
        return false;
    }

    public boolean isThis()
    {
        return false;
    }

    public boolean isSelection()
    {
        return false;
    }

    public boolean isMethodCall()
    {
        return false;
    }

    public boolean isIntLiteral()
    {
        return false;
    }

    public boolean isFloatLiteral()
    {
        return false;
    }

    public boolean isStringLiteral()
    {
        return false;
    }

    public boolean isBooleanLiteral()
    {
        return false;
    }

    public boolean equals(AbstractExpr symbol)
    {
        return false;
    }
    
    
    @Override
    protected void verifyInst(DecacCompiler compiler, EnvironmentExp localEnv,
            ClassDefinition currentClass, Type returnType)
            throws ContextualError {
        //throw new UnsupportedOperationException("not yet implemented");
        this.verifyExpr(compiler, localEnv, currentClass);
    }

    /**
     * Verify the expression as a condition, i.e. check that the type is
     * boolean.
     *
     * @param localEnv
     *            Environment in which the condition should be checked.
     * @param currentClass
     *            Definition of the class containing the expression, or null in
     *            the main program.
     */
    void verifyCondition(DecacCompiler compiler, EnvironmentExp localEnv,
            ClassDefinition currentClass) throws ContextualError {
        Type type = this.verifyExpr(compiler, localEnv, currentClass);
        if (!type.isBoolean())
            throw new ContextualError("invalid conversion from \'" + type + "\' to \'Boolean\'.", this.getLocation());
    }
    /*     protected void codeGenPrint(DecacCompiler compiler) {
        //throw new UnsupportedOperationException("not yet implemented");
    }
  */

    /**
     * Generate code to print the expression
     *
     * @param compiler
     */  
    protected void codeGenPrint(DecacCompiler compiler, boolean printHex) {
        if (this.isIntLiteral())
            compiler.addInstruction(new LOAD(new ImmediateInteger(((IntLiteral)this).getValue()), Register.R1));
        else if (this.isFloatLiteral())
            compiler.addInstruction(new LOAD(new ImmediateFloat(((FloatLiteral)this).getValue()), Register.R1));
        else if (this.isStringLiteral())
            compiler.addInstruction(new WSTR(new ImmediateString(((StringLiteral)this).getValue())));
        else if (this.isIdentifier())
        {
            GPRegister tmp = null;
            DVal rg = compiler.getDefinition((Identifier)this).getOperand();
            if (rg == null)
            {
                tmp = compiler.getRegisterGestion().getGPRegisterLibre();
                compiler.addInstruction(new LOAD(new RegisterOffset(-2, Register.LB), tmp));
                rg = new RegisterOffset(((Identifier)this).getFieldDefinition().getIndex(), tmp);
            }
            compiler.addInstruction(new LOAD(rg, Register.R1));
            if (tmp != null)
                compiler.getRegisterGestion().freeGPRegister(tmp);
        }
        else if (this.isSelection())
            compiler.addInstruction(new LOAD(((Selection)this).getStorage(compiler), Register.R1));
        else
        {
            this.codeGenInst(compiler);
            if (this.isMethodCall())
                compiler.addInstruction(new LOAD(Register.R0, Register.R1));
            else
                compiler.addInstruction(new LOAD(compiler.getRegisterGestion().getGPRegister(), Register.R1));
        }
	    if (getType().isInt())
        {
            if (!printHex) {
                compiler.addInstruction(new WINT());
            }
            else
            {
		        compiler.addInstruction(new FLOAT(Register.R1, Register.R1));
		        compiler.addInstruction(new WFLOATX());
            }
	    }
        else if (getType().isFloat())
        {
            if (!printHex)
                compiler.addInstruction(new WFLOAT());
            else
                compiler.addInstruction(new WFLOATX());
	    }
	}

    @Override
    protected void codeGenInst(DecacCompiler compiler) {
        //throw new UnsupportedOperationException("not yet implemented");
    }
    

    @Override
    protected void decompileInst(IndentPrintStream s) {
        decompile(s);
        s.print(";");
    }

    @Override
    protected void prettyPrintType(PrintStream s, String prefix) {
        Type t = getType();
        if (t != null) {
            s.print(prefix);
            s.print("type: ");
            s.print(t);
            s.println();
        }
    }
    
    
}
