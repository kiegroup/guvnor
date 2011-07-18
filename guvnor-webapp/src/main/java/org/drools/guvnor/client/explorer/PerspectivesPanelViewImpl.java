/*
 * Copyright 2011 JBoss Inc
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 */

package org.drools.guvnor.client.explorer;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.SpanElement;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.*;
import org.drools.guvnor.client.common.ErrorPopup;
import org.drools.guvnor.client.messages.Constants;
import org.drools.guvnor.client.util.TabbedPanel;

public class PerspectivesPanelViewImpl extends Composite
        implements
        PerspectivesPanelView {

    interface PerspectivesPanelViewImplBinder
            extends
            UiBinder<Widget, PerspectivesPanelViewImpl> {
    }

    private static PerspectivesPanelViewImplBinder uiBinder = GWT.create( PerspectivesPanelViewImplBinder.class );

    private static Constants constants = GWT.create( Constants.class );

    private Presenter presenter;

    @UiField
    ListBox perspectives;

    @UiField
    SpanElement userName;

    @UiField
    HTMLPanel titlePanel;

    @UiField(provided = true)
    Widget navigationPanel;

    @UiField(provided = true)
    ExplorerViewCenterPanel explorerCenterPanel;

    public PerspectivesPanelViewImpl( IsWidget navigationPanel,
                                      ExplorerViewCenterPanel explorerCenterPanel,
                                      boolean showTitle ) {
        this.navigationPanel = navigationPanel.asWidget();
        this.explorerCenterPanel = explorerCenterPanel;

        showTitle( showTitle );

        initWidget( uiBinder.createAndBindUi( this ) );

        titlePanel.setVisible( showTitle );
    }

    private void showTitle( boolean showTitle ) {
        if ( showTitle ) {
            TitlePanelHeight.show();
        } else {
            TitlePanelHeight.hide();
        }
    }

    public void setUserName( String userName ) {
        this.userName.setInnerText( userName );
    }

    public TabbedPanel getTabbedPanel(){
        return explorerCenterPanel;
    }

    public void addPerspectiveToList( String perspectiveId,
                                      String perspectiveName ) {
        perspectives.addItem( perspectiveName,
                perspectiveId );
    }

    public void setPresenter( Presenter presenter ) {
        this.presenter = presenter;
    }

    @UiHandler("perspectives")
    public void handleChange( ChangeEvent event ) {
        String perspectiveId = perspectives.getValue( perspectives.getSelectedIndex() );
        try {
            presenter.onPerspectiveChange( perspectiveId );
        } catch (UnknownPerspective unknownPerspective) {
            ErrorPopup.showMessage( constants.FailedToLoadPerspectiveUnknownId0( perspectiveId ) );
        }
    }

    public static class TitlePanelHeight {

        private static final int DEFAULT_HEIGHT = 4;
        private static int height = DEFAULT_HEIGHT;

        public int getHeight() {
            return height;
        }

        public static void show() {
            height = DEFAULT_HEIGHT;
        }

        public static void hide() {
            height = 0;
        }
    }
}
