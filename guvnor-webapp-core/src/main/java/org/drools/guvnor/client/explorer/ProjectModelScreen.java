package org.drools.guvnor.client.explorer;


import com.google.gwt.user.client.ui.IsWidget;
import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.annotations.WorkbenchPartView;
import org.uberfire.client.annotations.WorkbenchScreen;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import java.util.HashMap;

@Dependent
@WorkbenchScreen(identifier = "projectModel")
public class ProjectModelScreen
        implements ProjectModelScreenView.Presenter {

    private final ProjectModelScreenView view;
    private final HashMap<String, KnowledgeBaseConfiguration> configurations = new HashMap<String, KnowledgeBaseConfiguration>();

    @Inject
    public ProjectModelScreen(ProjectModelScreenView view) {
        this.view = view;
        view.setPresenter(this);

        createTempMockConf("org.test1", "KBase1");
        createTempMockConf("org.test1", "KBase2");

        for (String key : configurations.keySet()) {
            view.addKnowledgeBaseConfiguration(key);
        }
    }

    private KnowledgeBaseConfiguration createTempMockConf(String namespace, String name) {
        KnowledgeBaseConfiguration knowledgeBaseConfiguration = new KnowledgeBaseConfiguration();
        knowledgeBaseConfiguration.setNamespace(namespace);
        knowledgeBaseConfiguration.setName(name);

        configurations.put(knowledgeBaseConfiguration.getFullName(), knowledgeBaseConfiguration);

        return knowledgeBaseConfiguration;
    }

    @WorkbenchPartView
    public IsWidget asWidget() {
        return view;
    }

    @WorkbenchPartTitle
    public String getTitle() {
        return "Project Model"; // TODO : -Rikkola-
    }

    @Override
    public void onKBaseSelection(String name) {
        view.showForm(configurations.get(name));
    }
}
