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
package org.drools.guvnor.client.util;

import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * 
 * @author rikkola
 *
 */
class LazyStackPanelRow extends VerticalPanel {

    private final LazyStackPanelHeader header;
    private final LoadContentCommand   contentLoad;
    private Widget                     contentWidget = null;
    private SimplePanel                contentPanel  = new SimplePanel();

    private boolean                    expanded;

    public LazyStackPanelRow(LazyStackPanelHeader titleWidget,
                         LoadContentCommand contentLoad) {

        this.header = titleWidget;
        this.contentLoad = contentLoad;
        init();
    }

    private void init() {
        clear();

        if ( expanded ) {
            header.icon.setUrl( "images/collapse.gif" );
        } else {
            header.icon.setUrl( "images/expand.gif" );
        }

        add( header );

        if ( contentWidget != null ) {
            contentWidget.setVisible( expanded );
            contentPanel.add( contentWidget );
        }

    }

    public SimplePanel getContentPanel() {
        return contentPanel;
    }

    public void expand() {
        expanded = true;

        if ( contentWidget == null ) {
            contentWidget = contentLoad.load();
        }

        init();
    }

    public void compress() {
        expanded = false;
        init();
    }

    public boolean isExpanded() {
        return expanded;
    }
}