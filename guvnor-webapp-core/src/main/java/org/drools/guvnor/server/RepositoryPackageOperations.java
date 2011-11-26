/*
 * Copyright 2011 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.drools.guvnor.server;

import com.google.gwt.user.client.rpc.SerializationException;
import org.drools.RuleBase;
import org.drools.RuleBaseConfiguration;
import org.drools.RuleBaseFactory;
import org.drools.common.DroolsObjectOutputStream;
import org.drools.compiler.DroolsParserException;
import org.drools.guvnor.client.rpc.*;
import org.drools.guvnor.server.builder.PackageAssembler;
import org.drools.guvnor.server.builder.PackageAssemblerConfiguration;
import org.drools.guvnor.server.builder.PackageDRLAssembler;
import org.drools.guvnor.server.builder.pagerow.SnapshotComparisonPageRowBuilder;
import org.drools.guvnor.server.cache.RuleBaseCache;
import org.drools.guvnor.server.security.RoleType;
import org.drools.guvnor.server.util.*;
import org.drools.repository.*;
import org.jboss.seam.security.Identity;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;

import javax.jcr.PathNotFoundException;
import javax.jcr.RepositoryException;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutput;
import java.util.*;

import static org.drools.guvnor.server.util.ClassicDRLImporter.getRuleName;

/**
 * Handles operations for packages
 */
@ApplicationScoped
public class RepositoryPackageOperations {

    private static final LoggingHelper log = LoggingHelper.getLogger( RepositoryPackageOperations.class );

    /**
     * Maximum number of rules to display in "list rules in package" method
     */
    private static final int MAX_RULES_TO_SHOW_IN_PACKAGE_LIST = 5000;

    @Inject
    private RulesRepository rulesRepository;

    @Inject
    private Identity identity;

    @Deprecated
    public void setRulesRepositoryForTest(RulesRepository repository) {
        // TODO use GuvnorTestBase with a real RepositoryAssetOperations instead
        this.rulesRepository = repository;
    }

    protected PackageConfigData[] listPackages(boolean archive,
                                               String workspace,
                                               RepositoryFilter filter) {
        List<PackageConfigData> result = new ArrayList<PackageConfigData>();
        PackageIterator pkgs = rulesRepository.listPackages();
        handleIteratePackages( archive,
                workspace,
                filter,
                result,
                pkgs );

        sortPackages( result );
        return result.toArray( new PackageConfigData[result.size()] );
    }

    private void handleIteratePackages(boolean archive,
                                       String workspace,
                                       RepositoryFilter filter,
                                       List<PackageConfigData> result,
                                       PackageIterator pkgs) {
        pkgs.setArchivedIterator( archive );
        while (pkgs.hasNext()) {
            PackageItem packageItem = pkgs.next();

            PackageConfigData data = new PackageConfigData();
            data.setUuid( packageItem.getUUID() );
            data.setName( packageItem.getName() );
            data.setArchived( packageItem.isArchived() );
            data.setWorkspaces( packageItem.getWorkspaces() );
            handleIsPackagesListed( archive,
                    workspace,
                    filter,
                    result,
                    data );

            data.subPackages = listSubPackages( packageItem,
                    archive,
                    null,
                    filter );

        }
    }

    private PackageConfigData[] listSubPackages(PackageItem parentPkg,
                                                boolean archive,
                                                String workspace,
                                                RepositoryFilter filter) {
        List<PackageConfigData> children = new LinkedList<PackageConfigData>();

        handleIteratePackages( archive,
                workspace,
                filter,
                children,
                parentPkg.listSubPackages() );

        sortPackages( children );
        return children.toArray( new PackageConfigData[children.size()] );
    }

    void sortPackages(List<PackageConfigData> result) {
        Collections.sort( result,
                new Comparator<PackageConfigData>() {

                    public int compare(final PackageConfigData d1,
                                       final PackageConfigData d2) {
                        return d1.getName().compareTo( d2.getName() );
                    }

                } );
    }

