package fr.ensimag.deca.context;

//import fr.ensimag.deca.context.ClassType;
//import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.tools.SymbolTable.Symbol;
import fr.ensimag.deca.tree.Location;

/**
 * Deca Type (internal representation of the compiler)
 *
 * @author gl01
 * @date 01/01/2022
 */

public abstract class Type {


    /**
     * True if this and otherType represent the same type (in the case of
     * classes, this means they represent the same class).
     */
    public abstract boolean sameType(Type otherType);

    private final Symbol name;

    public Type(Symbol name) {
        this.name = name;
    }

    public Symbol getName() {
        return name;
    }

    @Override
    public String toString() {
        return getName().toString();
    }

    public boolean isClass() {
        return false;
    }

    public boolean isInt() {
        return false;
    }

    public boolean isFloat() {
        return false;
    }

    public boolean isBoolean() {
        return false;
    }

    public boolean isVoid() {
        return false;
    }

    public boolean isString() {
        return false;
    }

    public boolean isNull() {
        return false;
    }

    public boolean isClassOrNull() {
        return false;
    }

    public boolean equals(Type type)
    {
        return this.name.getName().equals(type.getName().getName()) ? true : false;
    }

    /**
     * Returns the same object, as type ClassType, if possible. Throws
     * ContextualError(errorMessage, l) otherwise.
     *
     * Can be seen as a cast, but throws an explicit contextual error when the
     * cast fails.
     */
    public ClassType asClassType(String errorMessage, Location l)
            throws ContextualError {
        throw new ContextualError(errorMessage, l);
    }
    public boolean subType(EnvironmentType env_types,  Type other) {
        if ( other == null || this == null) {
            return false; //gotta reconsider that and reconsider definition of object's env_exp
        }
        if (this.equals(other)) {
            return true;
        }

        if (this.isClass() && other.isClass()) {
            ClassDefinition typec1 = (ClassDefinition) env_types.get(this.getName());
            ClassDefinition typec2 = (ClassDefinition) env_types.get(other.getName());
            if (typec2.getSuperClass() == null) {
                return true;
            }

            if (typec2.equals(typec1.getSuperClass())) {
                //System.out.println(typec2.getType().getName());
                //System.out.println(typec1.getSuperClass().getType().getName());
                return true;
            }
            ClassDefinition C = typec1.getSuperClass();
            if (C == null){
                return false;
            }
            Type c = C.getType();
            if (c.subType(env_types,  other)) {
                return true;
            }
        }
        return false;
    }
}
