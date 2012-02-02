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
package org.drools.guvnor.server.converters;

import javax.enterprise.inject.Specializes;
import javax.inject.Inject;

import org.drools.guvnor.client.common.AssetFormats;

/**
 * A ConversionService implementation for Drools assets
 */
@Specializes
public class GuvnorDroolsConversionService extends GuvnorCoreConversionService
    implements
    ConversionService {

    @Inject
    private DecisionTableXLSToDecisionTableGuidedConverter decisionTableXLSToDecisionTableGuidedConverter;

    @Override
    public void registration() {
        //Add Converters in the core service
        super.registration();

        //Add Converters specific to Drools
        register( AssetFormats.DECISION_SPREADSHEET_XLS,
                  decisionTableXLSToDecisionTableGuidedConverter );
    }

}
