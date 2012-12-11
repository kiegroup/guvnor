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
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import com.google.gwt.user.client.ui.IsWidget;
import org.jboss.errai.bus.client.api.RemoteCallback;
import org.jboss.errai.ioc.client.api.Caller;
import org.jboss.errai.ioc.client.container.IOCBeanDef;
import org.jboss.errai.ioc.client.container.IOCBeanManager;
import org.kie.commons.data.Pair;
import org.kie.commons.java.nio.file.attribute.BasicFileAttributes;
import org.uberfire.backend.vfs.Path;
import org.uberfire.backend.vfs.PathFactory;
import org.uberfire.backend.vfs.VFSService;
import org.uberfire.backend.vfs.VFSTempUtil;
import org.uberfire.client.mvp.UberView;

@ApplicationScoped
public class NewItemPresenter {

    public interface View
            extends
            UberView<NewItemPresenter> {

        void show();

        void hide();

        void setActivePath( final Path activePath );

        void setActiveHandler( final NewResourceHandler activeHandler );

        void addExtensionWidget( final String caption,
                                 final IsWidget widget );

        void addHandler( final NewResourceHandler handler );

        String getItemName();

        void showMissingNameError();

        void showMissingPathError();

    }

    @Inject
    private IOCBeanManager iocBeanManager;

    @Inject
    private Caller<VFSService> vfsService;

    @Inject
    private View view;

    private Path activePath = null;
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

    public void show() {
        show( null,
              null );
    }

    public void show( final Path activePath ) {
        show( activePath,
              null );
    }

    public void show( final Path path,
                      final NewResourceHandler handler ) {
        activeHandler = handler;
        if ( activeHandler == null ) {
            activeHandler = handlers.get( 0 );
        }
        setup( path );
    }

    private void setup( final Path path ) {
        try {
            vfsService.call( new RemoteCallback<Map>() {
                @Override
                public void callback( final Map response ) {
                    final BasicFileAttributes attrs = VFSTempUtil.toBasicFileAttributes( response );
                    if ( attrs.isRegularFile() ) {
                        activePath = stripFileName( path );
                    } else {
                        activePath = path;
                    }
                    showView();
                }

            } ).readAttributes( path );
        } catch ( Exception e ) {
            //TODO readAttributes currently fails if the Path is a Root
            activePath = path;
            showView();
        }
    }

    private Path stripFileName( final Path path ) {
        String uri = path.toURI();
        uri = uri.replace( path.getFileName(),
                           "" );
        return PathFactory.newPath( uri );
    }

    private void showView() {
        view.show();
        view.setActiveHandler( activeHandler );
        view.setActivePath( activePath );
    }

    void setActiveHandler( final NewResourceHandler handler ) {
        activeHandler = handler;
        final List<Pair<String, IsWidget>> extensions = handler.getExtensions();
        if ( extensions != null ) {
            for ( Pair<String, IsWidget> extension : extensions ) {
                view.addExtensionWidget( extension.getK1(),
                                         extension.getK2() );
            }
        }
    }

    public void makeItem() {
        if ( activeHandler != null ) {
            if ( validate() ) {
                if ( activeHandler.validate() ) {
                    activeHandler.create( buildFullPathName() );
                    view.hide();
                }
            }
        }
    }

    private boolean validate() {
        boolean isValid = true;
        if ( view.getItemName().isEmpty() ) {
            view.showMissingNameError();
            isValid = false;
        }
        if ( activePath == null ) {
            view.showMissingPathError();
            isValid = false;
        }
        return isValid;
    }

    private Path buildFullPathName() {
        final String pathName = this.activePath.toURI();
        final String fileName = view.getItemName() + "." + this.activeHandler.getFileType();
        final Path assetPath = PathFactory.newPath( fileName,
                                                    pathName + "/" + fileName );
        return assetPath;
    }

}
