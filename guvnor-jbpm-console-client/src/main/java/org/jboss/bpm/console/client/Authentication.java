package org.jboss.bpm.console.client;

public interface Authentication {

    public String getUsername();

    public void handleSessionTimeout();
}
