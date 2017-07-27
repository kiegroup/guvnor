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

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.guvnor.ala.config.CloneableConfig;
import org.guvnor.ala.openshift.config.OpenShiftProviderConfig;

/**
 * Cloneable implementation of OpenShiftProviderConfig.
 */
public class OpenShiftProviderConfigImpl implements OpenShiftProviderConfig, CloneableConfig<OpenShiftProviderConfig> {

    // openshift provider properties
    private String name;
    // openshift client properties
    private String allProxy;
    private String httpProxy;
    private String httpsProxy;
    private String kubernetesApiVersion;
    private String kubernetesAuthBasicPassword;
    private String kubernetesAuthBasicUsername;
    private String kubernetesAuthToken;
    private String kubernetesCertsCaData;
    private String kubernetesCertsCaFile;
    private String kubernetesCertsClientData;
    private String kubernetesCertsClientFile;
    private String kubernetesCertsClientKeyAlgo;
    private String kubernetesCertsClientKeyData;
    private String kubernetesCertsClientKeyFile;
    private String kubernetesCertsClientKeyPassphrase;
    private String kubernetesConnectionTimeout;
    private String kubernetesKeystoreFile;
    private String kubernetesKeystorePassphrase;
    private String kubernetesLoggingInterval;
    private String kubernetesMaster;
    private String kubernetesNamespace;
    private String kubernetesOapiVersion;
    private String kubernetesRequestTimeout;
    private String kubernetesRollingTimeout;
    private String kubernetesScaleTimeout;
    private String kubernetesTlsVersions;
    private String kubernetesTrustCertificates;
    private String kubernetesTruststoreFile;
    private String kubernetesTruststorePassphrase;
    private String kubernetesUserAgent;
    private String kubernetesWatchReconnectInterval;
    private String kubernetesWatchReconnectLimit;
    private String kubernetesWebsocketPingInterval;
    private String kubernetesWebsocketTimeout;
    private String noProxy;
    private String openshiftBuildTimeout;
    private String openshiftUrl;
    private String proxyPassword;
    private String proxyUsername;

