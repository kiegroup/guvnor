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
package org.kie.guvnor.guided.rule.client.resources;

import com.google.gwt.user.client.ui.Image;
import org.kie.guvnor.commons.ui.client.resources.CommonImages;
import org.kie.guvnor.commons.ui.client.resources.ItemImages;
import org.kie.guvnor.guided.rule.client.resources.i18n.Constants;

public class GuvnorImages {

    public static final GuvnorImages INSTANCE = new GuvnorImages();

    private GuvnorImages() {

    }

    public Image DeleteItemSmall() {
        Image image = new Image( ItemImages.INSTANCE.deleteItemSmall() );
        image.setAltText( Constants.INSTANCE.DeleteItem() );
        return image;
    }

    public Image Edit() {
        Image image = new Image( CommonImages.INSTANCE.edit() );
        image.setAltText( Constants.INSTANCE.Edit() );
        return image;
    }

}
