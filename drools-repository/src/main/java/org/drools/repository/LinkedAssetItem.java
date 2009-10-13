package org.drools.repository;

import java.io.*;
import java.util.Calendar;
import java.util.Iterator;

import javax.jcr.InvalidItemStateException;
import javax.jcr.Node;
import javax.jcr.PathNotFoundException;
import javax.jcr.Property;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.UnsupportedRepositoryOperationException;

import org.apache.log4j.Logger;
import org.drools.repository.events.StorageEventManager;
import org.drools.repository.utils.IOUtils;

/**
 * LinkedAssetItem does not represent a real Asset, instead it is a wrapper node that  
 * wraps over an existing asset. LinkedAssetItem only has three properties that belongs to itself:
 * NAME: LinkedAssetItem has its own name which is the node name
 * LINKED_NODE_UUID: the node UUID that LinkedAssetItem links to.
 * PACKAGE_NAME_PROPERTY: our current design only allows one AssetItem belongs to one package. 
 * LinkedAssetItem and the linked(wrapped) asset have to have their own package name respectively. 
 * NOTE: those three properties are read only. Thus there is no need for LinkedAssetItem to have its own
 * check in/out method once it is created and saved.
 * Requests to all other properties are delegated to the linked(wrapped) asset. 
 */
public class LinkedAssetItem extends AssetItem {
    private Logger log = Logger.getLogger( LinkedAssetItem.class );
 
    protected Node wrapperNode;
    public static final String LINKED_NODE_UUID                = "drools:linkedNodeUUID";
    
    /**
     * Constructs a LinkedAssetItem object, setting its node attribute to the specified node.
     *
     * @param rulesRepository the rulesRepository that instantiated this object
     * @param node the node in the repository that this RuleItem corresponds to
     * @throws RulesRepositoryException
     */
    public LinkedAssetItem(RulesRepository rulesRepository,
                     Node wrapperNode) throws RulesRepositoryException {        
        super( rulesRepository,	wrapperNode );
        
        try {
			// If this node is a node that is linked to another node.
			if (wrapperNode.hasProperty(LINKED_NODE_UUID)) {
				Property linkedNodeUUIDProperty = wrapperNode
						.getProperty(LINKED_NODE_UUID);
				String linkedNodeUUID = linkedNodeUUIDProperty.getValue()
						.getString();

				Node linkedNode = rulesRepository.getSession().getNodeByUUID(
						linkedNodeUUID);

	        	this.wrapperNode = wrapperNode;  
				this.node = linkedNode;
				
				//When a node is created, it is in checked out status. So we do similar to the linked node. 
				if(!this.node.isCheckedOut()) {
					this.node.checkout();
				}
			} else {
				this.wrapperNode = null; 
			}
		} catch (RepositoryException e) {
			this.wrapperNode = null; 
		}      
    }

    public LinkedAssetItem() {
        super( null,
               null );
    }
    
    public boolean isLinkedAssetItem() {
		if (wrapperNode == null) {
			return false;
		} 
		return true;    	
    }
    
    /**
     * @return A unique identifier for this items content node.
     * return the UUID of the wrapper node. 
     */
    public String getUUID() {
		if (wrapperNode == null) {
			return super.getUUID();
		} else {
	        try {
	            return this.getVersionContentNode1().getUUID();
	        } catch (  RepositoryException e ) {
	            throw new RulesRepositoryException(e);
	        }
		}
    }
    
    /**
     * Need to get the name from the content node, not the version node
     * if it is in fact a version !
     */
    public String getName() {
		if (wrapperNode == null) {
			return super.getName();
		} else {
	        try {
	            return this.getVersionContentNode1().getName();
	        } catch (  RepositoryException e ) {
	            throw new RulesRepositoryException(e);
	        }
		}
    }

    /**
     * This will get the package that the wrapper asset belongs to.
     */
    public PackageItem getPackage() {
		if (wrapperNode == null) {
			return super.getPackage();
		} else {
	        try {
	            if ( this.isHistoricalVersion() ) {
	            	return this.rulesRepository.loadPackage(this.getPackageName());
	            }
	            return new PackageItem( this.rulesRepository,
	                                    this.wrapperNode.getParent().getParent() );
	        } catch ( RepositoryException e ) {
	            throw new RulesRepositoryException( e );
	        }
		}

    }
    
    /**
     * Get the wrapper asset's package name.
     */
    public String getPackageName() {
		if (wrapperNode == null) {
			return super.getPackageName();
		} else {
			return getStringProperty1(PACKAGE_NAME_PROPERTY);
		}
	}      
  
     /**
     * This will save the content (if it hasn't been already) and
     * then check it in to create a new version.
     * It will also set the last modified property.
     */
    public void checkin(String comment) {
    	super.checkin(comment);
    	
    	//LinkedAssetItem only has read-only properties. So actually save and checkin is only needed once
    	//when LinkedAssetItem is created. 
		if (wrapperNode != null) {
            checkIsUpdateable1();
			try {
				this.wrapperNode.getSession().save();
				this.wrapperNode.checkin();

				if (StorageEventManager.hasSaveEvent()) {
					if (this instanceof AssetItem) {
						StorageEventManager.getSaveEvent().onAssetCheckin(
								(AssetItem) this);
					}
				}
				StorageEventManager.doCheckinEvents(this);

			} catch (RepositoryException e) {
				throw new RulesRepositoryException("Unable to checkin.", e);
			}
		}
    }
    
    public String getStringProperty1(String property) {
        try {
            Node theNode = getVersionContentNode1();
            if ( theNode.hasProperty( property ) ) {
                Property data = theNode.getProperty( property );
                return data.getValue().getString();
            } else {
                return "";
            }
        } catch ( RepositoryException e ) {
            throw new RulesRepositoryException( e );
        }
    }
    
    public Node getVersionContentNode1() throws RepositoryException,
                                       PathNotFoundException {
        return getRealContentFromVersion(this.wrapperNode);
    }
    
    protected void checkIsUpdateable1() {
        try {
            if ( this.wrapperNode.getPrimaryNodeType().getName().equals( "nt:version" ) ) {
                String message = "Error. Tags can only be added to the head version of a rule node";
                log.error( message );
                throw new RulesRepositoryException( message );
            }
        } catch ( RepositoryException e ) {
            throw new RulesRepositoryException( e );
        }
    }

 }