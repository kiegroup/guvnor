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

package org.guvnor.ala.ui.client.provider.status;

import java.util.ArrayList;
import java.util.List;

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.guvnor.ala.ui.client.provider.status.runtime.RuntimePresenter;
import org.guvnor.ala.ui.model.RuntimeListItem;
import org.jboss.errai.ioc.client.api.ManagedInstance;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;

import static org.mockito.Mockito.*;

@RunWith(GwtMockitoTestRunner.class)
public class ProviderStatusPresenterTest {

    private static int ITEMS_COUNT = 5;

    @Mock
    private ProviderStatusPresenter.View view;

    @Mock
    private ManagedInstance<RuntimePresenter> runtimePresenterInstance;

    private ProviderStatusPresenter presenter;

    private List<RuntimePresenter> runtimePresenters;

    @Before
    public void setUp() {
        runtimePresenters = new ArrayList<>();
        presenter = new ProviderStatusPresenter(view,
                                                runtimePresenterInstance) {
            @Override
            protected RuntimePresenter newRuntimePresenter() {
                RuntimePresenter runtimePresenter = mock(RuntimePresenter.class);
                RuntimePresenter.View view = mock(RuntimePresenter.View.class);
                when(runtimePresenter.getView()).thenReturn(view);
                runtimePresenters.add(runtimePresenter);
                when(runtimePresenterInstance.get()).thenReturn(runtimePresenter);
                return super.newRuntimePresenter();
            }
        };
        presenter.init();
        verify(view,
               times(1)).init(presenter);
    }

    @Test
    public void testSetupItems() {
        List<RuntimeListItem> items = mockItems(ITEMS_COUNT);
        presenter.setupItems(items);

        verify(runtimePresenterInstance,
               times(ITEMS_COUNT)).get();
        verify(view,
               times(ITEMS_COUNT)).addListItem(any());

        for (int i = 0; i < ITEMS_COUNT; i++) {
            verify(runtimePresenters.get(i),
                   times(1)).setup(items.get(i));
            verify(view,
                   times(1)).addListItem(runtimePresenters.get(i).getView());
        }
    }

    @Test
    public void testClean() {
        List<RuntimeListItem> items = mockItems(ITEMS_COUNT);
        presenter.setupItems(items);

        presenter.clear();
        verify(view,
               times(2)).clear();
        runtimePresenters.forEach(runtimePresenter -> verify(runtimePresenterInstance,
                                                             times(1)).destroy(runtimePresenter));
        verify(runtimePresenterInstance,
               times(ITEMS_COUNT)).destroy(any());
    }

    private List<RuntimeListItem> mockItems(int count) {
        List<RuntimeListItem> items = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            items.add(mock(RuntimeListItem.class));
        }
        return items;
    }
}
