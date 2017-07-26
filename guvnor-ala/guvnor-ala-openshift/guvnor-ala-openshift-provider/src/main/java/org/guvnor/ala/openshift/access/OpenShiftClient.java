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

    public String getNamespace() {
        return delegate.getConfiguration().getNamespace();
    }

    public OpenShiftRuntimeState create(OpenShiftRuntimeConfig runtimeConfig) throws OpenShiftClientException {
        String ns = getNamespace();
        String svc = runtimeConfig.getServiceName();
        String app = runtimeConfig.getApplicationName();
        OpenShiftRuntimeId id = new OpenShiftRuntimeId(ns, svc, app);
        OpenShiftRuntimeState runtimeState = getRuntimeState(id);
        if (OpenShiftRuntimeState.NA.equals(runtimeState.getState())) {
            createProject();
            createFromUri(runtimeConfig.getResourceSecretsUri());
            createFromUri(runtimeConfig.getResourceStreamsUri());
            createFromTemplate(runtimeConfig);
            runtimeState = getRuntimeState(id);
        }
        return runtimeState;
    }

    private void createProject() {
        String ns = getNamespace();
        if (delegate.projects().withName(ns).get() == null) {
            delegate.projects().createNew().editOrNewMetadata().withName(ns).addToAnnotations(GUVNOR_ALA_GENERATED, Boolean.TRUE.toString()).endMetadata().done();
        }
        addServiceAccountRole("builder", "system:image-builder");
        addServiceAccountRole("default", "admin");
        addServiceAccountRole("default", "view");
        addServiceAccountRole("deployer", "system:deployer");
        addSystemGroupRole("deployer", "system:image-puller");
    }

    private void addServiceAccountRole(String name, String role) {
        String ns = getNamespace();
        Resource<PolicyBinding, DoneablePolicyBinding> bindingResource = delegate.policyBindings().inNamespace(ns).withName(":default");
        DoneablePolicyBinding binding;
        if (bindingResource.get() == null) {
            binding = bindingResource.createNew();
        } else {
            binding = bindingResource.edit();
        }
        binding.editOrNewMetadata().withName(":default").endMetadata().editOrNewPolicyRef().withName("default").endPolicyRef().addNewRoleBinding().withName(role).editOrNewRoleBinding().editOrNewMetadata().withName(role).withNamespace(ns).endMetadata().addToUserNames("system:serviceaccount:" + ns + ":" + name).addNewSubject().withName("default").withNamespace(ns).withKind("ServiceAccount").endSubject().withNewRoleRef().withName(role).endRoleRef().endRoleBinding().endRoleBinding().done();
    }

    private void addSystemGroupRole(String name, String role) {
        String ns = getNamespace();
        Resource<PolicyBinding, DoneablePolicyBinding> bindingResource = delegate.policyBindings().inNamespace(ns).withName(":default");
        DoneablePolicyBinding binding;
        if (bindingResource.get() == null) {
            binding = bindingResource.createNew();
        } else {
            binding = bindingResource.edit();
        }
        binding.editOrNewMetadata().withName(":default").endMetadata().editOrNewPolicyRef().withName("default").endPolicyRef().addNewRoleBinding().withName(role).editOrNewRoleBinding().editOrNewMetadata().withName(role).withNamespace(ns).endMetadata().addToGroupNames("system:serviceaccounts:" + ns).addNewSubject().withName("default").withNamespace(ns).withKind("SystemGroup").endSubject().withNewRoleRef().withName(role).endRoleRef().endRoleBinding().endRoleBinding().done();
    }

    private void createFromUri(String uri) throws OpenShiftClientException {
        URL url = toUrl(uri);
        if (url != null) {
            String ns = getNamespace();
            KubernetesList kubeList = delegate.lists().load(url).get();
            List<HasMetadata> items = kubeList.getItems();
            if (items.size() > 0) {
                for (HasMetadata item : items) {
                    String name = item.getMetadata().getName();
                    if (item instanceof ServiceAccount) {
                        if (delegate.serviceAccounts().inNamespace(ns).withName(name).get() == null) {
                            setGuvnorAlaGenerated(item);
                        }
                    } else if (item instanceof Secret) {
                        if (delegate.secrets().inNamespace(ns).withName(name).get() == null) {
                            setGuvnorAlaGenerated(item);
                        }
                    } else if (item instanceof ImageStream) {
                        if (delegate.imageStreams().inNamespace(ns).withName(name).get() == null) {
                            setGuvnorAlaGenerated(item);
                        }
                    }
                }
                delegate.lists().inNamespace(ns).create(kubeList);
            }
        }
    }

    private void createFromTemplate(OpenShiftRuntimeConfig runtimeConfig) throws OpenShiftClientException {
        KubernetesList kubeList = processTemplate(runtimeConfig);
        if (kubeList != null && kubeList.getItems().size() > 0) {
            try {
                DeploymentConfig dc = getDeploymentConfig(kubeList, runtimeConfig.getServiceName());
                if (dc != null) {
                    dc.getSpec().setReplicas(0);
                }
                delegate.lists().inNamespace(getNamespace()).create(kubeList);
            } catch (Throwable t) {
                throw new OpenShiftClientException(t.getMessage(), t);
            }
        }
    }

    private KubernetesList processTemplate(OpenShiftRuntimeConfig runtimeConfig) throws OpenShiftClientException {
        String ns = getNamespace();
        URL url = toUrl(runtimeConfig.getResourceTemplateUri());
        if (url != null) {
            Map<String, String> params = OpenShiftParameters.fromRuntimeConfig(runtimeConfig);
            return delegate.templates().inNamespace(ns).load(url).process(params);
        } else {
            String templateName = runtimeConfig.getResourceTemplateName();
            if (templateName != null && !templateName.isEmpty()) {
                Map<String, String> params = OpenShiftParameters.fromRuntimeConfig(runtimeConfig);
                return delegate.templates().inNamespace(ns).withName(templateName).process(params);
            }
        }
        return null;
    }

    private URL toUrl(String uri) throws OpenShiftClientException {
        if (uri != null && !uri.isEmpty()) {
            URL url;
            try {
                url = URI.create(uri).toURL();
            } catch (MalformedURLException ex) {
                throw new OpenShiftClientException(ex.getMessage(), ex);
            }
            return url;
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

    private static final String APP_LABEL = "application";

    public void destroy(String id) throws OpenShiftClientException {
        try {
            OpenShiftRuntimeId runtimeId = OpenShiftRuntimeId.fromString(id);
            String ns = runtimeId.namespace();
            String svc = runtimeId.service();
            // TODO: should we always depend on the app label being specified, or gotten from the service?
            String app = runtimeId.application();
            if (app == null || app.isEmpty()) {
                Service service = delegate.services().inNamespace(ns).withName(svc).get();
                if (service != null) {
                    app = service.getMetadata().getLabels().get(APP_LABEL);
                }
            }
            /*
             * cascading delete of deploymentConfigs means we don't have to also do the following:
             *     delegate.deploymentConfigs().inNamespace(ns).withLabel(APP_LABEL, app).delete();
             *     delegate.replicationControllers().inNamespace(ns).withLabel(APP_LABEL, app).delete();
             *     delegate.pods().inNamespace(ns).withLabel(APP_LABEL, app).delete();
             * , but deleting services and routes are still necessary:
             */
            delegate.deploymentConfigs().inNamespace(ns).withName(svc).cascading(true).delete();
            delegate.services().inNamespace(ns).withLabel(APP_LABEL, app).delete();
            delegate.routes().inNamespace(ns).withLabel(APP_LABEL, app).delete();
            // clean up any generated image streams, secrets, and service accounts
            for (ImageStream item : delegate.imageStreams().inNamespace(ns).list().getItems()) {
                if (isGuvnorAlaGenerated(item)) {
                    delegate.imageStreams().inNamespace(ns).delete(item);
                }
            }
            for (Secret item : delegate.secrets().inNamespace(ns).list().getItems()) {
                if (isGuvnorAlaGenerated(item)) {
                    delegate.secrets().inNamespace(ns).delete(item);
                }
            }
            for (ServiceAccount item : delegate.serviceAccounts().inNamespace(ns).list().getItems()) {
                if (isGuvnorAlaGenerated(item)) {
                    delegate.serviceAccounts().inNamespace(ns).delete(item);
                }
            }
            // clean up generated project
            if (isGuvnorAlaGenerated(delegate.projects().withName(ns).get())) {
                delegate.projects().withName(ns).delete();
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
        return getRuntimeEndpoint(OpenShiftRuntimeId.fromString(id));
    }

    public OpenShiftRuntimeEndpoint getRuntimeEndpoint(OpenShiftRuntimeId id) throws OpenShiftClientException {
        try {
            String ns = id.namespace();
            String svc = id.service();
            OpenShiftRuntimeEndpoint endpoint = new OpenShiftRuntimeEndpoint();
            Route route = delegate.routes().inNamespace(ns).withName(svc).get();
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
        return getRuntimeState(OpenShiftRuntimeId.fromString(id));
    }

    public OpenShiftRuntimeState getRuntimeState(OpenShiftRuntimeId id) throws OpenShiftClientException {
        try {
            String ns = id.namespace();
            String svc = id.service();
            String state;
            String startedAt;
            Service service = delegate.services().inNamespace(ns).withName(svc).get();
            if (service != null) {
                Integer replicas = null;
                String dcName = service.getSpec().getSelector().get("deploymentConfig");
                if (dcName != null) {
                    DeploymentConfig dc = delegate.deploymentConfigs().inNamespace(ns).withName(dcName).get();
                    if (dc != null) {
                        replicas = dc.getStatus().getReplicas();
                    }
                }
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
        } catch (Throwable t) {
            throw new OpenShiftClientException(t.getMessage(), t);
        }
    }

    private void scale(String id, int count) throws OpenShiftClientException {
        try {
            OpenShiftRuntimeId runtimeId = OpenShiftRuntimeId.fromString(id);
            String ns = runtimeId.namespace();
            String svc = runtimeId.service();
            Service service = delegate.services().inNamespace(ns).withName(svc).get();
            if (service != null) {
                String dcName = service.getSpec().getSelector().get("deploymentConfig");
                if (dcName != null) {
                    DeployableScalableResource<DeploymentConfig, DoneableDeploymentConfig> dcr = delegate.deploymentConfigs().inNamespace(ns).withName(dcName);
                    if (dcr != null) {
                        DeploymentConfig dc = dcr.get();
                        dc.getSpec().setReplicas(count);
                        dcr.replace(dc);
                        dcr.waitUntilReady(buildTimeout, TimeUnit.MILLISECONDS);
                    }
                }
            }
        } catch (Throwable t) {
            throw new OpenShiftClientException(t.getMessage(), t);
        }
    }

    public void start(String id) throws OpenShiftClientException {
        scale(id, 1);
    }

    public void stop(String id) throws OpenShiftClientException {
        scale(id, 0);
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

}
