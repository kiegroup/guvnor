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
import org.drools.guvnor.client.ruleeditor.toolbar.ActionToolbarButtonsConfigurationProvider;

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

	interface ArtifactEditorBinder extends UiBinder<Widget, ArtifactEditor> {
	}

    private static ArtifactEditorBinder                   uiBinder  = GWT.create( ArtifactEditorBinder.class );

    @UiField(provided = true)
    final MetaDataWidgetNew                              metaWidget;

    @UiField(provided = true)
    final RuleDocumentWidget                          ruleDocumentWidget;

    @UiField
    MessageWidget                                     messageWidget;
    
    public Command                                    checkedInCommand;
    protected Artifact                                artifact;
    private boolean                                   readOnly;
    private long lastSaved = System.currentTimeMillis();
    
    /**
     * @param Artifact artifact 
     */
    public ArtifactEditor(Artifact artifact) {
		this(artifact, false, null);
    }

    /**
     * @param Artifact artifact 
     * @param historicalReadOnly true if this is a read only view for historical purposes.
     */
	public ArtifactEditor(Artifact artifact, boolean historicalReadOnly) {
		this(artifact, historicalReadOnly, null);
    }

    /**
     * @param Artifact artifact 
     * @param historicalReadOnly true if this is a read only view for historical purposes.
     * @param actionToolbarButtonsConfigurationProvider
     *            used to change the default button configuration provider.
     */
    public ArtifactEditor(Artifact artifact,
                      boolean historicalReadOnly,
                      ActionToolbarButtonsConfigurationProvider actionToolbarButtonsConfigurationProvider) {
        this.artifact = artifact;
        this.readOnly = historicalReadOnly || artifact.isreadonly;

        ruleDocumentWidget = new RuleDocumentWidget( artifact,
                                                     historicalReadOnly);

        metaWidget = createMetaWidget();

        initWidget( uiBinder.createAndBindUi( this ) );
        setWidth( "100%" );
        LoadingPopup.close();
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
}
