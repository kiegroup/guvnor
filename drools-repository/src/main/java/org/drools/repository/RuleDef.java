package org.drools.repository;

import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class RuleDef extends Persistent implements IVersionable {


    private static final long serialVersionUID = -677781085801764266L;
    
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

    


    /**
     * Use tagging to aid with searching and sorting of large numbers of rules.
     * Tags should not effect the versioning of the rules.
     */
    public RuleDef addTag(String tag) {
        this.tags.add(new Tag(tag));
        return this;
    }
    
    public RuleDef addTag(Tag tag) {
        this.tags.add(tag);
        return this;
    }
        
    RuleDef() {}
    
    /**
     * This is for creating a brand new rule.
     * @param name Name of the MUST BE UNIQUE in the repository.
     * The only time duplicate names exist are for different versions of rules.
     * @param content
     */
    public RuleDef(String name, String content) {
        this.name = name;
        this.content = content;
        this.versionNumber = 1;
        this.tags = new HashSet();
    }

    
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

    public String getVersionComment(){
        return versionComment;
    }
    public void setVersionComment(String versionComment){
        this.versionComment = versionComment;
    }
    public long getVersionNumber(){
        return this.versionNumber;
    }
    public void setVersionNumber(long versionNumber){
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
    public void setName(String name){
        this.name = name;        
    }

    /** 
     * Return a list of tags as Strings. Tags are stored as Tag objects, 
     * but are essentially strings. 
     */
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
    
    /**
     * Copy the tags. It is allowable to reuse the same Tag identities.
     */
    private Set copyTags() {
        Set newTags = new HashSet();
        for ( Iterator iter = this.tags.iterator(); iter.hasNext(); ) {
            Tag tag = (Tag) iter.next();
            newTags.add(tag);
        }
        return newTags;
    }

    /**
     * This is used for versioning.
     */
    public IVersionable copy() {
        RuleDef newVersion = new RuleDef();
        newVersion.content = this.content;        
        newVersion.documentation = this.documentation;
        newVersion.effectiveDate = this.effectiveDate;
        newVersion.expiryDate = this.expiryDate;
        if (this.metaData != null) {
            newVersion.metaData = this.metaData.copy();
        }                
        newVersion.name = this.name;
        newVersion.status = "";
        newVersion.tags = this.copyTags();        
        return newVersion;
    }
    
    public String toString() {
        return "{ id = " + this.getId() 
            +  " name = (" + this.name + ") version = " 
            + this.getVersionNumber() + " }";
    }
    
}
