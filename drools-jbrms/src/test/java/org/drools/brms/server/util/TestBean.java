package org.drools.brms.server.util;

import java.util.Calendar;

public class TestBean {
    
    
    private String publisher = "42";
    private String creator = "42";
    private Calendar created = Calendar.getInstance();
    
    public String getPublisher() {
        return publisher;
    }
    
    public String getCreator() {
        return creator;
    }
    
    /** this should be ignored */
    public void getCoverage() {
        
    }
    
    public void updatePublisher(String pub) {
        this.publisher = pub;
    }
    
    public void updateCreator(String c) {
        this.creator = c;
    }
    
    public Calendar getCreatedDate() {
        return created ;
    }
    
}
