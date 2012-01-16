/*
 * Copyright 2005 JBoss Inc
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

package org.drools.guvnor.server.builder;

import org.drools.guvnor.server.contenthandler.ContentManager;
import org.drools.repository.utils.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * This configures the module assemblers based on a props file.
 */
public class ModuleAssemblerManager {

    private static final Logger log = LoggerFactory.getLogger(ContentManager.class);
    private static final String CONTENT_CONFIG_PROPERTIES = "/moduleassembler.properties";
    private static ModuleAssemblerManager INSTANCE;

    /**
     * This is a map of the module assemblers to use.
     */
    private final Map<String, ModuleAssembler> moduleAssemblers = new HashMap<String, ModuleAssembler>();


    @SuppressWarnings("rawtypes")
    ModuleAssemblerManager(String configPath) {
        log.debug("Loading content properties");
        Properties props = new Properties();
        InputStream in = null;
        try {
            in = getClass().getResourceAsStream(configPath);
            props.load(in);
            for (Object o : props.keySet()) {
                String moduleType = (String) o;
                String val = props.getProperty(moduleType);

                moduleAssemblers.put(moduleType, loadModuleAssemblerImplementation(val));
            }
        } catch (IOException e) {
            log.error("UNABLE to load content handlers. Ahem, nothing will actually work. Ignore subsequent errors until this is resolved.", e);
        } finally {
            IOUtils.closeQuietly(in);
        }
    }

    /**
     * Return the moduleAssemblers.
     */
    public Map<String, ModuleAssembler> getModuleAssemblers() {
        return moduleAssemblers;
    }


    private ModuleAssembler loadModuleAssemblerImplementation(String val) throws IOException {
        try {
            return (ModuleAssembler) Thread.currentThread().getContextClassLoader().loadClass(val).newInstance();
        } catch (InstantiationException e) {
            log.error("Unable to load ModuleAssembler implementation.", e);
            return null;
        } catch (IllegalAccessException e) {
            log.error("Unable to load ModuleAssembler implementation.", e);
            return null;
        } catch (ClassNotFoundException e) {
            log.error("Unable to load ModuleAssembler implementation.", e);
            return null;
        }
    }

    public static ModuleAssemblerManager getInstance() {
        if (INSTANCE == null) {
            //have to do this annoying thing, as in some cases, letting the classloader
            //load it up means that it will fail as the classes aren't yet available.
            //so have to use this nasty anti-pattern here. Sorry.
            synchronized (ModuleAssemblerManager.class) {
                ModuleAssemblerManager.INSTANCE = new ModuleAssemblerManager(CONTENT_CONFIG_PROPERTIES);
            }
        }
        return INSTANCE;
    }

    public static ModuleAssembler getModuleAssembler(String format) {
        ModuleAssembler h = ModuleAssemblerManager.getInstance().getModuleAssemblers().get(format);
        if (h == null) {
            //h = new DefaultContentHandler();
            throw new IllegalArgumentException("Unable to handle the module type: " + format);
        }
        return h;
    }
}
