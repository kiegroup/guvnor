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

package org.drools.guvnor.server.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.XMLEvent;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class PerspectiveConfigurationParser {
    private static final Logger log = LoggerFactory.getLogger(AssetEditorConfigurationParser.class);
    private static final String PERSPECTIVE_CONFIG = "/perspective.xml";
    static final String MODULE_EDITOR = "moduleeditor";
    static final String MODULE_EDITORS = "moduleeditors";
    static final String TITLE = "title";
    static final String CLASS = "class";
    static final String ICON = "icon";
    static final String FORMAT = "format";
    
    private final InputStream in;

    private List<ModuleEditorConfiguration> moduleEditors;
    
    public PerspectiveConfigurationParser() {
        this.in = getClass().getResourceAsStream(PERSPECTIVE_CONFIG);       
    }
    
    public PerspectiveConfigurationParser(InputStream in) {
        this.in = in;
    }
    
    public List<ModuleEditorConfiguration> getModuleEditors() {
        if (this.moduleEditors == null) {
            this.moduleEditors = readConfig(in);
        }
        return this.moduleEditors;
    }

    private List<ModuleEditorConfiguration> readConfig(InputStream in ) {
        List<ModuleEditorConfiguration> moduleEditors = new ArrayList<ModuleEditorConfiguration>();
        try {
            XMLInputFactory inputFactory = XMLInputFactory.newInstance();
            XMLEventReader eventReader = inputFactory.createXMLEventReader(in);
            ModuleEditorConfiguration configuration = null;

            while (eventReader.hasNext()) {
                XMLEvent event = eventReader.nextEvent();

                if (event.isStartElement()) {
                    final PerspectiveConfigurationElement element = PerspectiveConfigurationElement.forName(event.asStartElement().getName().getLocalPart());
                    switch (element) {
                        case MODULE_EDITOR: {
                            configuration = new ModuleEditorConfiguration();
                            break;
                        }
                        case FORMAT: {
                            event = eventReader.nextEvent();
                            if (event.isCharacters()) {
                                configuration.setFormat(event.asCharacters().getData());
                            } else if (event.isEndElement()) {
                                configuration.setFormat("");
                            }
                            break;
                        }
                        case CLASS: {
                            event = eventReader.nextEvent();
                            if (event.isCharacters()) {
                                configuration.setEditorClass(event.asCharacters().getData());
                            } else if (event.isEndElement()) {
                                configuration.setEditorClass("");
                            }
                            break;
                        } 
                        case ASSETEDITORFORMATS: {
                            event = eventReader.nextEvent();
                            if (event.isCharacters()) {
                                configuration.setAssetEditorFormats(event.asCharacters().getData());
                            } else if (event.isEndElement()) {
                                configuration.setAssetEditorFormats("");
                            }

                            break;
                        }
                    }
                }
                if (event.isEndElement()) {
                    final PerspectiveConfigurationElement element = PerspectiveConfigurationElement.forName(event
                            .asEndElement().getName().getLocalPart());
                    if (element == PerspectiveConfigurationElement.MODULE_EDITOR) {
                        moduleEditors.add(configuration);
                    }
                }

            }
        } catch (XMLStreamException e) {
            log.error("Failed to parse Asset editor configuration file",
                    e);
            e.printStackTrace();
        }
        return moduleEditors;
    }

    public static void main(String[] agrs) {
        PerspectiveConfigurationParser a = new PerspectiveConfigurationParser();
        //a.readConfig();
    }
}