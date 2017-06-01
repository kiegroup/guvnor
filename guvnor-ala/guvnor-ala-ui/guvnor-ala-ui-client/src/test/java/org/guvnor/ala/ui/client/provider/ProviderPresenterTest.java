/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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

package org.guvnor.ala.ui.client.provider;

import java.util.Collection;

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.guvnor.ala.ui.client.events.AddNewRuntimeEvent;
import org.guvnor.ala.ui.client.events.ProviderSelectedEvent;
import org.guvnor.ala.ui.client.events.ProviderTypeSelectedEvent;
import org.guvnor.ala.ui.client.events.RefreshRuntimeEvent;
import org.guvnor.ala.ui.client.handler.ClientProviderHandler;
import org.guvnor.ala.ui.client.handler.ClientProviderHandlerRegistry;
import org.guvnor.ala.ui.client.handler.FormResolver;
import org.guvnor.ala.ui.client.handler.ProviderConfigurationForm;
import org.guvnor.ala.ui.client.provider.status.ProviderStatusPresenter;
import org.guvnor.ala.ui.client.provider.status.empty.ProviderStatusEmptyPresenter;
import org.guvnor.ala.ui.client.wizard.provider.empty.ProviderConfigEmptyPresenter;
import org.guvnor.ala.ui.model.Provider;
import org.guvnor.ala.ui.model.ProviderConfiguration;
import org.guvnor.ala.ui.model.ProviderKey;
import org.guvnor.ala.ui.model.ProviderTypeKey;
import org.guvnor.ala.ui.model.RuntimeListItem;
import org.guvnor.ala.ui.model.RuntimesInfo;
import org.guvnor.ala.ui.service.ProviderService;
import org.guvnor.ala.ui.service.RuntimeService;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.api.IsElement;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.uberfire.mocks.CallerMock;
import org.uberfire.mocks.EventSourceMock;
import org.uberfire.mvp.Command;
import org.uberfire.workbench.events.NotificationEvent;

import static org.guvnor.ala.ui.ProvisioningManagementTestCommons.ERROR_MESSAGE;
import static org.guvnor.ala.ui.ProvisioningManagementTestCommons.SUCCESS_MESSAGE;
import static org.guvnor.ala.ui.ProvisioningManagementTestCommons.mockProviderKey;
import static org.guvnor.ala.ui.ProvisioningManagementTestCommons.mockProviderTypeKey;
import static org.guvnor.ala.ui.ProvisioningManagementTestCommons.prepareServiceCallerError;
import static org.mockito.Mockito.*;

@RunWith(GwtMockitoTestRunner.class)
public class ProviderPresenterTest {

    @Mock
    private ProviderPresenter.View view;

    @Mock
    private ProviderService providerService;

    private Caller<ProviderService> providerServiceCaller;

    @Mock
    private RuntimeService runtimeService;

    private Caller<RuntimeService> runtimeServiceCaller;

    @Mock
    private ProviderStatusEmptyPresenter providerStatusEmptyPresenter;

    @Mock
    private ProviderStatusEmptyPresenter.View providerStatusEmptyPresenterView;

    @Mock
    private ProviderStatusPresenter providerStatusPresenter;

    @Mock
    private ProviderStatusPresenter.View providerStatusPresenterView;

    @Mock
    private ProviderConfigEmptyPresenter providerConfigEmptyPresenter;

    @Mock
    private ClientProviderHandlerRegistry handlerRegistry;

    @Mock
    private ClientProviderHandler handler;

    @Mock
    private EventSourceMock<ProviderTypeSelectedEvent> providerTypeSelectedEvent;

    @Mock
    private EventSourceMock<NotificationEvent> notification;

    @Mock
    private EventSourceMock<AddNewRuntimeEvent> addNewRuntimeEvent;

    @Mock
    private FormResolver formResolver;

    @Mock
    private ProviderConfigurationForm configurationForm;

    @Mock
    private IsElement configurationFormView;

    private ProviderPresenter presenter;

    private Provider provider;

    private ProviderKey providerKey;

    private RuntimesInfo runtimesInfo;

    @SuppressWarnings("unchecked")
    private Collection<RuntimeListItem> runtimeItems = mock(Collection.class);

