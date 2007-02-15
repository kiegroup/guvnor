package org.drools.scm.svn;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import junit.framework.TestCase;

//import org.apache.log4j.Logger;
import org.drools.scm.CompositeScmAction;
import org.drools.scm.ScmAction;
import org.drools.scm.ScmActionFactory;
import org.drools.scm.ScmEntry;
import org.drools.scm.svn.SvnActionFactory.AddDirectory;
import org.drools.scm.svn.SvnActionFactory.AddFile;
import org.drools.scm.svn.SvnActionFactory.CopyDirectory;
import org.drools.scm.svn.SvnActionFactory.CopyFile;
import org.drools.scm.svn.SvnActionFactory.DeleteDirectory;
import org.drools.scm.svn.SvnActionFactory.DeleteFile;
import org.drools.scm.svn.SvnActionFactory.MoveDirectory;
import org.drools.scm.svn.SvnActionFactory.MoveFile;
import org.drools.scm.svn.SvnActionFactory.UpdateFile;
import org.tmatesoft.svn.core.SVNLogEntry;
import org.tmatesoft.svn.core.SVNLogEntryPath;

public class SvnActionFactoryTest extends TestCase {

    //private static Logger logger = Logger.getLogger( SvnActionFactoryTest.class );

    private static String svnUrl;

    public void setUp() throws IOException {
        // First we need to find the absolute path
        URL url = getClass().getResource( "/svn_repo_empty" );

        assertNotNull( url );
        File src = new File( url.getFile() );
        File dst = new File( src.getParent(),
                             "/copy_svn_repo_empty" );

        // make sure the destination is empty before we copy
        delete( dst );

        copy( src,
              dst );

        // Now set the two path roots
        svnUrl = "file:///" + dst.getAbsolutePath().replaceAll( "\\\\",
                                                                "/" );
    }

    public void tearDown() throws Exception {
        URL url = getClass().getResource( "/copy_svn_repo_empty" );
        delete( new File( url.getFile() ) );
    }

    public void testCannotAddDirectoryWithNoParent() throws Exception {
        ScmActionFactory svn = new SvnActionFactory( svnUrl,
                                                     "mrtrout",
                                                     "drools" );

        CompositeScmAction actions = new CompositeScmAction();

        try {
            ScmAction addDirectory = new AddDirectory( "folder1",
                                                    "folder1_1" );
            actions.addScmAction( addDirectory );
            svn.execute( actions,
                         "test message" );
            fail( "This should fail as 'folder1' has not yet been created" );
        } catch ( Exception e ) {

        }
    }

    public void testCannotAddDuplicateDirectories() throws Exception {
        ScmActionFactory svn = new SvnActionFactory( svnUrl,
                                                     "mrtrout",
                                                     "drools" );

        CompositeScmAction actions = new CompositeScmAction();

        // Correctly add a new directory at root
        actions = new CompositeScmAction();
        ScmAction addDirectory = new AddDirectory( "",
                                                "folder1" );
        actions.addScmAction( addDirectory );

        svn.execute( actions,
                     "test message" );

        // Check we can't add duplicate Directorys
        try {
            actions = new CompositeScmAction();
            addDirectory = new AddDirectory( "",
                                          "folder1" );
            actions.addScmAction( addDirectory );
            svn.execute( actions,
                         "test message" );
            fail( "This should fail as 'folder1' already exists" );
        } catch ( Exception e ) {

        }
    }

