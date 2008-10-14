package org.drools.repository;

import javax.jcr.Node;

import org.apache.log4j.Logger;

/**
 * The StateItem represents the status of an asset.
 * An asset can only be in 1 state at a time. Kind of for workflow.
 * 
 * 
 * @author btruitt
 */
public class StateItem extends Item {

    private Logger log = Logger.getLogger(StateItem.class);

    /**
     * All assets when created, or a new version saved, have a status of Draft.
     */
    public static final String DRAFT_STATE_NAME = "Draft";
    
    /**
     * The name of the state node type
     */
    public static final String STATE_NODE_TYPE_NAME = "drools:stateNodeType";
    
    /**
     * Constructs an object of type StateItem corresponding the specified node
     * 
     * @param rulesRepository the rulesRepository that instantiated this object
     * @param node the node to which this object corresponds
     * @throws RulesRepositoryException 
     */
    public StateItem(RulesRepository rulesRepository, Node node) throws RulesRepositoryException {
        super(rulesRepository, node);
        
        try {
            //make sure this node is a state node       
            if(!(this.node.getPrimaryNodeType().getName().equals(STATE_NODE_TYPE_NAME))) {
                String message = this.node.getName() + " is not a node of type " + STATE_NODE_TYPE_NAME + ". It is a node of type: " + this.node.getPrimaryNodeType().getName();
                log.error(message);
                throw new RulesRepositoryException(message);
            }    
        }
        catch(Exception e) {
            log.error("Caught exception: " + e);
            throw new RulesRepositoryException(e);
        }
    }
    
    public boolean equals(Object in) {
        if (!(in instanceof StateItem)) {
            return false;
        } else if (in == this) {
            return true;
        } else {
            StateItem other = (StateItem) in;
            return this.getName().equals( other.getName() );
        }
    }
    
    public String toString() {
        return "Current status: [" + getName() + "]  (" + super.toString() + ")";
    }
    
    public int hashCode() {
        return getName().hashCode();
    }
}