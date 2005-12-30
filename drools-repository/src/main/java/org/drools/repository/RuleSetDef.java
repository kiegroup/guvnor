package org.drools.repository;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class RuleSetDef extends Persistent {
    
    private String name;
    private MetaData metaData;
    private List rules;
    private long versionNumber;
    private Set tags;
    
    public RuleSetDef(String name, MetaData meta) {
        this.name = name;
        this.metaData = meta;
        this.versionNumber = 1;
        this.tags = new HashSet();
    }
    
    /**
     * This is not for public consumption. Use the 
     * proper constructor instead.
     */
    public RuleSetDef() {        
    }
    
    
    public long getVersionNumber(){
        return versionNumber;
    }
    private void setVersionNumber(long versionNumber){
        this.versionNumber = versionNumber;
    }
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

    public Set getTags(){
        return tags;
    }

    private void setTags(Set tags){
        this.tags = tags;
    }
    
    public RuleSetDef addTag(String tag) {
        this.tags.add(new Tag(tag));
        return this;
    }
    
    
    
}