    public OpenShiftProviderConfigImpl() {
        // openshift provider properties
        setName(OpenShiftProviderConfig.super.getName());
        // openshift client properties
        setAllProxy(OpenShiftProviderConfig.super.getAllProxy());
        setHttpProxy(OpenShiftProviderConfig.super.getHttpProxy());
        setHttpsProxy(OpenShiftProviderConfig.super.getHttpsProxy());
        setKubernetesApiVersion(OpenShiftProviderConfig.super.getKubernetesApiVersion());
        setKubernetesAuthBasicPassword(OpenShiftProviderConfig.super.getKubernetesAuthBasicPassword());
        setKubernetesAuthBasicUsername(OpenShiftProviderConfig.super.getKubernetesAuthBasicUsername());
        setKubernetesAuthToken(OpenShiftProviderConfig.super.getKubernetesAuthToken());
        setKubernetesCertsCaData(OpenShiftProviderConfig.super.getKubernetesCertsCaData());
        setKubernetesCertsCaFile(OpenShiftProviderConfig.super.getKubernetesCertsCaFile());
        setKubernetesCertsClientData(OpenShiftProviderConfig.super.getKubernetesCertsClientData());
        setKubernetesCertsClientFile(OpenShiftProviderConfig.super.getKubernetesCertsClientFile());
        setKubernetesCertsClientKeyAlgo(OpenShiftProviderConfig.super.getKubernetesCertsClientKeyAlgo());
        setKubernetesCertsClientKeyData(OpenShiftProviderConfig.super.getKubernetesCertsClientKeyData());
        setKubernetesCertsClientKeyFile(OpenShiftProviderConfig.super.getKubernetesCertsClientKeyFile());
        setKubernetesCertsClientKeyPassphrase(OpenShiftProviderConfig.super.getKubernetesCertsClientKeyPassphrase());
        setKubernetesConnectionTimeout(OpenShiftProviderConfig.super.getKubernetesConnectionTimeout());
        setKubernetesKeystoreFile(OpenShiftProviderConfig.super.getKubernetesKeystoreFile());
        setKubernetesKeystorePassphrase(OpenShiftProviderConfig.super.getKubernetesKeystorePassphrase());
        setKubernetesLoggingInterval(OpenShiftProviderConfig.super.getKubernetesLoggingInterval());
        setKubernetesMaster(OpenShiftProviderConfig.super.getKubernetesMaster());
        setKubernetesNamespace(OpenShiftProviderConfig.super.getKubernetesNamespace());
        setKubernetesOapiVersion(OpenShiftProviderConfig.super.getKubernetesOapiVersion());
        setKubernetesRequestTimeout(OpenShiftProviderConfig.super.getKubernetesRequestTimeout());
        setKubernetesRollingTimeout(OpenShiftProviderConfig.super.getKubernetesRollingTimeout());
        setKubernetesScaleTimeout(OpenShiftProviderConfig.super.getKubernetesScaleTimeout());
        setKubernetesTlsVersions(OpenShiftProviderConfig.super.getKubernetesTlsVersions());
        setKubernetesTrustCertificates(OpenShiftProviderConfig.super.getKubernetesTrustCertificates());
        setKubernetesTruststoreFile(OpenShiftProviderConfig.super.getKubernetesTruststoreFile());
        setKubernetesTruststorePassphrase(OpenShiftProviderConfig.super.getKubernetesTruststorePassphrase());
        setKubernetesUserAgent(OpenShiftProviderConfig.super.getKubernetesUserAgent());
        setKubernetesWatchReconnectInterval(OpenShiftProviderConfig.super.getKubernetesWatchReconnectInterval());
        setKubernetesWatchReconnectLimit(OpenShiftProviderConfig.super.getKubernetesWatchReconnectLimit());
        setKubernetesWebsocketPingInterval(OpenShiftProviderConfig.super.getKubernetesWebsocketPingInterval());
        setKubernetesWebsocketTimeout(OpenShiftProviderConfig.super.getKubernetesWebsocketTimeout());
        setNoProxy(OpenShiftProviderConfig.super.getNoProxy());
        setOpenshiftBuildTimeout(OpenShiftProviderConfig.super.getOpenshiftBuildTimeout());
        setOpenshiftUrl(OpenShiftProviderConfig.super.getOpenshiftUrl());
        setProxyPassword(OpenShiftProviderConfig.super.getProxyPassword());
        setProxyUsername(OpenShiftProviderConfig.super.getProxyUsername());
    }

    public OpenShiftProviderConfigImpl(OpenShiftProviderConfig origin) {
        if (origin != null) {
            // openshift provider properties
            setName(origin.getName());
            // openshift client properties
            setAllProxy(origin.getAllProxy());
            setHttpProxy(origin.getHttpProxy());
            setHttpsProxy(origin.getHttpsProxy());
            setKubernetesApiVersion(origin.getKubernetesApiVersion());
            setKubernetesAuthBasicPassword(origin.getKubernetesAuthBasicPassword());
            setKubernetesAuthBasicUsername(origin.getKubernetesAuthBasicUsername());
            setKubernetesAuthToken(origin.getKubernetesAuthToken());
            setKubernetesCertsCaData(origin.getKubernetesCertsCaData());
            setKubernetesCertsCaFile(origin.getKubernetesCertsCaFile());
            setKubernetesCertsClientData(origin.getKubernetesCertsClientData());
            setKubernetesCertsClientFile(origin.getKubernetesCertsClientFile());
            setKubernetesCertsClientKeyAlgo(origin.getKubernetesCertsClientKeyAlgo());
            setKubernetesCertsClientKeyData(origin.getKubernetesCertsClientKeyData());
            setKubernetesCertsClientKeyFile(origin.getKubernetesCertsClientKeyFile());
            setKubernetesCertsClientKeyPassphrase(origin.getKubernetesCertsClientKeyPassphrase());
            setKubernetesConnectionTimeout(origin.getKubernetesConnectionTimeout());
            setKubernetesKeystoreFile(origin.getKubernetesKeystoreFile());
            setKubernetesKeystorePassphrase(origin.getKubernetesKeystorePassphrase());
            setKubernetesLoggingInterval(origin.getKubernetesLoggingInterval());
            setKubernetesMaster(origin.getKubernetesMaster());
            setKubernetesNamespace(origin.getKubernetesNamespace());
            setKubernetesOapiVersion(origin.getKubernetesOapiVersion());
            setKubernetesRequestTimeout(origin.getKubernetesRequestTimeout());
            setKubernetesRollingTimeout(origin.getKubernetesRollingTimeout());
            setKubernetesScaleTimeout(origin.getKubernetesScaleTimeout());
            setKubernetesTlsVersions(origin.getKubernetesTlsVersions());
            setKubernetesTrustCertificates(origin.getKubernetesTrustCertificates());
            setKubernetesTruststoreFile(origin.getKubernetesTruststoreFile());
            setKubernetesTruststorePassphrase(origin.getKubernetesTruststorePassphrase());
            setKubernetesUserAgent(origin.getKubernetesUserAgent());
            setKubernetesWatchReconnectInterval(origin.getKubernetesWatchReconnectInterval());
            setKubernetesWatchReconnectLimit(origin.getKubernetesWatchReconnectLimit());
            setKubernetesWebsocketPingInterval(origin.getKubernetesWebsocketPingInterval());
            setKubernetesWebsocketTimeout(origin.getKubernetesWebsocketTimeout());
            setNoProxy(origin.getNoProxy());
            setOpenshiftBuildTimeout(origin.getOpenshiftBuildTimeout());
            setOpenshiftUrl(origin.getOpenshiftUrl());
            setProxyPassword(origin.getProxyPassword());
            setProxyUsername(origin.getProxyUsername());
        }
    }

