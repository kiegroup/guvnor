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
import java.util.HashMap;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.inject.Inject;

import com.google.gwt.user.client.Window;
import org.guvnor.asset.management.client.editors.repository.wizard.CreateRepositoryWizardModel;
import org.guvnor.asset.management.client.i18n.Constants;
import org.guvnor.structure.client.editors.repository.RepositoryPreferences;
import org.guvnor.structure.client.resources.i18n.CommonConstants;
import org.guvnor.structure.organizationalunit.OrganizationalUnit;
import org.guvnor.structure.organizationalunit.OrganizationalUnitService;
import org.jboss.errai.bus.client.api.messaging.Message;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.api.ErrorCallback;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.jboss.errai.ioc.client.container.IOC;
import org.jboss.errai.ioc.client.container.IOCBeanDef;
import org.jboss.errai.ioc.client.container.IOCResolutionException;
import org.kie.uberfire.client.resources.i18n.CoreConstants;
import org.uberfire.client.callbacks.Callback;

public class RepositoryInfoPage extends RepositoryWizardPage
        implements
        RepositoryInfoPageView.Presenter {

    public interface RepositoryInfoPageHandler {

        void managedRepositoryStatusChanged( boolean status );

    }

    @Inject
    private RepositoryInfoPageView view;

    @Inject
    private Caller<OrganizationalUnitService> organizationalUnitService;

    private boolean isNameValid = false;

    private boolean isOUValid = false;

    private Map<String, OrganizationalUnit> availableOrganizationalUnits = new HashMap<String, OrganizationalUnit>();
    private boolean mandatoryOU = true;

    private boolean isManagedRepository = false;

    private RepositoryInfoPageHandler handler;

    @Override
    public String getTitle() {
        return Constants.INSTANCE.RepositoryInfoPage();
    }

    @Override
    public void isComplete( final Callback<Boolean> callback ) {
        callback.callback( mandatoryOU ? isNameValid && isOUValid : isNameValid );
    }

    @Override
    public void initialise() {
        view.init( this );
        content.setWidget( view );
    }

    @Override
    public void prepareView() {
        init();
    }

    @Override
    public String getName() {
        return view.getName();
    }

    public void setHandler( RepositoryInfoPageHandler handler ) {
        this.handler = handler;
    }

    @Override
    public void stateChanged() {

        //TODO add the correct validation for the repository name
        isNameValid = view.getName() != null && !"error".equals( view.getName() );
        view.setValidName( isNameValid );
        model.setRepositoryName( view.getName() != null ? view.getName().trim() : null );

        isOUValid = view.getOrganizationalUnitName() != null && !RepositoryInfoPageView.NOT_SELECTED.equals( view.getOrganizationalUnitName() );
        if ( mandatoryOU ) {
            view.setValidOU( isOUValid );
        }
        model.setOrganizationalUnit( view.getOrganizationalUnitName() != null ? availableOrganizationalUnits.get( view.getOrganizationalUnitName() ) : null );

        model.setManged( view.isManagedRepository() );

        if ( handler != null && ( isManagedRepository != view.isManagedRepository() ) ) {
            isManagedRepository = view.isManagedRepository();
            handler.managedRepositoryStatusChanged( isManagedRepository );
        }

        fireEvent();
    }

    @PostConstruct
    private void init() {

        view.init( this );
        content.setWidget( view );

        mandatoryOU = isOUMandatory();

        if ( !mandatoryOU ) {
            view.setVisibleOU( false );
        }

        //populate Organizational Units list box
        organizationalUnitService.call( new RemoteCallback<Collection<OrganizationalUnit>>() {

                                            @Override
                                            public void callback( Collection<OrganizationalUnit> organizationalUnits ) {
                                                if ( organizationalUnits != null && !organizationalUnits.isEmpty() ) {
                                                    for ( OrganizationalUnit organizationalUnit : organizationalUnits ) {
                                                        availableOrganizationalUnits.put( organizationalUnit.getName(),
                                                                organizationalUnit );
                                                    }
                                                    view.initOrganizationalUnits( organizationalUnits );
                                                }
                                            }
                                        },
                new ErrorCallback<Message>() {
                    @Override
                    public boolean error( final Message message,
                            final Throwable throwable ) {
                        Window.alert( CoreConstants.INSTANCE.CantLoadOrganizationalUnits() + " \n" + message.toString() );

                        return false;
                    }
                }
        ).getOrganizationalUnits();
    }

    @Override
    public void setModel( CreateRepositoryWizardModel model ) {
        super.setModel( model );
        model.setMandatoryOU( mandatoryOU );
        model.setManged( view.isManagedRepository() );
    }

    private boolean isOUMandatory() {
        try {
            final IOCBeanDef<RepositoryPreferences> beanDef = IOC.getBeanManager().lookupBean( RepositoryPreferences.class );
            return beanDef == null || beanDef.getInstance().isOUMandatory();
        } catch ( IOCResolutionException exception ) {
        }
        return true;
    }

}