/**
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

package org.drools.ide.common.client.modeldriven.brl;

/**
 * This class is the parent for field setting or assertion actions.
 * 
 * Contains the list of fields and their values to be set.
 * 
 * @author Michael Neale
 *
 */
public abstract class ActionFieldList
    implements
    IAction {

    public ActionFieldValue[] fieldValues = new ActionFieldValue[0];

    public void removeField(final int idx) {
        //Unfortunately, this is kinda duplicate code with other methods, 
        //but with typed arrays, and GWT, its not really possible to do anything "better" 
        //at this point in time. 
        final ActionFieldValue[] newList = new ActionFieldValue[this.fieldValues.length - 1];
        int newIdx = 0;
        for ( int i = 0; i < this.fieldValues.length; i++ ) {

            if ( i != idx ) {
                newList[newIdx] = this.fieldValues[i];
                newIdx++;
            }

        }
        this.fieldValues = newList;
    }

    public void addFieldValue(final ActionFieldValue val) {
        if ( this.fieldValues == null ) {
            this.fieldValues = new ActionFieldValue[1];
            this.fieldValues[0] = val;
        } else {
            final ActionFieldValue[] newList = new ActionFieldValue[this.fieldValues.length + 1];
            for ( int i = 0; i < this.fieldValues.length; i++ ) {
                newList[i] = this.fieldValues[i];
            }
            newList[this.fieldValues.length] = val;
            this.fieldValues = newList;
        }
    }

}
