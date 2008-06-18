package org.drools.brms.client.common;
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



import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Widget;


/**
 * This is handy for in-place context help.
 *
 * @author Michael Neale
 */
public class InfoPopup extends Composite {

    public InfoPopup(final String title, final String message) {
        Image info = new Image("images/information.gif");
        info.setTitle( message );
        info.addClickListener( new ClickListener() {
            public void onClick(Widget w) {
                final FormStylePopup pop = new FormStylePopup("images/information.gif", title);
                pop.addRow( new SmallLabel(message) );

                pop.show();
            }
        } );
        initWidget( info );
    }
}