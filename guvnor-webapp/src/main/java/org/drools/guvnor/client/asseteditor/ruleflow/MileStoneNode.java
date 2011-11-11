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

package org.drools.guvnor.client.asseteditor.ruleflow;

import org.drools.guvnor.client.resources.FlowImages;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ImageResource;

public class MileStoneNode extends RuleFlowBaseNode {

    private static FlowImages images = GWT.create( FlowImages.class );

    @Override
    public Corners getCorners() {
        return Corners.ROUNDED;
    }

    @Override
    public ImageResource getImagePath() {
        return images.question();
    }

    @Override
    public String getStyle() {
        return YELLOW_RULE_FLOW_NODE_STYLE;
    }

}
