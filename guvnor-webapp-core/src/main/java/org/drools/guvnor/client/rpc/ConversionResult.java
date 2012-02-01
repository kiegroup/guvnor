/*
 * Copyright 2012 JBoss Inc
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
package org.drools.guvnor.client.rpc;

import java.util.ArrayList;
import java.util.List;

import org.drools.ide.common.client.modeldriven.brl.PortableObject;

/**
 * A single result of a conversion process
 */
public class ConversionResult
    implements
    PortableObject {

    private static final long       serialVersionUID = 540L;

    private String                  newAssetUUID;

    private boolean                 isConverted      = false;

    private List<ConversionMessage> messages         = new ArrayList<ConversionMessage>();

    public ConversionResult() {
    }

    public ConversionResult(String newAssetUUID) {
        this.newAssetUUID = newAssetUUID;
        this.isConverted = true;
    }

    public String getNewAssetUUID() {
        return this.newAssetUUID;
    }

    public boolean isConverted() {
        return isConverted;
    }

    public List<ConversionMessage> getMessages() {
        return messages;
    }

    public static class ConversionMessage
        implements
        PortableObject {

        private static final long serialVersionUID = 540L;

        private String            message;

        public ConversionMessage() {
        }

        public ConversionMessage(String message) {
            this.message = message;
        }

        public String getMessage() {
            return this.message;
        }

    }

}
