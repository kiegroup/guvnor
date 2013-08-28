package org.guvnor.common.services.project.builder.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.guvnor.common.services.project.model.GAV;
import org.jboss.errai.common.client.api.annotations.Portable;

@Portable
public class DeployResult {

    private GAV gav;
    private List<BuildMessage> messages = new ArrayList<BuildMessage>();
    private List<BuildMessage> deployMessages = new ArrayList<BuildMessage>();

    public DeployResult() {
        //Marshalling
    }

    public DeployResult( final GAV gav ) {
        this.gav = gav;
    }

    public GAV getGAV() {
        return gav;
    }

    public List<BuildMessage> getMessages() {
        return Collections.unmodifiableList( messages );
    }

    public void setBuildMessages( final List<BuildMessage> messages ) {
        this.messages = messages;
    }

    public List<BuildMessage> getDeployMessages() {
        return Collections.unmodifiableList( deployMessages );
    }

    public void addDeployMessage( final BuildMessage message ) {
        this.deployMessages.add( message );
    }

}
