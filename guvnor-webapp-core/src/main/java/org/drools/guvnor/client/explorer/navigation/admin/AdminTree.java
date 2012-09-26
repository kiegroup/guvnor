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
import org.drools.guvnor.client.messages.ConstantsCore;
import org.drools.guvnor.client.resources.ImagesCore;
import org.drools.guvnor.client.util.Util;

import java.util.Map;

public class AdminTree extends Tree {

    private static ConstantsCore constants = GWT.create(ConstantsCore.class);
    private static ImagesCore images = GWT.create(ImagesCore.class);

    public AdminTree(Map<TreeItem, String> itemWidgets) {
        setAnimationEnabled(true);

        Object[][] adminStructure = new Object[][]{
                {constants.Category(), images.categorySmall(), "categoryManager"},
                {constants.Status(), images.statusSmall(), "statusManager"},
                {constants.Archive(), images.backupSmall(), "archiveManager"},
                {constants.EventLog(), images.eventLogSmall(), "eventLogManager"},
                {constants.UserPermission(), images.userPermissionsSmall(), "userPermissionManager"},
                {constants.ImportExport(), images.saveEdit(), "backupManager"},
                {constants.RepositoryConfiguration(), images.config(), "repositoryConfigManager"},
                {constants.About(), images.information(), "aboutPopup"}
        };

        for (Object[] packageData : adminStructure) {

            TreeItem localChildNode = new TreeItem(Util.getHeader((ImageResource) packageData[1], (String) packageData[0]));
            itemWidgets.put(localChildNode, (String) packageData[2]);

            addItem(localChildNode);
        }
    }
}
