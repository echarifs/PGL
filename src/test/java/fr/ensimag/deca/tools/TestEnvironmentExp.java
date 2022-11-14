package fr.ensimag.deca.tools;
//import java.lang.String;
import fr.ensimag.deca.context.EnvironmentExp;
import fr.ensimag.deca.context.EnvironmentExp.DoubleDefException;

//import fr.ensimag.deca.tools.SymbolTable;
import fr.ensimag.deca.tools.SymbolTable.Symbol;
import fr.ensimag.deca.context.VariableDefinition;
import fr.ensimag.deca.context.IntType;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;


//Class de test pour EnvironmentExp.java

public class TestEnvironmentExp{
    @Test
    public void testEnvi(){
        EnvironmentExp testE = new EnvironmentExp(null); //Les definitions necessaires
        SymbolTable sym = new SymbolTable();
        Symbol Sx = sym.create("x");
        IntType typ = new IntType(Sx); //Type x
        VariableDefinition def = new VariableDefinition(typ,null);
        Symbol Sy = sym.create("y");
        try{
            testE.declare(Sy, def); //On teste la declaration de (y,def)
        }
        catch(DoubleDefException e){
            //"TEST Echoue! Erreur inattendue!
            assertSame(1,0); //On signale une erreur sur JUnit
        }

        //On teste la double declaration maintenant
        Symbol Sprime = sym.create("y"); //Symbole existant
        try{
            testE.declare(Sprime, def); //On teste la declaration de (y,def)
            //Test Echoue! Erreur attendue non detectee!
            assertSame(1,0);
        }
        catch(DoubleDefException e){
            //On ne fait rien: Test reussi
        }
        
    }
}