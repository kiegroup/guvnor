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

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RadioButton;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.TextBox;
import org.jboss.errai.ioc.client.container.IOCBeanDef;
import org.jboss.errai.ioc.client.container.IOCBeanManager;
import org.kie.guvnor.commons.ui.client.resources.ItemImages;
import org.kie.guvnor.commons.ui.client.resources.i18n.NewItemPopupConstants;
import org.uberfire.backend.vfs.Path;
import org.uberfire.backend.vfs.PathFactory;
import org.uberfire.client.common.FormStylePopup;
import org.uberfire.client.context.WorkbenchContext;

@ApplicationScoped
public class NewItemWidget extends FormStylePopup {

    @Inject
    private WorkbenchContext context;

    @Inject
    private IOCBeanManager iocBeanManager;

    private final TextBox itemNameTextBox = new TextBox();
    private final Label itemPathLabel = new Label();
    private final Button okButton = makeOKButton();

    private final SimplePanel extensionContainer = new SimplePanel();
    private final List<NewResourceHandler> handlers = new LinkedList<NewResourceHandler>();
    private final Map<NewResourceHandler, RadioButton> options = new HashMap<NewResourceHandler, RadioButton>();

    private Path activePath = null;
    private NewResourceHandler activeHandler = null;

    public NewItemWidget() {
        super( ItemImages.INSTANCE.newItem(),
               NewItemPopupConstants.INSTANCE.popupTitle() );
    }

    @PostConstruct
    private void setup() {
        //Mandatory details
        addAttribute( NewItemPopupConstants.INSTANCE.itemNameSubheading(),
                      itemNameTextBox );
        addAttribute( NewItemPopupConstants.INSTANCE.itemPathSubheading(),
                      itemPathLabel );

        //Register handlers
        addResourceHandlers();

        addAttribute( NewItemPopupConstants.INSTANCE.itemExtensionSubheading(),
                      extensionContainer );
        addAttribute( "",
                      okButton );
    }

    @Override
    public void show() {
        super.show();

        //Set active Path
        activePath = context.getActivePath();
        if ( activePath != null ) {
            itemPathLabel.setText( activePath.toURI() );
            okButton.setEnabled( true );
        } else {
            itemPathLabel.setText( NewItemPopupConstants.INSTANCE.itemUndefinedPath() );
            okButton.setEnabled( false );
        }
        if ( activeHandler == null ) {
            activeHandler = handlers.get( 0 );
        }

        //Select active Handler
        final RadioButton option = options.get( activeHandler );
        if ( option != null ) {
            option.setValue( true );
        }

    }

    public void setActiveHandler( final NewResourceHandler handler ) {
        activeHandler = handler;
    }

    private void addResourceHandlers() {
        final Collection<IOCBeanDef<NewResourceHandler>> handlerBeans = iocBeanManager.lookupBeans( NewResourceHandler.class );
        for ( IOCBeanDef<NewResourceHandler> handlerBean : handlerBeans ) {
            final NewResourceHandler handler = handlerBean.getInstance();
            addResourceHandler( handler );
        }
    }

    private void addResourceHandler( final NewResourceHandler handler ) {
        handlers.add( handler );
        final RadioButton option = makeOption( handler );
        options.put( handler,
                     option );
        addAttribute( "",
                      option );
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
        activeHandler = handler;
        extensionContainer.clear();
        final IsWidget extensionWidget = handler.getExtension();
        if ( extensionWidget != null ) {
            extensionContainer.setWidget( extensionWidget );
        }
    }

    private Button makeOKButton() {
        final Button okButton = new Button( NewItemPopupConstants.INSTANCE.OK() );
        okButton.addClickHandler( new ClickHandler() {
            @Override
            public void onClick( final ClickEvent event ) {
                if ( activeHandler != null ) {
                    activeHandler.create( buildFullPathName() );
                }
                hide();
            }
        } );
        return okButton;
    }

    private Path buildFullPathName() {
        final String pathName = this.activePath.toURI();
        final String fileName = this.itemNameTextBox.getText() + "." + this.activeHandler.getFileType();
        final Path assetPath = PathFactory.newPath( fileName,
                                                    pathName + "/" + fileName );
        return assetPath;
    }

}
