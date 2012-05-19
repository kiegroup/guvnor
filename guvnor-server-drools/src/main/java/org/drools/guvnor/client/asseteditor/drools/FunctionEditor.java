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
import org.drools.guvnor.client.explorer.ClientFactory;
import org.drools.guvnor.client.rpc.Asset;

import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.ui.Composite;

public class FunctionEditor extends Composite
    implements
    EditorWidget {

    public FunctionEditor(Asset a,
                          RuleViewer v,
                          ClientFactory clientFactory,
                          EventBus eventBus) {
        this( a );
    }

    public FunctionEditor(Asset a) {
        final DefaultRuleContentWidget ed = new DefaultRuleContentWidget( a );

        initWidget( ed );

    }

}
