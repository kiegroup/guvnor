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

package org.guvnor.ala.ui.client.wizard.project;

import org.guvnor.common.services.project.model.GAV;

public class GAVConfigurationChangeEvent {

    private GAV gav;

    public GAVConfigurationChangeEvent() {
    }

    public GAVConfigurationChangeEvent(final GAV gav) {
        this.gav = gav;
    }

    public GAV getGav() {
        return gav;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        GAVConfigurationChangeEvent that = (GAVConfigurationChangeEvent) o;

        return gav != null ? gav.equals(that.gav) : that.gav == null;
    }

    @Override
    public int hashCode() {
        return gav != null ? gav.hashCode() : 0;
    }
}
