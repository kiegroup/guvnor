package org.kie.guvnor.client.perspectives;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import org.jboss.errai.bus.client.api.RemoteCallback;
import org.jboss.errai.ioc.client.api.Caller;
import org.jboss.errai.ioc.client.container.IOCBeanDef;
import org.jboss.errai.ioc.client.container.IOCBeanManager;
import org.kie.guvnor.client.handlers.NewProjectHandler;
import org.kie.guvnor.commons.ui.client.handlers.NewResourceHandler;
import org.kie.guvnor.commons.ui.client.handlers.NewResourcePresenter;
import org.kie.guvnor.project.service.ProjectService;
import org.uberfire.backend.events.PathChangeEvent;
import org.uberfire.backend.vfs.Path;
import org.uberfire.client.mvp.Command;
import org.uberfire.client.workbench.widgets.menu.MenuItem;
import org.uberfire.client.workbench.widgets.menu.impl.DefaultMenuBar;
import org.uberfire.client.workbench.widgets.menu.impl.DefaultMenuItemCommand;
import org.uberfire.client.workbench.widgets.menu.impl.DefaultMenuItemSubMenu;

/**
 * A menu to create New Resources
 */
@ApplicationScoped
public class NewResourcesMenu extends DefaultMenuItemSubMenu {

    @Inject
    private IOCBeanManager iocBeanManager;

    @Inject
    private Caller<ProjectService> projectService;

    @Inject
    private NewResourcePresenter newResourcePresenter;

    private Map<NewResourceHandler, MenuItem> newResourceHandlers = new HashMap<NewResourceHandler, MenuItem>();

    public NewResourcesMenu() {
        super( "New",
               new DefaultMenuBar() );
    }

    @PostConstruct
    public void setup() {
        final Collection<IOCBeanDef<NewResourceHandler>> handlerBeans = iocBeanManager.lookupBeans( NewResourceHandler.class );
        if ( handlerBeans.size() > 0 ) {
            for ( IOCBeanDef<NewResourceHandler> handlerBean : handlerBeans ) {
                final NewResourceHandler activeHandler = handlerBean.getInstance();
                final String description = activeHandler.getDescription();
                final MenuItem menuItem = new DefaultMenuItemCommand( description,
                                                                      new Command() {
                                                                          @Override
                                                                          public void execute() {
                                                                              newResourcePresenter.show( activeHandler );
                                                                          }
                                                                      } );
                newResourceHandlers.put( activeHandler,
                                         menuItem );
                getSubMenu().addItem( menuItem );
            }
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
        } ).resolvePackage( path );
    }

    private void enableNewResourceHandlers( final boolean enable ) {
        for ( Map.Entry<NewResourceHandler, MenuItem> e : this.newResourceHandlers.entrySet() ) {
            if ( !( e.getKey() instanceof NewProjectHandler ) ) {
                e.getValue().setEnabled( enable );
            }
        }
    }

}
