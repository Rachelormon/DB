package ed.inf.adbs.minibase;

import ed.inf.adbs.minibase.base.RelationalAtom;
import ed.inf.adbs.minibase.base.Term;
import ed.inf.adbs.minibase.base.Variable;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * a class that keep track of information such as where a file for a given relation is located,
 * what the schema of different relations is, and so on.
 * use singleton pattern
 * **/
public class DatabaseCatalog {
    private static DatabaseCatalog instance = null;
    private String dbDir;
    private String inputFile;
    private  String outputFile;
    private  String schemaFile;
    private HashMap<String, List<String>> schemaMap = new HashMap<>();
    String root = System.getProperty("user.dir");


    private DatabaseCatalog(){}

    public static DatabaseCatalog getInstance(){
        if(instance == null){
            instance = new DatabaseCatalog();
        }
        return instance;
    }

    /**
     * initialize
     * @param dbDir db directory contains schema and db files
     * @param inputFile input query file
     * @param outputFile output query file
     */
    public void databaseCatalogInitialise(String dbDir, String inputFile, String outputFile){
        //String sFilePath = dbDir+"/schema.txt".toString();
        //String root = System.getProperty("user.dir");
        String sFilePath = root + "/" + dbDir + "/schema.txt";
        this.dbDir = dbDir;
        this.inputFile = inputFile;
        this.outputFile = outputFile;
        this.schemaMap = getSchema();
        //String sFilePath = dbDir+"/schema.txt".toString();
        this.schemaFile = root + "/" + dbDir + "/schema.txt";
    }

    /**
     * store the schema into a map
     * @return schemaMap stores relation name and attributes type
     * **/
    public HashMap<String, List<String>> getSchema(){
        String sFilePath = root + "/" + dbDir + "/schema.txt";
        try{
           BufferedReader br = new BufferedReader(new FileReader(sFilePath));
           String schemaLine = br.readLine();
           while(schemaLine != null){
               schemaLine.trim();
               String[] data = schemaLine.split("\\s+");
               String relationName = data[0];
               List<String> attributesType = new ArrayList<>();
               for(int i = 1; i< data.length; i++){
                   attributesType.add(data[i]);
               }
               schemaMap.put(relationName, attributesType);
               schemaLine = br.readLine();
           }
           br.close();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return schemaMap;
    }

    /**
     * construct a schema for a relational atom
     * @param ra relational atom
     * @return a schema
     */
    public Schema constructSchema(RelationalAtom ra){
        DatabaseCatalog.getInstance().getSchema().get(ra.getName());
        List<Term> attType = ra.getTerms();
        Schema schema = new Schema(ra.getName(), DatabaseCatalog.getInstance().getSchema().get(ra.getName()), ra.getTerms());
        return schema;
    }

    /**
     * get the attributes of a relation
     * @param dbDir db directory
     * @param SchemaName schema name
     * **/
    public List<String> getAttributes(String dbDir, String SchemaName){
        return getSchema().get(SchemaName);
    }

    /**
     * get the file path according file name
     * @param filename
     * @return file path
     */
    public String getFilePath(String filename){
        return instance.dbDir + "/files/" + filename + ".csv";
    }

    /**
     * get output file name
     * @return output file
     */
    public String getOutputFile(){
        return outputFile;
    }

}
