/*
 * Copyright 2013 JBoss by Red Hat.
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

package org.guvnor.udc.client.export;

import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import org.guvnor.udc.client.i8n.Constants;
import org.guvnor.udc.client.util.UtilUsageData;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.uberfire.workbench.events.NotificationEvent;

import com.github.gwtbootstrap.client.ui.ControlLabel;
import com.github.gwtbootstrap.client.ui.TextArea;
import com.github.gwtbootstrap.client.ui.base.IconAnchor;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;

@Dependent
@Templated(value = "ExportUsageDataViewImpl.html")
public class ExportUsageDataViewImpl extends Composite implements ExportUsageDataPresenter.ExportUsageDataEventView {


    private Constants constants = GWT.create(Constants.class);

    @Inject
    @DataField
    private TextArea textAreaExportCsv;

    @Inject
    @DataField
    public ControlLabel exportCsvNameText;

    @Inject
    @DataField
    public IconAnchor refreshIcon;

    @Inject
    private Event<NotificationEvent> notification;
    
    private ExportUsageDataPresenter presenter;
    
    private static final String EXPORT_CSV = "Export Usage Data to CSV";

    @Override
    public void init(ExportUsageDataPresenter presenter) {
        this.presenter = presenter;
        refreshIcon.setTitle(constants.Refresh());
        refreshIcon.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                refreshDataCsv();
            }
        });
        exportCsvNameText.add(new HTMLPanel(EXPORT_CSV));
    }

    private void refreshDataCsv() {
        textAreaExportCsv.setText(constants.No_Usage_Data());
        if (!presenter.getTextFormtCsv().equals("")) {
            StringBuilder formatCsv = new StringBuilder(UtilUsageData.HEADER_TITLE_CSV);
            formatCsv.append(presenter.getTextFormtCsv());
            textAreaExportCsv.setText(formatCsv.toString());
        }
    }

    @Override
    public void displayNotification(String text) {
        notification.fire(new NotificationEvent(text));
    }

}
