package org.kie.guvnor.guided.dtable.client.handlers;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.IsWidget;
import org.jboss.errai.bus.client.api.RemoteCallback;
import org.jboss.errai.ioc.client.api.Caller;
import org.kie.guvnor.commons.ui.client.handlers.DefaultNewResourceHandler;
import org.kie.guvnor.guided.dtable.client.resources.Resources;
import org.kie.guvnor.guided.dtable.client.resources.i18n.Constants;
import org.kie.guvnor.guided.dtable.client.resources.images.ImageResources;
import org.kie.guvnor.guided.dtable.model.GuidedDecisionTable52;
import org.kie.guvnor.guided.dtable.service.GuidedDecisionTableEditorService;
import org.uberfire.backend.vfs.Path;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.mvp.PathPlaceRequest;
import org.uberfire.shared.mvp.PlaceRequest;

/**
 * Handler for the creation of new Guided Decision Tables
 */
@ApplicationScoped
public class NewGuidedDecisionTableHandler extends DefaultNewResourceHandler {

    private static String FILE_TYPE = "gdst";

    @Inject
    private PlaceManager placeManager;

    @Inject
    private Caller<GuidedDecisionTableEditorService> service;

    @Override
    public String getFileType() {
        return FILE_TYPE;
    }

    @Override
    public String getDescription() {
        return Constants.INSTANCE.NewGuidedDecisionTableDescription();
    }

    @Override
    public IsWidget getIcon() {
        return new Image( Resources.INSTANCE.images().guidedDecisionTableIcon() );
    }

    @Override
    public void create( final String fileName ) {
        final Path path = buildFullPathName( fileName );
        final GuidedDecisionTable52 ruleModel = new GuidedDecisionTable52();
        service.call( new RemoteCallback<Path>() {
            @Override
            public void callback( Path response ) {
                notifySuccess();
                final PlaceRequest place = new PathPlaceRequest( path,
                                                                 "GuidedDecisionTableEditor" );
                placeManager.goTo( place );
            }
        } ).save( path,
                  ruleModel );
    }

}
