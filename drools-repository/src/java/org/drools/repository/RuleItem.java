package org.drools.repository;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;

import javax.jcr.Node;
import javax.jcr.PathNotFoundException;
import javax.jcr.Property;
import javax.jcr.RepositoryException;
import javax.jcr.UnsupportedRepositoryOperationException;
import javax.jcr.Value;

import org.apache.log4j.Logger;

/**
 * The RuleItem class is used to abstract away the details of the underlying JCR repository.
 * It is used to pass information about rules stored in the repository.
 * 
 * @author btruitt
 */
public class RuleItem extends VersionableItem {
    private Logger log = Logger.getLogger(RuleItem.class);
    
    /**
     * The name of the DSL property on the rule node type
     */
    public static final String DSL_PROPERTY_NAME = "drools:dsl_reference";
    
    /**
     * The name of the tag property on the rule node type
     */
    public static final String TAG_PROPERTY_NAME = "drools:tag_reference";
    
    /**
     * The name of the rule node type
     */
    public static final String RULE_NODE_TYPE_NAME = "drools:rule_node_type";
    
    /**
     * The name of the state property on the rule node type
     */
    public static final String STATE_PROPERTY_NAME = "drools:state_reference";                 
    
    /**
     * The name of the lhs property on the rule node type
     */
    public static final String LHS_PROPERTY_NAME = "drools:lhs";
    
    /**
     * The name of the rhs property on the rule node type
     */
    public static final String RHS_PROPERTY_NAME = "drools:rhs";
    
    /**
     * The name of the date effective property on the rule node type
     */
    public static final String DATE_EFFECTIVE_PROPERTY_NAME = "drools:date_effective";
    
    /**
     * The name of the date expired property on the rule node type
     */
    public static final String DATE_EXPIRED_PROPERTY_NAME = "drools:date_expired";                        
    
    /**
     * The name of the rule language property on the rule node type
     */
    public static final String RULE_LANGUAGE_PROPERTY_NAME = "drools:rule_language";    
    
    /**
     * Constructs a RuleItem object, setting its node attribute to the specified node.
     * 
     * @param rulesRepository the rulesRepository that instantiated this object
     * @param node the node in the repository that this RuleItem corresponds to
     * @throws RulesRepositoryException 
     */
    public RuleItem(RulesRepository rulesRepository, Node node) throws RulesRepositoryException {
        super(rulesRepository, node);
        
        try {
            //make sure this node is a rule node       
            if(!(this.node.getPrimaryNodeType().getName().equals(RULE_NODE_TYPE_NAME) ||
                 this.node.getPrimaryNodeType().getName().equals("nt:version"))) {
                String message = this.node.getName() + " is not a node of type " + RULE_NODE_TYPE_NAME + " nor nt:version. It is a node of type: " + this.node.getPrimaryNodeType().getName();
                log.error(message);
                throw new RulesRepositoryException(message);
            }    
        }
        catch(Exception e) {
            log.error("Caught exception", e);
            throw new RulesRepositoryException(e);
        }
    }
    
    /**
     * returns the lhs of this object's rule node
     * 
     * @return the lhs of this object's rule node
     * @throws RulesRepositoryException
     */
    public String getLhs() throws RulesRepositoryException {
        try {                        
            Node ruleNode;
            if(this.node.getPrimaryNodeType().getName().equals("nt:version")) {
                ruleNode = this.node.getNode("jcr:frozenNode");
            }
            else {
                ruleNode = this.node;
            }
            
            //grab the lhs of the node and dump it into a string            
            Property data = ruleNode.getProperty(LHS_PROPERTY_NAME);
            return data.getValue().getString();
        }
        catch(Exception e) {
            log.error("Caught Exception", e);
            throw new RulesRepositoryException(e);
        }
    }
    
    /**
     * returns the rhs of this object's rule node
     * 
     * @return the rhs of this object's rule node
     * @throws RulesRepositoryException
     */
    public String getRhs() throws RulesRepositoryException {
        try {                        
            Node ruleNode;
            if(this.node.getPrimaryNodeType().getName().equals("nt:version")) {
                ruleNode = this.node.getNode("jcr:frozenNode");
            }
            else {
                ruleNode = this.node;
            }
            
            //grab the lhs of the node and dump it into a string            
            Property data = ruleNode.getProperty(RHS_PROPERTY_NAME);
            return data.getValue().getString();
        }
        catch(Exception e) {
            log.error("Caught Exception", e);
            throw new RulesRepositoryException(e);
        }
    }
        
