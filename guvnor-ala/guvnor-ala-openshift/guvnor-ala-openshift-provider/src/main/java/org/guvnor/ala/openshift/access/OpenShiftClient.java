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
package org.guvnor.ala.openshift.access;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import com.fasterxml.jackson.annotation.JsonIgnoreType;
import io.fabric8.kubernetes.api.model.HasMetadata;
import io.fabric8.kubernetes.api.model.IntOrString;
import io.fabric8.kubernetes.api.model.KubernetesList;
import io.fabric8.kubernetes.api.model.ObjectMeta;
import io.fabric8.kubernetes.api.model.Secret;
import io.fabric8.kubernetes.api.model.Service;
import io.fabric8.kubernetes.api.model.ServiceAccount;
import io.fabric8.kubernetes.client.dsl.Resource;
import io.fabric8.openshift.api.model.DeploymentConfig;
import io.fabric8.openshift.api.model.DoneableDeploymentConfig;
import io.fabric8.openshift.api.model.DoneablePolicyBinding;
import io.fabric8.openshift.api.model.ImageStream;
import io.fabric8.openshift.api.model.PolicyBinding;
import io.fabric8.openshift.api.model.Route;
import io.fabric8.openshift.api.model.RoutePort;
import io.fabric8.openshift.api.model.RouteSpec;
import io.fabric8.openshift.client.OpenShiftConfig;
import io.fabric8.openshift.client.dsl.DeployableScalableResource;
import org.guvnor.ala.openshift.access.exceptions.OpenShiftClientException;
import org.guvnor.ala.openshift.config.OpenShiftParameters;
import org.guvnor.ala.openshift.config.OpenShiftRuntimeConfig;
import org.guvnor.ala.openshift.model.OpenShiftRuntimeEndpoint;
import org.guvnor.ala.openshift.model.OpenShiftRuntimeState;

/**
 * Guvnor ALA abstraction/wrapper of the io.fabricate Kubernetes/OpenShift client.
 * Implements the "create" and "destroy" lifecycle aspects for the OpenShiftRuntimeExecExecutor.
 * Implements the "start", "stop", "restart", and "pause" lifecycle aspects for the OpenShiftRuntimeManager.
 * @see org.guvnor.ala.openshift.executor.OpenShiftRuntimeExecExecutor
 * @see org.guvnor.ala.openshift.service.OpenShiftRuntimeManager
 */
@JsonIgnoreType
public class OpenShiftClient {

    private static final String GUVNOR_ALA_GENERATED = "guvnor.ala/generated";

    private final io.fabric8.openshift.client.OpenShiftClient delegate;
    private final long buildTimeout;

    // Support for OpenShiftAccessInterfaceImpl ------------------------------

    public OpenShiftClient(io.fabric8.openshift.client.OpenShiftClient delegate) {
        this.delegate = delegate;
        long buildTimeout = ((OpenShiftConfig) delegate.getConfiguration()).getBuildTimeout();
        if (buildTimeout < 0) {
            buildTimeout = OpenShiftConfig.DEFAULT_BUILD_TIMEOUT;
        }
        this.buildTimeout = buildTimeout;
    }

    public void dispose() {
        delegate.close();
    }

    // Support for OpenShiftRuntimeExecExecutor ------------------------------

    public OpenShiftRuntimeState create(OpenShiftRuntimeConfig runtimeConfig) throws OpenShiftClientException {
        try {
            String prjName = runtimeConfig.getProjectName();
            String svcName = runtimeConfig.getServiceName();
            String appName = runtimeConfig.getApplicationName();
            OpenShiftRuntimeId runtimeId = new OpenShiftRuntimeId(prjName, svcName, appName);
            OpenShiftRuntimeState runtimeState = getRuntimeState(runtimeId);
            if (OpenShiftRuntimeState.NA.equals(runtimeState.getState())) {
                createProject(prjName);
                createFromUri(prjName, runtimeConfig.getResourceSecretsUri());
                createFromUri(prjName, runtimeConfig.getResourceStreamsUri());
                createFromTemplate(prjName, runtimeConfig);
                runtimeState = getRuntimeState(runtimeId);
            }
            return runtimeState;
        } catch (Throwable t) {
            throw new OpenShiftClientException(t.getMessage(), t);
        }
    }

