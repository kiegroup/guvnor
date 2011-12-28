/*
 * Copyright 2011 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.drools.guvnor.server;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

import org.drools.guvnor.client.rpc.AssetPageRequest;
import org.drools.guvnor.client.rpc.AssetPageRow;
import org.drools.guvnor.client.rpc.PageResponse;
import org.drools.guvnor.client.rpc.TableDataResult;
import org.drools.guvnor.client.rpc.TableDataRow;
import org.drools.guvnor.server.util.AssetEditorConfiguration;
import org.drools.guvnor.server.util.AssetEditorConfigurationParser;
import org.drools.repository.AssetHistoryIterator;
import org.drools.repository.AssetItem;
import org.drools.repository.AssetItemIterator;
import org.drools.repository.ModuleItem;
import org.drools.repository.RulesRepository;
import org.junit.Test;
import org.mockito.Mockito;

import com.google.gwt.user.client.rpc.SerializationException;

public class RepositoryAssetOperationsTest {

    // TODO this entire test must be rewritten to extend GuvnorTestBase and test it for real

    @Test
    public void testRenameAsset() {
        RulesRepository rulesRepository = Mockito.mock( RulesRepository.class );
        RepositoryAssetOperations repositoryAssetOperations = new RepositoryAssetOperations();
        repositoryAssetOperations.setRulesRepositoryForTest(rulesRepository);
        when( rulesRepository.renameAsset( "uuid",
                                           "newname" ) ).thenReturn( "uuid" );
        assertEquals( repositoryAssetOperations.renameAsset( "uuid",
                                                             "newname" ),
                      "uuid" );
    }

    @Test
    public void testLoadAssetHistoryIsNull() throws SerializationException {
        RulesRepository rulesRepository = mock( RulesRepository.class );
        RepositoryAssetOperations repositoryAssetOperations = new RepositoryAssetOperations();
        repositoryAssetOperations.setRulesRepositoryForTest(rulesRepository);

        AssetItem assetItem = mock( AssetItem.class );

        AssetHistoryIterator assetHistoryIterator = mock( AssetHistoryIterator.class );
        when( assetItem.getHistory() ).thenReturn( assetHistoryIterator );
        Calendar calendar = GregorianCalendar.getInstance();
        when( assetItem.getLastModified() ).thenReturn( calendar );

        TableDataResult result = repositoryAssetOperations.loadItemHistory( assetItem );
        assertNotNull( result );
        assertEquals( 0,
                      result.data.length );
    }

    @Test
    public void testLoadAssetHistoryAndHistoryDoesNotExistsAndNullIsReturned()
                                                                              throws SerializationException {
        RulesRepository rulesRepository = mock( RulesRepository.class );
        RepositoryAssetOperations repositoryAssetOperations = new RepositoryAssetOperations();
        repositoryAssetOperations.setRulesRepositoryForTest(rulesRepository);

        AssetItem assetItem = initializeAssetItemMockForLoadAssetHistory();

        AssetHistoryIterator assetHistoryIterator = mock( AssetHistoryIterator.class );
        when( assetItem.getHistory() ).thenReturn( assetHistoryIterator );
        when( assetHistoryIterator.hasNext() ).thenReturn( true,
                                                           false );
        AssetItem historicalAssetItem = initializeAssetItemHistoryMockForLoadAssetHistory( assetHistoryIterator );
        when( historicalAssetItem.getVersionNumber() ).thenReturn( 1324567L );
        Calendar calendar = GregorianCalendar.getInstance();
        when( historicalAssetItem.getLastModified() ).thenReturn( calendar );

        assertNotNull( repositoryAssetOperations.loadItemHistory( assetItem ) );

    }

    @Test
    public void testLoadAssetHistoryAndHistoryExists()
                                                      throws SerializationException {
        RulesRepository rulesRepository = mock( RulesRepository.class );
        RepositoryAssetOperations repositoryAssetOperations = new RepositoryAssetOperations();
        repositoryAssetOperations.setRulesRepositoryForTest(rulesRepository);

        AssetItem assetItem = initializeAssetItemMockForLoadAssetHistory();

        AssetHistoryIterator assetHistoryIterator = mock( AssetHistoryIterator.class );
        when( assetItem.getHistory() ).thenReturn( assetHistoryIterator );
        when( assetHistoryIterator.hasNext() ).thenReturn( true,
                                                           false );

        AssetItem historicalAssetItem = initializeAssetItemHistoryMockForLoadAssetHistory( assetHistoryIterator );
        when( historicalAssetItem.getVersionNumber() ).thenReturn( 123456L );

        Calendar calendar = GregorianCalendar.getInstance();
        when( historicalAssetItem.getLastModified() ).thenReturn( calendar );

        TableDataResult tableDataResult = repositoryAssetOperations
                .loadItemHistory( assetItem );
        assertNotNull( tableDataResult );
        TableDataRow[] tableDataRow = tableDataResult.data;
        assertNotNull( tableDataRow );
        assertEquals( tableDataRow.length,
                      1 );
    }

    private AssetItem initializeAssetItemMockForLoadAssetHistory() {
        AssetItem assetItem = mock( AssetItem.class );
        when( assetItem.getVersionNumber() ).thenReturn( 1324567L );
        return assetItem;
    }

    private AssetItem initializeAssetItemHistoryMockForLoadAssetHistory(
                                                                        AssetHistoryIterator assetHistoryIterator) {
        AssetItem historicalAssetItem = mock( AssetItem.class );
        when( assetHistoryIterator.next() ).thenReturn( historicalAssetItem );
        return historicalAssetItem;
    }

    @Test
    public void testLoadArchivedAssetsReturnOne() throws SerializationException {
        RulesRepository rulesRepository = mock( RulesRepository.class );

        AssetItemIterator assetItemIterator = mock( AssetItemIterator.class );
        when( assetItemIterator.hasNext() ).thenReturn( true,
                                                        false );
        initializeAssetItemMockForLoadArchivedAssets( rulesRepository,
                                                      assetItemIterator );

        RepositoryAssetOperations repositoryAssetOperations = new RepositoryAssetOperations();
        repositoryAssetOperations.setRulesRepositoryForTest(rulesRepository);

        TableDataResult loadArchivedAssets = repositoryAssetOperations
                .loadArchivedAssets( 0,
                                     1 );
        assertEquals( loadArchivedAssets.data.length,
                      1 );
    }

    @Test
    public void testLoadArchivedAssetsReturnLessThanIsAvailable()
                                                                 throws SerializationException {
        RulesRepository rulesRepository = mock( RulesRepository.class );

        AssetItemIterator assetItemIterator = mock( AssetItemIterator.class );
        when( assetItemIterator.hasNext() ).thenReturn(true,
                true,
                true,
                false);
        initializeAssetItemMockForLoadArchivedAssets( rulesRepository,
                                                      assetItemIterator );

        RepositoryAssetOperations repositoryAssetOperations = new RepositoryAssetOperations();
        repositoryAssetOperations.setRulesRepositoryForTest(rulesRepository);

        TableDataResult loadArchivedAssets = repositoryAssetOperations
                .loadArchivedAssets( 0,
                                     2 );
        assertEquals( loadArchivedAssets.data.length,
                      2 );
    }

    private void initializeAssetItemMockForLoadArchivedAssets(
                                                              RulesRepository rulesRepository,
                                                              AssetItemIterator assetItemIterator) {
        AssetItem assetItem = mock( AssetItem.class );
        when( assetItem.getLastModified() ).thenReturn(
                                                        GregorianCalendar.getInstance() );
        when( assetItemIterator.next() ).thenReturn( assetItem );
        when( rulesRepository.findArchivedAssets() )
                .thenReturn( assetItemIterator );
    }

    @Test(expected = IllegalArgumentException.class)
    public void testFindAssetPageFormatInListAndFormatIsRegisteredIsNotSupported()
                                                                                  throws SerializationException {
        RulesRepository rulesRepository = mock( RulesRepository.class );
        RepositoryAssetOperations repositoryAssetOperations = new RepositoryAssetOperations();
        repositoryAssetOperations.setRulesRepositoryForTest(rulesRepository);
        AssetPageRequest assetPageRequest = new AssetPageRequest( "uuid",
                                                                  Arrays.asList( "formatInList" ),
                                                                  true,
                                                                  0,
                                                                  10 );
        repositoryAssetOperations.findAssetPage( assetPageRequest );
    }

    @Test
    public void testFindAssetPageFormatInList() throws SerializationException {
        RulesRepository rulesRepository = mock( RulesRepository.class );
        RepositoryAssetOperations repositoryAssetOperations = new RepositoryAssetOperations();
        repositoryAssetOperations.setRulesRepositoryForTest(rulesRepository);
        AssetPageRequest assetPageRequest = new AssetPageRequest( "uuid",
                                                                  Arrays.asList( "formatInList" ),
                                                                  null,
                                                                  1,
                                                                  2 );
        ModuleItem packageItem = mock( ModuleItem.class );
        when( rulesRepository.loadModuleByUUID( Mockito.anyString() ) ).thenReturn( packageItem );

        AssetItemIterator assetItemIterator = mock( AssetItemIterator.class );
        when( packageItem.listAssetsByFormat( assetPageRequest.getFormatInList() ) )
                .thenReturn( assetItemIterator );

        PageResponse<AssetPageRow> pageResponse = repositoryAssetOperations.findAssetPage( assetPageRequest );

        assertNotNull( pageResponse );
        assertEquals( pageResponse.getStartRowIndex(),
                      1 );
        verify( packageItem ).listAssetsByFormat(
                                                  assetPageRequest.getFormatInList() );
    }

    @Test
    public void testFindAssetPageFormatInList2() throws SerializationException {
        RulesRepository rulesRepository = mock( RulesRepository.class );
        RepositoryAssetOperations repositoryAssetOperations = new RepositoryAssetOperations();
        repositoryAssetOperations.setRulesRepositoryForTest(rulesRepository);
        AssetPageRequest assetPageRequest = new AssetPageRequest( "uuid",
                                                                  Arrays.asList( "formatInList" ),
                                                                  null,
                                                                  1,
                                                                  10 );
        ModuleItem packageItem = mock( ModuleItem.class );
        when( rulesRepository.loadModuleByUUID( Mockito.anyString() ) )
                .thenReturn( packageItem );

        AssetItem a1 = mock( AssetItem.class );
        when( a1.getFormat() ).thenReturn( "formatInList" );
        when( a1.getCreatedDate() ).thenReturn( Calendar.getInstance() );
        when( a1.getLastModified() ).thenReturn( Calendar.getInstance() );

        AssetItemIterator assetItemIterator = mock( AssetItemIterator.class );
        when( assetItemIterator.hasNext() ).thenReturn( true,
                                                        false );
        when( assetItemIterator.next() ).thenReturn( a1 );

        when( packageItem.listAssetsByFormat( assetPageRequest.getFormatInList() ) ).thenReturn( assetItemIterator );

        PageResponse<AssetPageRow> pageResponse = repositoryAssetOperations.findAssetPage( assetPageRequest );

        assertNotNull( pageResponse );
        assertEquals( pageResponse.getStartRowIndex(),
                      1 );
        assertEquals( 1,
                      pageResponse.getPageRowList().size() );

        verify( packageItem ).listAssetsByFormat( assetPageRequest.getFormatInList() );
    }

    @Test
    public void testFindAssetPageFormatInList3() throws SerializationException {
        RulesRepository rulesRepository = mock( RulesRepository.class );
        RepositoryAssetOperations repositoryAssetOperations = new RepositoryAssetOperations();
        repositoryAssetOperations.setRulesRepositoryForTest(rulesRepository);
        AssetPageRequest assetPageRequest = new AssetPageRequest( "uuid",
                                                                  null,
                                                                  false,
                                                                  1,
                                                                  10 );
        ModuleItem packageItem = mock( ModuleItem.class );
        when( rulesRepository.loadModuleByUUID( Mockito.anyString() ) )
                .thenReturn( packageItem );

        AssetItem a1 = mock( AssetItem.class );
        when( a1.getFormat() ).thenReturn( "formatNotInList" );
        when( a1.getCreatedDate() ).thenReturn( Calendar.getInstance() );
        when( a1.getLastModified() ).thenReturn( Calendar.getInstance() );

        AssetItemIterator assetItemIterator = mock( AssetItemIterator.class );
        when( assetItemIterator.hasNext() ).thenReturn( true,
                                                        false );
        when( assetItemIterator.next() ).thenReturn( a1 );

        String[] registeredFormats = registeredFormats();

        when( packageItem.listAssetsNotOfFormat( registeredFormats ) )
                .thenReturn( assetItemIterator );

        PageResponse<AssetPageRow> pageResponse = repositoryAssetOperations.findAssetPage( assetPageRequest );

        assertNotNull( pageResponse );
        assertEquals( pageResponse.getStartRowIndex(),
                      1 );
        assertEquals( 1,
                      pageResponse.getPageRowList().size() );

        verify( packageItem ).listAssetsNotOfFormat( registeredFormats );
    }

    private String[] registeredFormats() {
        AssetEditorConfigurationParser parser = new AssetEditorConfigurationParser();
        List<AssetEditorConfiguration> rfs = parser.getAssetEditors();
        String[] registeredFormats = new String[rfs.size()];
        for ( int i = 0; i < rfs.size(); i++ ) {
            registeredFormats[i] = rfs.get( i ).getFormat();
        }
        return registeredFormats;
    }

    
    @Test(expected = IllegalArgumentException.class)
    public void testGetAssetCountFormatInListAndFormatIsRegisteredIsNotSupported() throws SerializationException {
        RulesRepository rulesRepository = mock( RulesRepository.class );
        RepositoryAssetOperations repositoryAssetOperations = new RepositoryAssetOperations();
        repositoryAssetOperations.setRulesRepositoryForTest(rulesRepository);
        AssetPageRequest assetPageRequest = new AssetPageRequest( "uuid",
                                                                  Arrays.asList( "formatInList" ),
                                                                  true );
        repositoryAssetOperations.getAssetCount( assetPageRequest );
    }

    @Test
    public void testGetAssetCountFormatInList() throws SerializationException {
        RulesRepository rulesRepository = mock( RulesRepository.class );
        RepositoryAssetOperations repositoryAssetOperations = new RepositoryAssetOperations();
        repositoryAssetOperations.setRulesRepositoryForTest(rulesRepository);
        AssetPageRequest assetPageRequest = new AssetPageRequest( "uuid",
                                                                  Arrays.asList( "formatInList" ),
                                                                  null );
        ModuleItem packageItem = mock( ModuleItem.class );
        when( rulesRepository.loadModuleByUUID( Mockito.anyString() ) ).thenReturn( packageItem );

        AssetItemIterator assetItemIterator = mock( AssetItemIterator.class );
        when( packageItem.listAssetsByFormat( assetPageRequest.getFormatInList() ) ).thenReturn( assetItemIterator );
        when( assetItemIterator.getSize() ).thenReturn( 0l );
        
        long count = repositoryAssetOperations.getAssetCount( assetPageRequest );

        assertEquals( 0,
                      count );
        verify( packageItem ).listAssetsByFormat( assetPageRequest.getFormatInList() );
    }

    @Test
    public void testGetAssetCountFormatInList2() throws SerializationException {
        RulesRepository rulesRepository = mock( RulesRepository.class );
        RepositoryAssetOperations repositoryAssetOperations = new RepositoryAssetOperations();
        repositoryAssetOperations.setRulesRepositoryForTest(rulesRepository);
        AssetPageRequest assetPageRequest = new AssetPageRequest( "uuid",
                                                                  Arrays.asList( "formatInList" ),
                                                                  null );
        ModuleItem packageItem = mock( ModuleItem.class );
        when( rulesRepository.loadModuleByUUID( Mockito.anyString() ) ).thenReturn( packageItem );

        AssetItem a1 = mock( AssetItem.class );
        when( a1.getFormat() ).thenReturn( "formatInList" );
        when( a1.getCreatedDate() ).thenReturn( Calendar.getInstance() );
        when( a1.getLastModified() ).thenReturn( Calendar.getInstance() );

        AssetItemIterator assetItemIterator = mock( AssetItemIterator.class );
        when( packageItem.listAssetsByFormat( assetPageRequest.getFormatInList() ) ).thenReturn( assetItemIterator );
        when( assetItemIterator.getSize() ).thenReturn( 1l );

        long count = repositoryAssetOperations.getAssetCount( assetPageRequest );

        assertEquals( 1,
                      count );

        verify( packageItem ).listAssetsByFormat( assetPageRequest.getFormatInList() );
    }

    @Test
    public void testGetAssetCountFormatInList3() throws SerializationException {
        RulesRepository rulesRepository = mock( RulesRepository.class );
        RepositoryAssetOperations repositoryAssetOperations = new RepositoryAssetOperations();
        repositoryAssetOperations.setRulesRepositoryForTest(rulesRepository);
        AssetPageRequest assetPageRequest = new AssetPageRequest( "uuid",
                                                                  null,
                                                                  false );
        ModuleItem packageItem = mock( ModuleItem.class );
        when( rulesRepository.loadModuleByUUID( Mockito.anyString() ) ).thenReturn( packageItem );

        AssetItem a1 = mock( AssetItem.class );
        when( a1.getFormat() ).thenReturn( "formatNotInList" );
        when( a1.getCreatedDate() ).thenReturn( Calendar.getInstance() );
        when( a1.getLastModified() ).thenReturn( Calendar.getInstance() );

        AssetItemIterator assetItemIterator = mock( AssetItemIterator.class );
        when( assetItemIterator.hasNext() ).thenReturn( true,
                                                        false );
        when( assetItemIterator.next() ).thenReturn( a1 );

        String[] registeredFormats = registeredFormats();

        when( packageItem.listAssetsNotOfFormat( registeredFormats ) ).thenReturn( assetItemIterator );
        when( assetItemIterator.getSize() ).thenReturn( 0l );

        long count = repositoryAssetOperations.getAssetCount( assetPageRequest );

        assertEquals( 0,
                      count );

        verify( packageItem ).listAssetsNotOfFormat( registeredFormats );
    }

}
