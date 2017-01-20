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

package org.guvnor.m2repo.client.editor;

import java.util.Set;
import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.google.gwt.cell.client.FieldUpdater;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.RequiresResize;
import org.guvnor.common.services.shared.security.KieWorkbenchACL;
import org.guvnor.m2repo.client.resources.i18n.M2RepoEditorConstants;
import org.guvnor.m2repo.client.widgets.ArtifactListPresenter;
import org.guvnor.m2repo.client.widgets.ColumnType;
import org.guvnor.m2repo.model.JarListPageRow;
import org.guvnor.m2repo.security.MavenRepositoryPagedJarTableFeatures;
import org.gwtbootstrap3.client.ui.constants.ButtonSize;
import org.gwtbootstrap3.client.ui.gwt.ButtonCell;
import org.gwtbootstrap3.client.ui.html.Div;
import org.jboss.errai.security.shared.api.Role;
import org.jboss.errai.security.shared.api.identity.User;

@Dependent
public class MavenRepositoryPagedJarTable
        extends Composite
        implements RequiresResize {

    private ArtifactListPresenter presenter;
    private KieWorkbenchACL kieACL;

    protected User identity;

    private final Div content = new Div();

    public MavenRepositoryPagedJarTable() {
    }

    @Inject
    public MavenRepositoryPagedJarTable(final ArtifactListPresenter presenter,
                                        final KieWorkbenchACL kieACL,
                                        final User identity) {
        this.presenter = presenter;
        this.kieACL = kieACL;
        this.identity = identity;
        initWidget(content);
    }

    @PostConstruct
    public void init() {
        presenter.setup(ColumnType.NAME,
                        ColumnType.GAV,
                        ColumnType.LAST_MODIFIED);

        // Add "View KJAR's pom" button
        addViewPOMButton();

        //Add "Download JAR" button if the User has permission
        if (isUserPermittedToDownloadJARs()) {
            addDownloadJARButton();
        }

        presenter.search("");
        content.add(presenter.getView());
    }

    private boolean isUserPermittedToDownloadJARs() {
        final Set<String> grantedRoles = kieACL.getGrantedRoles(MavenRepositoryPagedJarTableFeatures.JAR_DOWNLOAD);
        if (identity.getRoles() != null) {
            for (Role role : identity.getRoles()) {
                if (grantedRoles.contains(role.getName())) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public void onResize() {
        if ((getParent().getOffsetHeight() - 148) > 0 && presenter != null) {
            presenter.getView().setContentHeight(getParent().getOffsetHeight() - 148 + "px");
        }
    }

    private String getFileDownloadURL(final String path) {
        return getGuvnorM2RepoBaseURL() + path;
    }

    private String getGuvnorM2RepoBaseURL() {
        final String baseUrl = GWT.getModuleBaseURL().replace(GWT.getModuleName() + "/",
                                                              "");
        return baseUrl + "maven2wb/";
    }

    public void search(String filter) {
        presenter.search(filter);
    }

    public void refresh() {
        presenter.refresh();
    }

    void addViewPOMButton() {
        final Column<JarListPageRow, String> openColumn = new Column<JarListPageRow, String>(new ButtonCell(ButtonSize.EXTRA_SMALL)) {
            @Override
            public String getValue(JarListPageRow row) {
                return M2RepoEditorConstants.INSTANCE.Open();
            }
        };
        openColumn.setFieldUpdater(new FieldUpdater<JarListPageRow, String>() {
            @Override
            public void update(int index,
                               JarListPageRow row,
                               String value) {
                presenter.onOpenPom(row.getPath());
            }
        });
        presenter.getView().addColumn(openColumn,
                                      M2RepoEditorConstants.INSTANCE.Open(),
                                      100.0,
                                      Style.Unit.PX);
    }

    void addDownloadJARButton() {
        final Column<JarListPageRow, String> downloadColumn = new Column<JarListPageRow, String>(new ButtonCell(ButtonSize.EXTRA_SMALL)) {
            public String getValue(JarListPageRow row) {
                return M2RepoEditorConstants.INSTANCE.Download();
            }
        };

        downloadColumn.setFieldUpdater(new FieldUpdater<JarListPageRow, String>() {
            public void update(int index,
                               JarListPageRow row,
                               String value) {
                Window.open(getFileDownloadURL(row.getPath()),
                            M2RepoEditorConstants.INSTANCE.Downloading(),
                            "resizable=no,scrollbars=yes,status=no");
            }
        });

        presenter.getView().addColumn(downloadColumn,
                                      M2RepoEditorConstants.INSTANCE.Download(),
                                      100.0,
                                      Style.Unit.PX);
    }
}
