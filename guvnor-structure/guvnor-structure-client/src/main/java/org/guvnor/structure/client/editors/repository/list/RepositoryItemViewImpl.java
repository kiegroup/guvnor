/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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

package org.guvnor.structure.client.editors.repository.list;

import javax.inject.Inject;

import com.github.gwtbootstrap.client.ui.Button;
import com.github.gwtbootstrap.client.ui.ListBox;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.InlineHTML;
import com.google.gwt.user.client.ui.Widget;
import org.uberfire.ext.widgets.core.client.resources.i18n.CoreConstants;

public class RepositoryItemViewImpl
        extends Composite
        implements RepositoryItemView {

    private static RepositoriesViewItemBinder uiBinder = GWT.create( RepositoriesViewItemBinder.class );

    @UiField
    public InlineHTML repoName;

    @UiField
    public InlineHTML repoDesc;

    @UiField
    public InlineHTML gitDaemonURI;

    @UiField
    public Button myGitCopyButton;

    @UiField
    public FlowPanel linksPanel;

    @UiField
    public ListBox branchesDropdown;

    private RepositoryItemPresenter presenter;


    @Inject
    public RepositoryItemViewImpl() {
        initWidget( uiBinder.createAndBindUi( this ) );
        glueCopy( myGitCopyButton.getElement() );
    }

    public static native void glueCopy( final com.google.gwt.user.client.Element element ) /*-{
        var clip = new $wnd.ZeroClipboard(element);
    }-*/;

    @Override
    public void setRepositoryName( final String repositoryName ) {
        repoName.setText( repositoryName );
    }

    @Override
    public void setRepositoryDescription( final String description ) {
        repoDesc.setText( description );
    }

    @Override
    public void showAvailableProtocols() {
        linksPanel.add( new InlineHTML() {{
                setText( CoreConstants.INSTANCE.AvailableProtocols() );
                getElement().getStyle().setPaddingLeft( 10, Style.Unit.PX );
            }} );
    }

    @Override
    public void setDaemonURI( final String uri ) {
        gitDaemonURI.setText( uri );
    }

    @Override
    public void addProtocol( final String protocol ) {
        linksPanel.add( new ProtocolButton( protocol,
                                            new ClickHandler() {
                                                @Override
                                                public void onClick( ClickEvent event ) {
                                                    presenter.onAnchorSelected( protocol );
                                                }
                                            },
                                            linksPanel.getWidgetCount() != 0 ) );
    }

    @Override
    public void setPresenter( final RepositoryItemPresenter presenter ) {
        this.presenter = presenter;
    }

    @Override
    public void setUriId( final String uriId ) {

        gitDaemonURI.getElement().setId( uriId );
        myGitCopyButton.getElement().setAttribute( "data-clipboard-target", uriId );
        myGitCopyButton.getElement().setAttribute( "data-clipboard-text", gitDaemonURI.getText() );

        myGitCopyButton.getElement().setId( "view-button-" + uriId );
    }

    @Override
    public void addBranch( final String branch ) {
        branchesDropdown.addItem( branch, branch );
    }

    @Override
    public void setSelectedBranch( final String currentBranch ) {
        branchesDropdown.setSelectedValue( currentBranch );
    }

    @UiHandler( "btnRemoveRepository" )
    public void onRemoveRepository( final ClickEvent event ) {
        presenter.onClickButtonRemoveRepository();
    }

    @UiHandler( "btnChangeBranch" )
    public void onUpdateRepository( final ClickEvent event ) {
        presenter.onUpdateRepository( branchesDropdown.getValue() );
    }

    interface RepositoriesViewItemBinder
            extends
            UiBinder<Widget, RepositoryItemViewImpl> {

    }

}
