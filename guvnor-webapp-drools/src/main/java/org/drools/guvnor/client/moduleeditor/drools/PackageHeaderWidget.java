/*
 * Copyright 2010 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.guvnor.client.moduleeditor.drools;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.*;
import org.drools.guvnor.client.common.FormStylePopup;
import org.drools.guvnor.client.common.GenericCallback;
import org.drools.guvnor.client.common.ImageButton;
import org.drools.guvnor.client.common.InfoPopup;
import org.drools.guvnor.client.messages.Constants;
import org.drools.guvnor.client.resources.DroolsGuvnorImageResources;
import org.drools.guvnor.client.resources.DroolsGuvnorImages;
import org.drools.guvnor.client.rpc.Module;
import org.drools.guvnor.client.rpc.ModuleService;
import org.drools.guvnor.client.rpc.ModuleServiceAsync;
import org.drools.guvnor.client.rpc.Path;
import org.drools.guvnor.client.rpc.PathImpl;
import org.drools.guvnor.shared.modules.ModuleHeader;
import org.drools.guvnor.shared.modules.ModuleHeader.Global;
import org.drools.guvnor.shared.modules.ModuleHeader.Import;
import org.drools.guvnor.shared.modules.ModuleHeaderHelper;

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;

/**
 * This is for managing imports etc.
 */
public class PackageHeaderWidget extends Composite {

    private Module      conf;
    private SimplePanel layout;
    private ListBox     importList;
    private ListBox     globalList;
    private boolean     isHistoricalReadOnly = false;

    public PackageHeaderWidget(Module conf,
                               boolean isHistoricalReadOnly) {
        this.conf = conf;
        this.isHistoricalReadOnly = isHistoricalReadOnly;
        layout = new SimplePanel();
        render();

        initWidget( layout );
    }

    private void render() {
        final ModuleHeader mh = ModuleHeaderHelper.parseHeader( conf.getHeader() );
        if ( mh == null ) {
            textEditorVersion();
        } else {
            basicEditorVersion( mh );
        }
    }

