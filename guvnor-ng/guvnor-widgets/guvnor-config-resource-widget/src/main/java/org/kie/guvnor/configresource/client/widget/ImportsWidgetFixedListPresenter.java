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

package org.kie.guvnor.configresource.client.widget;

import java.util.ArrayList;
import java.util.List;
import javax.enterprise.event.Event;

import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;
import org.jboss.errai.bus.client.api.RemoteCallback;
import org.jboss.errai.ioc.client.api.Caller;
import org.kie.commons.data.Pair;
import org.kie.guvnor.commons.ui.client.popup.list.FormListPopup;
import org.kie.guvnor.commons.ui.client.popup.list.PopupItemSelectedCommand;
import org.kie.guvnor.datamodel.service.DataModelService;
import org.kie.guvnor.services.config.events.ImportAddedEvent;
import org.kie.guvnor.services.config.events.ImportRemovedEvent;
import org.kie.guvnor.services.config.model.imports.Import;
import org.kie.guvnor.services.config.model.imports.Imports;
import org.uberfire.backend.vfs.Path;

import static org.kie.commons.validation.PortablePreconditions.*;

public class ImportsWidgetFixedListPresenter
        implements ImportsWidgetView.Presenter,
                   IsWidget {

    private final ImportsWidgetView view;
    private final FormListPopup addImportPopup;

    private final Event<ImportAddedEvent> importAddedEvent;
    private final Event<ImportRemovedEvent> importRemovedEvent;

    private final Caller<DataModelService> dataModelService;

    private Path path;
    private Imports resourceImports;
    private List<Pair<String, String>> imports;

    @Inject
    public ImportsWidgetFixedListPresenter( final ImportsWidgetView view,
                                            final FormListPopup addImportPopup,
                                            final Event<ImportAddedEvent> importAddedEvent,
                                            final Event<ImportRemovedEvent> importRemovedEvent,
                                            final Caller<DataModelService> dataModelService ) {
        this.view = view;
        this.addImportPopup = addImportPopup;
        this.importAddedEvent = importAddedEvent;
        this.importRemovedEvent = importRemovedEvent;
        this.dataModelService = dataModelService;
        view.setPresenter( this );
    }

    @Override
    public void setContent( final Path path,
                            final Imports resourceImports,
                            final boolean isReadOnly ) {
        checkNotNull( "path",
                      path );
        checkNotNull( "imports",
                      resourceImports );
        checkNotNull( "imports",
                      resourceImports.getImports() );

        this.path = path;
        this.resourceImports = resourceImports;

        view.setReadOnly( isReadOnly );

        //Add existing imports to view
        for ( Import item : resourceImports.getImports() ) {
            view.addImport( item.getType() );
        }

        //Get list of potential imports
        dataModelService.call( new RemoteCallback<String[]>() {

            @Override
            public void callback( final String[] response ) {
                imports = new ArrayList<Pair<String, String>>();
                for ( String item : response ) {
                    Pair<String, String> pair = new Pair( item,
                                                          item );
                    imports.add( pair );
                }
            }
        } ).getExternalFactTypes( path );
    }

    @Override
    public void onAddImport() {
        addImportPopup.show( imports,
                             new PopupItemSelectedCommand() {

                                 @Override
                                 public void setSelectedItem( final Pair<String, String> selectedItem ) {
                                     final String importClassName = selectedItem.getK1();
                                     final Import item = new Import( importClassName );
                                     view.addImport( importClassName );
                                     resourceImports.getImports().add( item );
                                     importAddedEvent.fire( new ImportAddedEvent( path, item ) );
                                 }

                             } );
    }

    @Override
    public void onRemoveImport() {
        String selected = view.getSelected();
        if ( selected == null ) {
            view.showPleaseSelectAnImport();
        } else {
            final Import item = new Import( selected );
            view.removeImport( selected );
            resourceImports.removeImport( item );

            importRemovedEvent.fire( new ImportRemovedEvent( path, item ) );
        }
    }

    @Override
    public Widget asWidget() {
        return view.asWidget();
    }

    public boolean isDirty() {
        return false; // TODO: -Rikkola-
    }
}
