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
import org.kie.guvnor.services.config.model.imports.Imports;
import org.kie.guvnor.services.config.model.imports.ImportsParser;
import org.kie.guvnor.services.metadata.MetadataService;
import org.kie.guvnor.services.metadata.model.Metadata;
import org.uberfire.backend.server.util.Paths;
import org.uberfire.backend.vfs.Path;
import org.uberfire.backend.vfs.PathFactory;
import org.uberfire.security.Identity;

import static java.util.Collections.*;

/**
 *
 */
@Service
@ApplicationScoped
public class FactModelServiceImpl
        implements FactModelService {

    @Inject
    @Named("ioStrategy")
    private IOService ioService;

    @Inject
    private Paths paths;

    @Inject
    private MetadataService metadataService;

    @Inject
    private DataModelService dataModelService;

    @Inject
    private Event<InvalidateDMOProjectCacheEvent> invalidateDMOProjectCache;

    @Inject
    private Identity identity;

    @Inject
    private SourceServices sourceServices;

    @Override
    public FactModelContent loadContent( final Path path ) {
        try {
            String drl = ioService.readAllString( paths.convert( path ) );

            //De-serialize model
            final List<FactMetaModel> models = toModel( drl );
            final FactModels factModels = new FactModels();
            factModels.getModels().addAll( models );

            //De-serialize imports
            final Imports imports = ImportsParser.parseImports( drl );
            factModels.setImports( imports );

            //Set imports on DataModelOracle
            final DataModelOracle oracle = dataModelService.getDataModel( path );
            oracle.filter( imports );

            return new FactModelContent( factModels,
                                         loadAllAvailableTypes( path ),
                                         oracle );
        } catch ( final DroolsParserException e ) {
            throw new RuntimeException( e );
        }
        //TODO {porcelli} needs define error handling strategy
////            log.error( "Unable to parse the DRL for the model - falling back to text (" + e.getMessage() + ")" );
////            RuleContentText text = new RuleContentText();
////            text.content = item.getContent();
////            asset.setContent( text );
//        }
    }

    private List<FactMetaModel> loadAllAvailableTypes( final Path path ) {
        //TODO {porcelli} list other DRL_MODEL's from the project
        return emptyList();
    }

    @Override
    public void save( Path path,
                      FactModels factModel,
                      String comment ) {
        ioService.write( paths.convert( path ),
                         toDRL( factModel ),
                         makeCommentedOption( comment ) );
    }

    @Override
    public void save( final Path resource,
                      final FactModels content,
                      final Metadata metadata,
                      final String comment ) {

        ioService.write( paths.convert( resource ),
                         toDRL( content ),
                         metadataService.setUpAttributes( resource, metadata ),
                         makeCommentedOption( comment ) );

        invalidateDMOProjectCache.fire( new InvalidateDMOProjectCacheEvent( resource ) );
    }

    @Override
    public void delete( final Path path,
                        final String comment ) {
        System.out.println( "USER:" + identity.getName() + " DELETING asset [" + path.getFileName() + "]" );

        ioService.delete( paths.convert( path ) );
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
        return targetPath;
    }

    @Override
    public String toSource(Path path, final FactModels model) {
        return sourceServices.getServiceFor(paths.convert(path)).getSource(paths.convert(path), toDRL(model));
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

    private String toDRL( final FactModels model ) {
        final StringBuilder sb = new StringBuilder();

        sb.append( model.getImports().toString() );
        sb.append( "\n" );

        for ( final FactMetaModel factMetaModel : model.getModels() ) {
            sb.append( toDRL( factMetaModel ) ).append( "\n\n" );
        }
        return sb.toString().trim();
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
}
