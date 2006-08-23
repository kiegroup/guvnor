package org.drools.repository;

import java.util.ArrayList;
import java.util.List;

import javax.jcr.Node;
import javax.jcr.PathNotFoundException;
import javax.jcr.Property;
import javax.jcr.RepositoryException;
import javax.jcr.UnsupportedRepositoryOperationException;
import javax.jcr.Value;
import javax.jcr.ValueFactory;
import javax.jcr.version.Version;
import javax.jcr.version.VersionIterator;

import org.apache.log4j.Logger;

//TODO: consider allowing ruleSets to aggregate other ruleSets in addition to rules
//TODO: exclude duplicate references
//TODO: add remove function   

/**
 * A ruleSet object aggregates a set of rules. This is advantageous for systems using the JBoss Rules
 * engine where the application might make use of many related rules.  
 * <p>
 * A rule set refers to rule nodes within the RulesRepository.  It can either have the reference to a 
 * specific rule follow the head version of that rule, or have this reference continue to refer to 
 * a specific version of that rule even when a new version of the rule is checked into the repository
 * 
 * @author btruitt
 */
public class RulePackageItem extends VersionableItem {       
    private static Logger log = Logger.getLogger(RulePackageItem.class);
    
    /**
     * The name of the reference property on the rulepackage_node_type type node that objects of
     * this type hold a reference to
     */
    public static final String RULE_REFERENCE_PROPERTY_NAME = "drools:rule_reference";
    
    /**
     * The name of the rule package node type
     */
    public static final String RULE_PACKAGE_TYPE_NAME = "drools:rulepackage_node_type";   
    
    /**
     * Constructs an object of type RulePackageItem corresponding the specified node
     * @param rulesRepository the rulesRepository that instantiated this object
     * @param node the node to which this object corresponds
     * @throws RulesRepositoryException 
     */
    public RulePackageItem(RulesRepository rulesRepository, Node node) throws RulesRepositoryException {
        super(rulesRepository, node);
        
        try {
            //make sure this node is a rule package node       
            if(!(this.node.getPrimaryNodeType().getName().equals(RULE_PACKAGE_TYPE_NAME))) {
                String message = this.node.getName() + " is not a node of type " + RULE_PACKAGE_TYPE_NAME + ". It is a node of type: " + this.node.getPrimaryNodeType().getName();
                log.error(message);
                throw new RulesRepositoryException(message);
            }    
        }
        catch(Exception e) {
            log.error("Caught exception: " + e);
            throw new RulesRepositoryException(e);
        }
    }    
    
    /**
     * Adds a rule to the rule package node this object represents.  The reference to the rule 
     * will follow the head version of the specified rule's node.
     * 
     * @param ruleItem the ruleItem corresponding to the node to add to the rule package this
     *                 object represents
     * @throws RulesRepositoryException
     */
    public void addRule(RuleItem ruleItem) throws RulesRepositoryException {
        this.addRule(ruleItem, true);        
    }
    
    /**
     * Adds a rule to the rule package node this object represents.  The reference to the rule
     * will optionally follow the head version of the specified rule's node or the specific 
     * current version.
     * 
     * @param ruleItem the ruleItem corresponding to the node to add to the rule package this 
     *                 object represents
     * @param followRuleHead if true, the reference to the rule node will follow the head version 
     *                       of the node, even if new versions are added. If false, will refer 
     *                       specifically to the current version.
     * @throws RulesRepositoryException
     */
    public void addRule(RuleItem ruleItem, boolean followRuleHead) throws RulesRepositoryException {        
        try {
            ValueFactory factory = this.node.getSession().getValueFactory();
            int i = 0;
            Value[] newValueArray = null;
            
            try {
                Value[] oldValueArray = this.node.getProperty(RULE_REFERENCE_PROPERTY_NAME).getValues();
                newValueArray = new Value[oldValueArray.length + 1];                
                
                for(i=0; i<oldValueArray.length; i++) {
                    newValueArray[i] = oldValueArray[i];
                }
            }
            catch(PathNotFoundException e) {
                //the property has not been created yet. do so now
                newValueArray = new Value[1];
            }
            finally {
                if(newValueArray != null) { //just here to make the compiler happy
                    if(followRuleHead) {                    
                        newValueArray[i] = factory.createValue(ruleItem.getNode());
                    }
                    else {
                        newValueArray[i] = factory.createValue(ruleItem.getNode().getBaseVersion());
                    }
                    this.node.checkout();
                    this.node.setProperty(RULE_REFERENCE_PROPERTY_NAME, newValueArray);                
                    this.node.getSession().save();
                    this.node.checkin();
                }
                else {
                    throw new RulesRepositoryException("Unexpected null pointer for newValueArray");
                }
            }                    
        }
        catch(UnsupportedRepositoryOperationException e) {
            String message = "";
            try {
                message = "Error: Caught UnsupportedRepositoryOperationException when attempting to get base version for rule: " + ruleItem.getNode().getName() + ". Are you sure your JCR repository supports versioning? ";
                log.error(message + e);
            }
            catch (RepositoryException e1) {
                log.error("Caught exception: " + e1);
                throw new RulesRepositoryException(message, e1);
            }
            log.error("Caught exception: " + e);
            throw new RulesRepositoryException(e);
        }
        catch(Exception e) {
            log.error("Caught exception: " + e);
            throw new RulesRepositoryException(e);
        }
    }
    
