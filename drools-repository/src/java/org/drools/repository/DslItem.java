package org.drools.repository;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.jcr.Node;
import javax.jcr.Property;
import javax.jcr.RepositoryException;
import javax.jcr.UnsupportedRepositoryOperationException;
import javax.jcr.version.Version;
import javax.jcr.version.VersionIterator;

import org.apache.log4j.Logger;

/**
 * The DslItem is used to abstract away details of the JCR repository 
 * 
 * @author btruitt
 */
public class DslItem extends VersionableItem {
    private Logger log = Logger.getLogger(DslItem.class);
    
    /**
     * The name of the DSL node type
     */
    public static final String DSL_NODE_TYPE_NAME = "drools:dsl_node_type";

    public static final String DSL_CONTENT = "drools:dsl_content";
    
    /**
     * Constructs a DslItem object with the specified node as its node attribute
     * 
     * @param rulesRepository the rulesRepository that instantiated this object
     * @param node
     * @throws RulesRepositoryException 
     */
    public DslItem(RulesRepository rulesRepository, Node node) throws RulesRepositoryException {
        super(rulesRepository, node);
        
        try {
            //make sure this node is a dsl node       
            if(!(this.node.getPrimaryNodeType().getName().equals(DSL_NODE_TYPE_NAME) ||
                 this.node.getPrimaryNodeType().getName().equals("nt:version"))) {
                String message = this.node.getName() + " is not a node of type " + DSL_NODE_TYPE_NAME + " nor nt:version. It is a node of type: " + this.node.getPrimaryNodeType().getName();
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
     * returns the content of this object's dsl node
     * 
     * @return the content of this object's dsl node
     * @throws RulesRepositoryException
     */
    public String getContent() throws RulesRepositoryException {
        try {            
            Node dslNode;
            if(this.node.getPrimaryNodeType().getName().equals("nt:version")) {
                dslNode = this.node.getNode("jcr:frozenNode");
            }
            else {
                dslNode = this.node;
            }
            
            Property data = dslNode.getProperty( DSL_CONTENT );
            return data.getValue().getString();
        }
        catch(Exception e) {
            log.error("Caught Exception", e);
            throw new RulesRepositoryException(e);
        }
    }
    
    /**
     * Creates a new version of this object's dsl node, using the content and attributes of the
     * specified file.
     * 
     * @param file the file from which to get the content and attributes for the new version of the
     *             dsl node
     * @throws RulesRepositoryException
     */
    public void updateContent(String content) throws RulesRepositoryException {
        try {
            this.node.checkout();
        }
        catch(UnsupportedRepositoryOperationException e) {
            String message = "";
            try {
                message = "Error: Caught UnsupportedRepositoryOperationException when attempting to checkout node: " + this.node.getName() + ". Are you sure your JCR repository supports versioning? ";
                log.error(message + e);
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
            
            Calendar lastModified = Calendar.getInstance();
            this.node.setProperty(LAST_MODIFIED_PROPERTY_NAME, lastModified);
            this.node.setProperty( DSL_CONTENT, content );
            
            this.node.getSession().save();                      
        }
        catch(Exception e) {
            log.error("Caught Exception", e);
            throw new RulesRepositoryException(e);
        }
        finally {
            try {
                this.node.checkin();
            }
            catch(Exception e) {
                log.error("Caught Exception: " + e);
                throw new RulesRepositoryException(e);
            }
        }
    }
    
    /**
     * Nicely formats the information contained by the node that this object encapsulates
     */
    public String toString() {                
        try {
            StringBuffer returnString = new StringBuffer();
            returnString.append("Content of DSL node named " + this.node.getName() + ":\n");
            returnString.append(this.getContent() + "\n");
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
                return new DslItem(this.rulesRepository, precedingVersionNode);
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
                return new DslItem(this.rulesRepository, succeedingVersionNode);
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
