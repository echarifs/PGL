package fr.ensimag.deca.tree;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.*;
//import fr.ensimag.deca.context.ContextualError;
//import fr.ensimag.deca.tools.IndentPrintStream;
import fr.ensimag.deca.tools.*;
//import org.apache.log4j.Logger;

/**
 *
 * @author gl01
 * @date 01/01/2022
 */
public class ListDeclClass extends TreeList<AbstractDeclClass> {
    //private static final Logger LOG = Logger.getLogger(ListDeclClass.class);
    
    @Override
    public void decompile(IndentPrintStream s) {
        for (AbstractDeclClass declClass : getList()) {
            declClass.decompile(s);
            s.println();
        }
    }


    //private EnvironnementType env_types_predef = new EnvironnementType(NULL);
    /**
     * Pass 1 of [SyntaxeContextuelle]
     */
    void verifyListClass(DecacCompiler compiler) throws ContextualError {

        //env_types_predef.declare(null,new TypeDefinition(new StringType("String"),null));

        //LOG.debug("verify listClass: start");
        //throw new UnsupportedOperationException("not yet implemented");  
        for (AbstractDeclClass c : getList()) {
            c.verifyClass(compiler);
        }
        //LOG.debug("verify listClass: end");
    }

    /**
     * Pass 2 of [SyntaxeContextuelle]
     */
    public void verifyListClassMembers(DecacCompiler compiler) throws ContextualError {
        //throw new UnsupportedOperationException("not yet implemented");
        for (AbstractDeclClass c : getList())
            c.verifyClassMembers(compiler);
    }
    
    /**
     * Pass 3 of [SyntaxeContextuelle]
     */
    public void verifyListClassBody(DecacCompiler compiler) throws ContextualError {
        //throw new UnsupportedOperationException("not yet implemented");
        for (AbstractDeclClass c : getList())
            c.verifyClassBody(compiler);
    }


}
