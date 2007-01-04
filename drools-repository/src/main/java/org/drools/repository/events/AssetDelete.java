package org.drools.repository.events;

public class AssetDelete extends AssetChange {

    private String fileName;
    private String format;
    private String path;
    
    public String getFileName() {
        return fileName;
    }
    
    public String getFormat() {
        return format;
    }
    public String getPath() {
        return path;
    }

    public AssetDelete(String fileName,
                       String format,
                       String path) {
        super();
        this.fileName = fileName;
        this.format = format;
        this.path = path;
    }

    
    
    
}
