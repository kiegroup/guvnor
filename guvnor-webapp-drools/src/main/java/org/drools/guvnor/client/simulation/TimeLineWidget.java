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

import com.google.gwt.dom.client.Style;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.LayoutPanel;
import com.google.gwt.user.client.ui.ResizeComposite;

public class TimeLineWidget extends ResizeComposite {

    private static final double PATH_HEIGHT = 20.0;

    // TODO zoom buttons

    private LayoutPanel timeLineContent;

    private double millisecondsPerPixel = 10.0;

    public TimeLineWidget() {
        timeLineContent = new LayoutPanel();
        timeLineContent.setHeight(Double.valueOf(5.0 * PATH_HEIGHT).intValue() + "px");
        timeLineContent.setWidth("100%");


        Button b1 = new Button("b1");
        timeLineContent.add(b1);
        timeLineContent.setWidgetLeftWidth(b1, 1000 / millisecondsPerPixel, Style.Unit.PX, 30, Style.Unit.PX);
        timeLineContent.setWidgetTopHeight(b1, 10, Style.Unit.PX, PATH_HEIGHT, Style.Unit.PX);

        Button b2 = new Button("b2");
        timeLineContent.add(b2);
        timeLineContent.setWidgetLeftWidth(b2, 2000 / millisecondsPerPixel, Style.Unit.PX, 30, Style.Unit.PX);
        timeLineContent.setWidgetTopHeight(b2, 10 + PATH_HEIGHT, Style.Unit.PX, PATH_HEIGHT, Style.Unit.PX);

        Button b3 = new Button("b3");
        timeLineContent.add(b3);
        timeLineContent.setWidgetLeftWidth(b3, 3000 / millisecondsPerPixel, Style.Unit.PX, 30, Style.Unit.PX);
        timeLineContent.setWidgetTopHeight(b3, 10 + PATH_HEIGHT, Style.Unit.PX, PATH_HEIGHT, Style.Unit.PX);



        initWidget(timeLineContent);
    }

    // TODO use timeLineContent.animate(500) to while zooming

}
