/**
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

package org.drools.guvnor.client.common;

import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.ui.AbstractImagePrototype;
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
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;


public class Util {
    
    /**
     * Get a string representation of the header that includes an image and some
     * text.
     * 
     * @param image the {@link ImageResource} to add next to the header
     * @param text the header text
     * @return the header as a string
     */
    public static String getHeader(ImageResource image, String text) {
       return AbstractImagePrototype.create(image).getHTML() + " " + text;
    }
    
    /**
     * Get a string representation of the header that includes an image and some
     * text.
     * 
     * @param image the {@link ImageResource} to add next to the header
     * @param text the header text
     * @return the header as a string
     */
    public static HTML getHeaderHTML(ImageResource image, String text) {
      // Add the image and text to a horizontal panel
      HorizontalPanel hPanel = new HorizontalPanel();
      hPanel.setSpacing(0);
      hPanel.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
      hPanel.add(new Image(image));
      HTML headerText = new HTML(text);
      headerText.setStyleName("cw-StackPanelHeader");
      hPanel.add(headerText);

      // Return the HTML string for the panel
      return new HTML(hPanel.getElement().getString());
    }
}