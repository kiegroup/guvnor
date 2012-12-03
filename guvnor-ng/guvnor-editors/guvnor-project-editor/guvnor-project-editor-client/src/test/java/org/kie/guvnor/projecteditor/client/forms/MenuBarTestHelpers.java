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

package org.kie.guvnor.projecteditor.client.forms;

import org.uberfire.client.workbench.widgets.menu.MenuBar;
import org.uberfire.client.workbench.widgets.menu.MenuItem;
import org.uberfire.client.workbench.widgets.menu.impl.DefaultMenuItemCommand;

public class MenuBarTestHelpers {

    public static void clickFirst(MenuBar menuBar) {
        for (MenuItem menuItem : menuBar.getItems()) {
            if (menuItem instanceof DefaultMenuItemCommand) {
                DefaultMenuItemCommand defaultMenuItemCommand = (DefaultMenuItemCommand) menuItem;
                defaultMenuItemCommand.getCommand().execute();
                break;
            }
        }
    }

    public static void clickSecond(MenuBar menuBar) {
        int i = 0;
        for (MenuItem menuItem : menuBar.getItems()) {
            if (menuItem instanceof DefaultMenuItemCommand) {
                if (i == 1) {
                    DefaultMenuItemCommand defaultMenuItemCommand = (DefaultMenuItemCommand) menuItem;
                    defaultMenuItemCommand.getCommand().execute();
                    break;
                }
                i++;
            }
        }
    }
}