    private void handleIsPackagesListed(boolean archive,
                                        String workspace,
                                        RepositoryFilter filter,
                                        List<PackageConfigData> result,
                                        PackageConfigData data) {
        if ( !archive && (filter == null || filter.accept( data,
                RoleType.PACKAGE_READONLY.getName() )) && (workspace == null || isWorkspace( workspace,
                data.getWorkspaces() )) ) {
            result.add( data );
        } else if ( archive && data.isArchived() && (filter == null || filter.accept( data,
                RoleType.PACKAGE_READONLY.getName() )) && (workspace == null || isWorkspace( workspace,
                data.getWorkspaces() )) ) {
            result.add( data );
        }
    }

    private boolean isWorkspace(String workspace,
                                String[] workspaces) {

        for (String w : workspaces) {
            if ( w.equals( workspace ) ) {
                return true;
            }
        }
        return false;
    }

    protected PackageConfigData loadGlobalPackage() {
        PackageItem item = rulesRepository.loadGlobalArea();

        PackageConfigData data = PackageConfigDataFactory.createPackageConfigDataWithOutDependencies( item );

        if ( data.isSnapshot() ) {
            data.setSnapshotName( item.getSnapshotName() );
        }

        return data;
    }

    protected String copyPackage(String sourcePackageName,
                                 String destPackageName) throws SerializationException {

        try {
            log.info( "USER:" + getCurrentUserName() + " COPYING package [" + sourcePackageName + "] to  package [" + destPackageName + "]" );

            return rulesRepository.copyPackage( sourcePackageName,
                    destPackageName );
        } catch (RulesRepositoryException e) {
            log.error( "Unable to copy package.",
                    e );
            throw e;
        }
    }

    protected void removePackage(String uuid) {

        try {
            PackageItem item = rulesRepository.loadPackageByUUID( uuid );
            log.info( "USER:" + getCurrentUserName() + " REMOVEING package [" + item.getName() + "]" );
            item.remove();
            rulesRepository.save();
        } catch (RulesRepositoryException e) {
            log.error( "Unable to remove package.",
                    e );
            throw e;
        }
    }

    protected String renamePackage(String uuid,
                                   String newName) {
        log.info( "USER:" + getCurrentUserName() + " RENAMING package [UUID: " + uuid + "] to package [" + newName + "]" );

        return rulesRepository.renamePackage( uuid,
                newName );
    }

    protected byte[] exportPackages(String packageName) {
        log.info( "USER:" + getCurrentUserName() + " export package [name: " + packageName + "] " );

        try {
            return rulesRepository.dumpPackageFromRepositoryXml( packageName );
        } catch (PathNotFoundException e) {
            throw new RulesRepositoryException( e );
        } catch (IOException e) {
            throw new RulesRepositoryException( e );
        } catch (RepositoryException e) {
            throw new RulesRepositoryException( e );
        }
    }

    // TODO: Not working. GUVNOR-475
    protected void importPackages(byte[] byteArray,
                                  boolean importAsNew) {
        rulesRepository.importPackageToRepository( byteArray,
                importAsNew );
    }

    protected String createPackage(String name, String description,
            String format) throws RulesRepositoryException {

        log.info("USER: " + getCurrentUserName() + " CREATING package [" + name
                + "]");
        PackageItem item = rulesRepository.createPackage(name,
                description, format);

        return item.getUUID();
    }
    
    protected String createPackage(String name,
                                   String description,
                                   String format,
                                   String[] workspace) throws RulesRepositoryException {

        log.info( "USER: " + getCurrentUserName() + " CREATING package [" + name + "]" );
        PackageItem item = rulesRepository.createPackage( name,
                description,
                format,
                workspace );

        return item.getUUID();
    }
    
