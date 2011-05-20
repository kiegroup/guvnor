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


package org.drools.guvnor.server.jaxrs;


import java.util.HashSet;
import java.util.Set;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;

import org.apache.cxf.jaxrs.provider.AtomEntryProvider;
import org.apache.cxf.jaxrs.provider.AtomFeedProvider;


@ApplicationPath("/")
public class GuvnorApplication extends Application {

    @Override
    public Set<Class<?>> getClasses() {
        Set<Class<?>> classes = new HashSet<Class<?>>();
        classes.add(PackageResource.class);
        classes.add(CategoryResource.class);
        classes.add(AtomEntryProvider.class);
        classes.add(AtomFeedProvider.class);
        classes.add(CXFAuthenticationHandler.class);        
        return classes;
    }
}

