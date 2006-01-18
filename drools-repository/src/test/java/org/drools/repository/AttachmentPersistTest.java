package org.drools.repository;

import org.drools.repository.db.PersistentCase;

public class AttachmentPersistTest extends PersistentCase {

    public void testLoadSave() {
        RuleSetAttachment at = new RuleSetAttachment("test","test", "test".getBytes(), "blah.xml" );
        RepositoryManager repo = getRepo();
        at.addTag("RULESETAT");
        repo.save(at);
        RuleSetAttachment at2 = repo.loadAttachment("test", 1);
        assertEquals("test", at2.getTypeOfAttachment());
        assertEquals("RULESETAT", ((Tag)at2.getTags().iterator().next()).getTag());
    }
    
}
