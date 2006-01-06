package org.drools.repository;

import java.util.HashSet;
import java.util.Set;

/**
 * The ruleset definition contains a grouping of rules for editing/release.
 * The workingVersionNumber drives what version of rules will be included in this ruleset.
 * Changing this number will mean that different versions of ruledefs are loaded etc.
 * 
 * 
 * @author <a href="mailto:michael.neale@gmail.com"> Michael Neale</a>
 *
 */
public class RuleSetDef extends Persistent {
    private static final long serialVersionUID = 608068118653708104L;
    
    private String name;
    private MetaData metaData;
    private Set rules;
    private Set tags;   
    private long workingVersionNumber; 
    private Set versionHistory;
    private RuleSetAttachment attachment;

    public RuleSetDef(String name, MetaData meta) {
        this.name = name;
        this.metaData = meta;
        this.tags = new HashSet();
        this.rules = new HashSet();
        this.workingVersionNumber = 1;
    }  
    
    /**
     * This is not for public consumption. Use the 
     * proper constructor instead.
     */
    RuleSetDef() {        
    }    
    
    public RuleSetAttachment getAttachment(){
        return attachment;
    }


    public void setAttachment(RuleSetAttachment attachment){
        this.attachment = attachment;
    }


    public Set getVersionHistory(){
        return versionHistory;
    }


    public void setVersionHistory(Set versionHistory){
        this.versionHistory = versionHistory;
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

    public long getWorkingVersionNumber(){
        return workingVersionNumber;
    }

    public void setWorkingVersionNumber(long workingVersionNumber){
        this.workingVersionNumber = workingVersionNumber;
    }

    /** This method increments the version of the ruleset, creating a brand new version.
     * This records the event in the version history.
     * All rules and ruleset-attachments that are connected to this version of the ruleset are 
     */ 
    public void createNewVersion(String comment, String newStatus) {
        
    }
    
    
    
}
