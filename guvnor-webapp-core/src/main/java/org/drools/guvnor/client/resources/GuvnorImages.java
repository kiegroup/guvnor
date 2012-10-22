/*
 * Copyright 2012 JBoss Inc
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
package org.drools.guvnor.client.resources;

import org.drools.guvnor.client.messages.ConstantsCore;
import org.drools.guvnor.shared.api.Valid;

import com.google.gwt.user.client.ui.Image;

public class GuvnorImages {

    public static final GuvnorImages INSTANCE = new GuvnorImages();

    private GuvnorImages() {

    }

    public Image DeleteItemSmall() {
        Image image = new Image(ImagesCore.INSTANCE.itemImages().deleteItemSmall());
        image.setAltText(ConstantsCore.INSTANCE.DeleteItem());
        return image;
    }

    public Image NewItem() {
        Image image = new Image(ImagesCore.INSTANCE.itemImages().newItem());
        image.setAltText(ConstantsCore.INSTANCE.NewItem());
        return image;
    }

    public Image Trash() {
        Image image = new Image(ImagesCore.INSTANCE.trash());
        image.setAltText(ConstantsCore.INSTANCE.Trash());
        return image;
    }

    public Image Edit() {
        Image image = new Image(ImagesCore.INSTANCE.edit());
        image.setAltText(ConstantsCore.INSTANCE.Edit());
        return image;
    }

    public Image Refresh() {
        Image image = new Image(ImagesCore.INSTANCE.refresh());
        image.setAltText(ConstantsCore.INSTANCE.Refresh());
        return image;
    }

    public Image RuleAsset() {
        Image image = new Image(ImagesCore.INSTANCE.ruleAsset());
        image.setAltText(ConstantsCore.INSTANCE.BusinessRuleAssets());
        return image;
    }

    public Image SpreadsheetSmall() {
        Image image = new Image(ImagesCore.INSTANCE.spreadsheetSmall());
        image.setAltText(ConstantsCore.INSTANCE.BusinessRuleAssets());
        return image;
    }

    public Image Gdst() {
        Image image = new Image(ImagesCore.INSTANCE.gdst());
        image.setAltText(ConstantsCore.INSTANCE.BusinessRuleAssets());
        return image;
    }

    public Image TechnicalRuleAssets() {
        Image image = new Image(ImagesCore.INSTANCE.technicalRuleAssets());
        image.setAltText(ConstantsCore.INSTANCE.TechnicalRuleAssets());
        return image;
    }

    public Image FunctionAssets() {
        Image image = new Image(ImagesCore.INSTANCE.functionAssets());
        image.setAltText(ConstantsCore.INSTANCE.Functions());
        return image;
    }

    public Image Dsl() {
        Image image = new Image(ImagesCore.INSTANCE.dsl());
        image.setAltText(ConstantsCore.INSTANCE.DSLConfigurations());
        return image;
    }

    public Image ModelAsset() {
        Image image = new Image(ImagesCore.INSTANCE.modelAsset());
        image.setAltText(ConstantsCore.INSTANCE.Model());
        return image;
    }

    public Image RuleflowSmall() {
        Image image = new Image(ImagesCore.INSTANCE.ruleflowSmall());
        image.setAltText(ConstantsCore.INSTANCE.RuleFlows());
        return image;
    }

    public Image Enumeration() {
        Image image = new Image(ImagesCore.INSTANCE.enumeration());
        image.setAltText(ConstantsCore.INSTANCE.Enumerations());
        return image;
    }

    public Image TestManager() {
        Image image = new Image(ImagesCore.INSTANCE.testManager());
        image.setAltText(ConstantsCore.INSTANCE.TestScenarios());
        return image;
    }

    public Image NewFile() {
        Image image = new Image(ImagesCore.INSTANCE.newFile());
        image.setAltText(ConstantsCore.INSTANCE.OtherAssetsDocumentation());
        return image;
    }

    public Image Workingset() {
        Image image = new Image(ImagesCore.INSTANCE.workingset());
        image.setAltText(ConstantsCore.INSTANCE.WorkingSets());
        return image;
    }

    public Image EventLogSmall() {
        Image image = new Image(ImagesCore.INSTANCE.eventLogSmall());
        image.setAltText(ConstantsCore.INSTANCE.Documentation());
        return image;
    }

    public Image getValidImage(Valid valid) {
        switch (valid) {
            case INVALID:
                Image image = new Image(ImagesCore.INSTANCE.validationError());
                //image.setAltText(ConstantsCore.INSTANCE.Documentation());
                return image;
            case VALID:
                Image image2 = new Image(ImagesCore.INSTANCE.greenTick());
                //image2.setAltText(ConstantsCore.INSTANCE.Documentation());
                return image2;
            default:
                Image image3 = new Image(ImagesCore.INSTANCE.warning());
                //image3.setAltText(ConstantsCore.INSTANCE.Documentation());
                return image3;

        }
    }

    public Image Feed() {
        Image image = new Image(ImagesCore.INSTANCE.feed());
        image.setAltText(ConstantsCore.INSTANCE.Feed());
        return image;
    }

    public Image Backup() {
        Image image = new Image(ImagesCore.INSTANCE.backupLarge());
        image.setAltText("");
        return image;
    }

    public Image UserPermissions() {
        Image image = new Image(ImagesCore.INSTANCE.userPermissionsLarge());
        image.setAltText("");
        return image;
    }

    public Image Status() {
        Image image = new Image(ImagesCore.INSTANCE.statusLarge());
        image.setAltText("");
        return image;
    }

    public Image Scenario() {
        Image image = new Image(ImagesCore.INSTANCE.scenarioLarge());
        image.setAltText("");
        return image;
    }

    public Image WorkspaceManager() {
        Image image = Status();
        image.setAltText("");
        return image;
    }

    public Image EventLog() {
        Image image = new Image(ImagesCore.INSTANCE.eventLogLarge());
        image.setAltText("");
        return image;
    }

    public Image Config() {
        Image image = new Image(ImagesCore.INSTANCE.config());
        image.setAltText("");
        return image;
    }

    public Image EditCategories() {
        Image image = new Image(ImagesCore.INSTANCE.editCategory());
        image.setAltText("");
        return image;
    }

    public Image RuleVerification() {
        Image image = new Image(ImagesCore.INSTANCE.ruleVerification());
        image.setAltText("");
        return image;
    }

    public Image Analyze() {
        Image image = new Image(ImagesCore.INSTANCE.analyzeLarge());
        image.setAltText("");
        return image;
    }
    public Image Snapshot() {
        Image image = new Image(ImagesCore.INSTANCE.snapshot());
        image.setAltText(ConstantsCore.INSTANCE.Snapshot());
        return image;
    }

}
