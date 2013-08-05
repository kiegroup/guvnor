/*
 * Copyright 2013 JBoss Inc
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 */

package org.guvnor.udc.service;

import org.guvnor.udc.model.UsageEventSummary;

public class UsageEventSummaryBuilder {
	
    protected static final String INFO = "INFO";
    protected static final String AUDIT = "AUDIT";
    protected static final String SUCCESS = "SUCCESS";
    private final UsageEventSummary usageEventSummary = new UsageEventSummary();

    public UsageEventSummaryBuilder key( final String key ) {
    	usageEventSummary.setKey(key);
        return this;
    }

    public UsageEventSummaryBuilder description( final String description ) {
    	usageEventSummary.setDescription(description);
        return this;
    }

    public UsageEventSummaryBuilder from( final String from ) {
    	usageEventSummary.setFrom(from);
        return this;
    }
    
    public UsageEventSummaryBuilder toUser( final String touser ) {
        usageEventSummary.setToUser(touser);
        return this;
    }
    
    public UsageEventSummaryBuilder component( final String component ) {
    	usageEventSummary.setComponent(component);
        return this;
    }
    
    public UsageEventSummaryBuilder action( final String action ) {
    	usageEventSummary.setAction(action);
        return this;
    }

    public UsageEventSummaryBuilder module( final String module ) {
    	usageEventSummary.setModule(module);
        return this;
    }

    public UsageEventSummaryBuilder status( final String status ) {
    	usageEventSummary.setStatus(status); 
        return this;
    }
    
    private void defaultValues(){
        if(usageEventSummary.getModule()==null){
            usageEventSummary.setModule("-");
        }
        if(usageEventSummary.getLevel()==null){
            usageEventSummary.setLevel(INFO);
        }
        if(usageEventSummary.getAction()==null){
            usageEventSummary.setAction(AUDIT);
        }
        if(usageEventSummary.getStatus()==null){
            usageEventSummary.setStatus(SUCCESS);
        }
    }
    
    public UsageEventSummaryBuilder itemPath(String itemPath){
        usageEventSummary.setItemPath(itemPath);
        return this;
    }
    
    public UsageEventSummaryBuilder fileName(String fileName){
        usageEventSummary.setFileName(fileName);
        return this;
    }
    
    public UsageEventSummaryBuilder fileSystem(String fileSystem){
        usageEventSummary.setFileSystem(fileSystem);
        return this;
    }
    
    public UsageEventSummaryBuilder level(final String level) {
    	usageEventSummary.setLevel(level); 
        return this;
    }
    public UsageEventSummary build() {
        defaultValues();
        return usageEventSummary;
    }

}
