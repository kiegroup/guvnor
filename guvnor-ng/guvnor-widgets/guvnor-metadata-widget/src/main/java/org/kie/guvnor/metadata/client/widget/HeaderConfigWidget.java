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

package org.kie.guvnor.metadata.client.widget;

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import org.jboss.errai.bus.client.api.RemoteCallback;
import org.jboss.errai.bus.client.api.base.MessageBuilder;
import org.jboss.errai.ioc.client.container.IOC;
import org.kie.guvnor.commons.data.header.HeaderConfig;
import org.kie.guvnor.commons.data.header.HeaderConfigAdvanced;
import org.kie.guvnor.commons.data.header.HeaderConfigBasic;
import org.kie.guvnor.commons.data.header.HeaderConfigHelper;
import org.kie.guvnor.commons.data.project.ProjectResources;
import org.kie.guvnor.commons.ui.client.widget.DecoratedTextArea;
import org.kie.guvnor.datamodel.service.DataModelService;
import org.kie.guvnor.metadata.client.resources.Images;
import org.kie.guvnor.metadata.client.resources.i18n.Constants;
import org.kie.guvnor.services.metadata.model.Metadata;
import org.uberfire.client.common.DirtyableComposite;
import org.uberfire.client.common.FormStylePopup;
import org.uberfire.client.common.ImageButton;
import org.uberfire.client.common.InfoPopup;

import static org.kie.commons.validation.PortablePreconditions.*;
import static org.kie.guvnor.commons.data.header.HeaderType.*;

public class HeaderConfigWidget extends DirtyableComposite {

    private final Metadata    metadata;
    private final SimplePanel layout;
    private final boolean     readOnly;
    private       ListBox     importList;

    private ProjectResources projectResources = null;

    public HeaderConfigWidget( final Metadata metadata,
                               final boolean readOnly ) {
        this.metadata = checkNotNull( "metadata", metadata );
        this.readOnly = readOnly;
        this.layout = new SimplePanel();

        if ( metadata.getHeaderType().equals( NONE ) ) {
            metadata.setHeaderConfig( new HeaderConfigBasic() );
        }

        if ( metadata.getHeaderType().equals( BASIC ) ) {
            setupBasic();
        } else {
            setupAdvanced();
        }

        initWidget( layout );
    }

    private void setupBasic() {
        layout.clear();

        final VerticalPanel main = new VerticalPanel();

        final VerticalPanel imports = new VerticalPanel();
        imports.add( new Label( Constants.INSTANCE.ImportedTypes() ) );

        importList = new ListBox( true );

        doImports();

        final HorizontalPanel importCols = new HorizontalPanel();
        importCols.add( importList );
        final VerticalPanel importActions = new VerticalPanel();

        if ( readOnly ) {
            final ImageButton newItemButton = new ImageButton( Images.INSTANCE.NewItem(),
                                                               Images.INSTANCE.NewItemDisabled() );
            newItemButton.setEnabled( false );
            importActions.add( newItemButton );

            final ImageButton trashButton = new ImageButton( Images.INSTANCE.Trash(),
                                                             Images.INSTANCE.TrashDisabled() );
            trashButton.setEnabled( false );
            importActions.add( trashButton );
        } else {
            final ImageButton newItemButton = new ImageButton( Images.INSTANCE.NewItem(),
                                                               Images.INSTANCE.NewItemDisabled() ) {{
                addClickHandler( new ClickHandler() {
                    public void onClick( ClickEvent event ) {
                        showTypeQuestion( Constants.INSTANCE.FactTypesJarTip() );
                    }
                } );
            }};
            importActions.add( newItemButton );

            final ImageButton trashButton = new ImageButton( Images.INSTANCE.Trash(),
                                                             Images.INSTANCE.TrashDisabled() ) {{
                addClickHandler( new ClickHandler() {
                    public void onClick( ClickEvent event ) {
                        if ( Window.confirm( Constants.INSTANCE.AreYouSureYouWantToRemoveThisFactType() ) ) {
                            if ( importList.getSelectedIndex() > -1 ) {
                                for ( int i = 0; i < importList.getItemCount(); i++ ) {
                                    if ( importList.isItemSelected( i ) ) {
                                        makeDirty();
                                        importList.removeItem( i );
                                        ( (HeaderConfigBasic) metadata.getHeaderConfig() ).removeImport( i );
                                        i--;
                                    }
                                }
                            }
                        }
                    }
                } );
            }};
            importActions.add( trashButton );
        }

        importCols.add( importActions );
        imports.add( importCols );

        final Button advanced = new Button() {{
            setText( Constants.INSTANCE.AdvancedView() );
            setTitle( Constants.INSTANCE.SwitchToTextModeEditing() );
            addClickHandler( new ClickHandler() {
                public void onClick( ClickEvent event ) {
                    if ( Window.confirm( Constants.INSTANCE.SwitchToAdvancedTextModeForPackageEditing() ) ) {
                        setupAdvanced();
                    }
                }
            } );
        }};

        main.add( imports );
        main.add( advanced );

        layout.add( main );
    }

