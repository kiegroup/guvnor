package org.drools.repository;

/**
 * A FunctionDef contains the definition of a function that is used in one or more rules.
 * Functions can be written in any language that is supported by the semantic framework.
 *  
 * @author <a href="mailto:michael.neale@gmail.com"> Michael Neale</a>
 */
public class FunctionDef extends Persistent
    implements
    IVersionable {

    private static final long serialVersionUID = -7585928690157539970L;
    
    private long versionNumber;
    private String versionComment;
    private String functionContent;
    private String description;
    private String language;
    
    public String getLanguage(){
        return language;
    }

    public void setLanguage(String language){
        this.language = language;
    }

    public FunctionDef(String functionContent, String description) {
        this.functionContent = functionContent;
        this.description = description;
        this.language = "";
    }
    
    FunctionDef() {}
    
    public IVersionable copy(){
        FunctionDef clone = new FunctionDef(this.functionContent, this.description);
        clone.language = this.language;
        clone.versionNumber = this.versionNumber;
        clone.versionComment = this.versionComment;
        return clone;
    }    

    public String getDescription(){
        return description;
    }

    public void setDescription(String description){
        this.description = description;
    }

    public String getFunctionContent(){
        return functionContent;
    }

    public void setFunctionContent(String functionContent){
        this.functionContent = functionContent;
    }



    public void setVersionNumber(long versionNumber){
       
        this.versionNumber = versionNumber;
    }

    public void setVersionComment(String comment){
        this.versionComment = comment;
    }

    public long getVersionNumber(){
        return this.versionNumber;
    }

    public String getVersionComment(){        
        return this.versionComment;
    }

}
