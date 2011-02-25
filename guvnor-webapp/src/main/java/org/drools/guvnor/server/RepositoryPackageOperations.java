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

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.jcr.PathNotFoundException;
import javax.jcr.RepositoryException;

import org.drools.guvnor.client.rpc.PackageConfigData;
import org.drools.guvnor.client.rpc.ValidatedResponse;
import org.drools.guvnor.server.security.PackageUUIDType;
import org.drools.guvnor.server.security.RoleTypes;
import org.drools.guvnor.server.util.BRMSSuggestionCompletionLoader;
import org.drools.guvnor.server.util.DroolsHeader;
import org.drools.guvnor.server.util.LoggingHelper;
import org.drools.guvnor.server.util.PackageConfigDataFactory;
import org.drools.repository.AssetItem;
import org.drools.repository.PackageItem;
import org.drools.repository.PackageIterator;
import org.drools.repository.RepositoryFilter;
import org.drools.repository.RulesRepository;
import org.drools.repository.RulesRepositoryException;
import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.contexts.Contexts;
import org.jboss.seam.security.Identity;

import com.google.gwt.user.client.rpc.SerializationException;

/**
 * Handles operations for packages
 */
@Name("org.drools.guvnor.server.RepositoryPackageOperations")
@AutoCreate
public class RepositoryPackageOperations {
    private RulesRepository            repository;

    private static final LoggingHelper log = LoggingHelper
                                                   .getLogger( RepositoryPackageOperations.class );

    public void setRulesRepository(RulesRepository repository) {
        this.repository = repository;
    }

    public RulesRepository getRulesRepository() {
        return repository;
    }

