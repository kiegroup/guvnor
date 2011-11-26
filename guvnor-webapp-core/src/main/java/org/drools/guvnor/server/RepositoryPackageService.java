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
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;

import javax.inject.Inject;
import javax.inject.Named;

import org.apache.commons.io.IOUtils;
import org.drools.ClockType;
import org.drools.RuleBase;
import org.drools.RuleBaseConfiguration;
import org.drools.RuleBaseFactory;
import org.drools.SessionConfiguration;
import org.drools.base.ClassTypeResolver;
import org.drools.common.AbstractRuleBase;
import org.drools.common.InternalRuleBase;
import org.drools.common.InternalWorkingMemory;
import org.drools.compiler.DrlParser;
import org.drools.compiler.DroolsParserException;
import org.drools.core.util.DroolsStreamUtils;
import org.drools.guvnor.client.common.AssetFormats;
import org.drools.guvnor.client.rpc.BuilderResult;
import org.drools.guvnor.client.rpc.BulkTestRunResult;
import org.drools.guvnor.client.rpc.DetailedSerializationException;
import org.drools.guvnor.client.rpc.PackageConfigData;
import org.drools.guvnor.client.rpc.PackageService;
import org.drools.guvnor.client.rpc.RuleAsset;
import org.drools.guvnor.client.rpc.ScenarioResultSummary;
import org.drools.guvnor.client.rpc.ScenarioRunResult;
import org.drools.guvnor.client.rpc.SingleScenarioResult;
import org.drools.guvnor.client.rpc.SnapshotComparisonPageRequest;
import org.drools.guvnor.client.rpc.SnapshotComparisonPageResponse;
import org.drools.guvnor.client.rpc.SnapshotDiffs;
import org.drools.guvnor.client.rpc.SnapshotInfo;
import org.drools.guvnor.client.rpc.ValidatedResponse;
import org.drools.guvnor.server.builder.AuditLogReporter;
import org.drools.guvnor.server.builder.ClassLoaderBuilder;
import org.drools.guvnor.server.cache.RuleBaseCache;
import org.drools.guvnor.server.contenthandler.ModelContentHandler;
import org.drools.guvnor.server.util.LoggingHelper;
import org.drools.ide.common.client.modeldriven.testing.Scenario;
import org.drools.ide.common.shared.workitems.PortableWorkDefinition;
import org.drools.lang.descr.PackageDescr;
import org.drools.lang.descr.TypeDeclarationDescr;
import org.drools.repository.AssetItem;
import org.drools.repository.AssetItemIterator;
import org.drools.repository.PackageItem;
import org.drools.repository.RepositoryFilter;
import org.drools.repository.RulesRepository;
import org.drools.repository.RulesRepositoryException;
import org.drools.rule.Package;
import org.drools.runtime.process.WorkItem;
import org.drools.runtime.process.WorkItemHandler;
import org.drools.runtime.process.WorkItemManager;
import org.drools.runtime.rule.ConsequenceException;
import org.drools.testframework.RuleCoverageListener;
import org.drools.testframework.ScenarioRunner;
import org.jboss.seam.remoting.annotations.WebRemote;
import org.jboss.seam.security.Identity;
import org.jboss.seam.security.annotations.LoggedIn;

import com.google.gwt.user.client.rpc.SerializationException;

