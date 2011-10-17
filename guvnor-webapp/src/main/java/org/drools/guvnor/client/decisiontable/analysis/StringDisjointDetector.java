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

public class StringDisjointDetector extends DisjointDetector<StringDisjointDetector> {

    public String value = null;
    private List<String> notList = new ArrayList<String>(1);

    public StringDisjointDetector(String value, String operator) {
        if (operator.equals("==")) {
            this.value = value;
        } else if (operator.equals("!=")) {
            notList.add(value);
        }
    }

    public void merge(StringDisjointDetector other) {
        if (value == null) {
            value = other.value;
        } else if (other.value != null) {
            if (!value.equals(other.value)) {
                impossibleMatch = true;
                value = null;
            }
        }
        notList.addAll(other.notList);
        if (value != null && notList.contains(value)) {
            impossibleMatch = true;
        }
    }

}
