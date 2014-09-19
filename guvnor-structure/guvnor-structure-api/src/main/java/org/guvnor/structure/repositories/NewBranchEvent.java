package org.guvnor.structure.repositories;

import org.jboss.errai.common.client.api.annotations.Portable;
import org.uberfire.backend.vfs.Path;

@Portable
public class NewBranchEvent {

    private Repository repository;

    private String name;

    private Path path;


    public NewBranchEvent() {
    }

    public NewBranchEvent( Repository repository, Path path, String name ) {
        this.repository = repository;
        this.path = path;
        this.name = name;
    }

    public Repository getRepository() {
        return repository;
    }

    public void setRepository( Repository repository ) {
        this.repository = repository;
    }

    public String getName() {
        return name;
    }

    public void setName( String name ) {
        this.name = name;
    }

    public Path getPath() {
        return path;
    }

    public void setPath( Path path ) {
        this.path = path;
    }
}