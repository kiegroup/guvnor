package org.drools.repository.db;

import java.util.List;

import org.drools.repository.Repository;
import org.drools.repository.RuleDef;
import org.drools.repository.RuleSetAttachment;
import org.drools.repository.RuleSetDef;
import org.hibernate.Session;


/**
 * The repository manager takes care of storing and sychronising the repository data with 
 * the repository database. 
 * @author <a href="mailto:michael.neale@gmail.com"> Michael Neale</a>
 */
public class RepositoryImpl implements Repository {

    /** This will simply save the current version of the rule */
    public void save(RuleDef newRule) {
        Session session = getSession();
        session.beginTransaction();

        session.saveOrUpdate(newRule);

        session.getTransaction().commit();
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
    

    
    /** Save the ruleset. The Ruleset will not be reloaded. */
    public void save(RuleSetDef ruleSet) {
        Session session = getSession();
        session.beginTransaction();     
        session.saveOrUpdate(ruleSet);
        session.getTransaction().commit();
    }
    
    /** 
     * This loads a RuleSet with the appropriate workingVersionNumber applied to its assets. 
     * @param workingVersionNumber The version of the ruleset and rules you want to work on.
     * @param ruleSetName The ruleset name to retrieve (ruleset names must be unique).
     */
    public RuleSetDef loadRuleSet(String ruleSetName, long workingVersionNumber) {
        Session session = getSession();
        
        session.beginTransaction();
        enableVersionFilter( workingVersionNumber,
                             session );
        
        RuleSetDef def = loadRuleSetByName( ruleSetName,
                                            session );        
        def.setWorkingVersionNumber(workingVersionNumber);  
        
        removeVersionFilter( session );
        session.getTransaction().commit();
        return def;
    }




    private RuleSetDef loadRuleSetByName(String ruleSetName,
                                         Session session) {
        RuleSetDef def = (RuleSetDef)
                session.createQuery("from RuleSetDef where name = :name")
                .setString("name", ruleSetName ).uniqueResult();
        return def;
    }

    
    public RuleSetAttachment loadAttachment(String name) {
        Session session = getSession();
        session.beginTransaction();
        RuleSetAttachment at = (RuleSetAttachment) 
                                session.createQuery("from RuleSetAttachment where name = :name")
                                .setString("name", name)
                                .uniqueResult();
        session.getTransaction().commit();
        return at;       
    }    


    private void enableVersionFilter(long workingVersionNumber,
                                     Session session){
        session.enableFilter("workingVersionFilter")
                .setParameter("filteredVersionNumber", 
                new Long(workingVersionNumber));
    }



    private void removeVersionFilter(Session session){
        session.disableFilter("workingVersionFilter");
    }
    
    public void save(RuleSetAttachment attachment) {
        Session session = getSession();
        session.beginTransaction();
        session.saveOrUpdate(attachment);
        session.getTransaction().commit();
    }
    
    
    /** Returns List<String> of Rule set names */
    public List listRuleSets() {
        Session session = getSession();
        session.beginTransaction();
        List list = session.createQuery("select name from RuleSetDef where name is not null").list();
        session.getTransaction().commit();
        return list;
    }
    
    public void delete(RuleDef rule) {
        Session session = getSession();
        session.beginTransaction();
        session.delete(rule);
        session.getTransaction().commit();
    }

    
    /** 
     * Searches the ruleset for a rule with a certain tag.
     * This will search ALL VERSIONS. 
     */
    public List searchRulesByTag(String ruleSetName, String tag) {
        Session session = getSession();
        session.beginTransaction();        
        RuleSetDef def = loadRuleSetByName(ruleSetName, session);
        List list = session.createFilter(def.getRules(), 
                             "where this.tags.tag = :tag")
                             .setString("tag", tag).list();
        session.getTransaction().commit();
        session.close();
        return list;
    }
    
    
    
    private Session getSession(){
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        return session;
    }
    
    
}
