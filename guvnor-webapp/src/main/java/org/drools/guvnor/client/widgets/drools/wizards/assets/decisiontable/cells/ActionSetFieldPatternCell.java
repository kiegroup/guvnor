/*
 * Copyright 2011 JBoss Inc
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
package org.drools.guvnor.client.widgets.drools.wizards.assets.decisiontable.cells;

import org.drools.guvnor.client.decisiontable.Validator;
import org.drools.guvnor.client.resources.WizardResources;
import org.drools.ide.common.client.modeldriven.dt52.Pattern52;

/**
 * A cell to display a Fact Pattern on the Action Set Field page. Additional
 * validation is performed on the Pattern's fields to determine whether the cell
 * should be rendered as valid or invalid
 */
public class ActionSetFieldPatternCell extends PatternCell {

    public ActionSetFieldPatternCell(Validator validator) {
        super( validator );
    }

    protected String getCssStyleName(Pattern52 p) {
        if ( !validator.isPatternBindingUnique( p ) ) {
            return WizardResources.INSTANCE.style().wizardDTableValidationError();
        }
        if ( !validator.arePatternActionSetFieldsValid( p ) ) {
            return WizardResources.INSTANCE.style().wizardDTableValidationError();
        }
        return "";
    }

}
