package org.drools.repository;

import org.drools.repository.db.PersistentCase;

public class AttachmentPersistTest extends PersistentCase {

    public void testLoadSave() {
        RuleSetAttachment at = new RuleSetAttachment("test","test", "test".getBytes(), "blah.xml" );
        RepositoryManager repo = getRepo();
        repo.save(at);
        RuleSetAttachment at2 = repo.loadAttachment("test");
        assertEquals("test", at2.getTypeOfAttachment());
    }
    
}
