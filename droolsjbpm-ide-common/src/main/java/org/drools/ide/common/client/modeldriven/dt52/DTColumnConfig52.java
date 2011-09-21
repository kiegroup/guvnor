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
package org.drools.ide.common.client.modeldriven.dt52;

import org.drools.ide.common.client.modeldriven.brl.PortableObject;

public class DTColumnConfig52
    implements
    PortableObject {

    private static final long serialVersionUID = 510l;

    // For a default value ! Will still be in the array of course, just use this value if its empty
    private String            defaultValue     = null;

    // To hide the column (eg if it has a mandatory default)
    private boolean           hideColumn       = false;

    //Column width
    private int               width            = -1;

    public String getDefaultValue() {
        return defaultValue;
    }

    public int getWidth() {
        return width;
    }

    public boolean isHideColumn() {
        return hideColumn;
    }

    public void setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
    }

    public void setHideColumn(boolean hideColumn) {
        this.hideColumn = hideColumn;
    }

    public void setWidth(int width) {
        this.width = width;
    }

}
