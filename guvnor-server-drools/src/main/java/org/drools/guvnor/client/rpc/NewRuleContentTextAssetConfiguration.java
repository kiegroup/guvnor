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

package org.drools.guvnor.client.rpc;

import com.google.gwt.user.client.rpc.IsSerializable;

/**
 * The configuration information required for a new RuleContentText Asset
 */
public class NewRuleContentTextAssetConfiguration extends NewAssetWithContentConfiguration<RuleContentText>
    implements
    IsSerializable {

    // For GWT serialisation
    public NewRuleContentTextAssetConfiguration() {
    }

    public NewRuleContentTextAssetConfiguration(String assetName,
                                                String packageName,
                                                String packageUUID,
                                                String description,
                                                String initialCategory,
                                                String format,
                                                RuleContentText content) {
        super( assetName,
               packageName,
               packageUUID,
               description,
               initialCategory,
               format,
               content );
    }

}
