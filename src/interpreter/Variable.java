//Variable class to store variables
package interpreter;

/**
 *
 * @author Nathan Moore
 */
public class Variable{
    String name;
    String type;
    String value;
    public Variable(String name, String type, String value){
        setName(name);
        setType(type);
        setValue(value);
    }
    
    public Variable(String name, String type){
        setName(name);
        setType(type);
    }
    private void setName(String name){
        this.name = name;
    }
    private void setType(String type){
        this.type = type;
    }
    private void setValue(String value){
        this.value = value;
    }
    public boolean equals(Variable v){
       return true; //later
    }
}
