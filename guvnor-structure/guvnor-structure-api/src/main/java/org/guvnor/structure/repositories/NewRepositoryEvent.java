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
