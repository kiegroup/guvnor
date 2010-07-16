/**
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

package org.drools.guvnor.client.ruleeditor;

import org.drools.guvnor.client.packages.SuggestionCompletionCache;
import org.drools.guvnor.client.rpc.RuleAsset;
import org.drools.guvnor.client.ruleeditor.FactTypeBrowser.ClickEvent;

import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;

public class DrlEditor extends Composite {

    public DrlEditor(RuleAsset a, RuleViewer v){
        this(a);
    }

    public DrlEditor(RuleAsset a) {
		final DefaultRuleContentWidget ed = new DefaultRuleContentWidget(a, 26);

		ClickEvent ce = new FactTypeBrowser.ClickEvent() {
			public void selected(String text) {
				ed.insertText(text);
			}
		};

		Grid layout = new Grid(1, 2);


		FactTypeBrowser browser =  new FactTypeBrowser(SuggestionCompletionCache.getInstance().getEngineFromCache(a.metaData.packageName), ce);
		layout.setWidget(0, 0, browser);
		layout.setWidget(0, 1, ed);

		layout.getColumnFormatter().setWidth(0, "10%");
		layout.getColumnFormatter().setWidth(1, "90%");
		layout.getCellFormatter().setAlignment(0, 0, HasHorizontalAlignment.ALIGN_LEFT, HasVerticalAlignment.ALIGN_TOP);
		layout.getCellFormatter().setAlignment(0, 1, HasHorizontalAlignment.ALIGN_LEFT, HasVerticalAlignment.ALIGN_TOP);
		layout.setWidth("100%");


		initWidget(layout);

	}

}