    protected String createSubPackage(String name,
                                      String description,
                                      String parentNode) throws SerializationException {
        log.info( "USER: " + getCurrentUserName() + " CREATING subPackage [" + name + "], parent [" + parentNode + "]" );
        PackageItem item = rulesRepository.createSubPackage( name,
                description,
                parentNode );
        return item.getUUID();
    }

    protected PackageConfigData loadPackageConfig(PackageItem packageItem) {
        PackageConfigData data = PackageConfigDataFactory.createPackageConfigDataWithDependencies( packageItem );
        if ( data.isSnapshot() ) {
            data.setSnapshotName( packageItem.getSnapshotName() );
        }
        return data;
    }

    public ValidatedResponse validatePackageConfiguration(PackageConfigData data) throws SerializationException {
        log.info( "USER:" + getCurrentUserName() + " validatePackageConfiguration package [" + data.getName() + "]" );

        RuleBaseCache.getInstance().remove( data.getUuid() );
        BRMSSuggestionCompletionLoader loader = createBRMSSuggestionCompletionLoader();
        loader.getSuggestionEngine( rulesRepository.loadPackage( data.getName() ),
                data.getHeader() );

        return validateBRMSSuggestionCompletionLoaderResponse( loader );
    }

    public void savePackage(PackageConfigData data) throws SerializationException {
        log.info( "USER:" + getCurrentUserName() + " SAVING package [" + data.getName() + "]" );

        PackageItem item = rulesRepository.loadPackage( data.getName() );

        // If package is being unarchived.
        boolean unarchived = (!data.isArchived() && item.isArchived());
        Calendar packageLastModified = item.getLastModified();

        DroolsHeader.updateDroolsHeader( data.getHeader(),
                item );
        updateCategoryRules( data,
                item );

        item.updateExternalURI( data.getExternalURI() );
        item.updateDescription( data.getDescription() );
        item.archiveItem( data.isArchived() );
        item.updateBinaryUpToDate( false );
        if(!data.getFormat().equals("")) {
            item.updateFormat(data.getFormat());
        }
        RuleBaseCache.getInstance().remove( data.getUuid() );
        item.checkin( data.getDescription() );

        // If package is archived, archive all the assets under it
        if ( data.isArchived() ) {
            handleArchivedForSavePackage( data,
                    item );
        } else if ( unarchived ) {
            handleUnarchivedForSavePackage( data,
                    item,
                    packageLastModified );
        }
    }

    BRMSSuggestionCompletionLoader createBRMSSuggestionCompletionLoader() {
        return new BRMSSuggestionCompletionLoader();
    }

    void updateCategoryRules(PackageConfigData data,
                             PackageItem item) {
        KeyValueTO keyValueTO = convertMapToCsv( data.getCatRules() );
        item.updateCategoryRules( keyValueTO.getKeys(),
                keyValueTO.getValues() );
    }

    // HashMap DOES NOT guarantee order in different iterations!
    private static KeyValueTO convertMapToCsv(final Map map) {
        StringBuilder keysBuilder = new StringBuilder();
        StringBuilder valuesBuilder = new StringBuilder();
        for (Object o : map.entrySet()) {
            Map.Entry entry = (Map.Entry) o;
            if ( keysBuilder.length() > 0 ) {
                keysBuilder.append( "," );
            }

            if ( valuesBuilder.length() > 0 ) {
                valuesBuilder.append( "," );
            }

            keysBuilder.append( entry.getKey() );
            valuesBuilder.append( entry.getValue() );
        }
        return new KeyValueTO( keysBuilder.toString(),
                valuesBuilder.toString() );
    }

    private static class KeyValueTO {
        private final String keys;
        private final String values;

        public KeyValueTO(final String keys,
                          final String values) {
            this.keys = keys;
            this.values = values;
        }

        public String getKeys() {
            return keys;
        }

        public String getValues() {
            return values;
        }
    }

