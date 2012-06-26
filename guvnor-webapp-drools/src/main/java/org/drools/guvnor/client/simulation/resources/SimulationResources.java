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

package org.drools.guvnor.client.simulation.resources;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ImageResource;
import org.drools.guvnor.client.resources.CollapseExpand;
import org.drools.guvnor.client.resources.ItemImages;
import org.drools.guvnor.client.resources.TableImageResources;

/**
 * GWT resources for simulation.
 */
public interface SimulationResources extends ClientBundle {

    SimulationResources INSTANCE = GWT.create(SimulationResources.class);

    @Source("zoomIn.png") // From Tango project (public domain license)
    ImageResource zoomInIcon();

    @Source("zoomOut.png") // From Tango project (public domain license)
    ImageResource zoomOutIcon();

};
