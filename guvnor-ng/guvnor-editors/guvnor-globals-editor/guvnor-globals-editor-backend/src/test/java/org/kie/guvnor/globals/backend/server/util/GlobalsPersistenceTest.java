package org.kie.guvnor.globals.backend.server.util;

import org.junit.Test;
import org.kie.guvnor.globals.model.Global;
import org.kie.guvnor.globals.model.GlobalsModel;
import org.kie.guvnor.services.config.model.imports.Import;

import static org.junit.Assert.*;

/**
 * Tests for GlobalsPersistence
 */
public class GlobalsPersistenceTest {

    @Test
    public void testMarshalling() {
        final GlobalsModel model = new GlobalsModel();
        final String expected = "import java.util.List;\n"
                + "\n"
                + "global java.lang.String myString;\n";

        model.getImports().addImport( new Import( "java.util.List" ) );
        model.getGlobals().add( new Global( "myString",
                                            "java.lang.String" ) );
        final String actual = GlobalsPersistence.getInstance().marshal( model );

        assertNotNull( actual );
        assertEquals( expected,
                      actual );
    }

    @Test
    public void testUnmarshalling() {
        final String content = "import java.util.List;\n"
                + "\n"
                + "global java.lang.String myString;\n";
        final GlobalsModel model = GlobalsPersistence.getInstance().unmarshal( content );

        assertNotNull( model );
        assertEquals( 1,
                      model.getImports().getImports().size() );
        assertEquals( "java.util.List",
                      model.getImports().getImports().get( 0 ).getType() );
        assertEquals( 1,
                      model.getGlobals().size() );
        assertEquals( "java.lang.String",
                      model.getGlobals().get( 0 ).getClassName() );
        assertEquals( "myString",
                      model.getGlobals().get( 0 ).getAlias() );
    }

}
