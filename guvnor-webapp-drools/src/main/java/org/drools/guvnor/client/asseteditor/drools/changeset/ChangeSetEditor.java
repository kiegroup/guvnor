/*
 * Copyright 2011 JBoss by Red Hat.
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

/*Every That is commented in relate to de attribute data is because a NEP*/
package org.drools.guvnor.client.asseteditor.drools.changeset;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.*;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.Widget;
import org.drools.guvnor.client.asseteditor.EditorWidget;
import org.drools.guvnor.client.asseteditor.RuleViewer;
import org.drools.guvnor.client.asseteditor.SaveEventListener;
import org.drools.guvnor.client.common.DirtyableComposite;
import org.drools.guvnor.client.common.ErrorPopup;
import org.drools.guvnor.client.common.FormStylePopup;
import org.drools.guvnor.client.explorer.ClientFactory;
import org.drools.guvnor.client.messages.Constants;
import org.drools.guvnor.client.rpc.Asset;
import org.drools.guvnor.client.rpc.RuleContentText;
import org.drools.guvnor.client.widgets.RESTUtil;

/**
 * This is the default Change Set editor widget - more to come later.
 */
public class ChangeSetEditor extends DirtyableComposite
        implements
        EditorWidget,
    SaveEventListener {

    // UI
    interface ChangeSetEditorBinder
            extends
            UiBinder<Widget, ChangeSetEditor> {
    }

    private static ChangeSetEditorBinder uiBinder  = GWT.create( ChangeSetEditorBinder.class );
    @UiField
    protected TextArea                   editorArea;
    @UiField
    protected Button                     btnAssetResource;
    @UiField
    protected Button                     btnPackageResource;
    @UiField
    protected HorizontalPanel            pnlURL;

    private ClientFactory                clientFactory;
    final private RuleContentText        data;
    final private String                 assetPackageName;
    final private String                 assetPackageUUID;
    final private String                 assetName;
    private final int                    visibleLines;
    private Constants                    constants = GWT.create( Constants.class );

    public ChangeSetEditor(Asset a,
                           RuleViewer v,
                           ClientFactory clientFactory,
                           EventBus eventBus) {
        this( a,
              clientFactory );
    }

    public ChangeSetEditor(Asset a,
                           ClientFactory clientFactory) {
        this( a,
              clientFactory,
              -1 );
    }

    public ChangeSetEditor(Asset asset,
                           ClientFactory clientFactory,
                           int visibleLines) {

        this.initWidget( uiBinder.createAndBindUi( this ) );

        this.clientFactory = clientFactory;

        assetPackageUUID = asset.getMetaData().getModuleUUID();
        assetPackageName = asset.getMetaData().getModuleName();
        assetName = asset.getName();

        data = (RuleContentText) asset.getContent();

        if ( data.content == null ) {
            data.content = "Empty!";
        }

        this.visibleLines = visibleLines;

        this.customizeUIElements();
    }

    private void customizeUIElements() {

        pnlURL.add( this.createChangeSetLink() );

        editorArea.setStyleName( "default-text-Area" ); //NON-NLS
        editorArea.setVisibleLines( (visibleLines == -1) ? 25 : visibleLines );
        editorArea.setText( data.content );
        editorArea.getElement().setAttribute( "spellcheck",
                                              "false" ); //NON-NLS

        editorArea.addChangeHandler( new ChangeHandler() {

            public void onChange(ChangeEvent event) {
                data.content = editorArea.getText();
                makeDirty();
            }
        } );

        editorArea.addKeyDownHandler( new KeyDownHandler() {

            public void onKeyDown(KeyDownEvent event) {
                if ( event.getNativeKeyCode() == KeyCodes.KEY_TAB ) {
                    event.preventDefault();
                    event.stopPropagation();
                    int pos = editorArea.getCursorPos();
                    insertText( "\t",
                                false );
                    editorArea.setCursorPos( pos + 1 );
                    editorArea.cancelKey();
                    editorArea.setFocus( true );
                }
            }
        } );

    }

    void insertText(String ins,
                    boolean isSpecialPaste) {

        editorArea.setFocus( true );

        int i = editorArea.getCursorPos();
        String left = editorArea.getText().substring( 0,
                                                      i );
        String right = editorArea.getText().substring( i,
                                                       editorArea.getText().length() );
        int cursorPosition = left.toCharArray().length;
        if ( isSpecialPaste ) {
            int p = ins.indexOf( "|" );
            if ( p > -1 ) {
                cursorPosition += p;
                ins = ins.replaceAll( "\\|",
                                      "" );
            }

        }

        editorArea.setText( left + ins + right );
        this.data.content = editorArea.getText();

        editorArea.setCursorPos( cursorPosition );
    }

    public void onSave() {
        //data.content = text.getText();
        //asset.content = data;
    }

    public void onAfterSave() {
    }

    @UiHandler("btnPackageResource")
    public void addNewPackageResource(ClickEvent e) {
        addNewResource( new CreatePackageResourceWidget( assetPackageUUID,
                                                         assetPackageName,
                                                         clientFactory ) );
    }

    @UiHandler("btnAssetResource")
    public void addNewAssetResource(ClickEvent e) {
        addNewResource( new CreateAssetResourceWidget( assetPackageUUID,
                                                       assetPackageName,
                                                       clientFactory ) );
    }

    private void addNewResource(final AbstractXMLResourceDefinitionCreatorWidget editor) {

        final NewResourcePopup popup = new NewResourcePopup( editor.asWidget() );

        popup.addOkButtonClickHandler( new ClickHandler() {

            public void onClick(ClickEvent event) {
                try {
                    editor.getResourceElement( new ResourceElementReadyCommand() {

                        public void onSuccess(String resource) {
                            insertText( resource,
                                        false );
                        }

                        public void onFailure(Throwable cause) {
                            ErrorPopup.showMessage( cause.getMessage() );
                        }
                    } );

                } catch ( Exception e ) {
                    ErrorPopup.showMessage( e.getMessage() );
                }
                popup.hide();
            }
        } );
        popup.show();
    }

    private Widget createChangeSetLink() {
        String url = RESTUtil.getRESTBaseURL();
        url += "packages/";
        url += this.assetPackageName;
        url += "/assets/";
        url += this.assetName;
        url += "/source";

        return new HTML( this.constants.Url() + ":&nbsp;<a href='" + url + "' target='_blank'>" + url + "</a>" );
    }

}

class NewResourcePopup extends FormStylePopup {

    private Constants constants = GWT.create( Constants.class );

    public Button     ok        = new Button( constants.OK() );
    public Button     cancel    = new Button( constants.Cancel() );

    public NewResourcePopup(Widget content) {
        setTitle( constants.NewResource() );

        HorizontalPanel hor = new HorizontalPanel();
        hor.add( ok );
        hor.add( cancel );

        addRow( content );
        addRow( hor );

        cancel.addClickHandler( new ClickHandler() {

            public void onClick(ClickEvent event) {
                hide();
            }
        } );

    }

    public void addOkButtonClickHandler(ClickHandler okClickHandler) {
        ok.addClickHandler( okClickHandler );
    }

}