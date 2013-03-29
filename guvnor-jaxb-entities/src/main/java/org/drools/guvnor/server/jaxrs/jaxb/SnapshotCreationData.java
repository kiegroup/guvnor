/*
 * Copyright 2013 JBoss by Red Hat.
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
package org.drools.guvnor.server.jaxrs.jaxb;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

/**
 *
 * @author nheron
 */
@XmlRootElement(namespace = "http://www.w3.org/2005/Atom", name = "SnapshotCreationData")
@XmlAccessorType(XmlAccessType.PROPERTY)
@XmlType(propOrder = {"buildMode", "statusOperator", "statusDescriptionValue", "enableStatusSelector", "categoryOperator",
    "categoryValue", "enableCategorySelector", "customSelectorConfigName"})
public class SnapshotCreationData {

    private String buildMode;
    private String statusOperator;
    private String statusDescriptionValue;
    private boolean enableStatusSelector = false;
    private String categoryOperator;
    private String categoryValue;
    private boolean enableCategorySelector = false;
    private String customSelectorConfigName;

    @XmlElement(namespace = "http://www.w3.org/2005/Atom")
    public String getBuildMode() {
        return buildMode;
    }

    public void setBuildMode(String buildMode) {
        this.buildMode = buildMode;
    }

    @XmlElement(namespace = "http://www.w3.org/2005/Atom")
    public String getStatusOperator() {
        return statusOperator;
    }

    public void setStatusOperator(String statusOperator) {
        this.statusOperator = statusOperator;
    }

    @XmlElement(namespace = "http://www.w3.org/2005/Atom")
    public String getStatusDescriptionValue() {
        return statusDescriptionValue;
    }

    public void setStatusDescriptionValue(String statusDescriptionValue) {
        this.statusDescriptionValue = statusDescriptionValue;
    }

    @XmlElement(namespace = "http://www.w3.org/2005/Atom")
    public boolean getEnableStatusSelector() {
        return enableStatusSelector;
    }

    public void setEnableStatusSelector(boolean enableStatusSelector) {
        this.enableStatusSelector = enableStatusSelector;
    }

    @XmlElement(namespace = "http://www.w3.org/2005/Atom")
    public String getCategoryOperator() {
        return categoryOperator;
    }

    public void setCategoryOperator(String categoryOperator) {
        this.categoryOperator = categoryOperator;
    }

    @XmlElement(namespace = "http://www.w3.org/2005/Atom")
    public String getCategoryValue() {
        return categoryValue;
    }

    public void setCategoryValue(String categoryValue) {
        this.categoryValue = categoryValue;
    }

    @XmlElement(namespace = "http://www.w3.org/2005/Atom")
    public boolean getEnableCategorySelector() {
        return enableCategorySelector;
    }

    public void setEnableCategorySelector(boolean enableCategorySelector) {
        this.enableCategorySelector = enableCategorySelector;
    }

    @XmlElement(namespace = "http://www.w3.org/2005/Atom")
    public String getCustomSelectorConfigName() {
        return customSelectorConfigName;
    }

    public void setCustomSelectorConfigName(String customSelectorConfigName) {
        this.customSelectorConfigName = customSelectorConfigName;
    }
}
