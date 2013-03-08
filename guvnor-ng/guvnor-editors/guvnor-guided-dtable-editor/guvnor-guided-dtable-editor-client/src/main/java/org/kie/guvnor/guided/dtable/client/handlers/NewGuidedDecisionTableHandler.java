package org.kie.guvnor.guided.dtable.client.handlers;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.IsWidget;
import org.drools.guvnor.models.guided.dtable.shared.model.GuidedDecisionTable52;
import org.jboss.errai.bus.client.api.RemoteCallback;
import org.jboss.errai.ioc.client.api.Caller;
import org.kie.commons.data.Pair;
import org.kie.guvnor.commons.ui.client.handlers.DefaultNewResourceHandler;
import org.kie.guvnor.commons.ui.client.popups.file.CommandWithCommitMessage;
import org.kie.guvnor.commons.ui.client.popups.file.SaveOperationService;
import org.kie.guvnor.datamodel.oracle.DataModelOracle;
import org.kie.guvnor.datamodel.service.DataModelService;
import org.kie.guvnor.guided.dtable.client.resources.Resources;
import org.kie.guvnor.guided.dtable.client.resources.i18n.Constants;
import org.kie.guvnor.guided.dtable.client.type.GuidedDTableResourceType;
import org.kie.guvnor.guided.dtable.client.wizard.NewGuidedDecisionTableAssetWizardContext;
import org.kie.guvnor.guided.dtable.client.wizard.NewGuidedDecisionTableWizard;
import org.kie.guvnor.guided.dtable.service.GuidedDecisionTableEditorService;
import org.uberfire.backend.vfs.Path;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.client.wizards.WizardPresenter;
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
    private Caller<DataModelService> dmoService;

    @Inject
    private GuidedDTableResourceType resourceType;

    @Inject
    private GuidedDecisionTableOptions options;

    @Inject
    private WizardPresenter wizardPresenter;

    @Inject
    private NewGuidedDecisionTableWizard wizard;

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
        final GuidedDecisionTable52 model = new GuidedDecisionTable52();
        model.setTableFormat( tableFormat );
        model.setTableName( baseFileName );
        save( baseFileName,
              contextPath,
              model,
              null );
    }

    private void createDecisionTableWithWizard( final String baseFileName,
                                                final Path contextPath,
                                                final GuidedDecisionTable52.TableFormat tableFormat ) {
        dmoService.call( new RemoteCallback<DataModelOracle>() {

            @Override
            public void callback( final DataModelOracle oracle ) {
                final NewGuidedDecisionTableAssetWizardContext context = new NewGuidedDecisionTableAssetWizardContext( baseFileName,
                                                                                                                       contextPath,
                                                                                                                       tableFormat );
                wizard.setContent( context,
                                   oracle,
                                   NewGuidedDecisionTableHandler.this );
                wizardPresenter.start( wizard );
            }
        } ).getDataModel( contextPath );

    }

    public void save( final String baseFileName,
                      final Path contextPath,
                      final GuidedDecisionTable52 model,
                      final Command postSaveCommand ) {
        new SaveOperationService().save( contextPath,
                                         new CommandWithCommitMessage() {

                                             @Override
                                             public void execute( final String comment ) {
                                                 service.call( new RemoteCallback<Path>() {

                                                     @Override
                                                     public void callback( final Path path ) {
                                                         notifySuccess();
                                                         executePostSaveCommand();
                                                         final PlaceRequest place = new PathPlaceRequest( path );
                                                         placeManager.goTo( place );
                                                     }

                                                     private void executePostSaveCommand() {
                                                         if ( postSaveCommand != null ) {
                                                             postSaveCommand.execute();
                                                         }
                                                     }
                                                 } ).create( contextPath,
                                                             buildFileName( resourceType,
                                                                            baseFileName ),
                                                             model,
                                                             comment );
                                             }
                                         } );
    }

}