    private void createProject(String prjName) {
        if (delegate.projects().withName(prjName).get() == null) {
            delegate.projects().createNew().editOrNewMetadata().withName(prjName).addToAnnotations(GUVNOR_ALA_GENERATED, Boolean.TRUE.toString()).endMetadata().done();
        }
        addServiceAccountRole(prjName, "builder", "system:image-builder");
        addServiceAccountRole(prjName, "default", "admin");
        addServiceAccountRole(prjName, "default", "view");
        addServiceAccountRole(prjName, "deployer", "system:deployer");
        addSystemGroupRole(prjName, "deployer", "system:image-puller");
    }

    private void addServiceAccountRole(String prjName, String name, String role) {
        Resource<PolicyBinding, DoneablePolicyBinding> bindingResource =
                delegate.policyBindings().inNamespace(prjName).withName(":default");
        DoneablePolicyBinding binding;
        if (bindingResource.get() == null) {
            binding = bindingResource.createNew();
        } else {
            binding = bindingResource.edit();
        }
        binding.editOrNewMetadata()
            .withName(":default")
            .endMetadata()
            .editOrNewPolicyRef()
            .withName("default")
            .endPolicyRef()
            .addNewRoleBinding()
            .withName(role)
            .editOrNewRoleBinding()
            .editOrNewMetadata()
            .withName(role)
            .withNamespace(prjName)
            .endMetadata()
            .addToUserNames("system:serviceaccount:" + prjName + ":" + name)
            .addNewSubject()
            .withName("default")
            .withNamespace(prjName)
            .withKind("ServiceAccount")
            .endSubject()
            .withNewRoleRef()
            .withName(role)
            .endRoleRef()
            .endRoleBinding()
            .endRoleBinding()
            .done();
    }

    private void addSystemGroupRole(String prjName, String name, String role) {
        Resource<PolicyBinding, DoneablePolicyBinding> bindingResource =
                delegate.policyBindings().inNamespace(prjName).withName(":default");
        DoneablePolicyBinding binding;
        if (bindingResource.get() == null) {
            binding = bindingResource.createNew();
        } else {
            binding = bindingResource.edit();
        }
        binding.editOrNewMetadata()
            .withName(":default")
            .endMetadata()
            .editOrNewPolicyRef()
            .withName("default")
            .endPolicyRef()
            .addNewRoleBinding()
            .withName(role)
            .editOrNewRoleBinding()
            .editOrNewMetadata()
            .withName(role)
            .withNamespace(prjName)
            .endMetadata()
            .addToGroupNames("system:serviceaccounts:" + prjName)
            .addNewSubject()
            .withName("default")
            .withNamespace(prjName)
            .withKind("SystemGroup")
            .endSubject()
            .withNewRoleRef()
            .withName(role)
            .endRoleRef()
            .endRoleBinding()
            .endRoleBinding()
            .done();
    }

    private void createFromUri(String prjName, String uri) throws MalformedURLException {
        URL url = toUrl(uri);
        if (url != null) {
            KubernetesList kubeList = delegate.lists().load(url).get();
            List<HasMetadata> items = kubeList.getItems();
            if (items.size() > 0) {
                for (HasMetadata item : items) {
                    String name = item.getMetadata().getName();
                    if (item instanceof ServiceAccount) {
                        if (delegate.serviceAccounts().inNamespace(prjName).withName(name).get() == null) {
                            setGuvnorAlaGenerated(item);
                        }
                    } else if (item instanceof Secret) {
                        if (delegate.secrets().inNamespace(prjName).withName(name).get() == null) {
                            setGuvnorAlaGenerated(item);
                        }
                    } else if (item instanceof ImageStream) {
                        if (delegate.imageStreams().inNamespace(prjName).withName(name).get() == null) {
                            setGuvnorAlaGenerated(item);
                        }
                    }
                }
                delegate.lists().inNamespace(prjName).create(kubeList);
            }
        }
    }

    private void createFromTemplate(String prjName, OpenShiftRuntimeConfig runtimeConfig) throws MalformedURLException {
        KubernetesList kubeList = processTemplate(runtimeConfig);
        if (kubeList != null && kubeList.getItems().size() > 0) {
            try {
                DeploymentConfig dc = getDeploymentConfig(kubeList, runtimeConfig.getServiceName());
                if (dc != null) {
                    dc.getSpec().setReplicas(0);
                }
                delegate.lists().inNamespace(prjName).create(kubeList);
            } catch (Throwable t) {
                throw new OpenShiftClientException(t.getMessage(), t);
            }
        }
    }

