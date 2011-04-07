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
	private PackageEditorNew packageEditor;
	private ActionToolbar actionToolBar;
    private PackageConfigData conf;
    private boolean isHistoricalReadOnly = false;
    private Command close;
    private Command refreshPackageList;

    public PackageEditorWrapper(PackageConfigData data,
                            Command close,
                            Command refreshPackageList) {
        this(data, false, close, refreshPackageList);
    }
    
	public PackageEditorWrapper(PackageConfigData data, 
			boolean isHistoricalReadOnly, 
			Command close,
			Command refreshPackageList) {
		this.conf = data;
		this.isHistoricalReadOnly = isHistoricalReadOnly;
		this.close = close;
		this.refreshPackageList = refreshPackageList;
        
        refreshWidgets();
        setWidth("100%");        
	}
    
    private void refreshWidgets() {  
    	this.artifactEditor = new ArtifactEditor(conf, null);
    	this.packageEditor = new PackageEditorNew(conf, this.isHistoricalReadOnly, this.close, this.refreshPackageList);
    	this.actionToolBar = this.packageEditor.getActionToolbar();
    	
    	VerticalPanel vp = new VerticalPanel();
    	vp.add(this.actionToolBar);
    	
        TabPanel tPanel = new TabPanel();
        tPanel.setWidth("100%");
        
        ScrollPanel pnl = new ScrollPanel();
        pnl.setWidth("100%");
        pnl.setTitle("Common");
        pnl.add(this.artifactEditor);
        tPanel.add(pnl, "Common");        
        tPanel.selectTab(0);                
        
        pnl = new ScrollPanel();
        pnl.setWidth("100%");
        pnl.add(this.packageEditor);
        tPanel.add( pnl, "Edit" );        
        tPanel.selectTab(0);        
        
        vp.add(tPanel);
        
        initWidget(vp);
    }
}
