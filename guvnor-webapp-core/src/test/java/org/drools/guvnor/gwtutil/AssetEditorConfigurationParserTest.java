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

package org.drools.guvnor.gwtutil;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.drools.guvnor.server.util.AssetEditorConfiguration;
import org.drools.guvnor.server.util.AssetEditorConfigurationParser;
import org.junit.Test;

public class AssetEditorConfigurationParserTest {

    @Test
    public void testReadConfig() throws Exception {
		AssetEditorConfigurationParser a = new AssetEditorConfigurationParser();
		List<AssetEditorConfiguration> configList = a.getAssetEditors();

        assertEquals(35, configList.size());
        boolean foundPropertiesWidgetForXML = false;
        boolean foundPropertiesWidgetForDocument = false;
        for(AssetEditorConfiguration config:configList) {
        	if(config.getFormat().equalsIgnoreCase("xml")) {
                assertEquals("org.drools.guvnor.client.asseteditor.XmlFileWidget", config.getEditorClass());       		
                assertEquals("images.newFile()", config.getIcon());       		
                assertEquals("constants.XMLProperties()", config.getTitle());  
                foundPropertiesWidgetForXML = true;
            } else if (config.getFormat().equalsIgnoreCase("")) {
				assertEquals(
						"org.drools.guvnor.client.asseteditor.drools.PropertiesWidget", config.getEditorClass());       		
                assertEquals("images.newFile()", config.getIcon());       		
                assertEquals("constants.OtherAssetsDocumentation()", config.getTitle());  
                foundPropertiesWidgetForDocument = true;
            }
        }
        assertTrue(foundPropertiesWidgetForXML);
        assertTrue(foundPropertiesWidgetForDocument);
    }
}
