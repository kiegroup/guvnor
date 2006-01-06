package org.drools.repository;

import java.util.HashSet;
import java.util.Set;

/**
 * A RuleSetAttachment contains a ruleset that is stored in a non-normalised format.
 * An attachment may be a spreadsheet for instance. Or it may be a HTML document and a properties file.
 * An "attachment" will contain one or more AttachmentFiles, which actually wraps the content.
 * 
 * These are versioned along with the ruleset (with optional per save versioning).
 * 
 * @author <a href="mailto:michael.neale@gmail.com"> Michael Neale</a>
 *
 */
public class RuleSetAttachment extends Persistent {

    private String typeOfAttachment;
    private Set attachments = new HashSet();
    private String name;
    private long versionNumber = 1;
    
    
    public RuleSetAttachment(String typeOfAttachment,
                             String name){
        super();       
        this.typeOfAttachment = typeOfAttachment;
        this.name = name;
        
    }

    public RuleSetAttachment() {
    }
    
    public String getName(){
        return name;
    }


    public void setName(String name){
        this.name = name;
    }


    public Set getAttachments(){
        return attachments;
    }


    private void setAttachments(Set attachments){
        this.attachments = attachments;
    }


    public RuleSetAttachment addFile(AttachmentFile file) {
        this.attachments.add(file);
        return this;
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

    public void setVersionNumber(long versionNumber){
        this.versionNumber = versionNumber;
    }
    
    
    
}