    private void setupAdvanced() {
        layout.clear();

        final String originalContent = metadata.getHeaderConfig().toString();
        final VerticalPanel main = new VerticalPanel();

        final DecoratedTextArea area = new DecoratedTextArea();
        if ( readOnly ) {
            area.setEnabled( false );
        }
        area.setWidth( "95%" );
        area.setVisibleLines( 8 );

        area.setText( metadata.getHeaderConfig().toString() );
        area.addChangeHandler( new ChangeHandler() {
            public void onChange( final ChangeEvent event ) {
                makeDirty();
                if ( metadata.getHeaderType().equals( ADVANCED ) ) {
                    ( (HeaderConfigAdvanced) metadata.getHeaderConfig() ).setContent( area.getText() );
                } else {
                    metadata.setHeaderConfig( new HeaderConfigAdvanced( area.getText() ) );
                }
            }
        } );

        final Button basicMode = new Button() {{
            setText( Constants.INSTANCE.BasicView() );
            setTitle( Constants.INSTANCE.SwitchToGuidedModeEditing() );
            addClickHandler( new ClickHandler() {
                public void onClick( ClickEvent event ) {
                    if ( !originalContent.equals( area.getText() ) ) {
                        makeDirty();
                        final HeaderConfig config = HeaderConfigHelper.parseHeaderConfig( area.getText() );
                        if ( config == null || config.getType().equals( NONE ) ) {
                            Window.alert( Constants.INSTANCE.CanNotSwitchToBasicView() );
                        } else if ( config.getType().equals( ADVANCED ) && ( (HeaderConfigAdvanced) config ).hasDeclaredTypes() ) {
                            Window.alert( Constants.INSTANCE.CanNotSwitchToBasicViewDeclaredTypes() );
                        } else if ( config.getType().equals( ADVANCED ) && ( (HeaderConfigAdvanced) config ).hasFunctions() ) {
                            Window.alert( Constants.INSTANCE.CanNotSwitchToBasicViewFunctions() );
                        } else if ( config.getType().equals( ADVANCED ) && ( (HeaderConfigAdvanced) config ).hasRules() ) {
                            Window.alert( Constants.INSTANCE.CanNotSwitchToBasicViewRules() );
                        } else if ( Window.confirm( Constants.INSTANCE.SwitchToGuidedModeForPackageEditing() ) ) {
                            metadata.setHeaderConfig( config );
                            setupBasic();
                        }
                    } else {
                        setupBasic();
                    }
                }
            } );
        }};

        main.add( area );
        main.add( basicMode );

        layout.add( main );
    }

    private void doImports() {
        importList.clear();
        for ( final HeaderConfigBasic.Import i : ( (HeaderConfigBasic) metadata.getHeaderConfig() ).getImports() ) {
            importList.addItem( i.getType() );
        }
    }

    private void showTypeQuestion( final String headerMessage ) {
        final FormStylePopup pop = new FormStylePopup( Images.INSTANCE.Home(),
                                                       Constants.INSTANCE.ChooseAFactType() );
        pop.addRow( new HTML( "<small><i>" + headerMessage + " </i></small>" ) ); //NON-NLS
        final ListBox factList = new ListBox();
        factList.addItem( Constants.INSTANCE.loadingList() );

        MessageBuilder.createCall( new RemoteCallback<String[]>() {
            public void callback( final String[] types ) {
                factList.clear();
                for ( int i = 0; i < types.length; i++ ) {
                    if ( types[ i ].indexOf( '.' ) > -1 ) {
                        factList.addItem( types[ i ] );
                    }
                }
            }
        }, DataModelService.class ).getFactTypes( getProjectResources().getProject( metadata.getPath() ) );

        final InfoPopup info = new InfoPopup( Constants.INSTANCE.TypesInThePackage(),
                                              Constants.INSTANCE.IfNoTypesTip() );

        pop.addAttribute( Constants.INSTANCE.ChooseClassType(),
                          createHorizontalPanel( factList, info ) );

        final TextBox className = new TextBox();

        final InfoPopup infoClass = new InfoPopup( Constants.INSTANCE.EnteringATypeClassName(),
                                                   Constants.INSTANCE.EnterTypeNameTip() );

        pop.addAttribute( Constants.INSTANCE.advancedClassName(),
                          createHorizontalPanel( className, infoClass ) );

        final Button ok = new Button( Constants.INSTANCE.OK() ) {{
            addClickHandler( new ClickHandler() {
                public void onClick( ClickEvent event ) {
                    makeDirty();
                    final String type = ( !"".equals( className.getText() ) ) ? className.getText() : factList.getItemText( factList.getSelectedIndex() );
                    ( (HeaderConfigBasic) metadata.getHeaderConfig() ).addImport( new HeaderConfigBasic.Import( type ) );
                    doImports();
                    pop.hide();
                }
            } );
        }};

        final Button cancel = new Button( Constants.INSTANCE.Cancel() ) {{
            addClickHandler( new ClickHandler() {
                public void onClick( ClickEvent event ) {
                    pop.hide();
                }
            } );
        }};

        final HorizontalPanel buttonPanel = new HorizontalPanel();
        buttonPanel.add( ok );
        buttonPanel.add( cancel );
        pop.addAttribute( "", buttonPanel );
        pop.show();
    }

    private HorizontalPanel createHorizontalPanel( final Widget... wigets ) {
        HorizontalPanel horizontalPanel = new HorizontalPanel();
        for ( final Widget widget : wigets ) {
            horizontalPanel.add( widget );
        }

        return horizontalPanel;
    }

    private ProjectResources getProjectResources() {
        if ( projectResources == null ) {
            projectResources = IOC.getBeanManager().lookupBean( ProjectResources.class ).getInstance();
        }
        return projectResources;
    }

}
