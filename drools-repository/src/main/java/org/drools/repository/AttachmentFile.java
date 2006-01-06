package org.drools.repository;

/** 
 * Simply hold an individual attachment. Attachments stored using this are truly opaque.
 * If they are DRL files, then they may be able to be stored and searched as text,
 * but this is database specific (DRL files should be stored normalised via importing).
 * @author <a href="mailto:michael.neale@gmail.com"> Michael Neale</a>
 */
public class AttachmentFile extends Persistent {
    
    private byte[] content;
    private String contentType;
    private String originalFileName;
    
    AttachmentFile() {}
    
    
    public AttachmentFile(byte[] content,
                          String contentType,
                          String originalFileName){
        super(); 
        this.content = content;
        this.contentType = contentType;
        this.originalFileName = originalFileName;
    }


    public byte[] getContent(){
        return content;
    }
    public void setContent(byte[] content){
        this.content = content;
    }
    public String getContentType(){
        return contentType;
    }
    public void setContentType(String contentType){
        this.contentType = contentType;
    }
    public String getOriginalFileName(){
        return originalFileName;
    }
    public void setOriginalFileName(String originalFileName){
        this.originalFileName = originalFileName;
    }
    
}
