package org.drools.repository;

import javax.jcr.Node;
import javax.jcr.PathNotFoundException;
import javax.jcr.Property;
import javax.jcr.Value;

import org.drools.repository.Item;

public abstract class VersionableItem extends Item {
    
    /**
     * The name of the name property on the node type
     */
    public static final String NAME_PROPERTY_NAME = "drools:name";
    
    /**
     * Sets this object's node attribute to the specified node
     * 
     * @param rulesRepository the RulesRepository object that this object is being created from
     * @param node the node in the repository that this item corresponds to
     */
    public VersionableItem(RulesRepository rulesRepository, Node node) {
        super(rulesRepository, node);
    }
    
    /**
     * @return the predessor node of this node in the version history, or null if no predecessor version exists
     * @throws RulesRepositoryException
     */
    protected Node getPrecedingVersionNode() throws RulesRepositoryException {
        try {
            Node versionNode;
            if(this.node.getPrimaryNodeType().getName().equals("nt:version")) {
                versionNode = this.node;
            }
            else {                
                versionNode = this.node.getBaseVersion();
            }                       
            
            Property predecessorsProperty = versionNode.getProperty("jcr:predecessors");
            Value [] predecessorValues = predecessorsProperty.getValues();                                              
            
            if(predecessorValues.length > 0) {
                Node predecessorNode = this.node.getSession().getNodeByUUID(predecessorValues[0].getString());
                
                //we don't want to return the root node - it isn't a true predecessor
                if(predecessorNode.getName().equals("jcr:rootVersion")) {
                    return null; 
                }
                
                return predecessorNode;
            }           
        }
        catch(PathNotFoundException e) {
            //do nothing - this will happen if no predecessors exits
        }
        catch(Exception e) {
            log.error("Caught exception", e);
            throw new RulesRepositoryException(e);
        }
        return null;               
    }

    /**
     * @return the successor node of this node in the version history
     * @throws RulesRepositoryException
     */
    protected Node getSucceedingVersionNode() throws RulesRepositoryException {
        try {
            Property successorsProperty = this.node.getProperty("jcr:successors");
            Value [] successorValues = successorsProperty.getValues();                      
            
            if(successorValues.length > 0) {
                Node successorNode = this.node.getSession().getNodeByUUID(successorValues[0].getString());
                return successorNode;
            }           
        }
        catch(PathNotFoundException e) {
            //do nothing - this will happen if no successors exist
        }
        catch(Exception e) {
            log.error("Caught exception", e);
            throw new RulesRepositoryException(e);
        }
        return null;
    }

    /**
     * @return an Iterator over VersionableItem objects encapsulating each successor node of this 
     *         Item's node
     * @throws RulesRepositoryException
     */
    public ItemVersionIterator getSuccessorVersionsIterator() throws RulesRepositoryException {
        return new ItemVersionIterator(this, ItemVersionIterator.ITERATION_TYPE_SUCCESSOR);
    }


    /**
     * @return an Iterator over VersionableItem objects encapsulating each predecessor node of this 
     *         Item's node
     * @throws RulesRepositoryException
     */
    public ItemVersionIterator getPredecessorVersionsIterator() throws RulesRepositoryException {
        return new ItemVersionIterator(this, ItemVersionIterator.ITERATION_TYPE_PREDECESSOR);
    }

    /**
     * Clients of this method can cast the resulting object to the type of object they are 
     * calling the method on (e.g. 
     *         <pre>
     *           RuleItem item;
     *           ...
     *           RuleItem predcessor = (RuleItem) item.getPrecedingVersion();
     *         </pre>
     * @return a VersionableItem object encapsulating the predessor node of this node in the 
     *         version history, or null if no predecessor version exists
     * @throws RulesRepositoryException
     */
    public abstract VersionableItem getPrecedingVersion() throws RulesRepositoryException;
    
    /**
     * Clients of this method can cast the resulting object to the type of object they are 
     * calling the method on (e.g. 
     *         <pre>
     *           RuleItem item;
     *           ...
     *           RuleItem successor = (RuleItem) item.getSucceedingVersion();
     *         </pre>
     *         
     * @return a VersionableItem object encapsulating the successor node of this node in the 
     *         version history. 
     * @throws RulesRepositoryException
     */
    public abstract VersionableItem getSucceedingVersion() throws RulesRepositoryException; 
    
    @Override
    public String getName() throws RulesRepositoryException {
        try {                        
            Node ruleNode;
            if(this.node.getPrimaryNodeType().getName().equals("nt:version")) {
                ruleNode = this.node.getNode("jcr:frozenNode");
            }
            else {
                ruleNode = this.node;
            }
                        
            Property data = ruleNode.getProperty(NAME_PROPERTY_NAME);
            return data.getValue().getString();
        }
        catch(Exception e) {
            log.error("Caught Exception", e);
            throw new RulesRepositoryException(e);
        }
    }
}
