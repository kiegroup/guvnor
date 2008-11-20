package org.drools.guvnor.client.ruleeditor;

import org.drools.guvnor.client.rpc.RuleAsset;

import com.google.gwt.user.client.ui.Composite;

public class FunctionEditor extends Composite {

    public FunctionEditor(RuleAsset a, RuleViewer v){
        this(a);
    }

    public FunctionEditor(RuleAsset a) {
		final DefaultRuleContentWidget ed = new DefaultRuleContentWidget(a);

		initWidget(ed);

	}

}
