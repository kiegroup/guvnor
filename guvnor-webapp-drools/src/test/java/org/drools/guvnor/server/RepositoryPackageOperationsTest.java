package org.drools.guvnor.server;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

import javax.jcr.PathNotFoundException;
import javax.jcr.RepositoryException;
import javax.jcr.Session;

import org.drools.compiler.DroolsParserException;
import org.drools.guvnor.client.rpc.Module;
import org.drools.guvnor.server.builder.PackageAssembler;
import org.drools.guvnor.server.util.BRMSSuggestionCompletionLoader;
import org.drools.guvnor.server.util.DroolsHeader;
import org.drools.repository.AssetItem;
import org.drools.repository.ModuleItem;
import org.drools.repository.RulesRepository;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.Mockito;

import com.google.gwt.user.client.rpc.SerializationException;

public class RepositoryPackageOperationsTest {

    // TODO this entire test must be rewritten to extend GuvnorTestBase and test it for real

    private final RulesRepository             rulesRepository             = mock( RulesRepository.class );
    private final RepositoryModuleOperations repositoryPackageOperations = new RepositoryModuleOperations();

    @Before
    public void setUp() {
        repositoryPackageOperations.setRulesRepositoryForTest(rulesRepository);
    }

    @Test
    public void testPackageNameSorting() {
        Module c1 = new Module( "org.foo" );
        Module c2 = new Module( "org.foo.bar" );

        List<Module> ls = new ArrayList<Module>();
        ls.add( c2 );
        ls.add( c1 );
        this.repositoryPackageOperations.sortModules( ls );
        assertEquals( c1,
                      ls.get( 0 ) );
        assertEquals( c2,
                      ls.get( 1 ) );
    }

    @Test
    public void testLoadGlobalPackageAndDependenciesAreNotFetched() {

        ModuleItem packageItem = mock( ModuleItem.class );
        when( this.rulesRepository.loadGlobalArea() ).thenReturn( packageItem );
        prepareMockForPackageConfigDataFactory( packageItem );
        assertNull( this.repositoryPackageOperations.loadGlobalModule().getDependencies() );

    }

    @Test
    public void testLoadGlobalPackageAndIsSnapshot() {
        ModuleItem packageItem = mock( ModuleItem.class );
        when( this.rulesRepository.loadGlobalArea() ).thenReturn( packageItem );
        preparePackageItemMockDates( packageItem );
        when( packageItem.isSnapshot() ).thenReturn( true );
        when( packageItem.getSnapshotName() ).thenReturn( "snapshotName123" );
        assertTrue( this.repositoryPackageOperations.loadGlobalModule().isSnapshot() );
        assertEquals( this.repositoryPackageOperations.loadGlobalModule().getSnapshotName(),
                      "snapshotName123" );

    }

    @Test
    public void testLoadGlobalPackageAndIsNotSnapshot() {
        ModuleItem packageItem = mock( ModuleItem.class );
        when( this.rulesRepository.loadGlobalArea() ).thenReturn( packageItem );
        preparePackageItemMockDates( packageItem );
        when( packageItem.isSnapshot() ).thenReturn( false );
        when( packageItem.getSnapshotName() ).thenReturn( "snapshotName123" );
        assertFalse( this.repositoryPackageOperations.loadGlobalModule().isSnapshot() );
        assertNull( this.repositoryPackageOperations.loadGlobalModule().getSnapshotName() );
    }

    @Test
    public void testCopyPackage() throws SerializationException {
        initSession();
        repositoryPackageOperations.copyModules( "from",
                                                 "to" );
        verify( rulesRepository ).copyModule( "from",
                                               "to" );
    }

    @Test
    public void testRemovePackage() throws SerializationException {
        initSession();
        ModuleItem packageItem = mock( ModuleItem.class );
        when( this.rulesRepository.loadModuleByUUID( "uuid" ) ).thenReturn( packageItem );
        this.repositoryPackageOperations.removeModule( "uuid" );
        verify( packageItem ).remove();
        verify( rulesRepository ).save();
    }

    @Test
    public void testRenamePackage() throws SerializationException {
        initSession();
        this.repositoryPackageOperations.renameModule( "old",
                                                        "new" );
        verify( this.rulesRepository ).renameModule( "old",
                                                      "new" );
    }

    @Test
    public void testExportPackages() throws PathNotFoundException,
                                    IOException,
                                    RepositoryException {
        initSession();
        this.repositoryPackageOperations.exportModules( "packageName" );
        verify( this.rulesRepository ).dumpModuleFromRepositoryXml( "packageName" );
    }

    @Test
    public void testImportPackages() {
        this.repositoryPackageOperations.importPackages( new byte[]{},
                                                         false );
        verify( this.rulesRepository ).importPackageToRepository( new byte[]{},
                                                                  false );
    }

