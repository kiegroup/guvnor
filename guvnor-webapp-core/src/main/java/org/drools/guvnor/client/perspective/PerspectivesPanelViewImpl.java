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

package org.drools.guvnor.client.perspective;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.SpanElement;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.Widget;
import org.drools.guvnor.client.explorer.ClientFactory;
import org.drools.guvnor.client.explorer.ExplorerViewCenterPanel;
import org.drools.guvnor.client.explorer.navigation.NavigationPanel;
import org.drools.guvnor.client.messages.ConstantsCore;
import org.drools.guvnor.client.util.TabbedPanel;

public class PerspectivesPanelViewImpl extends Composite
        implements
        PerspectivesPanelView {

    interface PerspectivesPanelViewImplBinder
            extends
            UiBinder<Widget, PerspectivesPanelViewImpl> {

    }

    private static PerspectivesPanelViewImplBinder uiBinder = GWT.create(PerspectivesPanelViewImplBinder.class);

    private static ConstantsCore constants = GWT.create(ConstantsCore.class);

    private Presenter presenter;

    @UiField
    ListBox perspectives;

    @UiField
    SpanElement userName;

    @UiField
    HTMLPanel titlePanel;

    @UiField(provided = true)
    NavigationPanel navigationPanel;

    @UiField(provided = true)
    ExplorerViewCenterPanel explorerCenterPanel;

    @UiField
    Anchor logoutAnchor;

    public PerspectivesPanelViewImpl(ClientFactory clientFactory, EventBus eventBus) {
//        this.navigationPanel = new NavigationPanel(clientFactory, eventBus);

        this.explorerCenterPanel = new ExplorerViewCenterPanel(clientFactory, eventBus);

        showTitle(canShowTitle());

        initWidget(uiBinder.createAndBindUi(this));

        titlePanel.setVisible(canShowTitle());
    }

    private boolean canShowTitle() {
        String parameter = Window.Location.getParameter("nochrome");

        if (parameter == null) {
            return true;
        } else {
            return parameter.equals("true");
        }
    }

    private void showTitle(boolean showTitle) {
        if (showTitle) {
            TitlePanelHeight.show();
        } else {
            TitlePanelHeight.hide();
        }
    }

    public void setUserName(String userName) {
        this.userName.setInnerText(userName);
    }

    public void addPerspective(String item, String value) {
        perspectives.addItem(item, value);
    }
    public TabbedPanel getTabbedPanel() {
        return explorerCenterPanel;
    }

    public void setPresenter(Presenter presenter) {
        this.presenter = presenter;
    }

    @UiHandler("perspectives")
    public void handleChange(ChangeEvent event) {
        presenter.onChangePerspective(perspectives.getValue(perspectives.getSelectedIndex()));
    }

    @UiHandler("logoutAnchor") void logout(ClickEvent clickEvent) {
        presenter.onLogout();
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