    private KubernetesList processTemplate(OpenShiftRuntimeConfig runtimeConfig) throws MalformedURLException {
        String prjName = runtimeConfig.getProjectName();
        URL url = toUrl(runtimeConfig.getResourceTemplateUri());
        if (url != null) {
            Map<String, String> params = OpenShiftParameters.fromRuntimeConfig(runtimeConfig);
            return delegate.templates().inNamespace(prjName).load(url).process(params);
        } else {
            String templateName = runtimeConfig.getResourceTemplateName();
            if (templateName != null && !templateName.isEmpty()) {
                Map<String, String> params = OpenShiftParameters.fromRuntimeConfig(runtimeConfig);
                return delegate.templates().inNamespace(prjName).withName(templateName).process(params);
            }
        }
        return null;
    }

    private DeploymentConfig getDeploymentConfig(KubernetesList list, String svcName) {
        if (list != null) {
            List<HasMetadata> items = list.getItems();
            String dcName = null;
            for (HasMetadata item : items) {
                if (item instanceof Service && item.getMetadata().getName().equals(svcName)) {
                    dcName = ((Service) item).getSpec().getSelector().get("deploymentConfig");
                    break;
                }
            }
            if (dcName != null) {
                for (HasMetadata item : items) {
                    if (item instanceof DeploymentConfig && item.getMetadata().getName().equals(dcName)) {
                        return (DeploymentConfig) item;
                    }
                }
            }
        }
        return null;
    }

    private URL toUrl(String uri) throws MalformedURLException {
        if (uri != null && !uri.isEmpty()) {
            return URI.create(uri).toURL();
        }
        return null;
    }

    private static final String APP_LABEL = "application";

    public void destroy(String id) throws OpenShiftClientException {
        try {
            OpenShiftRuntimeId runtimeId = OpenShiftRuntimeId.fromString(id);
            String prjName = runtimeId.project();
            String svcName = runtimeId.service();
            // TODO: should we always depend on the app label being specified, or gotten from the service?
            String appName = runtimeId.application();
            if (appName == null || appName.isEmpty()) {
                Service service = delegate.services().inNamespace(prjName).withName(svcName).get();
                if (service != null) {
                    appName = service.getMetadata().getLabels().get(APP_LABEL);
                }
            }
            /*
             * cascading delete of deploymentConfigs means we don't have to also do the following:
             *     delegate.deploymentConfigs().inNamespace(prjName).withLabel(APP_LABEL, app).delete();
             *     delegate.replicationControllers().inNamespace(prjName).withLabel(APP_LABEL, app).delete();
             *     delegate.pods().inNamespace(prjName).withLabel(APP_LABEL, app).delete();
             * , but deleting services and routes are still necessary:
             */
            delegate.deploymentConfigs().inNamespace(prjName).withName(svcName).cascading(true).delete();
            delegate.services().inNamespace(prjName).withLabel(APP_LABEL, appName).delete();
            delegate.routes().inNamespace(prjName).withLabel(APP_LABEL, appName).delete();
            // clean up any generated image streams, secrets, and service accounts
            for (ImageStream item : delegate.imageStreams().inNamespace(prjName).list().getItems()) {
                if (isGuvnorAlaGenerated(item)) {
                    delegate.imageStreams().inNamespace(prjName).delete(item);
                }
            }
            for (Secret item : delegate.secrets().inNamespace(prjName).list().getItems()) {
                if (isGuvnorAlaGenerated(item)) {
                    delegate.secrets().inNamespace(prjName).delete(item);
                }
            }
            for (ServiceAccount item : delegate.serviceAccounts().inNamespace(prjName).list().getItems()) {
                if (isGuvnorAlaGenerated(item)) {
                    delegate.serviceAccounts().inNamespace(prjName).delete(item);
                }
            }
            // clean up generated project
            if (isGuvnorAlaGenerated(delegate.projects().withName(prjName).get())) {
                delegate.projects().withName(prjName).delete();
            }
        } catch (Throwable t) {
            throw new OpenShiftClientException(t.getMessage(), t);
        }
    }

    private void setGuvnorAlaGenerated(HasMetadata item) {
        if (item != null) {
            ObjectMeta metadata = item.getMetadata();
            Map<String, String> annotations = metadata.getAnnotations();
            if (annotations == null) {
                annotations = new HashMap<String, String>();
                metadata.setAnnotations(annotations);
            }
            annotations.put(GUVNOR_ALA_GENERATED, Boolean.TRUE.toString());
        }
    }

    private boolean isGuvnorAlaGenerated(HasMetadata item) {
        if (item != null) {
            Map<String, String> annotations = item.getMetadata().getAnnotations();
            if (annotations != null) {
                String generated = annotations.get(GUVNOR_ALA_GENERATED);
                return generated != null && Boolean.parseBoolean(generated);
            }
        }
        return false;
    }

