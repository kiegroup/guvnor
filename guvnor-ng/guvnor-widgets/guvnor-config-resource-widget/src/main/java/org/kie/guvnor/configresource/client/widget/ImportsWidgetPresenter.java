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

import javax.enterprise.event.Event;

import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;
import org.kie.guvnor.commons.ui.client.popup.FormPopup;
import org.kie.guvnor.commons.ui.client.popup.PopupSetFieldCommand;
import org.kie.guvnor.services.config.events.ImportAddedEvent;
import org.kie.guvnor.services.config.events.ImportRemovedEvent;
import org.kie.guvnor.services.config.model.imports.Import;
import org.kie.guvnor.services.config.model.imports.Imports;
import org.uberfire.backend.vfs.Path;

import static org.kie.commons.validation.PortablePreconditions.*;

public class ImportsWidgetPresenter
        implements ImportsWidgetView.Presenter,
                   IsWidget {

    private Imports resourceImports;

    private ImportsWidgetView view;
    private final FormPopup addImportPopup;

    private Event<ImportAddedEvent> importAddedEvent;
    private Event<ImportRemovedEvent> importRemovedEvent;

    @Inject
    public ImportsWidgetPresenter( final ImportsWidgetView view,
                                   final FormPopup addImportPopup,
                                   final Event<ImportAddedEvent> importAddedEvent,
                                   final Event<ImportRemovedEvent> importRemovedEvent ) {
        this.view = view;
        this.addImportPopup = addImportPopup;
        this.importAddedEvent = importAddedEvent;
        this.importRemovedEvent = importRemovedEvent;
        view.setPresenter( this );
    }

    public void setReadOnly() {
        view.setupReadOnly();
    }

    public void setImports( final Imports resourceImports ) {
        checkNotNull( "imports",
                      resourceImports );
        checkNotNull( "resourcePath",
                      resourceImports.getImports() );
        checkNotNull( "imports",
                      resourceImports.getImports() );

        this.resourceImports = resourceImports;

        for ( Import item : resourceImports.getImports() ) {
            view.addImport( item.getType() );
        }
    }

    @Override
    public void onAddImport() {
        addImportPopup.show( new PopupSetFieldCommand() {
            @Override
            public void setName( String name ) {
                final Import item = new Import( name );
                view.addImport( name );
                resourceImports.getImports().add( item );
                importAddedEvent.fire( new ImportAddedEvent( resourceImports.getResourcePath(),
                                                             item ) );
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
            importRemovedEvent.fire( new ImportRemovedEvent( resourceImports.getResourcePath(),
                                                             item ) );
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
