/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/

package org.guvnor.client.screens.settings;

import javax.inject.Inject;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.RequiresResize;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import org.guvnor.client.widgets.Toggle;
import org.guvnor.shared.GeneralSettings;
import org.guvnor.shared.GeneralSettingsService;
import org.gwtbootstrap3.client.ui.TabPane;
import org.gwtbootstrap3.client.ui.TextBox;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.api.RemoteCallback;

public class GeneralTab
        extends TabPane implements IsWidget,
                                   RequiresResize {

    private GeneralSettings settings;

    interface Binder
            extends
            UiBinder<Widget, GeneralTab> {

    }

    private static Binder uiBinder = GWT.create( Binder.class );

    @UiField
    VerticalPanel base;

    @UiField
    TextBox name;
    @UiField
    Toggle enabled;
    @UiField
    Toggle socialLogin;
    @UiField
    Toggle userRegistration;
    @UiField
    Toggle resetPassword;
    @UiField
    Toggle verifyEmail;
    @UiField
    Toggle userAccountManagement;
    @UiField
    Toggle cookieLoginAllowed;
    @UiField
    Toggle requireSSL;

    @Inject
    private Caller<GeneralSettingsService> settingsService;

    public GeneralTab() {
        add( uiBinder.createAndBindUi( this ) );

        setUpValueChangeHandlers();
    }

    private void setUpValueChangeHandlers() {
        enabled.addValueChangeHandler( new ValueChangeHandler<Boolean>() {
            @Override
            public void onValueChange( ValueChangeEvent<Boolean> event ) {
                settings.setEnabled( event.getValue() );
            }
        } );
        socialLogin.addValueChangeHandler( new ValueChangeHandler<Boolean>() {
            @Override
            public void onValueChange( ValueChangeEvent<Boolean> event ) {
                settings.setSocialLogin( event.getValue() );
            }
        } );
        requireSSL.addValueChangeHandler( new ValueChangeHandler<Boolean>() {
            @Override
            public void onValueChange( ValueChangeEvent<Boolean> event ) {
                settings.setRequireSSL( event.getValue() );
            }
        } );
        userRegistration.addValueChangeHandler( new ValueChangeHandler<Boolean>() {
            @Override
            public void onValueChange( ValueChangeEvent<Boolean> event ) {
                settings.setUserRegistration( event.getValue() );
            }
        } );
        resetPassword.addValueChangeHandler( new ValueChangeHandler<Boolean>() {
            @Override
            public void onValueChange( ValueChangeEvent<Boolean> event ) {
                settings.setResetPassword( event.getValue() );
            }
        } );
        verifyEmail.addValueChangeHandler( new ValueChangeHandler<Boolean>() {
            @Override
            public void onValueChange( ValueChangeEvent<Boolean> event ) {
                settings.setVerifyEmail( event.getValue() );
            }
        } );
        userAccountManagement.addValueChangeHandler( new ValueChangeHandler<Boolean>() {
            @Override
            public void onValueChange( ValueChangeEvent<Boolean> event ) {
                settings.setUserAccountManagement( event.getValue() );
            }
        } );
        cookieLoginAllowed.addValueChangeHandler( new ValueChangeHandler<Boolean>() {
            @Override
            public void onValueChange( ValueChangeEvent<Boolean> event ) {
                settings.setCookieLoginAllowed( event.getValue() );
            }
        } );
    }

    public void load() {
        settingsService.call( new RemoteCallback<GeneralSettings>() {
            @Override
            public void callback( GeneralSettings settings ) {

                GeneralTab.this.settings = settings;

                name.setText( settings.getName() );
                enabled.setValue( settings.isEnabled() );
                socialLogin.setValue( settings.isSocialLogin() );
                requireSSL.setValue( settings.isRequireSSL() );
                userRegistration.setValue( settings.isUserRegistration() );
                resetPassword.setValue( settings.isResetPassword() );
                verifyEmail.setValue( settings.isVerifyEmail() );
                userAccountManagement.setValue( settings.isUserAccountManagement() );
                cookieLoginAllowed.setValue( settings.isCookieLoginAllowed() );
            }
        } ).load();
    }

    @Override
    public void onResize() {
        int height = asWidget().getParent().getOffsetHeight();
        int width = asWidget().getParent().getOffsetWidth();
        base.setPixelSize( width, height );
    }

    public String getHeading() {
        return "General";
    }

}
