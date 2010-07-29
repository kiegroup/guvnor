/**
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

package org.drools.ide.common.client.modeldriven.testing;

import java.util.ArrayList;
import java.util.List;

public class FactData
    implements
    Fixture {
    private static final long serialVersionUID = 510l;

    /**
     * The type (class)
     */
    public String             type;

    /**
     * The name of the "variable"
     */
    public String             name;

    public List<FieldData>    fieldData        = new ArrayList<FieldData>();

    /**
     * If its a modify, obviously we are modifying existing data in working memory.
     */
    public boolean            isModify;

    public FactData() {
    }

    public FactData(String type,
                    String name,
                    List<FieldData> fieldData,
                    boolean modify) {
        this( type,
              name,
              modify );
        this.fieldData = fieldData;

    }

    public FactData(String type,
                    String name,
                    boolean modify) {

        this.type = type;
        this.name = name;
        this.isModify = modify;
    }
}
