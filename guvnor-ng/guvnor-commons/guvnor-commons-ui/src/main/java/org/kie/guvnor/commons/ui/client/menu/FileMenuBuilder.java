package org.kie.guvnor.commons.ui.client.menu;

import org.uberfire.backend.vfs.Path;
import org.uberfire.client.mvp.Command;
import org.uberfire.client.workbench.widgets.menu.MenuBar;

public interface FileMenuBuilder {

    public GenericMenuBuilder addTopLevelMenuItem(String title, Command command);

    public MenuBar build();

    FileMenuBuilder addSave(Command command);

    FileMenuBuilder addValidation(Command command);

    FileMenuBuilder addDelete(Command command);

    FileMenuBuilder addRename(Command command);

    FileMenuBuilder addCopy(Command command);

    FileMenuBuilder addRestoreVersion(Path path);
}
