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

package org.drools.guvnor.client.perspective;

import com.google.gwt.user.client.ui.IsWidget;
import org.drools.guvnor.client.util.TabbedPanel;

public interface PerspectivesPanelView extends IsWidget {

    interface Presenter {        
        void onChangePerspective(String perspectiveType);
        void onLogout();
    }

    void setPresenter(Presenter presenter);

    void setUserName(String userName);
    
    public void addPerspective(String item, String value);   
    
    TabbedPanel getTabbedPanel();
}