@Named("org.drools.guvnor.client.rpc.PackageService")
public class RepositoryPackageService
        implements
        PackageService {
    @Inject
    private RulesRepository             rulesRepository;

    private static final long           serialVersionUID = 901123;

    private static final LoggingHelper  log              = LoggingHelper.getLogger( RepositoryAssetService.class );

    @Inject
    private ServiceSecurity             serviceSecurity;

    @Inject
    private Identity                    identity;

    @Inject
    private RepositoryPackageOperations repositoryPackageOperations;

    @Inject
    private RepositoryAssetOperations   repositoryAssetOperations;

    @Inject
    private ServiceImplementation       serviceImplementation;

    /**
     * Role-based Authorization check: This method only returns packages that
     * the user has permission to access. User has permission to access the
     * particular package when: The user has a package.readonly role or higher
     * (i.e., package.admin, package.developer) to this package.
     */
    @WebRemote
    @LoggedIn
    public PackageConfigData[] listPackages() {
        return listPackages( null );
    }

    @WebRemote
    @LoggedIn
    public PackageConfigData[] listPackages(String workspace) {
        RepositoryFilter pf = new PackageFilter( identity );
        return repositoryPackageOperations.listPackages(
                                                         false,
                                                         workspace,
                                                         pf );
    }

    @WebRemote
    @LoggedIn
    public PackageConfigData[] listArchivedPackages() {
        return listArchivedPackages( null );
    }

    @WebRemote
    @LoggedIn
    public PackageConfigData[] listArchivedPackages(String workspace) {
        return repositoryPackageOperations.listPackages(
                                                         true,
                                                         workspace,
                                                         new PackageFilter( identity ) );
    }

    public PackageConfigData loadGlobalPackage() {
        return repositoryPackageOperations.loadGlobalPackage();
    }

    @WebRemote
    @LoggedIn
    public void rebuildPackages() throws SerializationException {
        Iterator<PackageItem> pkit = rulesRepository.listPackages();
        StringBuilder errs = new StringBuilder();
        while ( pkit.hasNext() ) {
            PackageItem pkg = pkit.next();
            try {
                BuilderResult builderResult = this.buildPackage( pkg.getUUID(),
                                                                 true );
                if ( builderResult != null ) {
                    errs.append( "Unable to build package name [" ).append( pkg.getName() ).append( "]\n" );
                    StringBuilder buf = createStringBuilderFrom( builderResult );
                    log.warn( buf.toString() );
                }
            } catch ( Exception e ) {
                e.printStackTrace();
                log.error( "An error occurred building package [" + pkg.getName() + "]\n" );
                errs.append( "An error occurred building package [" ).append( pkg.getName() ).append( "]\n" );
            }
        }
    }

    private StringBuilder createStringBuilderFrom(BuilderResult res) {
        StringBuilder buf = new StringBuilder();
        for ( int i = 0; i < res.getLines().size(); i++ ) {
            buf.append( res.getLines().get( i ).toString() );
            buf.append( '\n' );
        }
        return buf;
    }

    @WebRemote
    @LoggedIn
    public String buildPackageSource(String packageUUID) throws SerializationException {
        serviceSecurity.checkSecurityIsPackageDeveloperWithPackageUuid( packageUUID );
        return repositoryPackageOperations.buildPackageSource( packageUUID );
    }

    @WebRemote
    public String copyPackage(String sourcePackageName,
                              String destPackageName) throws SerializationException {
        serviceSecurity.checkSecurityIsAdmin();
        return repositoryPackageOperations.copyPackage( sourcePackageName,
                                                        destPackageName );
    }

    @WebRemote
    @LoggedIn
    public void removePackage(String uuid) {
        serviceSecurity.checkSecurityIsPackageAdminWithPackageUuid( uuid );
        repositoryPackageOperations.removePackage( uuid );
    }

    @WebRemote
    @LoggedIn
    public String renamePackage(String uuid,
                                String newName) {
        serviceSecurity.checkSecurityIsPackageAdminWithPackageUuid( uuid );

        return repositoryPackageOperations.renamePackage( uuid,
                                                          newName );
    }

    @WebRemote
    @LoggedIn
    public byte[] exportPackages(String packageName) {
        serviceSecurity.checkSecurityIsPackageAdminWithPackageName( packageName );
        return repositoryPackageOperations.exportPackages( packageName );
    }

    @WebRemote
    @LoggedIn
    public void importPackages(byte[] byteArray,
                               boolean importAsNew) {
        repositoryPackageOperations.importPackages( byteArray,
                                                    importAsNew );
    }

    @WebRemote
    public String createPackage(String name,
                                String description,
                                String format) throws RulesRepositoryException {
        return repositoryPackageOperations.createPackage( name,
                                                          description,
                                                          format );
    }

    @WebRemote
    public String createPackage(String name,
                                String description,
                                String format,
                                String[] workspace) throws RulesRepositoryException {
        serviceSecurity.checkSecurityIsAdmin();
        return repositoryPackageOperations.createPackage( name,
                                                          description,
                                                          format,
                                                          workspace );
    }

    /*
     * @WebRemote public String createPackage(String name, String description,
     * String format) throws RulesRepositoryException {
     * serviceSecurity.checkSecurityIsAdmin(); return
     * repositoryPackageOperations.createPackage( name, description, new
     * String[]{} ); }
     */
    /*
     * @WebRemote public String createPackage(String name, String description,
     * String format, String[] workspace) throws RulesRepositoryException {
     * return createPackage( name, description, new String[]{} ); }
     */
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
    @LoggedIn
    public PackageConfigData loadPackageConfig(String uuid) {
        PackageItem packageItem = rulesRepository.loadPackageByUUID( uuid );
        // the uuid passed in is the uuid of that deployment bundle, not the
        // package uudi.
        // we have to figure out the package name.
        serviceSecurity.checkSecurityIsPackageReadOnlyWithPackageName( packageItem.getName() );
        return repositoryPackageOperations.loadPackageConfig( packageItem );
    }

    @WebRemote
    @LoggedIn
    public ValidatedResponse validatePackageConfiguration(PackageConfigData data) throws SerializationException {
        serviceSecurity.checkSecurityIsPackageDeveloperWithPackageUuid( data.getUuid() );
        return repositoryPackageOperations.validatePackageConfiguration( data );
    }

    @WebRemote
    @LoggedIn
    public void savePackage(PackageConfigData data) throws SerializationException {
        serviceSecurity.checkSecurityIsPackageDeveloperWithPackageUuid( data.getUuid() );
        repositoryPackageOperations.savePackage( data );
    }

    @WebRemote
    @LoggedIn
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
    @LoggedIn
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
        serviceSecurity.checkSecurityIsPackageDeveloperWithPackageUuid( packageUUID );
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
    @LoggedIn
    public void createPackageSnapshot(String packageName,
                                      String snapshotName,
                                      boolean replaceExisting,
                                      String comment) {
        serviceSecurity.checkSecurityIsPackageAdminWithPackageName( packageName );
        repositoryPackageOperations.createPackageSnapshot( packageName,
                                                           snapshotName,
                                                           replaceExisting,
                                                           comment );

    }

    @WebRemote
    @LoggedIn
    public void copyOrRemoveSnapshot(String packageName,
                                     String snapshotName,
                                     boolean delete,
                                     String newSnapshotName) throws SerializationException {
        serviceSecurity.checkSecurityIsPackageAdminWithPackageName( packageName );
        repositoryPackageOperations.copyOrRemoveSnapshot( packageName,
                                                          snapshotName,
                                                          delete,
                                                          newSnapshotName );
    }

    @WebRemote
    @LoggedIn
    public String[] listRulesInPackage(String packageName) throws SerializationException {
        serviceSecurity.checkSecurityIsPackageReadOnlyWithPackageName( packageName );
        return repositoryPackageOperations.listRulesInPackage( packageName );
    }

    @WebRemote
    @LoggedIn
    public String[] listImagesInPackage(String packageName) throws SerializationException {
        serviceSecurity.checkSecurityIsPackageReadOnlyWithPackageName( packageName );
        return repositoryPackageOperations.listImagesInPackage( packageName );
    }

    @WebRemote
    public void rebuildSnapshots() throws SerializationException {
        serviceSecurity.checkSecurityIsAdmin();

        Iterator<PackageItem> pkit = rulesRepository.listPackages();
        while ( pkit.hasNext() ) {
            PackageItem pkg = pkit.next();
            String[] snaps = rulesRepository.listPackageSnapshots( pkg.getName() );
            for ( String snapName : snaps ) {
                PackageItem snap = rulesRepository.loadPackageSnapshot( pkg.getName(),
                                                                        snapName );
                BuilderResult builderResult = this.buildPackage( snap.getUUID(),
                                                                 true );
                if ( builderResult.hasLines() ) {
                    StringBuilder stringBuilder = createStringBuilderFrom( builderResult );
                    throw new DetailedSerializationException( "Unable to rebuild snapshot [" + snapName,
                                                              stringBuilder.toString() + "]" );
                }
            }
        }
    }

    @WebRemote
    @LoggedIn
    public SnapshotInfo[] listSnapshots(String packageName) {
        serviceSecurity.checkSecurityIsPackageDeveloperWithPackageName( packageName );

        String[] snaps = rulesRepository.listPackageSnapshots( packageName );
        SnapshotInfo[] snapshotInfos = new SnapshotInfo[snaps.length];
        for ( int i = 0; i < snaps.length; i++ ) {
            PackageItem packageItem = rulesRepository.loadPackageSnapshot( packageName,
                                                                           snaps[i] );
            snapshotInfos[i] = packageItemToSnapshotItem( snaps[i],
                                                          packageItem );
        }
        return snapshotInfos;
    }

    @LoggedIn
    public SnapshotInfo loadSnapshotInfo(String packageName,
                                         String snapshotName) {
        serviceSecurity.checkSecurityIsPackageAdminWithPackageName( packageName );

        return packageItemToSnapshotItem(
                                          snapshotName,
                                          rulesRepository.loadPackageSnapshot(
                                                                               packageName,
                                                                               snapshotName ) );
    }

    private SnapshotInfo packageItemToSnapshotItem(String snapshotName,
                                                   PackageItem packageItem) {
        SnapshotInfo snapshotInfo = new SnapshotInfo();
        snapshotInfo.setComment( packageItem.getCheckinComment() );
        snapshotInfo.setName( snapshotName );
        snapshotInfo.setUuid( packageItem.getUUID() );
        return snapshotInfo;
    }

    @WebRemote
    @LoggedIn
    public String[] listTypesInPackage(String packageUUID) throws SerializationException {
        serviceSecurity.checkSecurityPackageReadOnlyWithPackageUuid( packageUUID );

        PackageItem pkg = this.rulesRepository.loadPackageByUUID( packageUUID );
        List<String> res = new ArrayList<String>();
        AssetItemIterator it = pkg.listAssetsByFormat( AssetFormats.MODEL,
                                                       AssetFormats.DRL_MODEL );

        JarInputStream jis = null;

        try {
            while ( it.hasNext() ) {
                AssetItem asset = it.next();
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

    @WebRemote
    @LoggedIn
    public void updateDependency(String uuid,
                                 String dependencyPath) {
        PackageItem item = rulesRepository.loadPackageByUUID( uuid );
        item.updateDependency( dependencyPath );
        item.checkin( "Update dependency" );
    }

    public String[] getDependencies(String uuid) {
        PackageItem item = rulesRepository.loadPackageByUUID( uuid );
        return item.getDependencies();
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

    @LoggedIn
    public void installSampleRepository() throws SerializationException {
        rulesRepository.importRepository( this.getClass().getResourceAsStream( "/mortgage-sample-repository.xml" ) );
        this.rebuildPackages();
        this.rebuildSnapshots();
    }

    /**
     * @deprecated in favour of
     *             {@link #compareSnapshots(SnapshotComparisonPageRequest)}
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

    @WebRemote
    @LoggedIn
    public SingleScenarioResult runScenario(String packageName,
                                            Scenario scenario) throws SerializationException {
        serviceSecurity.checkSecurityIsPackageDeveloperWithPackageName( packageName );

        return runScenario( packageName,
                            scenario,
                            null );

    }

    private SingleScenarioResult runScenario(String packageName,
                                             Scenario scenario,
                                             RuleCoverageListener coverage) throws SerializationException {
        PackageItem item = this.rulesRepository.loadPackage( packageName );
        SingleScenarioResult result = null;
        // nasty classloader needed to make sure we use the same tree the whole
        // time.
        ClassLoader originalCL = Thread.currentThread().getContextClassLoader();
        try {
            final RuleBase rb = loadCacheRuleBase( item );

            ClassLoader cl = ((InternalRuleBase) RuleBaseCache.getInstance().get( item.getUUID() )).getRootClassLoader();
            Thread.currentThread().setContextClassLoader( cl );
            result = runScenario( scenario,
                                  item,
                                  cl,
                                  rb,
                                  coverage );
        } catch ( Exception e ) {
            if ( e instanceof DetailedSerializationException ) {
                DetailedSerializationException err = (DetailedSerializationException) e;
                result = new SingleScenarioResult();
                if ( err.getErrs() != null ) {
                    result.result = new ScenarioRunResult( err.getErrs(),
                                                           null );
                } else {
                    throw err;
                }
            } else {
                throw new DetailedSerializationException( "Unable to run the scenario.",
                                                          e.getMessage() );
            }
        } finally {
            Thread.currentThread().setContextClassLoader( originalCL );
        }
        return result;
    }

    /*
     * Set the Rule base in a cache
     */
    private RuleBase loadCacheRuleBase(PackageItem packageItem) throws DetailedSerializationException {
        RuleBase rb = null;
        if ( packageItem.isBinaryUpToDate() && RuleBaseCache.getInstance().contains( packageItem.getUUID() ) ) {
            rb = RuleBaseCache.getInstance().get( packageItem.getUUID() );
        } else {
            // load up the classloader we are going to use
            ClassLoaderBuilder classLoaderBuilder = new ClassLoaderBuilder( packageItem.listAssetsWithVersionsSpecifiedByDependenciesByFormat( AssetFormats.MODEL ) );
            ClassLoader buildCl = classLoaderBuilder.buildClassLoader();

            // we have to build the package, and try again.
            if ( packageItem.isBinaryUpToDate() ) {
                rb = loadRuleBase( packageItem,
                                   buildCl );
                RuleBaseCache.getInstance().put( packageItem.getUUID(),
                                                 rb );
            } else {
                BuilderResult result = repositoryPackageOperations.buildPackage( packageItem,
                                                                                 false );
                if ( result == null || result.getLines().size() == 0 ) {
                    rb = loadRuleBase( packageItem,
                                       buildCl );
                    RuleBaseCache.getInstance().put( packageItem.getUUID(),
                                                     rb );
                } else throw new DetailedSerializationException( "Build error",
                                                                 result.getLines() );
            }

        }
        return rb;
    }

    private RuleBase loadRuleBase(PackageItem item,
                                  ClassLoader cl) throws DetailedSerializationException {
        try {
            return deserKnowledgebase( item,
                                       cl );
        } catch ( ClassNotFoundException e ) {
            log.error( "Unable to load rule base.",
                       e );
            throw new DetailedSerializationException( "A required class was not found.",
                                                      e.getMessage() );
        } catch ( Exception e ) {
            log.error( "Unable to load rule base.",
                       e );
            log.info( "...but trying to rebuild binaries..." );
            try {
                BuilderResult res = repositoryPackageOperations.buildPackage( item,
                                                                              true );
                if ( res != null && res.getLines().size() > 0 ) {
                    log.error( "There were errors when rebuilding the knowledgebase." );
                    throw new DetailedSerializationException( "There were errors when rebuilding the knowledgebase.",
                                                              "" );
                }
            } catch ( Exception e1 ) {
                log.error( "Unable to rebuild the rulebase: " + e.getMessage() );
                throw new DetailedSerializationException( "Unable to rebuild the rulebase.",
                                                          e.getMessage() );
            }
            try {
                return deserKnowledgebase( item,
                                           cl );
            } catch ( Exception e2 ) {
                log.error( "Unable to reload knowledgebase: " + e.getMessage() );
                throw new DetailedSerializationException( "Unable to reload knowledgebase.",
                                                          e.getMessage() );
            }

        }
    }

    private RuleBase deserKnowledgebase(PackageItem item,
                                        ClassLoader classloader) throws IOException,
                                                                ClassNotFoundException {
        RuleBase rulebase = RuleBaseFactory.newRuleBase( new RuleBaseConfiguration( classloader ) );
        Package bin = (Package) DroolsStreamUtils.streamIn( item.getCompiledPackageBytes(),
                                                            classloader );
        rulebase.addPackage( bin );
        return rulebase;
    }

    private SingleScenarioResult runScenario(Scenario scenario,
                                             PackageItem item,
                                             ClassLoader cl,
                                             RuleBase rulebase,
                                             RuleCoverageListener coverage) throws DetailedSerializationException {

        Package bin = rulebase.getPackages()[0];

        Set<String> imps = bin.getImports().keySet();
        Set<String> allImps = new HashSet<String>( imps );

        if ( bin.getGlobals() != null ) {
            for ( Object o : bin.getGlobals().keySet() ) {
                allImps.add( bin.getGlobals().get( o ) );
            }
        }
        // need this for Generated beans to work
        allImps.add( bin.getName() + ".*" ); 

        ClassTypeResolver classTypeResolver = new ClassTypeResolver( allImps,
                                                                     cl );
        SessionConfiguration sessionConfiguration = new SessionConfiguration();
        sessionConfiguration.setClockType( ClockType.PSEUDO_CLOCK );
        sessionConfiguration.setKeepReference( false );
        InternalWorkingMemory workingMemory = (InternalWorkingMemory) rulebase.newStatefulSession( sessionConfiguration,
                                                                                                   null );
        if ( coverage != null ) workingMemory.addEventListener( coverage );

        //Add stub Work Item Handlers
        String packageUUID = item.getUUID();
        Set<PortableWorkDefinition> workItems = serviceImplementation.loadWorkItemDefinitions( packageUUID );
        WorkItemHandler workItemHandlerStub = getWorkItemHandlerStub();
        for ( PortableWorkDefinition pwd : workItems ) {
            workingMemory.getWorkItemManager().registerWorkItemHandler( pwd.getName(),
                                                                        workItemHandlerStub );
        }

        //Run Test Scenario
        try {
            AuditLogReporter logger = new AuditLogReporter( workingMemory );
            new ScenarioRunner( scenario,
                                classTypeResolver,
                                workingMemory );
            SingleScenarioResult singleScenarioresult = new SingleScenarioResult();
            singleScenarioresult.auditLog = logger.buildReport();
            singleScenarioresult.result = new ScenarioRunResult( null,
                                                                 scenario );
            return singleScenarioresult;
        } catch ( ClassNotFoundException e ) {
            log.error( "Unable to load a required class.",
                       e );
            throw new DetailedSerializationException( "Unable to load a required class.",
                                                      e.getMessage() );
        } catch ( ConsequenceException e ) {
            String messageShort = "There was an error executing the consequence of rule [" + e.getRule().getName() + "]";
            String messageLong = e.getMessage();
            if ( e.getCause() != null ) {
                messageLong += "\nCAUSED BY " + e.getCause().getMessage();
            }

            log.error( messageShort + ": " + messageLong,
                       e );
            throw new DetailedSerializationException( messageShort,
                                                      messageLong );
        } catch ( Exception e ) {
            log.error( "Unable to run the scenario.",
                       e );
            throw new DetailedSerializationException( "Unable to run the scenario.",
                                                      e.getMessage() );
        }
    }

    @WebRemote
    @LoggedIn
    public BulkTestRunResult runScenariosInPackage(String packageUUID) throws SerializationException {
        serviceSecurity.checkSecurityIsPackageDeveloperWithPackageUuid( packageUUID );
        PackageItem item = rulesRepository.loadPackageByUUID( packageUUID );
        return runScenariosInPackage( item );
    }

    public BulkTestRunResult runScenariosInPackage(PackageItem packageItem) throws DetailedSerializationException,
                                                                           SerializationException {
        ClassLoader originalClassloader = Thread.currentThread().getContextClassLoader();
        ClassLoader classloader = null;

        try {
            if ( packageItem.isBinaryUpToDate() && RuleBaseCache.getInstance().contains( packageItem.getUUID() ) ) {

                AbstractRuleBase arb = (AbstractRuleBase) RuleBaseCache.getInstance().get( packageItem.getUUID() );
                // load up the existing class loader from before
                classloader = arb.getConfiguration().getClassLoader();
                Thread.currentThread().setContextClassLoader( classloader );
            } else {
                // load up the classloader we are going to use
                ClassLoaderBuilder classLoaderBuilder = new ClassLoaderBuilder( packageItem.listAssetsWithVersionsSpecifiedByDependenciesByFormat( AssetFormats.MODEL ) );
                classloader = classLoaderBuilder.buildClassLoader();
                Thread.currentThread().setContextClassLoader( classloader );

                // we have to build the package, and try again.
                if ( packageItem.isBinaryUpToDate() ) {
                    RuleBaseCache.getInstance().put( packageItem.getUUID(),
                                                     loadRuleBase( packageItem,
                                                                   classloader ) );
                } else {
                    BuilderResult result = repositoryPackageOperations.buildPackage( packageItem,
                                                                                     false );
                    if ( result == null || result.getLines().size() == 0 ) {
                        RuleBaseCache.getInstance().put( packageItem.getUUID(),
                                                         loadRuleBase( packageItem,
                                                                       classloader ) );
                    } else {
                        return new BulkTestRunResult( result,
                                                      null,
                                                      0,
                                                      null );
                    }
                }
            }

            AssetItemIterator it = packageItem.listAssetsByFormat( AssetFormats.TEST_SCENARIO );
            List<ScenarioResultSummary> resultSummaries = new ArrayList<ScenarioResultSummary>();
            RuleBase rb = RuleBaseCache.getInstance().get( packageItem.getUUID() );
            Package bin = rb.getPackages()[0];

            RuleCoverageListener coverage = new RuleCoverageListener( expectedRules( bin ) );

            while ( it.hasNext() ) {
                AssetItem as = it.next();
                if ( !as.getDisabled() ) {
                    RuleAsset asset = repositoryAssetOperations.loadAsset( as );
                    Scenario sc = (Scenario) asset.getContent();
                    runScenario( packageItem.getName(),
                                 sc,
                                 coverage );// runScenario(sc, res,
                    // workingMemory).scenario;

                    int[] totals = sc.countFailuresTotal();
                    resultSummaries.add( new ScenarioResultSummary( totals[0],
                                                                    totals[1],
                                                                    asset.getName(),
                                                                    asset.getDescription(),
                                                                    asset.getUuid() ) );
                }
            }

            ScenarioResultSummary[] summaries = resultSummaries.toArray( new ScenarioResultSummary[resultSummaries.size()] );

            return new BulkTestRunResult( null,
                                          resultSummaries.toArray( summaries ),
                                          coverage.getPercentCovered(),
                                          coverage.getUnfiredRules() );

        } finally {
            Thread.currentThread().setContextClassLoader( originalClassloader );
        }

    }

    private HashSet<String> expectedRules(Package bin) {
        HashSet<String> h = new HashSet<String>();
        for ( int i = 0; i < bin.getRules().length; i++ ) {
            h.add( bin.getRules()[i].getName() );
        }
        return h;
    }

    //Creates a stub Work Item Handler that does nothing. A problem is that if the *real* Work Item Handler 
    //sets a Result Parameter that is used in other rules the results of running the Test Scenario could (or 
    //more likely would) be different than those expected. We can't use the *real* Work Item Handler as we 
    //have no control what code it executes unless we look into using SecurityManagers...
    private WorkItemHandler getWorkItemHandlerStub() {
        return new WorkItemHandler() {

            public void executeWorkItem(WorkItem workItem,
                                        WorkItemManager manager) {
                //Does absolute nothing, however could log execution if needed 
            }

            public void abortWorkItem(WorkItem workItem,
                                      WorkItemManager manager) {
            }

        };
    }

}
