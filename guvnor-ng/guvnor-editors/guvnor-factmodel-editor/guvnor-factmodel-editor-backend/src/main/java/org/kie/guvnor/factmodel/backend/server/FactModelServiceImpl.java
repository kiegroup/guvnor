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

package org.kie.guvnor.factmodel.backend.server;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.inject.Named;

import org.drools.compiler.DrlParser;
import org.drools.compiler.DroolsParserException;
import org.drools.guvnor.models.commons.backend.imports.ImportsParser;
import org.drools.guvnor.models.commons.shared.imports.Imports;
import org.drools.lang.descr.AnnotationDescr;
import org.drools.lang.descr.PackageDescr;
import org.drools.lang.descr.TypeDeclarationDescr;
import org.drools.lang.descr.TypeFieldDescr;
import org.jboss.errai.bus.server.annotations.Service;
import org.kie.commons.io.IOService;
import org.kie.commons.java.nio.base.options.CommentedOption;
import org.kie.guvnor.commons.service.source.SourceServices;
import org.kie.guvnor.commons.service.validation.model.BuilderResult;
import org.kie.guvnor.commons.service.verification.model.AnalysisReport;
import org.kie.guvnor.datamodel.events.InvalidateDMOProjectCacheEvent;
import org.kie.guvnor.datamodel.oracle.DataModelOracle;
import org.kie.guvnor.datamodel.service.DataModelService;
import org.kie.guvnor.factmodel.model.AnnotationMetaModel;
import org.kie.guvnor.factmodel.model.FactMetaModel;
import org.kie.guvnor.factmodel.model.FactModelContent;
import org.kie.guvnor.factmodel.model.FactModels;
import org.kie.guvnor.factmodel.model.FieldMetaModel;
import org.kie.guvnor.factmodel.service.FactModelService;
import org.kie.guvnor.services.file.CopyService;
import org.kie.guvnor.services.file.DeleteService;
import org.kie.guvnor.services.file.RenameService;
import org.kie.guvnor.services.metadata.MetadataService;
import org.kie.guvnor.services.metadata.model.Metadata;
import org.uberfire.backend.server.util.Paths;
import org.uberfire.backend.vfs.Path;
import org.uberfire.client.workbench.widgets.events.ResourceAddedEvent;
import org.uberfire.client.workbench.widgets.events.ResourceOpenedEvent;
import org.uberfire.client.workbench.widgets.events.ResourceUpdatedEvent;
import org.uberfire.security.Identity;

import static java.util.Collections.*;

/**
 *
 */
@Service
@ApplicationScoped
public class FactModelServiceImpl implements FactModelService {

    @Inject
    @Named("ioStrategy")
    private IOService ioService;

    @Inject
    private MetadataService metadataService;

    @Inject
    private CopyService copyService;

    @Inject
    private DeleteService deleteService;

    @Inject
    private RenameService renameService;

    @Inject
    private Event<InvalidateDMOProjectCacheEvent> invalidateDMOProjectCache;

    @Inject
    private Event<ResourceOpenedEvent> resourceOpenedEvent;

    @Inject
    private Event<ResourceAddedEvent> resourceAddedEvent;

    @Inject
    private Event<ResourceUpdatedEvent> resourceUpdatedEvent;

    @Inject
    private Paths paths;

    @Inject
    private Identity identity;

    @Inject
    private DataModelService dataModelService;

    @Inject
    private SourceServices sourceServices;

    @Override
    public Path create( final Path context,
                        final String fileName,
                        final FactModels content,
                        final String comment ) {
        final Path newPath = paths.convert( paths.convert( context ).resolve( fileName ),
                                            false );

        ioService.write( paths.convert( newPath ),
                         marshal( content ),
                         makeCommentedOption( comment ) );

        //Signal creation to interested parties
        resourceAddedEvent.fire( new ResourceAddedEvent( newPath ) );

        return newPath;
    }

    @Override
    public FactModels load( final Path path ) {
        final String content = ioService.readAllString( paths.convert( path ) );

        //Signal opening to interested parties
        resourceOpenedEvent.fire( new ResourceOpenedEvent( path ) );

        return unmarshal( content );
    }

    @Override
    public FactModelContent loadContent( final Path path ) {
        final FactModels factModels = load( path );
        final DataModelOracle oracle = dataModelService.getDataModel( path );
        return new FactModelContent( factModels,
                                     loadAllAvailableTypes( path ),
                                     oracle );
    }

    private List<FactMetaModel> loadAllAvailableTypes( final Path path ) {
        //TODO {porcelli} list other DRL_MODEL's from the project
        return emptyList();
    }

    @Override
    public Path save( final Path context,
                      final String fileName,
                      final FactModels content,
                      final String comment ) {
        final Path newPath = paths.convert( paths.convert( context ).resolve( fileName ),
                                            false );

        ioService.write( paths.convert( newPath ),
                         marshal( content ),
                         makeCommentedOption( comment ) );

        //Invalidate Project-level DMO cache as Model has changed.
        invalidateDMOProjectCache.fire( new InvalidateDMOProjectCacheEvent( newPath ) );

        //Signal update to interested parties
        resourceUpdatedEvent.fire( new ResourceUpdatedEvent( newPath ) );

        return newPath;
    }

    @Override
    public Path save( final Path resource,
                      final FactModels content,
                      final Metadata metadata,
                      final String comment ) {
        ioService.write( paths.convert( resource ),
                         marshal( content ),
                         metadataService.setUpAttributes( resource,
                                                          metadata ),
                         makeCommentedOption( comment ) );

        //Invalidate Project-level DMO cache as Model has changed.
        invalidateDMOProjectCache.fire( new InvalidateDMOProjectCacheEvent( resource ) );

        //Signal update to interested parties
        resourceUpdatedEvent.fire( new ResourceUpdatedEvent( resource ) );

        return resource;
    }

