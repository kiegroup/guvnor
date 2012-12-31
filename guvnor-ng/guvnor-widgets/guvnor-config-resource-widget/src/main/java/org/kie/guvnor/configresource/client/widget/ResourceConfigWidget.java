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

package org.kie.guvnor.configresource.client.widget;

import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import org.kie.guvnor.configresource.client.resources.i18n.Constants;
import org.kie.guvnor.services.config.model.ResourceConfig;
import org.uberfire.client.common.DecoratedDisclosurePanel;
import org.uberfire.client.common.FormStyleLayout;

import static org.kie.commons.validation.PortablePreconditions.*;

/**
 * This displays the metadata for a versionable artifact. It also captures
 * edits, but it does not load or save anything itself.
 */
public class ResourceConfigWidget extends Composite {

    private ResourceConfig config = null;
    private boolean readOnly;
    private VerticalPanel layout = new VerticalPanel();

    private FormStyleLayout currentSection;
    private String          currentSectionName;

    public ResourceConfigWidget() {
        layout.setWidth( "100%" );
        initWidget( layout );
    }

    public void setContent( final ResourceConfig config,
                            final boolean readOnly ) {
        this.config = checkNotNull( "config", config );
        this.readOnly = readOnly;

        layout.clear();

        loadData();
    }

    private void loadData() {
        startSection( Constants.INSTANCE.ImportsSection() );

        addRow( new ImportsWidget( config, readOnly ) );

        endSection( false );
    }

    private void addRow( Widget widget ) {
        this.currentSection.addRow( widget );
    }

    private void endSection( final boolean collapsed ) {
        final DecoratedDisclosurePanel advancedDisclosure = new DecoratedDisclosurePanel( currentSectionName );
        advancedDisclosure.setWidth( "100%" );
        advancedDisclosure.setOpen( !collapsed );
        advancedDisclosure.setContent( this.currentSection );
        layout.add( advancedDisclosure );
    }

    private void startSection( final String name ) {
        currentSection = new FormStyleLayout();
        currentSectionName = name;
    }

    /**
     * Return the data if it is to be saved.
     */
    public ResourceConfig getContent() {
        return config;
    }
}
