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
package org.drools.ide.common.client.modeldriven.dt52;

import java.util.List;
import java.util.Map;

import org.drools.ide.common.client.modeldriven.brl.templates.InterpolationVariable;

/**
 * A column that consists of a BRL fragment
 */
public interface BRLColumn<T> {

    public List<T> getDefinition();

    public void setDefinition(List<T> definition);

    public Map<InterpolationVariable, Integer> getVariables();

    public void setVariables(Map<InterpolationVariable, Integer> variables);

    public boolean isHideColumn();

    public void setHideColumn(boolean hideColumn);

    public void setHeader(String header);

    public String getHeader();

}
