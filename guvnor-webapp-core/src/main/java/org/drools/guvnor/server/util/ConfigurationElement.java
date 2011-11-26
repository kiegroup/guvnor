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


package org.drools.guvnor.server.util;

import java.util.HashMap;
import java.util.Map;

enum AssetEditorConfigElement {
    /** always the first **/
    UNKNOWN(null),
    ASSET_EDITORS("asseteditors"),
    ASSET_EDITOR("asseteditor"),
    FORMAT("format"),
    CLASS("class"),
    TITLE("title"),
    ICON("icon");

    private final String name;

    private AssetEditorConfigElement(final String name) {
        this.name = name;
    }

    private static final Map<String, AssetEditorConfigElement> MAP;

    static {
        final Map<String, AssetEditorConfigElement> map = new HashMap<String, AssetEditorConfigElement>();
        for (final AssetEditorConfigElement element : values()) {
            final String name = element.getLocalName();
            if (name != null)
                map.put(name, element);
        }
        MAP = map;
    }

    static AssetEditorConfigElement forName(final String localName) {
        final AssetEditorConfigElement element = MAP.get(localName);
        return element == null ? UNKNOWN : element;
    }

    /**
     * Get the local name of this element.
     * 
     * @return the local name
     */
    String getLocalName() {
        return name;
    }

}