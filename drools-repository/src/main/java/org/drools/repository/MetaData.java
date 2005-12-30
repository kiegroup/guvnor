package org.drools.repository;

import java.io.Serializable;
import java.util.Date;

/**
 * This is based on the <a href="http://dublincore.org/documents/dces/"> Dublin
 * Core</a> specification. Not
 * all of these fields will be used by everyone. They should only be used for classification.
 */
public class MetaData
    implements
    Serializable {

    private String title;
    private String creator;
    private String subject;
    private String description;
    private String publisher;
    private String contributor;
    private Date   dateCreated = new Date();
    private String format;
    private String source;
    private String language;
    private String relation;
    private String coverage;
    private String rights;

    public String getContributor(){
        return contributor;
    }

    public void setContributor(String contributor){
        this.contributor = contributor;
    }

    public String getCoverage(){
        return coverage;
    }

    public void setCoverage(String coverage){
        this.coverage = coverage;
    }

    public String getCreator(){
        return creator;
    }

    public void setCreator(String creator){
        this.creator = creator;
    }

    public java.util.Date getDateCreated(){
        return dateCreated;
    }

    public void setDateCreated(java.util.Date dateCreated){
        this.dateCreated = dateCreated;
    }

    public String getDescription(){
        return description;
    }

    public void setDescription(String description){
        this.description = description;
    }

    public String getFormat(){
        return format;
    }

    public void setFormat(String format){
        this.format = format;
    }

    public String getLanguage(){
        return language;
    }

    public void setLanguage(String language){
        this.language = language;
    }

    public String getPublisher(){
        return publisher;
    }

    public void setPublisher(String publisher){
        this.publisher = publisher;
    }

    public String getRelation(){
        return relation;
    }

    public void setRelation(String relation){
        this.relation = relation;
    }

    public String getRights(){
        return rights;
    }

    public void setRights(String rights){
        this.rights = rights;
    }

    public String getSource(){
        return source;
    }

    public void setSource(String source){
        this.source = source;
    }

    public String getSubject(){
        return subject;
    }

    public void setSubject(String subject){
        this.subject = subject;
    }

    public String getTitle(){
        return title;
    }

    public void setTitle(String title){
        this.title = title;
    }

}