    @Test
    public void testCreatePackage() {
        initSession();
        ModuleItem packageItem = mock( ModuleItem.class );
        when( packageItem.getUUID() ).thenReturn( "uuid" );
        when( this.rulesRepository.createModule( "name",
                                                  "description",
                                                  "package",
                                                  new String[]{"workspace"} ) ).thenReturn( packageItem );
        assertEquals( this.repositoryPackageOperations.createModule( "name",
                                                                      "description",
                                                                      "package",
                                                                      new String[]{"workspace"} ),
                      "uuid" );
        verify( this.rulesRepository ).createModule( "name",
                                                      "description",
                                                      "package",
                                                      new String[]{"workspace"} );

    }

    @Test
    public void testSubCreatePackage() throws SerializationException {
        initSession();
        ModuleItem packageItem = mock( ModuleItem.class );
        when( packageItem.getUUID() ).thenReturn( "uuid" );
        when( this.rulesRepository.createSubModule( "name",
                                                     "description",
                                                     "parentNode" ) ).thenReturn( packageItem );
        assertEquals( this.repositoryPackageOperations.createSubModule( "name",
                                                                         "description",
                                                                         "parentNode" ),
                      "uuid" );
        verify( this.rulesRepository ).createSubModule( "name",
                                                         "description",
                                                         "parentNode" );

    }

    @Test
    public void testLoadPackageConfigWithDependencies() {
        ModuleItem packageItem = mock( ModuleItem.class );
        when( this.rulesRepository.loadGlobalArea() ).thenReturn( packageItem );
        prepareMockForPackageConfigDataFactory( packageItem );
        assertNotNull( this.repositoryPackageOperations.loadModule( packageItem ).getDependencies() );
    }

    @Test
    public void testSavePackageArhived() throws SerializationException {
        RepositoryModuleOperations localRepositoryPackageOperations = initSpyingOnRealRepositoryPackageOperations();

        Module packageConfigData = createPackageConfigData( false );
        ModuleItem packageItem = mock( ModuleItem.class );
        Calendar calendar = GregorianCalendar.getInstance();
        when( packageItem.getLastModified() ).thenReturn( calendar );
        initDroolsHeaderCheck( packageItem );
        when( packageItem.isArchived() ).thenReturn( true );
        when( this.rulesRepository.loadModule( packageConfigData.getName() ) ).thenReturn( packageItem );
        doNothing().when( localRepositoryPackageOperations ).updateCategoryRules( packageConfigData,
                                                                                  packageItem );
        doNothing().when( localRepositoryPackageOperations ).handleUnarchivedForSaveModule( packageConfigData,
                                                                                             packageItem,
                                                                                             calendar );
        initSpyingAndMockingOnSuggestionCompletionLoader( localRepositoryPackageOperations );
        localRepositoryPackageOperations.saveModule( packageConfigData );
        verify( packageItem ).updateExternalURI( packageConfigData.getExternalURI() );
        verify( packageItem ).updateDescription( packageConfigData.getDescription() );
        verify( packageItem ).archiveItem( packageConfigData.isArchived() );
        verify( packageItem ).checkin( packageConfigData.getDescription() );
        verify( localRepositoryPackageOperations ).handleUnarchivedForSaveModule( packageConfigData,
                                                                                   packageItem,
                                                                                   calendar );
    }

    @Test
    public void testSavePackageUnarhived() throws SerializationException {
        RepositoryModuleOperations localRepositoryPackageOperations = initSpyingOnRealRepositoryPackageOperations();

        Module packageConfigData = createPackageConfigData( true );

        ModuleItem packageItem = mock( ModuleItem.class );
        initDroolsHeaderCheck( packageItem );
        when( packageItem.isArchived() ).thenReturn( false );
        when( this.rulesRepository.loadModule( packageConfigData.getName() ) ).thenReturn( packageItem );
        doNothing().when( localRepositoryPackageOperations ).updateCategoryRules( packageConfigData,
                                                                                  packageItem );
        doNothing().when( localRepositoryPackageOperations ).handleArchivedForSaveModule( packageConfigData,
                                                                                             packageItem );
        initSpyingAndMockingOnSuggestionCompletionLoader( localRepositoryPackageOperations );
        localRepositoryPackageOperations.saveModule( packageConfigData );
        verify( packageItem ).updateExternalURI( packageConfigData.getExternalURI() );
        verify( packageItem ).updateDescription( packageConfigData.getDescription() );
        verify( packageItem ).archiveItem( packageConfigData.isArchived() );
        verify( packageItem ).checkin( packageConfigData.getDescription() );
        verify( localRepositoryPackageOperations ).handleArchivedForSaveModule( packageConfigData,
                                                                                   packageItem );
    }
    
