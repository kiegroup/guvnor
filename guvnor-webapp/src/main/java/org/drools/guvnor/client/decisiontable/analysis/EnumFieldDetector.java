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
import java.util.List;

public class EnumFieldDetector extends FieldDetector<EnumFieldDetector> {

    private final List<String> allowedValueList = new ArrayList<String>();

    public EnumFieldDetector(List<String> allValueList, String value, String operator) {
        if (operator.equals("==")) {
            if (allValueList.contains(value)) {
                allowedValueList.add(value);
            } else {
                System.out.println("Warning: value (" + value + ") is not a valid enum value (" + allValueList + ").");
            }
        } else if (operator.equals("!=")) {
            allowedValueList.addAll(allValueList);
            allowedValueList.remove(value);
        } else if (operator.equals("in")) {
            String[] tokens = value.split(",");
            for (String token : tokens) {
                if (allValueList.contains(token)) {
                    allowedValueList.add(token);
                } else {
                    System.out.println("Warning: value (" + token + ") is not a valid enum value ("
                            + allValueList + ").");
                }
            }
        } else {
            allowedValueList.addAll(allValueList);
            hasUnrecognizedConstraint = true;
        }
    }

    public EnumFieldDetector(EnumFieldDetector a, EnumFieldDetector b) {
        super(a, b);
        allowedValueList.addAll(a.allowedValueList);
        allowedValueList.retainAll(b.allowedValueList);
        detectImpossibleMatch();
    }

    private void detectImpossibleMatch() {
        if (allowedValueList.isEmpty()) {
            impossibleMatch = true;
        }
    }

    public EnumFieldDetector merge(EnumFieldDetector other) {
        return new EnumFieldDetector(this, other);
    }

}
