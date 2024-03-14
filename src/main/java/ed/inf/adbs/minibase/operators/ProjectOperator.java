package ed.inf.adbs.minibase.operators;

import ed.inf.adbs.minibase.Schema;
import ed.inf.adbs.minibase.base.Query;
import ed.inf.adbs.minibase.base.Term;
import ed.inf.adbs.minibase.base.Tuple;
import ed.inf.adbs.minibase.base.Variable;

import java.security.PublicKey;
import java.util.ArrayList;
import java.util.List;

/**
 * construct a project operator used for projection
 * **/
public class ProjectOperator extends Operator{
    //public Tuple tuple;
    public Query query;
    public Operator operator;

    /**
     * construct project operator
     * @param query query being evaluated
     * @param operator child operator
     * **/
    public ProjectOperator(Query query, Operator operator){
        //this.tuple = tuple;
        this.query = query;
        this.operator = operator;
    }


    /**
     * override getNextTuple() get variables in query head and find the corresponding position of a term in a tuple
     * and make a new tuple using these matched values, also create a new schema for projected tuples
     * @return newTuple projected from the original one
     * **/
    @Override
    public Tuple getNextTuple() {
        Tuple t = operator.getNextTuple();
        while(t!=null) {
            List<Variable> variabls = query.getHead().getVariables();
            List<String> newAttrType = new ArrayList<>();
            List<Term> newAttrName = new ArrayList<>();
            ArrayList<Term> newTerms = new ArrayList<>();
            for (int i = 0; i < variabls.size(); i++) {
                int idx = t.schema.getAttTypeIndex(String.valueOf(variabls.get(i)));
                //ArrayList<Term> newTerms = new ArrayList<>();
                newTerms.add(t.terms.get(idx));
                newAttrType.add(t.schema.attributeType.get(idx));
                newAttrName.add(t.schema.attributeName.get(idx));
            }
            //List<String> newAttrType = new ArrayList<>();

            Schema newSchema = new Schema(t.schema.name, newAttrType, newAttrName);
            Tuple newTuple = new Tuple(newSchema, newTerms);
            return newTuple;
        }
        return null;
    }

    /**
     * reset operator
     * **/
    @Override
    public void reset() {
        operator.reset();
    }
}
