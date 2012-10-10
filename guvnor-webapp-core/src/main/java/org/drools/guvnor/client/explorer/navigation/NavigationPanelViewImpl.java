/*
 * Copyright 2011 JBoss Inc
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 */

package org.drools.guvnor.client.explorer.navigation;

import com.google.gwt.dom.client.Style;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.StackLayoutPanel;

public class NavigationPanelViewImpl extends Composite implements NavigationPanelView {

    private StackLayoutPanel layout = new StackLayoutPanel(Style.Unit.EM);

    public NavigationPanelViewImpl() {
        layout.setHeight("700px");
        initWidget(layout);
    }

    public void add(IsWidget header, IsWidget content) {
        layout.add(
                content.asWidget(),
                header.asWidget(),
                2
        );
    }

    public void clear() {
        layout.clear();
    }
}
