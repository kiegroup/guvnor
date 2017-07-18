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

package org.guvnor.ala.ui.backend.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.guvnor.ala.services.api.backend.RuntimeProvisioningServiceBackend;
import org.guvnor.ala.ui.model.ProviderType;
import org.guvnor.ala.ui.model.ProviderTypeKey;
import org.guvnor.ala.ui.model.ProviderTypeStatus;
import org.guvnor.ala.ui.service.ProviderTypeService;
import org.jboss.errai.bus.server.annotations.Service;

import static org.uberfire.commons.validation.PortablePreconditions.checkNotEmpty;
import static org.uberfire.commons.validation.PortablePreconditions.checkNotNull;

@Service
@ApplicationScoped
public class ProviderTypeServiceImpl
        implements ProviderTypeService {

    //TODO, SPRINT6 will manage the storing of this information.
    private final Map<ProviderTypeKey, ProviderType> enabledProviders = new HashMap<>();

    private RuntimeProvisioningServiceBackend runtimeProvisioningService;

    public ProviderTypeServiceImpl() {
        //Empty constructor for Weld proxying
    }

    @Inject
    public ProviderTypeServiceImpl(final RuntimeProvisioningServiceBackend runtimeProvisioningService) {
        this.runtimeProvisioningService = runtimeProvisioningService;
    }

    @Override
    public Collection<ProviderType> getAvailableProviderTypes() {
        List<ProviderType> result = new ArrayList<>();
        List<org.guvnor.ala.runtime.providers.ProviderType> providers =
                runtimeProvisioningService.getProviderTypes(0,
                                                            100,
                                                            "providerTypeName",
                                                            true);

        if (providers != null) {
            providers.forEach(providerType ->
                                      result.add(new ProviderType(new ProviderTypeKey(providerType.getProviderTypeName(),
                                                                                      providerType.getVersion()),
                                                                  providerType.getProviderTypeName()))
            );
        }
        return result;
    }

    @Override
    public ProviderType getProviderType(final ProviderTypeKey providerTypeKey) {
        checkNotNull("providerTypeKey",
                     providerTypeKey);
        return getAvailableProviderTypes().stream()
                .filter(providerType -> providerType.getKey().equals(providerTypeKey))
                .findFirst().orElse(null);
    }

    @Override
    public Collection<ProviderType> getEnabledProviderTypes() {
        return new ArrayList<>(enabledProviders.values());
    }

    @Override
    public void enableProviderTypes(final Collection<ProviderType> providerTypes) {
        checkNotEmpty("providerTypes",
                      providerTypes);
        providerTypes.forEach(this::enableProviderType);
    }

    private void enableProviderType(final ProviderType providerType) {
        enabledProviders.put(providerType.getKey(),
                             providerType);
    }

    @Override
    public void disableProviderType(final ProviderType providerType) {
        checkNotNull("providerType",
                     providerType);
        enabledProviders.remove(providerType.getKey());
    }

    @Override
    public Map<ProviderType, ProviderTypeStatus> getProviderTypesStatus() {
        final Map<ProviderType, ProviderTypeStatus> result = new HashMap<>();

        enabledProviders.values().forEach(providerType -> result.put(providerType,
                                                                     ProviderTypeStatus.ENABLED));
        getAvailableProviderTypes().forEach(providerType -> {
            if (!result.containsKey(providerType)) {
                result.put(providerType,
                           ProviderTypeStatus.DISABLED);
            }
        });
        return result;
    }
}
