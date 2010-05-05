package org.drools.guvnor.client.modeldriven.ui;

import org.drools.ide.common.client.modeldriven.brl.ActionCallMethod;
import org.drools.ide.common.client.modeldriven.brl.ActionGlobalCollectionAdd;
import org.drools.ide.common.client.modeldriven.brl.ActionInsertFact;
import org.drools.ide.common.client.modeldriven.brl.ActionRetractFact;
import org.drools.ide.common.client.modeldriven.brl.ActionSetField;
import org.drools.ide.common.client.modeldriven.brl.CompositeFactPattern;
import org.drools.ide.common.client.modeldriven.brl.DSLSentence;
import org.drools.ide.common.client.modeldriven.brl.ExpressionFormLine;
import org.drools.ide.common.client.modeldriven.brl.FactPattern;
import org.drools.ide.common.client.modeldriven.brl.FreeFormLine;
import org.drools.ide.common.client.modeldriven.brl.FromAccumulateCompositeFactPattern;
import org.drools.ide.common.client.modeldriven.brl.FromCollectCompositeFactPattern;
import org.drools.ide.common.client.modeldriven.brl.FromCompositeFactPattern;
import org.drools.ide.common.client.modeldriven.brl.IAction;
import org.drools.ide.common.client.modeldriven.brl.IPattern;

public class RuleModellerWidgetFactory implements ModellerWidgetFactory {
	
	/* (non-Javadoc)
	 * @see org.drools.guvnor.client.modeldriven.ui.ModellerWidgetFactory#getWidget(org.drools.guvnor.client.modeldriven.ui.RuleModeller, org.drools.guvnor.client.modeldriven.brl.IAction, boolean)
	 */
	public RuleModellerWidget getWidget(RuleModeller ruleModeller, IAction action, Boolean readOnly){
        if (action instanceof ActionCallMethod) {
            return new ActionCallMethodWidget(ruleModeller, (ActionCallMethod) action, readOnly);
        }
        if (action instanceof ActionSetField) {
            return new ActionSetFieldWidget(ruleModeller, (ActionSetField) action,readOnly);
        } 
        if (action instanceof ActionInsertFact) {
            return new ActionInsertFactWidget(ruleModeller, (ActionInsertFact) action, readOnly);
        } 
        if (action instanceof ActionRetractFact) {
            return new ActionRetractFactWidget(ruleModeller, (ActionRetractFact) action, readOnly);
        }
        if (action instanceof DSLSentence) {
        	RuleModellerWidget w = new DSLSentenceWidget(ruleModeller,(DSLSentence) action, readOnly);
            w.addStyleName("model-builderInner-Background"); //NON-NLS
            return w;
        } 
        if (action instanceof FreeFormLine) {
            return new FreeFormLineWidget(ruleModeller, (FreeFormLine) action, readOnly);
        } 
        if (action instanceof ActionGlobalCollectionAdd) {
             return new GlobalCollectionAddWidget(ruleModeller, (ActionGlobalCollectionAdd) action, readOnly);
        } 
        throw new RuntimeException("I don't know what type of action is: " + action); //NON-NLS
	}
	
	/* (non-Javadoc)
	 * @see org.drools.guvnor.client.modeldriven.ui.ModellerWidgetFactory#getWidget(org.drools.guvnor.client.modeldriven.ui.RuleModeller, org.drools.guvnor.client.modeldriven.brl.IPattern, boolean)
	 */
	public RuleModellerWidget getWidget(RuleModeller ruleModeller, IPattern pattern, Boolean readOnly){
        if (pattern instanceof FactPattern) {
            return new FactPatternWidget(ruleModeller, pattern, true, readOnly);
        } 
        if (pattern instanceof CompositeFactPattern) {
            return new CompositeFactPatternWidget(ruleModeller, (CompositeFactPattern) pattern, readOnly);
        }
        if (pattern instanceof FromAccumulateCompositeFactPattern) {
            return new FromAccumulateCompositeFactPatternWidget(ruleModeller, (FromAccumulateCompositeFactPattern) pattern, readOnly);
        }
        if (pattern instanceof FromCollectCompositeFactPattern) {
            return new FromCollectCompositeFactPatternWidget(ruleModeller, (FromCollectCompositeFactPattern) pattern, readOnly);
        }
        if (pattern instanceof FromCompositeFactPattern) {
            return new FromCompositeFactPatternWidget(ruleModeller, (FromCompositeFactPattern) pattern, readOnly);
        }
        if (pattern instanceof DSLSentence) {
            return new DSLSentenceWidget(ruleModeller,(DSLSentence) pattern, readOnly);
        }
        if (pattern instanceof FreeFormLine) {
            return new FreeFormLineWidget(ruleModeller, (FreeFormLine)pattern, readOnly);
        }
        if (pattern instanceof ExpressionFormLine) {
            return new ExpressionBuilder(ruleModeller, (ExpressionFormLine) pattern, readOnly);
        }
        throw new RuntimeException("I don't know what type of pattern is: "+pattern);

	}

	public boolean isTemplate(){
		return false;
	}
}
