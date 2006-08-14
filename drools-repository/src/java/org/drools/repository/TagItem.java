package org.drools.repository;

import java.util.ArrayList;
import java.util.List;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.RepositoryException;

import org.apache.log4j.Logger;

/**
 * The TagItem class abstracts away details of the underlying JCR repository.
 * 
 * @author btruitt
 */
public class TagItem extends Item {
    private Logger log = Logger.getLogger(TagItem.class);
    
    /**
     * The name of the tag node type
     */
    public static final String TAG_NODE_TYPE_NAME = "drools:tag_node_type";
    
    /**
     * Constructs an object of type TagItem corresponding the specified node
     * @param rulesRepository the rulesRepository that instantiated this object
     * @param node the node to which this object corresponds
     * @throws RulesRepositoryException 
     */
    public TagItem(RulesRepository rulesRepository, Node node) throws RulesRepositoryException {
        super(rulesRepository, node);
        
        try {
            //make sure this node is a tag node       
            if(!(this.node.getPrimaryNodeType().getName().equals(TAG_NODE_TYPE_NAME))) {
                String message = this.node.getName() + " is not a node of type " + TAG_NODE_TYPE_NAME + ". It is a node of type: " + this.node.getPrimaryNodeType().getName();
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
     * @return the full path of this tag, rooted at the tag area of the repository. 
     * @throws RulesRepositoryException 
     */
    public String getFullPath() throws RulesRepositoryException {        
        try {
            log.debug("getting full path for node named: " + this.node.getName());
            
            StringBuffer returnString = new StringBuffer();
            returnString.append(this.node.getName());
            Node parentNode = this.node.getParent();
            while(!parentNode.getName().equals(RulesRepository.TAG_AREA)) {
                returnString.insert(0, parentNode.getName() + "/");
                parentNode = parentNode.getParent();
            }
            return returnString.toString();                           
        }
        catch(Exception e) {            
            log.error("Caught Exception: " + e);
            throw new RulesRepositoryException(e);
        }
    }
    
    /**
     * @return a List of the immediate children of this tag
     * @throws RulesRepositoryException 
     */
    public List getChildTags() throws RulesRepositoryException {
        List children = new ArrayList();
        
        try {
            NodeIterator it = this.node.getNodes();
            while(it.hasNext()) {
                Node currentNode = it.nextNode();
                children.add(new TagItem(this.rulesRepository, currentNode));
            }
        }
        catch(Exception e) {
            log.error("Caught Exception: " + e);
            throw new RulesRepositoryException(e);
        }        
        
        return children;
    }

    /**
     * Gets a TagItem object encapsulating the specified child tag. If the child tag 
     * doesn't exist, it is created.
     * 
     * @param tagName the name of the child tag to get or add
     * @return a TagItem encapsulating the specified child tag
     * @throws RulesRepositoryException
     */
    public TagItem getChildTag(String tagName) throws RulesRepositoryException {
        try {
            return this.rulesRepository.getTag(this.getFullPath() + "/" + tagName);
        }
        catch(Exception e) {
            log.error("Caught Exception: " + e);
            throw new RulesRepositoryException(e);
        }
    }
}
