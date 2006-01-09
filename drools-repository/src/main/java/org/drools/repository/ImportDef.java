package org.drools.repository;

/**
 * This holds a type import for a ruleset. 
 * @author <a href="mailto:michael.neale@gmail.com"> Michael Neale</a>
 */
public class ImportDef extends Persistent
    implements
    IVersionable {
    
    private static final long serialVersionUID = -5509795356918241886L;
    
    private long versionNumber;
    private String versionComment;
    private String type;
    
    /**
     * @param type The type to import into the ruleset.
     */
    public ImportDef(String type) {
        this.type = type;
    }
    
    ImportDef() {}
    
    /** The type to import into the ruleset */
    public String getType() {
        return type;
    }
    public void setType(String type) {
        this.type = type;
    }
    public String getVersionComment() {
        return versionComment;
    }
    public void setVersionComment(String versionComment) {
        this.versionComment = versionComment;
    }
    public long getVersionNumber() {
        return versionNumber;
    }
    public void setVersionNumber(long versionNumber) {
        this.versionNumber = versionNumber;
    }

    public IVersionable copy() {
        // TODO Auto-generated method stub
        return new ImportDef(this.type);
    }
    
    


}
