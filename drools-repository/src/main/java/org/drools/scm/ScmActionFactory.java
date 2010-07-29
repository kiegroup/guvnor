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
