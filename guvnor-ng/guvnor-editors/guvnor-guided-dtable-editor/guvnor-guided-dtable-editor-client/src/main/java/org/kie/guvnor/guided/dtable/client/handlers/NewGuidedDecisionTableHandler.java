package org.kie.guvnor.guided.dtable.client.handlers;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.IsWidget;
import org.jboss.errai.bus.client.api.RemoteCallback;
import org.jboss.errai.ioc.client.api.Caller;
import org.kie.commons.data.Pair;
import org.kie.guvnor.commons.ui.client.handlers.DefaultNewResourceHandler;
import org.kie.guvnor.commons.ui.client.save.CommandWithCommitMessage;
import org.kie.guvnor.commons.ui.client.save.SaveOperationService;
import org.kie.guvnor.commons.ui.client.wizards.WizardPresenter;
import org.kie.guvnor.guided.dtable.client.GuidedDTableResourceType;
import org.kie.guvnor.guided.dtable.client.resources.Resources;
import org.kie.guvnor.guided.dtable.client.resources.i18n.Constants;
import org.kie.guvnor.guided.dtable.client.wizard.pages.NewGuidedDecisionTableWizard;
import org.kie.guvnor.guided.dtable.client.wizard.util.NewGuidedDecisionTableAssetWizardContext;
import org.kie.guvnor.guided.dtable.model.GuidedDecisionTable52;
import org.kie.guvnor.guided.dtable.service.GuidedDecisionTableEditorService;
import org.uberfire.backend.vfs.Path;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.shared.mvp.PlaceRequest;
import org.uberfire.shared.mvp.impl.PathPlaceRequest;

/**
 * Handler for the creation of new Guided Decision Tables
 */
@ApplicationScoped
public class NewGuidedDecisionTableHandler extends DefaultNewResourceHandler {

    @Inject
    private PlaceManager placeManager;

    @Inject
    private Caller<GuidedDecisionTableEditorService> service;

    @Inject
    private GuidedDTableResourceType resourceType;

    @Inject
    private GuidedDecisionTableOptions options;

    @Inject
    private WizardPresenter wizardPresenter;

    @PostConstruct
    private void setupExtensions() {
        extensions.add( new Pair<String, GuidedDecisionTableOptions>( Constants.INSTANCE.Options(),
                                                                      options ) );
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
    public void create( final Path contextPath,
                        final String baseFileName ) {
        if ( !options.isUsingWizard() ) {
            createEmptyDecisionTable( baseFileName,
                                      contextPath,
                                      options.getTableFormat() );
        } else {
            createDecisionTableWithWizard( baseFileName,
                                           contextPath,
                                           options.getTableFormat() );
        }
    }

    private void createEmptyDecisionTable( final String baseFileName,
                                           final Path contextPath,
                                           final GuidedDecisionTable52.TableFormat tableFormat ) {
        final GuidedDecisionTable52 ruleModel = new GuidedDecisionTable52();
        ruleModel.setTableFormat( tableFormat );
        ruleModel.setTableName( baseFileName );

        new SaveOperationService().save( contextPath,
                                         new CommandWithCommitMessage() {
                                             @Override
                                             public void execute( final String comment ) {
                                                 service.call( new RemoteCallback<Path>() {
                                                     @Override
                                                     public void callback( final Path path ) {
                                                         notifySuccess();
                                                         notifyResourceAdded( path );
                                                         final PlaceRequest place = new PathPlaceRequest( path,
                                                                                                          "GuidedDecisionTableEditor" );
                                                         placeManager.goTo( place );
                                                     }
                                                 } ).save( contextPath, buildFileName( resourceType, baseFileName ), ruleModel, comment );

                                             }
                                         } );
    }

    private void createDecisionTableWithWizard( final String baseFileName,
                                                final Path contextPath,
                                                final GuidedDecisionTable52.TableFormat tableFormat ) {
        final NewGuidedDecisionTableAssetWizardContext context = new NewGuidedDecisionTableAssetWizardContext( baseFileName,
                                                                                                               contextPath,
                                                                                                               tableFormat );

        final NewGuidedDecisionTableWizard wizard = new NewGuidedDecisionTableWizard( context );
        wizardPresenter.start( wizard );
    }

}
