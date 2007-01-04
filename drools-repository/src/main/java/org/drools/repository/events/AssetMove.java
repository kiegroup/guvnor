package org.drools.repository.events;

public class AssetMove extends AssetChange {

    private String oldFileName;
    private String format;
    private String oldPath;
    private String newFileFormat;
    private String newPath;
    
    public AssetMove(String oldFileName,
                     String format,
                     String oldPath,
                     String newFileFormat,
                     String newPath) {
        super();
        this.oldFileName = oldFileName;
        this.format = format;
        this.oldPath = oldPath;
        this.newFileFormat = newFileFormat;
        this.newPath = newPath;
    }
    
    public String getFormat() {
        return format;
    }
    public String getNewFileFormat() {
        return newFileFormat;
    }
    public String getNewPath() {
        return newPath;
    }
    public String getOldFileName() {
        return oldFileName;
    }
    public String getOldPath() {
        return oldPath;
    }

    
}
