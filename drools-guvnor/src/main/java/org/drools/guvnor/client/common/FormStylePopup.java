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

package org.drools.guvnor.client.common;

import com.google.gwt.user.client.ui.Widget;

/**
 * This builds on the FormStyleLayout for providing common popup features in a
 * columnar form layout, with a title and a large (ish) icon.
 *
 * @author Michael Neale
 */
public class FormStylePopup extends Popup {

    private FormStyleLayout form;

    public FormStylePopup(String image,
                          final String title) {

        form = new FormStyleLayout( image,
                                    title );

        setTitle( title );

    }

    public FormStylePopup() {
        form = new FormStyleLayout();
    }

    public FormStylePopup(String image,
                          final String title,
                          Integer width) {
        this( image,
              title );
        setWidth( width + "px" );
    }

    @Override
    public Widget getContent() {
        return form;
    }

    public void clear() {
        this.form.clear();
    }

    public void addAttribute(String label,
                             Widget wid) {
        form.addAttribute( label,
                           wid );
    }

    public void addRow(Widget wid) {
        form.addRow( wid );
    }

}