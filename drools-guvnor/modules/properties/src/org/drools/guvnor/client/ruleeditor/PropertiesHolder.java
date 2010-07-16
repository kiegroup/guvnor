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

package org.drools.guvnor.client.ruleeditor;

import org.drools.guvnor.client.modeldriven.brl.PortableObject;

import java.util.List;
import java.util.ArrayList;

import com.google.gwt.user.client.rpc.IsSerializable;

/**
 *
 */
public class PropertiesHolder implements PortableObject {

    /**
	 * @gwt.typeArgs <org.drools.guvnor.client.ruleeditor.PropertyHolder>
	 */
    List<PropertyHolder> list = new ArrayList<PropertyHolder>();

    public PropertiesHolder() {
        list.add(new PropertyHolder("x1", "yyy1"));
        list.add(new PropertyHolder("x2", "yyy2"));
        list.add(new PropertyHolder("x3", "yyy3"));
        list.add(new PropertyHolder("x4", "yyy4"));
        list.add(new PropertyHolder("x5", "yyy5"));
        list.add(new PropertyHolder("x6", "yyy6"));
        list.add(new PropertyHolder("x7", "yyy7"));
    }
}