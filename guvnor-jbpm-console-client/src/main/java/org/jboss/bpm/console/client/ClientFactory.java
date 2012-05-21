package org.jboss.bpm.console.client;

import com.mvc4g.client.Controller;

public interface ClientFactory {

    public ApplicationContext getApplicationContext();

    public Controller getController();

}