    void handleArchivedForSavePackage(PackageConfigData data,
                                      PackageItem item) {
        for (Iterator<AssetItem> iter = item.getAssets(); iter.hasNext(); ) {
            AssetItem assetItem = iter.next();
            if ( !assetItem.isArchived() ) {
                assetItem.archiveItem( true );
                assetItem.checkin( data.getDescription() );
            }
        }
    }

    void handleUnarchivedForSavePackage(PackageConfigData data,
                                        PackageItem item,
                                        Calendar packageLastModified) {
        for (Iterator<AssetItem> iter = item.getAssets(); iter.hasNext(); ) {
            AssetItem assetItem = iter.next();
            // Unarchive the assets archived after the package
            // ( == at the same time that the package was archived)
            if ( assetItem.getLastModified().compareTo( packageLastModified ) >= 0 ) {
                assetItem.archiveItem( false );
                assetItem.checkin( data.getDescription() );
            }
        }
    }

    private ValidatedResponse validateBRMSSuggestionCompletionLoaderResponse(BRMSSuggestionCompletionLoader loader) {
        ValidatedResponse res = new ValidatedResponse();
        if ( loader.hasErrors() ) {
            res.hasErrors = true;
            String err = "";
            for (Iterator iter = loader.getErrors().iterator(); iter.hasNext(); ) {
                err += (String) iter.next();
                if ( iter.hasNext() ) err += "\n";
            }
            res.errorHeader = "Package validation errors";
            res.errorMessage = err;
        }
        return res;
    }

    protected void createPackageSnapshot(String packageName,
                                         String snapshotName,
                                         boolean replaceExisting,
                                         String comment) {

        log.info( "USER:" + getCurrentUserName() + " CREATING PACKAGE SNAPSHOT for package: [" + packageName + "] snapshot name: [" + snapshotName );

        if ( replaceExisting ) {
            rulesRepository.removePackageSnapshot( packageName,
                    snapshotName );
        }

        rulesRepository.createPackageSnapshot( packageName,
                snapshotName );
        PackageItem item = rulesRepository.loadPackageSnapshot( packageName,
                snapshotName );
        item.updateCheckinComment( comment );
        rulesRepository.save();

    }

    protected void copyOrRemoveSnapshot(String packageName,
                                        String snapshotName,
                                        boolean delete,
                                        String newSnapshotName) throws SerializationException {

        if ( delete ) {
            log.info( "USER:" + getCurrentUserName() + " REMOVING SNAPSHOT for package: [" + packageName + "] snapshot: [" + snapshotName + "]" );
            rulesRepository.removePackageSnapshot( packageName,
                    snapshotName );
        } else {
            if ( newSnapshotName.equals( "" ) ) {
                throw new SerializationException( "Need to have a new snapshot name." );
            }
            log.info( "USER:" + getCurrentUserName() + " COPYING SNAPSHOT for package: [" + packageName + "] snapshot: [" + snapshotName + "] to [" + newSnapshotName + "]" );

            rulesRepository.copyPackageSnapshot( packageName,
                    snapshotName,
                    newSnapshotName );
        }

    }

    public BuilderResult buildPackage(String packageUUID,
                                      boolean force,
                                      String buildMode,
                                      String statusOperator,
                                      String statusDescriptionValue,
                                      boolean enableStatusSelector,
                                      String categoryOperator,
                                      String category,
                                      boolean enableCategorySelector,
                                      String customSelectorName) throws SerializationException {

        PackageItem item = rulesRepository.loadPackageByUUID( packageUUID );
        try {
            return buildPackage( item,
                    force,
                    createConfiguration( buildMode,
                            statusOperator,
                            statusDescriptionValue,
                            enableStatusSelector,
                            categoryOperator,
                            category,
                            enableCategorySelector,
                            customSelectorName ) );
        } catch (NoClassDefFoundError e) {
            throw new DetailedSerializationException( "Unable to find a class that was needed when building the package  [" + e.getMessage() + "]",
                    "Perhaps you are missing them from the model jars, or from the BRMS itself (lib directory)." );
        } catch (UnsupportedClassVersionError e) {
            throw new DetailedSerializationException( "Can not build the package. One or more of the classes that are needed were compiled with an unsupported Java version.",
                    "For example the pojo classes were compiled with Java 1.6 and Guvnor is running on Java 1.5. [" + e.getMessage() + "]" );
        }
    }