    @Before
    public void setUp() {

        when(providerStatusPresenter.getView()).thenReturn(providerStatusPresenterView);
        when(providerStatusEmptyPresenter.getView()).thenReturn(providerStatusEmptyPresenterView);

        providerServiceCaller = spy(new CallerMock<>(providerService));
        runtimeServiceCaller = spy(new CallerMock<>(runtimeService));
        presenter = new ProviderPresenter(view,
                                          providerServiceCaller,
                                          runtimeServiceCaller,
                                          providerStatusEmptyPresenter,
                                          providerStatusPresenter,
                                          providerConfigEmptyPresenter,
                                          handlerRegistry,
                                          notification,
                                          providerTypeSelectedEvent,
                                          addNewRuntimeEvent);
        presenter.init();
        verify(view,
               times(1)).init(presenter);
    }

    @Test
    public void testOnProviderSelectedWithRuntimes() {

        prepareRuntimesInfo();

        //emulate that there are runtimes.
        when(runtimeItems.isEmpty()).thenReturn(false);

        presenter.onProviderSelected(new ProviderSelectedEvent(providerKey));

        //the ui must have been loaded with the items.
        verifyUILoadedWithItems(1);

        //common verifications.
        verifyRuntimesInfoLoaded(1);
    }

    @Test
    public void testOnProviderSelectedWithNoRuntimes() {

        prepareRuntimesInfo();

        //emulate that there are no runtimes.
        when(runtimeItems.isEmpty()).thenReturn(true);

        presenter.onProviderSelected(new ProviderSelectedEvent(providerKey));

        verifyUILoadedWithNoItems(1);

        //common verifications.
        verifyRuntimesInfoLoaded(1);
    }

    @Test
    public void testOnRefreshRuntimesWithItems() {
        //emulate that the provider was already loaded.
        prepareRefreshWithItems();

        //emulate the refresh action in a later time.
        presenter.onRefreshRuntime(new RefreshRuntimeEvent(providerKey));

        //the ui must have been loaded with the items twice, the initial loading + the refresh
        verify(providerStatusPresenter,
               times(2)).setupItems(runtimeItems);
        verify(view,
               times(2)).setStatus(providerStatusPresenterView);

        //common verifications.
        verifyRuntimesInfoLoaded(2);
    }

    @Test
    public void testOnRefreshRuntimesWithNoItems() {
        //emulate that the provider was already loaded.
        prepareRefreshWithNoItems();

        //emulate the refresh action in a later time.
        presenter.onRefreshRuntime(new RefreshRuntimeEvent(providerKey));

        //the ui must have been loaded with the items twice, the initial loading + the refresh
        //the empty status must have been set twice.
        verifyUILoadedWithNoItems(2);

        //common verifications.
        verifyRuntimesInfoLoaded(2);
    }

    @Test
    public void testRefreshWithItems() {
        //emulate that the provider was already loaded.
        prepareRefreshWithItems();

        //emulate the refresh action in a later time.
        presenter.refresh();

        //the ui must have been loaded with the items twice, the initial loading + the refresh
        verify(providerStatusPresenter,
               times(2)).setupItems(runtimeItems);
        verify(view,
               times(2)).setStatus(providerStatusPresenterView);

        //common verifications.
        verifyRuntimesInfoLoaded(2);
    }

    @Test
    public void testRefreshWithNoItems() {
        //emulate that the provider was already loaded.
        prepareRefreshWithNoItems();

        //emulate the refresh action in a later time.
        presenter.refresh();

        //the ui must have been loaded with the items twice, the initial loading + the refresh
        //the empty status must have been set twice.
        verifyUILoadedWithNoItems(2);

        //common verifications.
        verifyRuntimesInfoLoaded(2);
    }

    @Test
    public void testOnRemoveProvider() {
        presenter.onRemoveProvider();
        verify(view,
               times(1)).confirmRemove(any(Command.class));
    }

    @Test
    public void testRemoveProviderSuccessFull() {
        testRemoveProvider(false);
    }

    @Test
    public void testRemoveProviderWithError() {
        testRemoveProvider(true);
    }

