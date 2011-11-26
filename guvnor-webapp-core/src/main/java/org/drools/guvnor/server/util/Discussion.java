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

package org.drools.guvnor.server.util;

import com.thoughtworks.xstream.XStream;
import org.drools.guvnor.client.rpc.DiscussionRecord;

import java.util.ArrayList;
import java.util.List;

/**
 * Utility methods for persisting discussion data as a property on an asset.
 */
public class Discussion {

    public static final String DISCUSSION_PROPERTY_KEY = "discussion";

    private final XStream xs = getXStream();

    public String toString(List<DiscussionRecord> recs) {
        return xs.toXML(recs);
    }

    @SuppressWarnings("unchecked")
    public List<DiscussionRecord> fromString(String data) {
        if (data == null || data.equals("")) {
            return new ArrayList<DiscussionRecord>();
        }
        return (List<DiscussionRecord>) xs.fromXML(data);
    }

    public XStream getXStream() {
        XStream xs = new XStream();
        xs.alias("discussion-record",
                DiscussionRecord.class);
        xs.alias("discussion",
                List.class);
        return xs;
    }
}
