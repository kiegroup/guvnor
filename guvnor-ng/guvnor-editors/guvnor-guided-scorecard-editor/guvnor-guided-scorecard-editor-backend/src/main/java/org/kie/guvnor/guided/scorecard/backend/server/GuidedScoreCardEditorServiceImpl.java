/*
 * Copyright 2012 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.guvnor.guided.scorecard.backend.server;

import java.util.Date;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.inject.Named;

import org.codehaus.plexus.util.StringUtils;
import org.drools.guvnor.models.guided.scorecard.backend.GuidedScoreCardXMLPersistence;
import org.drools.guvnor.models.guided.scorecard.shared.Attribute;
import org.drools.guvnor.models.guided.scorecard.shared.Characteristic;
import org.drools.guvnor.models.guided.scorecard.shared.ScoreCardModel;
import org.jboss.errai.bus.server.annotations.Service;
import org.kie.commons.io.IOService;
import org.kie.commons.java.nio.base.options.CommentedOption;
import org.kie.guvnor.commons.data.events.AssetEditedEvent;
import org.kie.guvnor.commons.data.events.AssetOpenedEvent;
import org.kie.guvnor.commons.service.metadata.model.Metadata;
import org.kie.guvnor.commons.service.source.SourceServices;
import org.kie.guvnor.commons.service.validation.model.BuilderResult;
import org.kie.guvnor.commons.service.validation.model.BuilderResultLine;
import org.kie.guvnor.commons.service.verification.model.AnalysisReport;
import org.kie.guvnor.datamodel.oracle.DataModelOracle;
import org.kie.guvnor.datamodel.service.DataModelService;
import org.kie.guvnor.guided.scorecard.model.ScoreCardModelContent;
import org.kie.guvnor.guided.scorecard.service.GuidedScoreCardEditorService;
import org.kie.guvnor.services.metadata.MetadataService;
import org.uberfire.backend.server.util.Paths;
import org.uberfire.backend.vfs.Path;
import org.uberfire.backend.vfs.PathFactory;
import org.uberfire.security.Identity;

@Service
@ApplicationScoped
public class GuidedScoreCardEditorServiceImpl
        implements GuidedScoreCardEditorService {

    @Inject
    @Named("ioStrategy")
    private IOService ioService;

    @Inject
    private Paths paths;

    @Inject
    private DataModelService dataModelService;

    @Inject
    private SourceServices sourceServices;

    @Inject
    private MetadataService metadataService;

    @Inject
    private Identity identity;

    @Inject
    private Event<AssetEditedEvent> assetEditedEvent;

    @Inject
    private Event<AssetOpenedEvent> assetOpenedEvent;

    private static final String RESOURCE_EXTENSION = "scgd";

    @Override
    public ScoreCardModelContent loadContent( final Path path ) {
        //De-serialize model
        final ScoreCardModel model = loadModel( path );

        final DataModelOracle oracle = dataModelService.getDataModel( path );

        assetOpenedEvent.fire( new AssetOpenedEvent( path ) );
        return new ScoreCardModelContent( model,
                                          oracle );
    }

    @Override
    public ScoreCardModel loadModel( final Path path ) {
        return GuidedScoreCardXMLPersistence.getInstance().unmarshall( ioService.readAllString( paths.convert( path ) ) );
    }

    @Override
    public Path create( final Path context,
                        final String fileName,
                        final ScoreCardModel content,
                        final String comment ) {
        final Path newPath = paths.convert( paths.convert( context ).resolve( fileName ), false );

        ioService.write( paths.convert( newPath ),
                         toSource( newPath,
                                   content ),
                         makeCommentedOption( comment ) );

        //TODO {manstis} assetCreatedEvent.fire( new AssetCreatedEvent( newPath ) );
        return newPath;
    }

    @Override
    public Path save( final Path context,
                      final String fileName,
                      final ScoreCardModel model,
                      final String comment ) {
        final Path newPath = paths.convert( paths.convert( context ).resolve( fileName ), false );

        ioService.write( paths.convert( newPath ),
                         GuidedScoreCardXMLPersistence.getInstance().marshal( model ),
                         makeCommentedOption( comment ) );

        assetEditedEvent.fire( new AssetEditedEvent( newPath ) );
        return newPath;
    }

    @Override
    public Path save( final Path resource,
                      final ScoreCardModel model,
                      final Metadata metadata,
                      final String comment ) {
        ioService.write( paths.convert( resource ),
                         GuidedScoreCardXMLPersistence.getInstance().marshal( model ),
                         metadataService.setUpAttributes( resource, metadata ),
                         makeCommentedOption( comment ) );

        assetEditedEvent.fire( new AssetEditedEvent( resource ) );
        return resource;
    }

    @Override
    public void delete( final Path path,
                        final String comment ) {
        System.out.println( "USER:" + identity.getName() + " DELETING asset [" + path.getFileName() + "]" );

        ioService.delete( paths.convert( path ) );

        assetEditedEvent.fire( new AssetEditedEvent( path ) );
    }

    @Override
    public Path rename( final Path path,
                        final String newName,
                        final String comment ) {
        System.out.println( "USER:" + identity.getName() + " RENAMING asset [" + path.getFileName() + "] to [" + newName + "]" );
        String targetName = path.getFileName().substring( 0, path.getFileName().lastIndexOf( "/" ) + 1 ) + newName;
        String targetURI = path.toURI().substring( 0, path.toURI().lastIndexOf( "/" ) + 1 ) + newName;
        Path targetPath = PathFactory.newPath( path.getFileSystem(), targetName, targetURI );
        ioService.move( paths.convert( path ), paths.convert( targetPath ), new CommentedOption( identity.getName(), comment ) );

        assetEditedEvent.fire( new AssetEditedEvent( path ) );
        return targetPath;
    }

    @Override
    public Path copy( final Path path,
                      final String newName,
                      final String comment ) {
        System.out.println( "USER:" + identity.getName() + " COPYING asset [" + path.getFileName() + "] to [" + newName + "]" );
        String targetName = path.getFileName().substring( 0, path.getFileName().lastIndexOf( "/" ) + 1 ) + newName;
        String targetURI = path.toURI().substring( 0, path.toURI().lastIndexOf( "/" ) + 1 ) + newName;
        Path targetPath = PathFactory.newPath( path.getFileSystem(), targetName, targetURI );
        ioService.copy( paths.convert( path ), paths.convert( targetPath ), new CommentedOption( identity.getName(), comment ) );

        assetEditedEvent.fire( new AssetEditedEvent( path ) );
        return targetPath;
    }

    @Override
    public String toSource( Path path,
                            final ScoreCardModel model ) {
        final BuilderResult result = validateScoreCard( model );
        if ( !result.hasLines() ) {
            return toDRL( path, model );
        }
        return toDRL( result );
    }

    @Override
    public BuilderResult validate( final Path path,
                                   final ScoreCardModel model ) {
        final BuilderResult result = validateScoreCard( model );
        return result;
    }

    @Override
    public boolean isValid( final Path path,
                            final ScoreCardModel model ) {
        return !validate( path,
                          model ).hasLines();
    }

    @Override
    public AnalysisReport verify( final Path path,
                                  final ScoreCardModel content ) {
        //TODO {porcelli} verify
        return new AnalysisReport();
    }

    public String toDRL( Path path,
                         final ScoreCardModel model ) {

        return sourceServices.getServiceFor( paths.convert( path ) ).getSource( paths.convert( path ), model );
    }

    private String toDRL( final BuilderResult result ) {

        final StringBuilder drl = new StringBuilder();
        for ( final BuilderResultLine msg : result.getLines() ) {
            drl.append( "//" ).append( msg.getMessage() ).append( "\n" );
        }
        return drl.toString();
    }

    private BuilderResult validateScoreCard( final ScoreCardModel model ) {
        final BuilderResult builderResult = new BuilderResult();
        if ( StringUtils.isBlank( model.getFactName() ) ) {
            builderResult.addLine( createBuilderResultLine( "Fact Name is empty.",
                                                            "Setup Parameters" ) );
        }
        if ( StringUtils.isBlank( model.getFieldName() ) ) {
            builderResult.addLine( createBuilderResultLine( "Resultant Score Field is empty.",
                                                            "Setup Parameters" ) );
        }
        if ( model.getCharacteristics().size() == 0 ) {
            builderResult.addLine( createBuilderResultLine( "No Characteristics Found.",
                                                            "Characteristics" ) );
        }
        int ctr = 1;
        for ( final Characteristic c : model.getCharacteristics() ) {
            String characteristicName = "Characteristic ('#" + ctr + "')";
            if ( StringUtils.isBlank( c.getName() ) ) {
                builderResult.addLine( createBuilderResultLine( "Name is empty.",
                                                                characteristicName ) );
            } else {
                characteristicName = "Characteristic ('" + c.getName() + "')";
            }
            if ( StringUtils.isBlank( c.getFact() ) ) {
                builderResult.addLine( createBuilderResultLine( "Fact is empty.",
                                                                characteristicName ) );
            }
            if ( StringUtils.isBlank( c.getField() ) ) {
                builderResult.addLine( createBuilderResultLine( "Characteristic Field is empty.",
                                                                characteristicName ) );
            } else if ( StringUtils.isBlank( c.getDataType() ) ) {
                builderResult.addLine( createBuilderResultLine( "Internal Error (missing datatype).",
                                                                characteristicName ) );
            }
            if ( c.getAttributes().size() == 0 ) {
                builderResult.addLine( createBuilderResultLine( "No Attributes Found.",
                                                                characteristicName ) );
            }
            if ( model.isUseReasonCodes() ) {
                if ( StringUtils.isBlank( model.getReasonCodeField() ) ) {
                    builderResult.addLine( createBuilderResultLine( "Resultant Reason Codes Field is empty.",
                                                                    characteristicName ) );
                }
                if ( !"none".equalsIgnoreCase( model.getReasonCodesAlgorithm() ) ) {
                    builderResult.addLine( createBuilderResultLine( "Baseline Score is not specified.",
                                                                    characteristicName ) );
                }
            }
            int attrCtr = 1;
            for ( final Attribute attribute : c.getAttributes() ) {
                final String attributeName = "Attribute ('#" + attrCtr + "')";
                if ( StringUtils.isBlank( attribute.getOperator() ) ) {
                    builderResult.addLine( createBuilderResultLine( "Attribute Operator is empty.",
                                                                    attributeName ) );
                }
                if ( StringUtils.isBlank( attribute.getValue() ) ) {
                    builderResult.addLine( createBuilderResultLine( "Attribute Value is empty.",
                                                                    attributeName ) );
                }
                if ( model.isUseReasonCodes() ) {
                    if ( StringUtils.isBlank( c.getReasonCode() ) ) {
                        if ( StringUtils.isBlank( attribute.getReasonCode() ) ) {
                            builderResult.addLine( createBuilderResultLine( "Reason Code must be set at either attribute or characteristic.",
                                                                            attributeName ) );
                        }
                    }
                }
                attrCtr++;
            }
            ctr++;
        }
        return builderResult;
    }

    private BuilderResultLine createBuilderResultLine( final String msg,
                                                       final String name ) {
        return new BuilderResultLine().setMessage( msg ).setResourceFormat( RESOURCE_EXTENSION ).setResourceName( name );
    }

    private CommentedOption makeCommentedOption( final String commitMessage ) {
        final String name = identity.getName();
        final Date when = new Date();
        final CommentedOption co = new CommentedOption( name,
                                                        null,
                                                        commitMessage,
                                                        when );
        return co;
    }

}
