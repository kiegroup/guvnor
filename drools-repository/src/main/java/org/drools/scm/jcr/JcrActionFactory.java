package org.drools.scm.jcr;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;

import org.drools.repository.AssetItem;
import org.drools.repository.PackageItem;
import org.drools.repository.RulesRepository;
import org.drools.scm.DefaultScmEntry;
import org.drools.scm.ScmAction;
import org.drools.scm.ScmActionFactory;
import org.drools.scm.ScmEntry;
import org.drools.scm.log.ScmLogEntry;
import org.drools.scm.log.ScmLogEntryItem;
import org.drools.scm.log.ScmLogEntry.Add;
import org.drools.scm.log.ScmLogEntry.Copy;
import org.drools.scm.log.ScmLogEntry.Delete;
import org.drools.scm.log.ScmLogEntry.Update;
import org.tmatesoft.svn.core.SVNNodeKind;

public class JcrActionFactory
    implements
    ScmActionFactory {

    private RulesRepository repository;

    public JcrActionFactory(RulesRepository repo) {
        this.repository = repo;
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
        return null;
    }

    public ScmAction copyFile(String path,
                              String file,
                              String newPath,
                              String newFile,
                              long revision) {
        return null;
    }

    public ScmAction deleteDirectory(String path) {
        return null;
    }

    public ScmAction deleteFile(String path,
                                String file) {
        return null;
    }

    public void execute(ScmAction action,
                        String message) throws Exception {

        
        action.applyAction( new RepositoryContext( repository,
                                                   message ) );
        repository.save();
    }

    public void getContent(String path,
                           String file,
                           long revision,
                           OutputStream os) throws Exception {
    }

    public long getLatestRevision() throws Exception {
        return 0;
    }

    public List listEntries(String path) throws Exception {
        // Build a list of packages which are for this path and all sub paths
        List pkgs = new ArrayList();
        String pathAsPackageName = toPackageName( path );
        for ( Iterator it = this.repository.listPackages(); it.hasNext(); ) {
            PackageItem pkgItem = (PackageItem) it.next();
            if ( pkgItem.getName().startsWith( pathAsPackageName ) ) {
                pkgs.add( pkgItem );
            }

        }

        // now sort so that it's directory listing order
        Collections.sort( pkgs,
                          new PackagePathComparator() );

        // Now iterate each directory create an ScmEntry and then add an ScmEntry for each child
        List entries = new ArrayList();
        String parentPath = path;
        for ( Iterator pkgIter = pkgs.iterator(); pkgIter.hasNext(); ) {
            PackageItem item = (PackageItem) pkgIter.next();

            DefaultScmEntry scmEntry = new DefaultScmEntry();
            scmEntry.setPath( parentPath );
            String name = toDirectoryName( item.getName() ).substring( parentPath.length() );
            scmEntry.setName( name );
            scmEntry.setAuthor( item.getPublisher() );
            scmEntry.setDate( item.getLastModified().getTime() );
            scmEntry.setRevision( new Long( item.getVersionNumber() ).longValue() );
            scmEntry.setSize( 0 );
            scmEntry.setType( ScmEntry.DIRECTORY );
            entries.add( scmEntry );

            String pkgNameAsPath = toDirectoryName( item.getName() );
            for ( Iterator assetIter = item.getAssets(); assetIter.hasNext(); ) {
                AssetItem assetItem = (AssetItem) assetIter.next();

                if (!(assetItem.getVersionNumber() == 0)) {
                
                    scmEntry = new DefaultScmEntry();
                    scmEntry.setPath( pkgNameAsPath );
                    scmEntry.setName( toFileName( assetItem ) );
                    scmEntry.setAuthor( assetItem.getPublisher() );
                    scmEntry.setDate( assetItem.getLastModified().getTime() );
                    scmEntry.setRevision( new Long( assetItem.getVersionNumber() ).longValue() );
                    scmEntry.setSize( 0 );
                    scmEntry.setType( ScmEntry.FILE );
                    entries.add( scmEntry );
                }
            }
        }

        return entries;
    }

    public static class PackagePathComparator
        implements
        Comparator {
        public int compare(Object object0,
                           Object object1) {
            PackageItem item0 = (PackageItem) object0;
            PackageItem item1 = (PackageItem) object1;

            return item0.getName().compareTo( item1.getName() );
        }
    }

    public ScmAction moveDirectory(String path,
                                   String newPath,
                                   long revision) {
        return null;
    }

    public ScmAction moveFile(String path,
                              String file,
                              String newPath,
                              String newFile,
                              long revision) {
        return null;
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

    public void syncToScmLog(List list,
                             ScmActionFactory factory) throws Exception {
        for ( Iterator it = list.iterator(); it.hasNext(); ) {
            ScmLogEntry entry = (ScmLogEntry) it.next();
            for ( Iterator it2 = entry.getAction().iterator(); it.hasNext(); ) {
                ScmLogEntryItem item = (ScmLogEntryItem) it2.next();
                ScmAction action;
                switch ( item.getActionType() ) {
                    case 'A' : {
                        Add add = (Add) item;
                        if ( add.getPathType() == 'D' ) {
                            addDirectory( "",
                                          add.getPath() );
                        } else {
                            int lastSlash = add.getPath().lastIndexOf( '/' );
                            String path = add.getPath().substring( 0,
                                                                   lastSlash - 1 );
                            String file = add.getPath().substring( lastSlash + 1,
                                                                   add.getPath().length() - 1 );

                            ByteArrayOutputStream bos = new ByteArrayOutputStream();
                            factory.getContent( path,
                                                file,
                                                -1,
                                                bos );
                            action = addFile( path,
                                              file,
                                              bos.toByteArray() );
                        }
                        break;
                    }
                    case 'C' : {
                        Copy copy = (Copy) item;
                        if ( copy.getPathType() == 'D' ) {
                            action = copyDirectory( copy.getFromPath(),
                                                    copy.getToPath(),
                                                    copy.getFromRevision() );
                        } else {
                            int lastSlash = copy.getFromPath().lastIndexOf( '/' );
                            String fromPath = copy.getFromPath().substring( 0,
                                                                            lastSlash - 1 );
                            String fromFile = copy.getFromPath().substring( lastSlash + 1,
                                                                            copy.getFromPath().length() - 1 );

                            lastSlash = copy.getToPath().lastIndexOf( '/' );
                            String toPath = copy.getToPath().substring( 0,
                                                                        lastSlash - 1 );
                            String toFile = copy.getToPath().substring( lastSlash + 1,
                                                                        copy.getToPath().length() - 1 );
                            action = copyFile( fromPath,
                                               fromFile,
                                               toPath,
                                               toFile,
                                               copy.getFromRevision() );
                        }

                        break;
                    }
                    case 'D' : {
                        Delete delete = (Delete) item;
                        if ( delete.getPathType() == 'D' ) {
                            action = deleteDirectory( delete.getPath() );
                        } else {
                            int lastSlash = delete.getPath().lastIndexOf( '/' );
                            String path = delete.getPath().substring( 0,
                                                                      lastSlash - 1 );
                            String file = delete.getPath().substring( lastSlash + 1,
                                                                      delete.getPath().length() - 1 );

                            action = deleteFile( path,
                                                 file );
                        }
                        break;
                    }
                    case 'U' :
                        // Can only be a file
                        Update update = (Update) item;
                        int lastSlash = update.getPath().lastIndexOf( '/' );
                        String path = update.getPath().substring( 0,
                                                                  lastSlash - 1 );
                        String file = update.getPath().substring( lastSlash + 1,
                                                                  update.getPath().length() - 1 );

                        ByteArrayOutputStream bosOriginal = new ByteArrayOutputStream();
                        getContent( path,
                                    file,
                                    -1,
                                    bosOriginal );

                        ByteArrayOutputStream bosNew = new ByteArrayOutputStream();
                        factory.getContent( path,
                                            file,
                                            update.getRevision(),
                                            bosNew );

                        action = updateFile( path,
                                             file,
                                             bosOriginal.toByteArray(),
                                             bosNew.toByteArray() );

                        break;
                    case 'R' :
                        // @TODO this is a delete and add
                        break;
                }

            }
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

        public void applyAction(Object context) throws Exception {
            RepositoryContext ctx = (RepositoryContext) context;

            PackageItem pkg = ctx.repository.loadPackage( toPackageName( path ) );

            StringTokenizer tk = new StringTokenizer( file,
                                                      "." );

            String name = tk.nextToken();
            String format = tk.nextToken();

            AssetItem asset = pkg.addAsset( name,
                                            ctx.message );
            asset.updateFormat( format );
            asset.updateContent( new String( content ) );
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

        public void applyAction(Object context) throws Exception {
            RepositoryContext ctx = (RepositoryContext) context;

            if ( !this.root.equals( "" ) ) {
                PackageItem pkgItem = ctx.repository.loadPackage( toPackageName( this.root ) );
                if ( pkgItem == null ) {
                    throw new RuntimeException( "The parent package '" + this.root + "' must exist" );
                }
            }

            PackageItem item = ctx.repository.createPackage( toPackageName( root + "/" + this.path ),
                                                             "initial package" );
            //item.checkin( "save" );
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

        public void applyAction(Object context) throws Exception {
            RepositoryContext ctx = (RepositoryContext) context;
            PackageItem pkg = ctx.repository.loadPackage( toPackageName( path ) );
            String name = file.substring( 0,
                                          file.indexOf( '.' ) );
            AssetItem asset = pkg.loadAsset( name );
            asset.updateContent( new String( newContent ) );
            asset.checkin( ctx.message );
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

        public void applyAction(Object context) throws Exception {

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

        public void applyAction(Object context) throws Exception {
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

        public void applyAction(Object context) throws Exception {
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

        public void applyAction(Object context) throws Exception {

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

        public void applyAction(Object context) throws Exception {
        }
    }

    public static class DeleteDirectory
        implements
        ScmAction {
        private String path;

        public DeleteDirectory(String path) {
            this.path = path;
        }

        public void applyAction(Object context) throws Exception {
        }
    }

    /**
     * This is used for passing in a context to perform the actions.
     */
    public static class RepositoryContext {

        public RepositoryContext(RulesRepository repository2,
                                 String message2) {
            this.repository = repository2;
            this.message = message2;
        }

        public RulesRepository repository;
        public String          message;
    }

    private static String convertPath(String path,
                                      String token,
                                      String replace) {
        if ( path.indexOf( token ) == -1 ) return path;
        StringTokenizer tk = new StringTokenizer( path,
                                                  token );
        StringBuffer buf = new StringBuffer();
        while ( tk.hasMoreTokens() ) {
            String el = tk.nextToken();
            buf.append( el );
            if ( tk.hasMoreTokens() ) buf.append( replace );
        }
        return buf.toString();
    }

    static String toDirectoryName(String packageName) {
        return convertPath( packageName,
                            ".",
                            "/" );
    }

    static String toPackageName(String directory) {
        return convertPath( directory,
                            "/",
                            "." );
    }

    static String toFileName(AssetItem item) {
        return item.getName() + "." + item.getFormat();
    }

}
