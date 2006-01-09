package org.drools.repository;

/** All assets that support major versioning must implement this. */
interface IVersionable {
    
    /** of course they have to have an id ! 
     * Ids are always assigned by the database.
     */
    Long getId();    
    
    /** Must create a fresh copy OF THE SAME TYPE, with a null Id */
    IVersionable copy();
    
    /** 
     * The version number is used to group assets together in a RuleSet for instance
     * The version number should NOT only be set by the repository, not by users. 
     */
    void setVersionNumber(long versionNumber);

    /** The version comment is used when major versions are created */
    void setVersionComment(String comment);
       
    String getVersionComment();
    
    long getVersionNumber();
    
}
