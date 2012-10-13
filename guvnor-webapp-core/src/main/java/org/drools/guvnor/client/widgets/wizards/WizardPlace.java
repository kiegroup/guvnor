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
package org.drools.guvnor.client.widgets.wizards;

import org.uberfire.shared.mvp.impl.DefaultPlaceRequest;

import java.util.Map;

/**
 * A request for a specific Wizard
 */
public class WizardPlace<T extends WizardContext>
        extends DefaultPlaceRequest {

    public WizardPlace(T context) {
        super("wizardPopup");

        for (Map.Entry<String, String> parameter : context.getParameters().entrySet()) {
            addParameter(parameter.getKey(), parameter.getValue());
        }
    }

    public WizardContext getContext() {
        return null;  //TODO: -Rikkola-
    }
}
