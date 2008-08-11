package org.drools.scm;

import java.io.OutputStream;
import java.util.List;


public interface ScmActionFactory {
    public long getLatestRevision() throws Exception ;

    public ScmAction addFile(String path,
                             String file,
                             byte[] content);

    public ScmAction addDirectory(String root,
                                  String path);

    public ScmAction updateFile(String path,
                                String file,
                                byte[] oldContent,
                                byte[] newContent);

    public ScmAction copyFile(String path,
                              String file,
                              String newPath,
                              String newFile,
                              long revision);

    public ScmAction copyDirectory(String path,
                                   String newPath,
                                   long revision);

    public ScmAction moveFile(String path,
                              String file,
                              String newPath,
                              String newFile,
                              long revision);

    public ScmAction moveDirectory(String path,
                                   String newPath,
                                   long revision);

    public ScmAction deleteFile(String path,
                                String file);

    public ScmAction deleteDirectory(String path);
    
    public void execute(ScmAction action,
                        String message) throws Exception;
    
    public void getContent(String path, String file, long revision, OutputStream os) throws Exception;    
    
    public List listEntries(String path) throws Exception;    
    
}
