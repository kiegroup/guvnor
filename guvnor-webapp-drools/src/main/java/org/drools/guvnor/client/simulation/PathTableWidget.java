/*
 * Copyright 2012 JBoss Inc
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

package org.drools.guvnor.client.simulation;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;
import org.drools.guvnor.client.simulation.resources.SimulationResources;
import org.drools.guvnor.client.simulation.resources.SimulationStyle;

public class PathTableWidget extends Composite {

    protected interface PathTableWidgetBinder extends UiBinder<Widget, PathTableWidget> {}
    private static PathTableWidgetBinder uiBinder = GWT.create(PathTableWidgetBinder.class);

    private SimulationResources simulationResources = SimulationResources.INSTANCE;
    private SimulationStyle simulationStyle = SimulationResources.INSTANCE.style();

    public PathTableWidget() {
        initWidget(uiBinder.createAndBindUi(this));
    }

}
