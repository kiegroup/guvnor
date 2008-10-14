package org.drools.repository;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import javax.jcr.ItemExistsException;
import javax.jcr.Node;
import javax.jcr.Property;
import javax.jcr.RepositoryException;
import javax.jcr.query.Query;
import javax.jcr.query.QueryResult;

import org.apache.log4j.Logger;

/**
 * A PackageItem object aggregates a set of assets (for example, rules). This is advantageous for systems using the JBoss Rules
 * engine where the application might make use of many related rules.
 * <p>
 * A PackageItem refers to rule nodes within the RulesRepository.  It contains the "master copy" of assets (which may be linked
 * into other packages or other types of containers).
 * This is a container "node".
 *
 * @author btruitt
 */
public class PackageItem extends VersionableItem {
    private static Logger      log                              = Logger.getLogger( PackageItem.class );

    /**
     * This is the name of the rules "subfolder" where rules are kept
     * for this package.
     */
    public static final String ASSET_FOLDER_NAME                = "assets";

    /**
     * The dublin core format attribute.
     */
    public static final String PACKAGE_FORMAT                    = "package";

    /**
     * The name of the rule package node type
     */
    public static final String RULE_PACKAGE_TYPE_NAME           = "drools:packageNodeType";


    public static final String HEADER_PROPERTY_NAME             = "drools:header";
    public static final String EXTERNAL_URI_PROPERTY_NAME             = "drools:externalURI";

    private static final String COMPILED_PACKAGE_PROPERTY_NAME = "drools:compiledPackage";

    /**
     * Constructs an object of type RulePackageItem corresponding the specified node
     * @param rulesRepository the rulesRepository that instantiated this object
     * @param node the node to which this object corresponds
     * @throws RulesRepositoryException
     */
    public PackageItem(RulesRepository rulesRepository,
                           Node node) throws RulesRepositoryException {
        super( rulesRepository,
               node );

        try {
            //make sure this node is a rule package node
            if ( !(this.node.getPrimaryNodeType().getName().equals( RULE_PACKAGE_TYPE_NAME ) ||
                    isHistoricalVersion())) {
                String message = this.node.getName() + " is not a node of type " + RULE_PACKAGE_TYPE_NAME + ". It is a node of type: " + this.node.getPrimaryNodeType().getName();
                log.error( message );
                throw new RulesRepositoryException( message );
            }
        } catch ( Exception e ) {
            log.error( "Caught exception: " + e );
            throw new RulesRepositoryException( e );
        }
    }

    PackageItem() {
        super(null, null);
    }



    /**
     * Return the name of the package.
     */
    public String getName() {
        try {

            if (isSnapshot()) {
                return this.node.getParent().getName();
            } else {
                return super.getName();
            }
        } catch (RepositoryException e) {
            throw new RulesRepositoryException( e );
        }
    }

    /**
     * @return true if this package is actually a snapshot.
     */
    public boolean isSnapshot() {
        try {
            return (!this.rulesRepository.isNotSnapshot( this.node.getParent() ));
        } catch (  RepositoryException e ) {
            throw new IllegalStateException(e);
        }
    }



    /**
     * Set this to indicate if the binary is up to date, or not.
     */
    public void updateBinaryUpToDate(boolean status) {
    	try {
    		checkIsUpdateable();
    		node.checkout();
			node.setProperty("drools:binaryUpToDate", status);
		} catch (RepositoryException e) {
			log.error(e);
		}
    }

    /**
     * Return true if the binary is "up to date".
     * @return
     */
    public boolean isBinaryUpToDate() {
		try {
			if (this.node.hasProperty("drools:binaryUpToDate")) {
				return node.getProperty("drools:binaryUpToDate").getBoolean();
			} else {
				return false;
			}
		} catch (RepositoryException e) {
			log.error(e);
			throw new RulesRepositoryException(e);
		}
    }

    /**
     * returns the name of the snapshot, if this package is really a snapshot.
     * If it is not, it will just return the name of the package, so use wisely !
     */
    public String getSnapshotName() {
            return super.getName();
    }

