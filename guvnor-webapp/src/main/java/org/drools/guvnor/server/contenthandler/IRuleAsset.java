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

package org.drools.guvnor.server.contenthandler;

import org.drools.guvnor.client.rpc.RuleAsset;
import org.drools.guvnor.server.builder.BRMSPackageBuilder;
import org.drools.repository.AssetItem;

public interface IRuleAsset
    extends
    ICompilable {

    /**
     * This will be called when a rule asset is to render itself to DRL source.
     */
    public void assembleDRL(BRMSPackageBuilder builder,
                            AssetItem asset,
                            StringBuilder stringBuilder);

    public void assembleDRL(BRMSPackageBuilder builder,
                            RuleAsset asset,
                            StringBuilder stringBuilder);

    /**
     * If the rule has DSL in it, it is presented unexpanded.
     */
    public String getRawDRL(AssetItem asset);

}
