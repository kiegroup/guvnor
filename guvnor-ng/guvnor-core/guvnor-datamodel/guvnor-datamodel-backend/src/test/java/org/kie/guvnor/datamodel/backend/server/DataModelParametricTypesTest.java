package org.kie.guvnor.datamodel.backend.server;

import org.drools.core.util.asm.ClassFieldInspector;
import org.junit.Ignore;
import org.junit.Test;
import org.kie.guvnor.datamodel.backend.server.builder.packages.PackageDataModelOracleBuilder;
import org.kie.guvnor.datamodel.backend.server.builder.projects.ProjectDefinitionBuilder;
import org.kie.guvnor.datamodel.backend.server.testclasses.Product;
import org.kie.guvnor.datamodel.backend.server.testclasses.Purchase;
import org.kie.guvnor.datamodel.oracle.DataModelOracle;
import org.drools.guvnor.models.commons.shared.oracle.DataType;
import org.kie.guvnor.datamodel.oracle.ProjectDefinition;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Tests for MethodInfo and Parametric types
 */
public class DataModelParametricTypesTest {

    @Test
    @Ignore("[manstis] This should work. Asking mfusco about it.")
    public void testClassFieldInspector() throws Exception {
        final ClassFieldInspector cfi = new ClassFieldInspector( Purchase.class );
        final Type t1 = cfi.getFieldTypes().get( "customerName" );
        final Type t2 = cfi.getFieldTypes().get( "items" );

        assertNotNull( t1 );
        assertNotNull( t2 );

        assertFalse( t1 instanceof ParameterizedType );
        assertTrue( t2 instanceof ParameterizedType );
    }

    @Test
    @Ignore("See testClassFieldInspector")
    public void testParametricReturnTypes() throws Exception {
        final ProjectDefinition pd = ProjectDefinitionBuilder.newProjectDefinitionBuilder()
                .addClass( Purchase.class )
                .addClass( Product.class )
                .build();

        final DataModelOracle dmo = PackageDataModelOracleBuilder.newDataModelBuilder( "org.kie.guvnor.datamodel.backend.server.testclasses" ).setProjectDefinition( pd ).build();

        assertNotNull( dmo );

        assertEquals( 2,
                      dmo.getFactTypes().length );

        List<String> list = Arrays.asList( dmo.getFactTypes() );

        assertTrue( list.contains( "Purchase" ) );
        assertTrue( list.contains( "Product" ) );

        assertEquals( "java.util.Collection",
                      dmo.getFieldClassName( "Purchase",
                                             "bananas" ) );
        assertEquals( DataType.TYPE_COLLECTION,
                      dmo.getFieldType( "Purchase",
                                        "bananas" ) );
        assertEquals( "Product",
                      dmo.getParametricFieldType( "Purchase",
                                                  "bananas" ) );

    }

    @Test
    public void testParametricMethod() throws Exception {
        final ProjectDefinition pd = ProjectDefinitionBuilder.newProjectDefinitionBuilder()
                .addClass( Purchase.class )
                .build();

        final DataModelOracle dmo = PackageDataModelOracleBuilder.newDataModelBuilder( "org.kie.guvnor.datamodel.backend.server.testclasses" ).setProjectDefinition( pd ).build();

        assertNotNull( dmo );

        assertEquals( "Product",
                      dmo.getParametricFieldType( "Purchase",
                                                  "customerPurchased(int)" ) );
    }

}
