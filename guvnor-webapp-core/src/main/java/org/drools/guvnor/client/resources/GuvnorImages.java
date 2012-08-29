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

import com.google.gwt.user.client.ui.Image;
import org.drools.guvnor.client.messages.ConstantsCore;

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
}
