package org.drools.repository;

import java.util.List;

import org.drools.repository.db.Asset;

/**
 * The repository manager takes care of storing and sychronising the repository
 * data with the repository database.
 * 
 * This interface defines all the operations that cane be performed on the repository.
 * A client using this must be able to have a connection to the repository.
 * 
 * If RepositoryException is thrown, this usually means a validation error, a repository
 * rule violation etc. For other exceptions, the repository manager instance may become invalid.
 * If it is a stateful Repository, then it will need to be created from the factory fresh.
 * 
 * @author <a href="mailto:michael.neale@gmail.com"> Michael Neale</a>
 */
public interface RepositoryManager {

    /**
     * Load a rule based on a workingVersionNumber.
     */
    public abstract RuleDef loadRule(String ruleName,
                                     long workingVersionNumber);

    /** 
     * This will return a list of rules of "major versions" - these are rules that have been 
     * part of a ruleset version.
     */
    public abstract List listRuleVersions(String ruleName);

    /** 
     * Find and return all the historical versions of a rule.
     * Historcal versions are previous versions of the rules stored when
     * a change to the rule was saved.
     * 
     * This is distinct from Versions which are related to RuleSet versioning.
=     */
    public abstract List listRuleSaveHistory(RuleDef rule);

    public abstract List findRulesByTag(String tag);


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

    /**
     * Load an attachment with the appropriate version number.
     */
    public abstract RuleSetAttachment loadAttachment(String name, long workingVersionNumber);

    /** 
     * Saves an asset. Most assets are saved automatically when you save the parent, so
     * for instance when you save a ruleset, you should not need to save any of its assets as well.
     * 
     * Some assets, such as rules and ruleset attachments have history items created on each save.
     */
    public abstract void save(Asset asset);

    /** Returns List<String> of Rule set names */
    public abstract List listRuleSets();

    public abstract void delete(Asset rule);

    /**
     * Searches the ruleset for a rule with a certain tag. This will search ALL
     * VERSIONS.
     */
    public abstract List searchRulesByTag(String ruleSetName,
                                          String tag);

    
    /** 
     * This will check out a rule for the given user id.
     * This can be used to enforce "locking" of rule edits.
     * 
     * This will save the rule as it stands, including any changes.
     */
    public abstract void checkOutRule(RuleDef rule, String userId);

    /**
     * This removes the check out flag.
     * 
     * The userId must be supplied to confirm that the correct user 
     * is checking it in, an exception will be thrown if this is not correct.
     * 
     * This can effectively be "overridden" by either just saving the rule, or passing
     * in the correct username. It is up to client applications to enforce this behaviour.
     */
    public abstract void checkInRule(RuleDef rule, String userId);
    
    
    /** 
     * This will check out an for the given user id.
     * This can be used to enforce "locking" of rule edits.
     * 
     * This will save the attachment as it stands, including any changes.
     */
    public abstract void checkOutAttachment(RuleSetAttachment attachment, String userId);

    /**
     * This removes the check out flag.
     * 
     * The userId must be supplied to confirm that the correct user 
     * is checking it in, an exception will be thrown if this is not correct.
     * 
     * This can effectively be "overridden" by either just saving the rule, or passing
     * in the correct username. It is up to client applications to enforce this behaviour.
     */
    public abstract void checkInAttachment(RuleSetAttachment attachment, String userId);
     
    
    
    /** This is only required for stateful Repository session. It will be ignored for stateless ones. */
    public abstract void close();
    
}