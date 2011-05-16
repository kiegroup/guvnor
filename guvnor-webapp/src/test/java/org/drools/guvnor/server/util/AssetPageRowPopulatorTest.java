/*
 * Copyright 2010 JBoss Inc
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
package org.drools.guvnor.server.util;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Calendar;
import java.util.GregorianCalendar;

import org.drools.guvnor.client.rpc.AssetPageRow;
import org.drools.repository.AssetItem;
import org.junit.Test;

public class AssetPageRowPopulatorTest {

    @Test
    public void testPopulateFrom() {
    
            AssetItem assetItem = mock( AssetItem.class );
            Calendar calendar = GregorianCalendar.getInstance();
            calendar.add( Calendar.MONTH,
                          -1 );
            Calendar lastModifiedCalendar = GregorianCalendar.getInstance();
    
            when( assetItem.getUUID() ).thenReturn( "UUID" );
            when( assetItem.getFormat() ).thenReturn( "format" );
            when( assetItem.getName() ).thenReturn( "name" );
            when( assetItem.getDescription() ).thenReturn( "descriptiondescriptiondescriptiondescriptiondescriptiondescriptiondescriptiondescriptiondescription" );
            when( assetItem.getStateDescription() ).thenReturn( "statedescription" );
            when( assetItem.getCreator() ).thenReturn( "creator" );
            when( assetItem.getCreatedDate() ).thenReturn( calendar );
            when( assetItem.getLastContributor() ).thenReturn( "lastcontributor" );
            when( assetItem.getLastModified() ).thenReturn( lastModifiedCalendar );
            when( assetItem.getCategorySummary() ).thenReturn( "categorysummary" );
            when( assetItem.getExternalSource() ).thenReturn( "externalsource" );
            AssetPageRowPopulator assetPageRowPopulator = new AssetPageRowPopulator();
            AssetPageRow makeAssetPageRow = assetPageRowPopulator.populateFrom( assetItem );
            assertEquals( makeAssetPageRow.getUuid(),
                          assetItem.getUUID() );
            assertEquals( makeAssetPageRow.getFormat(),
                          assetItem.getFormat() );
            assertEquals( makeAssetPageRow.getName(),
                          assetItem.getName() );
            assertEquals( makeAssetPageRow.getDescription(),
                          assetItem.getDescription() );
            assertEquals( makeAssetPageRow.getAbbreviatedDescription().length(),
                          80 );
            assertEquals( makeAssetPageRow.getStateName(),
                          assetItem.getStateDescription() );
            assertEquals( makeAssetPageRow.getCreator(),
                          assetItem.getCreator() );
            assertEquals( makeAssetPageRow.getCreatedDate(),
                          assetItem.getCreatedDate().getTime() );
            assertEquals( makeAssetPageRow.getLastContributor(),
                          assetItem.getLastContributor() );
            assertEquals( makeAssetPageRow.getLastModified(),
                          assetItem.getLastModified().getTime() );
            assertEquals( makeAssetPageRow.getCategorySummary(),
                          assetItem.getCategorySummary() );
            assertEquals( makeAssetPageRow.getExternalSource(),
                          assetItem.getExternalSource() );
    
        }
}
