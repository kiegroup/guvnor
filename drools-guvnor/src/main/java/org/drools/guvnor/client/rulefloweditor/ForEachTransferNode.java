package org.drools.guvnor.client.rulefloweditor;

/*
 * Copyright 2005 JBoss Inc
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

import org.drools.guvnor.client.rpc.RuleFlowContentModel;

public class ForEachTransferNode extends TransferNode {

    private RuleFlowContentModel contentModel;

    public void setContentModel(RuleFlowContentModel contentModel) {
        this.contentModel = contentModel;
    }

    public RuleFlowContentModel getContentModel() {
        return contentModel;
    }

}
