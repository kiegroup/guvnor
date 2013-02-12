package org.kie.guvnor.commons.ui.client.menu;

import org.uberfire.client.mvp.Command;

public interface ResourceMenuBuilder {

    public FileMenuBuilder addFileMenu();

    public GenericMenuBuilder addTopLevelMenuItem(String title, Command command);

}
