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
package org.drools.guvnor.server.converters;

import org.drools.guvnor.client.rpc.ConversionResult;
import org.drools.repository.AssetItem;

/**
 * A converter from one format to another
 */
public abstract class AbstractConverter {

    private final String targetFormat;

    public AbstractConverter(String targetFormat) {
        this.targetFormat = targetFormat;
    }

    /**
     * Does this Converter handle the specified format
     * 
     * @param targetFormat
     */
    boolean isTargetFormatSupported(String targetFormat) {
        return this.targetFormat.equals( targetFormat );
    }

    abstract ConversionResult convert(AssetItem item);

}
