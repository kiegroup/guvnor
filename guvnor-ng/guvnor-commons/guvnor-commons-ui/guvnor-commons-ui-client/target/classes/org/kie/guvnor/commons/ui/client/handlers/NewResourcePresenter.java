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

package org.kie.guvnor.commons.ui.client.handlers;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import org.jboss.errai.bus.client.api.RemoteCallback;
import org.jboss.errai.ioc.client.api.Caller;
import org.jboss.errai.ioc.client.container.IOCBeanDef;
import org.jboss.errai.ioc.client.container.IOCBeanManager;
import org.kie.guvnor.project.service.ProjectService;
import org.uberfire.backend.events.PathChangeEvent;
import org.uberfire.backend.vfs.Path;
import org.uberfire.client.mvp.UberView;

@ApplicationScoped
public class NewResourcePresenter {

    public interface View
            extends
            UberView<NewResourcePresenter> {

        void show();

        void hide();

        void setActiveHandler( final NewResourceHandler activeHandler );

        void addHandler( final NewResourceHandler handler );

        String getFileName();

        void showMissingNameError();

        void enableHandler( final NewResourceHandler handler,
                            final boolean enable );

    }

    @Inject
    private IOCBeanManager iocBeanManager;

    @Inject
    private Caller<ProjectService> projectService;

    @Inject
    private View view;

    private NewResourceHandler activeHandler = null;

    private final List<NewResourceHandler> handlers = new LinkedList<NewResourceHandler>();

    @PostConstruct
    private void setup() {
        view.init( this );
        final Collection<IOCBeanDef<NewResourceHandler>> handlerBeans = iocBeanManager.lookupBeans( NewResourceHandler.class );
        for ( IOCBeanDef<NewResourceHandler> handlerBean : handlerBeans ) {
            final NewResourceHandler handler = handlerBean.getInstance();
            handlers.add( handler );
            view.addHandler( handler );
        }
    }

    public void selectedPathChanged( @Observes final PathChangeEvent event ) {
        final Path path = event.getPath();
        if ( path == null ) {
            enableNewResourceHandlers( false );
        }
        projectService.call( new RemoteCallback<Path>() {
            @Override
            public void callback( final Path path ) {
                enableNewResourceHandlers( path != null );
            }
        } ).resolveProject( path );
    }

    private void enableNewResourceHandlers( final boolean enable ) {
        for ( NewResourceHandler handler : this.handlers ) {
            if ( handler.requiresProjectPath() ) {
                view.enableHandler( handler,
                                    enable );
            }
        }
    }

    public void show() {
        show( null );
    }

    public void show( final NewResourceHandler handler ) {
        activeHandler = handler;
        if ( activeHandler == null ) {
            activeHandler = handlers.get( 0 );
        }
        view.show();
        view.setActiveHandler( activeHandler );
    }

    void setActiveHandler( final NewResourceHandler handler ) {
        activeHandler = handler;
    }

    public void makeItem() {
        if ( activeHandler != null ) {
            if ( validate() ) {
                if ( activeHandler.validate() ) {
                    final String fileName = view.getFileName();
                    final String fileType = activeHandler.getFileType();
                    final String fullFileName = makeFullFileName( fileName,
                                                                  fileType );
                    activeHandler.create( fullFileName );
                    view.hide();
                }
            }
        }
    }

    private String makeFullFileName( final String fileName,
                                     final String fileType ) {
        if ( fileType == null ) {
            return fileName;
        }
        return fileName + "." + fileType;
    }

    private boolean validate() {
        boolean isValid = true;
        if ( view.getFileName().isEmpty() ) {
            view.showMissingNameError();
            isValid = false;
        }
        return isValid;
    }

}
