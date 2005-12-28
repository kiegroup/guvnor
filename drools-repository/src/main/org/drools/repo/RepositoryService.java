package org.drools.repo;

import java.util.List;
import java.util.Properties;

import org.drools.metamodel.RuleDefinition;



public interface RepositoryService
{
    /**
     * Nodes are represented with a "drools:..." prefix to prevent namespace collisions for people 
     * using a shared repository (shared with a CMS for instance).
     */
    public static final String RULE_NAMESPACE_PREFIX = "drools";
    public static final String RULE_NAMESPACE = "http://www.drools.org/repo";
    
    
    /**
     * This will mark the asset as checked out by the current user.
     * 
     * The idea is to prevent anyone else working on it at the same time. 
     * Assets needs to be checked out, so when they are checked in their versions are updated.
     * This may also lock the asset, not sure...
     * Can provide seperate explicit lock methods if needed.
     */
//    public RuleSetFile checkoutFile(String ruleBaseName, String ruleSetName);
//    public RuleSetConfig checkoutRuleSetConfig(String ruleBaseName, String ruleSetName);
//    public Rule checkoutRule(String ruleBaseName, String ruleSetName, String ruleName);
    
    
    /**
     * These methods add brand new assets to the repository.
     * Exceptions thrown if you don't fill out the right stuff etc.
     * The names must be unique of course, or exceptions will be thrown.
     */
//    public RuleSetFile addNewFile(String ruleBaseName, RuleSetFile file);
//    public RuleSet addNewRuleSet(String ruleBaseName, RuleSet ruleSet);
//    public Rule addNewRule(String ruleBaseName, String ruleSetName, Rule rule);
    
    
//    /**
//     * This method updates the asset in the repository, and
//     * increment the version number etc.
//     * If someone else beat you to it, it will chuck a wobbly. Whereby you 
//     * can tell the poor user, they are going to have to find out who it was,
//     * and throw a phone at their heads, russle crowe style.
//     * Of course, with checkouts, it should stop anyone else checking it out. 
//     */
//    public VersionInfo checkin(Asset asset, String comment);
//    
//    /**
//     * This will change the status flag on the latest version of the asset.
//     * It will make sure that your version is up to date before allowing it. 
//     */
//    public VersionInfo changeStatus(Asset asset, String newStatus);
//    
//    
//    /**
//     * Returns a list of rulebase names in the repository.
//     */
//    public List listRuleBaseName();
//    
//    
//    /**
//     * This returns a list of rule-set names for a given rulebase.
//     */
//    public List listRuleSetNames(String ruleBase);
//
//
//    /**
//     * This will deeply load the whole RuleSet.
//     */
//    public RuleSet retrieveRuleSet(String ruleBaseName, String ruleSetName);
    
//
//    /**     
//     * This will load a RuleSetFile.
//     */
//    public RuleSetFile retrieveRuleSetFile(String ruleBaseName, String ruleSetName);
    
    
//    /**
//     * Haven't really defined how to search for things,
//     * but will be on most fields.
//     * Can return a list of assets that match, which could be rules, rulesets etc...
//     * Probably need some canned queries, as well as some XPath ones.
//     * Can do things like search for rules modified on a certain date, in various states etc.
//     * TODO: get a list of basic functionality to support.
//     */
//    public List searchRules(Properties searchProperties);
    
    

    /**
     * Sets up a virgin repository. Only needs to be called once.
     */
    public void createNewRepo();    
    
    /**
     * This will add a new rule to the repository.
     * If it already exists, and exception will be thrown.
     * You need to explicitly checkin a new version of a rule. This 
     * method is only for adding a brand new rule.
     * 
     * The RuleBase and RuleSet will be implicitly created if they don't already exist.
     */
    public RuleDefinition addNewRule(String ruleBase, String ruleSet, RuleDefinition rule);
    
    /**
     * This will permanently delete a rule.
     * Only use when you really don't want it anymore.
     */
    public void removeRule(String ruleBase, String ruleSet, String ruleName);
    
    /** Retrieve a rule */
    public RuleDefinition retrieveRule(String ruleBase, String ruleSet, String ruleName);
    
    
    /**
     * This will remove a ruleset, and all the contained rules.
     */
    public void removeRuleSet(String ruleBase, String ruleSet);
    
    /**
     * Applies changes that were made.
     * Some actions have implicit saves, but it is best to 
     */
    public void save();

    
}
