/*
 * Copyright 2050 JBoss Inc
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


import org.drools.guvnor.client.common.FormStyleLayout;
import org.drools.guvnor.client.common.FormStylePopup;
import org.drools.guvnor.client.common.GenericCallback;
import org.drools.guvnor.client.common.InfoPopup;
import org.drools.guvnor.client.messages.Constants;
import org.drools.guvnor.client.resources.Images;
import org.drools.guvnor.client.rpc.PackageConfigData;
import org.drools.guvnor.client.rpc.RepositoryServiceFactory;
import org.drools.guvnor.client.ruleeditor.MultiViewRow;
import org.drools.guvnor.client.ruleeditor.VersionChooser;
import org.drools.guvnor.client.rulelist.OpenItemCommand;

import org.drools.guvnor.client.widgets.tables.DependenciesPagedTable;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.FontWeight;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * This is the widget for building dependencies. 
 */
public class DependencyWidget extends Composite {

    private static Images         images    = (Images) GWT.create( Images.class );
    private Constants             constants = ((Constants) GWT.create( Constants.class ));

    private FormStyleLayout        layout;
    private DependenciesPagedTable table;

    private PackageConfigData conf;
    
    public DependencyWidget(final PackageConfigData conf) {
        this.conf = conf;
        layout = new FormStyleLayout();

/*
        VerticalPanel header = new VerticalPanel();
        Label caption = new Label( "Dependencies" );
        caption.getElement().getStyle().setFontWeight( FontWeight.BOLD );
        header.add( caption );
        header.add( howToTurnOn() );

        layout.addAttribute( "",
        		header );

        layout.addHeader( images.statusLarge(),
                      header );

        VerticalPanel vp = new VerticalPanel();
        vp.setHeight( "100%" );
        vp.setWidth( "100%" );

        //pf.startSection();
        layout.addRow( vp );
        //pf.endSection();
*/
        refresh();
        initWidget( layout );
    }

    private Widget howToTurnOn() {
        HorizontalPanel hp = new HorizontalPanel();
        hp.add( new HTML( "<small><i>"
                          + constants.TipAuthEnable()
                          + "</i></small>" ) );
        InfoPopup pop = new InfoPopup( constants.EnablingAuthorization(),
                                       constants.EnablingAuthPopupTip() );
        hp.add( pop );
        return hp;
    }

    private void refresh() {
        layout.clear();
        
        VerticalPanel header = new VerticalPanel();
        Label caption = new Label( "Dependencies" );
        caption.getElement().getStyle().setFontWeight( FontWeight.BOLD );
        header.add( caption );
        header.add( howToTurnOn() );

        layout.addAttribute( "",
                header );

/*        layout.addHeader( images.statusLarge(),
                      header );*/

        VerticalPanel vp = new VerticalPanel();
        vp.setHeight( "100%" );
        vp.setWidth( "100%" );

        //pf.startSection();
        layout.addRow( vp );
        table = new DependenciesPagedTable(conf.dependencies, 
        		null, null, new OpenItemCommand() {

            public void open(String path) {
                showEditor( path );
            }

            public void open(MultiViewRow[] rows) {
                // Do nothing, unsupported
            }

        } );

        layout.addRow( table );
        initWidget( layout );
    }
    
    public static String[] parseDependencyPath(String dependencyPath) {
    	if(dependencyPath.indexOf("?version=") >=0) {
    		return dependencyPath.split("\\?version=");
    	} else {
    		return new String[]{dependencyPath, "LATEST"};
    	}
    }
    
    public static String encodeDependencyPath(String dependencyPath, String dependencyVersion) {
    	return dependencyPath + "?version=" + dependencyVersion;
    }
    
    public static String parseDependencyAssetName(String dependencyPath) {
    	return dependencyPath.substring(dependencyPath.lastIndexOf("/")+1);
    }
    
    private void showEditor(final String dependencyPath) {
		final FormStylePopup editor = new FormStylePopup(images.management(), "Edit Dependency");
/*		editor.addRow(new HTML("<i>" + "Choose the version you want to depend on"
				+ "</i>"));
*/
		editor.addAttribute("Dependency Path: ", new Label(parseDependencyPath(dependencyPath)[0]));
		final VersionChooser versionChoose = new VersionChooser( 
				parseDependencyPath(dependencyPath)[1],
				conf.uuid,
				parseDependencyAssetName(parseDependencyPath(dependencyPath)[0]),
                new Command() {
                    public void execute() {
                        refresh();                        
                    }				    
				});
		editor.addAttribute("Dependency Version: ",  versionChoose);


		HorizontalPanel hp = new HorizontalPanel();
		Button save = new Button("Use selected version"); 
		hp.add(save);
        save.addClickHandler(new ClickHandler() {
            private Object archiveCommand;

            public void onClick(ClickEvent w) {
                String selectedVersion = versionChoose.getSelectedVersionName();
                if (Window.confirm("Are you sure you want to use version: " + selectedVersion  + " as dependency?")) {
                    RepositoryServiceFactory.getService().updateDependency(
                            conf.uuid,
                            encodeDependencyPath(DependencyWidget
                                    .parseDependencyPath(dependencyPath)[0],
                                    selectedVersion),
                            new GenericCallback<Void>() {
                                public void onSuccess(Void v) {
                                    editor.hide();
                                    refresh();
                                }
                            });
                }
            }
        });
		
		Button cancel = new Button(constants.Cancel());
		hp.add(cancel);
		cancel.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent w) {
				editor.hide();
			}
		});
		
		editor.addAttribute("", hp);
		editor.show();
	}
}
