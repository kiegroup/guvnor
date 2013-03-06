/*
 * Copyright 2012 JBoss Inc
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

package org.kie.guvnor.commons.ui.client.widget;

import com.github.gwtbootstrap.client.ui.Modal;
import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

public class LoadingView extends Composite {

    interface LoadingViewBinder
            extends
            UiBinder<Widget, LoadingView> {

    }

    private static LoadingViewBinder uiBinder = GWT.create( LoadingViewBinder.class );

    @UiField
    Modal popup;

    @UiField
    Label message;

    private static final LoadingView INSTANCE = new LoadingView();

    private LoadingView() {
        initWidget( uiBinder.createAndBindUi( this ) );
        popup.setDynamicSafe( true );
        popup.setAnimation( false );
    }

    public static void show( final String message ) {
        INSTANCE.message.setText( message );
        INSTANCE.popup.show();
    }

    public static void hide() {
        INSTANCE.popup.hide();
    }

}
