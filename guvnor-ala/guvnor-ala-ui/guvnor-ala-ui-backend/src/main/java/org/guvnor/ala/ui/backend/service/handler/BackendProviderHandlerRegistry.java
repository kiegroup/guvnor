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

package org.guvnor.ala.ui.backend.service.handler;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Any;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;

import org.guvnor.ala.ui.handler.AbstractProviderHandlerRegistry;
import org.guvnor.ala.ui.model.ProviderTypeKey;

/**
 * Registry for the backend side configured provider type converters.
 */
@ApplicationScoped
public class BackendProviderHandlerRegistry
        extends AbstractProviderHandlerRegistry<BackendProviderHandler> {

    public BackendProviderHandlerRegistry() {
        //Empty constructor for Weld proxying
    }

    @Inject
    public BackendProviderHandlerRegistry(final @Any Instance<BackendProviderHandler> handlerInstance) {
        super(handlerInstance);
    }

    public BackendProviderHandler ensureHandler(ProviderTypeKey providerTypeKey) {
        final BackendProviderHandler handler = getProviderHandler(providerTypeKey);
        if (handler == null) {
            throw new RuntimeException("BackendProviderHandler was not found for providerTypeKey: " + providerTypeKey);
        }
        return handler;
    }
}