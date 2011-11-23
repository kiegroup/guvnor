/*
 * Copyright 2011 JBoss Inc
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 */
package org.drools.guvnor.client.widgets.drools.decoratedgrid.events;

import org.drools.guvnor.client.asseteditor.drools.templatedata.TemplateDataColumn;

/**
 * An event to insert a Template Data column
 */
public class InsertTemplateDataColumnEvent extends InsertColumnEvent<TemplateDataColumn> {

    public InsertTemplateDataColumnEvent(TemplateDataColumn column,
                                         int index,
                                         boolean redraw) {
        super( column,
               index,
               redraw );
    }

    public InsertTemplateDataColumnEvent(TemplateDataColumn column,
                                         int index) {
        super( column,
               index );
    }

    public static Type<InsertColumnEvent.Handler<TemplateDataColumn>> TYPE = new Type<InsertColumnEvent.Handler<TemplateDataColumn>>();

    @Override
    public Type<InsertColumnEvent.Handler<TemplateDataColumn>> getAssociatedType() {
        return TYPE;
    }

}
