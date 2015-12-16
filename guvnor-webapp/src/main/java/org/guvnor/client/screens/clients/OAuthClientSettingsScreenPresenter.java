/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/

package org.guvnor.client.screens.clients;

import javax.enterprise.context.Dependent;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;
import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.annotations.WorkbenchPartView;
import org.uberfire.client.annotations.WorkbenchScreen;

@Dependent
@WorkbenchScreen(identifier = "oauthClientSettingsScreen")
public class OAuthClientSettingsScreenPresenter
        extends Composite {

    interface Binder
            extends
            UiBinder<Widget, OAuthClientSettingsScreenPresenter> {

    }

    private static Binder uiBinder = GWT.create(Binder.class);

    public OAuthClientSettingsScreenPresenter() {
        initWidget(uiBinder.createAndBindUi(this));
    }

    @WorkbenchPartView
    public Widget getWidget() {
        return this;
    }

    @WorkbenchPartTitle
    public String getTitle() {
        return "Settings";
    }

}