    public OpenShiftRuntimeEndpoint getRuntimeEndpoint(String id) throws OpenShiftClientException {
        try {
            OpenShiftRuntimeId runtimeId = OpenShiftRuntimeId.fromString(id);
            String prjName = runtimeId.project();
            String svcName = runtimeId.service();
            OpenShiftRuntimeEndpoint endpoint = new OpenShiftRuntimeEndpoint();
            Route route = delegate.routes().inNamespace(prjName).withName(svcName).get();
            if (route != null) {
                RouteSpec routeSpec = route.getSpec();
                endpoint.setHost(routeSpec.getHost());
                Integer port = null;
                RoutePort routePort = routeSpec.getPort();
                if (routePort != null) {
                    IntOrString targetPort = routePort.getTargetPort();
                    if (targetPort != null) {
                        port = targetPort.getIntVal();
                    }
                }
                endpoint.setPort(port != null && port.intValue() > 0 ? port.intValue() : 80);
            }
            endpoint.setContext("");
            return endpoint;
        } catch (Throwable t) {
            throw new OpenShiftClientException(t.getMessage(), t);
        }
    }

    // Support for OpenShiftRuntimeManager ------------------------------

    public OpenShiftRuntimeState getRuntimeState(String id) throws OpenShiftClientException {
        try {
            return getRuntimeState(OpenShiftRuntimeId.fromString(id));
        } catch (Throwable t) {
            throw new OpenShiftClientException(t.getMessage(), t);
        }
    }

    private OpenShiftRuntimeState getRuntimeState(OpenShiftRuntimeId runtimeId) {
        String prjName = runtimeId.project();
        String svcName = runtimeId.service();
        String state;
        String startedAt;
        Service service = delegate.services().inNamespace(prjName).withName(svcName).get();
        if (service != null) {
            Integer replicas = getReplicas(service);
            if (replicas != null && replicas.intValue() > 0) {
                state = OpenShiftRuntimeState.STARTED;
            } else {
                state = OpenShiftRuntimeState.READY;
            }
            startedAt = service.getMetadata().getCreationTimestamp();
        } else {
            state = OpenShiftRuntimeState.NA;
            startedAt = new Date().toString();
        }
        return new OpenShiftRuntimeState(state, startedAt);
    }

    public void start(String id) throws OpenShiftClientException {
        try {
            setReplicas(id, 1);
        } catch (Throwable t) {
            throw new OpenShiftClientException(t.getMessage(), t);
        }
    }

    public void stop(String id) throws OpenShiftClientException {
        try {
            setReplicas(id, 0);
        } catch (Throwable t) {
            throw new OpenShiftClientException(t.getMessage(), t);
        }
    }

    public void restart(String id) throws OpenShiftClientException {
        // restarting just calls stop and start
        stop(id);
        start(id);
    }

    public void pause(String id) throws OpenShiftClientException {
        // TODO: reevaluate if pausing should indeed just stop
        stop(id);
    }

    private void setReplicas(String id, int replicas) throws InterruptedException {
        OpenShiftRuntimeId runtimeId = OpenShiftRuntimeId.fromString(id);
        String prjName = runtimeId.project();
        String svcName = runtimeId.service();
        Service service = delegate.services().inNamespace(prjName).withName(svcName).get();
        DeployableScalableResource<DeploymentConfig, DoneableDeploymentConfig> dcr = getDeploymentConfigResource(service);
        if (dcr != null) {
            DeploymentConfig dc = dcr.get();
            dc.getSpec().setReplicas(replicas);
            dcr.replace(dc);
            dcr.waitUntilReady(buildTimeout, TimeUnit.MILLISECONDS);
        }
    }

    private Integer getReplicas(Service service) {
        DeployableScalableResource<DeploymentConfig, DoneableDeploymentConfig> dcr = getDeploymentConfigResource(service);
        return dcr != null ? dcr.get().getSpec().getReplicas() : null;
    }

    private DeployableScalableResource<DeploymentConfig, DoneableDeploymentConfig> getDeploymentConfigResource(Service service) {
        if (service != null) {
            String prjName = service.getMetadata().getNamespace();
            String dcName = service.getSpec().getSelector().get("deploymentConfig");
            if (dcName != null) {
                return delegate.deploymentConfigs().inNamespace(prjName).withName(dcName);
            }
        }
        return null;
    }

}