    /**
     * Adds a rule to the current package with no category (not recommended !).
     * Without categories, its going to be hard to find rules later on
     * (unless packages are enough for you).
     */
    public AssetItem addAsset(String assetName, String description) {
        return addAsset(assetName, description, null, null);
    }


    /**
     * This adds a rule to the current physical package (you can move it later).
     * With the given category.
     *
     * This will NOT check the asset in, just create the basic record.
     * @param assetName The name of the asset (the file name minus the extension)
     * @param description A description of the asset.
     * @param initialCategory The initial category the asset is placed in (can belong to multiple ones later).
     * @param format The dublin core format (which also determines what editor is used) - this is effectively the file extension.
     */
    public AssetItem addAsset(String assetName,
                            String description, String initialCategory, String format) {
        Node ruleNode;
        try {
        	assetName = assetName.trim();
            Node rulesFolder = this.node.getNode( ASSET_FOLDER_NAME );
            ruleNode = rulesFolder.addNode( assetName,
                                            AssetItem.RULE_NODE_TYPE_NAME );
            ruleNode.setProperty( AssetItem.TITLE_PROPERTY_NAME,
                                  assetName );

            ruleNode.setProperty( AssetItem.DESCRIPTION_PROPERTY_NAME,
                                  description );
            if (format != null) {
                ruleNode.setProperty( AssetItem.FORMAT_PROPERTY_NAME,
                                      format );
            } else {
                ruleNode.setProperty( AssetItem.FORMAT_PROPERTY_NAME,
                                      AssetItem.DEFAULT_CONTENT_FORMAT );
            }


            ruleNode.setProperty( VersionableItem.CHECKIN_COMMENT,
                                  "Initial" );

            Calendar lastModified = Calendar.getInstance();

            ruleNode.setProperty( AssetItem.LAST_MODIFIED_PROPERTY_NAME, lastModified );
            ruleNode.setProperty( AssetItem.PACKAGE_NAME_PROPERTY, this.getName() );
            ruleNode.setProperty( CREATOR_PROPERTY_NAME, this.node.getSession().getUserID() );


            AssetItem rule = new AssetItem( this.rulesRepository, ruleNode );

            rule.updateState( StateItem.DRAFT_STATE_NAME );

            if (initialCategory != null) {
                rule.addCategory( initialCategory );
            }

            return rule;

        } catch ( RepositoryException e ) {
            if ( e instanceof ItemExistsException ) {
                throw new RulesRepositoryException( "A rule of that name already exists in that package.",
                                                    e );
            } else {
                throw new RulesRepositoryException( e );
            }
        }

    }

    /**
     * Remove an asset by name
     * After doing this, you will need to check in the package
     * as removing an item effects the parent package.
     */
    public void removeAsset(String name) {
        try {
            this.node.getNode( ASSET_FOLDER_NAME + "/" + name ).remove();
        } catch ( RepositoryException e ) {
            throw new RulesRepositoryException( e );
        }
    }

    /**
     * This will permanently delete this package.
     */
    public void remove() {
    	checkIsUpdateable();
    	try {
			this.node.remove();
		} catch (RepositoryException e) {
			throw new RulesRepositoryException("Was not able to delete package.", e);
		}
    }



