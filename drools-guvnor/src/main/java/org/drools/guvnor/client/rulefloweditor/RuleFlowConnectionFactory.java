package org.drools.guvnor.client.rulefloweditor;

/*
 * Copyright 2005 JBoss Inc
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

import java.util.Map;

import pl.balon.gwt.diagrams.client.connection.Connection;
import pl.balon.gwt.diagrams.client.connection.RectilinearTwoEndedConnection;
import pl.balon.gwt.diagrams.client.connector.UIObjectConnector;

public class RuleFlowConnectionFactory {

    public static Connection createConnection(TransferConnection c,
                                              Map<Long, RuleFlowBaseNode> nodes) throws RuntimeException {

        long fromId = c.getFromId();
        long toId = c.getToId();

        RuleFlowBaseNode from = nodes.get( fromId );
        RuleFlowBaseNode to = nodes.get( toId );

        if ( from == null || to == null ) {
            throw new RuntimeException( "Connection needs existing from and to nodes." );
        }

        RectilinearTwoEndedConnection connection = new RectilinearTwoEndedConnection( UIObjectConnector.wrap( from ),
                                                                                      UIObjectConnector.wrap( to ) );

        return connection;
    }
}
