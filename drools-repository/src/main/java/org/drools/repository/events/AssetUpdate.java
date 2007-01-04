package org.drools.repository.events;

public class AssetUpdate extends AssetChange {

    private String oldContent;
    private String newContent;
    private String fileName;
    private String format;
    private String path;
    
    public String getOldContent() {
        return oldContent;
    }
    public String getFileName() {
        return fileName;
    }
    public String getNewContent() {
        return newContent;
    }
    
    
    /** This is in effect the "file extension" as well as the format type. */
    public String getFormat() {
        return format;
    }
    
    /** 
     * The path is the directory path to the resource in "file" terms (not JCR terms). 
     * Forward slash delimited.
     */
    public String getPath() {
        return path;
    }
    
    public AssetUpdate(String oldContent,
                       String newContent,
                       String fileName,
                       String format,
                       String path) {
       
        this.oldContent = oldContent;
        this.newContent = newContent;
        this.fileName = fileName;
        this.format = format;
        this.path = path;
    }    
    
}
