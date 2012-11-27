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

package org.kie.guvnor.editors.enums.client.editor;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.google.gwt.user.client.ui.IsWidget;
import org.jboss.errai.bus.client.api.RemoteCallback;
import org.jboss.errai.ioc.client.api.Caller;
import org.uberfire.backend.vfs.Path;
import org.uberfire.backend.vfs.VFSService;
import org.uberfire.client.annotations.IsDirty;
import org.uberfire.client.annotations.OnClose;
import org.uberfire.client.annotations.OnMayClose;
import org.uberfire.client.annotations.OnSave;
import org.uberfire.client.annotations.OnStart;
import org.uberfire.client.annotations.WorkbenchEditor;
import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.annotations.WorkbenchPartView;

/**
 * This is the default rule editor widget (just text editor based) - more to come later.
 */
@Dependent
@WorkbenchEditor(identifier = "EnumEditor", fileTypes = "enumeration")
public class EnumEditorPresenter {

    public interface View
            extends
            IsWidget {

        void setContent( String content );

        String getContent();

        boolean isDirty();

        void setNotDirty();

        boolean confirmClose();
    }

    @Inject
    public View view;

    @Inject
    private Caller<VFSService> vfs;

    private Path path;

    @OnStart
    public void onStart( final Path path ) {
        this.path = path;
        vfs.call( new RemoteCallback<String>() {
            @Override
            public void callback( String response ) {
                if ( response == null ) {
                    view.setContent( "-- empty --" );
                } else {
                    view.setContent( response );
                }
            }
        } ).readAllString( path );
    }

    @OnSave
    public void onSave() {
        vfs.call( new RemoteCallback<Path>() {
            @Override
            public void callback( Path response ) {
                view.setNotDirty();
            }
        } ).write( path, view.getContent() );
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
        return "Enum Editor [" + path.getFileName() + "]";
    }

    @WorkbenchPartView
    public IsWidget getWidget() {
        return view;
    }

}