    /**
     * @return the date the rule becomes effective
     * @throws RulesRepositoryException
     */
    public Calendar getDateEffective() throws RulesRepositoryException {
        try {                        
            Node ruleNode;
            if(this.node.getPrimaryNodeType().getName().equals("nt:version")) {
                ruleNode = this.node.getNode("jcr:frozenNode");
            }
            else {
                ruleNode = this.node;
            }
                        
            Property dateEffectiveProperty = ruleNode.getProperty(DATE_EFFECTIVE_PROPERTY_NAME);
            return dateEffectiveProperty.getDate();
        }
        catch(PathNotFoundException e) {
            // doesn't have this property
            return null;
        }
        catch(Exception e) {
            log.error("Caught Exception", e);
            throw new RulesRepositoryException(e);
        }
    }
    
    /**
     * Creates a new version of this object's rule node, updating the effective date for the
     * rule node. 
     *  
     * @param newDateEffective the new effective date for the rule 
     * @throws RulesRepositoryException
     */
    public void updateDateEffective(Calendar newDateEffective) throws RulesRepositoryException {
        try {
            this.node.checkout();
        }
        catch(UnsupportedRepositoryOperationException e) {
            String message = "";
            try {
                message = "Error: Caught UnsupportedRepositoryOperationException when attempting to checkout rule: " + this.node.getName() + ". Are you sure your JCR repository supports versioning? ";
                log.error(message, e);
            }
            catch (RepositoryException e1) {
                log.error("Caught Exception", e);
                throw new RulesRepositoryException(e1);
            }
            throw new RulesRepositoryException(message, e);
        }
        catch(Exception e) {
            log.error("Caught Exception", e);
            throw new RulesRepositoryException(e);
        }
        
        try {                                    
            this.node.setProperty(DATE_EFFECTIVE_PROPERTY_NAME, newDateEffective);
            
            Calendar lastModified = Calendar.getInstance();
            this.node.setProperty(LAST_MODIFIED_PROPERTY_NAME, lastModified);
            
            this.node.getSession().save();
            
            this.node.checkin();
        }
        catch(Exception e) {
            log.error("Caught Exception", e);
            throw new RulesRepositoryException(e);
        }
    }
    
    /**
     * @return the date the rule becomes expired
     * @throws RulesRepositoryException
     */
    public Calendar getDateExpired() throws RulesRepositoryException {
        try {                        
            Node ruleNode;
            if(this.node.getPrimaryNodeType().getName().equals("nt:version")) {
                ruleNode = this.node.getNode("jcr:frozenNode");
            }
            else {
                ruleNode = this.node;
            }
                        
            Property dateExpiredProperty = ruleNode.getProperty(DATE_EXPIRED_PROPERTY_NAME);
            return dateExpiredProperty.getDate();
        }
        catch(PathNotFoundException e) {
            // doesn't have this property
            return null;
        }
        catch(Exception e) {
            log.error("Caught Exception", e);
            throw new RulesRepositoryException(e);
        }
    }
    
    /**
     * Creates a new version of this object's rule node, updating the expired date for the
     * rule node. 
     *  
     * @param newDateExpired the new expired date for the rule 
     * @throws RulesRepositoryException
     */
    public void updateDateExpired(Calendar newDateExpired) throws RulesRepositoryException {
        try {
            this.node.checkout();
        }
        catch(UnsupportedRepositoryOperationException e) {
            String message = "";
            try {
                message = "Error: Caught UnsupportedRepositoryOperationException when attempting to checkout rule: " + this.node.getName() + ". Are you sure your JCR repository supports versioning? ";
                log.error(message, e);
            }
            catch (RepositoryException e1) {
                log.error("Caught Exception", e);
                throw new RulesRepositoryException(e1);
            }
            throw new RulesRepositoryException(message, e);
        }
        catch(Exception e) {
            log.error("Caught Exception", e);
            throw new RulesRepositoryException(e);
        }
        
        try {                                    
            this.node.setProperty(DATE_EXPIRED_PROPERTY_NAME, newDateExpired);
            
            Calendar lastModified = Calendar.getInstance();
            this.node.setProperty(LAST_MODIFIED_PROPERTY_NAME, lastModified);
            
            this.node.getSession().save();
            
            this.node.checkin();
        }
        catch(Exception e) {
            log.error("Caught Exception", e);
            throw new RulesRepositoryException(e);
        }
    }
    
