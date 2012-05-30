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

package org.drools.guvnor.client.asseteditor.drools.modeldriven.ui;

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
import org.drools.ide.common.client.modeldriven.brl.FromEntryPointFactPattern;
import org.drools.ide.common.client.modeldriven.brl.IAction;
import org.drools.ide.common.client.modeldriven.brl.IPattern;

import com.google.gwt.event.shared.EventBus;

public class RuleModellerWidgetFactory
    implements
    ModellerWidgetFactory {

    /*
     * (non-Javadoc)
     * @see
     * org.drools.guvnor.client.modeldriven.ui.ModellerWidgetFactory#getWidget
     * (org.drools.guvnor.client.modeldriven.ui.RuleModeller,
     * com.google.gwt.event.shared.EventBus,
     * org.drools.guvnor.client.modeldriven.brl.IAction, boolean )
     */
    public RuleModellerWidget getWidget(RuleModeller ruleModeller,
                                        EventBus eventBus,
                                        IAction action,
                                        Boolean readOnly) {
        if ( action instanceof ActionCallMethod ) {
            return new ActionCallMethodWidget( ruleModeller,
                                               eventBus,
                                               (ActionCallMethod) action,
                                               readOnly );
        }
        if ( action instanceof ActionSetField ) {
            return new ActionSetFieldWidget( ruleModeller,
                                             eventBus,
                                             (ActionSetField) action,
                                             readOnly );
        }
        if ( action instanceof ActionInsertFact ) {
            return new ActionInsertFactWidget( ruleModeller,
                                               eventBus,
                                               (ActionInsertFact) action,
                                               readOnly );
        }
        if ( action instanceof ActionRetractFact ) {
            return new ActionRetractFactWidget( ruleModeller,
                                                eventBus,
                                                (ActionRetractFact) action,
                                                readOnly );
        }
        if ( action instanceof DSLSentence ) {
            RuleModellerWidget w = new DSLSentenceWidget( ruleModeller,
                                                          eventBus,
                                                          (DSLSentence) action,
                                                          readOnly );
            w.addStyleName( "model-builderInner-Background" ); //NON-NLS
            return w;
        }
        if ( action instanceof FreeFormLine ) {
            return new FreeFormLineWidget( ruleModeller,
                                           eventBus,
                                           (FreeFormLine) action,
                                           readOnly );
        }
        if ( action instanceof ActionGlobalCollectionAdd ) {
            return new GlobalCollectionAddWidget( ruleModeller,
                                                  eventBus,
                                                  (ActionGlobalCollectionAdd) action,
                                                  readOnly );
        }
        throw new RuntimeException( "I don't know what type of action is: " + action ); //NON-NLS
    }

    /*
     * (non-Javadoc)
     * @see
     * org.drools.guvnor.client.modeldriven.ui.ModellerWidgetFactory#getWidget
     * (org.drools.guvnor.client.modeldriven.ui.RuleModeller,
     * com.google.gwt.event.shared.EventBus,
     * org.drools.guvnor.client.modeldriven.brl.IPattern, boolean)
     */
    public RuleModellerWidget getWidget(RuleModeller ruleModeller,
                                        EventBus eventBus,
                                        IPattern pattern,
                                        Boolean readOnly) {
        if ( pattern instanceof FactPattern ) {
            return new FactPatternWidget( ruleModeller,
                                          eventBus,
                                          pattern,
                                          true,
                                          readOnly );
        }
        if ( pattern instanceof CompositeFactPattern ) {
            return new CompositeFactPatternWidget( ruleModeller,
                                                   eventBus,
                                                   (CompositeFactPattern) pattern,
                                                   readOnly );
        }
        if ( pattern instanceof FromAccumulateCompositeFactPattern ) {
            return new FromAccumulateCompositeFactPatternWidget( ruleModeller,
                                                                 eventBus,
                                                                 (FromAccumulateCompositeFactPattern) pattern,
                                                                 readOnly );
        }
        if ( pattern instanceof FromCollectCompositeFactPattern ) {
            return new FromCollectCompositeFactPatternWidget( ruleModeller,
                                                              eventBus,
                                                              (FromCollectCompositeFactPattern) pattern,
                                                              readOnly );
        }
        if ( pattern instanceof FromEntryPointFactPattern ) {
            return new FromEntryPointFactPatternWidget( ruleModeller,
                                                        eventBus,
                                                        (FromEntryPointFactPattern) pattern,
                                                        readOnly );
        }
        if ( pattern instanceof FromCompositeFactPattern ) {
            return new FromCompositeFactPatternWidget( ruleModeller,
                                                       eventBus,
                                                       (FromCompositeFactPattern) pattern,
                                                       readOnly );
        }
        if ( pattern instanceof DSLSentence ) {
            return new DSLSentenceWidget( ruleModeller,
                                          eventBus,
                                          (DSLSentence) pattern,
                                          readOnly );
        }
        if ( pattern instanceof FreeFormLine ) {
            return new FreeFormLineWidget( ruleModeller,
                                           eventBus,
                                           (FreeFormLine) pattern,
                                           readOnly );
        }
        if ( pattern instanceof ExpressionFormLine ) {
            return new ExpressionBuilder( ruleModeller,
                                          eventBus,
                                          (ExpressionFormLine) pattern,
                                          readOnly );
        }
        throw new RuntimeException( "I don't know what type of pattern is: " + pattern );

    }

    public boolean isTemplate() {
        return false;
    }
}
