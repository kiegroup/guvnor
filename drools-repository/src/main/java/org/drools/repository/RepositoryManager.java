package org.drools.repository;

import java.util.List;

/**
 * The repository manager takes care of storing and sychronising the repository
 * data with the repository database.
 * 
 * This interface defines all the operations that cane be performed on the repository.
 * A client using this must be able to have a connection to the repository.
 * 
 * @author <a href="mailto:michael.neale@gmail.com"> Michael Neale</a>
 */
public interface RepositoryManager {

    /** 
     * This will simply save the current version of the rule.
     * If there is a previous saved version of the rule, it will be stored as a historical record.
     */
    public abstract void save(RuleDef newRule);

    public abstract RuleDef loadRule(String ruleName,
                                     long versionNumber);

    /** 
     * This will return a list of rules of "major versions" - these are rules that have been 
     * part of a ruleset version.
     */
    public abstract List listRuleVersions(String ruleName);

    public abstract List listRuleSaveHistory(RuleDef rule);

    public abstract List findRulesByTag(String tag);

    /** Save the ruleset. The Ruleset will not be reloaded. */
    public abstract void save(RuleSetDef ruleSet);

    /**
     * This loads a RuleSet with the appropriate workingVersionNumber applied to
     * its assets.
     * 
     * @param workingVersionNumber
     *            The version of the ruleset and rules you want to work on.
     * @param ruleSetName
     *            The ruleset name to retrieve (ruleset names must be unique).
     */
    public abstract RuleSetDef loadRuleSet(String ruleSetName,
                                           long workingVersionNumber);

    public abstract RuleSetAttachment loadAttachment(String name);

    public abstract void save(RuleSetAttachment attachment);

    /** Returns List<String> of Rule set names */
    public abstract List listRuleSets();

    public abstract void delete(RuleDef rule);

    /**
     * Searches the ruleset for a rule with a certain tag. This will search ALL
     * VERSIONS.
     */
    public abstract List searchRulesByTag(String ruleSetName,
                                          String tag);

    
    /** This is only required for stateful Repository session. It will be ignored for stateless ones */
    public abstract void close();
    
}