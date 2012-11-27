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

package org.kie.guvnor.guided.client.resources;

import com.google.gwt.user.client.ui.Image;
import org.kie.guvnor.guided.client.resources.i18n.Constants;

public class DroolsGuvnorImages {

    public static DroolsGuvnorImages INSTANCE = new DroolsGuvnorImages();

    private DroolsGuvnorImages() {
    }

    public Image Wizard() {
        Image image = new Image( DroolsGuvnorImageResources.INSTANCE.newWiz() );
        image.setAltText( Constants.INSTANCE.Wizard() );
        return image;
    }

    public Image DeleteItemSmall() {
        Image image = new Image( DroolsGuvnorImageResources.INSTANCE.itemImages().deleteItemSmall() );
        image.setAltText( Constants.INSTANCE.DeleteItem() );
        return image;
    }

    public Image NewItem() {
        Image image = new Image( DroolsGuvnorImageResources.INSTANCE.itemImages().newItem() );
        image.setAltText( Constants.INSTANCE.NewItem() );
        return image;
    }

    public Image NewItemBelow() {
        Image image = new Image( DroolsGuvnorImageResources.INSTANCE.newItemBelow() );
        image.setAltText( Constants.INSTANCE.NewItemBelow() );
        return image;
    }

    public Image MoveDown() {
        Image image = new Image( DroolsGuvnorImageResources.INSTANCE.shuffleDown() );
        image.setAltText( Constants.INSTANCE.MoveDown() );

        return image;
    }

    public Image MoveUp() {
        Image image = new Image( DroolsGuvnorImageResources.INSTANCE.shuffleUp() );
        image.setAltText( Constants.INSTANCE.MoveUp() );
        return image;
    }

    public Image WarningSmall() {
        Image image = new Image( DroolsGuvnorImageResources.INSTANCE.warning() );
        image.setAltText( Constants.INSTANCE.Warning() );
        return image;
    }

    public Image Error() {
        Image image = new Image( DroolsGuvnorImageResources.INSTANCE.error() );
        image.setAltText( Constants.INSTANCE.Error() );
        return image;
    }

    public Image EditDisabled() {
        Image image = new Image( DroolsGuvnorImageResources.INSTANCE.editDisabled() );
        image.setAltText( Constants.INSTANCE.EditDisabled() );
        return image;
    }

    public Image AddConnective() {
        Image image = new Image( DroolsGuvnorImageResources.INSTANCE.addConnective() );
        image.setAltText( Constants.INSTANCE.AddMoreOptionsToThisFieldsValues() );
        return image;
    }

    public Image AddFieldToFact() {
        Image image = new Image( DroolsGuvnorImageResources.INSTANCE.addFieldToFact() );
        image.setAltText( Constants.INSTANCE.AddAFieldToThisExpectation() );
        return image;
    }
}
