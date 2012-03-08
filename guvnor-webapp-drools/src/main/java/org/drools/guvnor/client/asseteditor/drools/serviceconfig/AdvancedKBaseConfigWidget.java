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
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;
import org.drools.guvnor.client.common.DirtyableComposite;
import org.drools.guvnor.client.messages.Constants;

import static org.drools.guvnor.client.asseteditor.drools.serviceconfig.AssertBehaviorOption.*;
import static org.drools.guvnor.client.asseteditor.drools.serviceconfig.EventProcessingOption.*;

public class AdvancedKBaseConfigWidget extends DirtyableComposite {

    // UI
    interface KBaseAdvancedConfigOptionsBinder extends UiBinder<Widget, AdvancedKBaseConfigWidget> {

    }

    private static KBaseAdvancedConfigOptionsBinder uiBinder = GWT.create(KBaseAdvancedConfigOptionsBinder.class);

    @UiField
    protected ListBox listMBeans;
    @UiField
    protected ListBox listEventProcessingMode;
    @UiField
    protected ListBox listAssertBehavior;
    @UiField
    protected CheckBox checkEnabledAuthentication;
    @UiField
    protected TextBox textAssetsUser;
    @UiField
    protected TextBox textAssetsPassword;

    private final ServiceKBaseConfig kbase;

    public AdvancedKBaseConfigWidget(final ServiceKBaseConfig kbase) {
        this.kbase = kbase;

        this.initWidget(uiBinder.createAndBindUi(this));

        listMBeans.addItem(Constants.INSTANCE.OmittedOption(), "");
        listMBeans.addItem(Constants.INSTANCE.TrueOption(), "true");
        listMBeans.addItem(Constants.INSTANCE.FalseOption(), "false");
        if (kbase.getMbeans() == null) {
            this.listMBeans.setSelectedIndex(0);
        } else if (kbase.getMbeans().equals(true)) {
            this.listMBeans.setSelectedIndex(1);
        } else {
            this.listMBeans.setSelectedIndex(2);
        }

        this.listEventProcessingMode.addItem(Constants.INSTANCE.OmittedOption(), "");
        this.listEventProcessingMode.addItem(CLOUD.toDisplay(), CLOUD.toString());
        this.listEventProcessingMode.addItem(STREAM.toDisplay(), STREAM.toString());

        if (kbase.getEventProcessingMode() == null) {
            this.listEventProcessingMode.setSelectedIndex(0);
        } else if (kbase.getEventProcessingMode().equals(CLOUD)) {
            this.listEventProcessingMode.setSelectedIndex(1);
        } else {
            this.listEventProcessingMode.setSelectedIndex(2);
        }

        this.listAssertBehavior.addItem(Constants.INSTANCE.OmittedOption(), "");
        this.listAssertBehavior.addItem(EQUALITY.toDisplay(), EQUALITY.toString());
        this.listAssertBehavior.addItem(IDENTITY.toDisplay(), IDENTITY.toString());

        if (kbase.getAssertBehavior() == null) {
            this.listAssertBehavior.setSelectedIndex(0);
        } else if (kbase.getAssertBehavior().equals(EQUALITY)) {
            this.listAssertBehavior.setSelectedIndex(1);
        } else {
            this.listAssertBehavior.setSelectedIndex(2);
        }

        if (kbase.getAssetsUser() == null || kbase.getAssetsPassword() == null) {
            checkEnabledAuthentication.setValue(false);
            textAssetsPassword.setText("");
            textAssetsUser.setText("");
            textAssetsPassword.setEnabled(false);
            textAssetsUser.setEnabled(false);
        } else {
            checkEnabledAuthentication.setValue(true);
            textAssetsPassword.setText(kbase.getAssetsPassword());
            textAssetsUser.setText(kbase.getAssetsUser());
            textAssetsPassword.setEnabled(true);
            textAssetsUser.setEnabled(true);
        }

        checkEnabledAuthentication.addValueChangeHandler(new ValueChangeHandler<Boolean>() {
            public void onValueChange(ValueChangeEvent<Boolean> booleanValueChangeEvent) {
                if (booleanValueChangeEvent.getValue()) {
                    textAssetsPassword.setEnabled(true);
                    textAssetsUser.setEnabled(true);
                } else {
                    textAssetsPassword.setEnabled(false);
                    textAssetsUser.setEnabled(false);
                }
            }
        });
    }

    public void updateKBase() {
        if (listMBeans.getValue(listMBeans.getSelectedIndex()).length() == 0) {
            kbase.setMbeansToNull();
        } else {
            kbase.setMbeans(Boolean.parseBoolean(listMBeans.getValue(listMBeans.getSelectedIndex())));
        }

        if (listEventProcessingMode.getValue(listEventProcessingMode.getSelectedIndex()).length() == 0) {
            kbase.setEventProcessingModeToNull();
        } else {
            kbase.setEventProcessingMode(EventProcessingOption.valueOf(listEventProcessingMode.getValue(listEventProcessingMode.getSelectedIndex())));
        }

        if (listAssertBehavior.getValue(listAssertBehavior.getSelectedIndex()).length() == 0) {
            kbase.setAssertBehaviorToNull();
        } else {
            kbase.setAssertBehavior(AssertBehaviorOption.valueOf(listAssertBehavior.getValue(listAssertBehavior.getSelectedIndex())));
        }

        if (checkEnabledAuthentication.getValue()) {
            kbase.setAssetsUser(textAssetsUser.getText());
            kbase.setAssetsPassword(textAssetsPassword.getText());
        } else {
            kbase.setAssetsPasswordToNull();
            kbase.setAssetsUserToNull();
        }
    }

}