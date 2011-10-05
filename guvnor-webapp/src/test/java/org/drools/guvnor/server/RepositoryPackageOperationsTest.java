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
import org.drools.guvnor.client.rpc.PackageConfigData;
import org.drools.guvnor.server.builder.PackageDRLAssembler;
import org.drools.guvnor.server.util.BRMSSuggestionCompletionLoader;
import org.drools.guvnor.server.util.DroolsHeader;
import org.drools.repository.AssetItem;
import org.drools.repository.PackageItem;
import org.drools.repository.RulesRepository;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.google.gwt.user.client.rpc.SerializationException;

public class RepositoryPackageOperationsTest {

    // TODO this entire test must be rewritten to extend GuvnorTestBase and test it for real

    private final RulesRepository             rulesRepository             = mock( RulesRepository.class );
    private final RepositoryPackageOperations repositoryPackageOperations = new RepositoryPackageOperations();

    @Before
    public void setUp() {
        repositoryPackageOperations.setRulesRepositoryForTest(rulesRepository);
    }

    @Test
    public void testPackageNameSorting() {
        PackageConfigData c1 = new PackageConfigData( "org.foo" );
        PackageConfigData c2 = new PackageConfigData( "org.foo.bar" );

        List<PackageConfigData> ls = new ArrayList<PackageConfigData>();
        ls.add( c2 );
        ls.add( c1 );
        this.repositoryPackageOperations.sortPackages( ls );
        assertEquals( c1,
                      ls.get( 0 ) );
        assertEquals( c2,
                      ls.get( 1 ) );
    }

    @Test
    public void testLoadGlobalPackageAndDependenciesAreNotFetched() {

        PackageItem packageItem = mock( PackageItem.class );
        when( this.rulesRepository.loadGlobalArea() ).thenReturn( packageItem );
        prepareMockForPackageConfigDataFactory( packageItem );
        assertNull( this.repositoryPackageOperations.loadGlobalPackage().getDependencies() );

    }

    @Test
    public void testLoadGlobalPackageAndIsSnapshot() {
        PackageItem packageItem = mock( PackageItem.class );
        when( this.rulesRepository.loadGlobalArea() ).thenReturn( packageItem );
        preparePackageItemMockDates( packageItem );
        when( packageItem.isSnapshot() ).thenReturn( true );
        when( packageItem.getSnapshotName() ).thenReturn( "snapshotName123" );
        assertTrue( this.repositoryPackageOperations.loadGlobalPackage().isSnapshot() );
        assertEquals( this.repositoryPackageOperations.loadGlobalPackage().getSnapshotName(),
                      "snapshotName123" );

    }

    @Test
    public void testLoadGlobalPackageAndIsNotSnapshot() {
        PackageItem packageItem = mock( PackageItem.class );
        when( this.rulesRepository.loadGlobalArea() ).thenReturn( packageItem );
        preparePackageItemMockDates( packageItem );
        when( packageItem.isSnapshot() ).thenReturn( false );
        when( packageItem.getSnapshotName() ).thenReturn( "snapshotName123" );
        assertFalse( this.repositoryPackageOperations.loadGlobalPackage().isSnapshot() );
        assertNull( this.repositoryPackageOperations.loadGlobalPackage().getSnapshotName() );
    }

    @Test
    public void testCopyPackage() throws SerializationException {
        initSession();
        repositoryPackageOperations.copyPackage( "from",
                                                 "to" );
        verify( rulesRepository ).copyPackage( "from",
                                               "to" );
    }

    @Test
    public void testRemovePackage() throws SerializationException {
        initSession();
        PackageItem packageItem = mock( PackageItem.class );
        when( this.rulesRepository.loadPackageByUUID( "uuid" ) ).thenReturn( packageItem );
        this.repositoryPackageOperations.removePackage( "uuid" );
        verify( packageItem ).remove();
        verify( rulesRepository ).save();
    }

    @Test
    public void testRenamePackage() throws SerializationException {
        initSession();
        this.repositoryPackageOperations.renamePackage( "old",
                                                        "new" );
        verify( this.rulesRepository ).renamePackage( "old",
                                                      "new" );
    }

