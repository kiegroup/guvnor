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

package org.drools.guvnor.client.explorer.navigation.admin.widget;

import com.google.gwt.user.client.ui.*;
import org.drools.guvnor.client.common.GenericCallback;
import org.drools.guvnor.client.common.InfoPopup;
import org.drools.guvnor.client.common.PrettyFormLayout;
import org.drools.guvnor.client.common.RdbmsConfigurable;
import org.drools.guvnor.client.messages.ConstantsCore;
import org.drools.guvnor.client.resources.GuvnorImages;
import org.drools.guvnor.client.resources.ImagesCore;
import org.drools.guvnor.client.rpc.RepositoryService;
import org.drools.guvnor.client.rpc.RepositoryServiceAsync;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.FlexTable.FlexCellFormatter;
import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.annotations.WorkbenchPartView;
import org.uberfire.client.annotations.WorkbenchScreen;
import javax.enterprise.context.Dependent;

@Dependent
@WorkbenchScreen(identifier = "repositoryConfigManager")
public class RepoConfigManager extends Composite {

    private RepositoryServiceAsync service            = GWT.create(RepositoryService.class);
    private RdbmsConfigurable      rdbmsConf          = new RdbmsConfigurable();
    private VerticalPanel          vPanel2            = new VerticalPanel();
    private RichTextArea           repoDisplayArea    = new RichTextArea();
    private FlexTable              layoutB            = new FlexTable();
    private FlexTable              layoutC            = new FlexTable();
    private DecoratorPanel         noJndiInfo         = getNoJNDIDbPanel();
    private DecoratorPanel         jndiInfo           = getJNDIDbPanel();
    private FormPanel              saveRepoConfigForm = new FormPanel();
    private Hidden                 hiddenRepoConfig   = new Hidden( "repoConfig" );

    @SuppressWarnings("deprecation")
    public RepoConfigManager() {     
        PrettyFormLayout form = new PrettyFormLayout();
        form.addHeader( GuvnorImages.INSTANCE.Config(),
                        new HTML( ConstantsCore.INSTANCE.ManageRepositoryConfig() ) );
        
        DockPanel dock = new DockPanel();
        dock.setSpacing( 4 );
        dock.setHorizontalAlignment( DockPanel.ALIGN_CENTER );
        HorizontalPanel hPanel1 = new HorizontalPanel();
        hPanel1.add(getDbTypePanel());
        VerticalPanel dataInputPanel = new VerticalPanel();
        dataInputPanel.add(hPanel1);
        SimplePanel divider = new SimplePanel();
        divider.setSize( "100px",
                         "30px" );
        vPanel2.add( divider );
        vPanel2.add( noJndiInfo );
        vPanel2.add( jndiInfo );
        vPanel2.setVisible( false );
        dataInputPanel.add(vPanel2);
        dock.add(dataInputPanel,
                  DockPanel.WEST );

        repoDisplayArea.setSize( "740px",
                                 "470px" );
        repoDisplayArea.setTitle( "repository.xml" );
        repoDisplayArea.setVisible( false );

        DockPanel idock = new DockPanel();
        idock.setSpacing( 4 );
        idock.setHorizontalAlignment( DockPanel.ALIGN_CENTER );
        idock.add( repoDisplayArea,
                   DockPanel.WEST );

        final Button saveButton = new Button( ConstantsCore.INSTANCE.SaveRepo() );
        saveButton.addClickHandler( new ClickHandler() {
            public void onClick(ClickEvent w) {
                String name = rdbmsConf.getDbType() + "-repository";
                if ( rdbmsConf.isJndi() ) name += "-jndi";
                hiddenRepoConfig.setValue( repoDisplayArea.getText() );
                saveRepoConfigForm.submit();
            }
        } );

        saveRepoConfigForm.setEncoding( FormPanel.ENCODING_URLENCODED );
        saveRepoConfigForm.setMethod( FormPanel.METHOD_POST );
        saveRepoConfigForm.setAction( GWT.getModuleBaseURL() + "backup" );

        VerticalPanel formHolder = new VerticalPanel();
        HorizontalPanel saveInfoHolder = new HorizontalPanel();
        saveInfoHolder.add( saveButton );
        saveInfoHolder.add( new InfoPopup( ConstantsCore.INSTANCE.SaveRepo(),
                                           ConstantsCore.INSTANCE.SaveRepoInfo() ) );
        formHolder.add( saveInfoHolder );
        formHolder.add( hiddenRepoConfig );

        saveRepoConfigForm.add( formHolder );
        saveRepoConfigForm.setVisible( false );

        idock.add( saveRepoConfigForm,
                   DockPanel.EAST );

        dock.add( idock,
                  DockPanel.EAST );

        form.startSection( ConstantsCore.INSTANCE.ManageRepositoryConfigDesc() );
        form.addAttribute( "",
                           dock );
        form.endSection();
        initWidget( form );
    }

