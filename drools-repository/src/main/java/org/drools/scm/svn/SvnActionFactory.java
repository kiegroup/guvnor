package org.drools.scm.svn;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

//import org.apache.log4j.Logger;
import org.drools.scm.DefaultScmEntry;
import org.drools.scm.ScmAction;
import org.drools.scm.ScmActionFactory;
import org.drools.scm.ScmEntry;

import org.drools.scm.log.ScmLogEntry;
import org.drools.scm.log.ScmLogEntry.Add;
import org.drools.scm.log.ScmLogEntry.Copy;
import org.drools.scm.log.ScmLogEntry.Delete;
import org.drools.scm.log.ScmLogEntry.Replaced;
import org.drools.scm.log.ScmLogEntry.Update;

import org.tmatesoft.svn.core.SVNCommitInfo;
import org.tmatesoft.svn.core.SVNDirEntry;
import org.tmatesoft.svn.core.SVNErrorCode;
import org.tmatesoft.svn.core.SVNErrorMessage;
import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.SVNLogEntry;
import org.tmatesoft.svn.core.SVNLogEntryPath;
import org.tmatesoft.svn.core.SVNNodeKind;
import org.tmatesoft.svn.core.SVNURL;
import org.tmatesoft.svn.core.auth.ISVNAuthenticationManager;
import org.tmatesoft.svn.core.internal.io.dav.DAVRepositoryFactory;
import org.tmatesoft.svn.core.internal.io.fs.FSRepositoryFactory;
import org.tmatesoft.svn.core.internal.io.svn.SVNRepositoryFactoryImpl;
import org.tmatesoft.svn.core.io.ISVNEditor;
import org.tmatesoft.svn.core.io.ISVNWorkspaceMediator;
import org.tmatesoft.svn.core.io.SVNRepository;
import org.tmatesoft.svn.core.io.SVNRepositoryFactory;
import org.tmatesoft.svn.core.io.diff.SVNDeltaGenerator;
import org.tmatesoft.svn.core.wc.SVNWCUtil;

