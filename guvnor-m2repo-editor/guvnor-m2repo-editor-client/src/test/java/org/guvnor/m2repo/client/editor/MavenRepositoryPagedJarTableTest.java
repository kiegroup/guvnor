/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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

package org.guvnor.m2repo.client.editor;

import java.util.HashSet;
import java.util.Set;

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.guvnor.common.services.shared.security.KieWorkbenchACL;
import org.guvnor.m2repo.client.widgets.ArtifactListPresenter;
import org.guvnor.m2repo.client.widgets.ArtifactListView;
import org.guvnor.m2repo.security.MavenRepositoryPagedJarTableFeatures;
import org.jboss.errai.security.shared.api.Role;
import org.jboss.errai.security.shared.api.RoleImpl;
import org.jboss.errai.security.shared.api.identity.User;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Matchers;
import org.mockito.Mock;

import static org.mockito.Mockito.*;

@RunWith(GwtMockitoTestRunner.class)
public class MavenRepositoryPagedJarTableTest {

    @Mock
    private ArtifactListPresenter presenter;

    @Mock
    private ArtifactListView view;

    @Mock
    private KieWorkbenchACL kieACL;

    @Mock
    private User identity;

    private MavenRepositoryPagedJarTable table;

    private final Set<String> requiredRoles = new HashSet<String>() {{
        add("admin");
    }};

    @Before
    public void setup() {
        when(presenter.getView()).thenReturn(view);
        when(kieACL.getGrantedRoles(Matchers.eq(MavenRepositoryPagedJarTableFeatures.JAR_DOWNLOAD))).thenReturn(requiredRoles);

        final MavenRepositoryPagedJarTable wrapped = new MavenRepositoryPagedJarTable(presenter,
                                                                                      kieACL,
                                                                                      identity);
        table = spy(wrapped);
    }

    @Test
    public void downloadJARButtonIncludedWhenUserHasPermission() {
        when(identity.getRoles()).thenReturn(new HashSet<Role>() {{
            add(new RoleImpl("admin"));
        }});

        table.init();

        verify(table,
               times(1)).addDownloadJARButton();
    }

    @Test
    public void downloadJARButtonExcludedWhenUserLacksPermission() {
        when(identity.getRoles()).thenReturn(new HashSet<Role>() {{
            add(new RoleImpl("analyst"));
        }});

        table.init();

        verify(table,
               never()).addDownloadJARButton();
    }
}