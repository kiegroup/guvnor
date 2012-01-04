/*
 * Copyright 2012 JBoss Inc
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.drools.ide.common.server.util;

import java.util.Map;

import org.drools.ide.common.client.modeldriven.brl.ActionFieldValue;
import org.drools.ide.common.client.modeldriven.brl.FieldConstraint;
import org.drools.ide.common.client.modeldriven.brl.IFactPattern;

/**
 * A specialised implementation of BRDELPersistence that can expand Template
 * Keys to values
 */
public class GuidedDTBRDRLPersistence extends BRDRLPersistence {

    private TemplateDataProvider rowDataProvider;

    public GuidedDTBRDRLPersistence(TemplateDataProvider rowDataProvider) {
        if ( rowDataProvider == null ) {
            throw new NullPointerException( "rowDataProvider cannot be null" );
        }
        this.rowDataProvider = rowDataProvider;
    }

    @Override
    protected LHSPatternVisitor getLHSPatternVisitor(boolean isDSLEnhanced,
                                                     StringBuilder buf,
                                                     String nestedIndentation,
                                                     boolean isNegated) {
        return new LHSPatternVisitor( isDSLEnhanced,
                                      rowDataProvider,
                                      bindingsPatterns,
                                      bindingsFields,
                                      buf,
                                      nestedIndentation,
                                      isNegated );
    }

    @Override
    protected RHSActionVisitor getRHSActionVisitor(boolean isDSLEnhanced,
                                                   StringBuilder buf,
                                                   String indentation) {
        return new RHSActionVisitor( isDSLEnhanced,
                                     rowDataProvider,
                                     bindingsPatterns,
                                     bindingsFields,
                                     buf,
                                     indentation );
    }

    //Substitutes Template Keys for values
    public static class LHSPatternVisitor extends BRDRLPersistence.LHSPatternVisitor {

        private TemplateDataProvider rowDataProvider;

        public LHSPatternVisitor(boolean isDSLEnhanced,
                                 TemplateDataProvider rowDataProvider,
                                 Map<String, IFactPattern> bindingsPatterns,
                                 Map<String, FieldConstraint> bindingsFields,
                                 StringBuilder b,
                                 String indentation,
                                 boolean isPatternNegated) {
            super( isDSLEnhanced,
                   bindingsPatterns,
                   bindingsFields,
                   b,
                   indentation,
                   isPatternNegated );
            this.rowDataProvider = rowDataProvider;
        }

        @Override
        protected void buildTemplateFieldValue(int type,
                                               String fieldType,
                                               String value,
                                               StringBuilder buf) {
            buf.append( " " );
            DRLConstraintValueBuilder.buildLHSFieldValue( buf,
                                                          type,
                                                          fieldType,
                                                          rowDataProvider.getTemplateKeyValue( value ) );
            buf.append( " " );
        }

    }

    //Substitutes Template Keys for values
    public static class RHSActionVisitor extends BRDRLPersistence.RHSActionVisitor {

        private TemplateDataProvider rowDataProvider;

        public RHSActionVisitor(boolean isDSLEnhanced,
                                TemplateDataProvider rowDataProvider,
                                Map<String, IFactPattern> bindingsPatterns,
                                Map<String, FieldConstraint> bindingsFields,
                                StringBuilder b,
                                String indentation) {
            super( isDSLEnhanced,
                   bindingsPatterns,
                   bindingsFields,
                   b,
                   indentation );
            this.rowDataProvider = rowDataProvider;
        }

        @Override
        protected void buildTemplateFieldValue(ActionFieldValue fieldValue,
                                               StringBuilder buf) {
            DRLConstraintValueBuilder.buildRHSFieldValue( buf,
                                                          fieldValue.type,
                                                          rowDataProvider.getTemplateKeyValue( fieldValue.value ) );
        }

    }

}
