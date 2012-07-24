/*
 * Copyright 2012 JBoss Inc
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

package org.drools.guvnor.shared.simulation.command;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import org.drools.guvnor.shared.api.PortableObject;
import org.drools.guvnor.shared.simulation.SimulationPathModel;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * This is a DTO, but it's always nested in {@link FireAllRulesCommandModel}.
 */
@XStreamAlias("AssertRuleFiredCommandModel")
public class AssertRuleFiredCommandModel implements PortableObject {

    private String ruleName;
    private int fireCount;

    public String getRuleName() {
        return ruleName;
    }

    public void setRuleName(String ruleName) {
        this.ruleName = ruleName;
    }

    public int getFireCount() {
        return fireCount;
    }

    public void setFireCount(int fireCount) {
        this.fireCount = fireCount;
    }

}