    protected PackageConfigData[] listPackages(boolean archive,
                                               String workspace,
                                               RepositoryFilter filter) {
        List<PackageConfigData> result = new ArrayList<PackageConfigData>();
        PackageIterator pkgs = getRulesRepository().listPackages();
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
        while ( pkgs.hasNext() ) {
            PackageItem pkg = pkgs.next();

            PackageConfigData data = new PackageConfigData();
            data.uuid = pkg.getUUID();
            data.name = pkg.getName();
            data.archived = pkg.isArchived();
            data.workspaces = pkg.getWorkspaces();
            handleIsPackagesListed( archive,
                                    workspace,
                                    filter,
                                    result,
                                    data );

            data.subPackages = listSubPackages( pkg,
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

        PackageIterator pkgs = parentPkg.listSubPackages();
        handleIteratePackages( archive,
                               workspace,
                               filter,
                               children,
                               pkgs );

        sortPackages( children );
        return children.toArray( new PackageConfigData[children.size()] );
    }

    void sortPackages(List<PackageConfigData> result) {
        Collections.sort( result,
                          new Comparator<PackageConfigData>() {

                              public int compare(final PackageConfigData d1,
                                                 final PackageConfigData d2) {
                                  return d1.name.compareTo( d2.name );
                              }

                          } );
    }

    private void handleIsPackagesListed(boolean archive,
                                        String workspace,
                                        RepositoryFilter filter,
                                        List<PackageConfigData> result,
                                        PackageConfigData data) {
        if ( !archive && (filter == null || filter.accept( data,
                                                           RoleTypes.PACKAGE_READONLY )) && (workspace == null || isWorkspace( workspace,
                                                                                                                               data.workspaces )) ) {
            result.add( data );
        } else if ( archive && data.archived && (filter == null || filter.accept( data,
                                                                                  RoleTypes.PACKAGE_READONLY )) && (workspace == null || isWorkspace( workspace,
                                                                                                                                                      data.workspaces )) ) {
            result.add( data );
        }
    }

    private boolean isWorkspace(String workspace,
                                String[] workspaces) {
        for ( String w : workspaces ) {
            if ( w.equals( workspace ) ) {
                return true;
            }
        }
        return false;
    }

    protected PackageConfigData loadGlobalPackage() {
        PackageItem item = getRulesRepository().loadGlobalArea();

        PackageConfigData data = PackageConfigDataFactory.createPackageConfigDataWithOutDependencies( item );

        if ( data.isSnapshot ) {
            data.snapshotName = item.getSnapshotName();
        }

        return data;
    }

    protected void copyPackage(String sourcePackageName,
                               String destPackageName) throws SerializationException {

        try {
            log.info( "USER:" + getCurrentUserName() + " COPYING package [" + sourcePackageName + "] to  package [" + destPackageName + "]" );

            getRulesRepository().copyPackage( sourcePackageName,
                                              destPackageName );
        } catch ( RulesRepositoryException e ) {
            log.error( "Unable to copy package.",
                       e );
            throw e;
        }

        // If we allow package owner to copy package, we will have to update the
        // permission store
        // for the newly copied package.
        // Update permission store
        /*
         * String copiedUuid = ""; try { PackageItem source =
         * repository.loadPackage( destPackageName ); copiedUuid =
         * source.getUUID(); } catch (RulesRepositoryException e) { log.error( e
         * ); } PackageBasedPermissionStore pbps = new
         * PackageBasedPermissionStore(); pbps.addPackageBasedPermission(new
         * PackageBasedPermission(copiedUuid,
         * Identity.instance().getPrincipal().getName(),
         * RoleTypes.PACKAGE_ADMIN));
         */
    }

    protected void removePackage(String uuid) {

        try {
            PackageItem item = getRulesRepository().loadPackageByUUID( uuid );
            log.info( "USER:" + getCurrentUserName() + " REMOVEING package [" + item.getName() + "]" );
            item.remove();
            getRulesRepository().save();
        } catch ( RulesRepositoryException e ) {
            log.error( "Unable to remove package.",
                       e );
            throw e;
        }
    }

    protected String renamePackage(String uuid,
                                   String newName) {
        log.info( "USER:" + getCurrentUserName() + " RENAMING package [UUID: " + uuid + "] to package [" + newName + "]" );

        return getRulesRepository().renamePackage( uuid,
                                                   newName );
    }

    protected byte[] exportPackages(String packageName) {
        log.info( "USER:" + getCurrentUserName() + " export package [name: " + packageName + "] " );

        byte[] result = null;

        try {
            result = getRulesRepository().dumpPackageFromRepositoryXml( packageName );
        } catch ( PathNotFoundException e ) {
            throw new RulesRepositoryException( e );
        } catch ( IOException e ) {
            throw new RulesRepositoryException( e );
        } catch ( RepositoryException e ) {
            throw new RulesRepositoryException( e );
        }
        return result;
    }

    // TODO: Not working. GUVNOR-475
    protected void importPackages(byte[] byteArray,
                                  boolean importAsNew) {
        getRulesRepository().importPackageToRepository( byteArray,
                                                        importAsNew );
    }

    protected String createPackage(String name,
                                   String description,
                                   String[] workspace) throws RulesRepositoryException {

        log.info( "USER: " + getCurrentUserName() + " CREATING package [" + name + "]" );
        PackageItem item = getRulesRepository().createPackage( name,
                                                               description,
                                                               workspace );

        return item.getUUID();
    }

    protected String createSubPackage(String name,
                                      String description,
                                      String parentNode) throws SerializationException {
        log.info( "USER: " + getCurrentUserName() + " CREATING subPackage [" + name + "], parent [" + parentNode + "]" );
        PackageItem item = getRulesRepository().createSubPackage( name,
                                                                  description,
                                                                  parentNode );
        return item.getUUID();
    }

    protected PackageConfigData loadPackageConfig(PackageItem packageItem) {
        PackageConfigData data = PackageConfigDataFactory.createPackageConfigDataWithDependencies( packageItem );
        if ( data.isSnapshot ) {
            data.snapshotName = packageItem.getSnapshotName();
        }
        return data;
    }

    public ValidatedResponse savePackage(PackageConfigData data) throws SerializationException {
        if ( Contexts.isSessionContextActive() ) {
            Identity.instance().checkPermission( new PackageUUIDType( data.uuid ),
                                                 RoleTypes.PACKAGE_DEVELOPER );
        }

        log.info( "USER:" + getCurrentUserName() + " SAVING package [" + data.name + "]" );

        PackageItem item = getRulesRepository().loadPackage( data.name );

        // If package is being unarchived.
        boolean unarchived = (data.archived == false && item.isArchived() == true);
        Calendar packageLastModified = item.getLastModified();

        DroolsHeader.updateDroolsHeader( data.header,
                                         item );
        updateCategoryRules( data,
                             item );

        item.updateExternalURI( data.externalURI );
        item.updateDescription( data.description );
        item.archiveItem( data.archived );
        item.updateBinaryUpToDate( false );
        ServiceImplementation.ruleBaseCache.remove( data.uuid );
        item.checkin( data.description );

        // If package is archived, archive all the assets under it
        if ( data.archived ) {
            handleArchivedForSavePackage( data,
                                          item );
        } else if ( unarchived ) {
            handleUnarchivedForSavePackage( data,
                                            item,
                                            packageLastModified );
        }

        BRMSSuggestionCompletionLoader loader = new BRMSSuggestionCompletionLoader();
        loader.getSuggestionEngine( item );

        return validateBRMSSuggestionCompletionLoaderResponse( loader );
    }

    private void updateCategoryRules(PackageConfigData data,
                                     PackageItem item) {
        KeyValueTO keyValueTO = convertMapToCsv( data.catRules );
        item.updateCategoryRules( keyValueTO.getKeys(),
                                  keyValueTO.getValues() );
    }

    // HashMap DOES NOT guarantee order in different iterations!
    private static KeyValueTO convertMapToCsv(final Map map) {
        StringBuilder keysBuilder = new StringBuilder();
        StringBuilder valuesBuilder = new StringBuilder();
        for ( Iterator i = map.entrySet().iterator(); i.hasNext(); ) {
            Map.Entry entry = (Map.Entry) i.next();
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
        private String keys;
        private String values;

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

    private void handleArchivedForSavePackage(PackageConfigData data,
                                              PackageItem item) {
        for ( Iterator<AssetItem> iter = item.getAssets(); iter.hasNext(); ) {
            AssetItem assetItem = iter.next();
            if ( !assetItem.isArchived() ) {
                assetItem.archiveItem( true );
                assetItem.checkin( data.description );
            }
        }
    }

    private void handleUnarchivedForSavePackage(PackageConfigData data,
                                                PackageItem item,
                                                Calendar packageLastModified) {
        for ( Iterator<AssetItem> iter = item.getAssets(); iter.hasNext(); ) {
            AssetItem assetItem = iter.next();
            // Unarchive the assets archived after the package
            // ( == at the same time that the package was archived)
            if ( assetItem.getLastModified().compareTo( packageLastModified ) >= 0 ) {
                assetItem.archiveItem( false );
                assetItem.checkin( data.description );
            }
        }
    }

    private ValidatedResponse validateBRMSSuggestionCompletionLoaderResponse(BRMSSuggestionCompletionLoader loader) {
        ValidatedResponse res = new ValidatedResponse();
        if ( loader.hasErrors() ) {
            res.hasErrors = true;
            String err = "";
            for ( Iterator iter = loader.getErrors().iterator(); iter.hasNext(); ) {
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
            getRulesRepository().removePackageSnapshot( packageName,
                                                        snapshotName );
        }

        getRulesRepository().createPackageSnapshot( packageName,
                                                    snapshotName );
        PackageItem item = getRulesRepository().loadPackageSnapshot( packageName,
                                                                     snapshotName );
        item.updateCheckinComment( comment );
        getRulesRepository().save();

    }

    protected void copyOrRemoveSnapshot(String packageName,
                                     String snapshotName,
                                     boolean delete,
                                     String newSnapshotName) throws SerializationException {

        if ( delete ) {
            log.info( "USER:" + getCurrentUserName() + " REMOVING SNAPSHOT for package: [" + packageName + "] snapshot: [" + snapshotName + "]" );
            getRulesRepository().removePackageSnapshot( packageName,
                                                        snapshotName );
        } else {
            if ( newSnapshotName.equals( "" ) ) {
                throw new SerializationException( "Need to have a new snapshot name." );
            }
            log.info( "USER:" + getCurrentUserName() + " COPYING SNAPSHOT for package: [" + packageName + "] snapshot: [" + snapshotName + "] to [" + newSnapshotName + "]" );

            getRulesRepository().copyPackageSnapshot( packageName,
                                                      snapshotName,
                                                      newSnapshotName );
        }

    }

    private String getCurrentUserName() {
        return getRulesRepository().getSession().getUserID();
    }

}
