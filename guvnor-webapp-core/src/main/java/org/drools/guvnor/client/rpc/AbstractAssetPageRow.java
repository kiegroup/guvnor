/*
 * Copyright 2011 JBoss Inc
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package org.drools.guvnor.client.rpc;

import org.drools.guvnor.client.rpc.Path;

import com.google.gwt.user.client.rpc.IsSerializable;

/**
 * A single row of a paged data
 */
public abstract class AbstractAssetPageRow extends AbstractPageRow
    implements
    IsSerializable {

    private Path path;
    private String format; // TODO should be an enum
    private String name;

    public int compareTo(AbstractAssetPageRow other) {
        return path.compareTo( other.path );
    }

    // ************************************************************************
    // Getters and setters
    // ************************************************************************

    public String getFormat() {
        return format;
    }

    public String getName() {
        return name;
    }

    public Path getPath() {
        return path;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPath(Path path) {
        this.path = path;
    }

}
