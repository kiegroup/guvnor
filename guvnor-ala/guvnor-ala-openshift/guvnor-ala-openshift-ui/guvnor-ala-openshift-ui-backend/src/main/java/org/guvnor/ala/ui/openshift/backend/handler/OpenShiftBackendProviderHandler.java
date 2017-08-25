/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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

package org.guvnor.ala.ui.openshift.backend.handler;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.guvnor.ala.openshift.model.OpenShiftProviderType;
import org.guvnor.ala.ui.backend.service.converter.ProviderConfigConverter;
import org.guvnor.ala.ui.backend.service.handler.BackendProviderHandler;
import org.guvnor.ala.ui.model.ProviderTypeKey;

/**
 * Backend provider handler implementation for OpenShift providers.
 * @see BackendProviderHandler
 */
@ApplicationScoped
public class OpenShiftBackendProviderHandler
        implements BackendProviderHandler {

    private OpenShiftProviderConfigConverter configConverter;

    public OpenShiftBackendProviderHandler() {
        //Empty constructor for Weld proxying
    }

    @Inject
    public OpenShiftBackendProviderHandler(final OpenShiftProviderConfigConverter configConverter) {
        this.configConverter = configConverter;
    }

    @Override
    public int getPriority() {
        return 1;
    }

    @Override
    public boolean acceptProviderType(ProviderTypeKey providerTypeKey) {
        return providerTypeKey != null && OpenShiftProviderType.instance().getProviderTypeName().equals(providerTypeKey.getId());
    }

    @Override
    public ProviderConfigConverter getProviderConfigConverter() {
        return configConverter;
    }
}