    private void testRemoveProvider(boolean withErrors) {
        //emulate that the provider was previously loaded.
        prepareRuntimesInfo();
        presenter.onProviderSelected(new ProviderSelectedEvent(providerKey));

        when(view.getRemoveProviderSuccessMessage()).thenReturn(SUCCESS_MESSAGE);
        when(view.getRemoveProviderErrorMessage()).thenReturn(ERROR_MESSAGE);

        if (withErrors) {
            //make the providerService fail.
            prepareServiceCallerError(providerService,
                                      providerServiceCaller);
        }

        //the user confirms the delete operation at a later time from the ui.
        presenter.removeProvider();

        verify(providerService,
               times(1)).deleteProvider(providerKey);
        verify(providerTypeSelectedEvent,
               times(1)).fire(new ProviderTypeSelectedEvent(providerKey.getProviderTypeKey()));

        if (withErrors) {
            verify(notification,
                   times(1)).fire(new NotificationEvent(ERROR_MESSAGE,
                                                        NotificationEvent.NotificationType.ERROR));
        } else {
            verify(notification,
                   times(1)).fire(new NotificationEvent(SUCCESS_MESSAGE,
                                                        NotificationEvent.NotificationType.SUCCESS));
        }
    }

    @Test
    public void testDeploy() {
        //emulate that the provider was previously loaded.
        prepareRuntimesInfo();
        presenter.onProviderSelected(new ProviderSelectedEvent(providerKey));

        //emulate the user adding a new deploy.
        presenter.deploy();

        verify(addNewRuntimeEvent,
               times(1)).fire(new AddNewRuntimeEvent(provider));
    }

    private void prepareRuntimesInfo() {
        ProviderTypeKey providerTypeKey = mockProviderTypeKey("1");
        providerKey = mockProviderKey(providerTypeKey,
                                      "1");
        ProviderConfiguration configuration = mock(ProviderConfiguration.class);
        provider = new Provider(providerKey,
                                configuration);
        runtimesInfo = mock(RuntimesInfo.class);
        when(runtimesInfo.getProvider()).thenReturn(provider);
        when(runtimesInfo.getRuntimeItems()).thenReturn(runtimeItems);
        when(providerService.getProvider(providerKey)).thenReturn(provider);
        when(runtimeService.getRuntimesInfo(providerKey)).thenReturn(runtimesInfo);

        when(handlerRegistry.isProviderEnabled(providerTypeKey)).thenReturn(true);
        when(handlerRegistry.getProviderHandler(providerTypeKey)).thenReturn(handler);
        when(handler.getFormResolver()).thenReturn(formResolver);
        when(formResolver.newProviderConfigurationForm()).thenReturn(configurationForm);
        when(configurationForm.getView()).thenReturn(configurationFormView);
    }

    private void verifyRuntimesInfoLoaded(int currentTimes) {
        verify(runtimeService,
               times(currentTimes)).getRuntimesInfo(providerKey);

        verify(providerStatusPresenter,
               times(currentTimes)).clear();

        verify(view,
               times(currentTimes)).setProviderName(provider.getKey().getId());

        verify(handlerRegistry,
               times(currentTimes)).getProviderHandler(providerKey.getProviderTypeKey());
        verify(handler,
               times(currentTimes)).getFormResolver();
        verify(formResolver,
               times(currentTimes)).newProviderConfigurationForm();
        verify(configurationForm,
               times(currentTimes)).load(provider);
        verify(configurationForm,
               times(currentTimes)).disable();
        verify(view,
               times(currentTimes)).setConfig(configurationForm.getView());
    }

    private void verifyUILoadedWithItems(int currentTimes) {
        //the ui must have been loaded with the items.
        verify(providerStatusPresenter,
               times(currentTimes)).setupItems(runtimeItems);
        verify(view,
               times(currentTimes)).setStatus(providerStatusPresenterView);
    }

    private void verifyUILoadedWithNoItems(int currentItems) {
        //the empty status must have been set.
        verify(providerStatusEmptyPresenter,
               times(currentItems)).setup(providerKey);
        verify(view,
               times(currentItems)).setStatus(providerStatusEmptyPresenterView);
    }

    /**
     * Prepares the refresh action. The refresh action only takes place if the provider has been loaded first.
     */
    private void prepareRefreshWithItems() {
        prepareRuntimesInfo();

        //emulate that there are runtimes.
        when(runtimeItems.isEmpty()).thenReturn(false);

        //emulate that the presenter was initially loaded. If not the refresh button is not available.
        presenter.onProviderSelected(new ProviderSelectedEvent(providerKey));
    }

    /**
     * Prepares the refresh action. The refresh action only takes place if the provider has been loaded first.
     */
    private void prepareRefreshWithNoItems() {
        prepareRuntimesInfo();

        //emulate that there are no runtimes.
        when(runtimeItems.isEmpty()).thenReturn(true);

        //emulate that the presenter was initially loaded. If not the refresh button is not available.
        presenter.onProviderSelected(new ProviderSelectedEvent(providerKey));
    }
}
