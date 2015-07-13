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

import java.util.Collection;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;
import org.guvnor.structure.organizationalunit.OrganizationalUnit;
import org.gwtbootstrap3.client.ui.CheckBox;
import org.gwtbootstrap3.client.ui.FormGroup;
import org.gwtbootstrap3.client.ui.HelpBlock;
import org.gwtbootstrap3.client.ui.TextBox;
import org.gwtbootstrap3.extras.select.client.ui.Option;
import org.gwtbootstrap3.extras.select.client.ui.Select;
import org.uberfire.ext.widgets.core.client.resources.i18n.CoreConstants;

public class RepositoryInfoPageViewImpl extends Composite
        implements RepositoryInfoPageView {

    private Presenter presenter;

    interface RepositoryInfoPageBinder extends UiBinder<Widget, RepositoryInfoPageViewImpl> {

    }

    @UiField
    FormGroup organizationalUnitGroup;

    @UiField
    HelpBlock organizationalUnitHelpBlock;

    @UiField
    Select organizationalUnitDropdown;

    @UiField
    FormGroup nameGroup;

    @UiField
    TextBox nameTextBox;

    @UiField
    HelpBlock nameHelpBlock;

    @UiField
    FormGroup managedReposiotryGroup;

    @UiField
    CheckBox isManagedRepository;

    private String name;

    private String organizationalUnitName;

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
        return name;
    }

    @Override
    public String getOrganizationalUnitName() {
        return organizationalUnitName;
    }

    @Override
    public void setName( String name ) {
        this.name = name;
        this.nameTextBox.setText( name );
    }

    @Override
    public void setValidName( boolean isValid ) {
        //TODO enable disable error messages.
    }

    @Override
    public void setValidOU( boolean ouValid ) {
        //TODO enable disable error messages.
    }

    @Override
    public void setVisibleOU( boolean visible ) {
        organizationalUnitDropdown.setVisible( visible );
    }

    @Override
    public void initOrganizationalUnits( Collection<OrganizationalUnit> organizationalUnits ) {

        final Option select = new Option();
        select.setText( CoreConstants.INSTANCE.SelectEntry() );
        select.setValue( NOT_SELECTED );
        organizationalUnitDropdown.add( select );
        if ( organizationalUnits != null && !organizationalUnits.isEmpty() ) {
            for ( OrganizationalUnit organizationalUnit : organizationalUnits ) {
                final Option option = new Option();
                option.setText( organizationalUnit.getName() );
                option.setValue( organizationalUnit.getName() );
                organizationalUnitDropdown.add( option );

            }
        }
        organizationalUnitDropdown.refresh();
    }

    @Override
    public boolean isManagedRepository() {
        return managedRepository;
    }

    @Override
    public void enabledManagedRepositoryCreation( boolean enabled ) {
        managedReposiotryGroup.setVisible( enabled );
    }

    private void initialiseFields() {
        nameTextBox.addKeyUpHandler( new KeyUpHandler() {
            @Override
            public void onKeyUp( KeyUpEvent event ) {
                name = nameTextBox.getText();
                presenter.stateChanged();
            }
        } );

        organizationalUnitDropdown.addChangeHandler( new ChangeHandler() {
            @Override
            public void onChange( ChangeEvent event ) {
                organizationalUnitName = organizationalUnitDropdown.getValue();
                presenter.stateChanged();
            }
        } );

        isManagedRepository.addValueChangeHandler( new ValueChangeHandler<Boolean>() {
            @Override
            public void onValueChange( ValueChangeEvent<Boolean> event ) {
                managedRepository = event.getValue();
                presenter.stateChanged();
            }
        } );
    }

}