    /**
     * @return the rule language of this object's rune node
     * @throws RulesRepositoryException
     */
    public String getRuleLanguage() throws RulesRepositoryException {
        try {                        
            Node ruleNode;
            if(this.node.getPrimaryNodeType().getName().equals("nt:version")) {
                ruleNode = this.node.getNode("jcr:frozenNode");
            }
            else {
                ruleNode = this.node;
            }
                        
            Property ruleLanguageProperty = ruleNode.getProperty(RULE_LANGUAGE_PROPERTY_NAME);
            return ruleLanguageProperty.getValue().getString();
        }
        catch(Exception e) {
            log.error("Caught Exception", e);
            throw new RulesRepositoryException(e);
        }
    }
    
    /**
     * Creates a new version of this object's rule node, updating the lhs content for the
     * rule node. 
     * 
     * @param lhs the new lhs content for the rule
     * @throws RulesRepositoryException
     */
    public void updateLhs(String newLhsContent) throws RulesRepositoryException {
        try {
            this.node.checkout();
        }
        catch(UnsupportedRepositoryOperationException e) {
            String message = "";
            try {
                message = "Error: Caught UnsupportedRepositoryOperationException when attempting to checkout rule: " + this.node.getName() + ". Are you sure your JCR repository supports versioning? ";
                log.error(message, e);
            }
            catch (RepositoryException e1) {
                log.error("Caught Exception", e);
                throw new RulesRepositoryException(e1);
            }
            throw new RulesRepositoryException(message, e);
        }
        catch(Exception e) {
            log.error("Caught Exception", e);
            throw new RulesRepositoryException(e);
        }
        
        try {                                    
            this.node.setProperty(LHS_PROPERTY_NAME, newLhsContent);
            
            Calendar lastModified = Calendar.getInstance();
            this.node.setProperty(LAST_MODIFIED_PROPERTY_NAME, lastModified);
            
            this.node.getSession().save();
            
            this.node.checkin();
        }
        catch(Exception e) {
            log.error("Caught Exception", e);
            throw new RulesRepositoryException(e);
        }
    }
    
    /**
     * Creates a new version of this object's rule node, updating the rhs content for the
     * rule node. 
     * 
     * @param lhs the new rhs content for the rule
     * @throws RulesRepositoryException
     */
    public void updateRhs(String newRhsContent) throws RulesRepositoryException {
        try {
            this.node.checkout();
        }
        catch(UnsupportedRepositoryOperationException e) {
            String message = "";
            try {
                message = "Error: Caught UnsupportedRepositoryOperationException when attempting to checkout rule: " + this.node.getName() + ". Are you sure your JCR repository supports versioning? ";
                log.error(message, e);
            }
            catch (RepositoryException e1) {
                log.error("Caught Exception", e);
                throw new RulesRepositoryException(e1);
            }
            throw new RulesRepositoryException(message, e);
        }
        catch(Exception e) {
            log.error("Caught Exception", e);
            throw new RulesRepositoryException(e);
        }
        
        try {                                    
            this.node.setProperty(RHS_PROPERTY_NAME, newRhsContent);
            
            Calendar lastModified = Calendar.getInstance();
            this.node.setProperty(LAST_MODIFIED_PROPERTY_NAME, lastModified);
            
            this.node.getSession().save();
            
            this.node.checkin();
        }
        catch(Exception e) {
            log.error("Caught Exception", e);
            throw new RulesRepositoryException(e);
        }
    }        
    
    /**
     * Adds the specified tag to this object's rule node. Tags are stored as nodes in a tag area of
     * the repository. If the specified tag does not already have a corresponding node, a node is 
     * created for it.
     *  
     * @param tag the tag to add to the rule. rules can have multiple tags
     * @throws RulesRepositoryException 
     */
    public void addTag(String tag) throws RulesRepositoryException {
        try {
            //make sure this object's node is the head version
            if(this.node.getPrimaryNodeType().getName().equals("nt:version")) {
                String message = "Error. Tags can only be added to the head version of a rule node";
                log.error(message);
                throw new RulesRepositoryException(message);
            }                                       
            
            CategoryItem tagItem = this.rulesRepository.getOrCreateCategory(tag);
                                    
            //now set the tag property of the rule
            Property tagReferenceProperty;
            int i = 0;
            Value[] newTagValues = null;
            try {
                tagReferenceProperty = this.node.getProperty(TAG_PROPERTY_NAME);
                Value[] oldTagValues = tagReferenceProperty.getValues();
                
                //first, make sure this tag wasn't already there. while we're at it, lets copy the array
                newTagValues = new Value[oldTagValues.length + 1];                
                for(i=0; i<oldTagValues.length; i++) {
                    if(oldTagValues[i].getString().equals(tag)) {
                        log.info("tag '" + tag + "' already existed for rule node: " + this.node.getName());
                        return;
                    }
                    newTagValues[i] = oldTagValues[i];
                }
            }
            catch(PathNotFoundException e) {
                //the property doesn't exist yet, so create it in the finally block
                newTagValues = new Value[1];                 
            }
            finally {   
                if(newTagValues != null) {
                    newTagValues[i] = this.node.getSession().getValueFactory().createValue(tagItem.getNode());
                    this.node.checkout();
                    this.node.setProperty(TAG_PROPERTY_NAME, newTagValues);
                    this.node.getSession().save();
                    this.node.checkin();
                }
                else {
                    log.error("reached expected path of execution when adding tag '" + tag + "' to ruleNode: " + this.node.getName());
                }
            }
        }
        catch(Exception e) {
            log.error("Caught exception", e);
            throw new RulesRepositoryException(e);
        }
    }

