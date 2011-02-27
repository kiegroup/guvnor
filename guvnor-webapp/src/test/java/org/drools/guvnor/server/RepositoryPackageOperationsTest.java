package org.drools.guvnor.server;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.List;

import org.drools.guvnor.client.rpc.PackageConfigData;
import org.drools.repository.PackageItem;
import org.drools.repository.RulesRepository;
import org.junit.Test;

public class RepositoryPackageOperationsTest {

    @Test
    public void testPackageNameSorting() {
        PackageConfigData c1 = new PackageConfigData( "org.foo" );
        PackageConfigData c2 = new PackageConfigData( "org.foo.bar" );

        List<PackageConfigData> ls = new ArrayList<PackageConfigData>();
        ls.add( c2 );
        ls.add( c1 );
        RepositoryPackageOperations repositoryPackageOperations = new RepositoryPackageOperations();
        repositoryPackageOperations.sortPackages( ls );
        assertEquals( c1,
                      ls.get( 0 ) );
        assertEquals( c2,
                      ls.get( 1 ) );
    }

    @Test
    public void testLoadGlobalPackageAndDependenciesAreNotFetched() {
        RulesRepository rulesRepository = mock( RulesRepository.class );
        RepositoryPackageOperations packageOperations = new RepositoryPackageOperations();
        packageOperations.setRulesRepository( rulesRepository );
        PackageItem packageItem = mock( PackageItem.class );
        when( rulesRepository.loadGlobalArea() ).thenReturn( packageItem );
        when( packageItem.getLastModified() ).thenReturn( GregorianCalendar.getInstance() );
        when( packageItem.getCreatedDate() ).thenReturn( GregorianCalendar.getInstance() );
        when( packageItem.getDependencies() ).thenReturn( new String[]{"dependency"} );
        assertNull( packageOperations.loadGlobalPackage().dependencies );

    }

    @Test
    public void testLoadGlobalPackageAndIsSnapshot() {
        RulesRepository rulesRepository = mock( RulesRepository.class );
        RepositoryPackageOperations packageOperations = new RepositoryPackageOperations();
        packageOperations.setRulesRepository( rulesRepository );
        PackageItem packageItem = mock( PackageItem.class );
        when( rulesRepository.loadGlobalArea() ).thenReturn( packageItem );
        when( packageItem.getLastModified() ).thenReturn( GregorianCalendar.getInstance() );
        when( packageItem.getCreatedDate() ).thenReturn( GregorianCalendar.getInstance() );
        when( packageItem.isSnapshot() ).thenReturn( true );
        when( packageItem.getSnapshotName() ).thenReturn( "snapshotName123" );
        assertTrue( packageOperations.loadGlobalPackage().isSnapshot );
        assertEquals( packageOperations.loadGlobalPackage().snapshotName,
                      "snapshotName123" );

    }

    @Test
    public void testLoadGlobalPackageAndIsNotSnapshot() {
        RulesRepository rulesRepository = mock( RulesRepository.class );
        RepositoryPackageOperations packageOperations = new RepositoryPackageOperations();
        packageOperations.setRulesRepository( rulesRepository );
        PackageItem packageItem = mock( PackageItem.class );
        when( rulesRepository.loadGlobalArea() ).thenReturn( packageItem );
        when( packageItem.getLastModified() ).thenReturn( GregorianCalendar.getInstance() );
        when( packageItem.getCreatedDate() ).thenReturn( GregorianCalendar.getInstance() );
        when( packageItem.isSnapshot() ).thenReturn( false );
        when( packageItem.getSnapshotName() ).thenReturn( "snapshotName123" );
        assertFalse( packageOperations.loadGlobalPackage().isSnapshot );
        assertNull( packageOperations.loadGlobalPackage().snapshotName );

    }

}
