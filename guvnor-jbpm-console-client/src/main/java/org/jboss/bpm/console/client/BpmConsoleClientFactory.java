package org.jboss.bpm.console.client;

import com.mvc4g.client.Controller;
import org.drools.guvnor.client.perspective.PerspectiveFactory;
import org.drools.guvnor.client.rpc.ModuleServiceAsync;

public interface BpmConsoleClientFactory extends org.drools.guvnor.client.explorer.ClientFactory {

    public ApplicationContext getApplicationContext();

    public Controller getController();

    PerspectiveFactory getPerspectiveFactory();

    ModuleServiceAsync getModuleService();
}
