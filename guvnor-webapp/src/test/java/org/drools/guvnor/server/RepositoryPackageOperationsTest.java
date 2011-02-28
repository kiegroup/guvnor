package org.drools.guvnor.server;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.List;

import javax.jcr.Session;

import org.drools.guvnor.client.rpc.PackageConfigData;
import org.drools.repository.PackageItem;
import org.drools.repository.RulesRepository;
import org.junit.Before;
import org.junit.Test;

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
        when( packageItem.getLastModified() ).thenReturn( GregorianCalendar.getInstance() );
        when( packageItem.getCreatedDate() ).thenReturn( GregorianCalendar.getInstance() );
        when( packageItem.getDependencies() ).thenReturn( new String[]{"dependency"} );
        assertNull( this.repositoryPackageOperations.loadGlobalPackage().dependencies );

    }

    @Test
    public void testLoadGlobalPackageAndIsSnapshot() {
        PackageItem packageItem = mock( PackageItem.class );
        when( this.rulesRepository.loadGlobalArea() ).thenReturn( packageItem );
        when( packageItem.getLastModified() ).thenReturn( GregorianCalendar.getInstance() );
        when( packageItem.getCreatedDate() ).thenReturn( GregorianCalendar.getInstance() );
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
        when( packageItem.getLastModified() ).thenReturn( GregorianCalendar.getInstance() );
        when( packageItem.getCreatedDate() ).thenReturn( GregorianCalendar.getInstance() );
        when( packageItem.isSnapshot() ).thenReturn( false );
        when( packageItem.getSnapshotName() ).thenReturn( "snapshotName123" );
        assertFalse( this.repositoryPackageOperations.loadGlobalPackage().isSnapshot );
        assertNull( this.repositoryPackageOperations.loadGlobalPackage().snapshotName );
    }

    @Test
    public void testCopyPackage() throws SerializationException {
        Session session = mock( Session.class );
        when( this.rulesRepository.getSession() ).thenReturn( session );
        repositoryPackageOperations.copyPackage( "from",
                                                 "to" );
        verify( rulesRepository ).copyPackage( "from",
                                               "to" );
    }

    @Test
    public void testRemovePackage() throws SerializationException {
        Session session = mock( Session.class );
        when( this.rulesRepository.getSession() ).thenReturn( session );
        PackageItem packageItem = mock( PackageItem.class );
        when( this.rulesRepository.loadPackageByUUID( "uuid" ) ).thenReturn( packageItem );
        this.repositoryPackageOperations.removePackage( "uuid" );
        verify( packageItem ).remove();
        verify( rulesRepository ).save();
    }

    @Test
    public void testRenamePackage() throws SerializationException {
        Session session = mock( Session.class );
        when( this.rulesRepository.getSession() ).thenReturn( session );
        repositoryPackageOperations.renamePackage( "old",
                                                   "new" );
        verify( this.rulesRepository ).renamePackage( "old",
                                                      "new" );
    }

}
