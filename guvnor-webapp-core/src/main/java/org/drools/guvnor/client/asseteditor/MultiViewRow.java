/*
 * Copyright 2010 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.guvnor.client.asseteditor;

public class MultiViewRow implements Comparable<MultiViewRow> {

    private final String uuid;
    private final String name;
    private final String format;

    public MultiViewRow(String uuid, String name, String format) {
        this.uuid = uuid;
        this.name = name;
        this.format = format;
    }

    public String getUuid() {
        return uuid;
    }

    public String getName() {
        return name;
    }

    public String getFormat() {
        return format;
    }

    @Override
    public boolean equals(Object o) {
        if ( this == o ) return true;
        if ( o == null || getClass() != o.getClass() ) return false;

        MultiViewRow that = (MultiViewRow) o;

        if ( format != null ? !format.equals( that.format ) : that.format != null ) return false;
        if ( name != null ? !name.equals( that.name ) : that.name != null ) return false;
        if ( uuid != null ? !uuid.equals( that.uuid ) : that.uuid != null ) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = uuid != null ? uuid.hashCode() : 0;
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (format != null ? format.hashCode() : 0);
        return result;
    }

    public int compareTo(MultiViewRow multiViewRow) {
        return uuid.compareTo( multiViewRow.getUuid() );
    }
}
