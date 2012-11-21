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

package org.kie.uberfirebootstrap.client.widgets;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiConstructor;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Image;
import org.kie.uberfirebootstrap.client.resources.constants.UberfireBootstrapConstants;
import org.kie.uberfirebootstrap.client.resources.images.UberfireBootstrapImages;

/**
 * This is handy for in-place context help.
 */
public class InfoPopup
        extends Composite {

    @UiConstructor
    public InfoPopup(final String title,
                     final String message) {
        Image info = new Image(UberfireBootstrapImages.INSTANCE.information());
        info.setTitle(message);
        info.setAltText(message);
        info.addClickHandler(new ClickHandler() {

            public void onClick(ClickEvent event) {
                Image image = new Image(UberfireBootstrapImages.INSTANCE.information());
                image.setAltText(UberfireBootstrapConstants.INSTANCE.Information());
                final FormStylePopup pop = new FormStylePopup(image,
                        title);
                pop.addRow(new SmallLabel(message));
                pop.show();
            }
        });
        initWidget(info);
    }
}
