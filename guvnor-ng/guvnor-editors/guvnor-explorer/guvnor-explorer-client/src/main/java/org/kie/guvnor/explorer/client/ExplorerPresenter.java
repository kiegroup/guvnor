package org.kie.guvnor.explorer.client;

import java.util.Collection;
import java.util.List;
import javax.inject.Inject;

import com.google.gwt.user.client.ui.IsWidget;
import org.jboss.errai.bus.client.api.RemoteCallback;
import org.jboss.errai.ioc.client.api.Caller;
import org.kie.guvnor.explorer.client.resources.i18n.Constants;
import org.uberfire.backend.FileExplorerRootService;
import org.uberfire.backend.Root;
import org.uberfire.backend.vfs.VFSService;
import org.uberfire.client.annotations.OnStart;
import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.annotations.WorkbenchPartView;
import org.uberfire.client.annotations.WorkbenchScreen;
import org.uberfire.client.mvp.UberView;

/**
 * Repository, Package, Folder and File explorer
 */
@WorkbenchScreen(identifier = "org.kie.guvnor.explorer")
public class ExplorerPresenter {

    @Inject
    private Caller<VFSService> vfsService;

    @Inject
    private Caller<FileExplorerRootService> rootService;

    @Inject
    private View view;

    public interface View
            extends
            UberView<ExplorerPresenter> {

        void setFocus();

        void reset();

        void removeIfExists( final Root root );

        void addNewRoot( final Root root );
    }

    @OnStart
    public void onStart() {

        view.reset();

        rootService.call( new RemoteCallback<Collection<Root>>() {
            @Override
            public void callback( Collection<Root> response ) {
                for ( final Root root : response ) {
                    view.removeIfExists( root );
                    view.addNewRoot( root );
                }
            }
        } ).listRoots();
    }

    @WorkbenchPartView
    public IsWidget getView() {
        return this.view.asWidget();
    }

    @WorkbenchPartTitle
    public String getTitle() {
        return Constants.INSTANCE.explorerTitle();
    }

}