    @JsonIgnore
    public OpenShiftProviderConfigImpl clear() {
        // openshift provider properties
        setName(null);
        // openshift client properties
        setAllProxy(null);
        setHttpProxy(null);
        setHttpsProxy(null);
        setKubernetesApiVersion(null);
        setKubernetesAuthBasicPassword(null);
        setKubernetesAuthBasicUsername(null);
        setKubernetesAuthToken(null);
        setKubernetesCertsCaData(null);
        setKubernetesCertsCaFile(null);
        setKubernetesCertsClientData(null);
        setKubernetesCertsClientFile(null);
        setKubernetesCertsClientKeyAlgo(null);
        setKubernetesCertsClientKeyData(null);
        setKubernetesCertsClientKeyFile(null);
        setKubernetesCertsClientKeyPassphrase(null);
        setKubernetesConnectionTimeout(null);
        setKubernetesKeystoreFile(null);
        setKubernetesKeystorePassphrase(null);
        setKubernetesLoggingInterval(null);
        setKubernetesMaster(null);
        setKubernetesNamespace(null);
        setKubernetesOapiVersion(null);
        setKubernetesRequestTimeout(null);
        setKubernetesRollingTimeout(null);
        setKubernetesScaleTimeout(null);
        setKubernetesTlsVersions(null);
        setKubernetesTrustCertificates(null);
        setKubernetesTruststoreFile(null);
        setKubernetesTruststorePassphrase(null);
        setKubernetesUserAgent(null);
        setKubernetesWatchReconnectInterval(null);
        setKubernetesWatchReconnectLimit(null);
        setKubernetesWebsocketPingInterval(null);
        setKubernetesWebsocketTimeout(null);
        setNoProxy(null);
        setOpenshiftBuildTimeout(null);
        setOpenshiftUrl(null);
        setProxyPassword(null);
        setProxyUsername(null);
        return this;
    }

    // openshift provider properties

    @Override
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    // openshift client properties

    @Override
    public String getAllProxy() {
        return allProxy;
    }

    public void setAllProxy(String allProxy) {
        this.allProxy = allProxy;
    }

    @Override
    public String getHttpProxy() {
        return httpProxy;
    }

    public void setHttpProxy(String httpProxy) {
        this.httpProxy = httpProxy;
    }

    @Override
    public String getHttpsProxy() {
        return httpsProxy;
    }

    public void setHttpsProxy(String httpsProxy) {
        this.httpsProxy = httpsProxy;
    }

    @Override
    public String getKubernetesApiVersion() {
        return kubernetesApiVersion;
    }

    public void setKubernetesApiVersion(String kubernetesApiVersion) {
        this.kubernetesApiVersion = kubernetesApiVersion;
    }

    @Override
    public String getKubernetesAuthBasicPassword() {
        return kubernetesAuthBasicPassword;
    }

