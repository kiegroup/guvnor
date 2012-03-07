/*
 * Copyright 2012 JBoss Inc
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

/*Every That is commented in relate to de attribute data is because a NEP*/
package org.drools.guvnor.client.asseteditor.drools.serviceconfig;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;
import org.drools.guvnor.client.common.DirtyableComposite;

public class AdvancedKSessionConfigWidget extends DirtyableComposite {

    // UI
    interface KSessionConfigEditorBinder extends UiBinder<Widget, AdvancedKSessionConfigWidget> {

    }

    private static KSessionConfigEditorBinder uiBinder = GWT.create(KSessionConfigEditorBinder.class);
    @UiField
    protected TextBox textUrl;
    @UiField
    protected ListBox listProtocol;
    @UiField
    protected ListBox listMarshalling;
    @UiField
    protected ListBox listClockType;
    @UiField
    protected ListBox listKeepReference;

    private final ServiceKSessionConfig ksession;

    public AdvancedKSessionConfigWidget(final ServiceKSessionConfig ksession) {
        this.ksession = ksession;

        this.initWidget(uiBinder.createAndBindUi(this));

        this.textUrl.setText(ksession.getUrl());

        this.listProtocol.addItem("Rest", ProtocolOption.REST.toString());
        this.listProtocol.addItem("Web Service", ProtocolOption.WEB_SERVICE.toString());

        if (ksession.getProtocol().equals(ProtocolOption.REST)) {
            this.listProtocol.setSelectedIndex(0);
        } else {
            this.listProtocol.setSelectedIndex(1);
        }

        this.listMarshalling.addItem("XStream", MarshallingOption.XSTREAM.toString());
        this.listMarshalling.addItem("Jaxb", MarshallingOption.JAXB.toString());
        this.listMarshalling.addItem("Json", MarshallingOption.JSON.toString());

        if (ksession.getMarshalling().equals(MarshallingOption.XSTREAM)) {
            this.listMarshalling.setSelectedIndex(0);
        } else if (ksession.getMarshalling().equals(MarshallingOption.JAXB)) {
            this.listMarshalling.setSelectedIndex(1);
        } else {
            this.listMarshalling.setSelectedIndex(2);
        }

        listClockType.addItem("Omitted", "");
        listClockType.addItem("Pseudo", ClockType.PSEUDO.toString());
        listClockType.addItem("Real Time", ClockType.REALTIME.toString());
        if (ksession.getClockType() == null) {
            this.listClockType.setSelectedIndex(0);
        } else if (ksession.getClockType().equals(ClockType.PSEUDO)) {
            this.listClockType.setSelectedIndex(1);
        } else if (ksession.getClockType().equals(ClockType.REALTIME)) {
            this.listClockType.setSelectedIndex(2);
        }

        listKeepReference.addItem("Omitted", "");
        listKeepReference.addItem("True", "true");
        listKeepReference.addItem("False", "false");
        if (ksession.getKeepReference() == null) {
            this.listKeepReference.setSelectedIndex(0);
        } else if (ksession.getKeepReference().equals(true)) {
            this.listKeepReference.setSelectedIndex(1);
        } else {
            this.listKeepReference.setSelectedIndex(2);
        }
    }

    public void updateKSession() {
        ksession.setUrl(textUrl.getText());
        ksession.setProtocol(ProtocolOption.valueOf(listProtocol.getValue(listProtocol.getSelectedIndex())));
        ksession.setMarshalling(MarshallingOption.valueOf(listMarshalling.getValue(listMarshalling.getSelectedIndex())));
        if (listClockType.getValue(listClockType.getSelectedIndex()).length() == 0) {
            ksession.setClockTypeToNull();
        } else {
            ksession.setClockType(ClockType.valueOf(listClockType.getValue(listClockType.getSelectedIndex())));
        }
        if (listKeepReference.getValue(listKeepReference.getSelectedIndex()).length() == 0) {
            ksession.setKeepReferenceToNull();
        } else {
            ksession.setKeepReference(Boolean.parseBoolean(listKeepReference.getValue(listKeepReference.getSelectedIndex())));
        }

    }

}