    @Test
    public void testExportPackages() throws PathNotFoundException,
                                    IOException,
                                    RepositoryException {
        initSession();
        this.repositoryPackageOperations.exportPackages( "packageName" );
        verify( this.rulesRepository ).dumpPackageFromRepositoryXml( "packageName" );
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
        PackageItem packageItem = mock( PackageItem.class );
        when( packageItem.getUUID() ).thenReturn( "uuid" );
        when( this.rulesRepository.createPackage( "name",
                                                  "description",
                                                  "package",
                                                  new String[]{"workspace"} ) ).thenReturn( packageItem );
        assertEquals( this.repositoryPackageOperations.createPackage( "name",
                                                                      "description",
                                                                      "package",
                                                                      new String[]{"workspace"} ),
                      "uuid" );
        verify( this.rulesRepository ).createPackage( "name",
                                                      "description",
                                                      "package",
                                                      new String[]{"workspace"} );

    }

    @Test
    public void testSubCreatePackage() throws SerializationException {
        initSession();
        PackageItem packageItem = mock( PackageItem.class );
        when( packageItem.getUUID() ).thenReturn( "uuid" );
        when( this.rulesRepository.createSubPackage( "name",
                                                     "description",
                                                     "parentNode" ) ).thenReturn( packageItem );
        assertEquals( this.repositoryPackageOperations.createSubPackage( "name",
                                                                         "description",
                                                                         "parentNode" ),
                      "uuid" );
        verify( this.rulesRepository ).createSubPackage( "name",
                                                         "description",
                                                         "parentNode" );

    }

    @Test
    public void testLoadPackageConfigWithDependencies() {
        PackageItem packageItem = mock( PackageItem.class );
        when( this.rulesRepository.loadGlobalArea() ).thenReturn( packageItem );
        prepareMockForPackageConfigDataFactory( packageItem );
        assertNotNull( this.repositoryPackageOperations.loadPackageConfig( packageItem ).getDependencies() );
    }

    @Test
    public void testSavePackageArhived() throws SerializationException {
        RepositoryPackageOperations localRepositoryPackageOperations = initSpyingOnRealRepositoryPackageOperations();

        PackageConfigData packageConfigData = createPackageConfigData( false );
        PackageItem packageItem = mock( PackageItem.class );
        Calendar calendar = GregorianCalendar.getInstance();
        when( packageItem.getLastModified() ).thenReturn( calendar );
        initDroolsHeaderCheck( packageItem );
        when( packageItem.isArchived() ).thenReturn( true );
        when( this.rulesRepository.loadPackage( packageConfigData.getName() ) ).thenReturn( packageItem );
        doNothing().when( localRepositoryPackageOperations ).updateCategoryRules( packageConfigData,
                                                                                  packageItem );
        doNothing().when( localRepositoryPackageOperations ).handleUnarchivedForSavePackage( packageConfigData,
                                                                                             packageItem,
                                                                                             calendar );
        initSpyingAndMockingOnSuggestionCompletionLoader( localRepositoryPackageOperations );
        localRepositoryPackageOperations.savePackage( packageConfigData );
        verify( packageItem ).updateExternalURI( packageConfigData.getExternalURI() );
        verify( packageItem ).updateDescription( packageConfigData.getDescription() );
        verify( packageItem ).archiveItem( packageConfigData.isArchived() );
        verify( packageItem ).checkin( packageConfigData.getDescription() );
        verify( localRepositoryPackageOperations ).handleUnarchivedForSavePackage( packageConfigData,
                                                                                   packageItem,
                                                                                   calendar );
    }

