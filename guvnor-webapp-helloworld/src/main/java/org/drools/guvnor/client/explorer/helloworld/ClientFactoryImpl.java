/*
 * Copyright 2011 JBoss Inc
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 */

package org.drools.guvnor.client.explorer.helloworld;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.EventBus;
import org.drools.guvnor.client.explorer.AbstractClientFactoryImpl;
import org.drools.guvnor.client.explorer.GuvnorActivityMapper;
import org.drools.guvnor.client.explorer.GuvnorPlaceHistoryMapper;
import org.drools.guvnor.client.widgets.wizards.WizardFactory;

public class ClientFactoryImpl extends AbstractClientFactoryImpl {

    private WizardFactory wizardFactory;

    public ClientFactoryImpl(EventBus eventBus) {
        super(eventBus);
    }

    /*
      * TODO: Alternatively, we can do below:
      * <generate-with class="org.drools.guvnor.client.util.ActivityMapper">
      *     <when-type-assignable class="org.drools.guvnor.client.explorer.GuvnorDroolsActivityMapper"/>
      * </generate-with>
      * We will revisit this code to decide which way is better later.
      */
    public GuvnorActivityMapper getActivityMapper() {
        return new GuvnorHelloWorldActivityMapper(this);
    }

    public GuvnorPlaceHistoryMapper getPlaceHistoryMapper() {
        if (guvnorPlaceHistoryMapper == null) {
            guvnorPlaceHistoryMapper = GWT.create(GuvnorHelloWorldPlaceHistoryMapper.class);
        }
        return guvnorPlaceHistoryMapper;
    }

    public WizardFactory getWizardFactory() {
        // TODO: Should this be here?
        return wizardFactory;
    }

}
