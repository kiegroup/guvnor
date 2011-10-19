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
package org.drools.guvnor.client.widgets;

import org.drools.guvnor.client.common.SmallLabel;
import org.drools.guvnor.client.resources.Images;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.safehtml.client.SafeHtmlTemplates;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.Composite;

public class MessageWidget extends Composite {

    interface MessageTemplate
        extends
        SafeHtmlTemplates {

        @Template("<div style=\"background-color: yellow;\" >{0}</div>")
        SafeHtml message(String message);
    }

    private Images                       images   = GWT.create( Images.class );

    private static final MessageTemplate TEMPLATE = GWT.create( MessageTemplate.class );

    private final SmallLabel             label    = new SmallLabel();

    public MessageWidget() {

        label.setVisible( false );

        initWidget( label );
    }

    public void showMessage(String message) {
        showMessage( images.greenTick(),
                     message );
    }

    public void showMessage(ImageResource image,
                            String message) {

        label.setVisible( true );
        label.setHTML( TEMPLATE.message( message ) );

        Timer timer = new Timer() {
            public void run() {
                label.setVisible( false );
            }
        };
        timer.schedule( 1500 );
    }
}