    @Test
    public void testSavePackageUnarhived() throws SerializationException {
        RepositoryPackageOperations localRepositoryPackageOperations = initSpyingOnRealRepositoryPackageOperations();

        PackageConfigData packageConfigData = createPackageConfigData( true );

        PackageItem packageItem = mock( PackageItem.class );
        initDroolsHeaderCheck( packageItem );
        when( packageItem.isArchived() ).thenReturn( false );
        when( this.rulesRepository.loadPackage( packageConfigData.getName() ) ).thenReturn( packageItem );
        doNothing().when( localRepositoryPackageOperations ).updateCategoryRules( packageConfigData,
                                                                                  packageItem );
        doNothing().when( localRepositoryPackageOperations ).handleArchivedForSavePackage( packageConfigData,
                                                                                             packageItem );
        initSpyingAndMockingOnSuggestionCompletionLoader( localRepositoryPackageOperations );
        localRepositoryPackageOperations.savePackage( packageConfigData );
        verify( packageItem ).updateExternalURI( packageConfigData.getExternalURI() );
        verify( packageItem ).updateDescription( packageConfigData.getDescription() );
        verify( packageItem ).archiveItem( packageConfigData.isArchived() );
        verify( packageItem ).checkin( packageConfigData.getDescription() );
        verify( localRepositoryPackageOperations ).handleArchivedForSavePackage( packageConfigData,
                                                                                   packageItem );
    }
    
    @Test
    public void testValidatePackageConfiguration() throws SerializationException {
        RepositoryPackageOperations localRepositoryPackageOperations = initSpyingOnRealRepositoryPackageOperations();

        PackageConfigData packageConfigData = createPackageConfigData( true );

        PackageItem packageItem = mock( PackageItem.class );
        initDroolsHeaderCheck( packageItem );
        when( packageItem.isArchived() ).thenReturn( false );
        when( this.rulesRepository.loadPackage( packageConfigData.getName() ) ).thenReturn( packageItem );
        doNothing().when( localRepositoryPackageOperations ).updateCategoryRules( packageConfigData,
                                                                                  packageItem );
        doNothing().when( localRepositoryPackageOperations ).handleArchivedForSavePackage( packageConfigData,
                                                                                             packageItem );
        initSpyingAndMockingOnSuggestionCompletionLoader( localRepositoryPackageOperations );
        localRepositoryPackageOperations.validatePackageConfiguration( packageConfigData );
        verify( packageItem, never() ).updateExternalURI( "");
        verify( packageItem, never() ).updateDescription( packageConfigData.getDescription() );
        verify( packageItem, never() ).archiveItem( packageConfigData.isArchived() );
        verify( packageItem, never() ).checkin( packageConfigData.getDescription() );
        verify( localRepositoryPackageOperations, never() ).handleArchivedForSavePackage( packageConfigData,
                                                                                   packageItem );
    }

    @Test
    public void testCreatePackageSnapshotAndReplacingExisting() {
        initSession();
        final String packageName = "packageName";
        final String snapshotName = "snapshotName";
        final String comment = "comment";

        PackageItem packageItem = mock( PackageItem.class );
        when( this.rulesRepository.loadPackageSnapshot( packageName,
                                                        snapshotName ) ).thenReturn( packageItem );
        this.repositoryPackageOperations.createPackageSnapshot( packageName,
                                                                snapshotName,
                                                                true,
                                                                comment );
        verify( this.rulesRepository ).removePackageSnapshot( packageName,
                                                              snapshotName );
        verify( this.rulesRepository ).createPackageSnapshot( packageName,
                                                              snapshotName );
        verify( packageItem ).updateCheckinComment( comment );

    }

