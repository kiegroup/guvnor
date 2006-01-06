package org.drools.repository.db;

import org.drools.repository.AttachmentFile;
import org.drools.repository.RuleSetAttachment;

public class AttachmentPersistTest extends PersistentCase {

    public void testLoadSave() {
        RuleSetAttachment at = new RuleSetAttachment();
        at.setName("RS1");
        at.setTypeOfAttachment("DRL");
        
        AttachmentFile file = new AttachmentFile("blah".getBytes(), "text", "blah.drl");
        at.addFile(file);
        
        RepositoryImpl repo = getRepo();
        repo.save(at);
        
        RuleSetAttachment at2 = repo.loadAttachment("RS1");
        assertEquals("DRL", at2.getTypeOfAttachment());
        
        assertEquals(1, at2.getAttachments().size());
    }
    
}
