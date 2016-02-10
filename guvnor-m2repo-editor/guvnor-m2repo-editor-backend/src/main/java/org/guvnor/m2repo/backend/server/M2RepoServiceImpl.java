/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
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

package org.guvnor.m2repo.backend.server;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.drools.compiler.kproject.xml.MinimalPomParser;
import org.drools.compiler.kproject.xml.PomModel;
import org.guvnor.common.services.project.model.GAV;
import org.guvnor.m2repo.model.JarListPageRequest;
import org.guvnor.m2repo.model.JarListPageRow;
import org.guvnor.m2repo.service.M2RepoService;
import org.jboss.errai.bus.server.annotations.Service;
import org.uberfire.paging.PageResponse;

@Service
@ApplicationScoped
// Implementation needs to implement both interfaces even though one extends the other
// otherwise the implementation discovery mechanism for the @Service annotation fails.
public class M2RepoServiceImpl implements M2RepoService,
                                          ExtendedM2RepoService {

    @Inject
    private GuvnorM2Repository repository;

    @Override
    public void deployJar( final InputStream is,
                           final GAV gav ) {
        repository.deployArtifact( is,
                                   gav,
                                   true );
    }

    @Override
    public void deployJarInternal( final InputStream is,
                                   final GAV gav ) {
        repository.deployArtifact( is,
                                   gav,
                                   false );
    }

    @Override
    public void deployPom( final InputStream is,
                           final GAV gav ) {
        repository.deployPom( is,
                              gav );
    }

    @Override
    public String getPomText( final String path ) {
        return repository.getPomText( path );
    }

    @Override
    public GAV loadGAVFromJar( final String path ) {
        final GAV gav = repository.loadGAVFromJar( path );
        return gav;
    }

    @Override
    public PageResponse<JarListPageRow> listArtifacts( final JarListPageRequest pageRequest ) {
        //Get unsorted files matching filter
        final String filters = pageRequest.getFilters();
        final String dataSourceName = pageRequest.getDataSourceName();
        final boolean isAscending = pageRequest.isAscending();
        final Collection<File> files = repository.listFiles( filters );

        //Convert files to JarListPageRow
        final List<JarListPageRow> jarPageRowList = new ArrayList<JarListPageRow>();
        for ( File file : files ) {
            JarListPageRow jarListPageRow = new JarListPageRow();
            jarListPageRow.setName( file.getName() );
            jarListPageRow.setPath( getJarPath( file.getPath(),
                                                File.separator ) );
            jarListPageRow.setGav( getGAV( jarListPageRow.getPath() ) );
            jarListPageRow.setLastModified( new Date( file.lastModified() ) );
            jarPageRowList.add( jarListPageRow );
        }

        //Sort JarListPageRow entries, if required
        if ( dataSourceName != null ) {
            final int order = ( isAscending ? 1 : -1 );
            if ( dataSourceName.equals( JarListPageRequest.COLUMN_NAME ) ) {
                Collections.sort( jarPageRowList,
                                  new Comparator<JarListPageRow>() {
                                      @Override
                                      public int compare( final JarListPageRow o1,
                                                          final JarListPageRow o2 ) {
                                          return o1.getName().compareTo( o2.getName() ) * order;
                                      }
                                  } );

            } else if ( dataSourceName.equals( JarListPageRequest.COLUMN_PATH ) ) {
                Collections.sort( jarPageRowList,
                                  new Comparator<JarListPageRow>() {
                                      @Override
                                      public int compare( final JarListPageRow o1,
                                                          final JarListPageRow o2 ) {
                                          return o1.getPath().compareTo( o2.getPath() ) * order;
                                      }
                                  } );

            } else if ( dataSourceName.equals( JarListPageRequest.COLUMN_GAV ) ) {
                Collections.sort( jarPageRowList,
                                  new Comparator<JarListPageRow>() {
                                      @Override
                                      public int compare( final JarListPageRow o1,
                                                          final JarListPageRow o2 ) {
                                          final GAV gav1 = o1.getGav();
                                          final GAV gav2 = o2.getGav();
                                          return gav1.toString().compareToIgnoreCase( gav2.toString() ) * order;
                                      }
                                  } );

            } else if ( dataSourceName.equals( JarListPageRequest.COLUMN_LAST_MODIFIED ) ) {
                Collections.sort( jarPageRowList,
                                  new Comparator<JarListPageRow>() {
                                      @Override
                                      public int compare( final JarListPageRow o1,
                                                          final JarListPageRow o2 ) {
                                          final Long ft1 = o1.getLastModified().getTime();
                                          final Long ft2 = o2.getLastModified().getTime();
                                          return ft1.compareTo( ft2 ) * order;
                                      }
                                  } );
            }
        }

        //Copy request "page" of entries to response
        final Integer pageSize = pageRequest.getPageSize();
        final int startRowIndex = pageRequest.getStartRowIndex();
        final int endRowIndex = ( pageSize == null ? jarPageRowList.size() : startRowIndex + pageSize );
        final List<JarListPageRow> responsePageRowList = new ArrayList<JarListPageRow>();
        for ( int i = startRowIndex; i < endRowIndex; i++ ) {
            responsePageRowList.add( jarPageRowList.get( i ) );
        }

        final PageResponse<JarListPageRow> response = new PageResponse<JarListPageRow>();
        response.setPageRowList( responsePageRowList );
        response.setStartRowIndex( pageRequest.getStartRowIndex() );
        response.setTotalRowSize( files.size() );
        response.setTotalRowSizeExact( true );

        return response;
    }

    // The file separator is provided as a parameter so that we can test for correct JAR path creation on both
    // Windows and Linux based Operating Systems in Unit tests running on either platform. See JarPathTest.
    String getJarPath( final String path,
                       final String separator ) {
        //Strip "Repository" prefix
        String jarPath = path.substring( GuvnorM2Repository.M2_REPO_DIR.length() + 1 );
        //Replace OS-dependent file separators with HTTP path separators
        jarPath = jarPath.replaceAll( "\\" + separator,
                                      "/" );
        return jarPath;
    }

    GAV getGAV( final String path ) {
        GAV gav = null;
        InputStream is = null;
        try {
            final String pom = getPomText( path );
            is = new ByteArrayInputStream( pom.getBytes( Charset.forName( "UTF-8" ) ) );
            final PomModel model = MinimalPomParser.parse( path,
                                                           is );
            gav = new GAV( model.getReleaseId().getGroupId(),
                           model.getReleaseId().getArtifactId(),
                           model.getReleaseId().getVersion() );

        } catch ( RuntimeException rte ) {
            //RuntimeException is thrown by MinimalPomParser for any Exception..
            gav = new GAV( "<undetermined>",
                           "<undetermined>",
                           "<undetermined>" );
        } finally {
            if ( is != null ) {
                try {
                    is.close();
                } catch ( IOException ioe ) {
                    //Swallow
                }
            }
        }
        return gav;
    }

    /**
     * @param baseURL the base URL where Guvnor M2 repo is hosted in web container. return a Guvnor M2 repo
     * URL point to local file system if baseURL is not available.
     * @return String
     */
    @Override
    public String getRepositoryURL( final String baseURL ) {
        if ( baseURL == null || baseURL.isEmpty() ) {
            return repository.getRepositoryURL();
        } else {
            if ( baseURL.endsWith( "/" ) ) {
                return baseURL + "maven2/";
            } else {
                return baseURL + "/maven2/";
            }
        }
    }

}
