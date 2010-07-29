/**
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

package org.drools.resource;

import java.io.File;
import java.net.URL;

import junit.framework.TestCase;

import org.drools.resource.util.SvnUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author James Williams (james.williams@redhat.com)
 * 
 */
public class SvnResourceHandlerTest extends TestCase {

    private static Logger       logger       = LoggerFactory.getLogger( SvnResourceHandlerTest.class );

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
