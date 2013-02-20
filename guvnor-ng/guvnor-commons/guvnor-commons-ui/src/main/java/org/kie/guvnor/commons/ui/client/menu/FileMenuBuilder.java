package org.kie.guvnor.commons.ui.client.menu;

import com.google.gwt.core.client.Callback;
import org.uberfire.backend.vfs.Path;
import org.uberfire.client.mvp.Command;
import org.uberfire.client.workbench.widgets.menu.MenuBar;

public interface FileMenuBuilder {

    public GenericMenuBuilder addTopLevelMenuItem(String title, Command command);

    public MenuBar build();

    FileMenuBuilder addSave(Command command);

    FileMenuBuilder addValidation(Command command);

    FileMenuBuilder addDelete(Path path);

    FileMenuBuilder addDelete(Path path, Callback<Void,Void> callback);

    FileMenuBuilder addDelete(Command command);

    FileMenuBuilder addRename(Command command);

    FileMenuBuilder addRename(Path path);

    FileMenuBuilder addRename(Path path, Callback<Path, Void> callback);

    FileMenuBuilder addCopy(Path path);

    FileMenuBuilder addCopy(Path path, Callback<Path, Void> callback);

    FileMenuBuilder addCopy(Command command);

    FileMenuBuilder addRestoreVersion(Path path);
}
