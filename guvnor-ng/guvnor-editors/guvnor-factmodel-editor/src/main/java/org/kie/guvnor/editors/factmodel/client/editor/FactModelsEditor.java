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
package org.kie.guvnor.editors.factmodel.client.editor;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;
import org.jboss.errai.bus.client.api.RemoteCallback;
import org.jboss.errai.ioc.client.api.Caller;
import org.kie.guvnor.editors.factmodel.model.FactModelContent;
import org.kie.guvnor.editors.factmodel.service.FactModelService;
import org.kie.guvnor.editors.factmodel.client.resources.i18n.Constants;
import org.kie.guvnor.editors.factmodel.model.FactMetaModel;
import org.uberfire.backend.vfs.Path;
import org.uberfire.client.annotations.OnClose;
import org.uberfire.client.annotations.OnSave;
import org.uberfire.client.annotations.OnStart;
import org.uberfire.client.annotations.WorkbenchEditor;
import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.common.AbstractLazyStackPanelHeader;
import org.uberfire.client.common.AddButton;
import org.uberfire.client.common.LazyStackPanel;
import org.uberfire.client.common.LoadContentCommand;

@Dependent
@WorkbenchEditor(identifier = "FactModelsEditor", fileTypes = "model.drl")
public class FactModelsEditor
        extends Composite {

    @Inject
    private Caller<FactModelService> factModelService;

    private Path path;

    interface FactModelsEditorBinder
            extends
            UiBinder<Widget, FactModelsEditor> {

    }

    private static FactModelsEditorBinder uiBinder = GWT.create( FactModelsEditorBinder.class );

    @UiField
    LazyStackPanel factModelsPanel;

    @UiField
    AddButton addFactIcon;

    private List<FactMetaModel> factModels;

    private List<FactMetaModel> superTypeFactModels;

    private ModelNameHelper modelNameHelper;

    public FactModelsEditor() {
        initWidget( uiBinder.createAndBindUi( this ) );

        addFactIcon.setTitle( Constants.INSTANCE.AddNewFactType() );
        addFactIcon.setText( Constants.INSTANCE.AddNewFactType() );
    }

    @OnStart
    public void onStart( final Path path ) {
        this.path = path;
        factModelService.call( new RemoteCallback<FactModelContent>() {
            @Override
            public void callback( final FactModelContent content ) {

                final ModelNameHelper modelNameHelper = new ModelNameHelper();

                for ( final FactMetaModel currentModel : content.getSuperTypes() ) {
                    modelNameHelper.getTypeDescriptions().put( currentModel.getName(),
                                                               currentModel.getName() );
                }

                FactModelsEditor.this.factModels = content.getCurrentTypes();
                FactModelsEditor.this.superTypeFactModels = content.getSuperTypes();
                FactModelsEditor.this.modelNameHelper = modelNameHelper;

                for ( final FactMetaModel factMetaModel : factModels ) {
                    addFactModelToStackPanel( factMetaModel );
                }
            }
        } ).loadContent( path );
    }

    public void addFactModelToStackPanel( final FactMetaModel factMetaModel ) {
        final FactModelEditor editor = new FactModelEditor( factMetaModel,
                                                            superTypeFactModels,
                                                            modelNameHelper );

        editor.setMoveDownCommand( getMoveDownCommand( factMetaModel ) );

        editor.setMoveUpCommand( getMoveUpCommand( factMetaModel ) );

        editor.setDeleteEvent( getDeleteCommand( factMetaModel ) );

        factModelsPanel.add( editor,
                             new LoadContentCommand() {
                                 public Widget load() {
                                     return editor.getContent();
                                 }
                             } );

        renderEditorArrows();
    }

    private Command getDeleteCommand( final FactMetaModel factMetaModel ) {
        return new Command() {
            public void execute() {
                int index = factModels.indexOf( factMetaModel );

                modelNameHelper.getTypeDescriptions().remove( factMetaModel.getName() );
                factModels.remove( factMetaModel );
                superTypeFactModels.remove( factMetaModel );
                factModelsPanel.remove( index );
            }
        };
    }

    private Command getMoveUpCommand( final FactMetaModel factMetaModel ) {
        return new Command() {

            public void execute() {
                int editingFactIndex = factModels.indexOf( factMetaModel );
                int newIndex = editingFactIndex - 1;

                swap( editingFactIndex,
                      newIndex );

                renderEditorArrows();
            }

        };
    }

    private Command getMoveDownCommand( final FactMetaModel factMetaModel ) {
        return new Command() {

            public void execute() {
                int editingFactIndex = factModels.indexOf( factMetaModel );
                int newIndex = editingFactIndex + 1;

                swap( editingFactIndex,
                      newIndex );

                renderEditorArrows();
            }
        };
    }

    private void swap( int editingFactIndex,
                       int newIndex ) {
        Collections.swap( factModels,
                          editingFactIndex,
                          newIndex );

        factModelsPanel.swap( editingFactIndex,
                              newIndex );
    }

    private void renderEditorArrows() {
        Iterator<AbstractLazyStackPanelHeader> iterator = factModelsPanel.getHeaderIterator();

        while ( iterator.hasNext() ) {
            final AbstractLazyStackPanelHeader widget = iterator.next();

            if ( widget instanceof FactModelEditor ) {
                final FactModelEditor editor = (FactModelEditor) widget;

                int index = factModels.indexOf( editor.getFactModel() );
                editor.setUpVisible( index != 0 );
                editor.setDownVisible( index != ( factModels.size() - 1 ) );
            }
        }
    }

    @UiHandler("addFactIcon")
    void addFactClick( final ClickEvent event ) {
        final FactEditorPopup popup = new FactEditorPopup( modelNameHelper,
                                                           superTypeFactModels );

        popup.setOkCommand( new Command() {
            public void execute() {
                FactMetaModel factMetaModel = popup.getFactModel();

                factModels.add( factMetaModel );
                superTypeFactModels.add( factMetaModel );
                addFactModelToStackPanel( factMetaModel );
            }
        } );
        popup.show();
    }

    @OnSave
    public void onSave() {
        factModelService.call( new RemoteCallback<Path>() {
            @Override
            public void callback( Path response ) {

            }
        } ).save( path, factModels );
    }

    @OnClose
    public void onClose() {
        this.path = null;
    }

    @WorkbenchPartTitle
    public String getTitle() {
        return "Fact Models Editor [" + path.getFileName() + "]";
    }
}
