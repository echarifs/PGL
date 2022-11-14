package fr.ensimag.deca.context;

import java.util.Map;
import java.util.Set;
import java.util.LinkedHashMap;

import fr.ensimag.deca.tools.SymbolTable.Symbol;
//import fr.ensimag.ima.pseudocode.DAddr;
//import fr.ensimag.ima.pseudocode.Register;
//import fr.ensimag.ima.pseudocode.RegisterOffset;

/**
 * Dictionary associating identifier's ExpDefinition to their names.
 * 
 * This is actually a linked list of dictionaries: each EnvironmentExp has a
 * pointer to a parentEnvironment, corresponding to superblock (eg superclass).
 * 
 * The dictionary at the head of this list thus corresponds to the "current" 
 * block (eg class).
 * 
 * Searching a definition (through method get) is done in the "current" 
 * dictionary and in the parentEnvironment if it fails. 
 * 
 * Insertion (through method declare) is always done in the "current" dictionary.
 * 
 * @author gl01
 * @date 01/01/2022
 */
public class EnvironmentExp
{
    // A FAIRE : implémenter la structure de donnée représentant un
    // environnement (association nom -> définition, avec possibilité
    // d'empilement).

    Map<Symbol, ExpDefinition> EnvExp;

    EnvironmentExp parentEnvironment;
    
    public EnvironmentExp(EnvironmentExp parentEnvironment)
    {
        this.parentEnvironment = parentEnvironment;
        this.EnvExp = new LinkedHashMap<Symbol, ExpDefinition>();
    }

    public static class DoubleDefException extends Exception
    {
        private static final long serialVersionUID = -2733379901827316441L;
        public DoubleDefException(String errorMessage){
            super(errorMessage);
        }
    }

    public Set<Map.Entry<Symbol, ExpDefinition>> entrySet()
    {
        return this.EnvExp.entrySet();
    }

    public Set<Symbol> keySet()
    {
        return this.EnvExp.keySet();
    }

    /**
     * Return the definition of the symbol in the environment, or null if the
     * symbol is undefined.
     */
    public ExpDefinition get(Symbol key)
    {
        //throw new UnsupportedOperationException("not yet implemented");
        EnvironmentExp localEnv = this;
        while (localEnv != null)
        {
            for (Map.Entry<Symbol, ExpDefinition> symbol : localEnv.entrySet())
                if (symbol.getKey().getName().equals(key.getName()))
                    return symbol.getValue();
            localEnv = localEnv.getParentEnv();
        }
        return null;
    }

    // added this one as well v

    public EnvironmentExp getParentEnv()
    {
        return this.parentEnvironment;
    }

    public boolean localContains(Symbol key) {
        for (Symbol symbol : this.EnvExp.keySet())
            if (symbol.getName().equals(key.getName()))
                return true;
        return false;
    }

    public boolean Contains(Symbol key) {
        EnvironmentExp localEnv = this;
        while (localEnv != null)
        {
            for (Symbol symbol : localEnv.keySet())
                if (symbol.getName().equals(key.getName()))
                    return true;
            localEnv = localEnv.getParentEnv();
        }
        return false;
    }

    // added this one as well ^

    /**
     * Add the definition def associated to the symbol name in the environment.
     * 
     * Adding a symbol which is already defined in the environment,
     * - throws DoubleDefException if the symbol is in the "current" dictionary 
     * - or, hides the previous declaration otherwise.
     * 
     * @param name
     *            Name of the symbol to define
     * @param def
     *            Definition of the symbol
     * @throws DoubleDefException
     *             if the symbol is already defined at the "current" dictionary
     *
     */
    public void declare(Symbol name, ExpDefinition def) throws DoubleDefException
    {
        //throw new UnsupportedOperationException("not yet implemented");
        /*for (Symbol symbol : EnvExp.keySet())
            if (symbol.getName().equals(name.getName()))
                throw new DoubleDefException("Double declaration dans le meme env");
        this.EnvExp.put(name, def);*/
        if (this.EnvExp.containsKey(name)){
            throw new DoubleDefException("Double declaration dans le meme env");
        }
        this.EnvExp.put(name, def);
    }
    /*public Map<Symbol, ExpDefinition> getEnvExp(){
        return this.EnvExp;
    }
    public void empilement(EnvironmentExp other){
        for(Symbol s : other.getEnvExp().keySet()){
            if(!this.EnvExp.containsKey(s)){
                EnvExp.put(s, other.getEnvExp().get(s));
            }
        }
    }*/

}
