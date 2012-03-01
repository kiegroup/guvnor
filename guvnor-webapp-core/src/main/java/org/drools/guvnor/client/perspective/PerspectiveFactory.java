/*
 * Copyright 2005 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.guvnor.client.perspective;

import org.drools.guvnor.client.explorer.ClientFactory;
import org.drools.guvnor.client.moduleeditor.AbstractModuleEditor;
import org.drools.guvnor.client.rpc.Module;
import org.drools.guvnor.client.rpc.Asset;

import com.google.gwt.event.shared.EventBus;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;

public interface PerspectiveFactory {
    public String[] getRegisteredAssetEditorFormats(String moduleType);
    public String[] getRegisteredModuleEditorFormats(String perspectiveType);
    public String[] getRegisteredPerspectiveTypes();

    public AbstractModuleEditor getModuleEditor(Module module, ClientFactory clientFactory, EventBus eventBus, boolean isHistoryReadOnly, Command refreshCommand);
    public IsWidget getModulesHeaderView(String perspectiveType);
    public SafeHtml getModulesTreeRootNodeHeader(String perspectiveType);
    public Widget getModulesNewAssetMenu(String perspectiveType, ClientFactory clientFactory, EventBus eventBus);
    public Widget getModuleEditorActionToolbar(Module data,  ClientFactory clientFactory, EventBus eventBus, boolean readOnly, Command refreshCommand);
    
    public Widget getAssetEditorActionToolbar(String perspectiveType, Asset asset, Widget editor, ClientFactory clientFactory, EventBus eventBus, boolean readOnly);    
    
    public Workspace getPerspective(String perspectiveType);
 }
