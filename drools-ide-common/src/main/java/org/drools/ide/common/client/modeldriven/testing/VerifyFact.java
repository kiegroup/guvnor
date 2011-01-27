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

package org.drools.ide.common.client.modeldriven.testing;

import java.util.ArrayList;
import java.util.List;

/**
 * This is for making assertions over a specific facts value/state AFTER execution.
 *
 * @author Michael Neale
 */
public class VerifyFact
        implements
        Expectation {

    private static final long serialVersionUID = 510l;

    private List<VerifyField> fieldValues = new ArrayList<VerifyField>();
    private String name;
    private String description;

    /**
     * This is true if it isn't a named fact, but it will just search working memory to verify.
     */
    public boolean anonymous = false;

    public VerifyFact() {
    }

    public VerifyFact(String name,
                      List<VerifyField> fieldValues,
                      boolean anonymous) {
        this.name = name;
        this.fieldValues = fieldValues;
        this.anonymous = anonymous;
    }

    public VerifyFact(String name,
                      List<VerifyField> fieldValues) {
        this(name,
                fieldValues,
                false);
    }

    public boolean wasSuccessful() {
        for (VerifyField verifyField : fieldValues) {
            if (!verifyField.getSuccessResult().booleanValue()) {
                return false;
            }
        }
        return true;
    }

    public void setFieldValues(List<VerifyField> fieldValues) {
        this.fieldValues = fieldValues;
    }

    public List<VerifyField> getFieldValues() {
        return fieldValues;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

}
