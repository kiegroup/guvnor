package org.drools.repository;

import java.util.Calendar;

import javax.jcr.Node;
import javax.jcr.PathNotFoundException;
import javax.jcr.Property;
import javax.jcr.RepositoryException;
import javax.jcr.UnsupportedRepositoryOperationException;
import javax.jcr.Value;

import org.drools.repository.Item;

public abstract class VersionableItem extends Item {

    /**
     * The name of the title property on the node type
     */
    public static final String TITLE_PROPERTY_NAME         = "drools:title";

    /**
     * The name of the contributor property on the node type
     */
    public static final String CONTRIBUTOR_PROPERTY_NAME   = "drools:contributor";

    /**
     * The name of the description property on the rule node type
     */
    public static final String DESCRIPTION_PROPERTY_NAME   = "drools:description";

    /**
     * The name of the last modified property on the rule node type
     */
    public static final String LAST_MODIFIED_PROPERTY_NAME = "drools:last_modified";

    /**
     * The name of the last modified property on the rule node type
     */
    public static final String FORMAT_PROPERTY_NAME        = "drools:format";

    
    /** The name of the checkin/change comment for change tracking */
    public static final String CHECKIN_COMMENT              = "drools:checkin_comment";
    
    /**
     * The possible formats for the format property of the node
     */
    public static final String RULE_FORMAT                 = "Rule";
    public static final String DSL_FORMAT                  = "DSL";
    public static final String RULE_PACKAGE_FORMAT         = "Rule Package";
    public static final String FUNCTION_FORMAT             = "Function";

    private Node               contentNode                 = null;

    /**
     * Sets this object's node attribute to the specified node
     * 
     * @param rulesRepository the RulesRepository object that this object is being created from
     * @param node the node in the repository that this item corresponds to
     */
    public VersionableItem(RulesRepository rulesRepository,
                           Node node) {
        super( rulesRepository,
               node );
    }

    /**
     * @return the predessor node of this node in the version history, or null if no predecessor version exists
     * @throws RulesRepositoryException
     */
    protected Node getPrecedingVersionNode() throws RulesRepositoryException {
        try {
            Node versionNode;
            if ( this.node.getPrimaryNodeType().getName().equals( "nt:version" ) ) {
                versionNode = this.node;
            } else {
                versionNode = this.node.getBaseVersion();
            }

            Property predecessorsProperty = versionNode.getProperty( "jcr:predecessors" );
            Value[] predecessorValues = predecessorsProperty.getValues();

            if ( predecessorValues.length > 0 ) {
                Node predecessorNode = this.node.getSession().getNodeByUUID( predecessorValues[0].getString() );

                //we don't want to return the root node - it isn't a true predecessor
                if ( predecessorNode.getName().equals( "jcr:rootVersion" ) ) {
                    return null;
                }

                return predecessorNode;
            }
        } catch ( PathNotFoundException e ) {
            //do nothing - this will happen if no predecessors exits
        } catch ( Exception e ) {
            log.error( "Caught exception",
                       e );
            throw new RulesRepositoryException( e );
        }
        return null;
    }

    /**
     * @return the successor node of this node in the version history
     * @throws RulesRepositoryException
     */
    protected Node getSucceedingVersionNode() throws RulesRepositoryException {
        try {
            Property successorsProperty = this.node.getProperty( "jcr:successors" );
            Value[] successorValues = successorsProperty.getValues();

            if ( successorValues.length > 0 ) {
                Node successorNode = this.node.getSession().getNodeByUUID( successorValues[0].getString() );
                return successorNode;
            }
        } catch ( PathNotFoundException e ) {
            //do nothing - this will happen if no successors exist
        } catch ( Exception e ) {
            log.error( "Caught exception",
                       e );
            throw new RulesRepositoryException( e );
        }
        return null;
    }

    /**
     * @return an Iterator over VersionableItem objects encapsulating each successor node of this 
     *         Item's node
     * @throws RulesRepositoryException
     */
    public ItemVersionIterator getSuccessorVersionsIterator() throws RulesRepositoryException {
        return new ItemVersionIterator( this,
                                        ItemVersionIterator.ITERATION_TYPE_SUCCESSOR );
    }

