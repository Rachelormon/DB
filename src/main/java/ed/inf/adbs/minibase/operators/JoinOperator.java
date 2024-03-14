package ed.inf.adbs.minibase.operators;

import ed.inf.adbs.minibase.base.ComparisonAtom;
import ed.inf.adbs.minibase.base.ComparisonOperator;
import ed.inf.adbs.minibase.base.Tuple;

import java.util.ArrayList;
import java.util.List;

/**
 * Construct a simple nested loop join operator using left
 * and right child operator and list of comparison atoms
 * **/
public class JoinOperator extends Operator{
    public Operator leftChild;
    public Operator rightChild;
    public List<ComparisonAtom> cAtom = new ArrayList<>();
    public Tuple leftTuple;
    public Tuple rightTuple;

    /**
     * construct join operator
     * @param leftOperator child operator 1
     * @param rightOperator child operator 2
     * @param  comparisonAtoms a list of comparison atoms used for join conditions
     * **/
    public JoinOperator(Operator leftOperator, Operator rightOperator, List<ComparisonAtom> comparisonAtoms){
        this.leftChild = leftOperator;
        this.rightChild = rightOperator;
        this.cAtom = comparisonAtoms;
        leftTuple = leftChild.getNextTuple();
        rightTuple = rightChild.getNextTuple();
    }

    /**
     * override getNextTuple(), first join left and right tuple together
     * and check if the joined tuple passed the condition, and respectively get the next left and right tuple,
     * only return the tuple that passed the condition.
     * @return t if there is a next tuple
     * **/
    @Override
    public Tuple getNextTuple() {
        while (leftTuple != null){
            Tuple t = new Tuple(leftTuple, rightTuple);
            Selection sel = new Selection(cAtom, t);
            if(!sel.evaluation()){t = null;};

            Tuple rightNext = rightChild.getNextTuple();
            if(rightNext != null){
                rightTuple = rightNext;
            }else{
                rightChild.reset();
                rightTuple = rightChild.getNextTuple();
                leftTuple = leftChild.getNextTuple();
            }
            if(t!=null){
                return t;
            }
        }
        return null;
    }

    /**
     * reset left and right operator
     * **/
    @Override
    public void reset() {
        leftChild.reset();
        rightChild.reset();
    }

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
