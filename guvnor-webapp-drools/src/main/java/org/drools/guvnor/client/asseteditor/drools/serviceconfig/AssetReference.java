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

package org.drools.guvnor.client.asseteditor.drools.serviceconfig;

import org.drools.ide.common.client.modeldriven.brl.PortableObject;

import static org.drools.guvnor.client.util.Preconditions.*;

public class AssetReference
        implements PortableObject {

    private static final long serialVersionUID = -4916888574125523558L;

    private String packageRef;
    private String name;
    private String format;
    private String url;
    private String uuid;

    public AssetReference() {
        //necessary for serialization
    }

    public AssetReference(final AssetReference source) {
        checkNotNull("source", source);

        this.packageRef = source.packageRef;
        this.name = source.name;
        this.format = source.format;
        this.url = source.url;
        this.uuid = source.uuid;
    }

    public AssetReference(final String packageRef, final String name, final String format, final String url, final String uuid) {
        this.packageRef = checkNotEmpty("packageRef", packageRef);
        this.name = checkNotEmpty("name", name);
        this.format = checkNotEmpty("format", format);
        this.url = checkNotEmpty("format", url);
        this.uuid = checkNotEmpty("format", uuid);
    }

    public String getUuid() {
        return uuid;
    }

    public String getPackageRef() {
        return packageRef;
    }

    public String getName() {
        return name;
    }

    public String getFormat() {
        return format;
    }

    public String getUrl() {
        return url;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        AssetReference that = (AssetReference) o;

        if (format != null ? !format.equals(that.format) : that.format != null) {
            return false;
        }
        if (name != null ? !name.equals(that.name) : that.name != null) {
            return false;
        }
        if (packageRef != null ? !packageRef.equals(that.packageRef) : that.packageRef != null) {
            return false;
        }
        if (url != null ? !url.equals(that.url) : that.url != null) {
            return false;
        }
        if (uuid != null ? !uuid.equals(that.uuid) : that.uuid != null) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = packageRef != null ? packageRef.hashCode() : 0;
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (format != null ? format.hashCode() : 0);
        result = 31 * result + (url != null ? url.hashCode() : 0);
        result = 31 * result + (uuid != null ? uuid.hashCode() : 0);
        return result;
    }
}
