package org.drools.repository;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;

import javax.jcr.AccessDeniedException;
import javax.jcr.ItemExistsException;
import javax.jcr.ItemNotFoundException;
import javax.jcr.Node;
import javax.jcr.PathNotFoundException;
import javax.jcr.Property;
import javax.jcr.RepositoryException;
import javax.jcr.Value;
import javax.jcr.lock.LockException;
import javax.jcr.nodetype.ConstraintViolationException;
import javax.jcr.version.VersionException;

import org.apache.log4j.Logger;

/**
 * The RuleItem class is used to abstract away the details of the underlying JCR repository.
 * It is used to pass information about rules stored in the repository.
 * 
 * @author btruitt
 */
public class AssetItem extends CategorisableItem {
    private Logger             log                            = Logger.getLogger( AssetItem.class );

    /**
     * The name of the DSL property on the rule node type
     */
    public static final String DSL_PROPERTY_NAME              = "drools:dslReference";

    /**
     * The name of the rule node type
     */
    public static final String RULE_NODE_TYPE_NAME            = "drools:ruleNodeType";

    public static final String RULE_CONTENT_PROPERTY_NAME     = "drools:content";

    public static final String RULE_CONTENT_URI_PROPERTY_NAME = "drools:contentURI";

    /**
     * The name of the date effective property on the rule node type
     */
    public static final String DATE_EFFECTIVE_PROPERTY_NAME   = "drools:dateEffective";

    /**
     * The name of the date expired property on the rule node type
     */
    public static final String DATE_EXPIRED_PROPERTY_NAME     = "drools:dateExpired";

    public static final String PACKAGE_NAME_PROPERTY = "drools:packageName";

    /**
     * Constructs a RuleItem object, setting its node attribute to the specified node.
     * 
     * @param rulesRepository the rulesRepository that instantiated this object
     * @param node the node in the repository that this RuleItem corresponds to
     * @throws RulesRepositoryException 
     */
    public AssetItem(RulesRepository rulesRepository,
                    Node node) throws RulesRepositoryException {
        super( rulesRepository,
               node );

        try {
            //make sure this node is a rule node       
            if ( !(this.node.getPrimaryNodeType().getName().equals( RULE_NODE_TYPE_NAME ) || isHistoricalVersion()) ) {
                String message = this.node.getName() + " is not a node of type " + RULE_NODE_TYPE_NAME + " nor nt:version. It is a node of type: " + this.node.getPrimaryNodeType().getName();
                log.error( message );
                throw new RulesRepositoryException( message );
            }
        } catch ( Exception e ) {
            log.error( "Caught exception",
                       e );
            throw new RulesRepositoryException( e );
        }
    }

    /**
     * returns the contents of the rule node.
     * It there is a URI, this may need to access the external resource
     * to grab/sync the latest, but in any case, it should be the real content.
     */
    public String getRuleContent() throws RulesRepositoryException {
        try {
            Node ruleNode = getVersionContentNode();
            if ( ruleNode.hasProperty( RULE_CONTENT_PROPERTY_NAME ) ) {
                Property data = ruleNode.getProperty( RULE_CONTENT_PROPERTY_NAME );
                return data.getValue().getString();

            } else {
                return null;
            }
        } catch ( Exception e ) {
            log.error( "Caught Exception",
                       e );
            throw new RulesRepositoryException( e );
        }
    }

    /**
     * returns the URI for where the rules content is stored.
     * Rule content may be stored in an external repository, 
     * such as subversion. This URI will contain information for
     * how to get to the exact version that maps to this rule node.
     */
    public String getRuleContentURI() throws RulesRepositoryException {
        try {
            Node ruleNode = getVersionContentNode();
            if ( ruleNode.hasProperty( RULE_CONTENT_URI_PROPERTY_NAME ) ) {
                Property data = ruleNode.getProperty( RULE_CONTENT_URI_PROPERTY_NAME );
                return data.getValue().getString();
            } else {
                return "";
            }

        } catch ( Exception e ) {
            log.error( "Caught Exception",
                       e );
            throw new RulesRepositoryException( e );
        }
    }

