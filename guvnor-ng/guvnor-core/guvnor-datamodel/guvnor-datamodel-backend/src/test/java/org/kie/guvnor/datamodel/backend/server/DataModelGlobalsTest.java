package org.kie.guvnor.datamodel.backend.server;

import java.util.Arrays;

import org.junit.Test;
import org.kie.guvnor.datamodel.backend.server.builder.packages.PackageDataModelOracleBuilder;
import org.kie.guvnor.datamodel.backend.server.builder.projects.ProjectDefinitionBuilder;
import org.kie.guvnor.datamodel.backend.server.testclasses.Product;
import org.kie.guvnor.datamodel.oracle.DataModelOracle;
import org.kie.guvnor.datamodel.oracle.ProjectDefinition;
import org.kie.guvnor.services.config.model.imports.Import;
import org.kie.guvnor.services.config.model.imports.Imports;

import static org.junit.Assert.*;

/**
 * Tests for Globals
 */
public class DataModelGlobalsTest {

    @Test
    public void testGlobal() throws Exception {
        final ProjectDefinition pd = ProjectDefinitionBuilder.newProjectDefinitionBuilder()
                .addClass( Product.class )
                .build();

        final DataModelOracle dmo = PackageDataModelOracleBuilder.newDataModelBuilder( "org.kie.guvnor.datamodel.backend.server.testclasses" )
                .setProjectDefinition( pd )
                .addGlobals( "global org.kie.guvnor.datamodel.backend.server.testclasses.Product g;" )
                .build();

        assertNotNull( dmo );

        assertEquals( 1,
                      dmo.getFactTypes().length );
        assertEquals( "Product",
                      dmo.getFactTypes()[ 0 ] );

        assertEquals( 1,
                      dmo.getGlobalVariables().length );
        assertEquals( "g",
                      dmo.getGlobalVariables()[ 0 ] );
        assertEquals( "Product",
                      dmo.getGlobalVariable( "g" ) );

        final String[] fields = dmo.getFieldCompletions( "Product" );
        assertNotNull( fields );
        assertTrue( Arrays.asList( fields ).contains( "this" ) );
        assertTrue( Arrays.asList( fields ).contains( "colour" ) );

        assertEquals( 0,
                      dmo.getGlobalCollections().length );
    }

    @Test
    public void testGlobalCollections() throws Exception {
        final ProjectDefinition pd = ProjectDefinitionBuilder.newProjectDefinitionBuilder()
                .addClass( java.util.List.class )
                .build();

        final DataModelOracle dmo = PackageDataModelOracleBuilder.newDataModelBuilder( "org.kie.guvnor.datamodel.backend.server.testclasses" )
                .setProjectDefinition( pd )
                .addGlobals( "global java.util.List list;" )
                .build();

        final Imports imports = new Imports();
        imports.addImport( new Import( "java.util.List" ) );
        dmo.filter( imports );

        assertNotNull( dmo );

        assertEquals( 1,
                      dmo.getFactTypes().length );
        assertEquals( "List",
                      dmo.getFactTypes()[ 0 ] );

        assertEquals( 1,
                      dmo.getGlobalVariables().length );
        assertEquals( "list",
                      dmo.getGlobalVariables()[ 0 ] );
        assertEquals( "List",
                      dmo.getGlobalVariable( "list" ) );

        assertEquals( 1,
                      dmo.getGlobalCollections().length );
        assertEquals( "list",
                      dmo.getGlobalCollections()[ 0 ] );
    }

}
