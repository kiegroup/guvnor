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

package org.kie.guvnor.commons.ui.client.handlers;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.RadioButton;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import org.kie.commons.data.Pair;
import org.kie.guvnor.commons.ui.client.resources.CommonsResources;
import org.kie.guvnor.commons.ui.client.resources.i18n.NewItemPopupConstants;
import org.uberfire.client.common.FormStylePopup;

import javax.enterprise.context.ApplicationScoped;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@ApplicationScoped
public class NewResourceView extends FormStylePopup implements NewResourcePresenter.View {

    private NewResourcePresenter presenter;

    private final TextBox fileNameTextBox = new TextBox();
    private final VerticalPanel handlersContainer = new VerticalPanel();
    private final Map<NewResourceHandler, RadioButton> handlers = new HashMap<NewResourceHandler, RadioButton>();
    private final Button okButton = makeOKButton();

    @Override
    public void init( final NewResourcePresenter presenter ) {
        this.presenter = presenter;
    }

    @Override
    public void show() {
        //Clear previous resource name
        fileNameTextBox.setText( "" );
        super.show();
    }

    @Override
    public void hide() {
        super.hide();
    }

    @Override
    public void setActiveHandler( final NewResourceHandler handler ) {
        //Render entire panel contents (this assures we can re-use the same underlying FormStyleLayout
        clear();
        addAttribute( NewItemPopupConstants.INSTANCE.itemNameSubheading(),
                      fileNameTextBox );
        addAttribute( "",
                      handlersContainer );

        //Extensions for Handler
        final List<Pair<String, ? extends IsWidget>> extensions = handler.getExtensions();
        if ( extensions != null ) {
            for ( Pair<String, ? extends IsWidget> extension : extensions ) {
                addAttribute( extension.getK1(),
                              extension.getK2() );
            }
        }

        addAttribute( "",
                      okButton );

        //Select handler
        final RadioButton option = handlers.get( handler );
        if ( option != null ) {
            option.setValue( true,
                             true );
        }

    }

    @Override
    public void addHandler( final NewResourceHandler handler ) {
        final RadioButton option = makeOption( handler );
        handlers.put( handler,
                      option );
        handlersContainer.add( option );
    }

    @Override
    public String getFileName() {
        return fileNameTextBox.getText();
    }

    @Override
    public void showMissingNameError() {
        Window.alert( NewItemPopupConstants.INSTANCE.MissingName() );
    }

    @Override
    public void enableHandler( final NewResourceHandler handler,
                               final boolean enable ) {
        final RadioButton handlerOption = this.handlers.get( handler );
        if ( handlerOption == null ) {
            return;
        }
        handlerOption.setEnabled( enable );
    }

    private RadioButton makeOption( final NewResourceHandler handler ) {
        final RadioButton option = new RadioButton( "handlers",
                                                    handler.getDescription() );
        option.setStyleName( CommonsResources.INSTANCE.css().newHandlerOption() );
        option.addValueChangeHandler( new ValueChangeHandler<Boolean>() {

            @Override
            public void onValueChange( ValueChangeEvent<Boolean> event ) {
                if ( event.getValue() == true ) {
                    selectNewResourceHandler( handler );
                    center();
                }
            }
        } );

        return option;
    }

    private void selectNewResourceHandler( final NewResourceHandler handler ) {
        setActiveHandler( handler );
        presenter.setActiveHandler( handler );
    }

    private Button makeOKButton() {
        final Button okButton = new Button( NewItemPopupConstants.INSTANCE.OK() );
        okButton.addClickHandler( new ClickHandler() {
            @Override
            public void onClick( final ClickEvent event ) {
                presenter.makeItem();
            }
        } );
        return okButton;
    }

}