    private BuilderResult buildPackage(PackageItem item,
                                       boolean force,
                                       PackageAssemblerConfiguration packageAssemblerConfiguration) throws DetailedSerializationException {
        if ( !force && item.isBinaryUpToDate() ) {
            // we can just return all OK if its up to date.
            return BuilderResult.emptyResult();
        }
        PackageAssembler packageAssembler = new PackageAssembler( item,
                packageAssemblerConfiguration );

        packageAssembler.compile();

        if ( packageAssembler.hasErrors() ) {
            BuilderResult result = new BuilderResult();
            BuilderResultHelper builderResultHelper = new BuilderResultHelper();
            result.addLines( builderResultHelper.generateBuilderResults( packageAssembler.getErrors() ) );
            return result;
        }

        updatePackageBinaries( item, packageAssembler );

        return BuilderResult.emptyResult();
    }

    private void updatePackageBinaries(PackageItem item, PackageAssembler packageAssembler) throws DetailedSerializationException {
        try {
            ByteArrayOutputStream bout = new ByteArrayOutputStream();
            ObjectOutput out = new DroolsObjectOutputStream( bout );
            out.writeObject( packageAssembler.getBinaryPackage() );

            item.updateCompiledPackage( new ByteArrayInputStream( bout.toByteArray() ) );
            out.flush();
            out.close();

            item.updateBinaryUpToDate( true );

            RuleBase ruleBase = RuleBaseFactory.newRuleBase(
                    new RuleBaseConfiguration( getClassLoaders( packageAssembler ) )
            );
            ruleBase.addPackage( packageAssembler.getBinaryPackage() );

            rulesRepository.save();
        } catch (Exception e) {
            e.printStackTrace();
            log.error( "An error occurred building the package [" + item.getName() + "]: " + e.getMessage() );
            throw new DetailedSerializationException( "An error occurred building the package.",
                    e.getMessage() );
        }
    }

    private PackageAssemblerConfiguration createConfiguration(String buildMode, String statusOperator, String statusDescriptionValue, boolean enableStatusSelector, String categoryOperator, String category, boolean enableCategorySelector, String selectorConfigName) {
        PackageAssemblerConfiguration packageAssemblerConfiguration = new PackageAssemblerConfiguration();
        packageAssemblerConfiguration.setBuildMode( buildMode );
        packageAssemblerConfiguration.setStatusOperator( statusOperator );
        packageAssemblerConfiguration.setStatusDescriptionValue( statusDescriptionValue );
        packageAssemblerConfiguration.setEnableStatusSelector( enableStatusSelector );
        packageAssemblerConfiguration.setCategoryOperator( categoryOperator );
        packageAssemblerConfiguration.setCategoryValue( category );
        packageAssemblerConfiguration.setEnableCategorySelector( enableCategorySelector );
        packageAssemblerConfiguration.setCustomSelectorConfigName( selectorConfigName );
        return packageAssemblerConfiguration;
    }

    private ClassLoader[] getClassLoaders(PackageAssembler packageAssembler) {
        Collection<ClassLoader> loaders = packageAssembler.getBuilder().getRootClassLoader().getClassLoaders();
        return loaders.toArray( new ClassLoader[loaders.size()] );
    }

    private String getCurrentUserName() {
        return rulesRepository.getSession().getUserID();
    }

    protected BuilderResult buildPackage(PackageItem item,
                                         boolean force) throws DetailedSerializationException {
        return buildPackage( item,
                force,
                createConfiguration(
                        null,
                        null,
                        null,
                        false,
                        null,
                        null,
                        false,
                        null ) );
    }