    public void testAddDirectories() throws Exception {
        ScmActionFactory svn = new SvnActionFactory( svnUrl,
                                                     "mrtrout",
                                                     "drools" );

        CompositeScmAction actions = new CompositeScmAction();

        // Correctly add a new Directory at root
        actions = new CompositeScmAction();
        ScmAction addDirectory = new AddDirectory( "",
                                                "folder1" );
        actions.addScmAction( addDirectory );

        svn.execute( actions,
                     "test message" );

        // Now check various flat and deep Directory creations
        actions = new CompositeScmAction();

        addDirectory = new AddDirectory( "folder1",
                                      "folder1_1" );
        actions.addScmAction( addDirectory );

        addDirectory = new AddDirectory( "folder1/folder1_1",
                                      "folder1_1_1" );
        actions.addScmAction( addDirectory );

        addDirectory = new AddDirectory( "folder1",
                                      "folder1_2" );
        actions.addScmAction( addDirectory );

        addDirectory = new AddDirectory( "",
                                      "folder2/folder2_1" );
        actions.addScmAction( addDirectory );

        addDirectory = new AddDirectory( "",
                                      "folder3/folder3_1/folder3_1_1/folder3_1_1_1" );
        actions.addScmAction( addDirectory );

        svn.execute( actions,
                     "test message" );
        //------
        // Now test results
        //-------                
        List list = convertToStringList( svn.listEntries( "" ) );

        assertTrue( list.contains( "folder1" ) );
        assertTrue( list.contains( "folder1/folder1_1" ) );
        assertTrue( list.contains( "folder1/folder1_2" ) );
        assertTrue( list.contains( "folder2/folder2_1" ) );
        assertTrue( list.contains( "folder3/folder3_1/folder3_1_1/folder3_1_1_1" ) );
    }

    public void testAddFiles() throws Exception {
        ScmActionFactory svn = new SvnActionFactory( svnUrl,
                                                     "mrtrout",
                                                     "drools" );

        CompositeScmAction actions = new CompositeScmAction();

        ScmAction addDirectory = new AddDirectory( "",
                                                "folder1" );
        actions.addScmAction( addDirectory );

        ScmAction addFile = new AddFile( "folder1",
                                         "file1.dat",
                                         new byte[]{1, 1, 1, 1} );
        actions.addScmAction( addFile );

        addDirectory = new AddDirectory( "folder1",
                                      "folder1_1" );
        actions.addScmAction( addDirectory );

        addFile = new AddFile( "folder1/folder1_1",
                               "file1_1.dat",
                               new byte[]{0, 0, 0, 0} );
        actions.addScmAction( addFile );

        svn.execute( actions,
                     "test message" );

        // Check the contents are correct
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        svn.getContent( "folder1",
                        "file1.dat",
                        -1,
                        baos );
        assertTrue( Arrays.equals( new byte[]{1, 1, 1, 1},
                                   baos.toByteArray() ) );

        baos = new ByteArrayOutputStream();
        svn.getContent( "folder1/folder1_1",
                        "file1_1.dat",
                        -1,
                        baos );
        assertTrue( Arrays.equals( new byte[]{0, 0, 0, 0},
                                   baos.toByteArray() ) );

        // Check the directories are correctly created
        List list = convertToStringList( svn.listEntries( "" ) );
        assertTrue( list.contains( "folder1" ) );
        assertTrue( list.contains( "folder1/file1.dat" ) );
        assertTrue( list.contains( "folder1/folder1_1" ) );
        assertTrue( list.contains( "folder1/folder1_1/file1_1.dat" ) );
    }

    public void testUpdateFile() throws Exception {
        ScmActionFactory svn = new SvnActionFactory( svnUrl,
                                                     "mrtrout",
                                                     "drools" );

        CompositeScmAction actions = new CompositeScmAction();

        ScmAction addDirectory = new AddDirectory( "",
                                                "folder1" );
        actions.addScmAction( addDirectory );

        byte[] oldContent = new byte[]{1, 1, 1, 1};
        byte[] newContent = new byte[]{1, 0, 1, 0};

        // Add the initial file
        ScmAction addFile = new AddFile( "folder1",
                                         "file1.dat",
                                         oldContent );
        actions.addScmAction( addFile );
        svn.execute( actions,
                     "test message" );

        // Check the contents are correct
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        svn.getContent( "folder1",
                        "file1.dat",
                        -1,
                        baos );
        assertTrue( Arrays.equals( oldContent,
                                   baos.toByteArray() ) );

        // Update the existing file
        actions = new CompositeScmAction();
        ScmAction updateFile = new UpdateFile( "folder1",
                                               "file1.dat",
                                               oldContent,
                                               newContent );
        actions.addScmAction( updateFile );
        svn.execute( actions,
                     "test message" );

        // Check the contents are correct
        baos = new ByteArrayOutputStream();
        svn.getContent( "folder1",
                        "file1.dat",
                        -1,
                        baos );
        assertTrue( Arrays.equals( newContent,
                                   baos.toByteArray() ) );

        // Check the correct directory structue was created
        List list = convertToStringList( svn.listEntries( "" ) );
        assertTrue( list.contains( "folder1" ) );
        assertTrue( list.contains( "folder1/file1.dat" ) );
    }

