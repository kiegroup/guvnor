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

package org.drools.guvnor.client.packages;


import org.drools.guvnor.client.rpc.PackageConfigData;
import org.drools.guvnor.client.ruleeditor.toolbar.ActionToolbar;

import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.TabPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * This is the package editor and viewer for package configuration.
 */
public class PackageEditorWrapper extends Composite {
	private ArtifactEditor artifactEditor;
	private PackageEditor packageEditor;
	private ActionToolbar actionToolBar;
    private PackageConfigData conf;
    private boolean isHistoricalReadOnly = false;
    private Command closeCommand;
    private Command refreshPackageListCommand;
    private OpenPackageCommand openPackageCommand;

    public PackageEditorWrapper(PackageConfigData data,
                            Command closeCommand,
                            Command refreshPackageListCommand,
                            OpenPackageCommand openPackageCommand) {
        this(data, false, closeCommand, refreshPackageListCommand, openPackageCommand);
    }
    
	public PackageEditorWrapper(PackageConfigData data, 
			boolean isHistoricalReadOnly, 
			Command closeCommand,
			Command refreshPackageListCommand,
			OpenPackageCommand openPackageCommand) {
		this.conf = data;
		this.isHistoricalReadOnly = isHistoricalReadOnly;
		this.closeCommand = closeCommand;
		this.refreshPackageListCommand = refreshPackageListCommand;
		this.openPackageCommand = openPackageCommand;
        
        refreshWidgets();
        setWidth("100%");        
	}
    
    private void refreshWidgets() {  
    	this.artifactEditor = new ArtifactEditor(conf, this.isHistoricalReadOnly);
    	this.packageEditor = new PackageEditor(conf, this.isHistoricalReadOnly, this.closeCommand, this.refreshPackageListCommand, this.openPackageCommand);
    	this.actionToolBar = this.packageEditor.getActionToolbar();
    	
    	VerticalPanel vp = new VerticalPanel();
    	vp.add(this.actionToolBar);
    	
        TabPanel tPanel = new TabPanel();
        tPanel.setWidth("100%");
        
        ScrollPanel pnl = new ScrollPanel();
        pnl.setWidth("100%");
        pnl.add(this.artifactEditor);
        tPanel.add(pnl, "Attributes");        
        tPanel.selectTab(0);                
        
        pnl = new ScrollPanel();
        pnl.setWidth("100%");
        pnl.add(this.packageEditor);
        tPanel.add( pnl, "Edit" );        
        tPanel.selectTab(0);        
        
        tPanel.setHeight("100%");
        vp.add(tPanel);
        vp.setHeight("100%");
        
        initWidget(vp);
    }
}
