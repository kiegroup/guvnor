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

package org.drools.ide.common.server.testscenarios.verifiers;

import java.util.Iterator;
import java.util.Map;

import org.drools.base.TypeResolver;
import org.drools.common.InternalWorkingMemory;
import org.drools.ide.common.client.testscenarios.fixtures.VerifyFact;
import org.drools.ide.common.client.testscenarios.fixtures.VerifyField;

public class FactVerifier {

    private final Map<String, Object> populatedData;
    private final TypeResolver resolver;
    private final InternalWorkingMemory workingMemory;
    private final Map<String, Object> globalData;

    public FactVerifier(
            Map<String, Object> populatedData,
            TypeResolver resolver,
            InternalWorkingMemory workingMemory,
            Map<String, Object> globalData) {
        this.populatedData = populatedData;
        this.resolver = resolver;
        this.workingMemory = workingMemory;
        this.globalData = globalData;
    }

    public void verify(VerifyFact verifyFact) {

        if (!verifyFact.anonymous) {
            FactFieldValueVerifier fieldVerifier = new FactFieldValueVerifier(
                    populatedData,
                    verifyFact.getName(),
                    getFactObject(
                            verifyFact.getName(),
                            populatedData,
                            globalData),
                    resolver);
            fieldVerifier.checkFields(verifyFact.getFieldValues());
        } else {
            Iterator objects = workingMemory.iterateObjects();
            while (objects.hasNext()) {
                if (verifyFact(objects.next(), verifyFact, populatedData, resolver)) {
                    return;
                }
            }
            for (VerifyField verifyField : verifyFact.getFieldValues()) {
                if (verifyField.getSuccessResult() == null) {
                    verifyField.setSuccessResult(Boolean.FALSE);
                    verifyField.setActualResult("No match");
                }
            }
        }
    }

    private static boolean verifyFact(Object factObject, VerifyFact verifyFact, Map<String, Object> populatedData, TypeResolver resolver) {
        if (factObject.getClass().getSimpleName().equals(verifyFact.getName())) {
            FactFieldValueVerifier fieldVerifier = new FactFieldValueVerifier(populatedData,
                    verifyFact.getName(),
                    factObject,
                    resolver);
            fieldVerifier.checkFields(verifyFact.getFieldValues());
            if (verifyFact.wasSuccessful()) {
                return true;
            }
        }
        return false;
    }

    private static Object getFactObject(
            String factName,
            Map<String, Object> populatedData,
            Map<String, Object> globalData) {

        if (populatedData.containsKey(factName)) {
            return populatedData.get(factName);
        } else {
            return globalData.get(factName);
        }
    }
}
