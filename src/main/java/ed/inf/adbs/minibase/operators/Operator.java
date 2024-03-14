package ed.inf.adbs.minibase.operators;


import ed.inf.adbs.minibase.base.Term;
import ed.inf.adbs.minibase.base.Tuple;

import java.util.ArrayList;
import java.util.List;

/**
 * abstract class of Operator
 * **/
public abstract class Operator {
    public abstract Tuple getNextTuple();
    public abstract void reset();

    /**
     * output joined tuples, create a list to store dumped tuples
     * in order to output only distinct tuples
     * **/
    public void dump(){
        Tuple tuple = getNextTuple();
        List<String> dumpList = new ArrayList<>();
        while(tuple != null){
            //make sure only distinct tuples are dumped
            if(dumpList.contains(tuple.getTerms().toString())){
                tuple = getNextTuple();
                continue;}
            dumpList.add(tuple.getTerms().toString());
            tuple.dump();
            //System.out.println(tuple);
            tuple = getNextTuple();
        }
    }
}