    protected String buildPackageSource(String packageUUID) throws SerializationException {

        PackageItem item = rulesRepository.loadPackageByUUID( packageUUID );
        PackageDRLAssembler asm = new PackageDRLAssembler( item );
        return asm.getDRL();
    }

    protected String[] listRulesInPackage(String packageName) throws SerializationException {
        // load package
        PackageItem item = rulesRepository.loadPackage( packageName );

        PackageDRLAssembler assembler = createPackageDRLAssembler( item );

        List<String> result = new ArrayList<String>();
        try {

            String drl = assembler.getDRL();
            if ( drl == null || "".equals( drl ) ) {
                return new String[0];
            } else {
                parseRulesToPackageList( assembler,
                        result );
            }

            return result.toArray( new String[result.size()] );
        } catch (DroolsParserException e) {
            log.error( "Unable to list rules in package",
                    e );
            return new String[0];
        }
    }

    protected String[] listImagesInPackage(String packageName) throws SerializationException {
        // load package
        PackageItem item = rulesRepository.loadPackage( packageName );
        List<String> retList = new ArrayList<String>();
        Iterator<AssetItem> iter = item.getAssets();
        while (iter.hasNext()) {
            AssetItem pitem = iter.next();
            if ( pitem.getFormat().equalsIgnoreCase( "png" ) || pitem.getFormat().equalsIgnoreCase( "gif" ) || pitem.getFormat().equalsIgnoreCase( "jpg" ) ) {
                retList.add( pitem.getName() );
            }
        }
        return retList.toArray( new String[]{} );
    }

    PackageDRLAssembler createPackageDRLAssembler(final PackageItem packageItem) {
        return new PackageDRLAssembler( packageItem );
    }

    void parseRulesToPackageList(PackageDRLAssembler asm,
                                 List<String> result) throws DroolsParserException {
        int count = 0;
        StringTokenizer stringTokenizer = new StringTokenizer( asm.getDRL(),
                "\n\r" );
        while (stringTokenizer.hasMoreTokens()) {
            String line = stringTokenizer.nextToken().trim();
            if ( line.startsWith( "rule " ) ) {
                String name = getRuleName( line );
                result.add( name );
                count++;
                if ( count == MAX_RULES_TO_SHOW_IN_PACKAGE_LIST ) {
                    result.add( "More then " + MAX_RULES_TO_SHOW_IN_PACKAGE_LIST + " rules." );
                    break;
                }
            }
        }
    }

