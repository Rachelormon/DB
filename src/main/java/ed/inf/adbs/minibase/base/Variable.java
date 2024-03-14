package ed.inf.adbs.minibase.base;

public class Variable extends Term {
    private String name;

    public Variable(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return name;
    }

    @Override
    public boolean equals(Object object){
        if((object instanceof Variable)){
            String n = ((Variable) object).getName();
            return this.name.equals(n);
        }
        return false;
    }

    @Override
    public int hashCode(){
        return this.name.hashCode();
    }
}
