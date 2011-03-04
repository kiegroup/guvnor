package org.drools.guvnor.client.explorer;

import org.drools.guvnor.client.common.GenericCallback;
import org.drools.guvnor.client.rpc.ConfigurationServiceAsync;
import org.drools.guvnor.client.rpc.IFramePerspectiveConfiguration;

import java.util.ArrayList;
import java.util.Collection;

public class PerspectiveLoader {

    private ConfigurationServiceAsync configurationService;

    public PerspectiveLoader(ConfigurationServiceAsync configurationService) {
        this.configurationService = configurationService;
    }

    public void loadPerspectives(final LoadPerspectives loadPerspectives) {
        configurationService.loadPerspectiveConfigurations(new GenericCallback<Collection<IFramePerspectiveConfiguration>>() {
            public void onSuccess(Collection<IFramePerspectiveConfiguration> perspectivesConfigurations) {
                handleResult(perspectivesConfigurations, loadPerspectives);
            }
        });
    }

    protected void handleResult(Collection<IFramePerspectiveConfiguration> perspectivesConfigurations, LoadPerspectives loadPerspectives) {
        Collection<Perspective> perspectives = new ArrayList<Perspective>();

        perspectives.add(getDefault());

        for (IFramePerspectiveConfiguration perspectivesConfiguration : perspectivesConfigurations) {
            perspectives.add(createIFramePerspective(perspectivesConfiguration));
        }

        loadPerspectives.loadPerspectives(perspectives);
    }

    private IFramePerspectivePlace createIFramePerspective(IFramePerspectiveConfiguration perspectivesConfiguration) {
        IFramePerspectivePlace iFramePerspectivePlace = new IFramePerspectivePlace();
        iFramePerspectivePlace.setName(perspectivesConfiguration.getName());
        iFramePerspectivePlace.setUrl(perspectivesConfiguration.getUrl());
        return iFramePerspectivePlace;
    }

    private AuthorPerspectivePlace getDefault() {
        return new AuthorPerspectivePlace();
    }

}
