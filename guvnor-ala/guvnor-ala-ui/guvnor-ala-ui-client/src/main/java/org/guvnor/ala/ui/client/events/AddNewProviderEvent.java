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

package org.guvnor.ala.ui.client.events;

import org.guvnor.ala.ui.model.ProviderType;

public class AddNewProviderEvent {

    private final ProviderType providerType;

    public AddNewProviderEvent(final ProviderType providerType ) {
        this.providerType = providerType;
    }

    public ProviderType getProviderType() {
        return providerType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        AddNewProviderEvent that = (AddNewProviderEvent) o;

        return providerType != null ? providerType.equals(that.providerType) : that.providerType == null;
    }

    @Override
    public int hashCode() {
        int result = providerType != null ? providerType.hashCode() : 0;
        result = ~~result;
        return result;
    }
}
