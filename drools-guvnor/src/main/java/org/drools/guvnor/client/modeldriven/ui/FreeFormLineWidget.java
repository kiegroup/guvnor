package org.drools.guvnor.client.modeldriven.ui;
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

import org.drools.guvnor.client.common.DirtyableFlexTable;
import org.drools.guvnor.client.messages.Constants;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.*;
import com.google.gwt.user.client.ui.FlexTable.FlexCellFormatter;
import org.drools.guvnor.client.modeldriven.brl.FreeFormLine;

/**
 * Free form DRL line widget
 * @author esteban.aliverti@gmail.com
 *
 */
public class FreeFormLineWidget extends RuleModellerWidget {

    private FreeFormLine action;
    private DirtyableFlexTable layout = new DirtyableFlexTable();
    private Constants constants = ((Constants) GWT.create(Constants.class));
    private boolean readOnly;

    public FreeFormLineWidget(RuleModeller mod, FreeFormLine p) {
        this(mod, p, null);
    }

    /**
     * Creates a new FactPatternWidget
     * @param mod
     * @param p
     * @param readOnly if the widget should be in RO mode. If this parameter
     * is null, the readOnly attribute is calculated.
     */
    public FreeFormLineWidget(RuleModeller mod, FreeFormLine p,
            Boolean readOnly) {
        super(mod);
        this.action = p;

        if (readOnly == null) {
            this.readOnly = false;
        } else {
            this.readOnly = readOnly;
        }

        layout.setWidget(0, 0, createTextBox());
        FlexCellFormatter formatter = layout.getFlexCellFormatter();
        formatter.setAlignment(0, 0, HasHorizontalAlignment.ALIGN_LEFT, HasVerticalAlignment.ALIGN_BOTTOM);

        if (this.readOnly) {
            this.layout.addStyleName("editor-disabled-widget");
        }

        initWidget(layout);

    }

    private Widget createTextBox() {
        final TextBox tb = new TextBox();
        tb.setText(this.action.text);
        tb.setTitle(constants.ThisIsADrlExpressionFreeForm());

        if (!this.readOnly) {
            tb.addChangeListener(new ChangeListener() {

                public void onChange(Widget arg0) {
                    action.text = tb.getText();
                }
            });

            
//            tb.addFocusListener(new FocusListenerAdapter() {
//
//                @Override
//                public void onLostFocus(Widget sender) {
//                    getModeller().verifyRule();
//                }
//
//            });
        } else {
            tb.setEnabled(false);
        }
        return tb;
    }

    @Override
    public boolean isDirty() {
        return layout.hasDirty();
    }

    @Override
    public boolean isReadOnly() {
        return this.readOnly;
    }
}
