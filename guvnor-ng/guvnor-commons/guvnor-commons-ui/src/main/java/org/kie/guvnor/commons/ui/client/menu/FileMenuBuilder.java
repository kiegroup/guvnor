package org.kie.guvnor.commons.ui.client.menu;

import com.google.gwt.core.client.Callback;
import org.uberfire.backend.vfs.Path;
import org.uberfire.client.mvp.Command;
import org.uberfire.client.workbench.widgets.menu.Menus;

public interface FileMenuBuilder {

    public Menus build();

    FileMenuBuilder addSave( final Command command );

    FileMenuBuilder addValidation( final Command command );

    FileMenuBuilder addDelete( final Command command );

    FileMenuBuilder addDelete( final Path path );

    FileMenuBuilder addDelete( final Path path,
                               final Callback<Void, Void> callback );

    FileMenuBuilder addRename( final Command command );

    FileMenuBuilder addCopy( final Command command );

    FileMenuBuilder addRename( final Path path );

    FileMenuBuilder addRename( final Path path,
                               final Callback<Path, Void> callback );

    FileMenuBuilder addCopy( final Path path );

    FileMenuBuilder addCopy( final Path path,
                             final Callback<Path, Void> callback );

    FileMenuBuilder addRestoreVersion( final Path path );
}