    /**
     * Removes the specified rule from the rule package node this object represents.  
     * 
     * @param ruleItem the ruleItem corresponding to the node to remove from the rule package 
     *                 this object represents
     * @throws RulesRepositoryException
     */
    public void removeRule(RuleItem ruleItem) throws RulesRepositoryException {                
        try {
            Value[] oldValueArray = this.node.getProperty(RULE_REFERENCE_PROPERTY_NAME).getValues();
            Value[] newValueArray = new Value[oldValueArray.length - 1];
            
            boolean wasThere = false;
            
            int j=0;
            for(int i=0; i<oldValueArray.length; i++) {
                Node ruleNode = this.node.getSession().getNodeByUUID(oldValueArray[i].getString());
                RuleItem currentRuleItem = new RuleItem(this.rulesRepository, ruleNode);
                if(currentRuleItem.equals(ruleItem)) {
                    wasThere = true;
                }
                else {
                    newValueArray[j] = oldValueArray[i];
                    j++;
                }
            }
                            
            if(!wasThere) {
                return;
            }
            else {
                this.node.checkout();
                this.node.setProperty(RULE_REFERENCE_PROPERTY_NAME, newValueArray);                
                this.node.getSession().save();
                this.node.checkin();
            }
        }
        catch(PathNotFoundException e) {
            //the property has not been created yet. 
            return;
        }  
        catch(Exception e) {
            log.error("Caught exception", e);
            throw new RulesRepositoryException(e);
        }
    }
    
    /**
     * Gets a list of RuleItem objects for each rule node in this rule package
     * 
     * @return the List object holding the RuleItem objects in this rule package
     * @throws RulesRepositoryException 
     */
    public List getRules() throws RulesRepositoryException {
        try {                       
            Value[] valueArray = this.node.getProperty(RULE_REFERENCE_PROPERTY_NAME).getValues();
            List returnList = new ArrayList();
           
            for(int i=0; i<valueArray.length; i++) {
                Node ruleNode = this.node.getSession().getNodeByUUID(valueArray[i].getString());
                returnList.add(new RuleItem(this.rulesRepository, ruleNode));
            }
            return returnList;
        }
        catch(PathNotFoundException e) {
            //the property has not been created yet. 
            return new ArrayList();
        }                                       
        catch(Exception e) {
            log.error("Caught exception: " + e);
            throw new RulesRepositoryException(e);
        }
    }    
    
    /**
     * Removes all rules from the rule package
     * 
     * @throws RulesRepositoryException
     */
    public void removeAllRules() throws RulesRepositoryException {
        try {
            Property rulesProperty = this.node.getProperty(RULE_REFERENCE_PROPERTY_NAME);
            this.node.checkout();
            rulesProperty.remove();
            this.node.save();
            this.node.checkin();
        }
        catch(PathNotFoundException e) {
            //the property has not been created yet. 
            return;
        }                                       
        catch(Exception e) {
            log.error("Caught exception: " + e);
            throw new RulesRepositoryException(e);
        }
    }
    
    /**
     * Nicely formats the information contained by the node that this object encapsulates    
     */
    public String toString() {
        try {            
            StringBuffer returnString = new StringBuffer();
            returnString.append("These are the rules in the rule package named " + this.node.getName() + ":");
            
            //iterate over the rules in this rule package and dump them
            Property rulesProp = this.node.getProperty(RULE_REFERENCE_PROPERTY_NAME);
            Value[] values = rulesProp.getValues();
            for(int i=0; i<values.length; i++) {
                Node ruleNode = this.node.getSession().getNodeByUUID(values[i].getString());
                RuleItem ruleItem = new RuleItem(this.rulesRepository, ruleNode);
                returnString.append(ruleItem.toString());
            }
            return returnString.toString();
        }
        catch(Exception e) {
            log.error("Caught Exception", e);
            return null;
        }
    }

    /**
     * @return a List of RulePackageItem objects encapsulating each Version node in the 
     *         VersionHistory of this Item's node
     * @throws RulesRepositoryException
     */
    public List getHistory() throws RulesRepositoryException {
        List returnList = new ArrayList();
        try {
            VersionIterator it = this.node.getVersionHistory().getAllVersions();
            while(it.hasNext()) {
                Version currentVersion = it.nextVersion();
                RulePackageItem item = new RulePackageItem(this.rulesRepository, currentVersion);
                returnList.add(item);
            }
        }
        catch(Exception e) {
            log.error("Caught exception: " + e);
            throw new RulesRepositoryException(e);
        }
        return returnList;
    }
    
    public VersionableItem getPrecedingVersion() throws RulesRepositoryException {
        try {
            Node precedingVersionNode = this.getPrecedingVersionNode();
            if(precedingVersionNode != null) {
                return new RulePackageItem(this.rulesRepository, precedingVersionNode);
            }
            else {
                return null;
            }
        }        
        catch(Exception e) {
            log.error("Caught exception", e);
            throw new RulesRepositoryException(e);
        }               
    }

    public VersionableItem getSucceedingVersion() throws RulesRepositoryException {
        try {
            Node succeedingVersionNode = this.getSucceedingVersionNode();
            if(succeedingVersionNode != null) {
                return new RulePackageItem(this.rulesRepository, succeedingVersionNode);
            }
            else {
                return null;
            }
        }        
        catch(Exception e) {
            log.error("Caught exception", e);
            throw new RulesRepositoryException(e);
        }
    }          
}