    // The following should be kept for reference on how to add a reference that
    //is either locked to a version or follows head - FOR SHARING ASSETS
    //    /**
    //     * Adds a rule to the rule package node this object represents.  The reference to the rule
    //     * will optionally follow the head version of the specified rule's node or the specific
    //     * current version.
    //     *
    //     * @param ruleItem the ruleItem corresponding to the node to add to the rule package this
    //     *                 object represents
    //     * @param followRuleHead if true, the reference to the rule node will follow the head version
    //     *                       of the node, even if new versions are added. If false, will refer
    //     *                       specifically to the current version.
    //     * @throws RulesRepositoryException
    //     */
    //    public void addRuleReference(RuleItem ruleItem, boolean followRuleHead) throws RulesRepositoryException {
    //        try {
    //            ValueFactory factory = this.node.getSession().getValueFactory();
    //            int i = 0;
    //            Value[] newValueArray = null;
    //
    //            try {
    //                Value[] oldValueArray = this.node.getProperty(RULE_REFERENCE_PROPERTY_NAME).getValues();
    //                newValueArray = new Value[oldValueArray.length + 1];
    //
    //                for(i=0; i<oldValueArray.length; i++) {
    //                    newValueArray[i] = oldValueArray[i];
    //                }
    //            }
    //            catch(PathNotFoundException e) {
    //                //the property has not been created yet. do so now
    //                newValueArray = new Value[1];
    //            }
    //            finally {
    //                if(newValueArray != null) { //just here to make the compiler happy
    //                    if(followRuleHead) {
    //                        newValueArray[i] = factory.createValue(ruleItem.getNode());
    //                    }
    //                    else {
    //                        //this is the magic that ties it to a specific version
    //                        newValueArray[i] = factory.createValue(ruleItem.getNode().getBaseVersion());
    //                    }
    //                    this.node.checkout();
    //                    this.node.setProperty(RULE_REFERENCE_PROPERTY_NAME, newValueArray);
    //                    this.node.getSession().save();
    //                    this.node.checkin();
    //                }
    //                else {
    //                    throw new RulesRepositoryException("Unexpected null pointer for newValueArray");
    //                }
    //            }
    //        }
    //        catch(UnsupportedRepositoryOperationException e) {
    //            String message = "";
    //            try {
    //                message = "Error: Caught UnsupportedRepositoryOperationException when attempting to get base version for rule: " + ruleItem.getNode().getName() + ". Are you sure your JCR repository supports versioning? ";
    //                log.error(message + e);
    //            }
    //            catch (RepositoryException e1) {
    //                log.error("Caught exception: " + e1);
    //                throw new RulesRepositoryException(message, e1);
    //            }
    //            log.error("Caught exception: " + e);
    //            throw new RulesRepositoryException(e);
    //        }
    //        catch(Exception e) {
    //            log.error("Caught exception: " + e);
    //            throw new RulesRepositoryException(e);
    //        }
    //    }


//MN: The following should be kept as a reference on how to remove a version tracking reference
//as a compliment to the above method (which is also commented out !).
//    /**
//     * Removes the specified rule from the rule package node this object represents.
//     *
//     * @param ruleItem the ruleItem corresponding to the node to remove from the rule package
//     *                 this object represents
//     * @throws RulesRepositoryException
//     */
//    public void removeRuleReference(AssetItem ruleItem) throws RulesRepositoryException {
//        try {
//            Value[] oldValueArray = this.node.getProperty( RULE_REFERENCE_PROPERTY_NAME ).getValues();
//            Value[] newValueArray = new Value[oldValueArray.length - 1];
//
//            boolean wasThere = false;
//
//            int j = 0;
//            for ( int i = 0; i < oldValueArray.length; i++ ) {
//                Node ruleNode = this.node.getSession().getNodeByUUID( oldValueArray[i].getString() );
//                AssetItem currentRuleItem = new AssetItem( this.rulesRepository,
//                                                         ruleNode );
//                if ( currentRuleItem.equals( ruleItem ) ) {
//                    wasThere = true;
//                } else {
//                    newValueArray[j] = oldValueArray[i];
//                    j++;
//                }
//            }
//
//            if ( !wasThere ) {
//                return;
//            } else {
//                this.node.checkout();
//                this.node.setProperty( RULE_REFERENCE_PROPERTY_NAME,
//                                       newValueArray );
//                this.node.getSession().save();
//                this.node.checkin();
//            }
//        } catch ( PathNotFoundException e ) {
//            //the property has not been created yet.
//            return;
//        } catch ( Exception e ) {
//            log.error( "Caught exception",
//                       e );
//            throw new RulesRepositoryException( e );
//        }
//    }


