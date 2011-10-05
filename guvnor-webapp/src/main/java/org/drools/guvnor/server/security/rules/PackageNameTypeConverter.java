/*
 * Copyright 2011 JBoss Inc
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
package org.drools.guvnor.server.security.rules;

import javax.enterprise.context.ApplicationScoped;

import org.drools.guvnor.server.security.PackageNameType;

@ApplicationScoped
public class PackageNameTypeConverter
    implements
    PermissionRuleObjectConverter {

    public Object convert(Object target) {
        return ((PackageNameType) target).getPackageName();
    }

}
