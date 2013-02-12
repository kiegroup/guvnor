/*
 * Copyright 2012 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.guvnor.commons.ui.client.menu;

import org.kie.guvnor.commons.ui.client.resources.i18n.CommonConstants;
import org.uberfire.backend.vfs.Path;
import org.uberfire.client.mvp.Command;
import org.uberfire.client.workbench.widgets.menu.MenuBar;
import org.uberfire.client.workbench.widgets.menu.MenuItem;
import org.uberfire.client.workbench.widgets.menu.impl.DefaultMenuBar;
import org.uberfire.client.workbench.widgets.menu.impl.DefaultMenuItemCommand;
import org.uberfire.client.workbench.widgets.menu.impl.DefaultMenuItemSubMenu;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import java.util.ArrayList;

/**
 *
 */
@Dependent
public final class ResourceMenuBuilder {


    @Inject
    private RestoreVersionCommandProvider restoreVersionCommandProvider;

    final MenuBar menuBar = new DefaultMenuBar();

    private ResourceMenuBuilder.FileMenuBuilder fileMenuBuilder;

    private ArrayList<GenericMenuBuilder> builders = new ArrayList<GenericMenuBuilder>();

    public FileMenuBuilder addFileMenu() {
        fileMenuBuilder = new FileMenuBuilder();
        return fileMenuBuilder;
    }

    private GenericMenuBuilder addTopLevelMenuItem(String title, Command command) {
        GenericMenuBuilder genericMenuBuilder = new GenericMenuBuilder(title, command);
        builders.add(genericMenuBuilder);
        return genericMenuBuilder;
    }

    private MenuBar build() {
        if (fileMenuBuilder != null) {
            fileMenuBuilder.buildFileMenu();
        }
        for (GenericMenuBuilder builder : builders) {
            builder.buildMenu();
        }
        return menuBar;
    }

    public class FileMenuBuilder {
        private Command saveCommand = null;
        private Command restoreCommand = null;
        private Command validateCommand = null;
        private Command copyCommand = null;
        private Command deleteCommand = null;
        private Command renameCommand = null;

        private Command moveCommand = null;

        public FileMenuBuilder addValidation(final Command command) {
            this.validateCommand = command;
            return this;
        }

        public FileMenuBuilder addSave(final Command command) {
            this.saveCommand = command;
            return this;
        }

        public FileMenuBuilder addRestoreVersion(Path path) {
            this.restoreCommand = restoreVersionCommandProvider.getCommand(path);
            return this;
        }

        public FileMenuBuilder addRestoreVersion(final Command command) {
            this.restoreCommand = command;
            return this;
        }

        public FileMenuBuilder addCopy(final Command command) {
            this.copyCommand = command;
            return this;
        }

        public FileMenuBuilder addRename(final Command command) {
            this.renameCommand = command;
            return this;
        }

        public FileMenuBuilder addDelete(final Command command) {
            this.deleteCommand = command;
            return this;
        }


        public FileMenuBuilder addMove(final Command command) {
            this.moveCommand = command;
            return this;
        }

        public MenuBar build() {
            return ResourceMenuBuilder.this.build();
        }

        public GenericMenuBuilder addTopLevelMenuItem(String title, Command command) {
            return ResourceMenuBuilder.this.addTopLevelMenuItem(title, command);
        }

        void buildFileMenu() {
            final MenuBar subMenuBar = new DefaultMenuBar();
            menuBar.addItem(new DefaultMenuItemSubMenu(CommonConstants.INSTANCE.File(),
                    subMenuBar));

            if (validateCommand != null) {
                final MenuItem validate = new DefaultMenuItemCommand(CommonConstants.INSTANCE.Validate(),
                        validateCommand);
                subMenuBar.addItem(validate);
            }

            if (saveCommand != null) {
                final MenuItem save = new DefaultMenuItemCommand(CommonConstants.INSTANCE.Save(),
                        saveCommand);
                subMenuBar.addItem(save);
            }

            if (restoreCommand != null) {
                final MenuItem restore = new DefaultMenuItemCommand(CommonConstants.INSTANCE.Restore(),
                        restoreCommand);
                subMenuBar.addItem(restore);
            }

            if (copyCommand != null) {
                final MenuItem copy = new DefaultMenuItemCommand(CommonConstants.INSTANCE.Copy(),
                        copyCommand);
                subMenuBar.addItem(copy);
            }

            if (deleteCommand != null) {
                final MenuItem delete = new DefaultMenuItemCommand(CommonConstants.INSTANCE.Delete(),
                        deleteCommand);
                subMenuBar.addItem(delete);
            }

            if (renameCommand != null) {
                final MenuItem rename = new DefaultMenuItemCommand(CommonConstants.INSTANCE.Rename(),
                        renameCommand);
                subMenuBar.addItem(rename);
            }

            if (moveCommand != null) {
                final MenuItem move = new DefaultMenuItemCommand(CommonConstants.INSTANCE.Move(),
                        moveCommand);
                subMenuBar.addItem(move);
            }

        }
    }

    public class GenericMenuBuilder {

        private final String title;
        private final Command command;

        public GenericMenuBuilder(String title, Command command) {
            this.title = title;
            this.command = command;
        }

        public MenuBar build() {
            return ResourceMenuBuilder.this.build();
        }

        void buildMenu() {
            menuBar.addItem(new DefaultMenuItemCommand(title, command));
        }
    }
}
