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

import java.util.List;

import org.drools.guvnor.client.widgets.drools.decoratedgrid.DynamicColumn;
import org.drools.guvnor.client.widgets.drools.decoratedgrid.data.DynamicData;
import org.drools.ide.common.client.modeldriven.dt52.DTColumnConfig52;
import org.drools.ide.common.client.modeldriven.dt52.GuidedDecisionTable52;

/**
 * An event to set the internal model for a Guided Decision Table
 */
public class SetInternalDecisionTableModelEvent extends SetInternalModelEvent<GuidedDecisionTable52, DTColumnConfig52> {

    public static Type<SetInternalModelEvent.Handler<GuidedDecisionTable52, DTColumnConfig52>> TYPE = new Type<SetInternalModelEvent.Handler<GuidedDecisionTable52, DTColumnConfig52>>();

    public SetInternalDecisionTableModelEvent(GuidedDecisionTable52 model,
                                              DynamicData data,
                                              List<DynamicColumn<DTColumnConfig52>> columns) {
        super( model,
               data,
               columns );
    }

    @Override
    public Type<SetInternalModelEvent.Handler<GuidedDecisionTable52, DTColumnConfig52>> getAssociatedType() {
        return TYPE;
    }

}
