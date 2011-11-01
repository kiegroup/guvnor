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
package org.drools.guvnor.client.widgets.drools.workitems;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

import org.drools.guvnor.client.common.IBindingProvider;
import org.drools.ide.common.shared.workitems.PortableParameterDefinition;

import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * A Widget to display a list of Work Item parameters
 */
public class WorkItemParametersWidget extends VerticalPanel {

    private List<PortableParameterDefinition> parameters;

    private IBindingProvider                  bindingProvider;

    public WorkItemParametersWidget(IBindingProvider bindingProvider) {
        this.bindingProvider = bindingProvider;
    }

    public void setParameters(Set<PortableParameterDefinition> parameters) {
        this.clear();
        this.parameters = sort( parameters );
        for ( PortableParameterDefinition ppd : this.parameters ) {
            WorkItemParameterWidget pw = WorkItemParameterWidgetFactory.getWidget( ppd, bindingProvider );
            add( pw );
        }
    }

    private List<PortableParameterDefinition> sort(Set<PortableParameterDefinition> parameters) {
        List<PortableParameterDefinition> sortedParameters = new ArrayList<PortableParameterDefinition>();
        sortedParameters.addAll( parameters );
        Collections.sort( sortedParameters,
                          new Comparator<PortableParameterDefinition>() {

                              public int compare(PortableParameterDefinition o1,
                                                 PortableParameterDefinition o2) {
                                  return o1.getName().compareTo( o2.getName() );
                              }

                          } );
        return sortedParameters;
    }

}
