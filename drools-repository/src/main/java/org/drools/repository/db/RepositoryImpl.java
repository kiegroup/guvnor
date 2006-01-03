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

    /** This will simply save the current version of the rule */
    public RuleDef saveOrUpdateRule(RuleDef newRule) {
        Session session = getSession();
        session.beginTransaction();

        session.saveOrUpdate(newRule);

        session.getTransaction().commit();
        //session.close();
        return newRule;
    }
    
    /** This will simply save the current version of the rule */
    public RuleDef merge(RuleDef newRule) {
        Session session = getSession();
        session.beginTransaction();

        session.merge(newRule);

        session.getTransaction().commit();
        return newRule;
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
    
    public RuleDef loadRule(String ruleName, long versionNumber) {
        Session session = getSession();
        session.beginTransaction();
        
        RuleDef result = (RuleDef) session.createQuery("from RuleDef where name = :name and versionNumber = :version")
              .setString("name", ruleName)
              .setLong("version", versionNumber).uniqueResult();
        
        session.getTransaction().commit();
        return result;
    }
    
    public List listRuleHistory(String ruleName) {
        Session session = getSession();
        session.beginTransaction();
        
        List result = (List) session.createQuery("from RuleDef where name = :name order by versionNumber")
              .setString("name", ruleName).list();
        
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
    

    
    public RuleSetDef saveOrUpdateRuleSet(RuleSetDef ruleSet) {
        Session session = getSession();
        session.beginTransaction();     
        session.saveOrUpdate(ruleSet);
        session.getTransaction().commit();
        return ruleSet;
    }
    
    public RuleSetDef loadRuleSet(String ruleSetName) {
        Session session = getSession();
        session.beginTransaction();        
        RuleSetDef def = (RuleSetDef)
                session.createQuery("from RuleSetDef where name = :name")
                .setString("name", ruleSetName ).uniqueResult();
        session.getTransaction().commit();
        return def;
    }
    
    private Session getSession(){
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        return session;
    }
    
    
}
