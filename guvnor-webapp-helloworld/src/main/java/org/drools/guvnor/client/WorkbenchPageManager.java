/*
 * Copyright 2012 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.drools.guvnor.client;

import com.google.gwt.event.shared.EventBus;

public class WorkbenchPageManager implements AddViewPartEvent.Handler {

    private WorkbenchPage workbenchPage;
    private final ViewRegistry viewRegistry;

    public WorkbenchPageManager(EventBus eventBus) {
        eventBus.addHandler(AddViewPartEvent.TYPE, this);
        viewRegistry = new ViewRegistry();
    }

    public WorkbenchPage getWorkbenchPage() {
        return workbenchPage;
    }

    void setWorkbenchPage(WorkbenchPage workbenchPage) {
        this.workbenchPage = workbenchPage;
    }

    @Override
    public void onAddViewPart(AddViewPartEvent event) {
        if (viewRegistry.keySet().contains(event.getViewPartId())) {
            ViewDescriptor viewDescriptor = viewRegistry.get(event.getViewPartId());
            workbenchPage.add(
                    viewDescriptor.getTitle(),
                    viewDescriptor.getWidget());
        }
    }
}