    public void testCopyFile() throws Exception {
        ScmActionFactory svn = new SvnActionFactory( svnUrl,
                                                     "mrtrout",
                                                     "drools" );

        CompositeScmAction actions = new CompositeScmAction();

        ScmAction addDirectory = new AddDirectory( "",
                                                "folder1" );
        actions.addScmAction( addDirectory );
        byte[] content = new byte[]{1, 1, 1, 1};
        ScmAction addFile = new AddFile( "folder1",
                                         "file1.dat",
                                         content );
        actions.addScmAction( addFile );
        svn.execute( actions,
                     "test message" );

        // Check the contents are correct
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        svn.getContent( "folder1",
                        "file1.dat",
                        -1,
                        baos );
        assertTrue( Arrays.equals( content,
                                   baos.toByteArray() ) );

        List list = convertToStringList( svn.listEntries( "" ) );
        assertTrue( list.contains( "folder1" ) );
        assertTrue( list.contains( "folder1/file1.dat" ) );
        assertFalse( list.contains( "folder2/file2.dat" ) );

        // Now copy the file
        actions = new CompositeScmAction();
        addDirectory = new AddDirectory( "",
                                      "folder2" );
        actions.addScmAction( addDirectory );
        ScmAction copyFile = new CopyFile( "folder1",
                                           "file1.dat",
                                           "folder2",
                                           "file2.dat",
                                           svn.getLatestRevision() );
        actions.addScmAction( copyFile );
        svn.execute( actions,
                     "test message" );

        baos = new ByteArrayOutputStream();
        svn.getContent( "folder2",
                        "file2.dat",
                        -1,
                        baos );
        assertTrue( Arrays.equals( content,
                                   baos.toByteArray() ) );

        list = convertToStringList( svn.listEntries( "" ) );
        assertTrue( list.contains( "folder1" ) );
        assertTrue( list.contains( "folder1/file1.dat" ) );
        assertTrue( list.contains( "folder2/file2.dat" ) );
    }

