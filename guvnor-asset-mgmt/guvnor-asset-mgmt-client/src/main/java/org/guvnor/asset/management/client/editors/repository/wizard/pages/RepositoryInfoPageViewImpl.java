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

package org.guvnor.asset.management.client.editors.repository.wizard.pages;

import java.util.List;

import com.github.gwtbootstrap.client.ui.CheckBox;
import com.github.gwtbootstrap.client.ui.ControlGroup;
import com.github.gwtbootstrap.client.ui.HelpInline;
import com.github.gwtbootstrap.client.ui.ListBox;
import com.github.gwtbootstrap.client.ui.TextBox;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.InlineHTML;
import com.google.gwt.user.client.ui.Widget;
import org.uberfire.commons.data.Pair;
import org.uberfire.ext.widgets.core.client.resources.i18n.CoreConstants;

public class RepositoryInfoPageViewImpl extends Composite
        implements RepositoryInfoPageView {

    private Presenter presenter;

    interface RepositoryInfoPageBinder extends UiBinder<Widget, RepositoryInfoPageViewImpl> {

    }

    @UiField
    ControlGroup organizationalUnitGroup;

    @UiField
    HelpInline organizationalUnitHelpInline;

    @UiField
    ListBox organizationalUnitDropdown;

    @UiField
    ControlGroup nameGroup;

    @UiField
    TextBox nameTextBox;

    @UiField
    HelpInline nameHelpInline;

    @UiField
    InlineHTML isOUMandatory;

    @UiField
    ControlGroup managedReposiotryGroup;

    @UiField
    CheckBox isManagedRepository;

    @UiField
    HelpInline isManagedRepositoryHelpInline;

    private boolean managedRepository = false;

    private static RepositoryInfoPageBinder uiBinder = GWT.create( RepositoryInfoPageBinder.class );

    public RepositoryInfoPageViewImpl() {
        initWidget( uiBinder.createAndBindUi( this ) );
        initialiseFields();
    }

    @Override
    public void init( Presenter presenter ) {
        this.presenter = presenter;
    }

    @Override
    public String getName() {
        return nameTextBox.getText();
    }

    @Override
    public String getOrganizationalUnitName() {
        return organizationalUnitDropdown.getValue();
    }

    @Override
    public void setName( String name ) {
        this.nameTextBox.setText( name );
    }

    public void setNameErrorMessage( String message ) {
        nameHelpInline.setText( message );
    }

    public void clearNameErrorMessage( ) {
        nameHelpInline.setText( null );
    }

    @Override
    public void setValidOU( boolean ouValid ) {
        //not apply for this case
    }

    @Override
    public void setVisibleOU( boolean visible ) {
        organizationalUnitDropdown.setVisible( visible );
    }

    @Override
    public void initOrganizationalUnits( List<Pair<String, String>> organizationalUnits ) {

        organizationalUnitDropdown.clear();
        organizationalUnitDropdown.addItem( CoreConstants.INSTANCE.SelectEntry(), NOT_SELECTED );
        if ( organizationalUnits != null ) {
            for ( Pair<String, String> organizationalUnitInfo : organizationalUnits ) {
                organizationalUnitDropdown.addItem( organizationalUnitInfo.getK1(),
                                                    organizationalUnitInfo.getK2() );

            }
        }
    }

    @Override
    public boolean isManagedRepository() {
        return managedRepository;
    }

    @Override
    public void enabledManagedRepositoryCreation( boolean enabled ) {
        managedReposiotryGroup.setVisible( enabled );
    }

    @Override
    public void alert( String message ) {
        Window.alert( message );
    }

    private void initialiseFields() {

        nameTextBox.addKeyUpHandler( new KeyUpHandler() {
            @Override
            public void onKeyUp( KeyUpEvent event ) {
                presenter.onNameChange();
            }
        } );

        organizationalUnitDropdown.addChangeHandler( new ChangeHandler() {
            @Override
            public void onChange( ChangeEvent event ) {
                presenter.onOUChange();
            }
        } );

        isManagedRepository.addValueChangeHandler( new ValueChangeHandler<Boolean>() {
            @Override
            public void onValueChange( ValueChangeEvent<Boolean> event ) {
                managedRepository = event.getValue();
                presenter.onManagedRepositoryChange();
            }
        } );
    }

}
