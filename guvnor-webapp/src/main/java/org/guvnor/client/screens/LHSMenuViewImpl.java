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

package org.guvnor.client.screens;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.RequiresResize;
import com.google.gwt.user.client.ui.Widget;

public class LHSMenuViewImpl
        extends Composite
        implements LHSMenuView, RequiresResize {

    private Presenter presenter;

    interface LHSMenuStyle
            extends CssResource {

        String base();

        String normal();

        String selected();

    }

    interface Binder
            extends
            UiBinder<Widget, LHSMenuViewImpl> {

    }

    private static Binder uiBinder = GWT.create(Binder.class);

    @UiField
    LHSMenuStyle style;

    @UiField HTMLPanel settingsPanel;
    @UiField HTMLPanel usersPanel;
    @UiField HTMLPanel applicationsPanel;
    @UiField HTMLPanel clientsPanel;

    @UiField Label settingsArrow;
    @UiField Label usersArrow;
    @UiField Label applicationsArrow;
    @UiField Label clientsArrow;

    public LHSMenuViewImpl() {
        initWidget(uiBinder.createAndBindUi(this));
        addClickHandler(settingsPanel,"settingsScreen");
        addClickHandler(usersPanel,"usersScreen");
        addClickHandler(applicationsPanel,"applicationsScreen");
        addClickHandler(clientsPanel,"clientsScreen");
    }

    @Override
    public void setPresenter(Presenter presenter) {
        this.presenter = presenter;
    }

    private void addClickHandler(Panel panel, final String screenName) {
        panel.sinkEvents(Event.ONCLICK);
        panel.addHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                onSelect(event);
                presenter.goTo(screenName);
            }
        }, ClickEvent.getType());
    }

    @Override
    public void onResize() {
        int height = getParent().getOffsetHeight();
        int width = getParent().getOffsetWidth();
        setPixelSize(width - 50,
                height - 50);
    }

    public void onSelect(ClickEvent e) {
        settingsArrow.setVisible(checkStyle(e, settingsPanel));
        usersArrow.setVisible(checkStyle(e, usersPanel));
        applicationsArrow.setVisible(checkStyle(e, applicationsPanel));
        clientsArrow.setVisible(checkStyle(e, clientsPanel));
    }

    private boolean checkStyle(ClickEvent e, Widget widget) {
        if (widget.getElement().equals(e.getRelativeElement())) {
            widget.removeStyleName(style.normal());
            widget.addStyleName(style.selected());
            return true;
        } else {
            widget.removeStyleName(style.selected());
            widget.addStyleName(style.normal());
            return false;
        }
    }
}
