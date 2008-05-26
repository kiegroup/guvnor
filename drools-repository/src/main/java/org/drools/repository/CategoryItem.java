package org.drools.repository;

import java.util.ArrayList;
import java.util.List;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.Property;
import javax.jcr.PropertyIterator;
import javax.jcr.RepositoryException;

import org.apache.log4j.Logger;

/**
 * The TagItem class abstracts away details of the underlying JCR repository.
 * 
 * @author btruitt
 */
public class CategoryItem extends Item {
    private Logger log = Logger.getLogger(CategoryItem.class);
    
    /**
     * The name of the tag node type
     */
    public static final String TAG_NODE_TYPE_NAME = "drools:categoryNodeType";
    
    /**
     * Constructs an object of type TagItem corresponding the specified node
     * @param rulesRepository the rulesRepository that instantiated this object
     * @param node the node to which this object corresponds
     * @throws RulesRepositoryException 
     */
    public CategoryItem(RulesRepository rulesRepository, Node node) throws RulesRepositoryException {
        super(rulesRepository, node);
        
//        try {
//            //make sure this node is a tag node       
//            if(!(this.node.getPrimaryNodeType().getName().equals(TAG_NODE_TYPE_NAME))) {
//                String message = this.node.getName() + " is not a node of type " + TAG_NODE_TYPE_NAME + ". It is a node of type: " + this.node.getPrimaryNodeType().getName();
//                log.error(message);
//                throw new RulesRepositoryException(message);
//            }    
//        }
//        catch(Exception e) {
//            log.error("Caught exception: " + e);
//            throw new RulesRepositoryException(e);
//        }
    }

    /**
     * @return the full path of this tag, rooted at the tag area of the repository. 
     * @throws RulesRepositoryException 
     */
    public String getFullPath() throws RulesRepositoryException {        
        try {
            
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
                children.add(new CategoryItem(this.rulesRepository, currentNode));
            }
        }
        catch(Exception e) {
            log.error("Caught Exception: " + e);
            throw new RulesRepositoryException(e);
        }        
        
        return children;
    }

    /**
     * This will create a child category under this one
     */
    public CategoryItem addCategory(String name,
                                    String description) {
        try {
            Node child = this.node.addNode( name, CategoryItem.TAG_NODE_TYPE_NAME );
            this.rulesRepository.getSession().save();
            return new CategoryItem(this.rulesRepository, child);
        } catch (Exception e) {
            if (e instanceof RuntimeException) {
                throw (RuntimeException) e;
            } else {
                throw new RulesRepositoryException(e);
            }
        }

    }

    /**
     * This will remove the category and any ones under it. 
     * This will only work if you have no current assets linked to the category unless the assets are archived.
     */
    public void remove() {
        try {
            PropertyIterator pi = this.node.getReferences();
            while (pi.hasNext()) {
				Property p = pi.nextProperty();
				Node parentNode = p.getParent();

				if (parentNode.getProperty(
					AssetItem.CONTENT_PROPERTY_ARCHIVE_FLAG).getBoolean()) {
					CategorisableItem.removeCategory(parentNode, this.node.getName());			
				} else {
					throw new RulesRepositoryException(
					"The category still has some assets linked to it. You will need to remove the links so you can delete the cateogory.");
				}
			}
            this.node.remove();
        } catch ( RepositoryException e ) {
            log.error( e );
        }
    }
 
}
