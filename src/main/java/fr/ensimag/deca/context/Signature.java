package fr.ensimag.deca.context;

import java.util.ArrayList;
import java.util.List;

/**
 * Signature of a method (i.e. list of arguments)
 *
 * @author gl01
 * @date 01/01/2022
 */
public class Signature {
    List<Type> args = new ArrayList<Type>();

    public void add(Type t) {
        args.add(t);
    }
    
    public Type paramNumber(int n) {
        return args.get(n);
    }
    
    public int size() {
        return args.size();
    }
    
    public List<Type> getArgs(){
        return args;
    }
    public boolean same(Signature sigother){
        for (Type t : args){
            if(!sigother.args.contains(t)){
                return false;
            }
        }
        return true;
    }
}
