/*
 * Copyright 2010 JBoss Inc
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

package org.drools.guvnor.client.explorer.navigation.qa;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.ui.AbstractImagePrototype;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.TreeItem;
import org.drools.guvnor.client.messages.Constants;
import org.drools.guvnor.client.resources.Images;
import org.drools.guvnor.client.rpc.AnalysisFactUsage;
import org.drools.guvnor.client.rpc.AnalysisFieldUsage;

class FactUsagesItem extends TreeItem {

    private Constants constants = GWT.create(Constants.class);
    private Images images = GWT.create(Images.class);

    public FactUsagesItem(AnalysisFactUsage[] factUsages) {
        setStyleName("analysis-Report");

        setHTML(createImageTag(images.factTemplate(), "") + " <b> " + constants.ShowFactUsages() + " </b> ");

        setUserObject(new HTML(createImageTag(images.factTemplate(), "") + " <b>" + constants.FactUsages() + ":</b>"));

        doFacts(factUsages);
    }

    private void doFacts(AnalysisFactUsage[] factUsages) {
        for (AnalysisFactUsage factUsage : factUsages) {

            TreeItem fact = new TreeItem(createImageTag(images.fact(), constants.Fact()) + factUsage.name);
            TreeItem fieldList = doFields(factUsage.fields);
            fact.addItem(fieldList);
            fieldList.setState(true);

            addItem(fact);
            fact.setState(true);
        }
    }

    private TreeItem doFields(AnalysisFieldUsage[] fields) {
        TreeItem fieldList = new TreeItem(constants.FieldsUsed());

        for (AnalysisFieldUsage fieldUsage : fields) {
            TreeItem field = new TreeItem(createImageTag(images.field(), constants.Field()) + fieldUsage.name);
            fieldList.addItem(field);
            TreeItem ruleList = doAffectedRules(fieldUsage);
            field.addItem(ruleList);
            field.setState(true);
        }

        return fieldList;
    }

    private TreeItem doAffectedRules(AnalysisFieldUsage fieldUsage) {
        TreeItem ruleList = new TreeItem(constants.ShowRulesAffected());
        ruleList.setUserObject(new HTML(constants.RulesAffected()));
        for (String ruleName : fieldUsage.rules) {
            ruleList.addItem(new TreeItem(createImageTag(images.ruleAsset(), constants.Rule()) + ruleName));
        }
        return ruleList;
    }

    private String createImageTag(ImageResource imageResource, String alt) {
        return "<img src=\"" + imageResource.getURL() + "\" alt=\"" + alt + "\"/>";
    }
}
