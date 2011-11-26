/*
 * Copyright 2011 JBoss Inc
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 */

package org.drools.guvnor.client.explorer.navigation.admin;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.ui.Tree;
import com.google.gwt.user.client.ui.TreeItem;
import org.drools.guvnor.client.messages.Constants;
import org.drools.guvnor.client.resources.Images;
import org.drools.guvnor.client.util.Util;

import java.util.Map;

public class AdminTree extends Tree {

    private static Constants constants = GWT.create(Constants.class);
    private static Images images = GWT.create(Images.class);

    public AdminTree(Map<TreeItem, String> itemWidgets) {
        setAnimationEnabled(true);

        Object[][] adminStructure = new Object[][]{
                {constants.Category(), images.categorySmall(), "0"},
                {constants.Status(), images.statusSmall(), "2"},
                {constants.Archive(), images.backupSmall(), "1"},
                {constants.EventLog(), images.eventLogSmall(), "4"},
                {constants.UserPermission(), images.userPermissionsSmall(), "5"},
                {constants.ImportExport(), images.saveEdit(), "3"},
                {constants.RepositoryConfiguration(), images.config(), "8"},
                {constants.About(), images.information(), "6"}
        };

        for (int i = 0; i < adminStructure.length; i++) {

            Object[] packageData = adminStructure[i];
            TreeItem localChildNode = new TreeItem(Util.getHeader((ImageResource) packageData[1], (String) packageData[0]));
            itemWidgets.put(localChildNode, (String) packageData[2]);

            addItem(localChildNode);
        }
    }
}
