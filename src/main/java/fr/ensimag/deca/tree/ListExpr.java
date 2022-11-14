package fr.ensimag.deca.tree;

//import fr.ensimag.deca.context.Type;
//import fr.ensimag.deca.DecacCompiler;
//import fr.ensimag.deca.context.ClassDefinition;
//import fr.ensimag.deca.context.ContextualError;
//import fr.ensimag.deca.context.EnvironmentExp;
import fr.ensimag.deca.tools.IndentPrintStream;
import java.util.Iterator;

/**
 * List of expressions (eg list of parameters).
 *
 * @author gl01
 * @date 01/01/2022
 */
public class ListExpr extends TreeList<AbstractExpr> {
    


    @Override
    public void decompile(IndentPrintStream s) {
        //throw new UnsupportedOperationException("Not yet implemented");
        
        Iterator<AbstractExpr> iteratorExpr = this.iterator();
	while (iteratorExpr.hasNext()) {
            iteratorExpr.next().decompile(s);
            if (iteratorExpr.hasNext()) {
		s.print(", ");
            }
	}
    }
}
