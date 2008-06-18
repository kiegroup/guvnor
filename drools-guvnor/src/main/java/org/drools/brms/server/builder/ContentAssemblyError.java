package org.drools.brms.server.builder;
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



import org.drools.repository.VersionableItem;

/**
 * This class is used to accumulate error reports for asset.
 * This can then be used to feed back to the user where the problems are.
 * 
 * @author Michael Neale
 */
public class ContentAssemblyError {

    public ContentAssemblyError(VersionableItem it, String message) {
        this.itemInError = it;
        this.errorReport = message;
    }
    /**
     * This may be null, if its not associated to any particular asset.
     */
    public VersionableItem itemInError;
    public String errorReport;
    
    public String toString() {
        return this.errorReport;
    }
    
}