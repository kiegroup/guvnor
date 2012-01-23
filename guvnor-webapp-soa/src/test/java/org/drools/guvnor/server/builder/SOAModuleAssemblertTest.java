/*
 * Copyright 2011 JBoss Inc
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 */

package org.drools.guvnor.server.builder;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.drools.guvnor.client.common.AssetFormats;
import org.drools.guvnor.server.GuvnorTestBase;
import org.drools.guvnor.server.builder.SOAModuleAssembler;
import org.drools.repository.AssetItem;
import org.drools.repository.ModuleItem;
import org.drools.repository.RulesRepository;
import org.junit.Test;


public class SOAModuleAssemblertTest extends GuvnorTestBase {

    @Test
    public void testSimpleServiceBuildNoErrors() throws Exception {
        RulesRepository repo = rulesRepository;

        ModuleItem module = repo.createModule( "testSimpleServiceBuildNoErrors", "", "soaservice" );
        
        AssetItem jar = module.addAsset("billasurf", "this is the Jar file containing Java classes as a service artifact.");
        jar.updateFormat("jar");
        jar.updateBinaryContentAttachment( this.getClass().getResourceAsStream( "/billasurf.jar" ) );
        jar.checkin( "" );

        AssetItem wsdl = module.addAsset( "wsdl1", "" );
        wsdl.updateFormat("wsdl");
        wsdl.updateBinaryContentAttachment( this.getClass().getResourceAsStream( "/billasurf.jar" ) );        wsdl.checkin( "" );

        AssetItem xsd = module.addAsset( "xsd1", "" );
        wsdl.updateFormat("xmlschema");
        wsdl.updateBinaryContentAttachment( this.getClass().getResourceAsStream( "/billasurf.jar" ) );
        wsdl.checkin( "" );

        AssetItem rule3 = module.addAsset( "A file",
                                        "" );
        rule3.updateFormat( AssetFormats.DRL );
        rule3.updateContent( "package testSimplePackageBuildNoErrors\n rule 'rule3' \n when \n then \n customer.setAge(43); \n end \n" + "rule 'rule4' \n when \n then \n System.err.println(44); \n end" );
        rule3.checkin( "" );

        repo.save();

        SOAModuleAssembler assembler = new SOAModuleAssembler();
        assembler.init(module, null);
        assembler.compile();
        assertFalse(assembler.hasErrors());
        
        byte[] compiledBinary = assembler.getCompiledBinary();
        assertNotNull(compiledBinary);
    }

}
