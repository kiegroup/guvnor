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
package org.drools.guvnor.client.widgets.drools.decoratedgrid;

import com.google.gwt.event.shared.EventHandler;

/**
 * Handler interface for {@link SelectedCellChangeEvent} events.
 */
public interface SelectedCellChangeHandler
    extends
    EventHandler {

    /**
     * Called when {@link SelectedCellChangeEvent} is fired.
     * 
     * @param event
     *            the {@link SelectedCellChangeEvent} that was fired
     */
    void onSelectedCellChange(SelectedCellChangeEvent event);
}
