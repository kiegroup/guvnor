package org.guvnor.structure.client.editors.repository;

public class RepositoryPreferences {

    private final boolean ouMandatory;

    public RepositoryPreferences( boolean ouMandatory ) {
        this.ouMandatory = ouMandatory;
    }

    public boolean isOUMandatory() {
        return ouMandatory;
    }
}
