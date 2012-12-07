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
import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.TextBox;
import org.jboss.errai.ioc.client.container.IOCBeanDef;
import org.jboss.errai.ioc.client.container.IOCBeanManager;
import org.kie.guvnor.commons.ui.client.resources.i18n.NewItemPopupConstants;
import org.uberfire.backend.vfs.Path;
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
    private final SimplePanel extensionContainer = new SimplePanel();

    private Path activePath = null;
    private NewResourceHandler activeHandler = null;

    @PostConstruct
    private void setup() {
        //Title and mandatory details
        setTitle( NewItemPopupConstants.INSTANCE.popupTitle() );
        addAttribute( NewItemPopupConstants.INSTANCE.itemNameSubheading(),
                      itemNameTextBox );
        addAttribute( NewItemPopupConstants.INSTANCE.itemPathSubheading(),
                      itemPathLabel );

        //Register handlers
        addResourceHandlers();

        addAttribute( NewItemPopupConstants.INSTANCE.itemExtensionSubheading(),
                      extensionContainer );
        addAttribute( "",
                      makeOKButton() );
    }

    @Override
    public void show() {
        activePath = context.getActivePath();
        if ( activePath != null ) {
            itemPathLabel.setText( activePath.toURI() );
        } else {
            itemPathLabel.setText( NewItemPopupConstants.INSTANCE.itemUndefinedPath() );
        }
        super.show();
    }

    private void addResourceHandlers() {
        final Collection<IOCBeanDef<NewResourceHandler>> handlerBeans = iocBeanManager.lookupBeans( NewResourceHandler.class );
        for ( IOCBeanDef<NewResourceHandler> handlerBean : handlerBeans ) {
            final NewResourceHandler handler = handlerBean.getInstance();
            addResourceHandler( handler );
        }
    }

    private void addResourceHandler( final NewResourceHandler handler ) {
        addAttribute( "",
                      makeButton( handler ) );
    }

    private Button makeButton( final NewResourceHandler handler ) {
        final Button button = new Button( handler.getDescription() );
        button.addClickHandler( new ClickHandler() {

            @Override
            public void onClick( final ClickEvent event ) {
                activeHandler = handler;
                extensionContainer.clear();
                final IsWidget extensionWidget = handler.getExtension();
                if ( extensionWidget != null ) {
                    extensionContainer.setWidget( extensionWidget );
                }
                center();
            }

        } );
        return button;
    }

    private Button makeOKButton() {
        final Button okButton = new Button( NewItemPopupConstants.INSTANCE.OK() );
        okButton.addClickHandler( new ClickHandler() {
            @Override
            public void onClick( final ClickEvent event ) {
                if ( activeHandler != null ) {
                    activeHandler.create( activePath );
                }
                hide();
            }
        } );
        return okButton;
    }

}
