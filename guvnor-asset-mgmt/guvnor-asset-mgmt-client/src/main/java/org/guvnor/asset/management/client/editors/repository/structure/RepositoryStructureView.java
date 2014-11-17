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

package org.guvnor.asset.management.client.editors.repository.structure;

import com.google.gwt.user.client.ui.IsWidget;
import org.guvnor.asset.management.client.editors.project.structure.widgets.ProjectModulesView;
import org.guvnor.asset.management.client.editors.project.structure.widgets.RepositoryStructureDataView;
import org.guvnor.asset.management.client.editors.repository.structure.configure.ConfigureScreenPopupViewImpl;
import org.guvnor.asset.management.client.editors.repository.structure.promote.PromoteScreenPopupViewImpl;
import org.guvnor.asset.management.client.editors.repository.structure.release.ReleaseScreenPopupViewImpl;
import org.guvnor.asset.management.model.RepositoryStructureModel;
import org.uberfire.ext.widgets.common.client.common.HasBusyIndicator;


public interface RepositoryStructureView
        extends HasBusyIndicator,
        IsWidget {

    interface Presenter {

    }

    void setPresenter( RepositoryStructurePresenter repositoryStructurePresenter );

    RepositoryStructureDataView getDataView();

    ProjectModulesView getModulesView();

    void setModel( RepositoryStructureModel model );

    void setModulesViewVisible( boolean visible );

    void clear();
    
    ReleaseScreenPopupViewImpl getReleaseScreenPopupView();
    
    ConfigureScreenPopupViewImpl getConfigureScreenPopupView();

    PromoteScreenPopupViewImpl getPromoteScreenPopupView();

}
