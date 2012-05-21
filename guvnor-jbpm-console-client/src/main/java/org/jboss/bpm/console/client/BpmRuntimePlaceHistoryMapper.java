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

package org.jboss.bpm.console.client;


import com.google.gwt.place.shared.WithTokenizers;
import org.drools.guvnor.client.explorer.*;
import org.drools.guvnor.client.explorer.navigation.admin.ManagerPlace;
import org.drools.guvnor.client.explorer.navigation.browse.CategoryPlace;
import org.drools.guvnor.client.explorer.navigation.browse.InboxPlace;
import org.drools.guvnor.client.explorer.navigation.browse.StatePlace;
import org.drools.guvnor.client.explorer.navigation.processes.ProcessOverviewPlace;
import org.drools.guvnor.client.explorer.navigation.reporting.ReportTemplatesPlace;
import org.drools.guvnor.client.explorer.navigation.settings.PreferencesPlace;
import org.drools.guvnor.client.explorer.navigation.tasks.GroupTasksPlace;
import org.drools.guvnor.client.explorer.navigation.tasks.PersonalTasksPlace;
import org.drools.guvnor.client.moduleeditor.AssetViewerPlace;

@WithTokenizers(
        {
                FindPlace.Tokenizer.class,
                AssetEditorPlace.Tokenizer.class,
                ModuleEditorPlace.Tokenizer.class,
                AssetViewerPlace.Tokenizer.class,
                ManagerPlace.Tokenizer.class,
                CategoryPlace.Tokenizer.class,
                StatePlace.Tokenizer.class,
                InboxPlace.Tokenizer.class,
                MultiAssetPlace.Tokenizer.class,
                PersonalTasksPlace.Tokenizer.class,
                GroupTasksPlace.Tokenizer.class,
                ReportTemplatesPlace.Tokenizer.class,
                PreferencesPlace.Tokenizer.class,
                ProcessOverviewPlace.Tokenizer.class
        }
)
public interface BpmRuntimePlaceHistoryMapper extends GuvnorPlaceHistoryMapper {
}
