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
package org.drools.guvnor.server.builder;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.drools.repository.AssetItem;
import org.junit.Test;

public class ZipUtilTest {

    @Test
    public void testZipUtil() throws Exception {        
        AssetItem wsdlAssetItem = mock( AssetItem.class );
        when( wsdlAssetItem.getUUID() ).thenReturn( "UUID1" );
        when( wsdlAssetItem.getFormat() ).thenReturn( "wsdl" );
        when( wsdlAssetItem.getName() ).thenReturn( "CustomerService" );
        when( wsdlAssetItem.getBinaryContentAttachment() ).thenReturn( this.getClass().getResourceAsStream( "/CustomerService.wsdl" ));
        when( wsdlAssetItem.getBinaryContentAttachmentFileName() ).thenReturn( "CustomerService.wsdl" );
        
        AssetItem jarAssetItem = mock( AssetItem.class );
        when( jarAssetItem.getUUID() ).thenReturn( "UUID2" );
        when( jarAssetItem.getFormat() ).thenReturn( "jar" );
        when( jarAssetItem.getName() ).thenReturn( "billasurf" );
        when( jarAssetItem.getBinaryContentAttachment() ).thenReturn( this.getClass().getResourceAsStream( "/billasurf.jar" ));
        when( jarAssetItem.getBinaryContentAttachmentFileName() ).thenReturn( "billasurf.jar" );
        
        List<AssetItem> assets = new ArrayList<AssetItem>();
        assets.add(wsdlAssetItem);
        assets.add(jarAssetItem);
        
        ZipUtil zipUtil = new ZipUtil(assets);
        InputStream is = zipUtil.zipAssets();
        
        ZipInputStream zin = new ZipInputStream(is);
        ZipEntry ze = null;
        boolean foundWsdl = false;
        boolean foundJarClass = false;
        while ((ze = zin.getNextEntry()) != null) {
            String name = ze.getName();
            zin.closeEntry();
            if("wsdl/CustomerService.wsdl".equals(name)) {
                foundWsdl = true;
            }
            if("com/billasurf/Board.java".equals(name)) {
                foundJarClass = true;
            }            
        }
        zin.close();
        assertTrue(foundWsdl);
        assertTrue(foundJarClass);
    }
   
}
