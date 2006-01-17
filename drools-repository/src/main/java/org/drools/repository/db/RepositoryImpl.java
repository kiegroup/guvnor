package org.drools.repository.db;

import java.util.List;


import org.drools.repository.RepositoryManager;
import org.drools.repository.RuleDef;
import org.drools.repository.RuleSetAttachment;
import org.drools.repository.RuleSetDef;
import org.hibernate.Session;

/**
 * The repository manager takes care of storing and sychronising the repository
 * data with the repository database.
 * 
 * @author <a href ="mailto:sujit.pal@comcast.net"> Sujit Pal </a>
 * @author <a href="mailto:michael.neale@gmail.com"> Michael Neale</a>
 */
public class RepositoryImpl
    implements
    RepositoryManager {

    private Session session;
    
    public void injectSession(Session session) {
        this.session = session;
    }
    
    /* (non-Javadoc)
     * @see org.drools.repository.db.RepositoryManager#save(org.drools.repository.RuleDef)
     */
    public void save(RuleDef newRule) {
        session.saveOrUpdate( newRule );
    }

    /* (non-Javadoc)
     * @see org.drools.repository.db.RepositoryManager#loadRule(java.lang.String, long)
     */
    public RuleDef loadRule(String ruleName,
                            long versionNumber) {
        RuleDef result = (RuleDef) session.createQuery( "from RuleDef where name = :name and versionNumber = :version" )
                            .setString( "name", ruleName )
                            .setLong( "version", versionNumber )
                            .uniqueResult();

        return result;
    }

    /* (non-Javadoc)
     * @see org.drools.repository.db.RepositoryManager#listRuleVersions(java.lang.String)
     */
    public List listRuleVersions(String ruleName) {
        List result = (List) session.createQuery( "from RuleDef where name = :name order by versionNumber" )
                                .setString( "name", ruleName ).list();
        return result;
    }

    /* (non-Javadoc)
     * @see org.drools.repository.db.RepositoryManager#listRuleSaveHistory(org.drools.repository.RuleDef)
     */
    public List listRuleSaveHistory(RuleDef rule) {
        disableHistoryFilter( session );

        List result = (List) session.createQuery( "from RuleDef where historicalId = :id" ).setLong( "id",
                                                                                                     rule.getId().longValue() ).list();

        enableHistoryFilter( session );
        return result;
    }

    /* (non-Javadoc)
     * @see org.drools.repository.db.RepositoryManager#findRulesByTag(java.lang.String)
     */
    public List findRulesByTag(String tag) {
        List result = session.createQuery( "select rule from RuleDef as rule " + 
                                           "join rule.tags as tags " + 
                                           "where tags.tag = :tag" ).setString( "tag", tag ).list();
        return result;
    }

    /* (non-Javadoc)
     * @see org.drools.repository.db.RepositoryManager#save(org.drools.repository.RuleSetDef)
     */
    public void save(RuleSetDef ruleSet) {
        session.saveOrUpdate( ruleSet );
    }

    /* (non-Javadoc)
     * @see org.drools.repository.db.RepositoryManager#loadRuleSet(java.lang.String, long)
     */
    public RuleSetDef loadRuleSet(String ruleSetName,
                                  long workingVersionNumber) {
        session.clear(); //to make sure latest is loaded up, not stale
        enableWorkingVersionFilter( workingVersionNumber,
                             session );        
        RuleSetDef def = loadRuleSetByName( ruleSetName,
                                            session );
        def.setWorkingVersionNumber( workingVersionNumber );

        disableWorkingVersionFilter( session );
        return def;
    }

    private RuleSetDef loadRuleSetByName(String ruleSetName,
                                         Session session) {
        RuleSetDef def = (RuleSetDef) session.createQuery( "from RuleSetDef where name = :name" ).setString( "name",
                                                                                                             ruleSetName ).uniqueResult();
        return def;
    }

    /* (non-Javadoc)
     * @see org.drools.repository.db.RepositoryManager#loadAttachment(java.lang.String)
     */
    public RuleSetAttachment loadAttachment(String name) {
        RuleSetAttachment at = (RuleSetAttachment) session.createQuery( "from RuleSetAttachment where name = :name" )
                                .setString( "name",name ).uniqueResult();
        return at;
    }

    /* (non-Javadoc)
     * @see org.drools.repository.db.RepositoryManager#save(org.drools.repository.RuleSetAttachment)
     */
    public void save(RuleSetAttachment attachment) {
        session.saveOrUpdate( attachment );
    }

    /* (non-Javadoc)
     * @see org.drools.repository.db.RepositoryManager#listRuleSets()
     */
    public List listRuleSets() {
        List list = session.createQuery( "select distinct name from RuleSetDef where name is not null" ).list();
        return list;
    }

    /* (non-Javadoc)
     * @see org.drools.repository.db.RepositoryManager#delete(org.drools.repository.RuleDef)
     */
    public void delete(RuleDef rule) {
        session.delete( rule );
    }

    /* (non-Javadoc)
     * @see org.drools.repository.db.RepositoryManager#searchRulesByTag(java.lang.String, java.lang.String)
     */
    public List searchRulesByTag(String ruleSetName,
                                 String tag) {
        RuleSetDef def = loadRuleSetByName( ruleSetName,
                                            session );
        List list = session.createFilter( def.getRules(),
                                          "where this.tags.tag = :tag" ).setString( "tag",
                                                                                    tag ).list();
      
        return list;
    }


    //////////////////////////
    // Filters follow
    //////////////////////////
    void enableHistoryFilter(Session session) {
        session.enableFilter( "historyFilter" ).setParameter( "viewHistory",
                                                              Boolean.FALSE );
    }

    void disableHistoryFilter(Session session) {
        session.disableFilter( "historyFilter" );
    }

    void enableWorkingVersionFilter(long workingVersionNumber,
                                     Session session) {
        session.enableFilter( "workingVersionFilter" ).setParameter( "filteredVersionNumber",
                                                                     new Long( workingVersionNumber ) );
    }

    void disableWorkingVersionFilter(Session session) {
        session.disableFilter( "workingVersionFilter" );
    }

    
    public void close() { /*implemented by the proxy */}
    
    

}
