/*
 * Copyright 2005 JBoss Inc
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package org.drools.guvnor.client.packages;

import org.drools.guvnor.client.common.GenericCallback;
import org.drools.guvnor.client.common.LoadingPopup;
import org.drools.guvnor.client.messages.Constants;
import org.drools.guvnor.client.rpc.Artifact;
import org.drools.guvnor.client.rpc.RepositoryServiceFactory;
import org.drools.guvnor.client.rpc.RuleAsset;
import org.drools.guvnor.client.ruleeditor.GuvnorEditor;
import org.drools.guvnor.client.ruleeditor.MessageWidget;
import org.drools.guvnor.client.ruleeditor.MetaDataWidgetNew;
import org.drools.guvnor.client.ruleeditor.RuleDocumentWidget;
import org.drools.guvnor.client.ruleeditor.RuleViewerSettings;
import org.drools.guvnor.client.ruleeditor.toolbar.ActionToolbarButtonsConfigurationProvider;
import org.drools.guvnor.client.rulelist.OpenItemCommand;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.Widget;

/**
 * The generic editor for all types of artifacts. 
 */
public class ArtifactEditor extends GuvnorEditor {
    private Constants     constants = GWT.create( Constants.class );

    interface ArtifactEditorBinder
        extends
        UiBinder<Widget, ArtifactEditor> {
    }

    private static ArtifactEditorBinder                   uiBinder  = GWT.create( ArtifactEditorBinder.class );

    @UiField(provided = true)
    final MetaDataWidgetNew                              metaWidget;

    @UiField(provided = true)
    final RuleDocumentWidget                          ruleDocumentWidget;

    @UiField
    MessageWidget                                     messageWidget;
    private Command                                   closeCommand;
    public Command                                    checkedInCommand;
    protected Artifact                                artifact;

    private boolean                                   readOnly;

    private final RuleViewerSettings                  ruleViewerSettings;

    private long                                      lastSaved = System.currentTimeMillis();

    public ArtifactEditor(Artifact artifact,
                      final OpenItemCommand event) {
        this( artifact,
              event,
              false,
              null,
              null );
    }

    /**
     * @param historicalReadOnly
     *            true if this is a read only view for historical purposes.
     */
    public ArtifactEditor(Artifact artifact,
                      final OpenItemCommand event,
                      boolean historicalReadOnly) {
        this( artifact,
              event,
              historicalReadOnly,
              null,
              null );
    }

    /**
     * @param historicalReadOnly
     *            true if this is a read only view for historical purposes.
     * @param actionToolbarButtonsConfigurationProvider
     *            used to change the default button configuration provider.
     */
    public ArtifactEditor(Artifact artifact,
                      final OpenItemCommand event,
                      boolean historicalReadOnly,
                      ActionToolbarButtonsConfigurationProvider actionToolbarButtonsConfigurationProvider,
                      RuleViewerSettings ruleViewerSettings) {
        this.artifact = artifact;
        this.readOnly = historicalReadOnly
                        && artifact.isreadonly;

        if ( ruleViewerSettings == null ) {
            this.ruleViewerSettings = new RuleViewerSettings();
        } else {
            this.ruleViewerSettings = ruleViewerSettings;
        }

        ruleDocumentWidget = new RuleDocumentWidget( artifact,
                                                     this.ruleViewerSettings.isDocoVisible() );

        metaWidget = createMetaWidget();
        metaWidget.setVisible( this.ruleViewerSettings.isMetaVisible() );

        initWidget( uiBinder.createAndBindUi( this ) );
        LoadingPopup.close();
    }

    public void setDocoVisible(boolean docoVisible) {
        this.ruleViewerSettings.setDocoVisible( docoVisible );
        this.ruleDocumentWidget.setVisible( docoVisible );
    }

    public void setMetaVisible(boolean metaVisible) {
        this.ruleViewerSettings.setMetaVisible( metaVisible );
        this.metaWidget.setVisible( metaVisible );
    }

    @Override
    public boolean isDirty() {
        return (System.currentTimeMillis() - lastSaved) > 3600000;
    }

	private MetaDataWidgetNew createMetaWidget() {
		return new MetaDataWidgetNew(this.artifact, readOnly,
				this.artifact.uuid, new Command() {
					public void execute() {
						refreshMetaWidgetOnly();
					}
				}, new Command() {
					public void execute() {
						refreshDataAndView();
					}
				});
	}

    protected boolean hasDirty() {
        // not sure how to implement this now.
        return false;
    }

    /** closes itself */
    private void close() {
        closeCommand.execute();
    }

    public void showInfoMessage(String message) {
        messageWidget.showMessage( message );
    }

    public void refreshDataAndView() {
        LoadingPopup.showMessage( constants.RefreshingItem() );
        RepositoryServiceFactory.getAssetService().loadRuleAsset( artifact.uuid,
                                                             new GenericCallback<RuleAsset>() {
                                                                 public void onSuccess(RuleAsset asset_) {
                                                                     artifact = asset_;
                                                                     //doWidgets();
                                                                     LoadingPopup.close();
                                                                 }
                                                             } );
    }

    /**
     * This will only refresh the meta data widget if necessary.
     */
    public void refreshMetaWidgetOnly() {
        refreshMetaWidgetOnly( true );
    }

	private void refreshMetaWidgetOnly(final boolean showBusy) {
		if (showBusy)
			LoadingPopup.showMessage(constants.RefreshingItem());
		RepositoryServiceFactory.getAssetService().loadRuleAsset(artifact.uuid,
				new GenericCallback<RuleAsset>() {
					public void onSuccess(RuleAsset asset_) {
						//FIXME: JLIU
						//artifact.metaData = asset_.metaData;
						metaWidget.setMetaData(artifact);
						metaWidget.refresh();
						if (showBusy)
							LoadingPopup.close();
					}
				});
	}

    /**
     * This needs to be called to allow the opened viewer to close itself.
     * 
     * @param c
     */
    public void setCloseCommand(Command c) {
        this.closeCommand = c;
    }
}