    @Test
    public void testCreatePackageSnapshotAndNotReplacingExisting() {
        initSession();
        final String packageName = "packageName";
        final String snapshotName = "snapshotName";
        final String comment = "comment";

        PackageItem packageItem = mock( PackageItem.class );
        when( this.rulesRepository.loadPackageSnapshot( packageName,
                                                        snapshotName ) ).thenReturn( packageItem );
        this.repositoryPackageOperations.createPackageSnapshot( packageName,
                                                                snapshotName,
                                                                false,
                                                                comment );
        verify( this.rulesRepository,
                never() ).removePackageSnapshot( packageName,
                                                              snapshotName );
        verify( this.rulesRepository ).createPackageSnapshot( packageName,
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
        verify( this.rulesRepository ).removePackageSnapshot( packageName,
                                                              snapshotName );
        verify( this.rulesRepository,
                Mockito.never() ).copyPackageSnapshot( packageName,
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
                Mockito.never() ).removePackageSnapshot( packageName,
                                                              snapshotName );
        verify( this.rulesRepository ).copyPackageSnapshot( packageName,
                                                            snapshotName,
                                                            newSnapshotName );
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testListRulesInPackageAndtDRLMissing() throws DroolsParserException,
                                                      SerializationException {
        RepositoryPackageOperations localRepositoryPackageOperations = initSpyingOnRealRepositoryPackageOperations();
        final String packageName = "packageName";
        PackageItem packageItem = mock( PackageItem.class );
        when( this.rulesRepository.loadPackage( packageName ) ).thenReturn( packageItem );
        PackageDRLAssembler contentPackageAssembler = mock( PackageDRLAssembler.class );
        doReturn( contentPackageAssembler ).when( localRepositoryPackageOperations ).createPackageDRLAssembler(packageItem);
        //doNothing().when( localRepositoryPackageOperations ).parseRulesToPackageList( contentPackageAssembler, new ArrayList<String>() );
        when( contentPackageAssembler.getDRL() ).thenReturn( null );
        assertArrayEquals( localRepositoryPackageOperations.listRulesInPackage( packageName ),
                           new String[]{} );
        verify( localRepositoryPackageOperations,
                never() ).parseRulesToPackageList( Mockito.any( PackageDRLAssembler.class ),
                                                   Mockito.anyList() );

    }

    @SuppressWarnings("unchecked")
    @Test
    public void testListRulesInPackageAndtDRLIsNotMissing() throws DroolsParserException,
                                                           SerializationException {
        RepositoryPackageOperations localRepositoryPackageOperations = initSpyingOnRealRepositoryPackageOperations();
        final String packageName = "packageName";
        PackageItem packageItem = mock( PackageItem.class );
        when( this.rulesRepository.loadPackage( packageName ) ).thenReturn( packageItem );
        PackageDRLAssembler contentPackageAssembler = mock( PackageDRLAssembler.class );
        doReturn( contentPackageAssembler ).when( localRepositoryPackageOperations ).createPackageDRLAssembler( packageItem );
        doNothing().when( localRepositoryPackageOperations ).parseRulesToPackageList( contentPackageAssembler,
                                                                                      new ArrayList<String>() );
        when( contentPackageAssembler.getDRL() ).thenReturn( "DRL" );
        assertArrayEquals( localRepositoryPackageOperations.listRulesInPackage( packageName ),
                           new String[]{} );
        verify( localRepositoryPackageOperations ).parseRulesToPackageList( Mockito.any( PackageDRLAssembler.class ),
                                                                            Mockito.anyList() );

    }

    private void initSpyingAndMockingOnSuggestionCompletionLoader(RepositoryPackageOperations localRepositoryPackageOperations) {
        BRMSSuggestionCompletionLoader suggestionCompletionLoader = mock( BRMSSuggestionCompletionLoader.class );
        doReturn( suggestionCompletionLoader ).when( localRepositoryPackageOperations ).createBRMSSuggestionCompletionLoader();
    }

    private PackageConfigData createPackageConfigData(boolean isArchived) {
        PackageConfigData packageConfigData = new PackageConfigData();
        packageConfigData.setName( "name" );
        packageConfigData.setHeader( "header" );
        packageConfigData.setArchived( isArchived );
        packageConfigData.setDescription( "description" );
        packageConfigData.setExternalURI( "externalUri" );
        return packageConfigData;
    }

    private RepositoryPackageOperations initSpyingOnRealRepositoryPackageOperations() {
        RepositoryPackageOperations localRepositoryPackageOperations;
        localRepositoryPackageOperations = spy( this.repositoryPackageOperations );
        localRepositoryPackageOperations.setRulesRepositoryForTest(rulesRepository);
        initSession();
        return localRepositoryPackageOperations;
    }

    private void initDroolsHeaderCheck(PackageItem packageItem) {
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

    private void prepareMockForPackageConfigDataFactory(PackageItem packageItem) {
        preparePackageItemMockDates( packageItem );
        when( packageItem.getDependencies() ).thenReturn( new String[]{"dependency"} );
    }

    private void preparePackageItemMockDates(PackageItem packageItem) {
        when( packageItem.getLastModified() ).thenReturn( GregorianCalendar.getInstance() );
        when( packageItem.getCreatedDate() ).thenReturn( GregorianCalendar.getInstance() );
    }

}
