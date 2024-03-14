package ed.inf.adbs.minibase.operators;

import ed.inf.adbs.minibase.DatabaseCatalog;
import ed.inf.adbs.minibase.Schema;
import ed.inf.adbs.minibase.base.*;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

/**
 * First do check on all relational atoms because there may be impicit joins and selections,
 * e.g., implicit equi-join: Q(x) :- R(x, y), S(y, z) , implicit selectio: Q(x, y) :- R(x, y, 4).
 * By replacing the same variable term in relational atom to make each variable has a unique name
 * and create comparison atoms for those terms. After rewriting the query, start generate query plan,
 * create scan operator for every relational atom, and check if there is selection conditions on this atom,
 * if so, create a select operator.
 * Fix the first relational atom as the left child and choose the next relational atom as the right child,
 * create join operator on this basis and if there is no join conditions, simply merge the tuples.
 * Do group by sum aggregation after join if there is one, otherwise do projection and output the results using dump().
 * **/
public class QueryPlan {
    public Query query;
    //public List<Atom> body;
    public List<RelationalAtom> relationalAtoms = new ArrayList<>();
    public List<ComparisonAtom> comparisonAtoms = new ArrayList<>();
    public List<String> varInRA = new ArrayList<>();
    public HashMap<String, String> varRelationMap = new HashMap<>();
    public List<ComparisonAtom> selectConditions = new ArrayList<>();
    public List<ComparisonAtom> joinConditions = new ArrayList<>();

    /**initialize query through checking implicit join and selection
     * Since there may be new variables created, creat a map to store the variable name and its relation
     * @param query input query
     */
    public QueryPlan(Query query) {

        this.query = query;
        Head head = query.getHead();
        List<Atom> body = query.getBody();
        for(Atom atom : body){
            if(atom instanceof RelationalAtom){
                for(Term term : ((RelationalAtom) atom).getTerms()){
                    if(term instanceof Variable && !varInRA.contains(term.toString())){
                        varInRA.add(term.toString());
                        //varRelationMap.put(term.toString(),((RelationalAtom) atom).getName());
                    }
                }
            }
        }

        //check implicit selection
        for (Atom atom : body) {
            if (atom instanceof RelationalAtom) {
                implicitSelection((RelationalAtom) atom);
            } else {
                comparisonAtoms.add((ComparisonAtom) atom);
            }
        }
        //check implicit equi-join
        implicitJoin(relationalAtoms);

        //match variables and relation
        for(Atom atom : relationalAtoms){
                for(Term term : ((RelationalAtom) atom).getTerms()){
                    if(term instanceof Variable){
                        //varInRA.add(term.toString());
                        varRelationMap.put(term.toString(),((RelationalAtom) atom).getName());
                    }
                }
        }
    }


    /**
     * check implicit selection, if there is a constant in a relational atom,
     * then replace the constant with a new variable and create a new comparison atom.
     * e.g. Q(x, y) :- R(x, y, 4)-->Q(x, y) :- R(x, y, xx), xx = 4
     * @param ra relational atom to be checked
     */
    public void implicitSelection(RelationalAtom ra){
        List<Term> terms = ra.getTerms();
        List<Term> newTerms = new ArrayList<>();
        List<ComparisonAtom> newComparisonAtoms = new ArrayList<>();
        for(int i = 0; i < terms.size(); i++){
            if(terms.get(i) instanceof Constant){
                Variable newVariableName = new Variable(newVaraible());//generate a new variable
                ComparisonAtom newCondition = new ComparisonAtom(newVariableName, terms.get(i), ComparisonOperator.EQ);
                newTerms.add(newVariableName);
                newComparisonAtoms.add(newCondition);
                //RelationalAtom newRa = new RelationalAtom(ra.getName(), )
            }else{
                newTerms.add(terms.get(i));
            }
        }

        //delete the old one and add new one
        RelationalAtom newRa = new RelationalAtom(ra.getName(), newTerms);
        relationalAtoms.remove(ra);
        relationalAtoms.add(newRa);
        if(!newComparisonAtoms.isEmpty()){
            for (ComparisonAtom ca : newComparisonAtoms){
                comparisonAtoms.add(ca);
                }
            }
    }

    /**
     * check implicit join conditions, if there are same name in two ra,
     * change the latter one to a new name and create a comparison atom on this.
     * e.g. Q(x) :- R(x, y), S(y, z)-->Q(x) :- R(x, y), S(yy, z), y = yy
     * @param reAtoms relational atom to be checked
     */
    public void implicitJoin(List<RelationalAtom> reAtoms){
        for(int i = 0; i < reAtoms.size(); i++){
            for(int j = i+1; j < reAtoms.size(); j++){
                RelationalAtom ra1 = reAtoms.get(i);
                RelationalAtom ra2 = reAtoms.get(j);
                List<Term> ra2Terms = ra2.getTerms();
                for(Term term : ra1.getTerms()){
                    for(int m = 0; m < ra2.getTerms().size(); m++){
                        if(term.equals(ra2.getTerms().get(m))){
                            Variable ra2NewVar = new Variable(newVaraible());//generate new name for ra2's variable
                            ra2Terms.remove(m);
                            ra2Terms.add(m, ra2NewVar);
                            RelationalAtom newRa2 = new RelationalAtom(ra2.getName(),ra2Terms);
                            ComparisonAtom newComp = new ComparisonAtom(term, ra2NewVar, ComparisonOperator.EQ);
                            this.relationalAtoms.remove(ra2);
                            this.relationalAtoms.add(newRa2);
                            this.comparisonAtoms.add(newComp);
                        }
                    }
                }
            }
        }
    }