    public static ListBox getDatabaseList() {
        ConstantsCore cons = ((ConstantsCore) GWT.create( ConstantsCore.class ));
        ListBox list = new ListBox();
        list.addItem( cons.Choose() );

        list.addItem( "Microsoft SQL Server",
                      "mssql" );
        list.addItem( "MySQL",
                      "mysql" );
        list.addItem( "Oracle 9i",
                      "oracle9i" );
        list.addItem( "Oracle 10g",
                      "oracle10g" );
        list.addItem( "Oracle 11",
                      "oracle11" );
        list.addItem( "PostgreSQL",
                      "postgressql" );
        list.addItem( "Derby",
                      "derby" );
        list.addItem( "H2",
                      "h2" );

        return list;
    }

    private void generateConfig() {
        String name = rdbmsConf.getDbType() + "-repository";
        if ( rdbmsConf.isJndi() ) name += "-jndi";
        service.processTemplate( name,
                                 rdbmsConf.getMapRep(),
                                 new GenericCallback<String>() {
                                     public void onSuccess(String repoc) {
                                         showRepoSource( repoc );
                                     }
                                 } );
        if ( !repoDisplayArea.isVisible() ) {
            repoDisplayArea.setVisible( true );
            saveRepoConfigForm.setVisible( true );
        }
    }

    private void showRepoSource(String src) {
        //repoDisplayArea.setText(src);
        repoDisplayArea.setHTML( "<pre>" + src + "</pre>" );
    }

    public DecoratorPanel getDbTypePanel() {
        FlexTable layoutA = new FlexTable();
        layoutA.setCellSpacing( 6 );
        FlexCellFormatter cellFormatter = layoutA.getFlexCellFormatter();

        // Add a title to the form
        layoutA.setHTML( 0,
                         0,
                         "RDBMS Info" );
        cellFormatter.setColSpan( 0,
                                  0,
                                  2 );
        cellFormatter.setHorizontalAlignment( 0,
                                              0,
                                              HasHorizontalAlignment.ALIGN_CENTER );

        layoutA.setHTML( 1,
                         0,
                         ConstantsCore.INSTANCE.SelectRdbmsType() );
        final ListBox databaseList = getDatabaseList();
        databaseList.addChangeHandler( new ChangeHandler() {

            public void onChange(ChangeEvent event) {
                ListBox listBox = (ListBox) event.getSource();
                int index = listBox.getSelectedIndex();
                rdbmsConf.setDbType( listBox.getItemText( index ) );
                layoutB.setHTML( 0,
                                 0,
                                 listBox.getItemText( index ) + " Info" );
                layoutC.setHTML( 0,
                                 0,
                                 listBox.getItemText( index ) + " Info" );
                repoDisplayArea.setVisible( false );
                saveRepoConfigForm.setVisible( false );
            }
        } );
        if ( rdbmsConf.getDbType() == null || rdbmsConf.getDbType().length() < 1 ) {
            databaseList.setSelectedIndex( 0 );
        } else {
            for ( int i = 0; i < databaseList.getItemCount(); i++ ) {
                if ( rdbmsConf.getDbType().equals( databaseList.getItemText( i ) ) ) {
                    databaseList.setSelectedIndex( i );
                    break;
                }
            }
        }
        databaseList.addChangeHandler( new ChangeHandler() {
            public void onChange(ChangeEvent event) {
                rdbmsConf.setDbType( databaseList.getValue( databaseList.getSelectedIndex() ) );
            }
        } );
        layoutA.setWidget( 1,
                           1,
                           databaseList );

        layoutA.setHTML( 2,
                         0,
                         ConstantsCore.INSTANCE.UseJndi() );

        final CheckBox useJndi = new CheckBox();
        useJndi.setChecked( rdbmsConf.isJndi() );
        useJndi.addClickHandler( new ClickHandler() {
            public void onClick(ClickEvent w) {
                // do not change this to isEnabled..it will always return true.
                boolean checked = ((CheckBox) w.getSource()).getValue();
                rdbmsConf.setJndi( checked );
                if ( checked ) {
                    noJndiInfo.setVisible( false );
                    jndiInfo.setVisible( true );
                } else {
                    noJndiInfo.setVisible( true );
                    jndiInfo.setVisible( false );
                }
                repoDisplayArea.setVisible( false );
                saveRepoConfigForm.setVisible( false );
            }
        } );
        layoutA.setWidget( 2,
                           1,
                           useJndi );

        Button continueButton = new Button( "Continue" );
        continueButton.addClickHandler( new ClickHandler() {
            public void onClick(ClickEvent w) {
                if ( databaseList.getSelectedIndex() == 0 ) {
                    Window.alert( ConstantsCore.INSTANCE.PleaseSelectRdbmsType() );
                    return;
                }
                if ( !useJndi.getValue() ) {
                    jndiInfo.setVisible( false );
                }
                vPanel2.setVisible( true );
            }
        } );

        layoutA.setWidget( 3,
                           1,
                           continueButton );
        DecoratorPanel decPanel = new DecoratorPanel();
        decPanel.setWidget( layoutA );
        return decPanel;
    }

