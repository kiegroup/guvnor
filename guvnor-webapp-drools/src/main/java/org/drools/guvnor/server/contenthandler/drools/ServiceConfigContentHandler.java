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

package org.drools.guvnor.server.contenthandler.drools;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.drools.guvnor.client.rpc.BuilderResult;
import org.drools.guvnor.client.rpc.BuilderResultLine;
import org.drools.guvnor.server.contenthandler.IHasCustomValidator;
import org.drools.guvnor.server.contenthandler.PlainTextContentHandler;
import org.drools.repository.AssetItem;

import static org.drools.guvnor.client.asseteditor.drools.serviceconfig.ServiceConfig.Protocol.*;

public class ServiceConfigContentHandler extends PlainTextContentHandler implements IHasCustomValidator {

    private static final Set<String> VALID_PROTOCOLS = new HashSet<String>() {{
        add(REST.toString());
        add(WEB_SERVICE.toString());
        add("ws");
        add("rs");
    }};

    public String validate(final String content) {
        final String[] lines = content.trim().split("\n");
        for (final String line : lines) {
            if (line.startsWith("polling=")) {
                try {
                    Integer.parseInt(line.substring(8));
                } catch (NumberFormatException ex) {
                    return "Invalid polling format.";
                }
            } else if (line.startsWith("protocol=")) {
                if (!VALID_PROTOCOLS.contains(line.substring(9))) {
                    return "Invalid protocol.";
                }
            } else if (line.startsWith("resource=")) {
                final String[] values = line.substring(9).split("\\|");
                if (values.length != 3) {
                    return "Invalid resource format.";
                }
            } else if (line.startsWith("model=")) {
                final String[] values = line.substring(6).split("\\|");
                if (values.length != 3) {
                    return "Invalid model format.";
                }
            } else if (line.startsWith("excluded.artifact=")) {
                final String[] values = line.substring(18).split("\\:");
                if (values.length < 5 || values.length > 6) {
                    return "Invalid excluded artifact format.";
                }
            } else if (line.trim().length() != 0) {
                return "Invalid data entry";
            }
        }
        return "";
    }
}
