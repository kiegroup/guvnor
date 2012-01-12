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

package org.drools.guvnor.client.asseteditor.drools;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.Command;

import org.drools.guvnor.client.asseteditor.AssetAttachmentFileWidget;
import org.drools.guvnor.client.asseteditor.EditorWidget;
import org.drools.guvnor.client.asseteditor.RuleViewer;
import org.drools.guvnor.client.asseteditor.SaveEventListener;
import org.drools.guvnor.client.common.LoadingPopup;
import org.drools.guvnor.client.explorer.ClientFactory;
import org.drools.guvnor.client.messages.Constants;
import org.drools.guvnor.client.moduleeditor.drools.SuggestionCompletionCache;
import org.drools.guvnor.client.resources.Images;
import org.drools.guvnor.client.rpc.Asset;

/**
 * This wraps a file uploader utility for model packages.
 * Model packages are jar files.
 */

public class ModelAttachmentFileWidget extends AssetAttachmentFileWidget
        implements
        SaveEventListener,
        EditorWidget {

    private static Images images = GWT.create( Images.class );

    private String packageName;

    public ModelAttachmentFileWidget( Asset asset,
                                      RuleViewer viewer,
                                      ClientFactory clientFactory,
                                      EventBus eventBus ) {
        super( asset,
                viewer,
                clientFactory,
                eventBus );
        this.packageName = asset.getMetaData().getModuleName();
    }

    public ImageResource getIcon() {
        return images.modelLarge();
    }

    public String getOverallStyleName() {
        return "editable-Surface";
    }

    public void onSave() {
    }

    /**
     * As we want to refresh the suggestion completion engine.
     */
    public void onAfterSave() {
        LoadingPopup.showMessage( ((Constants) GWT.create( Constants.class )).RefreshingModel() );
        SuggestionCompletionCache.getInstance().loadPackage( packageName,
                new Command() {
                    public void execute() {
                        LoadingPopup.close();
                    }
                } );

    }

}