    public void testCopyDirectory() throws Exception {
        ScmActionFactory svn = new SvnActionFactory( svnUrl,
                                                     "mrtrout",
                                                     "drools" );

        CompositeScmAction actions = new CompositeScmAction();

        ScmAction addDirectory = new AddDirectory( "",
                                                "folder1" );
        actions.addScmAction( addDirectory );
        byte[] content1 = new byte[]{1, 1, 1, 1};
        ScmAction addFile = new AddFile( "folder1",
                                         "file1.dat",
                                         content1 );
        actions.addScmAction( addFile );

        addDirectory = new AddDirectory( "folder1",
                                      "folder1_1" );
        actions.addScmAction( addDirectory );
        byte[] content2 = new byte[]{1, 0, 0, 1};
        addFile = new AddFile( "folder1/folder1_1",
                               "file1.dat",
                               content2 );
        actions.addScmAction( addFile );
        svn.execute( actions,
                     "test message" );

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        svn.getContent( "folder1",
                        "file1.dat",
                        -1,
                        baos );
        assertTrue( Arrays.equals( content1,
                                   baos.toByteArray() ) );
        baos = new ByteArrayOutputStream();
        svn.getContent( "folder1/folder1_1",
                        "file1.dat",
                        -1,
                        baos );
        assertTrue( Arrays.equals( content2,
                                   baos.toByteArray() ) );

        List list = convertToStringList( svn.listEntries( "" ) );
        assertTrue( list.contains( "folder1" ) );
        assertTrue( list.contains( "folder1/folder1_1/file1.dat" ) );
        assertFalse( list.contains( "folder2/folder1/file1.dat" ) );

        // Now copy the directory
        actions = new CompositeScmAction();
        addDirectory = new AddDirectory( "",
                                      "folder2" );
        actions.addScmAction( addDirectory );
        ScmAction copyDirectory = new CopyDirectory( "folder1",
                                                     "folder2/folder1",
                                                     svn.getLatestRevision() );
        actions.addScmAction( copyDirectory );
        svn.execute( actions,
                     "test message" );

        baos = new ByteArrayOutputStream();
        svn.getContent( "folder1",
                        "file1.dat",
                        -1,
                        baos );
        assertTrue( Arrays.equals( content1,
                                   baos.toByteArray() ) );
        baos = new ByteArrayOutputStream();
        svn.getContent( "folder1/folder1_1",
                        "file1.dat",
                        -1,
                        baos );
        assertTrue( Arrays.equals( content2,
                                   baos.toByteArray() ) );
        baos = new ByteArrayOutputStream();
        svn.getContent( "folder2/folder1",
                        "file1.dat",
                        -1,
                        baos );
        assertTrue( Arrays.equals( content1,
                                   baos.toByteArray() ) );
        baos = new ByteArrayOutputStream();
        svn.getContent( "folder2/folder1/folder1_1",
                        "file1.dat",
                        -1,
                        baos );
        assertTrue( Arrays.equals( content2,
                                   baos.toByteArray() ) );

        list = convertToStringList( svn.listEntries( "" ) );
        assertTrue( list.contains( "folder1" ) );
        assertTrue( list.contains( "folder1/folder1_1/file1.dat" ) );
        assertTrue( list.contains( "folder2/folder1/file1.dat" ) );
    }

    public void testMoveFile() throws Exception {
        ScmActionFactory svn = new SvnActionFactory( svnUrl,
                                                     "mrtrout",
                                                     "drools" );

        CompositeScmAction actions = new CompositeScmAction();

        ScmAction addDirectory = new AddDirectory( "",
                                                "folder1" );
        actions.addScmAction( addDirectory );
        byte[] content = new byte[]{1, 1, 1, 1};
        ScmAction addFile = new AddFile( "folder1",
                                         "file1.dat",
                                         content );
        actions.addScmAction( addFile );
        svn.execute( actions,
                     "test message" );

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        svn.getContent( "folder1",
                        "file1.dat",
                        -1,
                        baos );
        assertTrue( Arrays.equals( content,
                                   baos.toByteArray() ) );

        List list = convertToStringList( svn.listEntries( "" ) );
        assertTrue( list.contains( "folder1" ) );
        assertTrue( list.contains( "folder1/file1.dat" ) );
        assertFalse( list.contains( "folder2/file2.dat" ) );

        // No do the file move
        actions = new CompositeScmAction();
        addDirectory = new AddDirectory( "",
                                      "folder2" );
        actions.addScmAction( addDirectory );
        MoveFile moveFile = new MoveFile( "folder1",
                                          "file1.dat",
                                          "folder2",
                                          "file2.dat",
                                          svn.getLatestRevision() );
        actions.addScmAction( moveFile );
        svn.execute( actions,
                     "test message" );

        baos = new ByteArrayOutputStream();
        svn.getContent( "folder2",
                        "file2.dat",
                        -1,
                        baos );
        assertTrue( Arrays.equals( content,
                                   baos.toByteArray() ) );

        list = convertToStringList( svn.listEntries( "" ) );
        assertTrue( list.contains( "folder1" ) );
        assertFalse( list.contains( "folder1/file1.dat" ) );
        assertTrue( list.contains( "folder2/file2.dat" ) );
    }

