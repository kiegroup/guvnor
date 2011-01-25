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
 * Represents first order logic like Or, Not, Exists.
 *
 * @author Michael Neale
 */
public class CompositeFactPattern
    implements
    IPattern {
    public static final String COMPOSITE_TYPE_NOT    = "not";
    public static final String COMPOSITE_TYPE_EXISTS = "exists";
    public static final String COMPOSITE_TYPE_OR     = "or";

    /**
     * this will one of: [Not, Exist, Or]
     */
    public String              type;

    /**
     * The patterns.
     */
    private IFactPattern[]       patterns;



    /**
     * This type should be from the contants in this class of course.
     */
    public CompositeFactPattern(final String type) {
        this.type = type;
    }

    public CompositeFactPattern() {
    }

    public void addFactPattern(final IFactPattern pat) {
        if ( this.patterns == null ) {
            this.patterns = new FactPattern[0];
        }

        final IFactPattern[] list = this.patterns;
        final IFactPattern[] newList = new IFactPattern[list.length + 1];
        System.arraycopy(list, 0, newList, 0, list.length);
        newList[list.length] = pat;

        this.patterns = newList;
    }

    public IFactPattern[] getPatterns() {
        return patterns;
    }
    
}
