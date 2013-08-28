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

package org.guvnor.common.services.project.model;

import org.jboss.errai.common.client.api.annotations.Portable;

import java.util.ArrayList;
import java.util.List;

@Portable
public class KSessionModel
        implements HasListFormComboPanelProperties {

    private String name;
    private String type = "stateless";
    private ClockTypeOption clockType = ClockTypeOption.REALTIME;
    private boolean theDefault = false;
    private String scope;
    private List<ListenerModel> listenerModels = new ArrayList<ListenerModel>();
    private List<WorkItemHandlerModel> workItemHandelerModels = new ArrayList<WorkItemHandlerModel>();
    private ListenerModel listener;
    private WorkItemHandlerModel workItemHandlerModel;
    private KSessionLogger logger;

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public ClockTypeOption getClockType() {
        return clockType;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setClockType(ClockTypeOption clockTypeEnum) {
        this.clockType = clockTypeEnum;
    }

    public boolean isDefault() {
        return theDefault;
    }

    @Override
    public void setDefault(boolean theDefault) {
        this.theDefault = theDefault;
    }

    public String getScope() {
        return scope;
    }

    public List<ListenerModel> getListenerModels() {
        return listenerModels;
    }

    public List<ListenerModel> getListenerModels(ListenerModel.Kind kind) {
        List<ListenerModel> listeners = new ArrayList<ListenerModel>();
        for (ListenerModel listener : getListenerModels()) {
            if (listener.getKind() == kind) {
                listeners.add(listener);
            }
        }
        return listeners;
    }

    public List<WorkItemHandlerModel> getWorkItemHandelerModels() {
        return workItemHandelerModels;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }

    public void addListenerModel(ListenerModel listener) {
        this.listener = listener;
    }

    public void addWorkItemHandelerModel(WorkItemHandlerModel workItemHandlerModel) {
        this.workItemHandlerModel = workItemHandlerModel;
    }

    public void setLogger(KSessionLogger logger) {
        this.logger = logger;
    }

    public KSessionLogger getLogger() {
        return logger;
    }
}
