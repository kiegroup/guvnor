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

package org.guvnor.m2repo.backend.server;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.codehaus.plexus.util.xml.pull.XmlPullParserException;
import org.jboss.errai.bus.server.annotations.Service;
import org.guvnor.m2repo.model.JarListPageRow;
import org.guvnor.m2repo.service.M2RepoService;
import org.kie.workbench.common.services.project.backend.server.POMContentHandler;
import org.kie.workbench.common.services.project.service.model.GAV;
import org.uberfire.client.tables.PageRequest;
import org.uberfire.client.tables.PageResponse;

@Service
@ApplicationScoped
// Implementation needs to implement both interfaces even though one extends the other
// otherwise the implementation discovery mechanism for the @Service annotation fails.
public class M2RepoServiceImpl implements M2RepoService,
                                          ExtendedM2RepoService {

    @Inject
    private GuvnorM2Repository repository;

    @Inject
    private POMContentHandler pomContentHandler;

    @Override
    public void deployJar( InputStream is,
                           GAV gav ) {
        repository.deployArtifact( is, gav );
    }

    @Override
    public InputStream loadJar( String path ) {
        return repository.loadFile( path );
    }

    @Override
    public String getJarName( String path ) {
        return repository.getFileName( path );
    }

    @Override
    public void deleteJar( String[] path ) {
        repository.deleteFile( path );
    }

    @Override
    public String loadPOMStringFromJar( String path ) {
        return repository.loadPOMFromJar( path );
    }

    @Override
    public GAV loadGAVFromJar( String path ) {
        try {
            return pomContentHandler.toModel( repository.loadPOMFromJar( path ) ).getGav();
        } catch ( IOException e ) {
            e.printStackTrace();  //TODO Needs to be logged -Rikkola-
        } catch ( XmlPullParserException e ) {
            e.printStackTrace();  //TODO Needs to be logged -Rikkola-
        }
        return new GAV();
    }

    @Override
    public PageResponse<JarListPageRow> listJars( PageRequest pageRequest,
                                                  String filters ) {
        Collection<File> files = repository.listFiles( filters );

        PageResponse<JarListPageRow> response = new PageResponse<JarListPageRow>();
        List<JarListPageRow> tradeRatePageRowList = new ArrayList<JarListPageRow>();

        int i = 0;
        for ( File file : files ) {
            if ( i >= pageRequest.getStartRowIndex() + pageRequest.getPageSize() ) {
                break;
            }
            if ( i >= pageRequest.getStartRowIndex() ) {
                JarListPageRow jarListPageRow = new JarListPageRow();
                jarListPageRow.setName( file.getName() );
                jarListPageRow.setPath( file.getPath() );
                jarListPageRow.setLastModified( new Date( file.lastModified() ) );
                tradeRatePageRowList.add( jarListPageRow );
            }
            i++;
        }

        response.setPageRowList( tradeRatePageRowList );
        response.setStartRowIndex( pageRequest.getStartRowIndex() );
        response.setTotalRowSize( files.size() );
        response.setTotalRowSizeExact( true );
        //response.setLastPage(true);

        return response;
    }

    /**
     * @param baseURL the base URL where Guvnor M2 repo is hosted in web container. return a Guvnor M2 repo
     * URL point to local file system if baseURL is not available.
     * @return String
     */
    @Override
    public String getRepositoryURL( String baseURL ) {
        if ( baseURL == null || baseURL.isEmpty()) {
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
