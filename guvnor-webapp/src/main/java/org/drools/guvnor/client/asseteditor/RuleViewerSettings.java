/*
 * Copyright 2010 JBoss Inc
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
package org.drools.guvnor.client.asseteditor;

public class RuleViewerSettings {

    private boolean metaVisible = true;
    private boolean docoVisible = true;
    private boolean standalone = false;

    public void setMetaVisible(boolean metaVisible) {
        this.metaVisible = metaVisible;
    }

    public void setDocoVisible(boolean docoVisible) {
        this.docoVisible = docoVisible;
    }

    public boolean isDocoVisible() {
        return docoVisible;
    }

    public boolean isMetaVisible() {
        return metaVisible;
    }

    public boolean isStandalone() {
        return standalone;
    }

    public void setStandalone(boolean standalone) {
        this.standalone = standalone;
    }
}
