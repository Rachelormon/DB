package ed.inf.adbs.minibase;

import ed.inf.adbs.minibase.base.*;
import ed.inf.adbs.minibase.parser.QueryParser;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.*;

/**
 *
 * Minimization of conjunctive queries
 *
 */
public class CQMinimizer {

    public static void main(String[] args) {

        if (args.length != 2) {
            System.err.println("Usage: CQMinimizer input_file output_file");
            return;
        }

        String inputFile = args[0];
        String outputFile = args[1];

        minimizeCQ(inputFile, outputFile);


        //System.out.println("hello");

        //parsingExample(inputFile);
    }

    /**
     * CQ minimization procedure
     *
     * Assume the body of the query from inputFile has no comparison atoms
     * but could potentially have constants in its relational atoms.
     *
     * @param1 input file name
     * @param2 output file name
     */
    public static void minimizeCQ(String inputFile, String outputFile) {
        // TODO: add your implementation
        //load file
        try {
            Query query = QueryParser.parse(Paths.get(inputFile));//parse the query
            Query minimizedQuery = checkQueryHomomorphism(query);
            writeOutputFile(outputFile, minimizedQuery);
            //System.out.println("MinimizedQuery: " + query);

        }
        catch (Exception e)
        {
            System.err.println("Exception occurred during parsing");
            e.printStackTrace();
        }
        // Write the minimized query back to file
    }

   /**Check whether one relational atom can be mapped to another
    * @param atom1 relational atom1 to be deleted
    * @param atom2 relational atom2 to be mapped
    * **/
    public static boolean checkAtomMapping(RelationalAtom atom1, RelationalAtom atom2) {
        //List<Term>term1 = atom1.getTerms();
        //list<Term>term2 = atom2.getTerms();
        //List<Variable>headVariable= head.getVariables();

        //1.check whether the relation names are equal
        if (!atom1.getName().equals(atom2.getName())) return false;
        //2.check whether term numbers are the same
        if (atom1.getTerms().size() != atom2.getTerms().size()) return false;
        //3.check the terms within two atoms are the same

        if(checkAtomsEqual(atom1, atom2)) return false;

        return true;
    }

    /** if one atom can be mapped to another, than creat mapping
    * @param atom1  relational atom1
    * @param atom2 relational atom2
     **/
    public static HashMap<Term, Term> createMapping(RelationalAtom atom1, RelationalAtom atom2){
        HashMap<Term, Term> map = new HashMap<>();

        //put all term-term into map
        for(int i=0; i<atom1.getTerms().size(); i++){
            map.put(atom1.getTerms().get(i),atom2.getTerms().get(i));
        }

        for(Term key : map.keySet()){
            boolean a = key.equals(map.get(key));
            if(key instanceof Constant && !(key.equals(map.get(key)))){
                map.clear();;
                break;
            }
        }
        return map;
    }

    /** add new mapping to the current map
     * @param map1 the old map?
     * @param map2 the new map
     * @return new map
     * **/
    public static HashMap<Term, Term> addToMapping(HashMap<Term, Term>map1, HashMap<Term, Term>map2){
        //HashMap<Term, Term> newMap = new HashMap<>();
        HashMap<Term, Term> mapAfterAdding = new HashMap<>();
        //if(map1.isEmpty() || map2.isEmpty()) return null;
        Set<Term> key1 = map1.keySet();
        Set<Term> key2 = map2.keySet();
        HashSet<Term> keys = new HashSet<>(key1);
        keys.retainAll(key2);
        HashSet<Term> overlapepedKeys = new HashSet<>(keys);

        boolean isAdding = true;
        if(overlapepedKeys.size()!=0){
            for(Term olpedkeys : overlapepedKeys){
                if(!map1.get(olpedkeys).equals(map2.get(olpedkeys))){
                    isAdding = false;
                    break;
                }
            }
            //HashMap<Term, Term> mapAfterAdding = new HashMap<>();
            if(isAdding) {
                mapAfterAdding.putAll(map1);
                mapAfterAdding.putAll((map2));
            }else{mapAfterAdding.putAll(map2);}
        }else{
            mapAfterAdding.putAll(map1);
            mapAfterAdding.putAll(map2);
        }


        return mapAfterAdding;
    }


    /** check whether two atoms are equal
     * @param atom1  relational atom1
     * @param atom2 relational atom2
     * **/
    private static boolean checkAtomsEqual(RelationalAtom atom1, RelationalAtom atom2) {
//1.check whether the relation names are equal
        if (!atom1.getName().equals(atom2.getName())) {
            return false;
        }
        //2.check whether term numbers are the same
        if (atom1.getTerms().size() != atom2.getTerms().size()) {
            return false;
        }
        //3.check the terms within two atoms are the same
        for (int i = 0; i < atom1.getTerms().size() ; i++) {
         if (!checkTermsEqual(atom1.getTerms().get(i),atom2.getTerms().get(i))) {
         return false;
            }
         }
        return true;
    }

