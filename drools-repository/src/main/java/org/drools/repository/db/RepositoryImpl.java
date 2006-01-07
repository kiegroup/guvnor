package org.drools.repository.db;

import java.util.List;

import org.drools.repository.Repository;
import org.drools.repository.RuleDef;
import org.drools.repository.RuleSetAttachment;
import org.drools.repository.RuleSetDef;
import org.hibernate.Session;



public class RepositoryImpl implements Repository {

    /** This will simply save the current version of the rule */
    public RuleDef save(RuleDef newRule) {
        Session session = getSession();
        session.beginTransaction();

        session.saveOrUpdate(newRule);

        session.getTransaction().commit();
        
        return newRule;
    }
    

    
    //DODGY METHODS START
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
    //DODGY METHODS END
    
    public List findRulesByTag(String tag) {
        Session session = getSession();
        session.beginTransaction();
        List result = session.createQuery("from RuleDef as rule where rule.tags.tag = :tag")
            .setString("tag", tag)
            .list();              
        session.getTransaction().commit();
        return result;
    }
    

    
    public RuleSetDef save(RuleSetDef ruleSet) {
        Session session = getSession();
        session.beginTransaction();     
        session.saveOrUpdate(ruleSet);
        session.getTransaction().commit();
        return ruleSet;
    }
    
    /** This loads a RuleSet with the appropriate workingVersionNumber applied to its assets. 
     */
    public RuleSetDef loadRuleSet(String ruleSetName, long workingVersionNumber) {
        Session session = getSession();
        
        session.beginTransaction();
        enableVersionFilter( workingVersionNumber,
                             session );
        
        RuleSetDef def = (RuleSetDef)
                session.createQuery("from RuleSetDef where name = :name")
                .setString("name", ruleSetName ).uniqueResult();
        
        def.setWorkingVersionNumber(workingVersionNumber);        
        removeVersionFilter( session );
        session.getTransaction().commit();
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
    

    
    
    
    private Session getSession(){
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        return session;
    }
    
    
}
