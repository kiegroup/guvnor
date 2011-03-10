/*
 * Copyright 2011 JBoss Inc
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.drools.guvnor.server;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;

import org.apache.commons.io.IOUtils;
import org.drools.compiler.DrlParser;
import org.drools.compiler.DroolsParserException;
import org.drools.guvnor.client.common.AssetFormats;
import org.drools.guvnor.client.rpc.BuilderResult;
import org.drools.guvnor.client.rpc.DetailedSerializationException;
import org.drools.guvnor.client.rpc.PackageConfigData;
import org.drools.guvnor.client.rpc.PackageService;
import org.drools.guvnor.client.rpc.SnapshotComparisonPageRequest;
import org.drools.guvnor.client.rpc.SnapshotComparisonPageResponse;
import org.drools.guvnor.client.rpc.SnapshotDiffs;
import org.drools.guvnor.client.rpc.SnapshotInfo;
import org.drools.guvnor.client.rpc.ValidatedResponse;
import org.drools.guvnor.server.contenthandler.ModelContentHandler;
import org.drools.guvnor.server.security.PackageUUIDType;
import org.drools.guvnor.server.security.RoleTypes;
import org.drools.guvnor.server.util.LoggingHelper;
import org.drools.lang.descr.PackageDescr;
import org.drools.lang.descr.TypeDeclarationDescr;
import org.drools.repository.AssetItem;
import org.drools.repository.AssetItemIterator;
import org.drools.repository.PackageItem;
import org.drools.repository.RepositoryFilter;
import org.drools.repository.RulesRepository;
import org.drools.repository.RulesRepositoryException;
import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Create;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.remoting.WebRemote;
import org.jboss.seam.annotations.security.Restrict;
import org.jboss.seam.contexts.Contexts;
import org.jboss.seam.security.Identity;

import com.google.gwt.user.client.rpc.SerializationException;

@Name("org.drools.guvnor.client.rpc.PackageService")
@AutoCreate
public class RepositoryPackageService
    implements
    PackageService {
    @In
    private RulesRepository             repository;

    private static final long           serialVersionUID            = 901123;

    private static final LoggingHelper  log                         = LoggingHelper.getLogger( RepositoryAssetService.class );

    private ServiceSecurity             serviceSecurity             = new ServiceSecurity();

    private RepositoryPackageOperations repositoryPackageOperations = new RepositoryPackageOperations();

    @Create
    public void create() {
        repositoryPackageOperations.setRulesRepository( getRulesRepository() );
    }

    /* This is called in hosted mode when creating "by hand" */
    public void setRulesRepository(RulesRepository repository) {
        this.repository = repository;
        create();
    }

    public RulesRepository getRulesRepository() {
        return repository;
    }

    /**
     * Role-based Authorization check: This method only returns packages that
     * the user has permission to access. User has permission to access the
     * particular package when: The user has a package.readonly role or higher
     * (i.e., package.admin, package.developer) to this package.
     */
    @WebRemote
    @Restrict("#{identity.loggedIn}")
    public PackageConfigData[] listPackages() {
        return listPackages( null );
    }

    @WebRemote
    @Restrict("#{identity.loggedIn}")
    public PackageConfigData[] listPackages(String workspace) {
        RepositoryFilter pf = new PackageFilter();
        return repositoryPackageOperations.listPackages( false,
                                                         workspace,
                                                         pf );
    }

    @WebRemote
    @Restrict("#{identity.loggedIn}")
    public PackageConfigData[] listArchivedPackages() {
        return listArchivedPackages( null );
    }

    @WebRemote
    @Restrict("#{identity.loggedIn}")
    public PackageConfigData[] listArchivedPackages(String workspace) {
        RepositoryFilter pf = new PackageFilter();
        return repositoryPackageOperations.listPackages( true,
                                                         workspace,
                                                         pf );
    }

    public PackageConfigData loadGlobalPackage() {
        return repositoryPackageOperations.loadGlobalPackage();
    }

    @WebRemote
    @Restrict("#{identity.loggedIn}")
    public void rebuildPackages() throws SerializationException {
        Iterator<PackageItem> pkit = getRulesRepository().listPackages();
        StringBuilder errs = new StringBuilder();
        while ( pkit.hasNext() ) {
            PackageItem pkg = pkit.next();
            try {
                BuilderResult res = this.buildPackage( pkg.getUUID(),
                                                       true );
                if ( res != null ) {
                    errs.append( "Unable to build package name [" + pkg.getName() + "]\n" );
                    StringBuilder buf = new StringBuilder();
                    for ( int i = 0; i < res.getLines().size(); i++ ) {
                        buf.append( res.getLines().get( i ).toString() );
                        buf.append( '\n' );
                    }
                    log.warn( buf.toString() );
                }
            } catch ( Exception e ) {
                log.error( "An error occurred building package [" + pkg.getName() + "]\n" );
                errs.append( "An error occurred building package [" + pkg.getName() + "]\n" );
            }
        }

        if ( errs.toString().length() > 0 ) {
            throw new DetailedSerializationException( "Unable to rebuild all packages.",
                                                      errs.toString() );
        }
    }

    @WebRemote
    @Restrict("#{identity.loggedIn}")
    public String buildPackageSource(String packageUUID) throws SerializationException {
        serviceSecurity.checkSecurityIsPackageDeveloper( packageUUID );
        return repositoryPackageOperations.buildPackageSource( packageUUID );
    }

    @WebRemote
    public void copyPackage(String sourcePackageName,
                            String destPackageName) throws SerializationException {
        serviceSecurity.checkSecurityIsAdmin();
        repositoryPackageOperations.copyPackage( sourcePackageName,
                                                 destPackageName );
    }

    @WebRemote
    @Restrict("#{identity.loggedIn}")
    public void removePackage(String uuid) {
        serviceSecurity.checkSecurityIsPackageAdmin( uuid );
        repositoryPackageOperations.removePackage( uuid );
    }

    @WebRemote
    @Restrict("#{identity.loggedIn}")
    public String renamePackage(String uuid,
                                String newName) {
        serviceSecurity.checkSecurityIsPackageAdmin( uuid );

        return repositoryPackageOperations.renamePackage( uuid,
                                                          newName );
    }

    @WebRemote
    @Restrict("#{identity.loggedIn}")
    public byte[] exportPackages(String packageName) {
        serviceSecurity.checkSecurityIsPackageNameTypeAdmin( packageName );
        return repositoryPackageOperations.exportPackages( packageName );
    }

    @WebRemote
    @Restrict("#{identity.loggedIn}")
    // TODO: Not working. GUVNOR-475
    public void importPackages(byte[] byteArray,
                               boolean importAsNew) {
        repositoryPackageOperations.importPackages( byteArray,
                                                    importAsNew );
    }

    @WebRemote
    public String createPackage(String name,
                                String description,
                                String[] workspace) throws RulesRepositoryException {
        serviceSecurity.checkSecurityIsAdmin();
        return repositoryPackageOperations.createPackage( name,
                                                          description,
                                                          workspace );
    }

    @WebRemote
    public String createPackage(String name,
                                String description) throws RulesRepositoryException {
        return createPackage( name,
                              description,
                              new String[]{} );
    }

    @WebRemote
    public String createSubPackage(String name,
                                   String description,
                                   String parentNode) throws SerializationException {
        serviceSecurity.checkSecurityIsAdmin();
        return repositoryPackageOperations.createSubPackage( name,
                                                             description,
                                                             parentNode );
    }

    @WebRemote
    @Restrict("#{identity.loggedIn}")
    public PackageConfigData loadPackageConfig(String uuid) {
        PackageItem packageItem = getRulesRepository().loadPackageByUUID( uuid );
        // the uuid passed in is the uuid of that deployment bundle, not the
        // package uudi.
        // we have to figure out the package name.
        serviceSecurity.checkSecurityNameTypePackageReadOnly( packageItem );
        return repositoryPackageOperations.loadPackageConfig( packageItem );
    }

    @WebRemote
    @Restrict("#{identity.loggedIn}")
    public ValidatedResponse savePackage(PackageConfigData data) throws SerializationException {
        serviceSecurity.checkSecurityIsPackageDeveloper( data.uuid );
        return repositoryPackageOperations.savePackage( data );

    }

    @WebRemote
    @Restrict("#{identity.loggedIn}")
    public BuilderResult buildPackage(String packageUUID,
                                      boolean force) throws SerializationException {
        return buildPackage( packageUUID,
                             force,
                             null,
                             null,
                             null,
                             false,
                             null,
                             null,
                             false,
                             null );
    }

    @WebRemote
    @Restrict("#{identity.loggedIn}")
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
        serviceSecurity.checkSecurityIsPackageDeveloper( packageUUID );
        return repositoryPackageOperations.buildPackage( packageUUID,
                                                         force,
                                                         buildMode,
                                                         statusOperator,
                                                         statusDescriptionValue,
                                                         enableStatusSelector,
                                                         categoryOperator,
                                                         category,
                                                         enableCategorySelector,
                                                         customSelectorName );
    }

    @WebRemote
    @Restrict("#{identity.loggedIn}")
    public void createPackageSnapshot(String packageName,
                                      String snapshotName,
                                      boolean replaceExisting,
                                      String comment) {
        serviceSecurity.checkSecurityIsPackageNameTypeAdmin( packageName );
        repositoryPackageOperations.createPackageSnapshot( packageName,
                                                           snapshotName,
                                                           replaceExisting,
                                                           comment );

    }

    @WebRemote
    @Restrict("#{identity.loggedIn}")
    public void copyOrRemoveSnapshot(String packageName,
                                     String snapshotName,
                                     boolean delete,
                                     String newSnapshotName) throws SerializationException {
        serviceSecurity.checkSecurityIsPackageNameTypeAdmin( packageName );
        repositoryPackageOperations.copyOrRemoveSnapshot( packageName,
                                                          snapshotName,
                                                          delete,
                                                          newSnapshotName );
    }

    @WebRemote
    @Restrict("#{identity.loggedIn}")
    public String[] listRulesInPackage(String packageName) throws SerializationException {
        serviceSecurity.checkSecurityIsPackageReadOnly( packageName );
        return repositoryPackageOperations.listRulesInPackage( packageName );
    }

    @WebRemote
    public void rebuildSnapshots() throws SerializationException {
        serviceSecurity.checkSecurityIsAdmin();

        Iterator<PackageItem> pkit = getRulesRepository().listPackages();
        while ( pkit.hasNext() ) {
            PackageItem pkg = pkit.next();
            String[] snaps = getRulesRepository().listPackageSnapshots( pkg.getName() );
            for ( String snapName : snaps ) {
                PackageItem snap = getRulesRepository().loadPackageSnapshot( pkg.getName(),
                                                                             snapName );
                BuilderResult res = this.buildPackage( snap.getUUID(),
                                                       true );
                if ( res != null ) {
                    StringBuilder stringBuilder = new StringBuilder();
                    for ( int i = 0; i < res.getLines().size(); i++ ) {
                        stringBuilder.append( res.getLines().get( i ).toString() );
                        stringBuilder.append( '\n' );
                    }
                    throw new DetailedSerializationException( "Unable to rebuild snapshot [" + snapName,
                                                              stringBuilder.toString() + "]" );
                }
            }
        }
    }

    @WebRemote
    @Restrict("#{identity.loggedIn}")
    public SnapshotInfo[] listSnapshots(String packageName) {
        serviceSecurity.checkSecurityIsPackageDeveloper( packageName );

        String[] snaps = getRulesRepository().listPackageSnapshots( packageName );
        SnapshotInfo[] res = new SnapshotInfo[snaps.length];
        for ( int i = 0; i < snaps.length; i++ ) {
            PackageItem snap = getRulesRepository().loadPackageSnapshot( packageName,
                                                                         snaps[i] );
            SnapshotInfo info = new SnapshotInfo();
            res[i] = info;
            info.comment = snap.getCheckinComment();
            info.name = snaps[i];
            info.uuid = snap.getUUID();
        }
        return res;
    }

    @WebRemote
    @Restrict("#{identity.loggedIn}")
    public String[] listTypesInPackage(String packageUUID) throws SerializationException {
        if ( Contexts.isSessionContextActive() ) {
            Identity.instance().checkPermission( new PackageUUIDType( packageUUID ),
                                                 RoleTypes.PACKAGE_READONLY );
        }

        PackageItem pkg = this.getRulesRepository().loadPackageByUUID( packageUUID );
        List<String> res = new ArrayList<String>();
        AssetItemIterator it = pkg.listAssetsByFormat( new String[]{AssetFormats.MODEL, AssetFormats.DRL_MODEL} );

        JarInputStream jis = null;

        try {
            while ( it.hasNext() ) {
                AssetItem asset = (AssetItem) it.next();
                if ( !asset.isArchived() ) {
                    if ( asset.getFormat().equals( AssetFormats.MODEL ) ) {
                        jis = typesForModel( res,
                                             asset );
                    } else {
                        typesForOthers( res,
                                        asset );
                    }

                }
            }
            return res.toArray( new String[res.size()] );
        } catch ( IOException e ) {
            log.error( "Unable to read the jar files in the package: " + e.getMessage() );
            throw new DetailedSerializationException( "Unable to read the jar files in the package.",
                                                      e.getMessage() );
        } finally {
            IOUtils.closeQuietly( jis );
        }

    }

    private JarInputStream typesForModel(List<String> res,
                                         AssetItem asset) throws IOException {
        JarInputStream jis;
        jis = new JarInputStream( asset.getBinaryContentAttachment() );
        JarEntry entry = null;
        while ( (entry = jis.getNextJarEntry()) != null ) {
            if ( !entry.isDirectory() ) {
                if ( entry.getName().endsWith( ".class" ) ) {
                    res.add( ModelContentHandler.convertPathToName( entry.getName() ) );
                }
            }
        }
        return jis;
    }

    private void typesForOthers(List<String> res,
                                AssetItem asset) {
        // its delcared model
        DrlParser parser = new DrlParser();
        try {
            PackageDescr desc = parser.parse( asset.getContent() );
            List<TypeDeclarationDescr> types = desc.getTypeDeclarations();
            for ( TypeDeclarationDescr typeDeclarationDescr : types ) {
                res.add( typeDeclarationDescr.getTypeName() );
            }
        } catch ( DroolsParserException e ) {
            log.error( "An error occurred parsing rule: " + e.getMessage() );

        }
    }

    @Restrict("#{identity.loggedIn}")
    public void installSampleRepository() throws SerializationException {
        serviceSecurity.checkSecurityIsAdmin();
        getRulesRepository().importRepository( this.getClass().getResourceAsStream( "/mortgage-sample-repository.xml" ) );
        this.rebuildPackages();
        this.rebuildSnapshots();
    }
    
    /**
     * @deprecated in favour of {@link compareSnapshots(SnapshotComparisonPageRequest)}
     */
    public SnapshotDiffs compareSnapshots(String packageName,
                                          String firstSnapshotName,
                                          String secondSnapshotName) {
        return repositoryPackageOperations.compareSnapshots( packageName,
                                                             firstSnapshotName,
                                                             secondSnapshotName );
    }

    public SnapshotComparisonPageResponse compareSnapshots(SnapshotComparisonPageRequest request) {
        if ( request == null ) {
            throw new IllegalArgumentException( "request cannot be null" );
        }
        if ( request.getPageSize() != null && request.getPageSize() < 0 ) {
            throw new IllegalArgumentException( "pageSize cannot be less than zero." );
        }

        return repositoryPackageOperations.compareSnapshots( request );
    }


}