    public void setKubernetesAuthBasicPassword(String kubernetesAuthBasicPassword) {
        this.kubernetesAuthBasicPassword = kubernetesAuthBasicPassword;
    }

    @Override
    public String getKubernetesAuthBasicUsername() {
        return kubernetesAuthBasicUsername;
    }

    public void setKubernetesAuthBasicUsername(String kubernetesAuthBasicUsername) {
        this.kubernetesAuthBasicUsername = kubernetesAuthBasicUsername;
    }

    @Override
    public String getKubernetesAuthToken() {
        return kubernetesAuthToken;
    }

    public void setKubernetesAuthToken(String kubernetesAuthToken) {
        this.kubernetesAuthToken = kubernetesAuthToken;
    }

    @Override
    public String getKubernetesCertsCaData() {
        return kubernetesCertsCaData;
    }

    public void setKubernetesCertsCaData(String kubernetesCertsCaData) {
        this.kubernetesCertsCaData = kubernetesCertsCaData;
    }

    @Override
    public String getKubernetesCertsCaFile() {
        return kubernetesCertsCaFile;
    }

    public void setKubernetesCertsCaFile(String kubernetesCertsCaFile) {
        this.kubernetesCertsCaFile = kubernetesCertsCaFile;
    }

    @Override
    public String getKubernetesCertsClientData() {
        return kubernetesCertsClientData;
    }

    public void setKubernetesCertsClientData(String kubernetesCertsClientData) {
        this.kubernetesCertsClientData = kubernetesCertsClientData;
    }

    @Override
    public String getKubernetesCertsClientFile() {
        return kubernetesCertsClientFile;
    }

    public void setKubernetesCertsClientFile(String kubernetesCertsClientFile) {
        this.kubernetesCertsClientFile = kubernetesCertsClientFile;
    }

    @Override
    public String getKubernetesCertsClientKeyAlgo() {
        return kubernetesCertsClientKeyAlgo;
    }

    public void setKubernetesCertsClientKeyAlgo(String kubernetesCertsClientKeyAlgo) {
        this.kubernetesCertsClientKeyAlgo = kubernetesCertsClientKeyAlgo;
    }

    @Override
    public String getKubernetesCertsClientKeyData() {
        return kubernetesCertsClientKeyData;
    }

    public void setKubernetesCertsClientKeyData(String kubernetesCertsClientKeyData) {
        this.kubernetesCertsClientKeyData = kubernetesCertsClientKeyData;
    }

    @Override
    public String getKubernetesCertsClientKeyFile() {
        return kubernetesCertsClientKeyFile;
    }

    public void setKubernetesCertsClientKeyFile(String kubernetesCertsClientKeyFile) {
        this.kubernetesCertsClientKeyFile = kubernetesCertsClientKeyFile;
    }

    @Override
    public String getKubernetesCertsClientKeyPassphrase() {
        return kubernetesCertsClientKeyPassphrase;
    }

    public void setKubernetesCertsClientKeyPassphrase(String kubernetesCertsClientKeyPassphrase) {
        this.kubernetesCertsClientKeyPassphrase = kubernetesCertsClientKeyPassphrase;
    }

    @Override
    public String getKubernetesConnectionTimeout() {
        return kubernetesConnectionTimeout;
    }

    public void setKubernetesConnectionTimeout(String kubernetesConnectionTimeout) {
        this.kubernetesConnectionTimeout = kubernetesConnectionTimeout;
    }

    @Override
    public String getKubernetesKeystoreFile() {
        return kubernetesKeystoreFile;
    }

    public void setKubernetesKeystoreFile(String kubernetesKeystoreFile) {
        this.kubernetesKeystoreFile = kubernetesKeystoreFile;
    }

    @Override
    public String getKubernetesKeystorePassphrase() {
        return kubernetesKeystorePassphrase;
    }

    public void setKubernetesKeystorePassphrase(String kubernetesKeystorePassphrase) {
        this.kubernetesKeystorePassphrase = kubernetesKeystorePassphrase;
    }

    @Override
    public String getKubernetesLoggingInterval() {
        return kubernetesLoggingInterval;
    }

    public void setKubernetesLoggingInterval(String kubernetesLoggingInterval) {
        this.kubernetesLoggingInterval = kubernetesLoggingInterval;
    }

