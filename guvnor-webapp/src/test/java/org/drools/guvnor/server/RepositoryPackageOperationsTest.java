package org.drools.guvnor.server;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
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

import org.drools.guvnor.client.rpc.PackageConfigData;
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

    private final RulesRepository             rulesRepository             = mock( RulesRepository.class );
    private final RepositoryPackageOperations repositoryPackageOperations = new RepositoryPackageOperations();

    @Before
    public void setUp() {
        repositoryPackageOperations.setRulesRepository( rulesRepository );
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
        assertNull( this.repositoryPackageOperations.loadGlobalPackage().dependencies );

    }

    @Test
    public void testLoadGlobalPackageAndIsSnapshot() {
        PackageItem packageItem = mock( PackageItem.class );
        when( this.rulesRepository.loadGlobalArea() ).thenReturn( packageItem );
        preparePackageItemMockDates( packageItem );
        when( packageItem.isSnapshot() ).thenReturn( true );
        when( packageItem.getSnapshotName() ).thenReturn( "snapshotName123" );
        assertTrue( this.repositoryPackageOperations.loadGlobalPackage().isSnapshot );
        assertEquals( this.repositoryPackageOperations.loadGlobalPackage().snapshotName,
                      "snapshotName123" );

    }

    @Test
    public void testLoadGlobalPackageAndIsNotSnapshot() {
        PackageItem packageItem = mock( PackageItem.class );
        when( this.rulesRepository.loadGlobalArea() ).thenReturn( packageItem );
        preparePackageItemMockDates( packageItem );
        when( packageItem.isSnapshot() ).thenReturn( false );
        when( packageItem.getSnapshotName() ).thenReturn( "snapshotName123" );
        assertFalse( this.repositoryPackageOperations.loadGlobalPackage().isSnapshot );
        assertNull( this.repositoryPackageOperations.loadGlobalPackage().snapshotName );
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
                                                  new String[]{"workspace"} ) ).thenReturn( packageItem );
        assertEquals( this.repositoryPackageOperations.createPackage( "name",
                                                                      "description",
                                                                      new String[]{"workspace"} ),
                      "uuid" );
        verify( this.rulesRepository ).createPackage( "name",
                                                      "description",
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
        assertNotNull( this.repositoryPackageOperations.loadPackageConfig( packageItem ).dependencies );
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
        when( this.rulesRepository.loadPackage( packageConfigData.name ) ).thenReturn( packageItem );
        doNothing().when( localRepositoryPackageOperations ).updateCategoryRules( packageConfigData,
                                                                                  packageItem );
        doNothing().when( localRepositoryPackageOperations ).handleUnarchivedForSavePackage( packageConfigData,
                                                                                             packageItem,
                                                                                             calendar );
        initSpyingAndMockingOnSuggestionCompletionLoader( localRepositoryPackageOperations );
        localRepositoryPackageOperations.savePackage( packageConfigData );
        verify( packageItem ).updateExternalURI( packageConfigData.externalURI );
        verify( packageItem ).updateDescription( packageConfigData.description );
        verify( packageItem ).archiveItem( packageConfigData.archived );
        verify( packageItem ).checkin( packageConfigData.description );
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
        when( this.rulesRepository.loadPackage( packageConfigData.name ) ).thenReturn( packageItem );
        doNothing().when( localRepositoryPackageOperations ).updateCategoryRules( packageConfigData,
                                                                                  packageItem );
        doNothing().when( localRepositoryPackageOperations ).handleArchivedForSavePackage( packageConfigData,
                                                                                             packageItem );
        initSpyingAndMockingOnSuggestionCompletionLoader( localRepositoryPackageOperations );
        localRepositoryPackageOperations.savePackage( packageConfigData );
        verify( packageItem ).updateExternalURI( packageConfigData.externalURI );
        verify( packageItem ).updateDescription( packageConfigData.description );
        verify( packageItem ).archiveItem( packageConfigData.archived );
        verify( packageItem ).checkin( packageConfigData.description );
        verify( localRepositoryPackageOperations ).handleArchivedForSavePackage( packageConfigData,
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
                Mockito.never() ).removePackageSnapshot( packageName,
                                                              snapshotName );
        verify( this.rulesRepository ).createPackageSnapshot( packageName,
                                                              snapshotName );
        verify( packageItem ).updateCheckinComment( comment );

    }

    private void initSpyingAndMockingOnSuggestionCompletionLoader(RepositoryPackageOperations localRepositoryPackageOperations) {
        BRMSSuggestionCompletionLoader suggestionCompletionLoader = mock( BRMSSuggestionCompletionLoader.class );
        doReturn( suggestionCompletionLoader ).when( localRepositoryPackageOperations ).createBRMSSuggestionCompletionLoader();
    }

    private PackageConfigData createPackageConfigData(boolean isArchived) {
        PackageConfigData packageConfigData = new PackageConfigData();
        packageConfigData.name = "name";
        packageConfigData.header = "header";
        packageConfigData.archived = isArchived;
        packageConfigData.description = "description";
        packageConfigData.externalURI = "externalUri";
        return packageConfigData;
    }

    private RepositoryPackageOperations initSpyingOnRealRepositoryPackageOperations() {
        RepositoryPackageOperations localRepositoryPackageOperations;
        localRepositoryPackageOperations = spy( this.repositoryPackageOperations );
        localRepositoryPackageOperations.setRulesRepository( rulesRepository );
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
