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

package org.drools.guvnor.client.explorer;


import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;
import org.drools.guvnor.client.rpc.SnapshotInfo;
import org.drools.guvnor.client.ruleeditor.MultiViewRow;
import org.drools.guvnor.client.widgets.tables.InboxPagedTable;

import java.util.List;

public interface TabManager {

    void openFind();

    void openAsset(String key);

    void openAdministrationSelection(int id);

    void openPackageEditor(String uuid, Command command);

    void openSnapshot(SnapshotInfo snap);

    void openTestScenario(String uuid, String name);

    void openVerifierView(String uuid, String name);

    void openAssetsToMultiView(MultiViewRow[] rows);

    void openSnapshotAssetList(String name, String uuid, String[] assetTypes, String s);

    void openCategory(String categoryName, String categoryPath);

    void openPackageViewAssets(String uuid, String name, String key, List<String> strings, Boolean aBoolean, String text);

    boolean showIfOpen(String id);

    void addTab(String title, IsWidget widget, String id);

    void openInboxIncomingPagedTable(String title);

    void openInboxPagedTable(String title);

    void openStatePagedTable(String stateName);
}
