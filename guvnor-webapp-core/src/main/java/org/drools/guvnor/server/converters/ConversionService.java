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
 * A Service to convert Assets to a different formats
 */
public interface ConversionService {

    /**
     * Convert the given Asset to the target format
     * 
     * @param item
     *            The Asset to convert
     * @param targetFormat
     *            The target format of the asset
     * @return
     */
    public ConversionResult convert(AssetItem item,
                                    String targetFormat);

}
