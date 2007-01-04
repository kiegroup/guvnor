package org.drools.repository.events;

public class AssetCreate extends AssetChange {

    private String content;
    private String fileName;
    private String format;
    private String path;
    
    public String getContent() {
        return content;
    }
    public String getFileName() {
        return fileName;
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
    
    public AssetCreate(String content,
                       String fileName,
                       String format,
                       String path) {
       
        this.content = content;
        this.fileName = fileName;
        this.format = format;
        this.path = path;
    }
    
    
    
}
