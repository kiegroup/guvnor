package org.drools.guvnor.client.ruleeditor;
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



import org.drools.guvnor.client.common.DirtyableComposite;
import org.drools.guvnor.client.common.FormStylePopup;
import org.drools.guvnor.client.common.GenericCallback;
import org.drools.guvnor.client.common.LoadingPopup;
import org.drools.guvnor.client.common.SmallLabel;
import org.drools.guvnor.client.packages.PackageBuilderWidget;
import org.drools.guvnor.client.rpc.BuilderResult;
import org.drools.guvnor.client.rpc.BuilderResultLine;
import org.drools.guvnor.client.rpc.RepositoryServiceFactory;
import org.drools.guvnor.client.rpc.RuleAsset;
import org.drools.guvnor.client.messages.Constants;
import org.drools.guvnor.client.explorer.ExplorerLayoutManager;
import org.drools.guvnor.client.security.Capabilities;

import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.core.client.GWT;
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
    
    private static Constants constants = ((Constants) GWT.create(Constants.class));

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
        viewSource.setText(constants.ViewSource());
        viewSource.addListener(new ButtonListenerAdapter()  {
			public void onClick(
					com.gwtext.client.widgets.Button button,
					EventObject e) {
                doViewsource();
			}
		});

        //only show this for advanced users
        if (ExplorerLayoutManager.shouldShow(Capabilities.SHOW_PACKAGE_VIEW)) {
            tb.addButton(viewSource);
            tb.addSeparator();
        }

        ToolbarButton validate = new ToolbarButton();
        validate.setText(constants.Validate());
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
    	onSave();
        LoadingPopup.showMessage(constants.ValidatingItemPleaseWait());
        RepositoryServiceFactory.getService().buildAsset( asset, new GenericCallback<BuilderResult>() {
            public void onSuccess(BuilderResult result) {showBuilderErrors(result);}
        });

    }

    private void doViewsource() {
    	onSave();
        LoadingPopup.showMessage(constants.CalculatingSource());
        RepositoryServiceFactory.getService().buildAssetSource( this.asset, new GenericCallback<String>() {
            public void onSuccess(String src) { showSource(src);}
        });

    }

    private void showSource(String src) {
        PackageBuilderWidget.showSource( src, this.asset.metaData.name );
        LoadingPopup.close();
    }

    /**
     * This will show a popup of error messages in compilation.
     */
    public static void showBuilderErrors(BuilderResult result) {

        if (result == null || result.lines == null || result.lines.length == 0) {
        	FormStylePopup pop = new FormStylePopup();
        	pop.setWidth(200);
        	pop.setTitle(constants.ValidationResultsDotDot());
        	HorizontalPanel h = new HorizontalPanel();
        	h.add(new SmallLabel("<img src='images/tick_green.gif'/><i>" + constants.ItemValidatedSuccessfully() + "</i>")); //NON-NLS


            pop.addRow( h );
            pop.show();
        } else {
        	FormStylePopup pop = new FormStylePopup("images/package_builder.png", constants.ValidationResults()); //NON-NLS
            FlexTable errTable = new FlexTable();
            errTable.setStyleName( "build-Results" ); //NON-NLS
            for ( int i = 0; i < result.lines.length; i++ ) {
                int row = i;
                final BuilderResultLine res = result.lines[i];
                errTable.setWidget( row, 0, new Image("images/error.gif")); //NON-NLS
                if( res.assetFormat.equals( "package" )) {
                    errTable.setText( row, 1, constants.packageConfigurationProblem() + res.message );
                } else {
                    errTable.setText( row, 1, "[" + res.assetName + "] " + res.message );
                }

            }
            ScrollPanel scroll = new ScrollPanel(errTable);
            scroll.setWidth( "100%" );
            pop.addRow( scroll );
            pop.show();
        }

        LoadingPopup.close();
    }

	public void onSave() {
		if (editor instanceof SaveEventListener) {
			SaveEventListener el = (SaveEventListener) editor;
			el.onSave();
		}
	}

	public void onAfterSave() {
		if (editor instanceof SaveEventListener) {
			SaveEventListener el = (SaveEventListener) editor;
			el.onAfterSave();
		}
	}



}