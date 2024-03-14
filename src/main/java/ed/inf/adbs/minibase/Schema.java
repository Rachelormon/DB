package ed.inf.adbs.minibase;

import ed.inf.adbs.minibase.base.Term;

import java.util.HashMap;
import java.util.List;

/**
 * construct schema
 **/
public class Schema {
    public String name;
    public List<String> attributeType;
    public List<Term> attributeName;
    public HashMap<String, Integer> attPosMap = new HashMap<>();

    public Schema(String name, List<String> attType, List<Term> attName) {
        this.name = name;
        this.attributeType = attType;
        this.attributeName = attName;
        setAttTypeIndex(attType, attName);
    }

    /**
     * get attribute type through schema
     * @return attributetype
     */
    public List<String> getAttributesTypes(){
        return attributeType;
    }

    /**
     * create mapping between attirbute name and type index
     * @param attributeType
     * @param attributeName
     * @return attPosMap
     * **/
    public HashMap<String, Integer> setAttTypeIndex(List<String> attributeType, List<Term> attributeName){
        attPosMap = new HashMap<>();
        for(int i = 0; i < attributeName.size(); i++){
            attPosMap.put(attributeName.get(i).toString(), i);
        }
        return attPosMap;
    }

    /**
     * get attribute type index using attribute name
     * @param attName
     * @return attribute type index
     **/
    public int getAttTypeIndex(String attName){
        return attPosMap.get(attName);
    }
}
