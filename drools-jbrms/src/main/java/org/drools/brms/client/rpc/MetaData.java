package org.drools.brms.client.rpc;

import java.util.Date;

import com.google.gwt.user.client.rpc.IsSerializable;

/**
 * This is the DTO for a versionable asset's meta data.
 * ie basically everything except the payload.
 */
public class MetaData
    implements
    IsSerializable {

    public String name = "";
    public String description = "";
    
    public String title = "";
    public String state = "";

    public Date lastModifiedDate;
    public String lastContributor = "";
    public String versionNumber;

    public Date createdDate;
    
    public String packageName = "";
    public String[] categories = new String[0];
    
    public String format = "";
    public String type = "";
    public String creator = "";
    public String externalSource = "";
    public String subject = "";
    public String externalRelation = "";
    public String rights = ""; 
    public String coverage = "";
    public String publisher = "";   
    public String checkinComment = "";
    
    
    public Date dateEffective;
    public Date dateExpired;
    
    /** used to flag dirty - ie needs to be spanked. Or saved to the repo, whatever */
    public boolean dirty = false;
    
    /**
     * Remove a category.
     * @param idx The index of the cat to remove.
     */
    public void removeCategory(int idx) {
        String[] newList = new String[categories.length - 1];
        int newIdx = 0;
        for ( int i = 0; i < categories.length; i++ ) {
            
            if (i != idx) {
                newList[newIdx] = categories[i];
                newIdx++;
            }
            
        }
        this.categories = newList;
    }
    
    /**
     * Add the given cat to the end of the cat list.
     */
    public void addCategory(String cat) {
        for ( int i = 0; i < this.categories.length; i++ ) {
            if (categories[i].equals( cat )) return;
        }
        String[] list = this.categories;
        String[] newList = new String[list.length + 1];
        
        for ( int i = 0; i < list.length; i++ ) {
            newList[i] =  list[i];
        }
        newList[list.length] = cat; 
        
        this.categories = newList;           
    }

}
