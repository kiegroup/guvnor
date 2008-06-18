package org.drools.brms.server.util;
/*
 * Copyright 2005 JBoss Inc
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