package org.drools.guvnor.client.explorer;

import com.google.gwt.user.client.ui.IsWidget;

public interface KnowledgeBaseConfigurationFormView
        extends IsWidget {

    void setName(String name);

    void setNamespace(String namespace);
}
