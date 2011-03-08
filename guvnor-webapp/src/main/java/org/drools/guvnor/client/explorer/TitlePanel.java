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
package org.drools.guvnor.client.explorer;

import org.drools.guvnor.client.LoggedInUserInfo;
import org.drools.guvnor.client.resources.GuvnorResources;
import org.drools.guvnor.client.resources.Images;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.DockPanel;
import com.google.gwt.user.client.ui.HTML;

/**
 * The title bar at the top of the application.
 */
public class TitlePanel extends DockPanel {

    private static Images images = GWT.create( Images.class );

    public TitlePanel(LoggedInUserInfo uif) {
        setVerticalAlignment( DockPanel.ALIGN_MIDDLE );
        add( new HTML( "<div class='header'><img src='" + images.headerLogo().getURL() + "' /></div>" ),
             DockPanel.WEST );
        add( uif,
             DockPanel.EAST );
        setStyleName( GuvnorResources.INSTANCE.headerCss().headerClass() );
        setWidth( "100%" );
    }
}
