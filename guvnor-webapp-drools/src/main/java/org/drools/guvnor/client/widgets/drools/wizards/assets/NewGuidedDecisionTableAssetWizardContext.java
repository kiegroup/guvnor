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
package org.drools.guvnor.client.widgets.drools.wizards.assets;

import org.drools.guvnor.client.rpc.NewGuidedDecisionTableAssetConfiguration;
import org.drools.guvnor.client.widgets.wizards.WizardContext;
import org.drools.ide.common.client.modeldriven.dt52.GuidedDecisionTable52;
import org.drools.ide.common.client.modeldriven.dt52.GuidedDecisionTable52.TableFormat;

import java.util.Map;

/**
 * A container for the details required to create a new Guided Decision Table
 * Asset on the repository
 */
public class NewGuidedDecisionTableAssetWizardContext extends NewAssetWizardContext {

    private final TableFormat tableFormat;

    public NewGuidedDecisionTableAssetWizardContext(NewGuidedDecisionTableAssetConfiguration configuration) {
        super(configuration.getAssetName(),
                configuration.getPackageName(),
                configuration.getPackageUUID(),
                configuration.getDescription(),
                configuration.getInitialCategory(),
                configuration.getFormat());
        this.tableFormat = configuration.getContent().getTableFormat();
    }

    public TableFormat getTableFormat() {
        return this.tableFormat;
    }

    @Override
    public Map<String, String> getParameters() {
        Map<String, String> parameters = super.getParameters();
        parameters.put("TABLE_FORMAT", getTableFormat().toString());
        return parameters;
    }

    public static boolean isInstance(Map<String,String> parameters) {
        return parameters.containsKey("TABLE_FORMAT");
    }

    public static WizardContext create(Map<String,String> parameters) {
        final GuidedDecisionTable52 content = new GuidedDecisionTable52();
        content.setTableFormat(TableFormat.valueOf(parameters.get("TABLE_FORMAT")));
        NewGuidedDecisionTableAssetConfiguration configuration = new NewGuidedDecisionTableAssetConfiguration(
                parameters.get("ASSET_NAME"),
                parameters.get("PACKAGE_NAME"),
                parameters.get("PACKAGE_UUID"),
                parameters.get("DESCRIPTION"),
                parameters.get("CATEGORY"),
                parameters.get("FORMAT"),
                content
        );


        return new NewGuidedDecisionTableAssetWizardContext(configuration);
    }
}
