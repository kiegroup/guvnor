package org.kie.guvnor.guided.template.client.handlers;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.IsWidget;
import org.jboss.errai.bus.client.api.RemoteCallback;
import org.jboss.errai.ioc.client.api.Caller;
import org.kie.guvnor.commons.ui.client.handlers.DefaultNewResourceHandler;

import org.kie.guvnor.guided.template.client.resources.GuidedTemplateEditorResources;
import org.kie.guvnor.guided.template.client.resources.i18n.Constants;
import org.kie.guvnor.guided.template.model.TemplateModel;
import org.kie.guvnor.guided.template.service.GuidedRuleTemplateEditorService;
import org.uberfire.backend.vfs.Path;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.mvp.PathPlaceRequest;
import org.uberfire.shared.mvp.PlaceRequest;

/**
 * Handler for the creation of new Guided Templates
 */
@ApplicationScoped
public class NewGuidedRuleTemplateHandler extends DefaultNewResourceHandler {

    private static String FILE_TYPE = "template";

    @Inject
    private PlaceManager placeManager;

    @Inject
    private Caller<GuidedRuleTemplateEditorService> service;

    @Override
    public String getFileType() {
        return FILE_TYPE;
    }

    @Override
    public String getDescription() {
        return Constants.INSTANCE.NewGuidedRuleTemplateDescription();
    }

    @Override
    public IsWidget getIcon() {
        return new Image( GuidedTemplateEditorResources.INSTANCE.images().guidedRuleTemplateIcon() );
    }

    @Override
    public void create( final String fileName ) {
        final Path path = buildFullPathName( fileName );
        final TemplateModel templateModel = new TemplateModel();
        service.call( new RemoteCallback<Path>() {
            @Override
            public void callback( Path response ) {
                notifySuccess();
                final PlaceRequest place = new PathPlaceRequest( path,
                                                                 "GuidedRuleTemplateEditor" );
                placeManager.goTo( place );
            }
        } ).save( path,
                  templateModel );
    }

}
