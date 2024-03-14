package ed.inf.adbs.minibase.base;

import ed.inf.adbs.minibase.DatabaseCatalog;
import ed.inf.adbs.minibase.Schema;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * The class is to construct Tuple, using schema and terms data, it can also merge two tuples.
 * **/
public class Tuple {
    public Schema schema;
    public List<Term> terms = new ArrayList<>();

    /**
     * construct tuple
     * @param schm schema of the tuple
     * @param tms value/terms of the tuple
     * **/
    public Tuple(Schema schm, List<Term> tms){
        this.schema = schm;
        this.terms = tms;
    }

    /**
     * construct tuple
     * @param schm schema of the tuple
     * @param lineData determine type of terms
     * **/
    public Tuple(Schema schm, String[] lineData){
        this.schema = schm;
        List<String> attributeTypes = schema.getAttributesTypes();//从schema里get types
        for(int i = 0; i < attributeTypes.size(); i++){
            if(attributeTypes.get(i).equals("int")){
                terms.add(i, new IntegerConstant(Integer.valueOf(lineData[i])));
            }else{
                //string delete ''?
                lineData[i] = lineData[i].replaceAll("\'","");
                terms.add(i, new StringConstant(lineData[i]));
            }
        }
    }

    /**
     * glue two tuples to a new tuple
     * @param tuple1 tuple1 to be merged
     * @param tuple2  tuple2 to be merged
     * **/
    public Tuple(Tuple tuple1, Tuple tuple2){
        List<String> newName = new ArrayList<>();
        List<Term> newAttributes = new ArrayList<>();
        List<String> newAttributeTypes = new ArrayList<>();
        List<Term> newAttributeNames = new ArrayList<>();
        newName.add(tuple1.schema.name);
        newName.add(tuple2.schema.name);

        for(int i = 0; i < tuple1.terms.size(); i++){
            if(!newAttributeNames.contains(tuple1.schema.attributeName.get(i))){
                //newName.add(tuple1.schema.name)
                newAttributes.add(tuple1.terms.get(i));
                newAttributeNames.add(tuple1.schema.attributeName.get(i));
                newAttributeTypes.add(tuple1.schema.attributeType.get(i));
            }
        }

        for(int j = 0; j < tuple2.terms.size(); j++){
            if(!newAttributeNames.contains(tuple2.schema.attributeName.get(j))){
                newAttributes.add(tuple2.terms.get(j));
                newAttributeNames.add(tuple2.schema.attributeName.get(j));
                newAttributeTypes.add(tuple2.schema.attributeType.get(j));
            }
        }

        Schema newSchema = new Schema(newName.toString(), newAttributeTypes, newAttributeNames);
        //Tuple newTuple = new Tuple(newSchema, newAttributes);
        this.schema = newSchema;
        this.terms = newAttributes;
    }

    /**
     *output tuple to output file
     * **/
    public void dump(){
        try{
            String outputFilePath = DatabaseCatalog.getInstance().getOutputFile();
            BufferedWriter bw = new BufferedWriter(new FileWriter(outputFilePath,true));
            String opStr = terms.toString().replace("[","").replace("]","");
            bw.write(opStr+"\n");
            bw.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * return terms
     * @return terms of a tuple
     * **/
    public List<Term> getTerms(){
        return terms;
    }
}
