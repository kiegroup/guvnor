package org.kie.guvnor.guided.template.client.handlers;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.IsWidget;
import org.jboss.errai.bus.client.api.RemoteCallback;
import org.jboss.errai.ioc.client.api.Caller;
import org.kie.guvnor.commons.ui.client.handlers.DefaultNewResourceHandler;
import org.kie.guvnor.commons.ui.client.save.CommandWithCommitMessage;
import org.kie.guvnor.commons.ui.client.save.SaveOperationService;
import org.kie.guvnor.guided.template.client.resources.GuidedTemplateEditorResources;
import org.kie.guvnor.guided.template.client.resources.i18n.Constants;
import org.kie.guvnor.guided.template.model.TemplateModel;
import org.kie.guvnor.guided.template.service.GuidedRuleTemplateEditorService;
import org.uberfire.backend.vfs.Path;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.shared.mvp.PlaceRequest;
import org.uberfire.shared.mvp.impl.PathPlaceRequest;

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
        templateModel.name = stripFileExtension( fileName );

        new SaveOperationService().save( path,
                                         new CommandWithCommitMessage() {
                                             @Override
                                             public void execute( final String comment ) {
                                                 service.call( new RemoteCallback<Void>() {
                                                     @Override
                                                     public void callback( Void aVoid ) {
                                                         notifySuccess();
                                                         notifyResourceAdded( path );
                                                         final PlaceRequest place = new PathPlaceRequest( path,
                                                                                                          "GuidedRuleTemplateEditor" );
                                                         placeManager.goTo( place );
                                                     }
                                                 } ).save( path,
                                                           templateModel,
                                                           comment );
                                             }
                                         } );
    }

}
