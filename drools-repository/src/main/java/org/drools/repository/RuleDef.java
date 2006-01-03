package org.drools.repository;

import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class RuleDef extends Persistent {

    private String name;
    private long versionNumber;
    private String content;
    private MetaData metaData;
    private String status;
    private boolean checkedOut;    
    private String checkedOutBy;
    private String versionComment;
    private Set tags;
    private String documentation;
    private Date effectiveDate;
    private Date expiryDate;   
    private boolean deleted;

    
    public boolean isDeleted(){
        return deleted;
    }



    public void setDeleted(boolean deleted){
        this.deleted = deleted;
    }



    /**
     * Use tagging to aid with searching and sorting of large numbers of rules.
     * Tags should not effect the versioning of the rules.
     *  
     *
     */
    public RuleDef addTag(String tag) {
        this.tags.add(new Tag(tag));
        return this;
    }
    

    
    public RuleDef() {}
    
    /**
     * This is for creating a brand new rule.
     * @param name
     * @param content
     */
    public RuleDef(String name, String content) {
        this.name = name;
        this.content = content;
        this.versionNumber = 1;
        this.head = true;   
        this.tags = new HashSet();
    }
    /**
     * This little cheat tells the repo that this
     * rule is at the head of versions.
     */
    private boolean head;
    
    
    public String getContent(){
        return content;
    }
    public void setContent(String content){
        this.content = content;
    }

    public MetaData getMetaData(){
        return metaData;
    }
    public void setMetaData(MetaData metaData){
        this.metaData = metaData;
    }
    public String getStatus(){
        return status;
    }
    public void setStatus(String status){
        this.status = status;
    }

    public boolean isCheckedOut(){
        return checkedOut;
    }
    public void setCheckedOut(boolean checkedOut){
        this.checkedOut = checkedOut;
    }
    public String getCheckedOutBy(){
        return checkedOutBy;
    }
    public void setCheckedOutBy(String checkOutBy){
        this.checkedOutBy = checkOutBy;
    }
    public boolean isHead(){
        return head;
    }
    public void setHead(boolean isHead){
        this.head = isHead;
    }
    public String getVersionComment(){
        return versionComment;
    }
    public void setVersionComment(String versionComment){
        this.versionComment = versionComment;
    }
    public long getVersionNumber(){
        return this.versionNumber;
    }
    private void setVersionNumber(long versionNumber){
        this.versionNumber = versionNumber;
    }
    public String getDocumentation(){
        return documentation;
    }
    public void setDocumentation(String documentation){
        this.documentation = documentation;
    }
    public Set getTags(){
        return tags;
    }
    private void setTags(Set tags){
        this.tags = tags;
    }
    public Date getEffectiveDate(){
        return effectiveDate;
    }
    public void setEffectiveDate(Date effectiveDate){
        this.effectiveDate = effectiveDate;
    }
    public Date getExpiryDate(){
        return expiryDate;
    }
    public void setExpiryDate(Date expiryDate){
        this.expiryDate = expiryDate;
    }
    public String getName(){
        return name;
    }
    public RuleDef setName(String name){
        this.name = name;
        return this;
    }

    /** return a list of tags as Strings */
    public String[] listTags() {

        String[] tagList = new String[tags.size()];
        int i = 0;
        for ( Iterator iter = tags.iterator(); iter.hasNext(); ) {
            Tag tag = (Tag) iter.next();
            tagList[i] = tag.getTag();                   
            i++;
        }
        return tagList;
    }
    
    public RuleDef createNewVersion() {
//        if (this.checkedOut) {
//            throw new RuleRepositoryLockException("Rule is checked out by " + this.checkedOutBy);
//        }
        RuleDef newVersion = new RuleDef();
        newVersion.content = this.content;        
        this.head = false;
        newVersion.head = true;
        newVersion.documentation = documentation;
        newVersion.effectiveDate = this.effectiveDate;
        newVersion.expiryDate = this.expiryDate;
        if (this.metaData != null) {
            newVersion.metaData = this.metaData.copy();
        }                
        newVersion.name = this.name;
        newVersion.status = "";
        newVersion.tags = this.copyTags();
        newVersion.versionNumber = this.versionNumber + 1;
        return newVersion;
    }



    private Set copyTags() {
        Set newTags = new HashSet();
        for ( Iterator iter = this.tags.iterator(); iter.hasNext(); ) {
            Tag tag = (Tag) iter.next();
            newTags.add(tag);
        }
        return newTags;
    }



//    public boolean equals(Object arg){
//        if (arg.getClass() != this.getClass()) return false;
//        RuleDef other = (RuleDef) arg;
//        return (other.versionNumber == this.versionNumber 
//                && other.name.equals(this.name));        
//    }
//
//
//
//    public int hashCode(){
//        int result = this.name.hashCode();
//        return new Long(versionNumber).hashCode() + 27 * result;
//    }
    
    
    
    
}
