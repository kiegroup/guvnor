package org.kie.guvnor.testscenario.client.handler;

import com.google.gwt.core.client.Callback;
import com.google.gwt.user.client.ui.IsWidget;
import org.jboss.errai.bus.client.api.RemoteCallback;
import org.kie.guvnor.commons.ui.client.callbacks.HasBusyIndicatorDefaultErrorCallback;
import org.kie.guvnor.commons.ui.client.handlers.DefaultNewResourceHandler;
import org.kie.guvnor.commons.ui.client.handlers.NewResourcePresenter;
import org.kie.guvnor.commons.ui.client.resources.i18n.CommonConstants;
import org.kie.guvnor.commons.ui.client.widget.BusyIndicatorView;
import org.kie.guvnor.testscenario.client.resources.i18n.TestScenarioConstants;
import org.kie.guvnor.testscenario.client.resources.images.TestScenarioAltedImages;
import org.uberfire.backend.vfs.Path;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

@ApplicationScoped
public class NewTestScenarioHandler
        extends DefaultNewResourceHandler {

    @Inject
    private BusyIndicatorView busyIndicatorView;

    @Override
    public String getDescription() {
        return TestScenarioConstants.INSTANCE.NewTestScenarioDescription();
    }

    @Override
    public IsWidget getIcon() {
        return TestScenarioAltedImages.INSTANCE.TestScenario();
    }

    @Override
    public void create(Path context, String baseFileName, NewResourcePresenter presenter) {
        busyIndicatorView.showBusyIndicator( CommonConstants.INSTANCE.Saving() );
        drlTextService.call( getSuccessCallback( presenter ),
                new HasBusyIndicatorDefaultErrorCallback( busyIndicatorView ) ).create( contextPath,
                buildFileName( resourceType,
                        baseFileName ),
                "",
                "" );
    }

    @Override
    public void acceptPath(final Path path,
                           final Callback<Boolean, Void> callback) {
        projectService.call(new RemoteCallback<Path>() {
            @Override
            public void callback(final Path path) {
                callback.onSuccess(path != null);
            }
        }).resolveTestPackage(path);
    }
}
