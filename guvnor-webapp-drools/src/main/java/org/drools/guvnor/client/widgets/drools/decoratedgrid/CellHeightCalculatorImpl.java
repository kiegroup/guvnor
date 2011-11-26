/*
 * Copyright 2011 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.drools.guvnor.client.widgets.drools.decoratedgrid;

import org.drools.guvnor.client.resources.DecisionTableResources;
import org.drools.guvnor.client.resources.DecisionTableResources.DecisionTableStyle;

import com.google.gwt.core.client.GWT;

/**
 * Default implementation of utility class to calculate the height of a merged
 * cell in a VerticalMergedGrid widget. IE and Mozilla don't have any common
 * ground on how to calculate the height of a HTML table cell spanning multiple
 * rows. After much trying deferred bindings offered a simple, and arguably more
 * elegant, approach.
 */
public class CellHeightCalculatorImpl {

    protected static final DecisionTableResources resource = GWT.create( DecisionTableResources.class );
    protected static final DecisionTableStyle     style    = resource.style();

    public int calculateHeight(int rowSpan) {
        int divHeight = (style.rowHeight()) * rowSpan - style.borderWidth();
        return divHeight;
    }

}