    /** check whether two terms are the same
     * @param term1
     * @para2 term2
     * **/
    private static boolean checkTermsEqual(Term term1, Term term2) {
        if (!term1.getClass().equals(term2.getClass())) return false;

        return term1.equals(term2);
    }

    /**
     * find query homomorphism for query
     * @param query
     * @return new query
     */
    public static Query checkQueryHomomorphism(Query query){
        List<Variable> head1Variables = query.getHead().getVariables();
        Head head = query.getHead();

        List<Atom> body = query.getBody();
        //int searchCursor = body.size();

        Query expectedQuery = new Query(head, body);
        List<Atom> expectedQueryBody = expectedQuery.getBody();

        while(true) {
            boolean changed = false;
            for (int i = 0; i < body.size(); i++) {
                Query tempQuery = QueryParser.parse(query.toString());
                Head tempHead = tempQuery.getHead();
                List<Atom> tempBody = tempQuery.getBody();
                tempBody.remove(i);

                if (queryHomomorphismValid(query, tempQuery, searchPossibleMapping(query, tempQuery,i))) {
                    body.remove(i);
                    changed = true;
                    break;
                }
            }
            if(!changed)break;
        }
        return query;
    }


    /**
     * Use deep first search on two queries to see if there is possible mapping between two atoms
     * @param query1
     * @param query2
     * @param searchCursor
     * @return map
     * **/
    public static HashMap<Term, Term> searchPossibleMapping(Query query1, Query query2, int searchCursor){
        //searchIndex useless??????
        List<Atom> body1 = query1.getBody();
        List<Atom> body2 = query2.getBody();

        //1. if having searched all atoms in query1, that is the final result of this search

        //2. if not, using search index to search
        HashMap<Term, Term> currentMap = new HashMap<>();
        //for(int i = 0; i< body1.size(); i++){
            RelationalAtom relationalAtom1 = (RelationalAtom) body1.get(searchCursor); //convert Atom to Relational Atom
            for(int j = 0; j< body2.size();j++){
                RelationalAtom relationalAtom2 = (RelationalAtom) body2.get(j); //convert Atom to Relational Atom
                if(checkAtomMapping(relationalAtom1, relationalAtom2)){
                    //create & add mapping
                    currentMap = addToMapping(createMapping(relationalAtom1, relationalAtom2),currentMap);
                    }else{
                    //jump out of the loop
                    break;
                }

            }
        //}
        return currentMap;
    }

    /**
     * check head
     * @param query1
     * @param query2
     * @param map
     * @return
     * **/
    public static boolean queryHomomorphismValid(Query query1, Query query2, HashMap<Term, Term> map){
        Head head1 = query1.getHead();
        Head head2 = query2.getHead();
        List<Variable> head1Variables = head1.getVariables();

        boolean b = map.isEmpty();
        if(map.isEmpty())return false;

        for(Variable variable : head1Variables){
            Term a = map.get(variable);
            if(map.containsKey(variable)) {
                if (!variable.equals(map.get(variable))) {
                    return false;
                }
            }
        }

        if(!head1.getName().equals(head2.getName())){
            return false;
        }

        return true;
    }

    /**
     * write results to file
     * @param fileName output file name
     * @param minimizedQuery
     * @throws IOException
     */
    public static void writeOutputFile(String fileName, Query minimizedQuery) throws IOException {
        FileWriter fileWriter = new FileWriter(fileName);
        fileWriter.write(minimizedQuery.toString());
        fileWriter.close();
    }

    /**
     * Example method for getting started with the parser.
     * Reads CQ from a file and prints it to screen, then extracts Head and Body
     * from the query and prints them to screen.
     */

    public static void parsingExample(String filename) {

        try {
            Query query = QueryParser.parse(Paths.get(filename));//parse the query
            // Query query = QueryParser.parse("Q(x, y) :- R(x, z), S(y, z, w)");
            // Query query = QueryParser.parse("Q(x) :- R(x, 'z'), S(4, z, w)");

            //return the parsed queries, head&body
            System.out.println("Entire query: " + query);
            Head head = query.getHead();
            System.out.println("Head: " + head);
            List<Atom> body = query.getBody();
            System.out.println("Body: " + body);
        }
        catch (Exception e)
        {
            System.err.println("Exception occurred during parsing");
            e.printStackTrace();
        }
    }

}
