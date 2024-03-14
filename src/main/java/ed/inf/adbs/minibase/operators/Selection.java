package ed.inf.adbs.minibase.operators;

import ed.inf.adbs.minibase.base.*;

import java.util.List;

/**
 * test whether a selection condition holds on a given tuple
 * **/
public class Selection {
    Tuple tuple;
    List<ComparisonAtom> comparisonAtoms;

    /**
     * initialize
     * @param comparisonAtoms comparison atom used for selection
     * @param tuple tuple to be checked
     */
    public Selection(List<ComparisonAtom> comparisonAtoms, Tuple tuple){
        this.comparisonAtoms = comparisonAtoms;
        this.tuple = tuple;
    }

    /**
     * make two terms in comparison atom constants, suitable for compare
     * @param comparisonAtom
     * @param tuple
     * @return new comparison atom taking tuple into account
     */
    public ComparisonAtom checkTerm(ComparisonAtom comparisonAtom, Tuple tuple){
        Term term1;
        Term term2;

        if(comparisonAtom.getTerm1() instanceof Variable){
            String name = ((Variable) comparisonAtom.getTerm1()).getName();
            int index = tuple.schema.getAttTypeIndex(name);
            term1 = tuple.terms.get(index);
        }else {
            term1 = comparisonAtom.getTerm1();
        }
        if(comparisonAtom.getTerm2() instanceof Variable){
            String name2 = ((Variable) comparisonAtom.getTerm2()).getName();
            int index2 = tuple.schema.getAttTypeIndex(name2);
            term2 = tuple.terms.get(index2);
        }else{
            term2 = comparisonAtom.getTerm2();
        }
        ComparisonAtom newComp = new ComparisonAtom(term1, term2, comparisonAtom.getOp());
        return newComp;
    }

    /**
     * check if satisfy selection conditions
     * @param comparisonAtom ca to be checked
     * @return true or false
     * **/
    public boolean compareConditions(ComparisonAtom comparisonAtom){
        Term term1 = comparisonAtom.getTerm1();
        Term term2 = comparisonAtom.getTerm2();
        ComparisonOperator op = comparisonAtom.getOp();

        //if(!term1.getClass().equals(term2.getClass())){return false;} //

        if(term1 instanceof IntegerConstant){
            if(term2 instanceof IntegerConstant){
                return compareInt(((IntegerConstant) term1).getValue(), ((IntegerConstant) term2).getValue(), op);
            }else{return false;}
        }else{
            if(term2 instanceof StringConstant){
                return compareStr(((StringConstant) term1).getValue(),((StringConstant) term2).getValue(), op);
            }else{return false;}
        }
    }

    /**
     * compare when two terms in ca are integer
     * @param value1 term1
     * @param value2 term2
     * @param op comparison operator
     * @return true or false
     */
    public boolean compareInt(int value1, int value2, ComparisonOperator op){
        switch (op){
            case EQ:
                return value1 == value2;
            case GT:
                return value1 > value2;
            case LT:
                return value1 < value2;
            case GEQ:
                return value1 >= value2;
            case LEQ:
                return value1 <= value2;
            case NEQ:
                return value1 != value2;
        }
        return false;
    }

    /**
     *  compare when two terms in ca are string
     * @param str1 term1
     * @param str2 term2
     * @param op comparison operator
     * @return true or false
     */
    public boolean compareStr(String str1, String str2, ComparisonOperator op){
        switch (op){
            case EQ:
                return str1.equals(str2);
            case GT:
                return str1.compareTo(str2) > 0;
            case LT:
                return str1.compareTo(str2) < 0;
            case GEQ:
                return str1.compareTo(str2) >= 0;
            case LEQ:
                return str1.compareTo(str2) <= 0;
            case NEQ:
                return !str1.equals(str2);
        }
        return false;
    }

    /**
     * check if a tuple pass the condition
     * @return true or false
     */
    public boolean evaluation(){
        boolean result = true;
        for( ComparisonAtom compatom : comparisonAtoms){
            if(!compareConditions(checkTerm(compatom, tuple))){
                result = false;
            }
        }
        return result;
    }
}