    @Override
    public String getKubernetesMaster() {
        return kubernetesMaster;
    }

    public void setKubernetesMaster(String kubernetesMaster) {
        this.kubernetesMaster = kubernetesMaster;
    }

    @Override
    public String getKubernetesNamespace() {
        return kubernetesNamespace;
    }

    public void setKubernetesNamespace(String kubernetesNamespace) {
        this.kubernetesNamespace = kubernetesNamespace;
    }

    @Override
    public String getKubernetesOapiVersion() {
        return kubernetesOapiVersion;
    }

    public void setKubernetesOapiVersion(String kubernetesOapiVersion) {
        this.kubernetesOapiVersion = kubernetesOapiVersion;
    }

    @Override
    public String getKubernetesRequestTimeout() {
        return kubernetesRequestTimeout;
    }

    public void setKubernetesRequestTimeout(String kubernetesRequestTimeout) {
        this.kubernetesRequestTimeout = kubernetesRequestTimeout;
    }

    @Override
    public String getKubernetesRollingTimeout() {
        return kubernetesRollingTimeout;
    }

    public void setKubernetesRollingTimeout(String kubernetesRollingTimeout) {
        this.kubernetesRollingTimeout = kubernetesRollingTimeout;
    }

    @Override
    public String getKubernetesScaleTimeout() {
        return kubernetesScaleTimeout;
    }

    public void setKubernetesScaleTimeout(String kubernetesScaleTimeout) {
        this.kubernetesScaleTimeout = kubernetesScaleTimeout;
    }

    @Override
    public String getKubernetesTlsVersions() {
        return kubernetesTlsVersions;
    }

    public void setKubernetesTlsVersions(String kubernetesTlsVersions) {
        this.kubernetesTlsVersions = kubernetesTlsVersions;
    }

    @Override
    public String getKubernetesTrustCertificates() {
        return kubernetesTrustCertificates;
    }

    public void setKubernetesTrustCertificates(String kubernetesTrustCertificates) {
        this.kubernetesTrustCertificates = kubernetesTrustCertificates;
    }

    @Override
    public String getKubernetesTruststoreFile() {
        return kubernetesTruststoreFile;
    }

    public void setKubernetesTruststoreFile(String kubernetesTruststoreFile) {
        this.kubernetesTruststoreFile = kubernetesTruststoreFile;
    }

    @Override
    public String getKubernetesTruststorePassphrase() {
        return kubernetesTruststorePassphrase;
    }

    public void setKubernetesTruststorePassphrase(String kubernetesTruststorePassphrase) {
        this.kubernetesTruststorePassphrase = kubernetesTruststorePassphrase;
    }

    @Override
    public String getKubernetesUserAgent() {
        return kubernetesUserAgent;
    }

    public void setKubernetesUserAgent(String kubernetesUserAgent) {
        this.kubernetesUserAgent = kubernetesUserAgent;
    }

    @Override
    public String getKubernetesWatchReconnectInterval() {
        return kubernetesWatchReconnectInterval;
    }

    public void setKubernetesWatchReconnectInterval(String kubernetesWatchReconnectInterval) {
        this.kubernetesWatchReconnectInterval = kubernetesWatchReconnectInterval;
    }

    @Override
    public String getKubernetesWatchReconnectLimit() {
        return kubernetesWatchReconnectLimit;
    }

    public void setKubernetesWatchReconnectLimit(String kubernetesWatchReconnectLimit) {
        this.kubernetesWatchReconnectLimit = kubernetesWatchReconnectLimit;
    }

    @Override
    public String getKubernetesWebsocketPingInterval() {
        return kubernetesWebsocketPingInterval;
    }

    public void setKubernetesWebsocketPingInterval(String kubernetesWebsocketPingInterval) {
        this.kubernetesWebsocketPingInterval = kubernetesWebsocketPingInterval;
    }

    @Override
    public String getKubernetesWebsocketTimeout() {
        return kubernetesWebsocketTimeout;
    }

    public void setKubernetesWebsocketTimeout(String kubernetesWebsocketTimeout) {
        this.kubernetesWebsocketTimeout = kubernetesWebsocketTimeout;
    }

