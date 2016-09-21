/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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

package org.guvnor.ala.source.git;

import java.math.BigInteger;
import java.net.URI;
import java.nio.charset.Charset;
import java.util.Collections;
import java.util.Map;

import org.uberfire.commons.config.ConfigProperties;
import org.guvnor.ala.source.Host;
import org.guvnor.ala.source.Repository;

import static org.uberfire.commons.validation.PortablePreconditions.*;

public class UFLocal implements Host<GitCredentials> {

    private final String id;
    private final String name;
    private final GitCredentials credentials;
    private final ConfigProperties configProperties;

    public UFLocal() {
        this( new GitCredentials() );
    }

    public UFLocal( final GitCredentials credentials ) {
        this( credentials, new ConfigProperties( System.getProperties() ) );
    }

    public UFLocal( final GitCredentials credentials,
                    final ConfigProperties configProperties ) {
        this.id = toHex( "UFLocal" );
        this.name = "UFLocal";
        this.credentials = credentials;
        this.configProperties = configProperties;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public Repository getRepository( final String id ) {
        return getRepository( id, Collections.emptyMap() );
    }

    @Override
    public Repository getRepository( final String id,
                                     final Map<String, String> env ) {
        return getRepository( credentials, id, env );
    }

    @Override
    public Repository getRepository( final GitCredentials credential,
                                     final String repositoryId,
                                     final Map<String, String> env ) {
        checkNotNull( "credential", credential );
        checkNotEmpty( "id", repositoryId );

        return new GitRepository( this, repositoryId, repositoryId, URI.create( "git://local" ), credential, env, configProperties );
    }

    @Override
    public boolean equals( final Object o ) {
        if ( this == o ) {
            return true;
        }
        if ( !( o instanceof UFLocal ) ) {
            return false;
        }

        final UFLocal gitHub = (UFLocal) o;

        return id.equals( gitHub.id ) && name.equals( gitHub.name );
    }

    @Override
    public int hashCode() {
        return 31 * id.hashCode() + name.hashCode();
    }

    private String toHex( String arg ) {
        return String.format( "%040x", new BigInteger( 1, arg.getBytes( Charset.forName( "UTF-8" ) ) ) );
    }

}
