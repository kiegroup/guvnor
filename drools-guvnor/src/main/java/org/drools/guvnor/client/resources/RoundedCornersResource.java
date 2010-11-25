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
package org.drools.guvnor.client.resources;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.resources.client.ImageResource.ImageOptions;
import com.google.gwt.resources.client.ImageResource.RepeatStyle;

/**
 * 
 * @author rikkola
 *
 */
public interface RoundedCornersResource
    extends
    ClientBundle {

    RoundedCornersResource INSTANCE = GWT.create( RoundedCornersResource.class );

    @Source("images/corners/disclosurePanelTopLeftCorner.gif")
    ImageResource disclosurePanelTopLeftCorner();

    @Source("images/corners/disclosurePanelTopRightCorner.gif")
    ImageResource disclosurePanelTopRightCorner();

    @Source("images/corners/disclosurePanelBottomLeftCorner.gif")
    ImageResource disclosurePanelBottomLeftCorner();

    @Source("images/corners/disclosurePanelBottomRightCorner.gif")
    ImageResource disclosurePanelBottomRightCorner();

    @ImageOptions(repeatStyle = RepeatStyle.Horizontal)
    @Source("images/corners/disclosurePanelBottom.gif")
    ImageResource disclosurePanelBottom();

    @ImageOptions(flipRtl = true, repeatStyle = RepeatStyle.Horizontal)
    @Source("images/corners/disclosurePanelTop.gif")
    ImageResource disclosurePanelTop();

    @ImageOptions(repeatStyle = RepeatStyle.Vertical)
    @Source("images/corners/disclosurePanelLeftSide.gif")
    ImageResource disclosurePanelSideLeft();

    @ImageOptions(repeatStyle = RepeatStyle.Vertical)
    @Source("images/corners/disclosurePanelRightSide.gif")
    ImageResource disclosurePanelSideRight();

    @Source("css/RoundCorners.css")
    RoundCornersCss roundCornersCss();

}
