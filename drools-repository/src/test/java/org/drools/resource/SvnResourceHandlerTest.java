package org.drools.resource;

import java.io.File;
import java.net.URI;
import java.net.URL;

import junit.framework.TestCase;

import org.apache.log4j.Logger;
import org.drools.resource.RepositoryBean;
import org.drools.resource.ResourceHandler;
import org.drools.resource.SvnResourceHandler;
import org.drools.resource.util.SvnUtil;

/**
 * @author James Williams (james.williams@redhat.com)
 * 
 */
public class SvnResourceHandlerTest extends TestCase {

    private static Logger       logger       = Logger.getLogger( SvnResourceHandlerTest.class );

    private static String svnUrl       = "file:///D:/dev/trunk2/rule-resource-handler/target/test-classes/svn_repo";
    private static String testFilePath = "D:/dev/trunk2/rule-resource-handler/target/test-classes/files";
    
    public void setUp() {
        // First we need to find the absolute path
        URL url = getClass().getResource( "/svn_repo" );
        
//        ClassLoader cl = getClass().getClassLoader();
//        assertNotNull( cl );
//        ClassLoader sys = cl.getSystemClassLoader();
//        assertNotNull(sys);
//        
//        URL url = sys.getResource( "svn_repo" );
        assertNotNull(url);
        File file = new File ( url.getFile() );        

        // Now set the two path roots
        svnUrl       = "file:///" + file.getAbsolutePath().replaceAll( "\\\\", "/" );        
        testFilePath = file.getParentFile().getAbsolutePath().replaceAll( "\\\\", "/" ) + "/files";        
    }

    public void testAuthentication() {
        ResourceHandler rHandler = new SvnResourceHandler();
        rHandler.setCredentials( "mrtrout",
                                 "drools" );
        
        boolean authRtnPass = rHandler.authenticate( svnUrl ) ;//"file:///D:/dev/trunk2/rule-resource-handler/target/test-classes/svn_repo" );
        boolean authRtnFail = rHandler.authenticate( svnUrl + "2" );//"file:///D:/dev/trunk2/rule-resource-handler/target/test-classes/svn_repo2" );

        System.out.println(svnUrl);
        
        assertEquals( true,
                      authRtnPass );
        assertEquals( false,
                      authRtnFail );

        logger.debug( "testAuthentication executed" );
    }

    public void testGetDrlFile() {
        ResourceHandler rHandler = new SvnResourceHandler();
        rHandler.setRepositoryUrl( svnUrl );
        rHandler.setCredentials( "mrtrout",
                                 "drools" );

        RepositoryBean bean = new RepositoryBean();
        bean.setName( "test" );
        bean.setResourceType( ResourceType.DRL_FILE );
        try {
            File f = new File( testFilePath + "/drls/test.drl" );
            System.out.println( rHandler.getResourceStream( bean ) );
            assertEquals( SvnUtil.getByteArrayOutputFromFile( f ).toString(),
                          rHandler.getResourceStream( bean ).toString() );
        } catch ( Exception e ) {
            e.printStackTrace();
            throw new RuntimeException( e );
        }
    }

    public void testGetDrlFileByVersion() {
        ResourceHandler rHandler = new SvnResourceHandler();
        rHandler.setRepositoryUrl( svnUrl );
        rHandler.setCredentials( "mrtrout",
                                 "drools" );

        RepositoryBean bean = new RepositoryBean();
        bean.setName( "test" );
        bean.setResourceType( ResourceType.DRL_FILE );

        //this value is unique to your subversion implementation
        bean.setVersion( "1" );

        try {
            File f = new File( testFilePath + "/drls/test.drl" );
            System.out.println( rHandler.getResourceStream( bean ) );
            assertEquals( SvnUtil.getByteArrayOutputFromFile( f ).toString(),
                          rHandler.getResourceStream( bean ).toString() );
        } catch ( Exception e ) {
            throw new RuntimeException( e );
        }
    }

    public void testGetDslFile() {
        ResourceHandler rHandler = new SvnResourceHandler();
        rHandler.setRepositoryUrl( svnUrl );
        rHandler.setCredentials( "mrtrout",
                                 "drools" );

        RepositoryBean bean = new RepositoryBean();
        bean.setName( "test" );
        bean.setResourceType( ResourceType.DSL_FILE );

        try {
            File f = new File( testFilePath + "/dsls/test.dsl" );
            System.out.println( rHandler.getResourceStream( bean ) );
            assertEquals( SvnUtil.getByteArrayOutputFromFile( f ).toString(),
                          rHandler.getResourceStream( bean ).toString() );
        } catch ( Exception e ) {
            throw new RuntimeException( e );
        }
    }

    public void testGetDslFileByVersion() {
        ResourceHandler rHandler = new SvnResourceHandler();
        rHandler.setRepositoryUrl( svnUrl );
        rHandler.setCredentials( "mrtrout",
                                 "drools" );

        RepositoryBean bean = new RepositoryBean();
        bean.setName( "test" );
        bean.setResourceType( ResourceType.DSL_FILE );

        //this value is unique to your subversion implementation
        bean.setVersion( "1" );

        try {
            File f = new File( testFilePath + "/dsls/test.dsl" );
            System.out.println( rHandler.getResourceStream( bean ) );
            assertEquals( SvnUtil.getByteArrayOutputFromFile( f ).toString(),
                          rHandler.getResourceStream( bean ).toString() );
        } catch ( Exception e ) {
            throw new RuntimeException( e );
        }
    }

