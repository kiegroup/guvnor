package org.drools.scm;

import java.util.Date;

public interface ScmEntry {
    public static final int FILE = 0;
    public static final int DIRECTORY = 0;
    
    
    String  getAuthor();
    Date    getDate(); 
    String  getName(); 
    String  getPath();
    long    getRevision();
    long    getSize(); 
    public boolean isFile();    
    public boolean isDirectory();   
}
