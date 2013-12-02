package org.guvnor.common.services.project.backend.server;

import org.drools.workbench.models.datamodel.imports.Import;
import org.guvnor.common.services.project.model.ProjectImports;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class ProjectConfigurationContentHandlerTest {

    private ProjectConfigurationContentHandler handler;

    @Before
    public void setUp() throws Exception {
        handler = new ProjectConfigurationContentHandler();
    }

    @Test
    public void testVersion() throws Exception {
        assertTrue( handler.toString( new ProjectImports() ).contains( "<version>1.0</version>" ) );
    }

    @Test
    public void testNullSourceXml() throws Exception {
        ProjectImports imports = handler.toModel( null );
        assertNotNull( imports );
        assertEquals( 0,
                      imports.getImports().getImports().size() );
    }

    @Test
    public void testEmptySourceXml() throws Exception {
        ProjectImports imports = handler.toModel( "" );
        assertNotNull( imports );
        assertEquals( 0,
                      imports.getImports().getImports().size() );
    }

    @Test
    public void testNullModel() throws Exception {
        String xml = handler.toString( null );
        assertEquals( "",
                      xml );
    }

    @Test
    public void testEmptyImports() throws Exception {
        String xml = handler.toString( new ProjectImports() );
        assertTrue( xml.contains( "<imports>" ) );
        assertTrue( xml.contains( "</imports>" ) );
    }

    @Test
    public void testImports() throws Exception {
        ProjectImports configuration = new ProjectImports();
        configuration.getImports().addImport( new Import( "java.util.List" ) );
        configuration.getImports().addImport( new Import( "org.test.Object" ) );
        String xml = handler.toString( configuration );
        assertTrue( xml.contains( "<import>" ) );
        assertTrue( xml.contains( "</import>" ) );
        assertTrue( xml.contains( "<type>java.util.List</type>" ) );
        assertTrue( xml.contains( "<type>org.test.Object</type>" ) );
    }
}
