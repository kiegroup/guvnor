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

import java.util.HashMap;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RadioButton;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import org.kie.guvnor.commons.ui.client.resources.ItemImages;
import org.kie.guvnor.commons.ui.client.resources.i18n.CommonConstants;
import org.kie.guvnor.commons.ui.client.resources.i18n.NewItemPopupConstants;
import org.uberfire.backend.vfs.Path;
import org.uberfire.client.common.FormStyleLayout;
import org.uberfire.client.common.FormStylePopup;

@ApplicationScoped
public class NewItemView extends FormStylePopup implements NewItemPresenter.View {

    private NewItemPresenter presenter;

    private final TextBox itemNameTextBox = new TextBox();
    private final Label itemPathLabel = new Label();
    private final Button okButton = makeOKButton();

    private final VerticalPanel handlersContainer = new VerticalPanel();
    private final FormStyleLayout extensionContainer = new FormStyleLayout();
    private final Map<NewResourceHandler, RadioButton> options = new HashMap<NewResourceHandler, RadioButton>();

    public NewItemView() {
        super( ItemImages.INSTANCE.newItem(),
               NewItemPopupConstants.INSTANCE.popupTitle() );
    }

    @Override
    public void init( final NewItemPresenter presenter ) {
        this.presenter = presenter;
    }

    @PostConstruct
    private void setup() {
        //Mandatory details
        addAttribute( NewItemPopupConstants.INSTANCE.itemNameSubheading(),
                      itemNameTextBox );
        addAttribute( CommonConstants.INSTANCE.ItemPathSubheading(),
                      itemPathLabel );

        //Handlers
        addAttribute( "",
                      handlersContainer );

        //Extensions
        addRow( extensionContainer );

        //OK button
        addAttribute( "",
                      okButton );
    }

    @Override
    public void show() {
        //Clear previous resource name
        itemNameTextBox.setText( "" );
        super.show();
    }

    @Override
    public void hide() {
        super.hide();
    }

    @Override
    public void setActivePath( final Path path ) {
        if ( path == null ) {
            itemPathLabel.setText( CommonConstants.INSTANCE.ItemUndefinedPath() );
            okButton.setEnabled( false );
        } else {
            itemPathLabel.setText( path.toURI() );
            okButton.setEnabled( true );
        }
    }

    @Override
    public void setActiveHandler( final NewResourceHandler handler ) {
        final RadioButton option = options.get( handler );
        if ( option != null ) {
            option.setValue( true );
        }
    }

    @Override
    public void addHandler( final NewResourceHandler handler ) {
        final RadioButton option = makeOption( handler );
        options.put( handler,
                     option );
        handlersContainer.add( option );
    }

    @Override
    public String getItemName() {
        return itemNameTextBox.getText();
    }

    @Override
    public void showMissingNameError() {
        Window.alert( NewItemPopupConstants.INSTANCE.MissingName() );
    }

    @Override
    public void showMissingPathError() {
        Window.alert( CommonConstants.INSTANCE.MissingPath() );
    }

    @Override
    public void addExtensionWidget( final String caption,
                                    final IsWidget widget ) {
        extensionContainer.addAttribute( caption,
                                         widget );
    }

    private RadioButton makeOption( final NewResourceHandler handler ) {
        final RadioButton option = new RadioButton( "options",
                                                    handler.getDescription() );
        option.addClickHandler( new ClickHandler() {

            @Override
            public void onClick( final ClickEvent event ) {
                selectNewResourceHandler( handler );
                center();
            }

        } );
        return option;
    }

    private void selectNewResourceHandler( final NewResourceHandler handler ) {
        extensionContainer.clear();
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
