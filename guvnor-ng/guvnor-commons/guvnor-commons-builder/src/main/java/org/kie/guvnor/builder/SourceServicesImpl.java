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

package org.kie.guvnor.builder;

import org.jboss.errai.bus.server.annotations.Service;
import org.kie.guvnor.commons.service.source.SourceService;
import org.kie.guvnor.commons.service.source.SourceServices;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Any;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import java.util.HashMap;
import java.util.Map;

@Service
@ApplicationScoped
public class SourceServicesImpl
        implements SourceServices {

    private Map<String, SourceService> sourceServices = new HashMap<String, SourceService>();

    public SourceServicesImpl() {
        //Empty constructor for Weld
    }
    
    @Inject
    public SourceServicesImpl(@Any Instance<SourceService> sourceServiceList) {
        for (SourceService sourceService : sourceServiceList) {
            sourceServices.put(sourceService.getSupportedFileExtension(), sourceService);
        }
    }

    @Override
    public boolean hasServiceFor(String filePath) {

        if (getFileExtension(filePath) == null) {
            return false;
        } else {
            return true;
        }
    }

    public SourceService getServiceFor(String filePath) {
        return sourceServices.get(getFileExtension(filePath));
    }

    private String getFileExtension(String filePath) {

        String result = null;

        for (String extension : sourceServices.keySet()) {
            if (filePath.endsWith(extension)) {
                if (result == null || result.length() < extension.length()) {
                    result = extension;
                }
            }
        }

        return result;
    }
}
