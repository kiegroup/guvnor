package org.drools.brms.client.ruleeditor;
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



import org.drools.brms.client.common.DirtyableComposite;
import org.drools.brms.client.common.FormStylePopup;
import org.drools.brms.client.common.GenericCallback;
import org.drools.brms.client.common.LoadingPopup;
import org.drools.brms.client.packages.PackageBuilderWidget;
import org.drools.brms.client.rpc.BuilderResult;
import org.drools.brms.client.rpc.RepositoryServiceFactory;
import org.drools.brms.client.rpc.RuleAsset;

import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.gwtext.client.core.EventObject;
import com.gwtext.client.widgets.Toolbar;
import com.gwtext.client.widgets.ToolbarButton;
import com.gwtext.client.widgets.event.ButtonListenerAdapter;

/**
 * This widget wraps a rule asset widget, and provides actions to validate and view source.
 * @author Michael Neale
 */
public class RuleValidatorWrapper extends DirtyableComposite implements SaveEventListener {

    private RuleAsset asset;
    private VerticalPanel layout = new VerticalPanel();
	private Widget editor;

    public RuleValidatorWrapper(Widget editor, RuleAsset asset) {
        this.asset = asset;
        this.editor = editor;

        layout.add(editor);
        if (!asset.isreadonly) {
        	validatorActions();
        }

        layout.setWidth("100%");
        layout.setHeight( "100%" );

        initWidget( layout );
    }

    private void validatorActions() {
        Toolbar tb = new Toolbar();

        layout.setCellHeight(editor, "95%");
        layout.add(tb);

        ToolbarButton viewSource = new ToolbarButton();
        viewSource.setText("View source");
        viewSource.addListener(new ButtonListenerAdapter()  {
			public void onClick(
					com.gwtext.client.widgets.Button button,
					EventObject e) {
                doViewsource();
			}
		});
        tb.addButton(viewSource);

        tb.addSeparator();

        ToolbarButton validate = new ToolbarButton();
        validate.setText("Validate");
        validate.addListener(new ButtonListenerAdapter()  {
        			public void onClick(
        					com.gwtext.client.widgets.Button button,
        					EventObject e) {
        				doValidate();
        			}
        		});
        tb.addButton(validate);


    }

    private void doValidate() {

        LoadingPopup.showMessage( "Validating item, please wait..." );
        RepositoryServiceFactory.getService().buildAsset( asset, new GenericCallback() {
            public void onSuccess(Object data) {
                BuilderResult[] results = (BuilderResult[]) data;
                showBuilderErrors(results);
            }
        });

    }

    private void doViewsource() {
        LoadingPopup.showMessage( "Calculating source..." );
        RepositoryServiceFactory.getService().buildAssetSource( this.asset, new GenericCallback() {
            public void onSuccess(Object data) {
                String src = (String) data;
                showSource(src);
            }
        });

    }

    private void showSource(String src) {
        PackageBuilderWidget.showSource( src, this.asset.metaData.name );
        LoadingPopup.close();
    }

    /**
     * This will show a popup of error messages in compilation.
     */
    public static void showBuilderErrors(BuilderResult[] results) {
        FormStylePopup pop = new FormStylePopup("images/package_builder.png", "Validation results");
        if (results == null || results.length == 0) {
            pop.addRow( new HTML("<img src='images/tick_green.gif'/><i>Item validated.</i>") );
        } else {
            FlexTable errTable = new FlexTable();
            errTable.setStyleName( "build-Results" );
            for ( int i = 0; i < results.length; i++ ) {
                int row = i;
                final BuilderResult res = results[i];
                errTable.setWidget( row, 0, new Image("images/error.gif"));
                if( res.assetFormat.equals( "package" )) {
                    errTable.setText( row, 1, "[package configuration problem] " + res.message );
                } else {
                    errTable.setText( row, 1, "[" + res.assetName + "] " + res.message );
                }

            }
            ScrollPanel scroll = new ScrollPanel(errTable);
            //scroll.setAlwaysShowScrollBars(true);
            //scroll.setSize("100%","25em");
            scroll.setWidth( "100%" );
            //scroll.setScrollPosition( 100 );
            //errTable.setWidth( "60%" );
            pop.addRow( scroll );
//            pop.setWidth( "70%" );
//            pop.setHeight( "50%" );

        }
        pop.show();
        LoadingPopup.close();
    }

	public void onSave() {
		if (editor instanceof SaveEventListener) {
			SaveEventListener el = (SaveEventListener) editor;
			el.onSave();
		}
	}



}