/*
 * Copyright 2005 JBoss Inc
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

package org.drools.guvnor.client.widgets.drools;

import com.google.gwt.event.dom.client.ClickEvent;
import org.drools.guvnor.client.common.PrettyFormLayout;
import org.drools.guvnor.client.messages.Constants;
import org.drools.guvnor.client.moduleeditor.drools.WorkingSetManager;

import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.CheckBox;
import org.drools.guvnor.client.resources.Images;

/**
 * This controls category administration.
 */
public class RuleVerifierManager extends Composite {


    public VerticalPanel  layout    = new VerticalPanel();

    public RuleVerifierManager() {

        PrettyFormLayout form = new PrettyFormLayout();
        form.addHeader( Images.INSTANCE.ruleVerification(),
                        new HTML( Constants.INSTANCE.EditRulesVerificationConfiguration() ) );
        form.startSection( Constants.INSTANCE.AutomaticVerification() );

        final CheckBox enableOnlineValidator = new CheckBox();
        enableOnlineValidator.setValue( WorkingSetManager.getInstance().isAutoVerifierEnabled() );
        form.addAttribute( Constants.INSTANCE.Enabled(),
                           enableOnlineValidator );

        HorizontalPanel actions = new HorizontalPanel();

        form.addAttribute( "",
                           actions );

        Button btnSave = new Button( Constants.INSTANCE.SaveChanges() );
        btnSave.setTitle( Constants.INSTANCE.SaveAllChanges() );
        btnSave.addClickHandler( new ClickHandler() {
            public void onClick(ClickEvent event) {
                WorkingSetManager.getInstance().setAutoVerifierEnabled( enableOnlineValidator.getValue() );
                Window.alert( Constants.INSTANCE.AllChangesHaveBeenSaved() );
            }
        } );

        actions.add( btnSave );

        form.endSection();

        initWidget( form );

    }

}
