package org.drools.repository;

import javax.jcr.Node;
import javax.jcr.RepositoryException;

public class IFramePerspectiveConfigurationItem {

    public static final String CONFIGURATION_NODE_TYPE_NAME = "drools:configurationNodeType";
    public static final String TITLE_PROPERTY_NAME = "drools:title";
    public static final String URL_PROPERTY_NAME = "drools:url";
    private Node node;

    public IFramePerspectiveConfigurationItem(Node configurationPackageNode) {
        this.node = configurationPackageNode;
    }

    public String getUuid() {
        try {
            return node.getIdentifier();
        } catch (RepositoryException e) {
            throw new RulesRepositoryException(e);
        }
    }

    public String getName() {
        return getStringProperty(TITLE_PROPERTY_NAME);
    }

    public String getUrl() {
        return getStringProperty(URL_PROPERTY_NAME);
    }

    public void setName(String name) {
        setStringValue(TITLE_PROPERTY_NAME, name);
    }

    public void setUrl(String url) {
        setStringValue(URL_PROPERTY_NAME, url);
    }

    private String getStringProperty(String propertyName) {
        try {
            return node.getProperty(propertyName).getValue().getString();
        } catch (RepositoryException e) {
            throw new RulesRepositoryException(e);
        }
    }

    private void setStringValue(String propertyName, String name) {
        try {
            node.getProperty(propertyName).setValue(name);
        } catch (RepositoryException e) {
            throw new RulesRepositoryException(e);
        }
    }

    public void remove() {
        try {
            node.remove();
        } catch (RepositoryException e) {
            throw new RulesRepositoryException(e);
        }
    }
}
