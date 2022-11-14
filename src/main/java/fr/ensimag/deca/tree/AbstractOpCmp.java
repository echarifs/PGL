package fr.ensimag.deca.tree;

import fr.ensimag.deca.context.Type;
import fr.ensimag.deca.tools.SymbolTable;
import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.BooleanType;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.EnvironmentExp;

/**
 *
 * @author gl01
 * @date 01/01/2022
 */
public abstract class AbstractOpCmp extends AbstractBinaryExpr {

    public AbstractOpCmp(AbstractExpr leftOperand, AbstractExpr rightOperand) {
        super(leftOperand, rightOperand);
    }

    @Override
    public Type verifyExpr(DecacCompiler compiler, EnvironmentExp localEnv,
            ClassDefinition currentClass) throws ContextualError {
        /*Type type1 = this.getLeftOperand().verifyExpr(compiler, localEnv, currentClass);
        Type type2 = this.getRightOperand().verifyExpr(compiler, localEnv, currentClass);
        if (!type2.getName().getName().equals(type1.getName().getName()))
            throw new ContextualError("invalid conversion from \'" + type2 + "\' to \'" + type1 + "\'.", this.getLocation());*/
        Type leftOperandType = this.getLeftOperand().verifyExpr(compiler, localEnv, currentClass);
        Type rightOperandType = this.getRightOperand().verifyExpr(compiler, localEnv, currentClass);
		// throw exception when any of these two types not int or float
		if (rightOperandType.isBoolean() && leftOperandType.isBoolean()){
			Type type3 = new BooleanType(new SymbolTable().create("boolean"));
			if(this.getOperatorName()=="=="||this.getOperatorName()=="!=" )
				this.setType(type3);
				return type3;
		}

		if ((leftOperandType.isInt() || leftOperandType.isFloat()) && (!rightOperandType.isInt() && !rightOperandType.isFloat()))
			throw new ContextualError("invalid type for comparaison operation " + rightOperandType.toString() , this.getLocation());
		else if ((rightOperandType.isInt() || rightOperandType.isFloat()) && (!leftOperandType.isInt() && !leftOperandType.isFloat()))
			throw new ContextualError("invalid type for comparaison operation " + leftOperandType.toString() , this.getLocation());
		else if (!leftOperandType.isInt() && !leftOperandType.isFloat() && !rightOperandType.isInt() && !rightOperandType.isFloat())
			throw new ContextualError("invalid type for comparaison operation " + leftOperandType.toString() + " and " +rightOperandType.toString()  , this.getLocation());
        /*if (!(leftOperandType.isInt() || leftOperandType.isFloat() || rightOperandType.isFloat() || rightOperandType.isInt())) {
			throw new ContextualError("Contextual error : Type should be int or float for arithmetique operation", getLocation());
		}*/
		else
		{
			// Convert operand type to float when one of them is float and another is int 
			if (leftOperandType.isFloat() && rightOperandType.isInt()) {
				//this.setRightOperand(this.getRightOperand().verifyRValue(compiler, localEnv, currentClass, leftOperandType));
				ConvFloat conv = new ConvFloat(this.getRightOperand());
				conv.setType(leftOperandType);
				this.setRightOperand(conv);
			}
			else if (leftOperandType.isInt() && rightOperandType.isFloat()) {
				//this.setLeftOperand(this.getLeftOperand().verifyRValue(compiler, localEnv, currentClass, rightOperandType));
				ConvFloat conv = new ConvFloat(this.getLeftOperand());
				conv.setType(rightOperandType);
				this.setLeftOperand(conv);
			}
		}
        Type type3 = new BooleanType(new SymbolTable().create("boolean"));
        this.setType(type3);
        return type3;
        //throw new UnsupportedOperationException("not yet implemented");
    }


}
