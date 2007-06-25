package org.drools.brms.client.common;
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



import com.google.gwt.user.client.ui.Composite;

public abstract class DirtyableComposite extends Composite implements DirtyableWidget  {
    protected boolean dirtyflag = false;
    
    /* (non-Javadoc)
     * @see org.drools.brms.client.common.isDirtable#isDirty()
     */
    public boolean isDirty() {
        return this.dirtyflag;
    } 
    /* (non-Javadoc)
     * @see org.drools.brms.client.common.isDirtable#resetDirty()
     */
    public void resetDirty(){
        this.dirtyflag = false;
    }
    /* (non-Javadoc)
     * @see org.drools.brms.client.common.isDirtable#makeDirty()
     */
    public void makeDirty(){
        this.dirtyflag = true;
    }
}