    public void testMoveDirectory() throws Exception {
        ScmActionFactory svn = new SvnActionFactory( svnUrl,
                                                     "mrtrout",
                                                     "drools" );

        CompositeScmAction actions = new CompositeScmAction();

        ScmAction addDirectory = new AddDirectory( "",
                                                "folder1" );
        actions.addScmAction( addDirectory );
        byte[] content = new byte[]{1, 1, 1, 1};
        ScmAction addFile = new AddFile( "folder1",
                                         "file1.dat",
                                         content );
        actions.addScmAction( addFile );
        svn.execute( actions,
                     "test message" );

        // check the intial content and dir structure
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        svn.getContent( "folder1",
                        "file1.dat",
                        -1,
                        baos );
        assertTrue( Arrays.equals( content,
                                   baos.toByteArray() ) );

        actions = new CompositeScmAction();
        MoveDirectory moveDirectory = new MoveDirectory( "folder1",
                                                         "folder2",
                                                         svn.getLatestRevision() );
        actions.addScmAction( moveDirectory );
        svn.execute( actions,
                     "test message" );

        // Check the moved content and dir structure
        baos = new ByteArrayOutputStream();
        svn.getContent( "folder2",
                        "file1.dat",
                        -1,
                        baos );
        assertTrue( Arrays.equals( content,
                                   baos.toByteArray() ) );

        List list = convertToStringList( svn.listEntries( "" ) );

        assertFalse( list.contains( "folder1" ) );
        assertFalse( list.contains( "folder1/file1.dat" ) );
        assertTrue( list.contains( "folder2/file1.dat" ) );
    }

    public void testDeleteFile() throws Exception {
        ScmActionFactory svn = new SvnActionFactory( svnUrl,
                                                     "mrtrout",
                                                     "drools" );

        CompositeScmAction actions = new CompositeScmAction();

        ScmAction addDirectory = new AddDirectory( "",
                                                "folder1" );
        actions.addScmAction( addDirectory );
        byte[] content = new byte[]{1, 1, 1, 1};
        ScmAction addFile = new AddFile( "folder1",
                                         "file1.dat",
                                         content );
        actions.addScmAction( addFile );
        svn.execute( actions,
                     "test message" );
        List list = convertToStringList( svn.listEntries( "" ) );
        assertTrue( list.contains( "folder1" ) );
        assertTrue( list.contains( "folder1/file1.dat" ) );

        // Now do the file delete
        actions = new CompositeScmAction();
        ScmAction deleteFile = new DeleteFile( "folder1",
                                               "file1.dat" );
        actions.addScmAction( deleteFile );
        svn.execute( actions,
                     "test message" );
        list = convertToStringList( svn.listEntries( "" ) );
        assertTrue( list.contains( "folder1" ) );
        assertFalse( list.contains( "folder1/file1.dat" ) );
    }

    public void testDeleteDirectory() throws Exception {
        ScmActionFactory svn = new SvnActionFactory( svnUrl,
                                                     "mrtrout",
                                                     "drools" );

        CompositeScmAction actions = new CompositeScmAction();

        ScmAction addDirectory = new AddDirectory( "",
                                                "folder1" );
        actions.addScmAction( addDirectory );
        byte[] content = new byte[]{1, 1, 1, 1};
        ScmAction addFile = new AddFile( "folder1",
                                         "file1.dat",
                                         content );
        actions.addScmAction( addFile );
        addDirectory = new AddDirectory( "",
                                      "folder2" );
        actions.addScmAction( addDirectory );
        svn.execute( actions,
                     "test message" );
        List list = convertToStringList( svn.listEntries( "" ) );
        assertTrue( list.contains( "folder1" ) );
        assertTrue( list.contains( "folder1/file1.dat" ) );
        assertTrue( list.contains( "folder2" ) );

        // now do the directory delete        
        actions = new CompositeScmAction();
        ScmAction deleteDirectory = new DeleteDirectory( "folder1" );
        actions.addScmAction( deleteDirectory );
        svn.execute( actions,
                     "test message" );
        list = convertToStringList( svn.listEntries( "" ) );
        assertFalse( list.contains( "folder1" ) );
        assertFalse( list.contains( "folder1/file1.dat" ) );
        assertTrue( list.contains( "folder2" ) );
    }
    
