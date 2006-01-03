package org.drools.repository;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class RuleSetDef extends Persistent {
    
    private String name;
    private MetaData metaData;
    private Set rules;
    private Set tags;    
    
    public RuleSetDef(String name, MetaData meta) {
        this.name = name;
        this.metaData = meta;
        this.tags = new HashSet();
        this.rules = new HashSet();
    }
    
    /**
     * This is not for public consumption. Use the 
     * proper constructor instead.
     */
    RuleSetDef() {        
    }
    
    public RuleSetDef addRule(RuleDef rule) {
        this.rules.add(rule);
        return this;
    }
    
    

    public MetaData getMetaData(){
        return metaData;
    }
    public void setMetaData(MetaData metaData){
        this.metaData = metaData;
    }
    public Set getRules(){
        return rules;
    }
    private void setRules(Set rules){
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