    //MN: This should be kept as a reference for
    //    /**
    //     * Gets a list of RuleItem objects for each rule node in this rule package
    //     *
    //     * @return the List object holding the RuleItem objects in this rule package
    //     * @throws RulesRepositoryException
    //     */
    //    public List getRules() throws RulesRepositoryException {
    //        try {
    //            Value[] valueArray = this.node.getProperty(RULE_REFERENCE_PROPERTY_NAME).getValues();
    //            List returnList = new ArrayList();
    //
    //            for(int i=0; i<valueArray.length; i++) {
    //                Node ruleNode = this.node.getSession().getNodeByUUID(valueArray[i].getString());
    //                returnList.add(new RuleItem(this.rulesRepository, ruleNode));
    //            }
    //            return returnList;
    //        }
    //        catch(PathNotFoundException e) {
    //            //the property has not been created yet.
    //            return new ArrayList();
    //        }
    //        catch(Exception e) {
    //            log.error("Caught exception: " + e);
    //            throw new RulesRepositoryException(e);
    //        }
    //    }

    /** Return an iterator for the rules in this package */
    public Iterator<AssetItem> getAssets() {
        try {
            Node content = getVersionContentNode();
            return new AssetItemIterator( content.getNode( ASSET_FOLDER_NAME ).getNodes(),
                                                        this.rulesRepository );
        } catch ( RepositoryException e ) {
            throw new RulesRepositoryException( e );
        }

    }

    /**
     * This will query any assets stored under this package.
     * For example, you can pass in <code>"drools:format = 'drl'"</code> to get a list of
     * only a certain type of asset.
     *
     * @param fieldPredicates A predicate string (SQL style).
     * @return A list of matches.
     */
    public AssetItemIterator queryAssets(String fieldPredicates, boolean seekArchived) {
        try {

            String sql = "SELECT * FROM " + AssetItem.RULE_NODE_TYPE_NAME;
            sql += " WHERE jcr:path LIKE '" + getVersionContentNode().getPath() + "/" + ASSET_FOLDER_NAME + "[%]/%'";
            if ( fieldPredicates.length() > 0 ) {
                sql += " and " + fieldPredicates;
            }

            if ( seekArchived == false ) {
                sql += " AND " + AssetItem.CONTENT_PROPERTY_ARCHIVE_FLAG + " = 'false'";
            }

            Query q = node.getSession().getWorkspace().getQueryManager().createQuery( sql, Query.SQL );
            QueryResult res = q.execute();
            return new AssetItemIterator(res.getNodes(), this.rulesRepository);
        } catch ( RepositoryException e ) {
            throw new RulesRepositoryException(e);
        }
    }

    public AssetItemIterator queryAssets(String fieldPredicates){
        return queryAssets( fieldPredicates, false );
    }


    public AssetItemIterator listArchivedAssets () {
        return queryAssets( AssetItem.CONTENT_PROPERTY_ARCHIVE_FLAG + " = 'true'" , true );
    }

    /**
     * This will load an iterator for assets of the given format type.
     */
    public AssetItemIterator listAssetsByFormat(String[] formats) {
        if (formats.length == 1) {
            return queryAssets( "drools:format='" + formats[0] + "'" );
        } else {
            String predicate = " ( ";
            for ( int i = 0; i < formats.length; i++ ) {
                predicate = predicate + "drools:format='" + formats[i] + "'";
                if (!(i == formats.length -1 )) { predicate =  predicate + " OR "; }
            }
            predicate = predicate + " ) ";
            return queryAssets( predicate );
        }
    }

    public AssetItemIterator listAssetsNotOfFormat(String[] formats) {
        if (formats.length == 1) {
            return queryAssets( "not drools:format='" + formats[0] + "'" );
        } else {
            String predicate = "not ( ";
            for ( int i = 0; i < formats.length; i++ ) {
                predicate = predicate + "drools:format='" + formats[i] + "'";
                if (!(i == formats.length -1 )) { predicate =  predicate + " OR "; }
            }
            predicate = predicate + " ) ";
            return queryAssets( predicate );
        }

    }

