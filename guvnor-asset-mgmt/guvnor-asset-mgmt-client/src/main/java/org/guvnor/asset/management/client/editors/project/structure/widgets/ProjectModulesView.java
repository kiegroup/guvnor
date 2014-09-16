/*
 * Copyright 2014 JBoss Inc
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

package org.guvnor.asset.management.client.editors.project.structure.widgets;

import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.view.client.HasData;

public interface ProjectModulesView extends IsWidget {


    enum ViewMode {
        MODULES_VIEW,
        PROJECTS_VIEW
    }

    interface Presenter {

        void onAddModule();

        void addDataDisplay( final HasData<ProjectModuleRow> display );

        void onDeleteModule( ProjectModuleRow moduleRow );

        void onEditModule( ProjectModuleRow moduleRow );
    }

    void setPresenter( Presenter presenter );

    void setMode( ViewMode mode );

    void enableActions( boolean value );

}