    @Override
    public String getNoProxy() {
        return noProxy;
    }

    public void setNoProxy(String noProxy) {
        this.noProxy = noProxy;
    }

    @Override
    public String getOpenshiftBuildTimeout() {
        return openshiftBuildTimeout;
    }

    public void setOpenshiftBuildTimeout(String openshiftBuildTimeout) {
        this.openshiftBuildTimeout = openshiftBuildTimeout;
    }

    @Override
    public String getOpenshiftUrl() {
        return openshiftUrl;
    }

    public void setOpenshiftUrl(String openshiftUrl) {
        this.openshiftUrl = openshiftUrl;
    }

    @Override
    public String getProxyPassword() {
        return proxyPassword;
    }

    public void setProxyPassword(String proxyPassword) {
        this.proxyPassword = proxyPassword;
    }

    @Override
    public String getProxyUsername() {
        return proxyUsername;
    }

    public void setProxyUsername(String proxyUsername) {
        this.proxyUsername = proxyUsername;
    }

    @Override
    public String toString() {
        return "OpenShiftClientConfigImpl{name=" + name +
                ", httpProxy=" + httpProxy +
                ", httpsProxy=" + httpsProxy +
                ", kubernetesApiVersion=" + kubernetesApiVersion +
                ", kubernetesAuthBasicPassword=" + kubernetesAuthBasicPassword +
                ", kubernetesAuthBasicUsername=" + kubernetesAuthBasicUsername +
                ", kubernetesAuthToken=" + kubernetesAuthToken +
                ", kubernetesCertsCaData=" + kubernetesCertsCaData +
                ", kubernetesCertsCaFile=" + kubernetesCertsCaFile +
                ", kubernetesCertsClientData=" + kubernetesCertsClientData +
                ", kubernetesCertsClientFile=" + kubernetesCertsClientFile +
                ", kubernetesCertsClientKeyAlgo=" + kubernetesCertsClientKeyAlgo +
                ", kubernetesCertsClientKeyData=" + kubernetesCertsClientKeyData +
                ", kubernetesCertsClientKeyFile=" + kubernetesCertsClientKeyFile +
                ", kubernetesCertsClientKeyPassphrase=" + kubernetesCertsClientKeyPassphrase +
                ", kubernetesConnectionTimeout=" + kubernetesConnectionTimeout +
                ", kubernetesKeystoreFile=" + kubernetesKeystoreFile +
                ", kubernetesKeystorePassphrase=" + kubernetesKeystorePassphrase +
                ", kubernetesLoggingInterval=" + kubernetesLoggingInterval +
                ", kubernetesMaster=" + kubernetesMaster +
                ", kubernetesNamespace=" + kubernetesNamespace +
                ", kubernetesOapiVersion=" + kubernetesOapiVersion +
                ", kubernetesRequestTimeout=" + kubernetesRequestTimeout +
                ", kubernetesRollingTimeout=" + kubernetesRollingTimeout +
                ", kubernetesScaleTimeout=" + kubernetesScaleTimeout +
                ", kubernetesTlsVersions=" + kubernetesTlsVersions +
                ", kubernetesTrustCertificates=" + kubernetesTrustCertificates +
                ", kubernetesTruststoreFile=" + kubernetesTruststoreFile +
                ", kubernetesTruststorePassphrase=" + kubernetesTruststorePassphrase +
                ", kubernetesUserAgent=" + kubernetesUserAgent +
                ", kubernetesWatchReconnectInterval=" + kubernetesWatchReconnectInterval +
                ", kubernetesWatchReconnectLimit=" + kubernetesWatchReconnectLimit +
                ", kubernetesWebsocketPingInterval=" + kubernetesWebsocketPingInterval +
                ", kubernetesWebsocketTimeout=" + kubernetesWebsocketTimeout +
                ", noProxy=" + noProxy +
                ", openshiftBuildTimeout=" + openshiftBuildTimeout +
                ", openshiftUrl=" + openshiftUrl +
                ", proxyPassword=" + proxyPassword +
                ", proxyUsername=" + proxyUsername +
                "}";
    }

    @Override
    public OpenShiftProviderConfig asNewClone(OpenShiftProviderConfig origin) {
        return new OpenShiftProviderConfigImpl(origin);
    }

}
