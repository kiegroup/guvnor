package org.drools.brms.server.util;

import org.apache.commons.fileupload.FileItem;

public class FormData {
    private FileItem file;
    private String   uuid;
    
    
    public FormData() {
    }

    public FileItem getFile() {
        return file;
    }
    public void setFile(FileItem file) {
        this.file = file;
    }
    public String getUuid() {
        return uuid;
    }
    public void setUuid(String uuid) {
        this.uuid = uuid;
    }
    
    public void closeFile() {
    }
    
}
