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

package org.drools.guvnor.client.asseteditor.drools.modeldriven.ui;

/**
 * Configuration singleton class for RuleModeller. 
 */
public class RuleModellerConfiguration {
    private static RuleModellerConfiguration INSTANCE;
    
    private RuleModellerConfiguration(){
        
    }
    
    private boolean hideLHS;
    private boolean hideRHS;
    private boolean hideAttributes;
    
    public synchronized static RuleModellerConfiguration getInstance(){
        if (INSTANCE == null){
            INSTANCE = new RuleModellerConfiguration();
        }
        return INSTANCE;
    }

    public boolean isHideAttributes() {
        return hideAttributes;
    }

    public void setHideAttributes(boolean hideAttributes) {
        this.hideAttributes = hideAttributes;
    }

    public boolean isHideLHS() {
        return hideLHS;
    }

    public void setHideLHS(boolean hideLHS) {
        this.hideLHS = hideLHS;
    }

    public boolean isHideRHS() {
        return hideRHS;
    }

    public void setHideRHS(boolean hideRHS) {
        this.hideRHS = hideRHS;
    }
    
}
