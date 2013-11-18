/*
 * Copyright 2011 JBoss Inc
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.guvnor.m2repo.client.editor;

import com.github.gwtbootstrap.client.ui.Modal;
import com.github.gwtbootstrap.client.ui.constants.BackdropType;
import com.google.gwt.core.client.GWT;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Widget;
import org.guvnor.m2repo.client.resources.i18n.M2RepoEditorConstants;
import org.uberfire.client.common.popups.footers.ModalFooterOKButton;

/**
 * A popup that shows an error message
 */
public class JarDetailPopup extends Modal {

    interface JarDetailPopupWidgetBinder
            extends
            UiBinder<Widget, JarDetailPopup> {

    }

    private static JarDetailPopupWidgetBinder uiBinder = GWT.create( JarDetailPopupWidgetBinder.class );

    @UiField
    protected HTML pom;

    public JarDetailPopup( final String details ) {
        setTitle( M2RepoEditorConstants.INSTANCE.JarDetails() );
        setMaxHeigth( ( Window.getClientHeight() * 0.75 ) + "px" );
        setBackdrop( BackdropType.STATIC );
        setKeyboard( true );
        setAnimation( true );
        setDynamicSafe( true );
        setHideOthers( false );

        add( uiBinder.createAndBindUi( this ) );
        add( new ModalFooterOKButton( new Command() {
            @Override
            public void execute() {
                hide();
            }
        } ) );

        this.pom.setHTML( new SafeHtmlBuilder().appendEscapedLines( details ).toSafeHtml() );
    }

}
