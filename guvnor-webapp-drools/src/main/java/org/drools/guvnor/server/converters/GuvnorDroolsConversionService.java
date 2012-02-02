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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.drools.guvnor.client.common.AssetFormats;
import org.drools.guvnor.client.rpc.ConversionResult;
import org.drools.guvnor.client.rpc.ConversionResultNoConverter;
import org.drools.repository.AssetItem;

/**
 * A ConversionService implementation for Drools assets
 */
@ApplicationScoped
public class GuvnorDroolsConversionService
    implements
    ConversionService {

    @Inject
    private DecisionTableXLSToDecisionTableGuidedConverter decisionTableXLSToDecisionTableGuidedConverter;

    private final Map<String, List<AbstractConverter>>     CONVERTERS  = new HashMap<String, List<AbstractConverter>>();

    private final ConversionResult                         NULL_RESULT = new ConversionResultNoConverter();

    @PostConstruct
    public void registration() {
        //Add Converters specific to Drools
        register( AssetFormats.DECISION_SPREADSHEET_XLS,
                  decisionTableXLSToDecisionTableGuidedConverter );
    }

    public void register(String sourceFormat,
                         AbstractConverter converter) {
        List<AbstractConverter> registeredConverters = CONVERTERS.get( sourceFormat );
        if ( registeredConverters == null ) {
            registeredConverters = new ArrayList<AbstractConverter>();
            CONVERTERS.put( sourceFormat,
                            registeredConverters );
        }
        registeredConverters.add( converter );
    }

    public ConversionResult convert(AssetItem item,
                                    String targetFormat) {
        final String sourceFormat = item.getFormat();
        if ( !CONVERTERS.containsKey( sourceFormat ) ) {
            return NULL_RESULT;
        }
        for ( AbstractConverter converter : CONVERTERS.get( sourceFormat ) ) {
            if ( converter.isTargetFormatSupported( targetFormat ) ) {
                return converter.convert( item );
            }
        }
        return NULL_RESULT;
    }

}
