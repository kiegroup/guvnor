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
import javax.inject.Inject;

import com.google.gwt.user.client.ui.IsWidget;
import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.annotations.WorkbenchPartView;
import org.uberfire.client.annotations.WorkbenchScreen;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.lifecycle.OnStartup;

@Dependent
@WorkbenchScreen(identifier = "defaultLHSMenu")
public class LHSMenuPresenter
        implements LHSMenuView.Presenter {

    private LHSMenuView view;

    private PlaceManager placeManager;

    public LHSMenuPresenter() {
    }

    @Inject
    public LHSMenuPresenter(LHSMenuView view,PlaceManager placeManager) {
        this.view = view;
        this.placeManager = placeManager;
        view.setPresenter(this);
    }

    @OnStartup
    public void onStartup() {
    }

    @Override
    public void goTo(String screenName) {
        placeManager.goTo(screenName);
    }

    @WorkbenchPartView
    public IsWidget getWidget() {
        return view;
    }

    @WorkbenchPartTitle
    public String getTitle() {
        return "";
    }

}
