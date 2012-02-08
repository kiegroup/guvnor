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
package org.drools.guvnor.server.converters.decisiontable.builders;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.drools.decisiontable.parser.RuleSheetParserUtil;
import org.drools.ide.common.client.modeldriven.dt52.DTCellValue52;

/**
 * 
 */
public class SimpleValueBuilder
        extends
        ValueBuilder {

    private static final List<String> NO_PARAMETERS = Collections.emptyList();

    public SimpleValueBuilder(String template) {
        super( template );
    }

    @Override
    public List<String> extractParameters(String template) {
        return NO_PARAMETERS;
    }

    public List<DTCellValue52> build(String value) {
        List<DTCellValue52> values = new ArrayList<DTCellValue52>();
        values.add( new DTCellValue52( RuleSheetParserUtil.isStringMeaningTrue( value ) ) );
        return values;
    }

}
