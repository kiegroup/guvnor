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
        Session session = getSessionNewTx();

        session.saveOrUpdate(newRule);

        commit( session );
    }
    

    

    public RuleDef loadRule(String ruleName, long versionNumber) {
        Session session = getSessionNewTx();
        
        RuleDef result = (RuleDef) session.createQuery("from RuleDef where name = :name and versionNumber = :version")
              .setString("name", ruleName)
              .setLong("version", versionNumber).uniqueResult();
        
        commit( session );
        return result;
    }
    
    public List listRuleVersions(String ruleName) {
        Session session = getSessionNewTx();
        
        List result = (List) session.createQuery("from RuleDef where name = :name order by versionNumber")
              .setString("name", ruleName).list();
        
        commit( session );
        return result;        
    }
    
    public List listRuleSaveHistory(RuleDef rule) {
        Session session = getSessionNewTx();
        disableHistoryFilter(session);
        
        List result = (List) session.createQuery("from RuleDef where historicalId = :id")
                        .setLong("id", rule.getId().longValue()).list();
        
        enableHistoryFilter(session);
        commit( session );
        return result;
    }
    
    public List findRulesByTag(String tag) {
        Session session = getSessionNewTx();
        List result = session.createQuery("from RuleDef as rule " +
                                        "join rule.tags as tags " +
                                        "where tags.tag = :tag")
            .setString("tag", tag)
            .list();              
        commit( session );
        return result;
    }
    

    
    /** Save the ruleset. The Ruleset will not be reloaded. */
    public void save(RuleSetDef ruleSet) {
        Session session = getSessionNewTx();  
        session.saveOrUpdate(ruleSet);
        commit( session );
    }
    
    /** 
     * This loads a RuleSet with the appropriate workingVersionNumber applied to its assets. 
     * @param workingVersionNumber The version of the ruleset and rules you want to work on.
     * @param ruleSetName The ruleset name to retrieve (ruleset names must be unique).
     */
    public RuleSetDef loadRuleSet(String ruleSetName, long workingVersionNumber) {
        Session session = getSessionNewTx();
        
        enableVersionFilter( workingVersionNumber,
                             session );
        
        RuleSetDef def = loadRuleSetByName( ruleSetName,
                                            session );        
        def.setWorkingVersionNumber(workingVersionNumber);  
        
        disableVersionFilter( session );
        commit( session );
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
        Session session = getSessionNewTx();
        RuleSetAttachment at = (RuleSetAttachment) 
                                session.createQuery("from RuleSetAttachment where name = :name")
                                .setString("name", name)
                                .uniqueResult();
        commit( session );
        return at;       
    }    



    
    public void save(RuleSetAttachment attachment) {
        Session session = getSessionNewTx();
        session.saveOrUpdate(attachment);
        commit( session );
    }
    
    
    /** Returns List<String> of Rule set names */
    public List listRuleSets() {
        Session session = getSessionNewTx();
        List list = session.createQuery("select name from RuleSetDef where name is not null").list();
        commit( session );
        return list;
    }
    
    public void delete(RuleDef rule) {
        Session session = getSessionNewTx();
        session.delete(rule);
        commit( session );
    }

    
    /** 
     * Searches the ruleset for a rule with a certain tag.
     * This will search ALL VERSIONS. 
     */
    public List searchRulesByTag(String ruleSetName, String tag) {
        Session session = getSessionNewTx();
               
        RuleSetDef def = loadRuleSetByName(ruleSetName, session);
        List list = session.createFilter(def.getRules(), 
                             "where this.tags.tag = :tag")
                             .setString("tag", tag).list();
        commit( session );
        session.close();
        return list;
    }




    private void commit(Session session) {
        session.getTransaction().commit();
    }
    
    
    
    private Session getSessionNewTx(){
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        session.beginTransaction(); 
        enableHistoryFilter( session );
        return session;
    }

    private void enableHistoryFilter(Session session) {
        session.enableFilter("historyFilter").setParameter("viewHistory", Boolean.FALSE);
    }
    
    private void disableHistoryFilter(Session session) {
        session.disableFilter("historyFilter");
    }
    
    private void enableVersionFilter(long workingVersionNumber,
                                     Session session){
        session.enableFilter("workingVersionFilter")
                .setParameter("filteredVersionNumber", 
                new Long(workingVersionNumber));
    }

    private void disableVersionFilter(Session session){
        session.disableFilter("workingVersionFilter");
    }    
    
}
