/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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
package org.guvnor.ala.openshift.config.impl;

import org.guvnor.ala.config.CloneableConfig;
import org.guvnor.ala.openshift.config.OpenShiftRuntimeConfig;
import org.guvnor.ala.runtime.providers.ProviderId;

public class OpenShiftRuntimeConfigImpl implements OpenShiftRuntimeConfig, CloneableConfig<OpenShiftRuntimeConfig> {

    private ProviderId providerId;
    private String applicationName;
    private String resourceSecretsUri;
    private String resourceStreamsUri;
    private String resourceTemplateName;
    private String resourceTemplateParamDelimiter;
    private String resourceTemplateParamAssigner;
    private String resourceTemplateParamValues;
    private String resourceTemplateUri;
    private String serviceName;

    public OpenShiftRuntimeConfigImpl() {
    }

    public OpenShiftRuntimeConfigImpl(ProviderId providerId, String applicationName, String resourceSecretsUri, String resourceStreamsUri, String resourceTemplateName, String resourceTemplateParamDelimiter, String resourceTemplateParamAssigner, String resourceTemplateParamValues, String resourceTemplateUri, String serviceName) {
        this.applicationName = applicationName;
        this.providerId = providerId;
        this.resourceSecretsUri = resourceSecretsUri;
        this.resourceStreamsUri = resourceStreamsUri;
        this.resourceTemplateName = resourceTemplateName;
        this.resourceTemplateParamDelimiter = resourceTemplateParamDelimiter;
        this.resourceTemplateParamAssigner = resourceTemplateParamAssigner;
        this.resourceTemplateParamValues = resourceTemplateParamValues;
        this.resourceTemplateUri = resourceTemplateUri;
        this.serviceName = serviceName;
    }

    @Override
    public ProviderId getProviderId() {
        return providerId;
    }

    public void setProviderId(ProviderId providerId) {
        this.providerId = providerId;
    }

    @Override
    public String getApplicationName() {
        return applicationName;
    }

    public void setApplicationName(String applicationName) {
        this.applicationName = applicationName;
    }

    @Override
    public String getResourceSecretsUri() {
        return resourceSecretsUri;
    }

    public void setResourceSecretsUri(String resourceSecretsUri) {
        this.resourceSecretsUri = resourceSecretsUri;
    }

    @Override
    public String getResourceStreamsUri() {
        return resourceStreamsUri;
    }

    public void setResourceStreamsUri(String resourceStreamsUri) {
        this.resourceStreamsUri = resourceStreamsUri;
    }

    @Override
    public String getResourceTemplateName() {
        return resourceTemplateName;
    }

    public void setResourceTemplateName(String resourceTemplateName) {
        this.resourceTemplateName = resourceTemplateName;
    }

    @Override
    public String getResourceTemplateParamDelimiter() {
        return resourceTemplateParamDelimiter;
    }

    public void setResourceTemplateParamDelimiter(String resourceTemplateParamDelimiter) {
        this.resourceTemplateParamDelimiter = resourceTemplateParamDelimiter;
    }

    @Override
    public String getResourceTemplateParamAssigner() {
        return resourceTemplateParamAssigner;
    }

    public void setResourceTemplateParamAssigner(String resourceTemplateParamAssigner) {
        this.resourceTemplateParamAssigner = resourceTemplateParamAssigner;
    }

    @Override
    public String getResourceTemplateParamValues() {
        return resourceTemplateParamValues;
    }

    public void setResourceTemplateParamValues(String resourceTemplateParamValues) {
        this.resourceTemplateParamValues = resourceTemplateParamValues;
    }

    @Override
    public String getResourceTemplateUri() {
        return resourceTemplateUri;
    }

    public void setResourceTemplateUri(String resourceTemplateUri) {
        this.resourceTemplateUri = resourceTemplateUri;
    }

    @Override
    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    @Override
    public String toString() {
        return "OpenShiftRuntimeConfigImpl{" + ", providerId=" + providerId + ", applicationName=" + applicationName + ", resourceSecretsUri=" + resourceSecretsUri + ", resourceStreamsUri=" + resourceStreamsUri + ", resourceTemplateName=" + resourceTemplateName + ", resourceTemplateParamDelimiter=" + resourceTemplateParamDelimiter + ", resourceTemplateParamAssigner=" + resourceTemplateParamAssigner + ", resourceTemplateParamValues=" + resourceTemplateParamValues + ", resourceTemplateUri=" + resourceTemplateUri + ", serviceName=" + serviceName + '}';
    }

    @Override
    public OpenShiftRuntimeConfig asNewClone(final OpenShiftRuntimeConfig source) {
        return new OpenShiftRuntimeConfigImpl(source.getProviderId(), source.getApplicationName(), source.getResourceSecretsUri(), source.getResourceStreamsUri(), source.getResourceTemplateName(), source.getResourceTemplateParamDelimiter(), source.getResourceTemplateParamAssigner(), source.getResourceTemplateParamValues(), source.getResourceTemplateUri(), source.getServiceName());
    }
}
