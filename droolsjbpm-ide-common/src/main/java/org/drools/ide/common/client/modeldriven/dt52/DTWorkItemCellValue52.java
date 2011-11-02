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
package org.drools.ide.common.client.modeldriven.dt52;

import org.drools.ide.common.shared.workitems.PortableParameterDefinition;
import org.drools.ide.common.shared.workitems.PortableWorkDefinition;

/**
 * Holder for cell value relating to Work Items
 */
public class DTWorkItemCellValue52 extends DTCellValue52 {

    private static final long           serialVersionUID = 540L;

    // The WorkItem to which the Field value is bound
    private PortableWorkDefinition      workItemDefinition;

    // The WorkItem Result parameter to which the Field value is bound
    private PortableParameterDefinition workItemResultParameter;

    public boolean isBoundToWorkItem() {
        return (this.workItemDefinition != null && this.workItemResultParameter != null);
    }

    public PortableWorkDefinition getWorkItemDefinition() {
        return workItemDefinition;
    }

    public void setWorkItemDefinition(PortableWorkDefinition workItemDefinition) {
        this.workItemDefinition = workItemDefinition;
    }

    public PortableParameterDefinition getWorkItemResultParameter() {
        return workItemResultParameter;
    }

    public void setWorkItemResultParameter(PortableParameterDefinition workItemResultParameter) {
        this.workItemResultParameter = workItemResultParameter;
    }

}