    /**
     * Load a specific rule asset by name.
     */
    public AssetItem loadAsset(String name) {

        try {
            Node content = getVersionContentNode();
            return new AssetItem(
                        this.rulesRepository,
                        content.getNode( ASSET_FOLDER_NAME ).getNode( name ));
        } catch ( RepositoryException e ) {
             throw new RulesRepositoryException(e);
       }
    }


    /**
     * Returns true if this package item contains an asset of the given name.
     */
    public boolean containsAsset(String name) {
        Node content;
        try {
            content = getVersionContentNode();
            return content.getNode( ASSET_FOLDER_NAME ).hasNode( name );
        }
        catch ( RepositoryException e ) {
            throw new RulesRepositoryException(e);
        }
    }

    /**
     * Nicely formats the information contained by the node that this object encapsulates
     */
    public String toString() {
        try {
            StringBuffer returnString = new StringBuffer();
            returnString.append( "Content of the rule package named " + this.node.getName() + ":" );
            returnString.append( "Description: " + this.getDescription() + "\n" );
            returnString.append( "Format: " + this.getFormat() + "\n" );
            returnString.append( "Last modified: " + this.getLastModified() + "\n" );
            returnString.append( "Title: " + this.getTitle() + "\n" );
            returnString.append( "----\n" );

            return returnString.toString();
        } catch ( Exception e ) {
            log.error( "Caught Exception",
                       e );
            return "";
        }
    }

