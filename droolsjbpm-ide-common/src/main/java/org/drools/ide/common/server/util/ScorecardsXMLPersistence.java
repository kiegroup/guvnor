/*
 * Copyright 2012 JBoss Inc
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

package org.drools.ide.common.server.util;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;
import org.drools.ide.common.client.modeldriven.scorecards.ScorecardModel;

import java.math.BigDecimal;

public class ScorecardsXMLPersistence {
    private XStream xt;
    private static final ScorecardsXMLPersistence INSTANCE = new ScorecardsXMLPersistence();

    public ScorecardsXMLPersistence() {
        xt = new XStream(new DomDriver());
        //All numerical values are historically BigDecimal
        xt.alias("valueNumeric", Number.class, BigDecimal.class);
    }

    public static ScorecardsXMLPersistence getInstance() {
        return INSTANCE;
    }

    public String marshal(ScorecardModel model) {
        return xt.toXML(model);
    }

    public ScorecardModel unmarshall(String xml) {
        if (xml == null || xml.trim().length() == 0) {
            return new ScorecardModel();
        }
        return (ScorecardModel) xt.fromXML(xml);
    }
}