    public DecoratorPanel getNoJNDIDbPanel() {
        layoutB.setCellSpacing( 6 );
        FlexCellFormatter cellFormatter = layoutB.getFlexCellFormatter();

        layoutB.setHTML( 0,
                         0,
                         "" );
        cellFormatter.setColSpan( 0,
                                  0,
                                  2 );
        cellFormatter.setHorizontalAlignment( 0,
                                              0,
                                              HasHorizontalAlignment.ALIGN_CENTER );

        layoutB.setHTML( 1,
                         0,
                         "Driver:" );
        final TextBox driverInput = new TextBox();
        if ( rdbmsConf.getDbDriver() != null && rdbmsConf.getDbDriver().trim().length() > 0 ) {
            driverInput.setValue( rdbmsConf.getDbDriver() );
        }
        driverInput.addClickHandler( new ClickHandler() {
            public void onClick(ClickEvent w) {
                repoDisplayArea.setVisible( false );
                saveRepoConfigForm.setVisible( false );
            }
        } );
        layoutB.setWidget( 1,
                           1,
                           driverInput );

        layoutB.setHTML( 2,
                         0,
                         "URL:" );
        final TextBox urlInput = new TextBox();
        if ( rdbmsConf.getDbUrl() != null && rdbmsConf.getDbUrl().trim().length() > 0 ) {
            urlInput.setValue( rdbmsConf.getDbUrl() );
        }
        urlInput.addClickHandler( new ClickHandler() {
            public void onClick(ClickEvent w) {
                repoDisplayArea.setVisible( false );
                saveRepoConfigForm.setVisible( false );
            }
        } );
        layoutB.setWidget( 2,
                           1,
                           urlInput );

        layoutB.setHTML( 3,
                         0,
                         "User:" );
        final TextBox userNameInput = new TextBox();
        if ( rdbmsConf.getDbUser() != null && rdbmsConf.getDbUser().trim().length() > 0 ) {
            userNameInput.setValue( rdbmsConf.getDbUser() );
        }
        userNameInput.addClickHandler( new ClickHandler() {
            public void onClick(ClickEvent w) {
                repoDisplayArea.setVisible( false );
                saveRepoConfigForm.setVisible( false );
            }
        } );
        layoutB.setWidget( 3,
                           1,
                           userNameInput );

        layoutB.setHTML( 4,
                         0,
                         "Password:" );
        final PasswordTextBox userPassInput = new PasswordTextBox();
        if ( rdbmsConf.getDbPass() != null && rdbmsConf.getDbPass().trim().length() > 0 ) {
            userPassInput.setValue( rdbmsConf.getDbPass() );
        }
        userPassInput.addClickHandler( new ClickHandler() {
            public void onClick(ClickEvent w) {
                repoDisplayArea.setVisible( false );
                saveRepoConfigForm.setVisible( false );
            }
        } );
        layoutB.setWidget( 4,
                           1,
                           userPassInput );

        Button generateButton = new Button( ConstantsCore.INSTANCE.GenerateRepositoryConfiguration() );
        generateButton.addClickHandler( new ClickHandler() {
            public void onClick(ClickEvent w) {
                if ( driverInput.getValue() == null || driverInput.getValue().trim().length() < 1 ) {
                    Window.alert( ConstantsCore.INSTANCE.PleaseEnterDriver() );
                    return;
                } else if ( urlInput.getValue() == null || urlInput.getValue().trim().length() < 1 ) {
                    Window.alert( ConstantsCore.INSTANCE.PleaseEnterUrl() );
                    return;
                } else if ( userNameInput.getValue() == null || userNameInput.getValue().trim().length() < 1 ) {
                    Window.alert( ConstantsCore.INSTANCE.PleaseEnterUserName() );
                    return;
                } else if ( userPassInput.getValue() == null || userPassInput.getValue().trim().length() < 1 ) {
                    Window.alert( ConstantsCore.INSTANCE.PleaseEnterPassword() );
                    return;
                }
                rdbmsConf.setDbDriver( driverInput.getValue() );
                rdbmsConf.setDbUrl( urlInput.getValue() );
                rdbmsConf.setDbUser( userNameInput.getValue() );
                rdbmsConf.setDbPass( userPassInput.getValue() );
                //rdbmsConf.setJndiDsName(jndiNameInput.getValue());
                generateConfig();
            }
        } );
        layoutB.setWidget( 5,
                           1,
                           generateButton );
        DecoratorPanel decPanel = new DecoratorPanel();
        decPanel.setWidget( layoutB );
        return decPanel;
    }

