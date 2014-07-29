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

package org.guvnor.common.services.backend.metadata;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.guvnor.common.services.backend.exceptions.ExceptionUtilities;
import org.guvnor.common.services.backend.metadata.attribute.DiscussionAttributes;
import org.guvnor.common.services.backend.metadata.attribute.DiscussionAttributesUtil;
import org.guvnor.common.services.backend.metadata.attribute.DiscussionView;
import org.guvnor.common.services.backend.metadata.attribute.OtherMetaAttributes;
import org.guvnor.common.services.backend.metadata.attribute.OtherMetaAttributesUtil;
import org.guvnor.common.services.backend.metadata.attribute.OtherMetaView;
import org.guvnor.common.services.shared.metadata.MetadataService;
import org.guvnor.common.services.shared.metadata.model.DiscussionRecord;
import org.guvnor.common.services.shared.metadata.model.Metadata;
import org.guvnor.structure.repositories.impl.PortableVersionRecord;
import org.jboss.errai.bus.server.annotations.Service;
import org.uberfire.backend.server.util.Paths;
import org.uberfire.backend.vfs.Path;
import org.uberfire.io.IOService;
import org.uberfire.io.attribute.DublinCoreAttributes;
import org.uberfire.io.attribute.DublinCoreAttributesUtil;
import org.uberfire.io.attribute.DublinCoreView;
import org.uberfire.java.nio.base.BasicFileAttributesUtil;
import org.uberfire.java.nio.base.version.VersionAttributeView;
import org.uberfire.java.nio.base.version.VersionRecord;
import org.uberfire.java.nio.file.NoSuchFileException;
import org.uberfire.java.nio.file.attribute.FileTime;

import static java.util.Collections.*;
import static org.uberfire.commons.validation.PortablePreconditions.*;

@Service
@ApplicationScoped
public class MetadataServiceImpl implements MetadataService {

    @Inject
    @Named("ioStrategy")
    private IOService ioService;

    @Override
    public Metadata getMetadata( final Path resource ) {
        try {
            final org.uberfire.java.nio.file.Path path = Paths.convert( resource );

            final DublinCoreView dcoreView = ioService.getFileAttributeView( path, DublinCoreView.class );
            final DiscussionView discussView = ioService.getFileAttributeView( path, DiscussionView.class );
            final OtherMetaView otherMetaView = ioService.getFileAttributeView( path, OtherMetaView.class );
            final VersionAttributeView versionAttributeView = ioService.getFileAttributeView( path, VersionAttributeView.class );

            return MetadataBuilder.newMetadata()
                    .withPath( Paths.convert( path.toRealPath() ) )
                    .withCheckinComment( versionAttributeView.readAttributes().history().records().size() > 0 ? versionAttributeView.readAttributes().history().records().get( versionAttributeView.readAttributes().history().records().size() - 1 ).comment() : null )
                    .withLastContributor( versionAttributeView.readAttributes().history().records().size() > 0 ? versionAttributeView.readAttributes().history().records().get( versionAttributeView.readAttributes().history().records().size() - 1 ).author() : null )
                    .withCreator( versionAttributeView.readAttributes().history().records().size() > 0 ? versionAttributeView.readAttributes().history().records().get( 0 ).author() : null )
                    .withLastModified( new Date( versionAttributeView.readAttributes().lastModifiedTime().toMillis() ) )
                    .withDateCreated( new Date( versionAttributeView.readAttributes().creationTime().toMillis() ) )
                    .withSubject( dcoreView.readAttributes().subjects().size() > 0 ? dcoreView.readAttributes().subjects().get( 0 ) : null )
                    .withType( dcoreView.readAttributes().types().size() > 0 ? dcoreView.readAttributes().types().get( 0 ) : null )
                    .withExternalRelation( dcoreView.readAttributes().relations().size() > 0 ? dcoreView.readAttributes().relations().get( 0 ) : null )
                    .withExternalSource( dcoreView.readAttributes().sources().size() > 0 ? dcoreView.readAttributes().sources().get( 0 ) : null )
                    .withDescription( dcoreView.readAttributes().descriptions().size() > 0 ? dcoreView.readAttributes().descriptions().get( 0 ) : null )
                    .withCategories( otherMetaView.readAttributes().categories() )
                    .withDiscussion( discussView.readAttributes().discussion() )
                    .withVersion( new ArrayList<VersionRecord>( versionAttributeView.readAttributes().history().records().size() ) {{
                        for ( final VersionRecord record : versionAttributeView.readAttributes().history().records() ) {
                            add( new PortableVersionRecord( record.id(), record.author(), record.email(), record.comment(), record.date(), record.uri() ) );
                        }
                    }} )
                    .build();

        } catch ( Exception e ) {
            throw ExceptionUtilities.handleException( e );
        }
    }