    /**
     * generate a new variable name for ra using lowercase letters and length is 2
     * possible length should be expanded for larger db
     * @return newVar the new variable name
     */
    public String newVaraible(){
        char[] str = {'a','b','c','d','e','f','g','h','i','j','k','l','m',
                'n','o','p','q','r','s','t','u','v','w','x','y','z'};//lowercase letters used to generate new variables
        int len = 2;
        int count = 0;
        int randomNumber;

        StringBuilder tempVar = new StringBuilder();
        Random random = new Random();
        while (count < len){
            randomNumber = random.nextInt(str.length);
            if(randomNumber >= 0 && randomNumber < str.length){
                tempVar.append(str[randomNumber]);
                count++;
            }
        }
        String newVar = tempVar.toString();

        //check if duplicate
        if(varInRA.contains(newVar)){
            newVar = newVaraible();
            varInRA.add(newVar);
        } else {
            varInRA.add(newVar);
        }
        return newVar;
    }

    /**
     * check which kind condition a comparison atom is, select or join
     * select: 1 variable or 2 variable in the same relation or two constants
     * join: 2 variables in different relations
     */
    public void checkConditions(){
        for(ComparisonAtom ca : comparisonAtoms){
            if(ca.getTerm1() instanceof Variable && ca.getTerm2() instanceof Variable){
                if(varRelationMap.get(((Variable) ca.getTerm1()).getName())
                        .equals(varRelationMap.get(((Variable) ca.getTerm2()).getName()))){
                    selectConditions.add(ca);
                }else{
                    joinConditions.add(ca);
                }
            }else{
                selectConditions.add(ca);
            }
        }
    }

    /**
     * check if there is related select conditions on this relational atom
     * @param ra relational atom to be checked
     * @return list of comparison atoms related to selection
     */
    public List<ComparisonAtom> checkSelectionConditions(RelationalAtom ra){
        List<Term> raTerms = ra.getTerms();
        List<Term> caTerms = new ArrayList<>();
        List<ComparisonAtom> selectList = new ArrayList<>();
        for(ComparisonAtom ca : selectConditions){
            if(ca.getTerm1() instanceof Variable){
                caTerms.add(ca.getTerm1());
            }else{
                if(ca.getTerm2() instanceof Variable){
                    caTerms.add(ca.getTerm2());
                }else{
                    selectList.add(ca);
                }
            }
            if(raTerms.containsAll(caTerms)){
                selectList.add(ca);
            }
            //return selectList;
        }
        return selectList;
    }

    /**
     * check if there is related join conditions on these two groups of terms, i.e. already joined terms & ready to join terms
     * @param termList1 already joined terms
     * @param termList2 ready to join terms
     * @return list of comparison atoms related to join
     */
    public List<ComparisonAtom> checkJoinConditions(List<Term> termList1, List<Term> termList2){
        List<Term> tempList = new ArrayList<>();
        List<Term> caTerms = new ArrayList<>();
        List<ComparisonAtom> joinList = new ArrayList<>();
        tempList.addAll(termList1);
        tempList.addAll(termList2);
        for(ComparisonAtom ca : joinConditions){
            caTerms.add(ca.getTerm1());
            caTerms.add(ca.getTerm2());
            if(tempList.containsAll(caTerms)){
                joinList.add(ca);
            }
        }
        return joinList;
    }

    /**
     * create scan operator for every relational atom, and check if there is selection conditions on this atom,
     * if so, create a select operator.
     * Fix the first relational atom as the left child and choose the next relational atom as the right child,
     * create join operator on this basis and if there is no join conditions, simply merge the tuples.
     * Do group by sum aggregation after join if there is one, otherwise do projection and output the results using dump().
     * @return the root operator to start evaluation
     * @throws FileNotFoundException
     */
    public Operator generateQueryPlan() throws FileNotFoundException {
        //categorize the comparison atoms
        checkConditions();
        RelationalAtom ra = relationalAtoms.get(0);
        Schema schema = DatabaseCatalog.getInstance().constructSchema(ra);
        //create a scan operator for the first ra
        Operator root = new ScanOperator(ra, schema);
        List<Term> leftTerms = new ArrayList<>();
        //check if there is related select conditions, if so, create a select operator
        List<ComparisonAtom> relatedSelection = checkSelectionConditions(ra);
        if(!relatedSelection.isEmpty()){
            root = new SelectOperator((ScanOperator) root, relatedSelection);
        }
        leftTerms.addAll(ra.getTerms());
        //do join with other ra, for these ras, do scan and select first too
        for(int i=1; i < relationalAtoms.size(); i++){
            RelationalAtom ra2 = relationalAtoms.get(i);
            Schema schema2 = DatabaseCatalog.getInstance().constructSchema(ra2);
            Operator root2 = new ScanOperator(ra2, schema2);
            relatedSelection = checkSelectionConditions(ra2);
            if(!relatedSelection.isEmpty()){
                root2 = new SelectOperator((ScanOperator) root2, relatedSelection);
            }
            root = new JoinOperator(root, root2, checkJoinConditions(leftTerms, ra2.getTerms()));
            leftTerms.addAll(ra2.getTerms());
        }

        // do aggregation if there is a sum aggregate in head
        if(query.getHead().getSumAggregate()!=null){
            root = new SumOperator(root, query.getHead());
            return root;
        }

        //do projection if there is no sum aggregate
        int headSize = query.getHead().getVariables().size();
        //if(query.getHead().getVariables().equals(varInRA))
        if(headSize != varInRA.size()){
            root = new ProjectOperator(query, root);
        }

        return root;
    }
}
