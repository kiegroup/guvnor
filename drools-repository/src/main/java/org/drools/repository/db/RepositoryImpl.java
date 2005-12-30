package org.drools.repository.db;

import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.drools.repository.Repository;
import org.drools.repository.RuleDef;
import org.drools.repository.RuleSetDef;
import org.drools.repository.Tag;
import org.hibernate.Query;
import org.hibernate.Session;



public class RepositoryImpl implements Repository {

    public RuleDef addNewRule(RuleDef newRule) {
        Session session = getSession();
        session.beginTransaction();

        Set tags = newRule.getTags();
        saveTags( session,
                  tags );
        
        session.save(newRule);

        session.getTransaction().commit();
        return newRule;
    }


    private void saveTags(Session session,
                          Set tags){
        for ( Iterator iter = tags.iterator(); iter.hasNext(); ) {
            Tag tag = (Tag) iter.next();
            session.saveOrUpdate(tag);
        }
    }

    
    public List listAllRules(boolean head) {
        Session session = getSession();
        session.beginTransaction();
        List results = session
                    .createQuery("from RuleDef where head = :head")
                    .setBoolean("head", head).setMaxResults(1000)
                    .list();        
        session.getTransaction().commit();
        return results;        
    }
    
    public RuleDef retrieveRule(String ruleName, long versionNumber) {
        Session session = getSession();
        session.beginTransaction();
        
        RuleDef result = (RuleDef) session.createQuery("from RuleDef where name = :name and versionNumber = :version")
              .setString("name", ruleName)
              .setLong("version", versionNumber).uniqueResult();
        
        session.getTransaction().commit();
        return result;
    }
    
    public List findRulesByTag(String tag) {
        Session session = getSession();
        session.beginTransaction();
        List result = session.createQuery("from RuleDef as rule where rule.tags.tag = :tag")
            .setString("tag", tag)
            .list();              
        session.getTransaction().commit();
        return result;
    }
    

    
    public RuleSetDef saveRuleSet(RuleSetDef ruleSet) {
        Session session = getSession();
        session.beginTransaction();     
        saveTags(session, ruleSet.getTags());
        session.saveOrUpdate(ruleSet);
        session.getTransaction().commit();
        return ruleSet;
    }
    
    public RuleSetDef retrieveRuleSet(String ruleSetName, long versionNumber) {
        Session session = getSession();
        session.beginTransaction();        
        RuleSetDef def = (RuleSetDef)
                session.createQuery("from RuleSetDef where name = :name and versionNumber = :versionNumber")
                .setString("name", ruleSetName )
                .setLong("versionNumber", versionNumber).uniqueResult();
        session.getTransaction().commit();
        return def;
    }
    
    private Session getSession(){
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        return session;
    }
    
    
}
