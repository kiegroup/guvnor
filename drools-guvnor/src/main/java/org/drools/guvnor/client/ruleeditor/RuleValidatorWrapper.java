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

package org.drools.guvnor.client.ruleeditor;

import org.drools.guvnor.client.common.DirtyableComposite;
import org.drools.guvnor.client.common.FormStylePopup;
import org.drools.guvnor.client.common.LoadingPopup;
import org.drools.guvnor.client.common.SmallLabel;
import org.drools.guvnor.client.messages.Constants;
import org.drools.guvnor.client.resources.Images;
import org.drools.guvnor.client.rpc.BuilderResult;
import org.drools.guvnor.client.rpc.BuilderResultLine;
import org.drools.guvnor.client.util.Format;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * This widget wraps a rule asset widget, and provides actions to validate and view source.
 * @author Michael Neale
 */
public class RuleValidatorWrapper extends DirtyableComposite
    implements
    SaveEventListener,
    EditorWidget {

    private static Constants constants = GWT.create( Constants.class );
    private static Images    images    = GWT.create( Images.class );

    private VerticalPanel    layout    = new VerticalPanel();
    private Widget           editor;

    public RuleValidatorWrapper(Widget editor) {
        this.editor = editor;

        layout.add( editor );

        layout.setWidth( "100%" );
        layout.setHeight( "100%" );

        initWidget( layout );
    }

    /**
     * This will show a popup of error messages in compilation.
     */
    public static void showBuilderErrors(BuilderResult result) {

        if ( result == null || result.lines == null || result.lines.length == 0 ) {
            FormStylePopup pop = new FormStylePopup();
            pop.setWidth( 200 + "px" );
            pop.setTitle( constants.ValidationResultsDotDot() );
            HorizontalPanel h = new HorizontalPanel();
            h.add( new SmallLabel( Format.format( "<img src='{0}'/><i>{1}</i>",
                                                  new Image( images.greenTick() ).getUrl(),
                                                  constants.ItemValidatedSuccessfully() ) ) ); //NON-NLS

            pop.addRow( h );
            pop.show();
        } else {
            FormStylePopup pop = new FormStylePopup( images.packageBuilder(),
                                                     constants.ValidationResults() );
            FlexTable errTable = new FlexTable();
            errTable.setStyleName( "build-Results" ); //NON-NLS
            for ( int i = 0; i < result.lines.length; i++ ) {
                int row = i;
                final BuilderResultLine res = result.lines[i];
                errTable.setWidget( row,
                                    0,
                                    new Image( images.error() ) );
                if ( res.assetFormat.equals( "package" ) ) {
                    errTable.setText( row,
                                      1,
                                      constants.packageConfigurationProblem() + res.message );
                } else {
                    errTable.setText( row,
                                      1,
                                      "[" + res.assetName + "] " + res.message );
                }

            }
            ScrollPanel scroll = new ScrollPanel( errTable );
            scroll.setWidth( "100%" );
            pop.addRow( scroll );
            pop.show();
        }

        LoadingPopup.close();
    }

    public void onSave() {
        if ( editor instanceof SaveEventListener ) {
            SaveEventListener el = (SaveEventListener) editor;
            el.onSave();
        }
    }

    public void onAfterSave() {
        if ( editor instanceof SaveEventListener ) {
            SaveEventListener el = (SaveEventListener) editor;
            el.onAfterSave();
        }
    }
}