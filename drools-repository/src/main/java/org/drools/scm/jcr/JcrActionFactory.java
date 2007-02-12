package org.drools.scm.jcr;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.util.Iterator;
import java.util.List;

import org.drools.repository.RulesRepository;
import org.drools.scm.ScmAction;
import org.drools.scm.ScmActionFactory;
import org.drools.scm.log.ScmLogEntry;
import org.drools.scm.log.ScmLogEntryItem;
import org.drools.scm.log.ScmLogEntry.Add;
import org.drools.scm.log.ScmLogEntry.Copy;
import org.drools.scm.log.ScmLogEntry.Delete;
import org.drools.scm.log.ScmLogEntry.Update;

public class JcrActionFactory
    implements
    ScmActionFactory {
    
    private RulesRepository repository;

    public ScmAction addDirectory(String root,
                                  String path) {
        return null;
    }

    public ScmAction addFile(String path,
                             String file,
                             byte[] content) {
        return null;
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
        return null;
    }

    public void listEntries(String path,
                            List list) throws Exception {
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
        return null;
    }
    
    public void syncToScmLog(List list, ScmActionFactory factory) throws Exception { 
        for ( Iterator it = list.iterator(); it.hasNext(); ) {
            ScmLogEntry entry = (ScmLogEntry) it.next();
            for ( Iterator it2 = entry.getAction().iterator(); it.hasNext(); ) {
                ScmLogEntryItem item = (ScmLogEntryItem) it2.next();
                ScmAction action;                
                switch ( item.getActionType() ) {
                    case 'A': {
                        Add add = (Add) item;
                        if ( add.getPathType() == 'D'  ) {
                            addDirectory( "", add.getPath() );
                        } else {
                            int lastSlash = add.getPath().lastIndexOf( '/' ); 
                            String path = add.getPath().substring( 0, lastSlash - 1 );
                            String file = add.getPath().substring( lastSlash + 1, add.getPath().length() -1 );
                            
                            ByteArrayOutputStream bos = new ByteArrayOutputStream();
                            factory.getContent( path, file, -1, bos );
                            action = addFile(path, file, bos.toByteArray() );
                        }
                        break;
                    }
                    case 'C': {
                        Copy copy = (Copy) item;                        
                        if ( copy.getPathType() == 'D'  ) {
                            action =copyDirectory( copy.getFromPath(), copy.getToPath(), copy.getFromRevision() );
                        } else {
                            int lastSlash = copy.getFromPath().lastIndexOf( '/' ); 
                            String fromPath = copy.getFromPath().substring( 0, lastSlash - 1 );
                            String fromFile = copy.getFromPath().substring( lastSlash + 1, copy.getFromPath().length() -1 );
                            
                            lastSlash = copy.getToPath().lastIndexOf( '/' );
                            String toPath = copy.getToPath().substring( 0, lastSlash - 1 );
                            String toFile = copy.getToPath().substring( lastSlash + 1, copy.getToPath().length() -1 );                            
                            action = copyFile( fromPath, fromFile, toPath, toFile, copy.getFromRevision() );
                        }
                        
                        break; 
                    }
                    case 'D': {
                        Delete delete = (Delete) item;                        
                        if ( delete.getPathType() == 'D'  ) {
                            action = deleteDirectory( delete.getPath() );
                        } else {
                            int lastSlash = delete.getPath().lastIndexOf( '/' ); 
                            String path = delete.getPath().substring( 0, lastSlash - 1 );
                            String file = delete.getPath().substring( lastSlash + 1, delete.getPath().length() -1 );
                            
                            action = deleteFile( path, file );
                        }                        
                        break;
                    }
                    case 'U':
                        // Can only be a file
                        Update update = (Update) item;
                        int lastSlash = update.getPath().lastIndexOf( '/' ); 
                        String path = update.getPath().substring( 0, lastSlash - 1 );
                        String file = update.getPath().substring( lastSlash + 1, update.getPath().length() -1 );

                        ByteArrayOutputStream bosOriginal = new ByteArrayOutputStream();
                        getContent( path, file, -1, bosOriginal );
                        
                        ByteArrayOutputStream bosNew = new ByteArrayOutputStream();
                        factory.getContent( path, file, update.getRevision(), bosNew );
                        
                        action = updateFile( path, file, bosOriginal.toByteArray(), bosNew.toByteArray() );
                        
                        break;
                    case 'R':
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
    

}
