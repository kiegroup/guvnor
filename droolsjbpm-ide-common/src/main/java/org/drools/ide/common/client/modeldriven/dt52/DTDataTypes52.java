/*
 * Copyright 2011 JBoss Inc
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
package org.drools.ide.common.client.modeldriven.dt52;

import org.drools.ide.common.client.modeldriven.brl.PortableObject;

/**
 * Explicit DataTypes handled by the Mergable Grid Widgets
 */
public enum DTDataTypes52
        implements PortableObject {

    STRING,
    NUMERIC,
    NUMERIC_BIGDECIMAL,
    NUMERIC_BIGINTEGER,
    NUMERIC_BYTE,
    NUMERIC_DOUBLE,
    NUMERIC_FLOAT,
    NUMERIC_INTEGER,
    NUMERIC_LONG,
    NUMERIC_SHORT,
    DATE,
    BOOLEAN

}
