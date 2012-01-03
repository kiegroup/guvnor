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

import java.util.ArrayList;
import java.util.List;

import org.drools.guvnor.client.common.FormStylePopup;
import org.drools.guvnor.client.common.GenericCallback;
import org.drools.guvnor.client.common.ImageButton;
import org.drools.guvnor.client.common.InfoPopup;
import org.drools.guvnor.client.messages.Constants;
import org.drools.guvnor.client.resources.Images;
import org.drools.guvnor.client.rpc.Module;
import org.drools.guvnor.client.rpc.RepositoryServiceFactory;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * This is for managing imports etc.
 */
public class PackageHeaderWidget extends Composite {

    private Constants         constants            = GWT.create( Constants.class );
    private static Images     images               = GWT.create( Images.class );

    private Module conf;
    private SimplePanel       layout;
    private ListBox           importList;
    private ListBox           globalList;
    private boolean           isHistoricalReadOnly = false;

    public PackageHeaderWidget(Module conf,
                               boolean isHistoricalReadOnly) {
        this.conf = conf;
        this.isHistoricalReadOnly = isHistoricalReadOnly;
        layout = new SimplePanel();
        render();

        initWidget( layout );
    }

    private void render() {
        final Types t = PackageHeaderHelper.parseHeader( conf.getHeader() );
        if ( t == null ) {
            textEditorVersion();
        } else {
            basicEditorVersion( t );
        }
    }

