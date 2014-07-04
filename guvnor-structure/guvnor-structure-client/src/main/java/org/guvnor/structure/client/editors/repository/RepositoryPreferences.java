package org.guvnor.structure.client.editors.repository;

public class RepositoryPreferences {

    private boolean ouMandatory;

    public RepositoryPreferences() {

    }

    public RepositoryPreferences( boolean ouMandatory ) {
        this.ouMandatory = ouMandatory;
    }

    public boolean isOUMandatory() {
        return ouMandatory;
    }
}
