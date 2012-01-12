/*
 * Copyright 2011 JBoss Inc
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

package org.drools.guvnor.client.explorer.navigation;

import com.google.gwt.place.shared.Place;
import org.drools.guvnor.client.rpc.Module;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class ModuleFormatsGridPlace extends Place {

    private String[] formats;
    private Module packageConfig;
    private String title;

    public ModuleFormatsGridPlace( Module packageConfig,
                                   String title,
                                   String[] formats ) {
        this.packageConfig = packageConfig;
        this.title = title;
        this.formats = formats;
    }

    public String[] getFormats() {
        return formats;
    }

    public Module getPackageConfigData() {
        return packageConfig;
    }

    public String getTitle() {
        return title;
    }

    public boolean hasFormats() {
        return getFormats() != null && getFormats().length > 0;
    }

    public List<String> getFormatsAsList() {
        if ( hasFormats() ) {
            return Arrays.asList( getFormats() );
        } else {
            return Collections.emptyList();
        }
    }

    public Boolean getFormatIsRegistered() {
        return hasFormats();
    }
}
