/*
 * Copyright 2012 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.guvnor.guided.client;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.google.gwt.user.client.ui.IsWidget;
import org.jboss.errai.bus.client.api.RemoteCallback;
import org.jboss.errai.ioc.client.api.Caller;
import org.kie.guvnor.datamodel.oracle.DataModelOracle;
import org.kie.guvnor.guided.model.GuidedEditorContent;
import org.kie.guvnor.guided.model.RuleModel;
import org.kie.guvnor.guided.service.GuidedEditorService;
import org.kie.guvnor.viewsource.client.screen.ViewSourceView;
import org.uberfire.backend.vfs.Path;
import org.uberfire.client.annotations.IsDirty;
import org.uberfire.client.annotations.OnClose;
import org.uberfire.client.annotations.OnMayClose;
import org.uberfire.client.annotations.OnSave;
import org.uberfire.client.annotations.OnStart;
import org.uberfire.client.annotations.WorkbenchEditor;
import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.annotations.WorkbenchPartView;
import org.uberfire.client.common.MultiPageEditor;
import org.uberfire.client.common.Page;

@Dependent
@WorkbenchEditor(identifier = "GuidedEditor", fileTypes = "brl")
public class GuidedEditorPresenter {

    public interface View
            extends
            IsWidget {

        void setContent( final Path path,
                         final DataModelOracle dataModel,
                         final RuleModel content );

        RuleModel getContent();

        boolean isDirty();

        void setNotDirty();

        boolean confirmClose();
    }

    @Inject
    private View view;

    @Inject
    private ViewSourceView viewSource;

    @Inject
    private MultiPageEditor multiPage;

    @Inject
    private Caller<GuidedEditorService> service;

    private Path path = null;

    @PostConstruct
    public void init() {
        multiPage.addWidget( view, "Edit" );
        multiPage.addPage( new Page( viewSource, "Source" ) {
            @Override
            public void onFocus() {
                service.call( new RemoteCallback<String>() {
                    @Override
                    public void callback( final String response ) {
                        viewSource.setContent( response );
                    }
                } ).toSource( view.getContent() );
            }

            @Override
            public void onLostFocus() {
                viewSource.clear();
            }
        } );
    }

    @OnStart
    public void onStart( final Path path ) {
        this.path = path;

        service.call( new RemoteCallback<GuidedEditorContent>() {
            @Override
            public void callback( final GuidedEditorContent response ) {
                view.setContent( path, response.getDataModel(), response.getRuleModel() );
            }
        } ).loadContent( path );
    }

    @OnSave
    public void onSave() {
        service.call( new RemoteCallback<Path>() {
            @Override
            public void callback( Path response ) {
                view.setNotDirty();
            }
        } ).save( path, view.getContent() );
    }

    @IsDirty
    public boolean isDirty() {
        return view.isDirty();
    }

    @OnClose
    public void onClose() {
        this.path = null;
    }

    @OnMayClose
    public boolean checkIfDirty() {
        if ( isDirty() ) {
            return view.confirmClose();
        }
        return true;
    }

    @WorkbenchPartTitle
    public String getTitle() {
        return "Guided Editor [" + path.getFileName() + "]";
    }

    @WorkbenchPartView
    public IsWidget getWidget() {
        return multiPage;
    }

}
