/*
 * Copyright 2011 JBoss Inc
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
package org.drools.ide.common.client.modeldriven;

import org.drools.guvnor.shared.api.PortableObject;

import java.util.HashMap;
import java.util.Map;


/**
 * An annotation on a Fact type
 */
public class ModelAnnotation
    implements
        PortableObject {

    private String              annotationName;
    private Map<String, String> annotationValues = new HashMap<String, String>();

    public String getAnnotationName() {
        return annotationName;
    }

    public void setAnnotationName(String annotationName) {
        this.annotationName = annotationName;
    }

    public Map<String, String> getAnnotationValues() {
        return annotationValues;
    }

    public void setAnnotationValues(Map<String, String> annotationValues) {
        this.annotationValues = annotationValues;
    }

}
