package ed.inf.adbs.minibase.operators;

import ed.inf.adbs.minibase.DatabaseCatalog;
import ed.inf.adbs.minibase.base.Query;
import ed.inf.adbs.minibase.parser.QueryParser;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 *a top-level interpreter class that reads the statement from the query file.
 * **/
public class Interpreter {
    QueryPlan queryPlan;
    String inputFile;

    /**
     * construct an interpreter using input file
     * @param inputFile
     * **/
    public Interpreter(String inputFile){
        this.inputFile = inputFile;
        try{
            Query query = QueryParser.parse(Paths.get(inputFile));
            //System.out.println(query.getHead().getSumAggregate().getProductTerms());
            queryPlan = new QueryPlan(query);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        //queryPlan = new QueryPlan(query);
    }

    /**
     * gain root operator from generated query plan
     * **/
    public void dump() throws FileNotFoundException {
        Operator root = queryPlan.generateQueryPlan();
        root.dump();
    }
}
