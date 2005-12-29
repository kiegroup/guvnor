package org.drools.repository;

import java.util.List;

public class RuleSetDef extends Persistent {
    
    private String name;
    private MetaData metaData;
    private List rules;
    public MetaData getMetaData(){
        return metaData;
    }
    public void setMetaData(MetaData metaData){
        this.metaData = metaData;
    }
    public List getRules(){
        return rules;
    }
    public void setRules(List rules){
        this.rules = rules;
    }
    public String getName(){
        return name;
    }
    public void setName(String name){
        this.name = name;
    }
    
    
    
}
