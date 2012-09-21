/*
 * Copyright 2005 JBoss Inc
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
package org.drools.guvnor.client.widgets.drools.toolbar;


import com.google.gwt.user.client.ui.*;
import org.drools.guvnor.client.common.FormStylePopup;
import org.drools.guvnor.client.common.GenericCallback;
import org.drools.guvnor.client.common.LoadingPopup;
import org.drools.guvnor.client.common.StatusChangePopup;
import org.drools.guvnor.client.explorer.ClientFactory;
import org.drools.guvnor.client.explorer.ModuleEditorPlace;
import org.drools.guvnor.client.explorer.navigation.ClosePlaceEvent;
import org.drools.guvnor.client.messages.Constants;
import org.drools.guvnor.client.moduleeditor.ModuleNameValidator;
import org.drools.guvnor.client.moduleeditor.RefreshModuleListEvent;
import org.drools.guvnor.client.moduleeditor.drools.PackageBuilderWidget;
import org.drools.guvnor.client.moduleeditor.drools.SuggestionCompletionCache;
import org.drools.guvnor.client.resources.DroolsGuvnorImages;
import org.drools.guvnor.client.rpc.Module;
import org.drools.guvnor.client.rpc.ModuleService;
import org.drools.guvnor.client.rpc.ModuleServiceAsync;
import org.drools.guvnor.client.widgets.toolbar.ActionToolbarButtonsConfigurationProvider;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Window;

/**
 * This contains the widgets used to action a package
 * (ie checkin, change state, close window)
 */
public class PackageEditorActionToolbar extends Composite {

    interface PackageEditorActionToolbarBinder
            extends
            UiBinder<Widget, PackageEditorActionToolbar> {
    }


    private static PackageEditorActionToolbarBinder uiBinder = GWT.create(PackageEditorActionToolbarBinder.class);

    @UiField
    MenuItem saveChanges;

    @UiField
    MenuItem saveChangesAndClose;

    @UiField
    MenuItem archive;

    @UiField
    MenuItem delete;

    @UiField
    MenuItem copy;
    
    @UiField
    MenuItem rename;
    
    @UiField
    MenuItem promoteToGlobal;

    @UiField
    MenuItem selectWorkingSets;

    @UiField
    MenuItem validate;

    @UiField
    MenuItem verify;

    @UiField
    MenuItem viewSource;

    @UiField
    MenuItem changeStatus;

    @UiField
    Label status;

    @UiField
    MenuItem sourceMenu;
    
    private ModuleServiceAsync moduleService = GWT.create(ModuleService.class);

    private ActionToolbarButtonsConfigurationProvider actionToolbarButtonsConfigurationProvider = new PackageActionToolbarButtonsConfigurationProvider();
    private Module packageConfigData;
    private final EventBus eventBus;
    private final ClientFactory clientFactory;
    private Command refreshCommand;
    private boolean readOnly;
    
    public PackageEditorActionToolbar(Module data, 
                         ClientFactory clientFactory,
                         EventBus eventBus,
                         boolean readOnly,
                         Command refreshCommand) {     
        this.packageConfigData = data;
        this.eventBus = eventBus;
        this.clientFactory = clientFactory;
        this.readOnly = readOnly;
        this.refreshCommand =  refreshCommand;
        
        initWidget(uiBinder.createAndBindUi(this));

        setState(packageConfigData.getState());
        applyToolBarConfiguration();
        this.status.setVisible(this.actionToolbarButtonsConfigurationProvider.showStateLabel());
        
        initActionToolBar();
    }

    /**
     * Sets the visible status display.
     */
    public void setState(String newStatus) {
        status.setText(Constants.INSTANCE.statusIs(newStatus));
    }

