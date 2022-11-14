package fr.ensimag.deca.context;

import java.util.Map;

import java.util.LinkedHashMap;

import fr.ensimag.deca.tools.SymbolTable.Symbol;

public class EnvironmentType
{
    Map<Symbol, TypeDefinition> EnvType;

    public EnvironmentType()
    {
        this.EnvType = new LinkedHashMap<Symbol, TypeDefinition>();    
    }

    public TypeDefinition get(Symbol key)
    {
        for (Map.Entry<Symbol, TypeDefinition> symbol : this.EnvType.entrySet())
            if (symbol.getKey().getName().equals(key.getName()))
                return symbol.getValue();
        return null;
    }

    public void declare(Symbol Typename, TypeDefinition def)
    {
        this.EnvType.put(Typename, def);
    }
    
    public boolean isdeclared(Symbol symbol){
        return this.EnvType.containsKey(symbol);
    }
}
