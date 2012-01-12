/*
 * Copyright 2010 JBoss Inc
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

package org.drools.guvnor.client.asseteditor.drools;

import org.drools.guvnor.client.asseteditor.DefaultRuleContentWidget;
import org.drools.guvnor.client.asseteditor.EditorWidget;
import org.drools.guvnor.client.asseteditor.RuleViewer;
import org.drools.guvnor.client.asseteditor.drools.FactTypeBrowser.ClickEvent;
import org.drools.guvnor.client.explorer.ClientFactory;
import org.drools.guvnor.client.moduleeditor.drools.SuggestionCompletionCache;
import org.drools.guvnor.client.rpc.Asset;

import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;

public class DrlEditor extends Composite
    implements
    EditorWidget {

    public DrlEditor(Asset a,
                     RuleViewer v,
                     ClientFactory clientFactory,
                     EventBus eventBus) {
        this( a );
    }

    public DrlEditor(Asset a) {
        final DefaultRuleContentWidget ed = new DefaultRuleContentWidget( a,
                                                                          26 );

        ClickEvent ce = new FactTypeBrowser.ClickEvent() {
            public void selected(String text) {
                ed.insertText( text );
            }
        };

        Grid layout = new Grid( 1,
                                2 );

        FactTypeBrowser browser = new FactTypeBrowser( SuggestionCompletionCache.getInstance().getEngineFromCache( a.getMetaData().getModuleName() ),
                                                       ce );
        layout.setWidget( 0,
                          0,
                          browser );
        layout.setWidget( 0,
                          1,
                          ed );

        layout.getColumnFormatter().setWidth( 0,
                                              "10%" );
        layout.getColumnFormatter().setWidth( 1,
                                              "90%" );
        layout.getCellFormatter().setAlignment( 0,
                                                0,
                                                HasHorizontalAlignment.ALIGN_LEFT,
                                                HasVerticalAlignment.ALIGN_TOP );
        layout.getCellFormatter().setAlignment( 0,
                                                1,
                                                HasHorizontalAlignment.ALIGN_LEFT,
                                                HasVerticalAlignment.ALIGN_TOP );
        layout.setWidth( "95%" );

        initWidget( layout );

    }

}