    /**
     * Removes the specified tag from this object's rule node.
     * 
     * @param tag the tag to remove from the rule
     * @throws RulesRepositoryException 
     */
    public void removeTag(String tag) throws RulesRepositoryException {
        //TODO: implement if the removed tag no longer has anyone referencing it, remove the tag (are we sure we want to do this, for versioning's sake?)
        try {
            //make sure this object's node is the head version
            if(this.node.getPrimaryNodeType().getName().equals("nt:version")) {
                String message = "Error. Tags can only be removed from the head version of a rule node";
                log.error(message);
                throw new RulesRepositoryException(message);
            }                                                   
                                    
            //now set the tag property of the rule
            Property tagReferenceProperty;
            int i = 0;
            int j = 0;
            Value[] newTagValues = null;
            try {
                tagReferenceProperty = this.node.getProperty(TAG_PROPERTY_NAME);
                Value[] oldTagValues = tagReferenceProperty.getValues();
                
                //see if the tag was even there
                boolean wasThere = false;
                for(i=0; i<oldTagValues.length; i++) {
                    Node tagNode = this.node.getSession().getNodeByUUID(oldTagValues[i].getString());
                    if(tagNode.getName().equals(tag)) {                                                
                        wasThere = true;
                    }
                }
                
                if(wasThere) {
                    //copy the array, minus the specified tag
                    newTagValues = new Value[oldTagValues.length + 1];                
                    for(i=0; i<oldTagValues.length; i++) {
                        Node tagNode = this.node.getSession().getNodeByUUID(oldTagValues[i].getString());
                        if(!tagNode.getName().equals(tag)) {                                                         
                            newTagValues[j] = oldTagValues[i];
                            j++;
                        }
                    }
                }
                else {
                    //TODO: remove the tag if it isn't used by anyone else
                    return;
                }
            }
            catch(PathNotFoundException e) {
                //the property doesn't exist yet
                //TODO: first remove the tag if it isn't used by anyone else
                return;             
            }
            finally {   
                if(newTagValues != null) {
                    this.node.checkout();
                    this.node.setProperty(TAG_PROPERTY_NAME, newTagValues);
                    this.node.getSession().save();
                    this.node.checkin();
                }
                else {
                    log.error("reached expected path of execution when removing tag '" + tag + "' from ruleNode: " + this.node.getName());
                }
            }
        }
        catch(Exception e) {
            log.error("Caught exception", e);
            throw new RulesRepositoryException(e);
        }
    }
    
    /**
     * Gets a list of TagItem objects for this object's rule node.
     * 
     * @return a list of TagItem objects for each tag on the rule. If there are no tags, an empty list. 
     * @throws RulesRepositoryException
     */
    public List getTags() throws RulesRepositoryException {
        try {                            
            Node ruleNode;
            if(this.node.getPrimaryNodeType().getName().equals("nt:version")) {
                ruleNode = this.node.getNode("jcr:frozenNode");
            }
            else {
                ruleNode = this.node;
            }
            
            List returnList = new ArrayList();
            try {
                Property tagReferenceProperty = ruleNode.getProperty(TAG_PROPERTY_NAME);
                Value[] tagValues = tagReferenceProperty.getValues();                
                for(int i=0; i<tagValues.length; i++) {
                    Node tagNode = this.node.getSession().getNodeByUUID(tagValues[i].getString());
                    CategoryItem tagItem = new CategoryItem(this.rulesRepository, tagNode);
                    returnList.add(tagItem);
                }
            }
            catch(PathNotFoundException e) {
                //the property doesn't even exist yet, so just return nothing
            }
            return returnList;
        }
        catch(Exception e) {
            log.error("Caught exception", e);
            throw new RulesRepositoryException(e);
        }
    }
    