    /**
     * @return the date the rule becomes effective
     * @throws RulesRepositoryException
     */
    public Calendar getDateEffective() throws RulesRepositoryException {
        try {
            Node ruleNode = getVersionContentNode();

            Property dateEffectiveProperty = ruleNode.getProperty( DATE_EFFECTIVE_PROPERTY_NAME );
            return dateEffectiveProperty.getDate();
        } catch ( PathNotFoundException e ) {
            // doesn't have this property
            return null;
        } catch ( Exception e ) {
            log.error( "Caught Exception",
                       e );
            throw new RulesRepositoryException( e );
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
        checkIsUpdateable();
        checkout();
        try {
            this.node.setProperty( DATE_EFFECTIVE_PROPERTY_NAME,
                                   newDateEffective );
        } catch ( RepositoryException e ) {
            log.error( "Caught Exception",
                       e );
            throw new RulesRepositoryException( e );
        }
    }

    /**
     * @return the date the rule becomes expired
     * @throws RulesRepositoryException
     */
    public Calendar getDateExpired() throws RulesRepositoryException {
        try {
            Node ruleNode = getVersionContentNode();

            Property dateExpiredProperty = ruleNode.getProperty( DATE_EXPIRED_PROPERTY_NAME );
            return dateExpiredProperty.getDate();
        } catch ( PathNotFoundException e ) {
            // doesn't have this property
            return null;
        } catch ( Exception e ) {
            log.error( "Caught Exception",
                       e );
            throw new RulesRepositoryException( e );
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
        checkout();

        try {
            this.node.setProperty( DATE_EXPIRED_PROPERTY_NAME,
                                   newDateExpired );
        } catch ( Exception e ) {
            log.error( "Caught Exception",
                       e );
            throw new RulesRepositoryException( e );
        }
    }

    /**
     * This will update the rules content (checking it out if it is not already).
     * This will not save the session or create a new version of the node 
     * (this has to be done seperately, as several properties may change as part of one edit).
     */
    public AssetItem updateRuleContent(String newRuleContent) throws RulesRepositoryException {
        checkout();
        try {
            this.node.setProperty( RULE_CONTENT_PROPERTY_NAME,
                                   newRuleContent );
            return this;
        } catch ( RepositoryException e ) {
            log.error( "Caught Exception",
                       e );
            throw new RulesRepositoryException( e );
        }
    }

    /**
     * The URI represents a location for 
     */
    public void updateRuleContentURI(String newURI) throws RulesRepositoryException {
        checkout();
        try {
            this.node.setProperty( RULE_CONTENT_URI_PROPERTY_NAME,
                                   newURI );
        } catch ( RepositoryException e ) {
            log.error( "Caught Exception",
                       e );
            throw new RulesRepositoryException( e );
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
            Property dslProperty = getVersionContentNode().getProperty( DSL_PROPERTY_NAME );
            Node dslNode = this.node.getSession().getNodeByUUID( dslProperty.getString() );
            return new DslItem( this.rulesRepository,
                                dslNode );
        } catch ( PathNotFoundException e ) {
            //not set
            return null;
        } catch ( Exception e ) {
            log.error( "Caught exception",
                       e );
            throw new RulesRepositoryException( e );
        }
    }

    /**
     * Nicely formats the information contained by the node that this object encapsulates
     */
    public String toString() {
        try {
            StringBuffer returnString = new StringBuffer();
            returnString.append( "Content of rule item named '" + this.getName() + "':\n" );
            returnString.append( "Content: " + this.getRuleContent() + "\n" );
            returnString.append( "Content URI: " + this.getRuleContentURI() + "\n" );
            returnString.append( "------\n" );

            returnString.append( "Date Effective: " + this.getDateEffective() + "\n" );
            returnString.append( "Date Expired: " + this.getDateExpired() + "\n" );
            returnString.append( "------\n" );

            returnString.append( "Rule state: " );
            StateItem stateItem = this.getState();
            if ( stateItem != null ) {
                returnString.append( this.getState().getName() + "\n" );
            } else {
                returnString.append( "NO STATE SET FOR THIS NODE\n" );
            }
            returnString.append( "------\n" );

            returnString.append( "Rule tags:\n" );
            for ( Iterator it = this.getCategories().iterator(); it.hasNext(); ) {
                CategoryItem currentTag = (CategoryItem) it.next();
                returnString.append( currentTag.getName() + "\n" );
            }
            returnString.append( "--------------\n" );
            return returnString.toString();
        } catch ( Exception e ) {
            throw new RulesRepositoryException( e );
        }
    }

    public VersionableItem getPrecedingVersion() throws RulesRepositoryException {
        try {
            Node precedingVersionNode = this.getPrecedingVersionNode();
            if ( precedingVersionNode != null ) {
                return new AssetItem( this.rulesRepository,
                                     precedingVersionNode );
            } else {
                return null;
            }
        } catch ( Exception e ) {
            log.error( "Caught exception",
                       e );
            throw new RulesRepositoryException( e );
        }
    }

    public VersionableItem getSucceedingVersion() throws RulesRepositoryException {
        try {
            Node succeedingVersionNode = this.getSucceedingVersionNode();
            if ( succeedingVersionNode != null ) {
                return new AssetItem( this.rulesRepository,
                                     succeedingVersionNode );
            } else {
                return null;
            }
        } catch ( Exception e ) {
            log.error( "Caught exception",
                       e );
            throw new RulesRepositoryException( e );
        }
    }

    /**
     * Get the name of the enclosing package.
     * As assets are stored in versionable subfolders, this means walking up 2 levels in the 
     * hierarchy to get to the enclosing "package" node.
     */
    public String getPackageName() {
            return super.getStringProperty( PACKAGE_NAME_PROPERTY );
    }

}