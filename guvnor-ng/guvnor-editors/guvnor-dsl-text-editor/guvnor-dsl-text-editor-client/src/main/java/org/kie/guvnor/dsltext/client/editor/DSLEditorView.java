/*
 * Copyright 2012 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.guvnor.dsltext.client.editor;

import com.google.gwt.user.client.ui.Composite;

/**
 * The view for the Domain Specific Language editor
 */
public class DSLEditorView
        extends Composite
        implements DSLEditorPresenter.View {

    @Override
    public void setContent( String content ) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public String getContent() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public boolean isDirty() {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void setNotDirty() {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public boolean confirmClose() {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

}
