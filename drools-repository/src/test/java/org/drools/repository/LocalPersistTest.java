package org.drools.repository;

import org.drools.repository.db.PersistentCase;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;

public class LocalPersistTest extends PersistentCase {

    public void testXStream() {
        RuleSetDef def = new RuleSetDef("xstream1", new MetaData());
        def.addRule(new RuleDef("rulex1", "ndklsanlkdsan"));
        XStream xstream = new XStream(new DomDriver());
        String xml = xstream.toXML(def);
        //System.out.println(xml);
        
        def = (RuleSetDef) xstream.fromXML(xml);

        RepositoryManager repo = getRepo();
        repo.save(def);
        
        def = repo.loadRuleSet("xstream1", 1);
        xml = xstream.toXML(def);
        //System.out.println(xml);
        
        def = (RuleSetDef) xstream.fromXML(xml);
        def.addRule( new RuleDef("xstream2", "xxxx"));
        repo.save(def);
        
        assertNotNull(def);
        
    }
    
}
