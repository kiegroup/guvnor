/*
 * Copyright 2012 JBoss Inc
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

package org.drools.ide.common.client.modeldriven.scorecards;

import com.google.gwt.user.client.rpc.IsSerializable;
import org.drools.guvnor.shared.api.PortableObject;

import java.util.ArrayList;
import java.util.List;

public class ScorecardModel implements PortableObject, IsSerializable {
    String name;
    String reasonCodesAlgorithm;
    double baselineScore;
    double initialScore;
    boolean useReasonCodes;
    String factName = "";
    String fieldName = "";
    String reasonCodeField = "";

    List<Characteristic> characteristics = new ArrayList<Characteristic>();
    private String packageName;

    public String getPackageName() {
        return packageName;
    }

    public ScorecardModel() {

    }

    public String getReasonCodeField() {
        return reasonCodeField;
    }

    public void setReasonCodeField(String reasonCodeField) {
        this.reasonCodeField = reasonCodeField;
    }


    public String getFactName() {
        return factName;
    }

    public void setFactName(String factName) {
        this.factName = factName;
    }

    public String getFieldName() {
        return fieldName;
    }

    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    public double getInitialScore() {
        return initialScore;
    }

    public void setInitialScore(double initialScore) {
        this.initialScore = initialScore;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getReasonCodesAlgorithm() {
        return reasonCodesAlgorithm;
    }

    public void setReasonCodesAlgorithm(String reasonCodesAlgorithm) {
        this.reasonCodesAlgorithm = reasonCodesAlgorithm;
    }

    public double getBaselineScore() {
        return baselineScore;
    }

    public void setBaselineScore(double baselineScore) {
        this.baselineScore = baselineScore;
    }

    public boolean isUseReasonCodes() {
        return useReasonCodes;
    }

    public void setUseReasonCodes(boolean useReasonCodes) {
        this.useReasonCodes = useReasonCodes;
    }

    public List<Characteristic> getCharacteristics() {
        return characteristics;
    }

    public void setCharacteristics(List<Characteristic> characteristics) {
        this.characteristics = characteristics;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }
}
