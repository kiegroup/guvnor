/*
 * Copyright 2011 JBoss Inc
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
package org.drools.guvnor.client.explorer;

import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceTokenizer;

public class ModuleEditorPlace extends Place {

    private final String uuid;

    public ModuleEditorPlace(String uuid) {
        this.uuid = uuid;
    }

    public String getUuid() {
        return uuid;
    }

    @Override
    public boolean equals(Object o) {
        if ( this == o ) return true;
        if ( o == null || getClass() != o.getClass() ) return false;

        ModuleEditorPlace that = (ModuleEditorPlace) o;

        if ( uuid != null ? !uuid.equals( that.uuid ) : that.uuid != null ) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return uuid != null ? uuid.hashCode() : 0;
    }

    public static class Tokenizer implements PlaceTokenizer<ModuleEditorPlace> {

        public String getToken(ModuleEditorPlace place) {
            return place.getUuid();
        }

        public ModuleEditorPlace getPlace(String token) {
            return new ModuleEditorPlace( token );
        }
    }
}