    @Test
    public void testValidatePackageConfiguration() throws SerializationException {
        RepositoryModuleOperations localRepositoryPackageOperations = initSpyingOnRealRepositoryPackageOperations();

        Module packageConfigData = createPackageConfigData( true );

        ModuleItem packageItem = mock( ModuleItem.class );
        initDroolsHeaderCheck( packageItem );
        when( packageItem.isArchived() ).thenReturn( false );
        when( this.rulesRepository.loadModule( packageConfigData.getName() ) ).thenReturn( packageItem );
        doNothing().when( localRepositoryPackageOperations ).updateCategoryRules( packageConfigData,
                                                                                  packageItem );
        doNothing().when( localRepositoryPackageOperations ).handleArchivedForSaveModule( packageConfigData,
                                                                                             packageItem );
        initSpyingAndMockingOnSuggestionCompletionLoader( localRepositoryPackageOperations );
        localRepositoryPackageOperations.validateModule( packageConfigData );
        verify( packageItem, never() ).updateExternalURI( "");
        verify( packageItem, never() ).updateDescription( packageConfigData.getDescription() );
        verify( packageItem, never() ).archiveItem( packageConfigData.isArchived() );
        verify( packageItem, never() ).checkin( packageConfigData.getDescription() );
        verify( localRepositoryPackageOperations, never() ).handleArchivedForSaveModule( packageConfigData,
                                                                                   packageItem );
    }

    @Test
    public void testCreatePackageSnapshotAndReplacingExisting() {
        initSession();
        final String packageName = "packageName";
        final String snapshotName = "snapshotName";
        final String comment = "comment";

        ModuleItem packageItem = mock( ModuleItem.class );
        when( this.rulesRepository.loadModuleSnapshot( packageName,
                                                        snapshotName ) ).thenReturn( packageItem );
        this.repositoryPackageOperations.createModuleSnapshot( packageName,
                                                                snapshotName,
                                                                true,
                                                                comment );
        verify( this.rulesRepository ).removeModuleSnapshot( packageName,
                                                              snapshotName );
        verify( this.rulesRepository ).createModuleSnapshot( packageName,
                                                              snapshotName );
        verify( packageItem ).updateCheckinComment( comment );

    }

    @Test
    public void testCreatePackageSnapshotAndNotReplacingExisting() {
        initSession();
        final String packageName = "packageName";
        final String snapshotName = "snapshotName";
        final String comment = "comment";

        ModuleItem packageItem = mock( ModuleItem.class );
        when( this.rulesRepository.loadModuleSnapshot( packageName,
                                                        snapshotName ) ).thenReturn( packageItem );
        this.repositoryPackageOperations.createModuleSnapshot( packageName,
                                                                snapshotName,
                                                                false,
                                                                comment );
        verify( this.rulesRepository,
                never() ).removeModuleSnapshot( packageName,
                                                              snapshotName );
        verify( this.rulesRepository ).createModuleSnapshot( packageName,
                                                              snapshotName );
        verify( packageItem ).updateCheckinComment( comment );

    }

    @Test
    public void testCopyOrRemoveSnapshotAndRemoving() throws SerializationException {
        initSession();
        final String packageName = "packageName";
        final String snapshotName = "snapshotName";
        final String newSnapshotName = "newSnapshotName";
        this.repositoryPackageOperations.copyOrRemoveSnapshot( packageName,
                                                               snapshotName,
                                                               true,
                                                               newSnapshotName );
        verify( this.rulesRepository ).removeModuleSnapshot( packageName,
                                                              snapshotName );
        verify( this.rulesRepository,
                Mockito.never() ).copyModuleSnapshot( packageName,
                                                       snapshotName,
                                                       newSnapshotName );
    }

    @Test
    public void testCopyOrRemoveSnapshotAndCopying() throws SerializationException {
        initSession();
        final String packageName = "packageName";
        final String snapshotName = "snapshotName";
        final String newSnapshotName = "newSnapshotName";
        this.repositoryPackageOperations.copyOrRemoveSnapshot( packageName,
                                                               snapshotName,
                                                               false,
                                                               newSnapshotName );
        verify( this.rulesRepository,
                Mockito.never() ).removeModuleSnapshot( packageName,
                                                              snapshotName );
        verify( this.rulesRepository ).copyModuleSnapshot( packageName,
                                                            snapshotName,
                                                            newSnapshotName );
    }

