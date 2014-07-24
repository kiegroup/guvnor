/*
 * Copyright 2014 JBoss Inc
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

package org.guvnor.client.screens;

import javax.enterprise.context.Dependent;

import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.RequiresResize;
import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.annotations.WorkbenchPartView;
import org.uberfire.client.annotations.WorkbenchScreen;

@Dependent
@WorkbenchScreen(identifier = "applicationsScreen")
public class ApplicationsScreenPresenter
        implements RequiresResize {

    private Empty widget = new Empty("applications");

    @WorkbenchPartView
    public IsWidget getWidget() {
        return widget;
    }

    @WorkbenchPartTitle
    public String getTitle() {
        return "Applications";
    }

    @Override
    public void onResize() {
        int height = widget.getParent().getOffsetHeight();
        int width = widget.getParent().getOffsetWidth();
        widget.setPixelSize(width, height);

    }
}
