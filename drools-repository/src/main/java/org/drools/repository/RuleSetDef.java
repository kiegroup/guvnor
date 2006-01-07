package org.drools.repository;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * The ruleset definition contains a grouping of rules for editing/release.
 * The workingVersionNumber drives what version of rules will be included in this ruleset.
 * Changing this number will mean that different versions of ruledefs are loaded etc.
 * 
 * @author <a href="mailto:michael.neale@gmail.com"> Michael Neale</a>
 */
public class RuleSetDef extends Persistent implements Comparable {
    private static final long serialVersionUID = 608068118653708104L;
    
    private String name;
    private MetaData metaData;
    private Set rules;
    private Set tags;   
    private long workingVersionNumber; 
    private Set versionHistory;
    private Set attachments;

    public RuleSetDef(String name, MetaData meta) {
        this.name = name;
        this.metaData = meta;
        this.tags = new HashSet();
        this.rules = new HashSet();
        this.attachments = new HashSet();
        this.versionHistory = new HashSet();
        this.workingVersionNumber = 1;
    }  
    
    /**
     * This is not for public consumption. Use the 
     * proper constructor instead.
     */
    RuleSetDef() {        
    }    
    

    public Set getVersionHistory(){
        return versionHistory;
    }


    public void setVersionHistory(Set versionHistory){
        this.versionHistory = versionHistory;
    }

    /** 
     * This adds a rule to the ruleset.
     * 
     * If the rule already has an Id, and it is a different version number, then
     * it will be copied for this ruleset.
     * If it has the same version number, then it will be shared.
     */
    public RuleSetDef addRule(RuleDef rule) {
        if (rule.getId() == null) {
            rule.setVersionNumber(this.workingVersionNumber);
            this.rules.add(rule);            
        } else if (rule.getVersionNumber() == this.workingVersionNumber) {
            this.rules.add(rule);
        } else {
            RuleDef copy = rule.copy();
            copy.setVersionNumber(this.workingVersionNumber);
            this.rules.add(copy);            
        }
        return this;
    }
    
    public RuleSetDef addAttachment(RuleSetAttachment attachmentFile) {
        attachmentFile.setVersionNumber(this.workingVersionNumber);
        this.attachments.add(attachmentFile);
        return this;
    }
    
    public MetaData getMetaData(){
        return metaData;
    }
    public void setMetaData(MetaData metaData){
        this.metaData = metaData;
    }
    
    
    /** The list of rules that are currently loaded for this ruleset */
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

    /**
     * This will only be set when loading the RuleSet from the repository.
     * When you load a ruleset, a version number is specified. 
     * This property is not persistent, as multiple people could be working on different versions
     * at the same time. 
     * 
     * DO NOT set this property.
     * 
     * @param workingVersionNumber
     */
    public void setWorkingVersionNumber(long workingVersionNumber){
        this.workingVersionNumber = workingVersionNumber;
    }

    /** 
     * This method increments the working version of the ruleset, creating a brand new version.
     * This records the event in the version history.
     * 
     * Typically you would call this method when you want to make a stable version of a rule set (lock in all 
     * the related assets) and then move on to an "editing" version. You can always switch back to a previous version 
     * of a rulebase.
     * 
     * All rules and ruleset-attachments etc that are 
     * connected to this version of the ruleset are cloned with the new workingVersionNumber.
     * 
     * This means that the previous state of the RuleSet is kept in tact (for instance, as a release of rules).
     * Rules can then be edited, removed and so on without effecting any previous versions of rules and the ruleset.
     * 
     * Previous rules can be retrieved by changing the value of workingVersionNumber.
     * 
     * Note that further to this, rules themselves will be versioned on save (think of that versioning as 
     * "minor" versions, and this sort of ruleset versions as major versions).
     * 
     * Ideally once a new version is created, the RuleSet should be stored and then loaded fresh, 
     * which will hide the non working versions of the rules.
     * 
     */ 
    public void createNewVersion(String comment, String newStatus) {

        this.workingVersionNumber++;
        RuleSetVersionInfo newVersion = new RuleSetVersionInfo();
        newVersion.setStatus(newStatus);
        newVersion.setVersionNumber(this.workingVersionNumber);
        this.versionHistory.add(newVersion);
        
        //as the Ids are null, copied objects 
        //will get a new identity, and have the new workingVersionNumber        
        
        //now have to create new rules and add to the collection
        createNewRuleVersions( comment, this.workingVersionNumber );
        
//        //create new attachment
//        for ( Iterator iter = this.attachments.iterator(); iter.hasNext(); ) {
//            RuleSetAttachment att = (RuleSetAttachment) iter.next();
//            //TODO: need too finish this.
//            att.copy();
//            
//        }

        //create new functions, app data and imports etc.
        System.out.println("DON'T FORGET FUNCTIONS ETC !!");
        
    }

    private void createNewRuleVersions(String comment, long newVersionNumber){
        Set newVersions = new HashSet();
        for ( Iterator iter = this.rules.iterator(); iter.hasNext(); ) {
            RuleDef old = (RuleDef) iter.next();
            if (old.getVersionNumber() == newVersionNumber - 1) {
                //we only want to clone rules that are for the version being cloned
                RuleDef clone = old.copy();
                clone.setVersionComment(comment);
                clone.setVersionNumber(newVersionNumber);
                newVersions.add(clone);
            }
        }
        this.rules.addAll(newVersions);
    }
    
    public String toString() {
        return "{ name=" + this.name + " , workingVersionNumber=" + this.workingVersionNumber + " }";
    }

    /** The name provides the natural ordering */
    public int compareTo(Object arg){
        if (arg instanceof RuleSetDef) {
            return ((RuleSetDef) arg).name.compareTo(this.name);
        }
        return 0;
    }

    public Set getAttachments(){
        return attachments;
    }

    private void setAttachments(Set attachments){
        this.attachments = attachments;
    }
    
    
}