public class SvnActionFactory
    implements
    ScmActionFactory {

//    private static Logger logger = Logger.getLogger( SvnActionFactory.class );
    private SVNRepository repository;

    /*
     * Initializes the library to work with a repository either via svn:// (and
     * svn+ssh://) or via http:// (and https://)
     */
    static {
        // for DAV (over http and https)
        DAVRepositoryFactory.setup();

        // for SVN (over svn and svn+ssh)
        SVNRepositoryFactoryImpl.setup();

        // For File        
        FSRepositoryFactory.setup();
    }

    public SVNRepository getSVNRepository() {
        return this.repository;
    }

    public ScmAction addDirectory(String root,
                                  String path) {
        return new AddDirectory( root,
                                 path );
    }

    public ScmAction addFile(String path,
                             String file,
                             byte[] content) {
        return new AddFile( path,
                            file,
                            content );
    }

    public ScmAction copyDirectory(String path,
                                   String newPath,
                                   long revision) {
        return new CopyDirectory( path,
                                  newPath,
                                  revision );
    }

    public ScmAction copyFile(String path,
                              String file,
                              String newPath,
                              String newFile,
                              long revision) {
        return new CopyFile( path,
                             file,
                             newPath,
                             newFile,
                             revision );
    }

    public ScmAction deleteDirectory(String path) {
        return new DeleteDirectory( path );
    }

    public ScmAction deleteFile(String path,
                                String file) {
        return new DeleteFile( path,
                               file );
    }

    public ScmAction moveDirectory(String path,
                                   String newPath,
                                   long revision) {
        return new MoveDirectory( path,
                                  newPath,
                                  revision );
    }

    public ScmAction moveFile(String path,
                              String file,
                              String newPath,
                              String newFile,
                              long revision) {
        return new MoveFile( path,
                             file,
                             newPath,
                             newFile,
                             revision );
    }

    public ScmAction updateFile(String path,
                                String file,
                                byte[] oldContent,
                                byte[] newContent) {
        return new UpdateFile( path,
                               file,
                               oldContent,
                               newContent );
    }

    public SvnActionFactory(String url,
                            String svnUsername,
                            String svnPassword) throws Exception {
        String username = svnUsername;
        String password = svnPassword;
        try {
            this.repository = SVNRepositoryFactory.create( SVNURL.parseURIEncoded( url ) );
            ISVNAuthenticationManager authManager = SVNWCUtil.createDefaultAuthenticationManager( username,
                                                                                                  password );
            repository.setAuthenticationManager( authManager );

            SVNNodeKind nodeKind = repository.checkPath( "",
                                                         -1 );
            if ( nodeKind == SVNNodeKind.NONE ) {
                SVNErrorMessage err = SVNErrorMessage.create( SVNErrorCode.UNKNOWN,
                                                              "No entry at URL ''{0}''",
                                                              url );
                throw new SVNException( err );
            } else if ( nodeKind == SVNNodeKind.FILE ) {
                SVNErrorMessage err = SVNErrorMessage.create( SVNErrorCode.UNKNOWN,
                                                              "Entry at URL ''{0}'' is a file while directory was expected",
                                                              url );
                throw new SVNException( err );
            }
        } catch ( SVNException e ) {
            e.printStackTrace();
            //logger.error( "svn error: " );
            throw e;
        }
    }
    
    public void getContent(String path, String file, long revision, OutputStream os) throws SVNException  {
        this.repository.getFile( path + "/" + file, revision, null, os);
    }
    
    public List log(String[] paths, long startRevision, long endRevision)  throws SVNException {
        return toScm( this.repository.log( paths, null, startRevision, endRevision, true, false ) );
    }
    
    private List toScm(Collection collection) throws SVNException {
      List list = new ArrayList();
      for ( Iterator it = collection.iterator(); it.hasNext(); ) {
          SVNLogEntry logEntry = ( SVNLogEntry ) it.next();
          Map map = logEntry.getChangedPaths();
          Set changePathSet = map.keySet();          
          
          ScmLogEntry scmLogEntry = new ScmLogEntry(logEntry.getAuthor(),logEntry.getDate(), logEntry.getMessage() );
          for ( Iterator it2 = changePathSet.iterator(); it2.hasNext(); ) {              
              SVNLogEntryPath entryPath = ( SVNLogEntryPath ) map.get( it2.next() );              
              
              switch (entryPath.getType()) {
                  
                  case SVNLogEntryPath.TYPE_ADDED: {
                      SVNDirEntry dirEntry = this.repository.info( entryPath.getPath(), -1 );
                      char type = ( dirEntry.getKind() == SVNNodeKind.DIR ) ? 'D' : 'F';
                      if ( entryPath.getCopyPath() == null ) {
                          // this entry was added
                          Add add = new Add( type, entryPath.getPath(), logEntry.getRevision());
                          scmLogEntry.addAction( add );
                          break;
                      } else {
                          // this entry was copied
                          Copy copy = new Copy( type, entryPath.getCopyPath(), entryPath.getCopyRevision(), entryPath.getPath(), logEntry.getRevision() );
                          scmLogEntry.addAction( copy );
                          break;                 
                      }                      
                  }
                      
                  case SVNLogEntryPath.TYPE_DELETED: {
                      SVNDirEntry dirEntry = this.repository.info( entryPath.getPath(), -1 );
                      char type = ( dirEntry.getKind() == SVNNodeKind.DIR ) ? 'D' : 'F';
                      Delete delete = new Delete( type, entryPath.getPath(), logEntry.getRevision());
                      scmLogEntry.addAction( delete );
                      break;
                  }
                  
                  case SVNLogEntryPath.TYPE_MODIFIED: {
                      SVNDirEntry dirEntry = this.repository.info( entryPath.getPath(), -1 );
                      char type = ( dirEntry.getKind() == SVNNodeKind.DIR ) ? 'D' : 'F';
                      Update update = new Update( type, entryPath.getPath(), logEntry.getRevision());
                      scmLogEntry.addAction( update );
                      break;
                  }
                  
                  case SVNLogEntryPath.TYPE_REPLACED: {
                      SVNDirEntry dirEntry = this.repository.info( entryPath.getPath(), -1 );
                      char type = ( dirEntry.getKind() == SVNNodeKind.DIR ) ? 'D' : 'F';
                      Replaced replaced = new Replaced( type, entryPath.getPath(), logEntry.getRevision());
                      scmLogEntry.addAction( replaced );
                      break;
                  }                                       
              }                            
          }
          list.add( scmLogEntry );
      }        
      return list;
    }

    public long getLatestRevision() throws Exception {
        try {
            return repository.getLatestRevision();
        } catch ( SVNException e ) {
            e.printStackTrace();
            //logger.error( "svn error: " );
            throw e;
        }
    }

    public void execute(ScmAction action,
                        String message) throws Exception {
        try {
            ISVNEditor editor = this.repository.getCommitEditor( message,
                                                                 null );
            editor.openRoot( -1 );
            action.applyAction( editor );
            editor.closeDir();

            SVNCommitInfo info = editor.closeEdit();
        } catch ( SVNException e ) {
            e.printStackTrace();
            //logger.error( "svn error: " );
            throw e;
        }
    }

    public static class AddFile
        implements
        ScmAction {
        private String file;
        private String path;
        private byte[] content;

        public AddFile(String path,
                       String file,
                       byte[] content) {
            this.path = path;
            this.file = file;
            this.content = content;
        }

        public void applyAction(Object context) throws SVNException {
            ISVNEditor editor = ( ISVNEditor ) context;
            openDirectories( editor,
                             path );

            editor.addFile( path + "/" + file,
                            null,
                            -1 );
            editor.applyTextDelta( path + "/" + file,
                                   null );
            SVNDeltaGenerator deltaGenerator = new SVNDeltaGenerator();
            String checksum = deltaGenerator.sendDelta( path + "/" + file,
                                                        new ByteArrayInputStream( this.content ),
                                                        editor,
                                                        true );
            editor.closeFile( path + "/" + file,
                              checksum );

            closeDirectories( editor,
                              path );
        }
    }

    /**
     * root should be the last, previously created, parent folder. Each directory in the path
     * will be created.
     *
     */
    public static class AddDirectory
        implements
        ScmAction {
        private String root;
        private String path;

        public AddDirectory(String root,
                            String path) {
            this.root = root;
            this.path = path;
        }

        public void applyAction(Object context) throws SVNException {
            ISVNEditor editor = ( ISVNEditor ) context;
            openDirectories( editor,
                             this.root );
            String[] paths = this.path.split( "/" );
            String newPath = this.root;
            for ( int i = 0, length = paths.length; i < length; i++ ) {
                newPath = (newPath.length() != 0) ? newPath + "/" + paths[i] : paths[i];

                editor.addDir( newPath,
                               null,
                               -1 );
            }

            closeDirectories( editor,
                              path );
            closeDirectories( editor,
                              this.root );
        }
    }

    public static class UpdateFile
        implements
        ScmAction {
        private String file;
        private String path;
        private byte[] oldContent;
        private byte[] newContent;

        public UpdateFile(String path,
                          String file,
                          byte[] oldContent,
                          byte[] newContent) {
            this.path = path;
            this.file = file;
            this.oldContent = oldContent;
            this.newContent = newContent;
        }

        public void applyAction(Object context) throws SVNException {
            ISVNEditor editor = ( ISVNEditor ) context;
            openDirectories( editor,
                             path );
            editor.openFile( path + "/" + file,
                             -1 );

            editor.applyTextDelta( path + "/" + file,
                                   null );
            SVNDeltaGenerator deltaGenerator = new SVNDeltaGenerator();
            String checksum = deltaGenerator.sendDelta( path + "/" + file,
                                                        new ByteArrayInputStream( this.oldContent ),
                                                        0,
                                                        new ByteArrayInputStream( this.newContent ),
                                                        editor,
                                                        true );
            editor.closeFile( path + "/" + file,
                              checksum );
            closeDirectories( editor,
                              path );
        }
    }

    public static class CopyFile
        implements
        ScmAction {
        private String file;
        private String path;
        private String newPath;
        private String newFile;
        private long   revision;

        public CopyFile(String path,
                        String file,
                        String newPath,
                        String newFile,
                        long revision) {
            this.path = path;
            this.file = file;
            this.newPath = newPath;
            this.newFile = newFile;
            this.revision = revision;
        }

        public void applyAction(Object context) throws SVNException {
            ISVNEditor editor = ( ISVNEditor ) context;
            editor.addFile( newPath + "/" + newFile,
                            path + "/" + file,
                            revision );
        }
    }

    public static class CopyDirectory
        implements
        ScmAction {
        private String path;
        private String newPath;
        private long   revision;

        public CopyDirectory(String path,
                             String newPath,
                             long revision) {
            this.path = path;
            this.newPath = newPath;
            this.revision = revision;
        }

        public void applyAction(Object context) throws SVNException {
            ISVNEditor editor = ( ISVNEditor ) context;
            editor.addDir( newPath,
                           path,
                           revision );
            editor.closeDir();
        }
    }

    public static class MoveFile
        implements
        ScmAction {
        private String file;
        private String path;
        private String newPath;
        private String newFile;
        private long   revision;

        public MoveFile(String path,
                        String file,
                        String newPath,
                        String newFile,
                        long revision) {
            this.path = path;
            this.file = file;
            this.newPath = newPath;
            this.newFile = newFile;
            this.revision = revision;
        }

        public void applyAction(Object context) throws SVNException {
            ISVNEditor editor = ( ISVNEditor ) context;
            CopyFile copyFile = new CopyFile( path,
                                              file,
                                              newPath,
                                              newFile,
                                              revision );
            DeleteFile deleteFile = new DeleteFile( path,
                                                    file );

            copyFile.applyAction( editor );
            deleteFile.applyAction( editor );

        }
    }

    public static class MoveDirectory
        implements
        ScmAction {
        private String path;
        private String newPath;
        private long   revision;

        public MoveDirectory(String path,
                             String newPath,
                             long revision) {
            this.path = path;
            this.newPath = newPath;
            this.revision = revision;
        }

        public void applyAction(Object context) throws SVNException {
            ISVNEditor editor = ( ISVNEditor ) context;
            CopyDirectory copyDirectory = new CopyDirectory( path,
                                                             newPath,
                                                             revision );
            DeleteDirectory deleteDirectory = new DeleteDirectory( path );

            copyDirectory.applyAction( editor );
            deleteDirectory.applyAction( editor );

        }
    }

    public static class DeleteFile
        implements
        ScmAction {
        private String path;
        private String file;

        public DeleteFile(String path,
                          String file) {
            this.path = path;
            this.file = file;
        }

        public void applyAction(Object context) throws SVNException {
            ISVNEditor editor = ( ISVNEditor ) context;
            openDirectories( editor,
                             path );
            editor.deleteEntry( path + "/" + file,
                                -1 );
            closeDirectories( editor,
                              path );
        }
    }

    public static class DeleteDirectory
        implements
        ScmAction {
        private String path;

        public DeleteDirectory(String path) {
            this.path = path;
        }

        public void applyAction(Object context) throws SVNException {
            ISVNEditor editor = ( ISVNEditor ) context;
            openDirectories( editor,
                             path );
            editor.deleteEntry( path,
                                -1 );
            closeDirectories( editor,
                              path );
        }
    }

    public static class CommitMediator
        implements
        ISVNWorkspaceMediator {
        private Map myTmpStorages = new HashMap();

        /*
         * This may be implemented to get properties from 
         * '.svn/wcprops'
         */
        public String getWorkspaceProperty(String path,
                                           String name) throws SVNException {
            return null;
        }

        /*
         * This may be implemented to set properties in 
         * '.svn/wcprops'
         */
        public void setWorkspaceProperty(String path,
                                         String name,
                                         String value) throws SVNException {
        }

        /*
         * Creates a temporary file delta  storage. id  will be  
         * used as the temporary storage identifier. Returns an  
         * OutputStream to write the delta data into the temporary 
         * storage.
         */
        public OutputStream createTemporaryLocation(String path,
                                                    Object id) throws IOException {
            ByteArrayOutputStream tempStorageOS = new ByteArrayOutputStream();
            myTmpStorages.put( id,
                               tempStorageOS );
            return tempStorageOS;
        }

        /*
         * Returns an InputStream of the temporary file delta 
         * storage identified by id to read the delta.
         */
        public InputStream getTemporaryLocation(Object id) throws IOException {
            return new ByteArrayInputStream( ((ByteArrayOutputStream) myTmpStorages.get( id )).toByteArray() );
        }

        /*
         * Gets the length of the delta that was written  
         * to the temporary storage identified by id.
         */
        public long getLength(Object id) throws IOException {
            ByteArrayOutputStream tempStorageOS = (ByteArrayOutputStream) myTmpStorages.get( id );
            if ( tempStorageOS != null ) {
                return tempStorageOS.size();
            }
            return 0;
        }

        /*
         * Deletes the temporary file delta storage identified 
         * by id.
         */
        public void deleteTemporaryLocation(Object id) {
            myTmpStorages.remove( id );
        }
    }

    public static void openDirectories(ISVNEditor editor,
                                       String path) throws SVNException {
        int pos = path.indexOf( '/',
                                0 );
        while ( pos != -1 ) {
            editor.openDir( path.substring( 0,
                                            pos ),
                            -1 );
            pos = path.indexOf( '/',
                                pos + 1 );
        }
        editor.openDir( path.substring( 0,
                                        path.length() ),
                        -1 );
    }

    public static void closeDirectories(ISVNEditor editor,
                                        String path) throws SVNException {
        int length = path.length() - 1;
        int pos = path.lastIndexOf( '/',
                                    length );
        editor.closeDir();
        while ( pos != -1 ) {
            editor.closeDir();
            pos = path.lastIndexOf( '/',
                                    pos - 1 );
        }
    }

    public List listEntries(String path) throws SVNException {
        List entries = new ArrayList();
        listEntries( path,
                     entries );
        return entries;
    }

    public void listEntries(String path,
                            List list) throws SVNException {
        Collection entries = this.repository.getDir( path,
                                                     -1,
                                                     null,
                                                     (Collection) null );
        Iterator iterator = entries.iterator();
        while ( iterator.hasNext() ) {
            SVNDirEntry svnEntry = (SVNDirEntry) iterator.next();

            DefaultScmEntry scmEntry = new DefaultScmEntry();
            scmEntry.setPath( path.equals( "" ) ? path : path.substring( 1 ) );
            scmEntry.setName( svnEntry.getName() );
            scmEntry.setAuthor( svnEntry.getAuthor() );
            scmEntry.setDate( svnEntry.getDate() );
            scmEntry.setRevision( svnEntry.getRevision() );
            scmEntry.setSize( svnEntry.getSize() );
            scmEntry.setType( (svnEntry.getKind() == SVNNodeKind.DIR) ? ScmEntry.DIRECTORY : ScmEntry.FILE );
            list.add( scmEntry );

            if ( svnEntry.getKind() == SVNNodeKind.DIR ) {
                listEntries( path + "/" + svnEntry.getName(),
                             list );
            }
        }
    }

}
