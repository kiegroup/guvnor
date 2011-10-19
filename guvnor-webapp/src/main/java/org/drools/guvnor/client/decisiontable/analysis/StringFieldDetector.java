/*
 * Copyright 2011 JBoss Inc
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

package org.drools.guvnor.client.decisiontable.analysis;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public class StringFieldDetector extends FieldDetector<StringFieldDetector> {

    private List<String> allowedValueList = null;
    private List<String> disallowedList = new ArrayList<String>(1);

    public StringFieldDetector(String value, String operator) {
        if (operator.equals("==")) {
            allowedValueList = new ArrayList<String>(1);
            allowedValueList.add(value);
        } else if (operator.equals("!=")) {
            disallowedList.add(value);
        } else if (operator.equals("in")) {
            String[] tokens = value.split(",");
            allowedValueList = new ArrayList<String>(tokens.length);
            Collections.addAll(allowedValueList, tokens);
        } else {
            hasUnrecognizedConstraint = true;
        }
    }

    public StringFieldDetector(StringFieldDetector a, StringFieldDetector b) {
        super(a, b);
        if (b.allowedValueList == null) {
            allowedValueList = a.allowedValueList;
        } else if (a.allowedValueList == null) {
            allowedValueList = b.allowedValueList;
        } else {
            allowedValueList = new ArrayList<String>(a.allowedValueList);
            allowedValueList.retainAll(b.allowedValueList);
        }
        disallowedList.addAll(a.disallowedList);
        disallowedList.addAll(b.disallowedList);
        optimizeAllowedValueList();
        detectImpossibleMatch();
    }

    private void optimizeAllowedValueList() {
        if (allowedValueList != null) {
            allowedValueList.removeAll(disallowedList);
            disallowedList.clear();
        }
    }

    private void detectImpossibleMatch() {
        if (allowedValueList != null && allowedValueList.isEmpty()) {
            impossibleMatch = true;
        }
    }

    public StringFieldDetector merge(StringFieldDetector other) {
        return new StringFieldDetector(this, other);
    }

}