    public void XXXtestHistory() throws Exception {
        SvnActionFactory svn = new SvnActionFactory( svnUrl,
                                                     "mrtrout",
                                                     "drools" );

        CompositeScmAction actions = new CompositeScmAction();

        ScmAction addDirectory = new AddDirectory( "",
                                                "folder1" );
        actions.addScmAction( addDirectory );
        byte[] content = new byte[]{1, 1, 1, 1};
        ScmAction addFile = new AddFile( "folder1",
                                         "file1.dat",
                                         content );
        actions.addScmAction( addFile );
        svn.execute( actions,
                     "test message" );

        actions = new CompositeScmAction();
        MoveDirectory moveDirectory = new MoveDirectory( "folder1",
                                                         "folder2",
                                                         svn.getLatestRevision() );
        actions.addScmAction( moveDirectory );
        svn.execute( actions,
                     "test message" );
        
        Collection collection = svn.log( new String[] { "" }, 0, -1 );
        for ( Iterator it = collection.iterator(); it.hasNext(); ) {
            SVNLogEntry logEntry = ( SVNLogEntry ) it.next();
            Map map = logEntry.getChangedPaths();
            Set changePathSet = map.keySet();
            for ( Iterator it2 = changePathSet.iterator(); it2.hasNext(); ) {
                SVNLogEntryPath entryPath = ( SVNLogEntryPath ) map.get( it2.next() );
                System.out.println( entryPath );
            }
        }
        
    }    

    public static void copy(File src,
                            File dest) throws IOException {
        if ( src.isDirectory() ) {
            dest.mkdirs();
            String list[] = src.list();

            for ( int i = 0; i < list.length; i++ ) {
                String dest1 = dest.getPath() + File.separator + list[i];
                String src1 = src.getPath() + File.separator + list[i];
                copy( new File( src1 ),
                      new File( dest1 ) );
            }
        } else {
            
            FileInputStream fin = new FileInputStream( src );
            FileOutputStream fout = new FileOutputStream( dest );
            int c;
            while ( (c = fin.read()) >= 0 )
                fout.write( c );
            fin.close();
            fout.close();
        }
    }

    public static void delete(File src) throws IOException {
        if ( src.isDirectory() ) {
            String list[] = src.list();

            for ( int i = 0; i < list.length; i++ ) {
                String src1 = src.getPath() + File.separator + list[i];
                delete( new File( src1 ) );
            }
            src.delete();
        } else {
            src.delete();
        }
    }

    public static List convertToStringList(List list) {
        List files = new ArrayList( list.size() );

        for ( Iterator it = list.iterator(); it.hasNext(); ) {
            ScmEntry entry = (ScmEntry) it.next();
            files.add( entry.getPath().equals( "" ) ? entry.getName() : entry.getPath() + "/" + entry.getName() );
        }
        return files;
    }

    public static Map convertToMap(List list) {
        Map map = new HashMap( list.size() );

        for ( Iterator it = list.iterator(); it.hasNext(); ) {
            ScmEntry entry = (ScmEntry) it.next();
            if ( entry.isDirectory() ) {
                map.put( entry.getPath().equals( "" ) ? entry.getName() : entry.getPath() + "/" + entry.getName(),
                         entry );
            } else {
                List files = (List) map.get( entry.getPath() );
                if ( files == null ) {
                    files = new ArrayList();
                    map.put( entry.getPath(),
                             files );
                }

                files.add( entry );
            }
        }
        return map;
    }
}
