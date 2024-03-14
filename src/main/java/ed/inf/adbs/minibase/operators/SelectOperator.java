package ed.inf.adbs.minibase.operators;

import ed.inf.adbs.minibase.base.ComparisonAtom;
import ed.inf.adbs.minibase.base.Tuple;

import java.util.List;

/**
 * construct a select operator
 */
public class SelectOperator extends Operator{
    //get next tuple
    //check whether pass conditions
    public ScanOperator scanOperator;
    public List<ComparisonAtom> comparisonAtoms;

    /**
     * initialize
     * @param scanOperator child operator
     * @param comparisonAtoms select conditions
     */
    public SelectOperator(ScanOperator scanOperator, List<ComparisonAtom> comparisonAtoms){
        this.scanOperator = scanOperator;
        this.comparisonAtoms = comparisonAtoms;
    }

    /**
     * getNextTuple() get tuple from scan operator
     * and check if the tuple pass the condition
     * return if pass
     * @return tuple that pass the condition
     */
    @Override
    public Tuple getNextTuple() {
        Tuple t = scanOperator.getNextTuple();
        while(t != null){
            Selection sel = new Selection(comparisonAtoms, t);
            boolean b = sel.evaluation();
            if(b){return t;}
            t = scanOperator.getNextTuple();
            }
        return null;
    }

    /**
     * reset the operator
     */
    @Override
    public void reset() {
        scanOperator.reset();
    }
}
