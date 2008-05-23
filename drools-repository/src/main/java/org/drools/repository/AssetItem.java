package org.drools.repository;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Calendar;
import java.util.Iterator;

import javax.jcr.Node;
import javax.jcr.PathNotFoundException;
import javax.jcr.Property;
import javax.jcr.RepositoryException;

import org.apache.log4j.Logger;

/**
 * The RuleItem class is used to abstract away the details of the underlying JCR repository.
 * It is used to pass information about rules stored in the repository.
 *
 * @author btruitt
 */
public class AssetItem extends CategorisableItem {
    private Logger             log                          = Logger.getLogger( AssetItem.class );
    /**
     * The name of the rule node type
     */
    public static final String RULE_NODE_TYPE_NAME           = "drools:assetNodeType";

    public static final String CONTENT_PROPERTY_NAME         = "drools:content";
    public static final String CONTENT_PROPERTY_BINARY_NAME  = "drools:binaryContent";
    public static final String CONTENT_PROPERTY_ATTACHMENT_FILENAME = "drools:attachmentFileName";

    /**
     * The name of the date effective property on the rule node type
     */
    public static final String DATE_EFFECTIVE_PROPERTY_NAME = "drools:dateEffective";

    /**
     * The name of the date expired property on the rule node type
     */
    public static final String DATE_EXPIRED_PROPERTY_NAME   = "drools:dateExpired";

    public static final String PACKAGE_NAME_PROPERTY        = "drools:packageName";



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

    public AssetItem() {
        super(null, null);
    }

