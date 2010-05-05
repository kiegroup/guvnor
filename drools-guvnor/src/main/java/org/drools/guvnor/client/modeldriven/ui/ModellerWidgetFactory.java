package org.drools.guvnor.client.modeldriven.ui;

import org.drools.ide.common.client.modeldriven.brl.IAction;
import org.drools.ide.common.client.modeldriven.brl.IPattern;

public interface ModellerWidgetFactory {

	/**
	 * Used for get widgets for RHS
	 * @param ruleModeller
	 * @param action
	 * @param readOnly
	 * @return
	 */
	public RuleModellerWidget getWidget(RuleModeller ruleModeller,
			IAction action, Boolean readOnly);

	/**
	 * Used for get widgets for LHS
	 * @param ruleModeller
	 * @param pattern
	 * @param readOnly
	 * @return
	 */
	public RuleModellerWidget getWidget(RuleModeller ruleModeller,
			IPattern pattern, Boolean readOnly);
	
	public boolean isTemplate();

}