    /**
     * Sets this object's rule node's state property to refer to the specified state node
     * 
     * @param stateName the name of the state to set the rule node to
     * @throws RulesRepositoryException 
     */
    public void setState(String stateName) throws RulesRepositoryException {
        try {
            StateItem stateItem = this.rulesRepository.getState(stateName);
            this.setState(stateItem);
        }
        catch(Exception e) {
            log.error("Caught exception", e);
            throw new RulesRepositoryException(e);
        }
    }
    
    /**
     * Sets this object's rule node's state property to refer to the specified StateItem's node
     * 
     * @param stateItem the StateItem encapsulating the node to refer to from this object's node's state 
     *                  property
     * @throws RulesRepositoryException 
     */
    public void setState(StateItem stateItem) throws RulesRepositoryException {
        try {
            //make sure this node is a rule node
            if(this.node.getPrimaryNodeType().getName().equals("nt:version")) {
                String message = "Error. States can only be set for the head version of a rule node";
                log.error(message);
                throw new RulesRepositoryException(message);
            } 
            
            //now set the state property of the rule                              
            this.node.checkout();
            this.node.setProperty(STATE_PROPERTY_NAME, stateItem.getNode());
            this.node.getSession().save();
            this.node.checkin();        
        }
        catch(Exception e) {
            log.error("Caught exception", e);
            throw new RulesRepositoryException(e);
        }
    }
    
    /**
     * Gets StateItem object corresponding to the state property of this object's node
     * 
     * @return a StateItem object corresponding to the state property of this object's node, or null
     *         if the state property is not set
     * @throws RulesRepositoryException
     */
    public StateItem getState() throws RulesRepositoryException {
        try {
            Property stateProperty = this.node.getProperty(STATE_PROPERTY_NAME);
            Node stateNode = this.node.getSession().getNodeByUUID(stateProperty.getString());
            return new StateItem(this.rulesRepository, stateNode);
        }
        catch(PathNotFoundException e) {
            //not set
            return null;
        }
        catch(Exception e) {
            log.error("Caught exception", e);
            throw new RulesRepositoryException(e);
        }
    }

    /**
     * Gets a DslItem object corresponding to the DSL reference from the node that this object
     * encapsulates.
     * 
     * @return a DslItem object corresponding to the DSL reference for this rule node. If there is
     *         no DSL node referenced from this object's node, then null.
     * @throws RulesRepositoryException 
     */
    public DslItem getDsl() throws RulesRepositoryException {
        try {
            Property dslProperty = this.node.getProperty(DSL_PROPERTY_NAME);
            Node dslNode = this.node.getSession().getNodeByUUID(dslProperty.getString());
            return new DslItem(this.rulesRepository, dslNode);
        }
        catch(PathNotFoundException e) {
            //not set
            return null;
        }
        catch(Exception e) {
            log.error("Caught exception", e);
            throw new RulesRepositoryException(e);
        }
    }
    
    /**
     * Nicely formats the information contained by the node that this object encapsulates
     */
    public String toString() {                
        try {
            StringBuffer returnString = new StringBuffer();
            returnString.append("Content of rule item named '" + this.getName() + "':\n");
            returnString.append("LHS: " + this.getLhs() + "\n");
            returnString.append("RHS: " + this.getRhs() + "\n");
            returnString.append("------\n");
            
            returnString.append("Date Effective: " + this.getDateEffective() + "\n");
            returnString.append("Date Expired: " + this.getDateExpired() + "\n");
            returnString.append("Rule Language: " + this.getRuleLanguage() + "\n");
            returnString.append("Version Name: " + this.getVersionName() + "\n");
            returnString.append("------\n");
            
            returnString.append("Rule state: ");
            StateItem stateItem = this.getState();
            if(stateItem != null) {
                returnString.append(this.getState().getName() + "\n");
            }
            else {
                returnString.append("NO STATE SET FOR THIS NODE\n");
            }            
            returnString.append("------\n");
            
            returnString.append("Rule tags:\n");
            for(Iterator it=this.getTags().iterator(); it.hasNext();) {
                CategoryItem currentTag = (CategoryItem)it.next();
                returnString.append(currentTag.getName() + "\n");
            }
            returnString.append("--------------\n");
            return returnString.toString();
        }
        catch(Exception e) {         
            log.error("Caught Exception", e);
            return null;
        }
    }
        
    public VersionableItem getPrecedingVersion() throws RulesRepositoryException {
        try {
            Node precedingVersionNode = this.getPrecedingVersionNode();
            if(precedingVersionNode != null) {
                return new RuleItem(this.rulesRepository, precedingVersionNode);
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
                return new RuleItem(this.rulesRepository, succeedingVersionNode);
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