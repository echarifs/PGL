/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package fr.ensimag.deca.tree;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.EnvironmentExp;
import fr.ensimag.deca.context.Signature;
import fr.ensimag.deca.tools.IndentPrintStream;
//import java.util.Iterator;

/**
 *
 * @author gl01
 */
public class ListDeclParam extends TreeList<AbstractDeclParam>{

    @Override
    public void decompile(IndentPrintStream s) {
        int cpt = 0;
        for (AbstractDeclParam param : getList()) {
            param.decompile(s);
            if (cpt < (getList().size() - 1)) {
		        s.print(", ");
            }
            cpt++;
        }
    }
    
    public Signature verifyListDeclParam(DecacCompiler compiler, EnvironmentExp envLocal) throws ContextualError {
	    Signature signatures = new Signature();
        for (AbstractDeclParam p : getList()) {
            signatures.add(p.verifyDeclParam(compiler, envLocal));
        }
	    return signatures;
	}

    
}
