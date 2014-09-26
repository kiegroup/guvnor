/*
 * Copyright 2014 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.guvnor.asset.management.social;

import org.jboss.errai.common.client.api.annotations.Portable;

@Portable
public class ProcessEndEvent {

    private String processName;

    private String user;

    private String repositoryAlias;

    private String rootURI;

    private Long timestamp;

    public ProcessEndEvent() {
    }

    public ProcessEndEvent( String processName, String repositoryAlias, String rootURI, String user, Long timestamp ) {
        this.processName = processName;
        this.repositoryAlias = repositoryAlias;
        this.rootURI = rootURI;
        this.user = user;
        this.timestamp = timestamp;
    }

    public String getProcessName() {
        return processName;
    }

    public void setProcessName( String processName ) {
        this.processName = processName;
    }

    public String getUser() {
        return user;
    }

    public void setUser( String user ) {
        this.user = user;
    }

    public String getRepositoryAlias() {
        return repositoryAlias;
    }

    public void setRepositoryAlias( String repositoryAlias ) {
        this.repositoryAlias = repositoryAlias;
    }

    public String getRootURI() {
        return rootURI;
    }

    public void setRootURI( String rootURI ) {
        this.rootURI = rootURI;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp( Long timestamp ) {
        this.timestamp = timestamp;
    }
}