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

import java.io.InputStream;
import java.util.List;

import org.drools.guvnor.server.util.ModuleEditorConfiguration;
import org.drools.guvnor.server.util.PerspectiveConfigurationParser;
import org.junit.Test;

public class PerspectiveConfigurationParserTest {

    @Test
    public void testReadConfig() throws Exception {
        InputStream in = this.getClass().getResourceAsStream("/perspective.xml");
        PerspectiveConfigurationParser a = new PerspectiveConfigurationParser(in);
		List<ModuleEditorConfiguration> configList = a.getModuleEditors();

        assertEquals(2, configList.size());
        boolean foundPackageModuleEditor = false;
        boolean foundSOAServiceModuleEditor = false;
        for(ModuleEditorConfiguration config:configList) {
        	if(config.getFormat().equalsIgnoreCase("package")) {
                assertEquals("org.drools.guvnor.client.moduleeditor.drools.PackageEditor", config.getEditorClass());       		
                assertEquals("brl,dslr,xls", config.getAssetEditorFormats());       		
                foundPackageModuleEditor = true;
            } 
            if(config.getFormat().equalsIgnoreCase("soaservice")) {
                assertEquals("org.drools.guvnor.client.moduleeditor.soa.SOAServiceEditor", config.getEditorClass());               
                assertEquals("brl", config.getAssetEditorFormats());               
                foundSOAServiceModuleEditor = true;
            } 
        }
        assertTrue(foundPackageModuleEditor);
        assertTrue(foundSOAServiceModuleEditor);
    }
}
