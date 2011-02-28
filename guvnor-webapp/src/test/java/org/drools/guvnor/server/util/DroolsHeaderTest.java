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
package org.drools.guvnor.server.util;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.drools.repository.AssetItem;
import org.drools.repository.PackageItem;
import org.junit.Test;

public class DroolsHeaderTest {

    @Test
    public void testGetDroolsHeaderAndExists() {
        PackageItem packageItem = mock( PackageItem.class );
        AssetItem assetItem = mock( AssetItem.class );
        when( packageItem.containsAsset( "drools" ) ).thenReturn( true );
        when( packageItem.loadAsset( "drools" ) ).thenReturn( assetItem );
        when( assetItem.getContent() ).thenReturn( "content" );
        assertEquals( DroolsHeader.getDroolsHeader( packageItem ),
                      "content" );
        verify( packageItem ).loadAsset( "drools" );
        verify( assetItem ).getContent();
    }

    @Test
    public void testGetDroolsHeaderAndDoesNotExist() {
        PackageItem packageItem = mock( PackageItem.class );
        AssetItem assetItem = mock( AssetItem.class );
        when( packageItem.containsAsset( "drools" ) ).thenReturn( false );
        when( packageItem.loadAsset( "drools" ) ).thenReturn( assetItem );
        assertEquals( DroolsHeader.getDroolsHeader( packageItem ),
                      "" );
        verify( packageItem,
                never() ).loadAsset( "drools" );
        verify( assetItem,
                never() ).getContent();
    }

    @Test
    public void testUpdateDroolsHeaderAndExists() {
        PackageItem packageItem = mock( PackageItem.class );
        AssetItem assetItem = mock( AssetItem.class );
        when( packageItem.containsAsset( "drools" ) ).thenReturn( true );
        when( packageItem.loadAsset( "drools" ) ).thenReturn( assetItem );
        DroolsHeader.updateDroolsHeader( "expected",
                                         packageItem );
        verify( assetItem ).updateContent( "expected" );
        verify( assetItem ).checkin( "" );
    }

    @Test
    public void testUpdateDroolsHeaderAndDoesNotExist() {
        PackageItem packageItem = mock( PackageItem.class );
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

}
