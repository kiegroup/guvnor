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

import com.google.gwt.place.shared.Place;
import org.drools.guvnor.client.widgets.wizards.WizardContext;

import java.util.HashMap;
import java.util.Map;

/**
 * A container for the details required to create a new Asset on the repository
 */
public abstract class NewAssetWizardContext extends Place
        implements
        WizardContext {

    private final Map<String,String> parameters = new HashMap<String, String>();

    public NewAssetWizardContext(String assetName,
                                 String packageName,
                                 String packageUUID,
                                 String description,
                                 String initialCategory,
                                 String format) {
        parameters.put("ASSET_NAME",assetName);
        parameters.put("PACKAGE_NAME",packageName);
        parameters.put("PACKAGE_UUID",packageUUID);
        parameters.put("DESCRIPTION",description);
        parameters.put("CATEGORY",initialCategory);
        parameters.put("FORMAT",format);
    }

    public String getAssetName() {
        return parameters.get("ASSET_NAME");
    }

    public String getPackageName() {
        return parameters.get("PACKAGE_NAME");
    }

    public String getPackageUUID() {
        return parameters.get("PACKAGE_UUID");
    }

    public String getFormat() {
        return parameters.get("FORMAT");
    }

    public String getDescription() {
        return parameters.get("DESCRIPTION");
    }

    public String getInitialCategory() {
        return parameters.get("CATEGORY");
    }

    @Override
    public Map<String, String> getParameters() {
        return parameters;
    }

}