    /**
     * returns the string contents of the rule node.
     * If this is a binary asset, this will return null (use getBinaryContent instead).
     */
    public String getContent() throws RulesRepositoryException {
        try {
        	if (isBinary()) {
        		return new String(this.getBinaryContentAsBytes());
        	}
            Node ruleNode = getVersionContentNode();
            if ( ruleNode.hasProperty( CONTENT_PROPERTY_NAME ) ) {
                Property data = ruleNode.getProperty( CONTENT_PROPERTY_NAME );
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
     * returns the number of bytes of the content.
     */
    public long getContentLength() {
		try {
			Node ruleNode = getVersionContentNode();
			if (ruleNode.hasProperty( CONTENT_PROPERTY_BINARY_NAME )) {
				   Property data = ruleNode.getProperty( CONTENT_PROPERTY_BINARY_NAME );
				   return data.getLength();
			} else {
	            if ( ruleNode.hasProperty( CONTENT_PROPERTY_NAME ) ) {
	                Property data = ruleNode.getProperty( CONTENT_PROPERTY_NAME );
	                return data.getLength();
	            } else {
	                return 0;
	            }
			}
		} catch (RepositoryException e) {
			log.error(e);
			throw new RulesRepositoryException(e);
		}
    }

    /**
     * True if this is a binary asset (or has binary content).
     */
    public boolean isBinary() {
		try {
			Node ruleNode = getVersionContentNode();
			return ruleNode.hasProperty( CONTENT_PROPERTY_BINARY_NAME );
		} catch (RepositoryException e) {
			log.error(e);
			throw new RulesRepositoryException(e);
		}
    }

    /**
     * If this asset contains binary data, this is how you return it.
     * Otherwise it will return null.
     */
    public InputStream getBinaryContentAttachment() {
        try {
            Node ruleNode = getVersionContentNode();
            if ( ruleNode.hasProperty( CONTENT_PROPERTY_BINARY_NAME ) ) {
                Property data = ruleNode.getProperty( CONTENT_PROPERTY_BINARY_NAME );
                return data.getStream();
            } else {
                return null;
            }
        } catch ( Exception e ) {
            log.error( "Caught Exception",
                       e );
            throw new RulesRepositoryException( e );
        }
    }

    /** Get the name of the "file" attachment, if one is set. Null otherwise */
    public String getBinaryContentAttachmentFileName() {
        return getStringProperty( CONTENT_PROPERTY_ATTACHMENT_FILENAME );
    }

    /**
     * This is a convenience method for returning the binary data as a byte array.
     */
    public byte[] getBinaryContentAsBytes() {
        try {
            Node ruleNode = getVersionContentNode();
            if ( ruleNode.hasProperty( CONTENT_PROPERTY_BINARY_NAME ) ) {
                Property data = ruleNode.getProperty( CONTENT_PROPERTY_BINARY_NAME );
                InputStream in = data.getStream();

                // Create the byte array to hold the data
                byte[] bytes = new byte[(int) data.getLength()];

                // Read in the bytes
                int offset = 0;
                int numRead = 0;
                while (offset < bytes.length
                       && (numRead=in.read(bytes, offset, bytes.length-offset)) >= 0) {
                    offset += numRead;
                }

                // Ensure all the bytes have been read in
                if (offset < bytes.length) {
                    throw new RulesRepositoryException("Could not completely read asset "+ getName());
                }

                // Close the input stream and return bytes
                in.close();
                return bytes;
            } else {
                return null;
            }
        } catch ( Exception e ) {
            log.error( e );
            if (e instanceof RuntimeException) throw (RuntimeException) e;
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
     * This will update the asset's content (checking it out if it is not already).
     * This will not save the session or create a new version of the node
     * (this has to be done seperately, as several properties may change as part of one edit).
     * This is only used if the asset is a textual asset. For binary, use the updateBinaryContent method
     * instead.
     */
    public AssetItem updateContent(String newRuleContent) throws RulesRepositoryException {
        checkout();
        try {
        	if (this.isBinary()) {
        		this.updateBinaryContentAttachment(new ByteArrayInputStream(newRuleContent.getBytes()));
        	}
            this.node.setProperty( CONTENT_PROPERTY_NAME,
                                   newRuleContent );
            return this;
        } catch ( RepositoryException e ) {
            log.error( "Unable to update the asset content", e );
            throw new RulesRepositoryException( e );
        }
    }

    /**
     * If the asset is a binary asset, then use this to update the content
     * (do NOT use text).
     */
	public AssetItem updateBinaryContentAttachment(InputStream data) {
        checkout();
        try {
            this.node.setProperty( CONTENT_PROPERTY_BINARY_NAME, data );
            return this;
        } catch (RepositoryException e ) {
            log.error( "Unable to update the assets binary content", e );
            throw new RulesRepositoryException( e );
        }
    }

    /**
     * Optionally set the filename to be associated with the binary content.
     */
    public void updateBinaryContentAttachmentFileName(String name) {
        updateStringProperty( name, CONTENT_PROPERTY_ATTACHMENT_FILENAME );
    }



    /**
     * This updates a user defined property (not one of the intrinsic ones).
     */
    public void updateUserProperty(String propertyName,
                                   String value) {
        if ( propertyName.startsWith( "drools:" ) ) {
            throw new IllegalArgumentException( "Can only set the pre defined fields using the appropriate methods." );
        }
        updateStringProperty( value,
                              propertyName );

    }

    /**
     * Nicely formats the information contained by the node that this object encapsulates
     */
	public String toString() {
        try {
            StringBuffer returnString = new StringBuffer();
            returnString.append( "Content of rule item named '" + this.getName() + "':\n" );
            returnString.append( "Content: " + this.getContent() + "\n" );
            returnString.append( "------\n" );

            returnString.append( "Archived: " + this.isArchived() + "\n" );
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

    /**
     * @return A property value (for a user defined property).
     */
    public String getUserProperty(String property) {
        return getStringProperty( property );
    }

    /**
     * This will remove the item.
     * The repository will need to be saved for this to take effect.
     * Typically the package that contains this should be versioned before removing this,
     * to make it easy to roll back.
     */
    public void remove() {
        checkIsUpdateable();
        if ( this.getDateExpired() != null ) {
            if ( Calendar.getInstance().before( this.getDateExpired() ) ) {
                throw new RulesRepositoryException( "Can't delete an item before its expiry date." );
            }
        }
        try {
            this.node.remove();
        } catch ( RepositoryException e ) {
            throw new RulesRepositoryException( e );
        }
    }

    /**
     *
     * @return An iterator over the nodes history.
     */
    public AssetHistoryIterator getHistory() {
        return new AssetHistoryIterator(this.rulesRepository, this.node);
    }


    /**
     * This will get the package an asset item belongs to.
     */
    public PackageItem getPackage() {

        try {
            if (this.isHistoricalVersion()) {
                throw new UnsupportedOperationException("Unable to get package for versioned asset. Use base revision.");
            }
            return new PackageItem(this.rulesRepository, this.node.getParent().getParent());
        } catch (RepositoryException e) {
            throw new RulesRepositoryException(e);
        }
    }

    /**
     * This converts a "filename" to an asset name.
     *
     * File name is foo.drl -> ["foo", "drl"]
     *
     * File name is foo.bar.xls -> ["foo", "bar.xls"]
     * @param fileName
     * @return
     */
    public static String[] getAssetNameFromFileName(String fileName) {

    	String[] r = new String[] {"", ""};
    	char[] cs = fileName.toCharArray();
    	boolean name = true;
    	for (int i = 0; i < cs.length; i++) {
    		if (name && cs[i] == '.') {
    			name = false;
    		} else if (name) {
				r[0] = r[0] + cs[i];
			} else {
				r[1] = r[1] + cs[i];
			}
		}
    	return r;


    }



}