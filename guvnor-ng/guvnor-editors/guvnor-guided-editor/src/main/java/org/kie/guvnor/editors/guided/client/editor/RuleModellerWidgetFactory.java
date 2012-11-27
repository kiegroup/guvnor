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

package org.kie.guvnor.editors.guided.client.editor;

import com.google.web.bindery.event.shared.EventBus;
import org.kie.guvnor.datamodel.api.shared.DSLSentence;
import org.kie.guvnor.datamodel.api.shared.IAction;
import org.kie.guvnor.datamodel.api.shared.IPattern;
import org.kie.guvnor.editors.guided.client.widget.ActionCallMethodWidget;
import org.kie.guvnor.editors.guided.client.widget.ActionRetractFactWidget;
import org.kie.guvnor.editors.guided.client.widget.ActionSetFieldWidget;
import org.kie.guvnor.editors.guided.client.widget.ExpressionBuilder;
import org.kie.guvnor.editors.guided.client.widget.FreeFormLineWidget;
import org.kie.guvnor.editors.guided.client.widget.FromAccumulateCompositeFactPatternWidget;
import org.kie.guvnor.editors.guided.client.widget.FromCompositeFactPatternWidget;
import org.kie.guvnor.editors.guided.model.ActionCallMethod;
import org.kie.guvnor.editors.guided.model.ActionGlobalCollectionAdd;
import org.kie.guvnor.editors.guided.model.ActionRetractFact;
import org.kie.guvnor.editors.guided.model.ActionSetField;
import org.kie.guvnor.editors.guided.model.CompositeFactPattern;
import org.kie.guvnor.editors.guided.model.ExpressionFormLine;
import org.kie.guvnor.editors.guided.model.FromCollectCompositeFactPattern;
import org.kie.guvnor.editors.guided.model.FromCompositeFactPattern;
import org.kie.guvnor.editors.guided.client.widget.ActionInsertFactWidget;
import org.kie.guvnor.editors.guided.client.widget.CompositeFactPatternWidget;
import org.kie.guvnor.editors.guided.client.widget.DSLSentenceWidget;
import org.kie.guvnor.editors.guided.client.widget.FactPatternWidget;
import org.kie.guvnor.editors.guided.client.widget.FromCollectCompositeFactPatternWidget;
import org.kie.guvnor.editors.guided.client.widget.FromEntryPointFactPatternWidget;
import org.kie.guvnor.editors.guided.client.widget.GlobalCollectionAddWidget;
import org.kie.guvnor.editors.guided.client.widget.RuleModellerWidget;
import org.kie.guvnor.editors.guided.model.ActionInsertFact;
import org.kie.guvnor.editors.guided.model.FactPattern;
import org.kie.guvnor.editors.guided.model.FreeFormLine;
import org.kie.guvnor.editors.guided.model.FromAccumulateCompositeFactPattern;
import org.kie.guvnor.editors.guided.model.FromEntryPointFactPattern;

public class RuleModellerWidgetFactory
        implements
        ModellerWidgetFactory {

    /*
     * (non-Javadoc)
     * @see
     * org.drools.guvnor.client.modeldriven.ui.ModellerWidgetFactory#getWidget
     * (org.drools.guvnor.client.modeldriven.ui.RuleModeller,
     * com.google.web.bindery.event.shared.EventBus,
     * org.drools.guvnor.client.modeldriven.brl.IAction, boolean )
     */
    public RuleModellerWidget getWidget( RuleModeller ruleModeller,
                                         EventBus eventBus,
                                         IAction action,
                                         Boolean readOnly ) {
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
     * com.google.web.bindery.event.shared.EventBus,
     * org.drools.guvnor.client.modeldriven.brl.IPattern, boolean)
     */
    public RuleModellerWidget getWidget( RuleModeller ruleModeller,
                                         EventBus eventBus,
                                         IPattern pattern,
                                         Boolean readOnly ) {
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
