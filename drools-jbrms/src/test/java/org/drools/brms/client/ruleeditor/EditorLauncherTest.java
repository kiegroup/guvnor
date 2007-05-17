package org.drools.brms.client.ruleeditor;

import org.drools.brms.client.common.AssetFormats;

import junit.framework.TestCase;

public class EditorLauncherTest extends TestCase {

    public void testIcons()  {
        assertNotNull(EditorLauncher.TYPE_IMAGES);
        assertNotNull(EditorLauncher.getAssetFormatIcon( "drl" ));
        assertNotNull(EditorLauncher.getAssetFormatIcon( "JKLGFJSLKGJFDLKGJFKLDJGLFKDJGKLFD" ));
        assertEquals("model_asset.gif", EditorLauncher.getAssetFormatIcon( AssetFormats.MODEL ));
    }
    
}
