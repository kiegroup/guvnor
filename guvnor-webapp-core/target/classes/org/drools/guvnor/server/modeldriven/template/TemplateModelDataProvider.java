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

package org.drools.guvnor.server.modeldriven.template;

import org.drools.ide.common.client.modeldriven.brl.templates.TemplateModel;
import org.drools.template.DataProvider;

public class TemplateModelDataProvider implements DataProvider {

    private final String[][] rows;
    private final int rowsCount;
    private int currRow = 0;

    public TemplateModelDataProvider(TemplateModel model) {
        this.rows = model.getTableAsArray();
        rowsCount = model.getRowsCount();
    }

    public boolean hasNext() {
        return rowsCount != -1 && currRow < rowsCount;
    }

    public String[] next() {
        return rows[currRow++];
    }
}
