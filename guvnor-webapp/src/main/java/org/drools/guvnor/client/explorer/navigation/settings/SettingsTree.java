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

package org.drools.guvnor.client.explorer.navigation.settings;

import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;

public class SettingsTree implements IsWidget, SettingsTreeView.Presenter {

    private SettingsTreeView view;
    private PlaceController placeController;

    public SettingsTree(SettingsTreeView view, PlaceController placeController) {
        view.setPresenter(this);
        this.view = view;
        this.placeController = placeController;
    }

    public Widget asWidget() {
        return view.asWidget();
    }

    public void onPreferencesSelected() {
        goTo(new PreferencesPlace());
    }

    public void onSystemSelected() {
        goTo(new SystemPlace());
    }

    private void goTo(Place place) {
        placeController.goTo(place);
    }
}
