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

import org.drools.ide.common.client.modeldriven.dt52.GuidedDecisionTable52.TableFormat;

import com.google.gwt.user.client.rpc.IsSerializable;

/**
 * The configuration information required for a new Guided Decision Table Asset
 */
public class NewGuidedDecisionTableAssetConfiguration extends NewAssetConfiguration
    implements
    IsSerializable {

    private TableFormat tableFormat;

    // For GWT serialisation
    public NewGuidedDecisionTableAssetConfiguration() {
    }

    public NewGuidedDecisionTableAssetConfiguration(String assetName,
                                                    String packageName,
                                                    String packageUUID,
                                                    TableFormat tableFormat,
                                                    String description,
                                                    String initialCategory,
                                                    String format) {
        super( assetName,
               packageName,
               packageUUID,
               description,
               initialCategory,
               format );
        this.tableFormat = tableFormat;
    }

    // ************************************************************************
    // Getters
    // ************************************************************************

    public TableFormat getTableFormat() {
        return tableFormat;
    }

}
