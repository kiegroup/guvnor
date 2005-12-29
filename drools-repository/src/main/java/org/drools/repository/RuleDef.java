package org.drools.repository;

import java.util.Date;
import java.util.Set;

public class RuleDef extends Persistent {

    private String name;
    private Long versionNumber;
    private String content;
    private MetaData metaData;
    private String status;
    private boolean checkedOut;    
    private String checkOutBy;
    private String versionComment;
    private Set tags;
    private String documentation;
    private Date effectiveDate;
    private Date expiryDate;     
    private Date dateSaved;
    
    public Date getDateSaved(){
        return dateSaved;
    }
    private void setDateSaved(Date dateSaved){
        this.dateSaved = dateSaved;
    }
    
    public RuleDef() {}
    /**
     * This little cheat tells the repo that this
     * rule is at the head of versions.
     */
    private boolean isHead;
    
    
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
    public Long getVersion(){
        return versionNumber;
    }
    private void setVersion(Long version){
        this.versionNumber = version;
    }
    public boolean isCheckedOut(){
        return checkedOut;
    }
    public void setCheckedOut(boolean checkedOut){
        this.checkedOut = checkedOut;
    }
    public String getCheckOutBy(){
        return checkOutBy;
    }
    public void setCheckOutBy(String checkOutBy){
        this.checkOutBy = checkOutBy;
    }
    public boolean isHead(){
        return isHead;
    }
    public void setHead(boolean isHead){
        this.isHead = isHead;
    }
    public String getVersionComment(){
        return versionComment;
    }
    public void setVersionComment(String versionComment){
        this.versionComment = versionComment;
    }
    public Long getVersionNumber(){
        return versionNumber;
    }
    private void setVersionNumber(Long versionNumber){
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
    public void setTags(Set tags){
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

    
    
}
