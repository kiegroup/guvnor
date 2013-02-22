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

package org.kie.guvnor.viewsource.client.screen;

import com.google.gwt.user.client.ui.Composite;
import org.kie.guvnor.viewsource.client.widget.ViewDRLSourceWidget;

import javax.enterprise.context.Dependent;

@Dependent
public class ViewSourceViewImpl
        extends Composite
        implements ViewSourceView {

    final ViewDRLSourceWidget drlSourceViewer = new ViewDRLSourceWidget();

    public ViewSourceViewImpl() {
        initWidget( drlSourceViewer );
    }

    @Override
    public void setContent( final String content ) {
        drlSourceViewer.setContent( content );
    }

    @Override
    public void clear() {
        drlSourceViewer.clearContent();
    }
}
