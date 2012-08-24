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

package org.drools.guvnor.client.resources;

import com.google.gwt.user.client.ui.Image;
import org.drools.guvnor.client.messages.Constants;

public class DroolsGuvnorImages {

    public static DroolsGuvnorImages INSTANCE = new DroolsGuvnorImages();

    private DroolsGuvnorImages() {
    }

    public Image Wizard() {
        Image image = new Image(DroolsGuvnorImageResources.INSTANCE.newWiz());
        image.setAltText(Constants.INSTANCE.Wizard());
        return image;
    }

    public Image WarningImage() {
        Image image = new Image(DroolsGuvnorImageResources.INSTANCE.warningLarge());
        image.setAltText(Constants.INSTANCE.Warning());
        return image;
    }

    public Image Snapshot() {
        Image image = new Image(DroolsGuvnorImageResources.INSTANCE.snapshot());
        image.setAltText(Constants.INSTANCE.Snapshot());
        return image;
    }

    public Image Home() {
        Image image = new Image(DroolsGuvnorImageResources.INSTANCE.homeIcon());
        image.setAltText(Constants.INSTANCE.Home());
        return image;
    }

    public Image RuleAsset() {
        Image image = new Image(DroolsGuvnorImageResources.INSTANCE.ruleAsset());
        image.setAltText(Constants.INSTANCE.RuleAsset());
        return image;
    }

    public Image PackageBuilder() {
        Image image = new Image(DroolsGuvnorImageResources.INSTANCE.packageBuilder());
        image.setAltText(Constants.INSTANCE.PackageBuilder());
        return image;
    }
}