    /**
     * @deprecated in favour of {@link compareSnapshots(SnapshotComparisonPageRequest)}
     */
    protected SnapshotDiffs compareSnapshots(String packageName,
                                             String firstSnapshotName,
                                             String secondSnapshotName) {
        SnapshotDiffs diffs = new SnapshotDiffs();
        List<SnapshotDiff> list = new ArrayList<SnapshotDiff>();

        PackageItem leftPackage = rulesRepository.loadPackageSnapshot( packageName,
                firstSnapshotName );
        PackageItem rightPackage = rulesRepository.loadPackageSnapshot( packageName,
                secondSnapshotName );

        // Older one has to be on the left.
        if ( isRightOlderThanLeft( leftPackage,
                rightPackage ) ) {
            PackageItem temp = leftPackage;
            leftPackage = rightPackage;
            rightPackage = temp;

            diffs.leftName = secondSnapshotName;
            diffs.rightName = firstSnapshotName;
        } else {
            diffs.leftName = firstSnapshotName;
            diffs.rightName = secondSnapshotName;
        }

        Iterator<AssetItem> leftExistingIter = leftPackage.getAssets();
        while (leftExistingIter.hasNext()) {
            AssetItem left = leftExistingIter.next();
            if ( isPackageItemDeleted( rightPackage,
                    left ) ) {
                SnapshotDiff diff = new SnapshotDiff();

                diff.name = left.getName();
                diff.diffType = SnapshotDiff.TYPE_DELETED;
                diff.leftUuid = left.getUUID();

                list.add( diff );
            }
        }

        Iterator<AssetItem> rightExistingIter = rightPackage.getAssets();
        while (rightExistingIter.hasNext()) {
            AssetItem right = rightExistingIter.next();
            AssetItem left = null;
            if ( right != null && leftPackage.containsAsset( right.getName() ) ) {
                left = leftPackage.loadAsset( right.getName() );
            }

            // Asset is deleted or added
            if ( right == null || left == null ) {
                SnapshotDiff diff = new SnapshotDiff();

                if ( left == null ) {
                    diff.name = right.getName();
                    diff.diffType = SnapshotDiff.TYPE_ADDED;
                    diff.rightUuid = right.getUUID();
                }

                list.add( diff );
            } else if ( isAssetArchivedOrRestored( right,
                    left ) ) { // Has the asset
                // been archived
                // or restored
                SnapshotDiff diff = new SnapshotDiff();

                diff.name = right.getName();
                diff.leftUuid = left.getUUID();
                diff.rightUuid = right.getUUID();

                if ( left.isArchived() ) {
                    diff.diffType = SnapshotDiff.TYPE_RESTORED;
                } else {
                    diff.diffType = SnapshotDiff.TYPE_ARCHIVED;
                }

                list.add( diff );
            } else if ( isAssetItemUpdated( right,
                    left ) ) { // Has the asset been
                // updated
                SnapshotDiff diff = new SnapshotDiff();

                diff.name = right.getName();
                diff.leftUuid = left.getUUID();
                diff.rightUuid = right.getUUID();
                diff.diffType = SnapshotDiff.TYPE_UPDATED;

                list.add( diff );
            }
        }

        diffs.diffs = list.toArray( new SnapshotDiff[list.size()] );
        return diffs;
    }

    private boolean isAssetArchivedOrRestored(AssetItem right,
                                              AssetItem left) {
        return right.isArchived() != left.isArchived();
    }

    private boolean isAssetItemUpdated(AssetItem right,
                                       AssetItem left) {
        return right.getLastModified().compareTo( left.getLastModified() ) != 0;
    }

    private boolean isPackageItemDeleted(PackageItem rightPackage,
                                         AssetItem left) {
        return !rightPackage.containsAsset( left.getName() );
    }

    private boolean isRightOlderThanLeft(PackageItem leftPackage,
                                         PackageItem rightPackage) {
        return leftPackage.getLastModified().compareTo( rightPackage.getLastModified() ) > 0;
    }

    protected SnapshotComparisonPageResponse compareSnapshots(SnapshotComparisonPageRequest request) {

        SnapshotComparisonPageResponse response = new SnapshotComparisonPageResponse();

        // Do query (bit of a cheat really!)
        long start = System.currentTimeMillis();
        SnapshotDiffs diffs = compareSnapshots( request.getPackageName(),
                request.getFirstSnapshotName(),
                request.getSecondSnapshotName() );
        log.debug( "Search time: " + (System.currentTimeMillis() - start) );

        // Populate response
        response.setLeftSnapshotName( diffs.leftName );
        response.setRightSnapshotName( diffs.rightName );

        List<SnapshotComparisonPageRow> rowList = new SnapshotComparisonPageRowBuilder()
                .withPageRequest( request )
                .withIdentity(identity)
                .withContent( diffs )
                .build();

        response.setPageRowList( rowList );
        response.setStartRowIndex( request.getStartRowIndex() );
        response.setTotalRowSize( diffs.diffs.length );
        response.setTotalRowSizeExact( true );
        response.setLastPage( (request.getStartRowIndex() + rowList.size() == diffs.diffs.length) );

        long methodDuration = System.currentTimeMillis() - start;
        log.debug( "Compared Snapshots ('" + request.getFirstSnapshotName() + "') and ('" + request.getSecondSnapshotName() + "') in package ('" + request.getPackageName() + "') in " + methodDuration + " ms." );

        return response;
    }

}