    public VersionableItem getPrecedingVersion() throws RulesRepositoryException {
        try {
            Node precedingVersionNode = this.getPrecedingVersionNode();
            if ( precedingVersionNode != null ) {
                return new PackageItem( this.rulesRepository,
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
                return new PackageItem( this.rulesRepository,
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
     * This will return a list of assets for a given state.
     * It works through the assets that belong to this package, and
     * if they are not in the correct state, walks backwards until it finds one
     * in the correct state.
     *
     * If it walks all the way back up the versions looking for the "latest"
     * version with the appropriate state, and can't find one,
     * that asset is not included in the result.
     *
     * This will exclude any items that have the "ignoreState" set
     * (so for example, retired items, invalid items etc).
     *
     *  @param state The state of assets to retrieve.
     *  @param ignoreState The statuses to not include in the results (it will look
     *  at the status of the latest one).
     */
    public Iterator getAssetsWithStatus(final StateItem state, final StateItem ignoreState) {
        final Iterator rules = getAssets();

        List result = new ArrayList();
        while(rules.hasNext()) {
            AssetItem head = (AssetItem) rules.next();
            if (head.sameState( state )) {
                result.add( head );
            } else if (head.sameState( ignoreState )) {
                //ignore this one
            }
            else {
                List fullHistory = new ArrayList();
                for ( Iterator iter = head.getHistory(); iter.hasNext(); ) {
                    AssetItem element = (AssetItem) iter.next();
                    if (!(element.getVersionNumber() == 0)) {
                        fullHistory.add( element );
                    }
                }

                sortHistoryByVersionNumber( fullHistory );


                Iterator prev = fullHistory.iterator();
                while (prev.hasNext()) {
                    AssetItem prevRule = (AssetItem) prev.next();
                    if (prevRule.sameState( state )) {
                        result.add( prevRule );
                        break;
                    }
                }
            }
        }
        return result.iterator();
    }


    void sortHistoryByVersionNumber(List fullHistory) {
        Collections.sort( fullHistory, new Comparator() {

            public int compare(Object o1,
                               Object o2) {
                AssetItem a1 = (AssetItem) o1;
                AssetItem a2 = (AssetItem) o2;
                long la1 = a1.getVersionNumber();
                long la2 = a2.getVersionNumber();
                if (la1 == la2) return 0;
                else if (la1 < la2) return 1;
                else return -1;

            }

        });
    }

    /**
     * This will return a list of assets for a given state.
     * It works through the assets that belong to this package, and
     * if they are not in the correct state, walks backwards until it finds one
     * in the correct state.
     *
     * If it walks all the way back up the versions looking for the "latest"
     * version with the appropriate state, and can't find one,
     * that asset is not included in the result.
     */
    public Iterator getAssetsWithStatus(final StateItem state) {
        return getAssetsWithStatus( state, null );
    }

    /**
     * @return The header contents as pertains to a package of rule assets.
     */
//    public String getHeader() {
//        return this.getStringProperty( HEADER_PROPERTY_NAME );
//    }

    /**
     * @return The external URI which will be used to sync this package to an external resource.
     * Generally this will resolve to a directory in (for example) Subversion - with each asset
     * being a file (with the format property as the file extension).
     */
    public String getExternalURI() {
        return this.getStringProperty( EXTERNAL_URI_PROPERTY_NAME );
    }

//    public void updateHeader(String header) {
//        updateStringProperty( header, HEADER_PROPERTY_NAME );
//    }

    public void updateExternalURI(String uri) {
        updateStringProperty( uri, EXTERNAL_URI_PROPERTY_NAME );
    }

    /**
     * Update the checkin comment.
     */
    public void updateCheckinComment(String comment) {
        updateStringProperty(comment, VersionableItem.CHECKIN_COMMENT);
    }

    /**
     * This will change the status of this package, and all the contained assets.
     * No new versions are created of anything.
     * @param newState The status tag to change it to.
     */
    public void changeStatus(String newState) {
        StateItem stateItem = rulesRepository.getState( newState );
        updateState( stateItem );
        for ( Iterator iter = getAssets(); iter.hasNext(); ) {
            AssetItem element = (AssetItem) iter.next();
            element.updateState( stateItem );
        }
    }

    /**
     * If the asset is a binary asset, then use this to update the content
     * (do NOT use text).
     */
    public PackageItem updateCompiledPackage(InputStream data) {
        checkout();
        try {
            this.node.setProperty( COMPILED_PACKAGE_PROPERTY_NAME, data );
            this.node.setProperty( LAST_MODIFIED_PROPERTY_NAME,
                                   Calendar.getInstance() );
            return this;
        } catch (RepositoryException e ) {
            log.error( "Unable to update the assets binary content", e );
            throw new RulesRepositoryException( e );
        }
    }

    /**
     * This is a convenience method for returning the binary data as a byte array.
     */
    public byte[] getCompiledPackageBytes() {

        try {
            Node ruleNode = getVersionContentNode();
            if ( ruleNode.hasProperty(  COMPILED_PACKAGE_PROPERTY_NAME ) ) {
                Property data = ruleNode.getProperty( COMPILED_PACKAGE_PROPERTY_NAME );
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
                    throw new RulesRepositoryException("Could not completely read binary package for "+ getName());
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
     * Creates a nested package.
     */
	public PackageItem createPackage(String subPackageName) throws RepositoryException {

		node.checkout();
        Node rulePackageNode = node.addNode( subPackageName, PackageItem.RULE_PACKAGE_TYPE_NAME );

        rulePackageNode.addNode( PackageItem.ASSET_FOLDER_NAME, "drools:versionableAssetFolder" );

        rulePackageNode.setProperty( PackageItem.TITLE_PROPERTY_NAME, subPackageName );

        rulePackageNode.setProperty( AssetItem.DESCRIPTION_PROPERTY_NAME, "" );
        rulePackageNode.setProperty( AssetItem.FORMAT_PROPERTY_NAME, PackageItem.PACKAGE_FORMAT );
        rulePackageNode.setProperty( PackageItem.CREATOR_PROPERTY_NAME, this.rulesRepository.getSession().getUserID() );
        Calendar lastModified = Calendar.getInstance();
        rulePackageNode.setProperty( PackageItem.LAST_MODIFIED_PROPERTY_NAME, lastModified );


		return new PackageItem(this.rulesRepository, rulePackageNode);
	}




}