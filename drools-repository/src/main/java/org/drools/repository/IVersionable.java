package org.drools.repository;

/** All assets that support major versioning must implement this. */
interface IVersionable {
    
    /** must create a fresh copy OF THE SAME TYPE, with a null Id */
    IVersionable copy();
    
    void setVersionNumber(long versionNumber);

    void setVersionComment(String comment);
    
    long getVersionNumber();
    
}
