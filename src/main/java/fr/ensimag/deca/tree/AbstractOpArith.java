package fr.ensimag.deca.tree;

import fr.ensimag.deca.context.Type;
import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.EnvironmentExp;

/**
 * Arithmetic binary operations (+, -, /, ...)
 * 
 * @author gl01
 * @date 01/01/2022
 */
public abstract class AbstractOpArith extends AbstractBinaryExpr {

    public AbstractOpArith(AbstractExpr leftOperand, AbstractExpr rightOperand) {
        super(leftOperand, rightOperand);
    }

    @Override
    public Type verifyExpr(DecacCompiler compiler, EnvironmentExp localEnv, ClassDefinition currentClass)
		throws ContextualError {
        Type leftOperandType = this.getLeftOperand().verifyExpr(compiler, localEnv, currentClass);
        Type rightOperandType = this.getRightOperand().verifyExpr(compiler, localEnv, currentClass);
		// throw exception when any of these two types not int or float
		if ((leftOperandType.isInt() || leftOperandType.isFloat()) && (!rightOperandType.isInt() && !rightOperandType.isFloat()))
			throw new ContextualError("invalid type for arithmetique operation " + rightOperandType.toString(), this.getLocation());
		else if ((rightOperandType.isInt() || rightOperandType.isFloat()) && (!leftOperandType.isInt() && !leftOperandType.isFloat()))
			throw new ContextualError("invalid type for arithmetique operation " + leftOperandType.toString(), this.getLocation());
		else if (!leftOperandType.isInt() && !leftOperandType.isFloat() && !rightOperandType.isInt() && !rightOperandType.isFloat())
			throw new ContextualError("invalid type for arithmetique operation " + leftOperandType.toString() + " and " + rightOperandType.toString(), this.getLocation());
		else
		{
			// Convert operand type to float when one of them is float and another is int 
			if (leftOperandType.isFloat() && rightOperandType.isInt()) {
				//this.setRightOperand(this.getRightOperand().verifyRValue(compiler, localEnv, currentClass, leftOperandType));
				ConvFloat conv = new ConvFloat(this.getRightOperand());
                conv.setType(leftOperandType);
				this.setRightOperand(conv);
				this.setType(leftOperandType);
				return leftOperandType;
			}
			else if (leftOperandType.isInt() && rightOperandType.isFloat()) {
				//this.setLeftOperand(this.getLeftOperand().verifyRValue(compiler, localEnv, currentClass, rightOperandType));
				ConvFloat conv = new ConvFloat(this.getLeftOperand());
                conv.setType(rightOperandType);
				this.setLeftOperand(conv);
				this.setType(rightOperandType);
				return rightOperandType;
			}
		}
		this.setType(leftOperandType);
		return leftOperandType;
	}
}
