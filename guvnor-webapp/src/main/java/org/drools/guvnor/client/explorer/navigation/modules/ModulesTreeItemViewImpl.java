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

package org.drools.guvnor.client.explorer.navigation.modules;

import java.util.Iterator;

import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.user.client.ui.TreeItem;

public class ModulesTreeItemViewImpl
        extends ModulesTreeItemBaseViewImpl
        implements
    ModulesTreeItemView {

    @Override
    protected SafeHtml getTreeHeader() {
        return this.presenter.getModuleTreeRootNodeHeader();
    }

    public void clearModulesTreeItem() {        
        tree.clear();
    }

    public void collapseAll() {
        Iterator<TreeItem> i = tree.treeItemIterator();
        while(i.hasNext()) {
            TreeItem ti = i.next();
            ti.setState( false );
        }
    }
    
    public void expandAll() {
        Iterator<TreeItem> i = tree.treeItemIterator();
        while(i.hasNext()) {
            TreeItem ti = i.next();
            ti.setState( true, false );
        }
    }

}