    /**
     * @return an Iterator over VersionableItem objects encapsulating each predecessor node of this 
     *         Item's node
     * @throws RulesRepositoryException
     */
    public ItemVersionIterator getPredecessorVersionsIterator() throws RulesRepositoryException {
        return new ItemVersionIterator( this,
                                        ItemVersionIterator.ITERATION_TYPE_PREDECESSOR );
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

    /** 
     * Gets the Title of the versionable node.  See the Dublin Core documentation for more
     * explanation: http://dublincore.org/documents/dces/
     * 
     * @return the title of the node this object encapsulates
     * @throws RulesRepositoryException
     */
    public String getTitle() throws RulesRepositoryException {
        try {
            Node theNode;
            if ( this.node.getPrimaryNodeType().getName().equals( "nt:version" ) ) {
                theNode = this.node.getNode( "jcr:frozenNode" );
            } else {
                theNode = this.node;
            }

            Property data = theNode.getProperty( TITLE_PROPERTY_NAME );
            return data.getValue().getString();
        } catch ( Exception e ) {
            log.error( "Caught Exception",
                       e );
            throw new RulesRepositoryException( e );
        }
    }

    /** 
     * Creates a new version of this object's node, updating the title content 
     * for the node.
     * <br>
     * See the Dublin Core documentation for more
     * explanation: http://dublincore.org/documents/dces/
     * 
     * @param title the new title for the node
     * @throws RulesRepositoryException
     */
    public void updateTitle(String title) throws RulesRepositoryException {
        try {
            Node theNode;
            if ( this.node.getPrimaryNodeType().getName().equals( "nt:version" ) ) {
                theNode = this.node.getNode( "jcr:frozenNode" );
            } else {
                theNode = this.node;
            }

            theNode.checkout();
            theNode.setProperty( TITLE_PROPERTY_NAME,
                                 title );

            Calendar lastModified = Calendar.getInstance();
            this.node.setProperty( LAST_MODIFIED_PROPERTY_NAME,
                                   lastModified );

            theNode.save();
            theNode.checkin();
        } catch ( Exception e ) {
            log.error( "Caught Exception",
                       e );
            throw new RulesRepositoryException( e );
        }
    }

    /** 
     * Gets the Contributor of the versionable node.  See the Dublin Core documentation for more
     * explanation: http://dublincore.org/documents/dces/
     * 
     * @return the contributor of the node this object encapsulates
     * @throws RulesRepositoryException
     */
    public String getContributor() {
        try {
            Property data = getVersionContentNode().getProperty( CONTRIBUTOR_PROPERTY_NAME );
            return data.getValue().getString();
        } catch ( Exception e ) {
            log.error( "Caught Exception",
                       e );
            throw new RulesRepositoryException( e );
        }
    }

    /**
     * See the Dublin Core documentation for more
     * explanation: http://dublincore.org/documents/dces/
     * 
     * @return the description of this object's node.
     * @throws RulesRepositoryException
     */
    public String getDescription() throws RulesRepositoryException {
        try {
            
            Property data = getVersionContentNode().getProperty( DESCRIPTION_PROPERTY_NAME );
            return data.getValue().getString();
        } catch ( Exception e ) {
            log.error( "Caught Exception",
                       e );
            throw new RulesRepositoryException( e );
        }
    }

    /**
     * This will return the checkin comment for the latest revision.
     */
    public String getCheckinComment() throws RulesRepositoryException {
        try {            
            Property data = getVersionContentNode().getProperty( CHECKIN_COMMENT );
            return data.getValue().getString();
        } catch ( Exception e ) {
            log.error( "Caught Exception",
                       e );
            throw new RulesRepositoryException( e );
        }
    }    
    
    /**
     * @return the date the function node (this version) was last modified
     * @throws RulesRepositoryException
     */
    public Calendar getLastModified() throws RulesRepositoryException {
        try {

            Property lastModifiedProperty = getVersionContentNode().getProperty( "drools:last_modified" );
            return lastModifiedProperty.getDate();
        } catch ( Exception e ) {
            log.error( "Caught Exception",
                       e );
            throw new RulesRepositoryException( e );
        }
    }

    /**
     * Creates a new version of this object's node, updating the description content 
     * for the node.
     * <br>
     * See the Dublin Core documentation for more
     * explanation: http://dublincore.org/documents/dces/ 
     * 
     * @param newDescriptionContent the new description content for the rule
     * @throws RulesRepositoryException
     */
    public void updateDescription(String newDescriptionContent) throws RulesRepositoryException {
        try {
            this.node.checkout();
        } catch ( UnsupportedRepositoryOperationException e ) {
            String message = "";
            try {
                message = "Error: Caught UnsupportedRepositoryOperationException when attempting to checkout node: " + this.node.getName() + ". Are you sure your JCR repository supports versioning? ";
                log.error( message,
                           e );
            } catch ( RepositoryException e1 ) {
                log.error( "Caught Exception",
                           e );
                throw new RulesRepositoryException( e1 );
            }
            throw new RulesRepositoryException( message,
                                                e );
        } catch ( Exception e ) {
            log.error( "Caught Exception",
                       e );
            throw new RulesRepositoryException( e );
        }

        try {
            this.node.setProperty( DESCRIPTION_PROPERTY_NAME,
                                   newDescriptionContent );

            Calendar lastModified = Calendar.getInstance();
            this.node.setProperty( LAST_MODIFIED_PROPERTY_NAME,
                                   lastModified );

            this.node.getSession().save();

            this.node.checkin();
        } catch ( Exception e ) {
            log.error( "Caught Exception",
                       e );
            throw new RulesRepositoryException( e );
        }
    }

    /**
     * See the Dublin Core documentation for more
     * explanation: http://dublincore.org/documents/dces/
     * 
     * @return the format of this object's node
     * @throws RulesRepositoryException
     */
    public String getFormat() throws RulesRepositoryException {
        try {
            Node theNode;
            if ( this.node.getPrimaryNodeType().getName().equals( "nt:version" ) ) {
                theNode = this.node.getNode( "jcr:frozenNode" );
            } else {
                theNode = this.node;
            }

            Property data = theNode.getProperty( FORMAT_PROPERTY_NAME );
            return data.getValue().getString();
        } catch ( Exception e ) {
            log.error( "Caught Exception",
                       e );
            throw new RulesRepositoryException( e );
        }
    }

    /**
     * When retrieving content, if we are dealing with a version in the history, 
     * we need to get the actual content node to retrieve values.
     * 
     */
    public Node getVersionContentNode() throws RepositoryException,
                                       PathNotFoundException {
        if ( this.contentNode == null ) {
            
            if ( this.node.getPrimaryNodeType().getName().equals( "nt:version" ) ) {
                contentNode = this.node.getNode( "jcr:frozenNode" );
            } else {
                contentNode = this.node;
            }
            
        }

        return contentNode;
    }

    /**
     * This will check out the node prior to editing.
     */
    public void checkout() {

        try {
            this.node.checkout();
        } catch ( UnsupportedRepositoryOperationException e ) {
            String message = "";
            try {
                message = "Error: Caught UnsupportedRepositoryOperationException when attempting to checkout rule: " + this.node.getName() + ". Are you sure your JCR repository supports versioning? ";
                log.error( message,
                           e );
            } catch ( RepositoryException e1 ) {
                log.error( "Caught Exception",
                           e );
                throw new RulesRepositoryException( e1 );
            }
            throw new RulesRepositoryException( message,
                                                e );
        } catch ( Exception e ) {
            log.error( "Caught Exception",
                       e );
            throw new RulesRepositoryException( e );
        }
    }
    
    /** 
     * This will save the content (if it hasn't been already) and 
     * then check it in to create a new version.
     */
    public void checkin(String comment)  {
        try {
        this.node.setProperty( VersionableItem.CHECKIN_COMMENT, comment);
        this.node.getSession().save();        
        this.node.checkin();
        } catch (Exception e) {
            throw new RulesRepositoryException("Unable to checkin.", e);
        }
    }
}
