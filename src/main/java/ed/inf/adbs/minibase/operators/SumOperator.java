package ed.inf.adbs.minibase.operators;

import ed.inf.adbs.minibase.DatabaseCatalog;
import ed.inf.adbs.minibase.base.Head;
import ed.inf.adbs.minibase.base.Term;
import ed.inf.adbs.minibase.base.Tuple;
import ed.inf.adbs.minibase.base.Variable;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

/**
 *reads all the output from its child, extracts relevant values into tuples, organizes tuples into
 * groups, and for each group computes an aggregate value.
 */
public class SumOperator extends Operator{
    public Operator operator;
    public Head head;
    public List<Term> variable;
    public int sum = 0;

    /**
     * initialize
     * @param operator child operator
     * @param head query head contains sum aggregate
     */
    public SumOperator(Operator operator, Head head){
        this.operator = operator;
        this.variable = head.getSumAggregate().getProductTerms();
        this.head = head;
    }
    @Override
    public Tuple getNextTuple() {
        return null;
    }

    @Override
    public void reset() {

    }

    /**
     * calculate sum using product terms in query head.
     * get tuple from child operator and extract values using product terms
     * calculate the product of each value and sum of all products
     * write result to file using writeOutputFile()
     */
    public void dump(){
        Tuple tuple;

        //no groupByVariable
        List<Term> productTerms = head.getSumAggregate().getProductTerms();
        tuple = this.operator.getNextTuple();
        while(tuple!=null){
            int product = 1;
            for(Term term : productTerms){
                if(term instanceof Variable){
                    int idx = tuple.schema.getAttTypeIndex(term.toString());
                    int value = Integer.parseInt(String.valueOf(tuple.terms.get(idx)));
                    product = product * value;
                }else{//if term is a constant
                //int idx = tuple.schema.getAttTypeIndex(term.toString());
                //int value = Integer.parseInt(String.valueOf(tuple.terms.get(idx)))
                    int value = 1;
                    product = product * value;
                }
            }
            sum = sum + product;
            tuple = this.operator.getNextTuple();
        }
        writeOutputFile();
    }

    /**
     * write the results to output file
     */
    public void writeOutputFile(){
        String outputFilePath = DatabaseCatalog.getInstance().getOutputFile();
        try{
        BufferedWriter bw = new BufferedWriter(new FileWriter(outputFilePath,true));
        //String opStr = terms.toString().replace("[","").replace("]","");
        bw.write(Integer.toString(sum)+"\n");
        bw.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
