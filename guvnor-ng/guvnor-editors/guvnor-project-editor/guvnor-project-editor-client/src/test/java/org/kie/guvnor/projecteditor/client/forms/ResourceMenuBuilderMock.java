package org.kie.guvnor.projecteditor.client.forms;

import com.google.gwt.core.client.Callback;
import org.kie.guvnor.commons.ui.client.menu.FileMenuBuilder;
import org.kie.guvnor.commons.ui.client.menu.GenericMenuBuilder;
import org.kie.guvnor.commons.ui.client.menu.ResourceMenuBuilder;
import org.uberfire.backend.vfs.Path;
import org.uberfire.client.mvp.Command;
import org.uberfire.client.workbench.widgets.menu.MenuBar;
import org.uberfire.client.workbench.widgets.menu.MenuItem;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;

public class ResourceMenuBuilderMock
        implements ResourceMenuBuilder {

    private ResourceMenuBuilderMock.FileMenuBuilderMock fileMenuBuilder = new FileMenuBuilderMock();
    private ResourceMenuBuilderMock.GenericMenuBuilderMock genericMenuBuilder = new GenericMenuBuilderMock();
    private ResourceMenuBuilderMock.MenuBarMock menuBar = new MenuBarMock();
    private Command saveCommand;
    private Command validateCommand;
    private Command deleteCommand;
    private Command renameCommand;
    private Command copyCommand;
    private HashMap<String, Command> genericCommands = new HashMap<String, Command>();

    @Override
    public FileMenuBuilder addFileMenu() {
        return fileMenuBuilder;
    }

    @Override
    public GenericMenuBuilder addTopLevelMenuItem(String title, Command command) {
        genericCommands.put(title, command);
        return genericMenuBuilder;
    }

    public void clickSave() {
        saveCommand.execute();
    }

    public void click(String title) {
        genericCommands.get(title).execute();
    }

    private class FileMenuBuilderMock
            implements FileMenuBuilder {

        @Override
        public GenericMenuBuilder addTopLevelMenuItem(String title, Command command) {
            return ResourceMenuBuilderMock.this.addTopLevelMenuItem(title, command);
        }

        @Override
        public MenuBar build() {
            return menuBar;
        }

        @Override
        public FileMenuBuilder addSave(Command command) {
            saveCommand = command;
            return this;
        }

        @Override
        public FileMenuBuilder addValidation(Command command) {
            validateCommand = command;
            return this;
        }

        @Override
        public FileMenuBuilder addDelete(Path path) {
            return null;  //TODO -Rikkola-
        }

        @Override
        public FileMenuBuilder addDelete(Path path, Callback<Void, Void> callback) {
            return null;  //TODO -Rikkola-
        }

        @Override
        public FileMenuBuilder addDelete(Command command) {
            deleteCommand = command;
            return this;
        }

        @Override
        public FileMenuBuilder addRename(Command command) {
            renameCommand = command;
            return this;
        }

        @Override
        public FileMenuBuilder addRename(Path path) {
            return null;  //TODO -Rikkola-
        }

        @Override
        public FileMenuBuilder addRename(Path path, Callback<Path, Void> callback) {
            return null;  //TODO -Rikkola-
        }

        @Override
        public FileMenuBuilder addCopy(Path path) {
            return null;  //TODO -Rikkola-
        }

        @Override
        public FileMenuBuilder addCopy(Path path, Callback<Path, Void> callback) {
            return null;  //TODO -Rikkola-
        }

        @Override
        public FileMenuBuilder addCopy(Command command) {
            copyCommand = command;
            return this;
        }

        @Override
        public FileMenuBuilder addRestoreVersion(Path path) {
            return this;
        }

    }

    private class GenericMenuBuilderMock
            implements GenericMenuBuilder {

        @Override
        public MenuBar build() {
            return menuBar;
        }
    }

    private class MenuBarMock
            implements MenuBar {
        @Override
        public void addItem(MenuItem item) {
            //TODO -Rikkola-
        }

        @Override
        public List<MenuItem> getItems() {
            return null;  //TODO -Rikkola-
        }

        @Override
        public String getSignatureId() {
            return null;  //TODO -Rikkola-
        }

        @Override
        public Collection<String> getRoles() {
            return null;  //TODO -Rikkola-
        }

        @Override
        public Collection<String> getTraits() {
            return null;  //TODO -Rikkola-
        }
    }
}
