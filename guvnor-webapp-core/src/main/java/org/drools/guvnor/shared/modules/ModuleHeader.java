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
package org.drools.guvnor.shared.modules;

import java.util.ArrayList;
import java.util.List;

/**
 * 
 */
public class ModuleHeader {

    private List<Import> imports = new ArrayList<Import>();
    private List<Global> globals = new ArrayList<Global>();
    private boolean      hasDeclaredTypes;
    private boolean      hasFunctions;
    private boolean      hasRules;

    public List<Import> getImports() {
        return imports;
    }

    public List<Global> getGlobals() {
        return globals;
    }

    public void setHasDeclaredTypes(boolean hasDeclaredTypes) {
        this.hasDeclaredTypes = hasDeclaredTypes;
    }

    public boolean hasDeclaredTypes() {
        return hasDeclaredTypes;
    }

    public void setHasFunctions(boolean hasFunctions) {
        this.hasFunctions = hasFunctions;
    }

    public boolean hasFunctions() {
        return hasFunctions;
    }

    public void setHasRules(boolean hasRules) {
        this.hasRules = hasRules;
    }

    public boolean hasRules() {
        return hasRules;
    }

    public static class Global {

        private String type;
        private String name;

        public Global(String type,
                          String name) {
            this.type = type;
            this.name = name;
        }

        public String getType() {
            return type;
        }

        public String getName() {
            return name;
        }

    }

    public static class Import {

        private String type;

        public Import(String t) {
            this.type = t;
        }

        public String getType() {
            return this.type;
        }

    }

}
