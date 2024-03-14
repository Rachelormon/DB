package ed.inf.adbs.minibase.operators;

import ed.inf.adbs.minibase.DatabaseCatalog;
import ed.inf.adbs.minibase.Schema;
import ed.inf.adbs.minibase.base.RelationalAtom;
import ed.inf.adbs.minibase.base.Term;
import ed.inf.adbs.minibase.base.Tuple;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

/**
 * Construct a scan operator to dump all tuples in a relation
 */
public class ScanOperator extends Operator{
    public RelationalAtom relationalAtom;
    public Schema schema;
    public DatabaseCatalog dbCatalog = DatabaseCatalog.getInstance();
    public String fileName;
    public String root = System.getProperty("user.dir");
    String dbFilePath;
    public BufferedReader br;
    //public String fileNm = DatabaseCatalog.getInstance().getFilePath(relationalAtom.getName());
    //public BufferedReader br = new BufferedReader(new FileReader(fileNm));//get db file here

    /**
     * initialize
     * @param reAtom relational atom to be scanned
     * @param scm schema of the relation
     * @throws FileNotFoundException
     */
    public ScanOperator(RelationalAtom reAtom, Schema scm) throws FileNotFoundException {
        this.relationalAtom = reAtom;//
        this.schema = scm;
        this.fileName = DatabaseCatalog.getInstance().getFilePath(relationalAtom.getName());
        this.dbFilePath = root + "/" + fileName;
        br = new BufferedReader(new FileReader(dbFilePath));
    }

    /**
     * getNextTuple() directly from reading db files
     * and make it a tuple using data read from the file and schema information
     * @return tuple
     */
    @Override
    public Tuple getNextTuple() {
        //String fileNm = DatabaseCatalog.getInstance().getFilePath(relationalAtom.getName());
        try{
            //BufferedReader br = new BufferedReader(new FileReader(fileName));//get db file here
            String fileLn = br.readLine();
            if(fileLn != null){//if null, it's the last line
                //split line
                fileLn.trim();
                String[] data = fileLn.split(",\\s+");
                //tuple
                Tuple tuple = new Tuple(schema, data);
                //output
                return tuple;
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    /**
     * reset the operator by resetting the buffered reader
     * **/
    @Override
    public void reset() {
        try{
            br = new BufferedReader(new FileReader(fileName));
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void dump() {
        super.dump();
    }
}
