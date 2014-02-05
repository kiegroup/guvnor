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

package org.guvnor.m2repo.client;

import javax.enterprise.context.Dependent;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import org.guvnor.m2repo.client.editor.MavenRepositoryPagedJarTable;
import org.guvnor.m2repo.client.event.M2RepoRefreshEvent;
import org.guvnor.m2repo.client.event.M2RepoSearchEvent;
import org.guvnor.m2repo.client.resources.i18n.M2RepoEditorConstants;
import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.annotations.WorkbenchPartView;
import org.uberfire.client.annotations.WorkbenchScreen;
import org.uberfire.lifecycle.OnStartup;

@Dependent
@WorkbenchScreen(identifier = "M2RepoEditor")
public class M2RepoEditorPresenter {

    @Inject
    private MavenRepositoryPagedJarTable view;

    @OnStartup
    public void onStartup() {
        view.refresh();
    }

    @WorkbenchPartView
    public MavenRepositoryPagedJarTable getWidget() {
        return view;
    }

    @WorkbenchPartTitle
    public String getTitle() {
        return M2RepoEditorConstants.INSTANCE.M2RepositoryContent();
    }

    public void refreshEvent( @Observes final M2RepoRefreshEvent event ) {
        view.refresh();
    }

    public void searchEvent( @Observes final M2RepoSearchEvent event ) {
        view.search( event.getFilter() );
    }

}