    @Override
    public void delete( final Path path,
                        final String comment ) {
        deleteService.delete( path,
                              comment );
    }

    @Override
    public Path rename( final Path path,
                        final String newName,
                        final String comment ) {
        return renameService.rename( path,
                                     newName,
                                     comment );
    }

    @Override
    public Path copy( final Path path,
                      final String newName,
                      final String comment ) {
        return copyService.copy( path,
                                 newName,
                                 comment );
    }

    @Override
    public String toSource( final Path path,
                            final FactModels model ) {
        return sourceServices.getServiceFor( paths.convert( path ) ).getSource( paths.convert( path ), marshal( model ) );
    }

    @Override
    public BuilderResult validate( final Path path,
                                   final FactModels content ) {
        //TODO {porcelli} validate
        return new BuilderResult();
    }

    @Override
    public boolean isValid( Path path,
                            FactModels content ) {
        return !validate( path, content ).hasLines();
    }

    @Override
    public AnalysisReport verify( Path path,
                                  FactModels content ) {
        //TODO {porcelli} verify
        return new AnalysisReport();
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

    private String marshal( final FactModels content ) {
        final StringBuilder sb = new StringBuilder();
        sb.append( content.getImports().toString() );
        sb.append( "\n" );
        for ( final FactMetaModel factMetaModel : content.getModels() ) {
            sb.append( toDRL( factMetaModel ) ).append( "\n\n" );
        }
        return sb.toString().trim();
    }

    private FactModels unmarshal( final String content ) {
        try {
            final List<FactMetaModel> models = toModel( content );
            final FactModels factModels = new FactModels();
            factModels.getModels().addAll( models );

            //De-serialize imports
            final Imports imports = ImportsParser.parseImports( content );
            factModels.setImports( imports );
            return factModels;

        } catch ( final DroolsParserException e ) {
            throw new RuntimeException( e );
        }
        //TODO {porcelli} needs define error handling strategy
//            log.error( "Unable to parse the DRL for the model - falling back to text (" + e.getMessage() + ")" );
//            RuleContentText text = new RuleContentText();
//            text.content = item.getContent();
//            asset.setContent( text );
    }

    private List<FactMetaModel> toModel( String drl )
            throws DroolsParserException {
        if ( drl != null && ( drl.startsWith( "#advanced" ) || drl.startsWith( "//advanced" ) ) ) {
            throw new DroolsParserException( "Using advanced editor" );
        }
        final DrlParser parser = new DrlParser();
        final PackageDescr pkg = parser.parse( drl );
        if ( parser.hasErrors() ) {
            throw new DroolsParserException( "The model drl " + drl + " is not valid" );
        }

        if ( pkg == null ) {
            return emptyList();
        }
        final List<TypeDeclarationDescr> types = pkg.getTypeDeclarations();
        final List<FactMetaModel> list = new ArrayList<FactMetaModel>( types.size() );
        for ( final TypeDeclarationDescr td : types ) {
            final FactMetaModel mm = new FactMetaModel();
            mm.setName( td.getTypeName() );
            mm.setSuperType( td.getSuperTypeName() );

            final Map<String, TypeFieldDescr> fields = td.getFields();
            for ( Map.Entry<String, TypeFieldDescr> en : fields.entrySet() ) {
                final String fieldName = en.getKey();
                final TypeFieldDescr descr = en.getValue();
                final FieldMetaModel fm = new FieldMetaModel( fieldName,
                                                              descr.getPattern().getObjectType() );

                mm.getFields().add( fm );
            }

            final Map<String, AnnotationDescr> annotations = td.getAnnotations();
            for ( final Map.Entry<String, AnnotationDescr> en : annotations.entrySet() ) {
                final String annotationName = en.getKey();
                final AnnotationDescr descr = en.getValue();
                final Map<String, String> values = descr.getValues();
                final AnnotationMetaModel am = new AnnotationMetaModel( annotationName,
                                                                        values );

                mm.getAnnotations().add( am );
            }

            list.add( mm );
        }

        return list;
    }

    private String toDRL( FactMetaModel mm ) {
        final StringBuilder sb = new StringBuilder();
        sb.append( "declare " ).append( mm.getName() );
        if ( mm.hasSuperType() ) {
            sb.append( " extends " );
            sb.append( mm.getSuperType() );
        }
        for ( int i = 0; i < mm.getAnnotations().size(); i++ ) {
            AnnotationMetaModel a = mm.getAnnotations().get( i );
            sb.append( "\n\t" );
            sb.append( buildAnnotationDRL( a ) );
        }
        for ( int i = 0; i < mm.getFields().size(); i++ ) {
            FieldMetaModel f = mm.getFields().get( i );
            sb.append( "\n\t" );
            sb.append( f.name ).append( ": " ).append( f.type );
        }
        sb.append( "\nend" );
        return sb.toString();
    }

    private StringBuilder buildAnnotationDRL( AnnotationMetaModel a ) {
        final StringBuilder sb = new StringBuilder();
        sb.append( "@" );
        sb.append( a.name );
        sb.append( "(" );
        for ( final Map.Entry<String, String> e : a.getValues().entrySet() ) {
            if ( e.getKey() != null && e.getKey().length() > 0 ) {
                sb.append( e.getKey() );
                sb.append( " = " );
            }
            if ( e.getValue() != null && e.getValue().length() > 0 ) {
                sb.append( e.getValue() );
            }
            sb.append( ", " );
        }
        sb.delete( sb.length() - 2,
                   sb.length() );
        sb.append( ")" );
        return sb;
    }

}
