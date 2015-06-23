/*
 * Copyright 2015 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/

package org.guvnor.structure.repositories;

import org.jboss.errai.common.client.api.annotations.Portable;

@Portable
public class NewRepositoryEvent {

    private Repository newRepository;

    private String groupId;
    private String artifactId;
    private String version;

    public NewRepositoryEvent() {
    }

    public NewRepositoryEvent( final Repository newRepository ) {
        this.newRepository = newRepository;
    }

    /*
     * For Managed Repositories
    */
    public NewRepositoryEvent(Repository newRepository, String groupId, String artifactId, String version) {
      this.newRepository = newRepository;
      this.groupId = groupId;
      this.artifactId = artifactId;
      this.version = version;
    }

    public Repository getNewRepository() {
        return newRepository;
    }

    public void setNewRepository( final Repository newRepository ) {
        this.newRepository = newRepository;
    }

    public String getGroupId() {
      return groupId;
    }

    public String getArtifactId() {
      return artifactId;
    }

    public String getVersion() {
      return version;
    }


}
