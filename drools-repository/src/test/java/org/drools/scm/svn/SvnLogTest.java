package org.drools.scm.svn;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import junit.framework.TestCase;

import org.drools.scm.CompositeScmAction;
import org.drools.scm.ScmAction;
import org.drools.scm.ScmEntry;
import org.drools.scm.svn.SvnActionFactory.AddDirectory;
import org.drools.scm.svn.SvnActionFactory.AddFile;

public class SvnLogTest extends TestCase {
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
    
    public void testHistory() throws Exception {
        SvnActionFactory svn = new SvnActionFactory( svnUrl,
                                                     "mrtrout",
                                                     "drools" );

        CompositeScmAction actions = new CompositeScmAction();

        ScmAction addFolder = new AddDirectory( "",
                                                "folder1" );
        actions.addScmAction( addFolder );
        byte[] content = new byte[]{1, 1, 1, 1};
        ScmAction addFile = new AddFile( "folder1",
                                         "file1.dat",
                                         content );
        actions.addScmAction( addFile );
        svn.execute( actions,
                     "test message" );
        
        

//        actions = new CompositeSvnAction();
//        MoveDirectory moveDirectory = new MoveDirectory( "folder1",
//                                                         "folder2",
//                                                         svn.getLatestRevision() );
//        actions.addScmAction( moveDirectory );
//        svn.execute( actions,
//                     "test message" );
//        
//        Collection collection = svn.log( new String[] { "" }, 0, -1 );
//        for ( Iterator it = collection.iterator(); it.hasNext(); ) {
//            SVNLogEntry logEntry = ( SVNLogEntry ) it.next();
//            Map map = logEntry.getChangedPaths();
//            Set changePathSet = map.keySet();
//            for ( Iterator it2 = changePathSet.iterator(); it2.hasNext(); ) {
//                SVNLogEntryPath entryPath = ( SVNLogEntryPath ) map.get( it2.next() );
//                System.out.println( entryPath );
//            }
//        }
        
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
    
}