    private void basicEditorVersion(final ModuleHeader mh) {
        layout.clear();
        HorizontalPanel main = new HorizontalPanel();

        VerticalPanel imports = new VerticalPanel();
        imports.add( new Label( Constants.INSTANCE.ImportedTypes() ) );
        importList = new ListBox( true );

        doImports( mh );
        HorizontalPanel importCols = new HorizontalPanel();
        importCols.add( importList );
        VerticalPanel importActions = new VerticalPanel();
        if ( isHistoricalReadOnly ) {
            ImageButton newItemButton = new ImageButton( DroolsGuvnorImages.INSTANCE.NewItem(),
                                                         DroolsGuvnorImages.INSTANCE.NewItemDisabled() );
            newItemButton.setEnabled( false );
            importActions.add( newItemButton );

            ImageButton trashButton = new ImageButton( DroolsGuvnorImages.INSTANCE.Trash(),
                                                       DroolsGuvnorImages.INSTANCE.TrashDisabled() );
            trashButton.setEnabled( false );
            importActions.add( trashButton );
        } else {
            ImageButton newItemButton = new ImageButton( DroolsGuvnorImages.INSTANCE.NewItem(),
                                                         DroolsGuvnorImages.INSTANCE.NewItemDisabled() ) {
                {
                    addClickHandler( new ClickHandler() {
                        public void onClick(ClickEvent event) {
                            showTypeQuestion( (Widget) event.getSource(),
                                              mh,
                                              false,
                                              Constants.INSTANCE.FactTypesJarTip() );
                        }
                    } );
                }
            };
            importActions.add( newItemButton );

            ImageButton trashButton = new ImageButton( DroolsGuvnorImages.INSTANCE.Trash(),
                                                       DroolsGuvnorImages.INSTANCE.TrashDisabled()) {
                {
                    addClickHandler( new ClickHandler() {
                        public void onClick(ClickEvent event) {
                            if ( Window.confirm( Constants.INSTANCE.AreYouSureYouWantToRemoveThisFactType() ) ) {
                                if ( importList.getSelectedIndex() > -1 ) {
                                    for ( int i = 0; i < importList.getItemCount(); i++ ) {
                                        if ( importList.isItemSelected( i ) ) {
                                            importList.removeItem( i );
                                            mh.getImports().remove( i );
                                            i--;
                                        }
                                    }
                                    updateHeader( mh );
                                }
                            }
                        }
                    } );
                }
            };
            importActions.add( trashButton );
        }

        importCols.add( importActions );
        imports.add( importCols );

        VerticalPanel globals = new VerticalPanel();
        globals.add( new Label( Constants.INSTANCE.Globals() ) );
        globalList = new ListBox( true );
        doGlobals( mh );
        HorizontalPanel globalCols = new HorizontalPanel();
        globalCols.add( globalList );
        VerticalPanel globalActions = new VerticalPanel();
        if ( isHistoricalReadOnly ) {
            ImageButton newItemButton = new ImageButton( DroolsGuvnorImages.INSTANCE.NewItem(),
                                                         DroolsGuvnorImages.INSTANCE.NewItemDisabled() );
            newItemButton.setEnabled( false );
            globalActions.add( newItemButton );

            ImageButton trashButton = new ImageButton( DroolsGuvnorImages.INSTANCE.Trash(),
                                                       DroolsGuvnorImages.INSTANCE.TrashDisabled() );
            trashButton.setEnabled( false );
            globalActions.add( trashButton );
        } else {
            ImageButton newItemButton = new ImageButton( DroolsGuvnorImages.INSTANCE.NewItem(),
                                                         DroolsGuvnorImages.INSTANCE.NewItemDisabled() ) {
                {
                    addClickHandler( new ClickHandler() {
                        public void onClick(ClickEvent event) {
                            showTypeQuestion( (Widget) event.getSource(),
                                              mh,
                                              true,
                                              Constants.INSTANCE.GlobalTypesAreClassesFromJarFilesThatHaveBeenUploadedToTheCurrentPackage() );
                        }
                    } );
                }
            };
            globalActions.add( newItemButton );

            ImageButton trashButton = new ImageButton( DroolsGuvnorImages.INSTANCE.Trash(),
                                                       DroolsGuvnorImages.INSTANCE.TrashDisabled() ) {
                {
                    addClickHandler( new ClickHandler() {
                        public void onClick(ClickEvent event) {
                            if ( Window.confirm( Constants.INSTANCE.AreYouSureYouWantToRemoveThisGlobal() ) ) {
                                if ( globalList.getSelectedIndex() > -1 ) {
                                    for ( int i = 0; i < globalList.getItemCount(); i++ ) {
                                        if ( globalList.isItemSelected( i ) ) {
                                            globalList.removeItem( i );
                                            mh.getGlobals().remove( i );
                                            i--;
                                        }
                                    }
                                    updateHeader( mh );
                                }
                            }
                        }
                    } );
                }
            };
            globalActions.add( trashButton );
        }
        globalCols.add( globalActions );
        globals.add( globalCols );

        main.add( imports );
        main.add( new HTML( "&nbsp;" ) ); //NON-NLS
        main.add( globals );

        Button advanced = new Button() {
            {
                setText( Constants.INSTANCE.AdvancedView() );
                setTitle( Constants.INSTANCE.SwitchToTextModeEditing() );
                addClickHandler( new ClickHandler() {
                    public void onClick(ClickEvent event) {
                        if ( Window.confirm( Constants.INSTANCE.SwitchToAdvancedTextModeForPackageEditing() ) ) {
                            textEditorVersion();
                        }
                    }
                } );
            }
        };
        main.add( advanced );

        layout.add( main );
    }

    private void textEditorVersion() {
        layout.clear();

        VerticalPanel main = new VerticalPanel();

        final TextArea area = new TextArea();
        if ( isHistoricalReadOnly ) {
            area.setEnabled( false );
        }
        area.setWidth( "100%" );
        area.setVisibleLines( 8 );

        area.setCharacterWidth( 100 );

        area.setText( this.conf.getHeader() );
        area.addChangeHandler( new ChangeHandler() {
            public void onChange(ChangeEvent event) {
                conf.setHeader( area.getText() );
            }
        } );

        main.add( area );

        Button basicMode = createBasicModeButton( area );
        main.add( basicMode );

        layout.add( main );
    }

    private Button createBasicModeButton(final TextArea area) {
        Button basicMode = new Button() {
            {
                setText( Constants.INSTANCE.BasicView() );
                setTitle( Constants.INSTANCE.SwitchToGuidedModeEditing() );
                addClickHandler( createClickHanderForBasicModeButton( area ) );
            }

        };
        return basicMode;
    }

    private ClickHandler createClickHanderForBasicModeButton(final TextArea area) {
        return new ClickHandler() {
            public void onClick(ClickEvent event) {
                conf.setHeader( area.getText() );
                handleCasesForBasicModeButton();
            }

        };
    }

    private void handleCasesForBasicModeButton() {
        final ModuleHeader mh = ModuleHeaderHelper.parseHeader( conf.getHeader() );
        if ( mh == null ) {
            Window.alert( Constants.INSTANCE.CanNotSwitchToBasicView() );
        } else {
            if ( mh.hasDeclaredTypes() ) {
                Window.alert( Constants.INSTANCE.CanNotSwitchToBasicViewDeclaredTypes() );
            } else if ( mh.hasFunctions() ) {
                Window.alert( Constants.INSTANCE.CanNotSwitchToBasicViewFunctions() );
            } else if ( mh.hasRules() ) {
                Window.alert( Constants.INSTANCE.CanNotSwitchToBasicViewRules() );
            } else {
                if ( Window.confirm( Constants.INSTANCE.SwitchToGuidedModeForPackageEditing() ) ) {
                    basicEditorVersion( mh );
                }
            }
        }
    }

