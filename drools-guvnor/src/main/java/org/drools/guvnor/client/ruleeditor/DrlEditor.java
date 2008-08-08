package org.drools.guvnor.client.ruleeditor;

import org.drools.guvnor.client.packages.SuggestionCompletionCache;
import org.drools.guvnor.client.rpc.RuleAsset;
import org.drools.guvnor.client.ruleeditor.FactTypeBrowser.ClickEvent;

import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;

public class DrlEditor extends Composite {

	public DrlEditor(RuleAsset a) {
		final DefaultRuleContentWidget ed = new DefaultRuleContentWidget(a);

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
