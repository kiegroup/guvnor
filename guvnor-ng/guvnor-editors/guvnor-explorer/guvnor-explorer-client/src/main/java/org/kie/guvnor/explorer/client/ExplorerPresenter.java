package org.kie.guvnor.explorer.client;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import org.jboss.errai.bus.client.api.RemoteCallback;
import org.jboss.errai.ioc.client.api.Caller;
import org.kie.guvnor.explorer.client.resources.i18n.Constants;
import org.kie.guvnor.explorer.model.Item;
import org.kie.guvnor.explorer.model.RepositoryItem;
import org.kie.guvnor.explorer.service.ExplorerService;
import org.uberfire.backend.FileExplorerRootService;
import org.uberfire.backend.Root;
import org.uberfire.backend.events.PathChangeEvent;
import org.uberfire.backend.vfs.Path;
import org.uberfire.client.annotations.OnStart;
import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.annotations.WorkbenchPartView;
import org.uberfire.client.annotations.WorkbenchScreen;
import org.uberfire.client.context.WorkbenchContext;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.client.mvp.UberView;

/**
 * Repository, Package, Folder and File explorer
 */
@WorkbenchScreen(identifier = "org.kie.guvnor.explorer")
public class ExplorerPresenter {

    @Inject
    private Caller<FileExplorerRootService> rootService;

    @Inject
    private Caller<ExplorerService> explorerService;

    @Inject
    private PlaceManager placeManager;

    @Inject
    private Event<PathChangeEvent> pathChangeEvent;

    @Inject
    private WorkbenchContext context;

    @Inject
    private View view;

    private Path activePath;

    public interface View
            extends
            UberView<ExplorerPresenter> {

        void setItems( List<Item> item );

    }

    @OnStart
    public void onStart() {
        final Path p = context.getActivePath();
        loadItems( p );
    }

    @WorkbenchPartView
    public UberView<ExplorerPresenter> getView() {
        return this.view;
    }

    @WorkbenchPartTitle
    public String getTitle() {
        return Constants.INSTANCE.explorerTitle();
    }

    public void pathChangeHandler( @Observes PathChangeEvent event ) {
        final Path path = event.getPath();
        loadItems( path );
    }

    public void openResource( final Path path ) {
        placeManager.goTo( path );
    }

    public void setContext( final Path path ) {
        pathChangeEvent.fire( new PathChangeEvent( path ) );
    }

    private void loadItems( final Path path ) {
        if ( path == null ) {
            loadRootItems();
            activePath = path;
        } else {
            if ( !path.equals( activePath ) ) {
                explorerService.call( new RemoteCallback<List<Item>>() {

                    @Override
                    public void callback( final List<Item> items ) {
                        view.setItems( items );
                        activePath = path;
                    }

                } ).getItemsForPathScope( path );
            }
        }
    }

    private void loadRootItems() {
        rootService.call( new RemoteCallback<Collection<Root>>() {
            @Override
            public void callback( final Collection<Root> roots ) {
                final List<Item> items = new ArrayList<Item>();
                for ( final Root root : roots ) {
                    items.add( wrapRoot( root ) );
                }
                view.setItems( items );
            }
        } ).listRoots();
    }

    private RepositoryItem wrapRoot( final Root root ) {
        final RepositoryItem repositoryItem = new RepositoryItem( root.getPath() );
        return repositoryItem;
    }

}
