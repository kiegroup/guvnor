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

package org.kie.guvnor.m2repo.client.editor;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.google.gwt.user.client.ui.IsWidget;

import org.uberfire.backend.vfs.Path;
import org.uberfire.client.annotations.OnClose;
import org.uberfire.client.annotations.OnSave;
import org.uberfire.client.annotations.OnStart;
import org.uberfire.client.annotations.WorkbenchEditor;
import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.annotations.WorkbenchPartView;

@Dependent
@WorkbenchEditor(identifier = "M2RepoEditor", fileTypes = "repository")
public class M2RepoEditorPresenter {

    public interface View
            extends
            IsWidget {

    }

    @Inject
    private View view;

    private Path path;

    @PostConstruct
    public void init() {
    }

    @OnStart
    public void onStart( final Path path ) {
        this.path = path;
    }

    @OnSave
    public void onSave() {
    }

    @WorkbenchPartView
    public IsWidget getWidget() {
        return view;
    }

    @OnClose
    public void onClose() {
        this.path = null;
    }

    @WorkbenchPartTitle
    public String getTitle() {
        return "Guvnor M2_REPO Editor [" + path.getFileName() + "]";
    }

}