    @SuppressWarnings("unchecked")
    @Test
    @Ignore("This test does not test anything useful.Move the test to Arquillian or remove this test.")
    public void testListRulesInPackageAndtDRLMissing() throws DroolsParserException,
                                                      SerializationException {
        RepositoryModuleOperations localRepositoryPackageOperations = initSpyingOnRealRepositoryPackageOperations();
        final String packageName = "packageName";
        ModuleItem packageItem = mock( ModuleItem.class );
        when( this.rulesRepository.loadModule( packageName ) ).thenReturn( packageItem );
        PackageAssembler contentPackageAssembler = mock( PackageAssembler.class );
        //doReturn( contentPackageAssembler ).when( localRepositoryPackageOperations ).createPackageDRLAssembler(packageItem);
        //doNothing().when( localRepositoryPackageOperations ).parseRulesToPackageList( contentPackageAssembler, new ArrayList<String>() );
        when( contentPackageAssembler.getCompiledSource() ).thenReturn( null );
        assertArrayEquals( localRepositoryPackageOperations.listRulesInPackage( packageName ),
                           new String[]{} );
        verify( localRepositoryPackageOperations,
                never() ).parseRulesToPackageList( Mockito.any( PackageAssembler.class ),
                                                   Mockito.anyList() );

    }

    @SuppressWarnings("unchecked")
    @Test
    @Ignore("This test does not test anything useful.Move the test to Arquillian or remove this test.")
    public void testListRulesInPackageAndtDRLIsNotMissing() throws DroolsParserException,
                                                           SerializationException {
        RepositoryModuleOperations localRepositoryPackageOperations = initSpyingOnRealRepositoryPackageOperations();
        final String packageName = "packageName";
        ModuleItem packageItem = mock( ModuleItem.class );
        when( this.rulesRepository.loadModule( packageName ) ).thenReturn( packageItem );
        PackageAssembler contentPackageAssembler = mock( PackageAssembler.class );
        //doReturn( contentPackageAssembler ).when( localRepositoryPackageOperations ).createPackageDRLAssembler( packageItem );
        doNothing().when( localRepositoryPackageOperations ).parseRulesToPackageList( contentPackageAssembler,
                                                                                      new ArrayList<String>() );
        when( contentPackageAssembler.getCompiledSource() ).thenReturn( "DRL" );
        assertArrayEquals( localRepositoryPackageOperations.listRulesInPackage( packageName ),
                           new String[]{} );
        verify( localRepositoryPackageOperations ).parseRulesToPackageList( Mockito.any( PackageAssembler.class ),
                                                                            Mockito.anyList() );

    }

    private void initSpyingAndMockingOnSuggestionCompletionLoader(RepositoryModuleOperations localRepositoryPackageOperations) {
        BRMSSuggestionCompletionLoader suggestionCompletionLoader = mock( BRMSSuggestionCompletionLoader.class );
        doReturn( suggestionCompletionLoader ).when( localRepositoryPackageOperations ).createBRMSSuggestionCompletionLoader();
    }

    private Module createPackageConfigData(boolean isArchived) {
        Module packageConfigData = new Module();
        packageConfigData.setName( "name" );
        packageConfigData.setHeader( "header" );
        packageConfigData.setArchived( isArchived );
        packageConfigData.setDescription( "description" );
        packageConfigData.setExternalURI( "externalUri" );
        return packageConfigData;
    }

    private RepositoryModuleOperations initSpyingOnRealRepositoryPackageOperations() {
        RepositoryModuleOperations localRepositoryPackageOperations;
        localRepositoryPackageOperations = spy( this.repositoryPackageOperations );
        localRepositoryPackageOperations.setRulesRepositoryForTest(rulesRepository);
        initSession();
        return localRepositoryPackageOperations;
    }

    private void initDroolsHeaderCheck(ModuleItem packageItem) {
        AssetItem assetItem = mock( AssetItem.class );
        when( packageItem.containsAsset( "drools" ) ).thenReturn( false );
        when( packageItem.addAsset( "drools",
                                    "" ) ).thenReturn( assetItem );
        DroolsHeader.updateDroolsHeader( "expected",
                                         packageItem );
        verify( packageItem ).addAsset( "drools",
                                        "" );
        verify( assetItem ).updateFormat( "package" );
        verify( assetItem ).updateContent( "expected" );
        verify( assetItem ).checkin( "" );
    }

    private void initSession() {
        Session session = mock( Session.class );
        when( this.rulesRepository.getSession() ).thenReturn( session );
    }

    private void prepareMockForPackageConfigDataFactory(ModuleItem packageItem) {
        preparePackageItemMockDates( packageItem );
        when( packageItem.getDependencies() ).thenReturn( new String[]{"dependency"} );
    }

    private void preparePackageItemMockDates(ModuleItem packageItem) {
        when( packageItem.getLastModified() ).thenReturn( GregorianCalendar.getInstance() );
        when( packageItem.getCreatedDate() ).thenReturn( GregorianCalendar.getInstance() );
    }

}