    public void testGetRuleFunctionByVersion() {
        ResourceHandler rHandler = new SvnResourceHandler();
        rHandler.setRepositoryUrl( svnUrl );
        rHandler.setCredentials( "mrtrout",
                                 "drools" );

        RepositoryBean bean = new RepositoryBean();
        bean.setName( "test" );
        bean.setResourceType( ResourceType.FUNCTION );

        //this value is unique to your subversion implementation
        bean.setVersion( "1" );

        try {
            File f = new File( testFilePath + "/functions/test.function" );
            System.out.println( rHandler.getResourceStream( bean ) );
            assertEquals( SvnUtil.getByteArrayOutputFromFile( f ).toString(),
                          rHandler.getResourceStream( bean ).toString() );
        } catch ( Exception e ) {
            throw new RuntimeException( e );
        }
    }

    public void testGetRuleFunction() {
        ResourceHandler rHandler = new SvnResourceHandler();
        rHandler.setRepositoryUrl( svnUrl );
        rHandler.setCredentials( "mrtrout",
                                 "drools" );

        RepositoryBean bean = new RepositoryBean();
        bean.setName( "test" );
        bean.setResourceType( ResourceType.FUNCTION );

        try {
            File f = new File( testFilePath + "/functions/test.function" );
            System.out.println( rHandler.getResourceStream( bean ) );
            assertEquals( SvnUtil.getByteArrayOutputFromFile( f ).toString(),
                          rHandler.getResourceStream( bean ).toString() );
        } catch ( Exception e ) {
            throw new RuntimeException( e );
        }
    }

    public void testGetRule() {
        ResourceHandler rHandler = new SvnResourceHandler();
        rHandler.setRepositoryUrl( svnUrl );
        rHandler.setCredentials( "mrtrout",
                                 "drools" );

        RepositoryBean bean = new RepositoryBean();
        bean.setName( "test" );
        bean.setResourceType( ResourceType.RULE );

        try {
            File f = new File( testFilePath + "/rules/test.rule" );
            System.out.println( rHandler.getResourceStream( bean ) );
            assertEquals( SvnUtil.getByteArrayOutputFromFile( f ).toString(),
                          rHandler.getResourceStream( bean ).toString() );
        } catch ( Exception e ) {
            throw new RuntimeException( e );
        }
    }

    public void testGetRuleByVersion() {
        ResourceHandler rHandler = new SvnResourceHandler();
        rHandler.setRepositoryUrl( svnUrl );
        rHandler.setCredentials( "mrtrout",
                                 "drools" );

        RepositoryBean bean = new RepositoryBean();
        bean.setName( "test" );
        bean.setResourceType( ResourceType.RULE );

        //this value is unique to your subversion implementation
        bean.setVersion( "1" );

        try {
            File f = new File( testFilePath + "/rules/test.rule" );
            System.out.println( rHandler.getResourceStream( bean ) );
            assertEquals( SvnUtil.getByteArrayOutputFromFile( f ).toString(),
                          rHandler.getResourceStream( bean ).toString() );
        } catch ( Exception e ) {
            throw new RuntimeException( e );
        }
    }

    public void testGetSpreadsheet() {
        ResourceHandler rHandler = new SvnResourceHandler();
        rHandler.setRepositoryUrl( svnUrl );
        rHandler.setCredentials( "mrtrout",
                                 "drools" );

        RepositoryBean bean = new RepositoryBean();
        bean.setName( "test" );
        bean.setResourceType( ResourceType.XLS_FILE );

        try {
            File f = new File( testFilePath + "/spreadsheets/test.xls" );
            // System.out.println(rHandler.getResourceStream(bean));
            assertEquals( SvnUtil.getByteArrayOutputFromFile( f ).toString(),
                          rHandler.getResourceStream( bean ).toString() );
        } catch ( Exception e ) {
            throw new RuntimeException( e );
        }
    }

    public void testGetSpreadsheetByVersion() {
        ResourceHandler rHandler = new SvnResourceHandler();
        rHandler.setRepositoryUrl( svnUrl );
        rHandler.setCredentials( "mrtrout",
                                 "drools" );

        RepositoryBean bean = new RepositoryBean();
        bean.setName( "test" );
        bean.setResourceType( ResourceType.XLS_FILE );

        //this value is unique to your subversion implementation
        bean.setVersion( "1" );

        try {
            File f = new File( testFilePath + "/spreadsheets/test.xls" );
            // System.out.println(rHandler.getResourceStream(bean));
            assertEquals( SvnUtil.getByteArrayOutputFromFile( f ).toString(),
                          rHandler.getResourceStream( bean ).toString() );
        } catch ( Exception e ) {
            throw new RuntimeException( e );
        }
    }

}