    private void basicEditorVersion(final Types t) {
        layout.clear();
        HorizontalPanel main = new HorizontalPanel();

        VerticalPanel imports = new VerticalPanel();
        imports.add( new Label( constants.ImportedTypes() ) );
        importList = new ListBox( true );

        doImports( t );
        HorizontalPanel importCols = new HorizontalPanel();
        importCols.add( importList );
        VerticalPanel importActions = new VerticalPanel();
        if ( isHistoricalReadOnly ) {
            ImageButton newItemButton = new ImageButton( images.newItem(),
                                                         images.newItemDisabled() );
            newItemButton.setEnabled( false );
            importActions.add( newItemButton );

            ImageButton trashButton = new ImageButton( images.trash(),
                                                       images.trashDisabled() );
            trashButton.setEnabled( false );
            importActions.add( trashButton );
        } else {
            ImageButton newItemButton = new ImageButton( images.newItem(),
                                                         images.newItemDisabled() ) {
                {
                    addClickHandler( new ClickHandler() {
                        public void onClick(ClickEvent event) {
                            showTypeQuestion( (Widget) event.getSource(),
                                              t,
                                              false,
                                              constants.FactTypesJarTip() );
                        }
                    } );
                }
            };
            importActions.add( newItemButton );

            ImageButton trashButton = new ImageButton( images.trash(),
                                                       images.trashDisabled() ) {
                {
                    addClickHandler( new ClickHandler() {
                        public void onClick(ClickEvent event) {
                            if ( Window.confirm( constants.AreYouSureYouWantToRemoveThisFactType() ) ) {
                                if ( importList.getSelectedIndex() > -1 ) {
                                    for ( int i = 0; i < importList.getItemCount(); i++ ) {
                                        if ( importList.isItemSelected( i ) ) {
                                            importList.removeItem( i );
                                            t.imports.remove( i );
                                            i--;
                                        }
                                    }
                                    updateHeader( t );
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
        globals.add( new Label( constants.Globals() ) );
        globalList = new ListBox( true );
        doGlobals( t );
        HorizontalPanel globalCols = new HorizontalPanel();
        globalCols.add( globalList );
        VerticalPanel globalActions = new VerticalPanel();
        if ( isHistoricalReadOnly ) {
            ImageButton newItemButton = new ImageButton( images.newItem(),
                                                         images.newItemDisabled() );
            newItemButton.setEnabled( false );
            globalActions.add( newItemButton );

            ImageButton trashButton = new ImageButton( images.trash(),
                                                       images.trashDisabled() );
            trashButton.setEnabled( false );
            globalActions.add( trashButton );
        } else {
            ImageButton newItemButton = new ImageButton( images.newItem(),
                                                         images.newItemDisabled() ) {
                {
                    addClickHandler( new ClickHandler() {
                        public void onClick(ClickEvent event) {
                            showTypeQuestion( (Widget) event.getSource(),
                                              t,
                                              true,
                                              constants.GlobalTypesAreClassesFromJarFilesThatHaveBeenUploadedToTheCurrentPackage() );
                        }
                    } );
                }
            };
            globalActions.add( newItemButton );

            ImageButton trashButton = new ImageButton( images.trash(),
                                                       images.trashDisabled() ) {
                {
                    addClickHandler( new ClickHandler() {
                        public void onClick(ClickEvent event) {
                            if ( Window.confirm( constants.AreYouSureYouWantToRemoveThisGlobal() ) ) {
                                if ( globalList.getSelectedIndex() > -1 ) {
                                    for ( int i = 0; i < globalList.getItemCount(); i++ ) {
                                        if ( globalList.isItemSelected( i ) ) {
                                            globalList.removeItem( i );
                                            t.globals.remove( i );
                                            i--;
                                        }
                                    }
                                    updateHeader( t );
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
                setText( constants.AdvancedView() );
                setTitle( constants.SwitchToTextModeEditing() );
                addClickHandler( new ClickHandler() {
                    public void onClick(ClickEvent event) {
                        if ( Window.confirm( constants.SwitchToAdvancedTextModeForPackageEditing() ) ) {
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
                setText( constants.BasicView() );
                setTitle( constants.SwitchToGuidedModeEditing() );
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
        final Types types = PackageHeaderHelper.parseHeader( conf.getHeader() );
        if ( types == null ) {
            Window.alert( constants.CanNotSwitchToBasicView() );
        } else {
            if ( types.hasDeclaredTypes ) {
                Window.alert( constants.CanNotSwitchToBasicViewDeclaredTypes() );
            } else if ( types.hasFunctions ) {
                Window.alert( constants.CanNotSwitchToBasicViewFunctions() );
            } else if ( types.hasRules ) {
                Window.alert( constants.CanNotSwitchToBasicViewRules() );
            } else {
                if ( Window.confirm( constants.SwitchToGuidedModeForPackageEditing() ) ) {
                    basicEditorVersion( types );
                }
            }
        }
    }

    private void showTypeQuestion(Widget w,
                                  final Types t,
                                  final boolean global,
                                  String headerMessage) {
        final FormStylePopup pop = new FormStylePopup( images.homeIcon(),
                                                       constants.ChooseAFactType() );
        pop.addRow( new HTML( "<small><i>" + headerMessage + " </i></small>" ) ); //NON-NLS
        final ListBox factList = new ListBox();
        factList.addItem( constants.loadingList() );

        RepositoryServiceFactory.getPackageService().listTypesInPackage( this.conf.getUuid(),
                                                                         createGenericCallbackForListTypesInPackage( global,
                                                                                                                     factList ) );

        InfoPopup info = new InfoPopup( constants.TypesInThePackage(),
                                        constants.IfNoTypesTip() );

        pop.addAttribute( constants.ChooseClassType(),
                          createHorizontalPanel( factList,
                                                 info ) );
        final TextBox globalName = new TextBox();
        if ( global ) {
            pop.addAttribute( constants.GlobalName(),
                              globalName );
        }
        final TextBox className = new TextBox();
        InfoPopup infoClass = new InfoPopup( constants.EnteringATypeClassName(),
                                             constants.EnterTypeNameTip() );
        pop.addAttribute( constants.advancedClassName(),
                          createHorizontalPanel( className,
                                                 infoClass ) );

        Button ok = new Button( constants.OK() ) {
            {
                addClickHandler( new ClickHandler() {
                    public void onClick(ClickEvent event) {
                        String type = (!"".equals( className.getText() )) ? className.getText() : factList.getItemText( factList.getSelectedIndex() );
                        if ( !global ) {
                            t.imports.add( new Import( type ) );
                            doImports( t );
                        } else {
                            if ( "".equals( globalName.getText() ) ) {
                                Window.alert( constants.YouMustEnterAGlobalVariableName() );
                                return;
                            }
                            t.globals.add( new Global( type,
                                                       globalName.getText() ) );
                            doGlobals( t );
                        }
                        updateHeader( t );
                        pop.hide();
                    }
                } );
            }
        };

        Button cancel = new Button( constants.Cancel() ) {
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

    private void updateHeader(Types t) {
        this.conf.setHeader( PackageHeaderHelper.renderTypes( t ) );
    }

    private void doGlobals(Types t) {
        globalList.clear();
        for ( Global g : t.globals ) {
            globalList.addItem( g.type + " [" + g.name + "]" );
        }
    }

    private void doImports(Types t) {
        importList.clear();
        for ( Import i : t.imports ) {
            importList.addItem( i.type );
        }
    }

    static class Types {
        List<Import> imports = new ArrayList<Import>();
        List<Global> globals = new ArrayList<Global>();
        boolean      hasDeclaredTypes;
        boolean      hasFunctions;
        boolean      hasRules;
    }

    static class Import {
        String type;

        Import(String t) {
            this.type = t;
        }
    }

    static class Global {
        String type;
        String name;

        Global(String type,
               String name) {
            this.type = type;
            this.name = name;
        }
    }

}
