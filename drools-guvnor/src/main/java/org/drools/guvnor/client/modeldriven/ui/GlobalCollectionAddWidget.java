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
import com.gwtext.client.util.Format;
import org.drools.guvnor.client.common.SmallLabel;
import org.drools.guvnor.client.modeldriven.brl.ActionGlobalCollectionAdd;

/**
 * Add Variable to global collection Widget
 * @author esteban.aliverti@gmail.com
 *
 */
public class GlobalCollectionAddWidget extends RuleModellerWidget {

    private DirtyableFlexTable layout = new DirtyableFlexTable();
    private Constants constants = ((Constants) GWT.create(Constants.class));
    private boolean readOnly;

    public GlobalCollectionAddWidget(RuleModeller mod, ActionGlobalCollectionAdd action) {
        this(mod, action, null);
    }

    /**
     * Creates a new FactPatternWidget
     * @param mod
     * @param p
     * @param readOnly if the widget should be in RO mode. If this parameter
     * is null, the readOnly attribute is calculated.
     */
    public GlobalCollectionAddWidget(RuleModeller modeller, ActionGlobalCollectionAdd action,
            Boolean readOnly) {

        if (readOnly == null) {
            this.readOnly = !modeller.getSuggestionCompletions().containsFactType(modeller.getModel().getBoundFact(action.factName).factType);
        } else {
            this.readOnly = readOnly;
        }

        ActionGlobalCollectionAdd gca = (ActionGlobalCollectionAdd) action;
        SimplePanel sp = new SimplePanel();
        sp.setStyleName("model-builderInner-Background"); //NON-NLS
        sp.add(new SmallLabel("&nbsp;" + Format.format(constants.AddXToListY(), gca.factName, gca.globalName)));

        if (this.readOnly) {
            this.layout.addStyleName("editor-disabled-widget");
            sp.addStyleName("editor-disabled-widget");
        }

        layout.setWidget(0, 0, sp);
        initWidget(layout);

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
