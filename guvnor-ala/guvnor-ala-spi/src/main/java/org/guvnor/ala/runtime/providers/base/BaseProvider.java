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
package org.guvnor.ala.runtime.providers.base;

import java.util.Objects;
import org.guvnor.ala.config.ProviderConfig;
import org.guvnor.ala.runtime.providers.Provider;
import org.guvnor.ala.runtime.providers.ProviderType;

/*
 * BaseProvide implementation to be extended by concrete Providers
*/
public abstract class BaseProvider implements Provider {

    private String id;
    private ProviderConfig config;
    private ProviderType providerType;

    /*
     * No-args constructor for enabling marshalling to work, please do not remove. 
    */
    public BaseProvider() {
    }

    public BaseProvider( final String id,
                         final ProviderType providerType, ProviderConfig config ) {
        this.id = id;
        this.providerType = providerType;
        this.config = config;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public ProviderConfig getConfig() {
        return config;
    }

    @Override
    public ProviderType getProviderType() {
        return providerType;
    }

    @Override
    public String toString() {
        return "Provider{" + "id=" + id + ", config=" + config + ", providerType=" + providerType + '}';
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 97 * hash + Objects.hashCode( this.id );
        hash = 97 * hash + Objects.hashCode( this.config );
        hash = 97 * hash + Objects.hashCode( this.providerType );
        return hash;
    }

    @Override
    public boolean equals( Object obj ) {
        if ( this == obj ) {
            return true;
        }
        if ( obj == null ) {
            return false;
        }
        if ( getClass() != obj.getClass() ) {
            return false;
        }
        final BaseProvider other = ( BaseProvider ) obj;
        if ( !Objects.equals( this.id, other.id ) ) {
            return false;
        }
        if ( !Objects.equals( this.config, other.config ) ) {
            return false;
        }
        if ( !Objects.equals( this.providerType, other.providerType ) ) {
            return false;
        }
        return true;
    }
    
    
    

}
