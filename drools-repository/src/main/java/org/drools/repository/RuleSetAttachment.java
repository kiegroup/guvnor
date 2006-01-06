package org.drools.repository;


/**
 * A RuleSetAttachment may contain a ruleset that is stored in a non-normalised format.
 * An attachment may be a spreadsheet for instance. Or it may be a HTML document and a properties file.
 * It can even be a plain old DRL file.
 * 
 * Attachments can also be miscellanious files, such as test scripts or documentation. The deployer will 
 * use the typeOfAttachment property to work out what to do with it.
 * 
 * These are versioned along with the ruleset (with per save versioning as well).
 * 
 * @author <a href="mailto:michael.neale@gmail.com"> Michael Neale</a>
 *
 */
public class RuleSetAttachment extends Persistent {


    private static final long serialVersionUID = 7474038734785975202L;
    
    
    private byte[] content;  
    private String originalFileName;
    
    private String typeOfAttachment;
    
    private String name;
    private long versionNumber = 1;
    
    
    public RuleSetAttachment(String typeOfAttachment,
                             String name, 
                             byte[] content, 
                             String originalFileName ) {
        super();       
        this.typeOfAttachment = typeOfAttachment;
        this.name = name;
        this.content = content;
        this.originalFileName = originalFileName;
        
    }

    RuleSetAttachment() {
    }
    
    public String getName(){
        return name;
    }


    public void setName(String name){
        this.name = name;
    }


    public String getTypeOfAttachment(){
        return typeOfAttachment;
    }
    public void setTypeOfAttachment(String typeOfAttachment){
        this.typeOfAttachment = typeOfAttachment;
    }

    public long getVersionNumber(){
        return versionNumber;
    }

    void setVersionNumber(long versionNumber){
        this.versionNumber = versionNumber;
    }

    public byte[] getContent(){
        return content;
    }

    public void setContent(byte[] content){
        this.content = content;
    }

    public String getOriginalFileName(){
        return originalFileName;
    }

    public void setOriginalFileName(String originalFileName){
        this.originalFileName = originalFileName;
    }
    
    
    
}
