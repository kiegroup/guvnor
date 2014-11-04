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
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.InlineHTML;
import com.google.gwt.user.client.ui.Widget;
import org.guvnor.structure.organizationalunit.OrganizationalUnit;
import org.kie.uberfire.client.resources.i18n.CoreConstants;

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

        organizationalUnitDropdown.addItem( CoreConstants.INSTANCE.SelectEntry(), NOT_SELECTED );
        if ( organizationalUnits != null && !organizationalUnits.isEmpty() ) {
            for ( OrganizationalUnit organizationalUnit : organizationalUnits ) {
                organizationalUnitDropdown.addItem( organizationalUnit.getName(),
                        organizationalUnit.getName() );

            }
        }
    }

    @Override
    public boolean isManagedRepository() {
        return managedRepository;
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