    public DecoratorPanel getJNDIDbPanel() {
        layoutC.setCellSpacing( 6 );
        FlexCellFormatter cellFormatter = layoutC.getFlexCellFormatter();

        layoutC.setHTML( 0,
                         0,
                         "" );
        cellFormatter.setColSpan( 0,
                                  0,
                                  2 );
        cellFormatter.setHorizontalAlignment( 0,
                                              0,
                                              HasHorizontalAlignment.ALIGN_CENTER );

        layoutC.setHTML( 1,
                         0,
                         "JNDI Name" );
        final TextBox jndiNameInput = new TextBox();
        if ( rdbmsConf.getJndiDsName() != null && rdbmsConf.getJndiDsName().trim().length() > 0 ) {
            jndiNameInput.setValue( rdbmsConf.getJndiDsName() );
        }
        jndiNameInput.addClickHandler( new ClickHandler() {
            public void onClick(ClickEvent w) {
                repoDisplayArea.setVisible( false );
                saveRepoConfigForm.setVisible( false );
            }
        } );
        layoutC.setWidget( 1,
                           1,
                           jndiNameInput );

        Button generateButton = new Button( ConstantsCore.INSTANCE.GenerateRepositoryConfiguration() );
        generateButton.addClickHandler( new ClickHandler() {
            public void onClick(ClickEvent w) {
                if ( jndiNameInput.getValue() == null || jndiNameInput.getValue().trim().length() < 1 ) {
                    Window.alert( ConstantsCore.INSTANCE.PleaseEnterJndiName() );
                    return;
                }
                rdbmsConf.setJndiDsName( jndiNameInput.getValue() );
                generateConfig();
            }
        } );
        layoutC.setWidget( 2,
                           1,
                           generateButton );

        // Wrap the content in a DecoratorPanel
        DecoratorPanel decPanel = new DecoratorPanel();
        decPanel.setWidget( layoutC );
        return decPanel;
    }

    @WorkbenchPartView
    public Widget asWidget() {
        return this;
    }

    @WorkbenchPartTitle
    public String getTitle() {
        return ConstantsCore.INSTANCE.RepositoryConfig();
    }
}