    private void applyToolBarConfiguration() {
        saveChanges.setVisible(actionToolbarButtonsConfigurationProvider.showSaveButton());
        saveChangesAndClose.setVisible(actionToolbarButtonsConfigurationProvider.showSaveAndCloseButton());
        validate.setVisible(actionToolbarButtonsConfigurationProvider.showValidateButton());
        verify.setVisible(actionToolbarButtonsConfigurationProvider.showVerifyButton());
        viewSource.setVisible(actionToolbarButtonsConfigurationProvider.showViewSourceButton());
        copy.setVisible(actionToolbarButtonsConfigurationProvider.showCopyButton());
        rename.setVisible(actionToolbarButtonsConfigurationProvider.showRenameButton());
        promoteToGlobal.setVisible(actionToolbarButtonsConfigurationProvider.showPromoteToGlobalButton());
        archive.setVisible(actionToolbarButtonsConfigurationProvider.showArchiveButton());
        delete.setVisible(actionToolbarButtonsConfigurationProvider.showDeleteButton());
        changeStatus.setVisible(actionToolbarButtonsConfigurationProvider.showChangeStatusButton());
        selectWorkingSets.setVisible(actionToolbarButtonsConfigurationProvider.showSelectWorkingSetsButton());

        sourceMenu.setVisible(areSourceMenuChildrenVisible());
    }

    private boolean areSourceMenuChildrenVisible() {
        return validate.isVisible() || verify.isVisible() || viewSource.isVisible();
    }

    public void setSelectWorkingSetsCommand(Command command) {
        selectWorkingSets.setCommand(command);
    }

    public void setViewSourceCommand(Command command) {
        viewSource.setCommand(command);
    }

    public void setVerifyCommand(Command command) {
        verify.setCommand(command);
    }

    public void setValidateCommand(Command command) {
        validate.setCommand(command);
    }

    public void setSaveChangesCommand(Command command) {
        saveChanges.setCommand(command);
    }

    public void setSaveChangesAndCloseCommand(Command command) {
        saveChangesAndClose.setCommand(command);
    }

    public void setChangeStatusCommand(Command command) {
        changeStatus.setCommand(command);
    }

    public void setDeleteVisible(boolean b) {
        delete.setVisible(b);
    }

    public void setArchiveVisible(boolean b) {
        archive.setVisible(b);
    }

    public void setArchiveCommand(final Command archiveCommand) {
        archive.setCommand(new Command() {

            public void execute() {
                if (Window.confirm(Constants.INSTANCE.AreYouSureYouWantToArchiveThisItem())) {
                    archiveCommand.execute();
                }
            }
        });
    }

    public void setCopyCommand(Command command) {
        copy.setCommand(command);
    }
    
    public void setRenameCommand(Command command) {
        rename.setCommand(command);
    }
    
    public void setDeleteCommand(final Command deleteCommand) {
        delete.setCommand(new Command() {

            public void execute() {
                if (Window.confirm(Constants.INSTANCE.DeleteAreYouSure())) {
                    deleteCommand.execute();
                }
            }
        });
    }

    public void setPromtToGlobalCommand(Command command) {
        promoteToGlobal.setCommand(command);
    }
    
    /**
     * This will actually load up the data (this is called by the callback) when
     * we get the data back from the server, also determines what widgets to
     * load up).
     */
    private void initActionToolBar() {
        if ( readOnly ) {
            setVisible( false );
        } else {
            setSaveChangesCommand( new Command() {
                public void execute() {
                    doSave( null );
                }
            } );
            setArchiveCommand( new Command() {
                public void execute() {
                    doArchive();
                }
            } );
            setCopyCommand( new Command() {
                public void execute() {
                    doCopy();
                }
            } );
            setRenameCommand( new Command() {
                public void execute() {
                    doRename();
                }
            } );
            setChangeStatusCommand( new Command() {
                public void execute() {
                    showStatusChanger();
                }
            } );
            setViewSourceCommand( new Command() {
                public void execute() {
                    PackageBuilderWidget.doBuildSource( packageConfigData.getUuid(),
                            packageConfigData.getName() );
                }
            } );
        }

    }

    protected void showStatusChanger() {
        final StatusChangePopup pop = new StatusChangePopup( packageConfigData.getUuid(),
                true );
        pop.setChangeStatusEvent( new Command() {
            public void execute() {
                setState( pop.getState() );
            }
        } );

        pop.show();
    }