    private void showTypeQuestion(Widget w,
                                  final ModuleHeader mh,
                                  final boolean global,
                                  String headerMessage) {
        final FormStylePopup pop = new FormStylePopup(DroolsGuvnorImages.INSTANCE.Home(),
                                                       Constants.INSTANCE.ChooseAFactType() );
        pop.addRow( new HTML( "<small><i>" + headerMessage + " </i></small>" ) ); //NON-NLS
        final ListBox factList = new ListBox();
        factList.addItem( Constants.INSTANCE.loadingList() );

        ModuleServiceAsync moduleService = GWT.create(ModuleService.class);
        Path path = new PathImpl();
        path.setUUID(this.conf.getUuid());        
        moduleService.listTypesInPackage( path,
                                                                         createGenericCallbackForListTypesInPackage( global,
                                                                                                                     factList ) );

        InfoPopup info = new InfoPopup( Constants.INSTANCE.TypesInThePackage(),
                                        Constants.INSTANCE.IfNoTypesTip() );

        pop.addAttribute( Constants.INSTANCE.ChooseClassType(),
                          createHorizontalPanel( factList,
                                                 info ) );
        final TextBox globalName = new TextBox();
        if ( global ) {
            pop.addAttribute( Constants.INSTANCE.GlobalName(),
                              globalName );
        }
        final TextBox className = new TextBox();
        InfoPopup infoClass = new InfoPopup( Constants.INSTANCE.EnteringATypeClassName(),
                                             Constants.INSTANCE.EnterTypeNameTip() );
        pop.addAttribute( Constants.INSTANCE.advancedClassName(),
                          createHorizontalPanel( className,
                                                 infoClass ) );

        Button ok = new Button( Constants.INSTANCE.OK() ) {
            {
                addClickHandler( new ClickHandler() {
                    public void onClick(ClickEvent event) {
                        String type = (!"".equals( className.getText() )) ? className.getText() : factList.getItemText( factList.getSelectedIndex() );
                        if ( !global ) {
                            mh.getImports().add( new Import( type ) );
                            doImports( mh );
                        } else {
                            if ( "".equals( globalName.getText() ) ) {
                                Window.alert( Constants.INSTANCE.YouMustEnterAGlobalVariableName() );
                                return;
                            }
                            mh.getGlobals().add( new Global( type,
                                                             globalName.getText() ) );
                            doGlobals( mh );
                        }
                        updateHeader( mh );
                        pop.hide();
                    }
                } );
            }
        };

        Button cancel = new Button( Constants.INSTANCE.Cancel() ) {
            {
                addClickHandler( new ClickHandler() {
                    public void onClick(ClickEvent event) {
                        pop.hide();
                    }
                } );
            }
        };

        HorizontalPanel buttonPanel = new HorizontalPanel();
        buttonPanel.add( ok );
        buttonPanel.add( cancel );
        pop.addAttribute( "",
                          buttonPanel );
        pop.show();
    }

    private HorizontalPanel createHorizontalPanel(final Widget... wigets) {
        HorizontalPanel horizontalPanel = new HorizontalPanel();
        for ( Widget widget : wigets ) {
            horizontalPanel.add( widget );
        }

        return horizontalPanel;
    }

    private GenericCallback<String[]> createGenericCallbackForListTypesInPackage(final boolean global,
                                                                                 final ListBox factList) {
        return new GenericCallback<String[]>() {
            public void onSuccess(String[] list) {
                factList.clear();
                for ( int i = 0; i < list.length; i++ ) {
                    if ( global ) {
                        factList.addItem( list[i] );
                    } else {
                        if ( list[i].indexOf( '.' ) > -1 ) {
                            factList.addItem( list[i] );
                        }
                    }
                }
            }
        };
    }

    private void updateHeader(ModuleHeader mh) {
        this.conf.setHeader( ModuleHeaderHelper.renderModuleHeader( mh ) );
    }

    private void doGlobals(ModuleHeader mh) {
        globalList.clear();
        for ( Global g : mh.getGlobals() ) {
            globalList.addItem( g.getType() + " [" + g.getName() + "]" );
        }
    }

    private void doImports(ModuleHeader mh) {
        importList.clear();
        for ( Import i : mh.getImports() ) {
            importList.addItem( i.getType() );
        }
    }

}
