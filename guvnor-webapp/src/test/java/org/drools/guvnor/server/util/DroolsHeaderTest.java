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
    public void testGetDroolsHeaderDoetNotAndExists() {
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

}