    private void doRename() {
        final FormStylePopup pop = new FormStylePopup(DroolsGuvnorImages.INSTANCE.Wizard(),
                Constants.INSTANCE.RenameThePackage() );
        pop.addRow( new HTML( Constants.INSTANCE.RenamePackageTip() ) );
        final TextBox name = new TextBox();
        pop.addAttribute( Constants.INSTANCE.NewPackageNameIs(),
                name );
        Button ok = new Button( Constants.INSTANCE.OK() );
        pop.addAttribute( "",
                ok );

        ok.addClickHandler( new ClickHandler() {
            public void onClick(ClickEvent event) {
                moduleService.renameModule( packageConfigData.getUuid(),
                        name.getText(),
                        new GenericCallback<String>() {
                            public void onSuccess(String data) {
                                completedRenaming( data );
                                pop.hide();
                            }
                        } );
            }
        } );

        pop.show();
    }

    private void completedRenaming(String newAssetUUID) {
        Window.alert( Constants.INSTANCE.PackageRenamedSuccessfully() );
        refreshPackageList();

        eventBus.fireEvent( new ClosePlaceEvent( new ModuleEditorPlace( newAssetUUID ) ) );

        openModule( newAssetUUID );
    }

    private void openModule(String newAssetUUID) {
        clientFactory.getDeprecatedPlaceController().goTo( new ModuleEditorPlace( newAssetUUID ) );
    }

    /**
     * Will show a copy dialog for copying the whole package.
     */
    private void doCopy() {
        final FormStylePopup pop = new FormStylePopup(DroolsGuvnorImages.INSTANCE.Wizard(),
                Constants.INSTANCE.CopyThePackage() );
        pop.addRow( new HTML( Constants.INSTANCE.CopyThePackageTip() ) );
        final TextBox name = new TextBox();
        pop.addAttribute( Constants.INSTANCE.NewPackageNameIs(),
                name );
        Button ok = new Button( Constants.INSTANCE.OK() );
        pop.addAttribute( "",
                ok );

        ok.addClickHandler( new ClickHandler() {

            public void onClick(ClickEvent event) {
                if ( !ModuleNameValidator.validatePackageName( name.getText() ) ) {
                    Window.alert( Constants.INSTANCE.NotAValidPackageName() );
                    return;
                }
                LoadingPopup.showMessage( Constants.INSTANCE.PleaseWaitDotDotDot() );
                moduleService.copyModule( packageConfigData.getName(),
                        name.getText(),
                        new GenericCallback<String>() {
                            public void onSuccess(String uuid) {
                                completedCopying( uuid );
                                pop.hide();
                            }
                        } );
            }
        } );

        pop.show();
    }

    private void completedCopying(String newAssetUUID) {
        Window.alert( Constants.INSTANCE.PackageCopiedSuccessfully() );
        refreshPackageList();

        openModule( newAssetUUID );
    }

    private void doSave(final Command refresh) {
        LoadingPopup.showMessage( Constants.INSTANCE.SavingPackageConfigurationPleaseWait() );

        moduleService.saveModule( this.packageConfigData,
                new GenericCallback<Void>() {
                    public void onSuccess(Void data) {
                        refreshCommand.execute();
                        LoadingPopup.showMessage( Constants.INSTANCE.PackageConfigurationUpdatedSuccessfullyRefreshingContentCache() );

                        SuggestionCompletionCache.getInstance().loadPackage( packageConfigData.getName(),
                                new Command() {
                                    public void execute() {
                                        if ( refresh != null ) {
                                            refresh.execute();
                                        }
                                        LoadingPopup.close();
                                    }
                                } );
                    }
                } );
    }

    private void doArchive() {
        packageConfigData.setArchived( true );
        Command ref = new Command() {
            public void execute() {
                eventBus.fireEvent( new ClosePlaceEvent( new ModuleEditorPlace( packageConfigData.getUuid() ) ) );
                refreshPackageList();
            }
        };
        doSave( ref );
    }

    private void refreshPackageList() {
        eventBus.fireEvent( new RefreshModuleListEvent() );
    }
}
