/*
 * Copyright 2010 JBoss Inc
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package org.jboss.bpm.console.client.navigation.admin.widget;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.FontWeight;
import com.google.gwt.user.client.ui.*;
import org.drools.guvnor.client.common.InfoPopup;
import org.drools.guvnor.client.common.PrettyFormLayout;
import org.drools.guvnor.client.messages.ConstantsCore;
import org.drools.guvnor.client.resources.ImagesCore;
import org.drools.guvnor.client.widgets.tables.PermissionsPagedTablePresenter;
import org.drools.guvnor.client.widgets.tables.PermissionsPagedTableView;

public class PermissionViewer extends Composite {
    private static ImagesCore images = (ImagesCore) GWT.create(ImagesCore.class);
    private ConstantsCore constants = ((ConstantsCore) GWT.create(ConstantsCore.class));

    private VerticalPanel layout;

    public PermissionViewer() {
        PrettyFormLayout pf = new PrettyFormLayout();

        VerticalPanel header = new VerticalPanel();
        Label caption = new Label(constants.PermissionDetails());
        caption.getElement().getStyle().setFontWeight(FontWeight.BOLD);
        header.add(caption);
        header.add(howToTurnOn());

        pf.addHeader(images.userPermissionsLarge(),
                header);

        layout = new VerticalPanel();
        layout.setHeight("100%");
        layout.setWidth("100%");

        pf.startSection();
        pf.addRow(layout);
        pf.endSection();

        setupWidget();
        initWidget(pf);
    }

    private Widget howToTurnOn() {
        HorizontalPanel hp = new HorizontalPanel();
        hp.add(new HTML("<small><i>"
                + constants.TipAuthEnable()
                + "</i></small>"));
        InfoPopup pop = new InfoPopup(constants.EnablingAuthorization(),
                constants.EnablingAuthPopupTip());
        hp.add(pop);
        return hp;
    }

    private void setupWidget() {
        PermissionsPagedTableView table = new PermissionsPagedTableView();
        PermissionsPagedTablePresenter presenter = new PermissionsPagedTablePresenter(table);
        layout.add(table);
    }
}