    @Override
    public Map<String, Object> configAttrs( final Map<String, Object> _attrs,
                                            final Metadata metadata ) {
        try {
            checkNotNull( "_attrs", _attrs );
            checkNotNull( "metadata", metadata );

            Map<String, Object> attrs = BasicFileAttributesUtil.cleanup( _attrs );
            attrs = DublinCoreAttributesUtil.cleanup( attrs );
            attrs = DiscussionAttributesUtil.cleanup( attrs );
            attrs = OtherMetaAttributesUtil.cleanup( attrs );

            attrs.putAll( DiscussionAttributesUtil.toMap(
                    new DiscussionAttributes() {
                        @Override
                        public List<DiscussionRecord> discussion() {
                            return metadata.getDiscussion();
                        }

                        @Override
                        public FileTime lastModifiedTime() {
                            return null;
                        }

                        @Override
                        public FileTime lastAccessTime() {
                            return null;
                        }

                        @Override
                        public FileTime creationTime() {
                            return null;
                        }

                        @Override
                        public boolean isRegularFile() {
                            return false;
                        }

                        @Override
                        public boolean isDirectory() {
                            return false;
                        }

                        @Override
                        public boolean isSymbolicLink() {
                            return false;
                        }

                        @Override
                        public boolean isOther() {
                            return false;
                        }

                        @Override
                        public long size() {
                            return 0;
                        }

                        @Override
                        public Object fileKey() {
                            return null;
                        }
                    }, "*" ) );

            attrs.putAll( OtherMetaAttributesUtil.toMap(
                    new OtherMetaAttributes() {
                        @Override
                        public List<String> categories() {
                            return metadata.getCategories();
                        }

                        @Override
                        public FileTime lastModifiedTime() {
                            return null;
                        }

                        @Override
                        public FileTime lastAccessTime() {
                            return null;
                        }

                        @Override
                        public FileTime creationTime() {
                            return null;
                        }

                        @Override
                        public boolean isRegularFile() {
                            return false;
                        }

                        @Override
                        public boolean isDirectory() {
                            return false;
                        }

                        @Override
                        public boolean isSymbolicLink() {
                            return false;
                        }

                        @Override
                        public boolean isOther() {
                            return false;
                        }

                        @Override
                        public long size() {
                            return 0;
                        }

                        @Override
                        public Object fileKey() {
                            return null;
                        }
                    }, "*" ) );

            attrs.putAll( DublinCoreAttributesUtil.toMap(
                    new DublinCoreAttributes() {

                        @Override
                        public List<String> titles() {
                            return emptyList();
                        }

                        @Override
                        public List<String> creators() {
                            return emptyList();
                        }

                        @Override
                        public List<String> subjects() {
                            return new ArrayList<String>( 1 ) {{
                                add( metadata.getSubject() );
                            }};
                        }

                        @Override
                        public List<String> descriptions() {
                            return new ArrayList<String>( 1 ) {{
                                add( metadata.getDescription() );
                            }};
                        }

                        @Override
                        public List<String> publishers() {
                            return emptyList();
                        }

                        @Override
                        public List<String> contributors() {
                            return emptyList();
                        }

                        @Override
                        public List<String> types() {
                            return new ArrayList<String>( 1 ) {{
                                add( metadata.getType() );
                            }};
                        }

                        @Override
                        public List<String> formats() {
                            return emptyList();
                        }

                        @Override
                        public List<String> identifiers() {
                            return emptyList();
                        }

                        @Override
                        public List<String> sources() {
                            return new ArrayList<String>( 1 ) {{
                                add( metadata.getExternalSource() );
                            }};
                        }

                        @Override
                        public List<String> languages() {
                            return emptyList();
                        }

                        @Override
                        public List<String> relations() {
                            return new ArrayList<String>( 1 ) {{
                                add( metadata.getExternalRelation() );
                            }};
                        }

                        @Override
                        public List<String> coverages() {
                            return emptyList();
                        }

                        @Override
                        public List<String> rights() {
                            return emptyList();
                        }

                        @Override
                        public FileTime lastModifiedTime() {
                            return null;
                        }

                        @Override
                        public FileTime lastAccessTime() {
                            return null;
                        }

                        @Override
                        public FileTime creationTime() {
                            return null;
                        }

                        @Override
                        public boolean isRegularFile() {
                            return false;
                        }

                        @Override
                        public boolean isDirectory() {
                            return false;
                        }

                        @Override
                        public boolean isSymbolicLink() {
                            return false;
                        }

                        @Override
                        public boolean isOther() {
                            return false;
                        }

                        @Override
                        public long size() {
                            return 0;
                        }

                        @Override
                        public Object fileKey() {
                            return null;
                        }
                    }, "*" ) );

            return attrs;

        } catch ( Exception e ) {
            throw ExceptionUtilities.handleException( e );
        }
    }

    @Override
    public Map<String, Object> setUpAttributes( final Path path,
                                                final Metadata metadata ) {
        try {
            Map<String, Object> attributes;
            try {
                attributes = ioService.readAttributes( Paths.convert( path ) );
            } catch ( final NoSuchFileException ex ) {
                attributes = new HashMap<String, Object>();
            }
            if ( metadata != null ) {
                attributes = configAttrs( attributes, metadata );
            }

            return BasicFileAttributesUtil.cleanup( attributes );
        } catch ( Exception e ) {
            throw ExceptionUtilities.handleException( e );
        }
    }

}
