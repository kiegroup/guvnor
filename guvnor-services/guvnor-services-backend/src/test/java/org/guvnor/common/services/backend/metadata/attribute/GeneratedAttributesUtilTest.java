/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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

package org.guvnor.common.services.backend.metadata.attribute;

import java.util.HashMap;
import java.util.Map;

import org.guvnor.common.services.shared.metadata.model.GeneratedInfoHolder;
import org.junit.Test;

import static org.junit.Assert.*;

public class GeneratedAttributesUtilTest {

    @Test
    public void cleanup() {
        Map<String, Object> originalAttributeMap = new HashMap<String, Object>() {{
            put(GeneratedAttributesView.GENERATED_ATTRIBUTE_NAME,
                new GeneratedInfoHolder(true));
            put("customAttribute",
                "value");
        }};

        Map<String, Object> result = GeneratedAttributesUtil.cleanup(originalAttributeMap);

        assertNull(result.get(GeneratedAttributesView.GENERATED_ATTRIBUTE_NAME));
        assertNotNull(result.get("customAttribute"));
    }
}
