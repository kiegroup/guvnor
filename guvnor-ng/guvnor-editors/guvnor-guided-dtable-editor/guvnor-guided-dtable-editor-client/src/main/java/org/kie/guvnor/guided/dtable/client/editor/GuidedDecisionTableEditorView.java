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

package org.kie.guvnor.guided.dtable.client.editor;

import com.google.gwt.event.shared.EventBus;
import com.google.gwt.event.shared.SimpleEventBus;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.VerticalPanel;
import org.kie.guvnor.commons.ui.client.resources.i18n.CommonConstants;
import org.kie.guvnor.datamodel.oracle.DataModelOracle;
import org.kie.guvnor.guided.dtable.client.widget.GuidedDecisionTableWidget;
import org.drools.guvnor.models.guided.dtable.shared.model.GuidedDecisionTable52;
import org.uberfire.backend.vfs.Path;
import org.uberfire.security.Identity;

import javax.inject.Inject;

public class GuidedDecisionTableEditorView
        extends Composite
        implements GuidedDecisionTableEditorPresenter.View {

    private final EventBus localBus = new SimpleEventBus();

    private final VerticalPanel panel = new VerticalPanel();
    private GuidedDecisionTable52 model;
    private GuidedDecisionTableWidget editor;

    @Inject
    private Identity identity;

    public GuidedDecisionTableEditorView() {
        panel.setWidth( "100%" );
        initWidget( panel );
    }

    @Override
    public void setContent( final Path path,
                            final DataModelOracle dataModel,
                            final GuidedDecisionTable52 model,
                            final boolean isReadOnly ) {
        this.model = model;
        this.editor = new GuidedDecisionTableWidget( path,
                                                     dataModel,
                                                     model,
                                                     localBus,
                                                     identity,
                                                     isReadOnly );
        panel.add( this.editor );
    }

    @Override
    public GuidedDecisionTable52 getContent() {
        return this.model;
    }

    @Override
    public boolean isDirty() {
        return false;
    }

    @Override
    public void setNotDirty() {
    }

    @Override
    public boolean confirmClose() {
        return Window.confirm( CommonConstants.INSTANCE.DiscardUnsavedData() );
    }
}
