package org.kie.guvnor.guided.rule.client.handlers;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.IsWidget;
import org.jboss.errai.bus.client.api.RemoteCallback;
import org.jboss.errai.ioc.client.api.Caller;
import org.kie.guvnor.commons.ui.client.handlers.DefaultNewResourceHandler;
import org.kie.guvnor.commons.ui.client.save.CommandWithCommitMessage;
import org.kie.guvnor.commons.ui.client.save.SaveOperationService;
import org.kie.guvnor.guided.rule.GuidedRuleFileType;
import org.kie.guvnor.guided.rule.client.resources.GuidedRuleEditorResources;
import org.kie.guvnor.guided.rule.client.resources.i18n.Constants;
import org.kie.guvnor.guided.rule.model.RuleModel;
import org.kie.guvnor.guided.rule.service.GuidedRuleEditorService;
import org.uberfire.backend.vfs.Path;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.shared.mvp.PlaceRequest;
import org.uberfire.shared.mvp.impl.PathPlaceRequest;

/**
 * Handler for the creation of new Guided Rules
 */
@ApplicationScoped
public class NewGuidedRuleHandler extends DefaultNewResourceHandler {

    @Inject
    private PlaceManager placeManager;

    @Inject
    private Caller<GuidedRuleEditorService> service;

    @Override
    public String getFileType() {
        return GuidedRuleFileType.TYPE;
    }

    @Override
    public String getDescription() {
        return Constants.INSTANCE.NewGuidedRuleDescription();
    }

    @Override
    public IsWidget getIcon() {
        return new Image( GuidedRuleEditorResources.INSTANCE.images().guidedRuleIcon() );
    }

    @Override
    public void create( final String fileName ) {
        final Path path = buildFullPathName( fileName );
        final RuleModel ruleModel = new RuleModel();
        ruleModel.name = stripFileExtension( fileName );

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
                                                                                                          "GuidedRuleEditor" );
                                                         placeManager.goTo( place );
                                                     }
                                                 } ).save( path,
                                                           ruleModel,
                                                           comment );
                                             }
                                         } );
    }

}
