package org.kie.guvnor.globals.client.editor;

import java.util.List;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import org.kie.guvnor.globals.client.resources.i18n.GlobalsEditorConstants;
import org.kie.guvnor.globals.model.Global;
import org.uberfire.backend.vfs.Path;
import org.uberfire.client.annotations.OnStart;
import org.uberfire.client.annotations.WorkbenchEditor;
import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.annotations.WorkbenchPartView;
import org.uberfire.client.mvp.UberView;
import org.uberfire.client.workbench.widgets.events.ResourceCopiedEvent;
import org.uberfire.client.workbench.widgets.events.ResourceDeletedEvent;
import org.uberfire.client.workbench.widgets.events.ResourceRenamedEvent;

/**
 * Editor for Global variables
 */
@WorkbenchEditor(identifier = "org.kie.guvnor.globals", fileTypes = "*.global.drl")
public class GlobalsEditorPresenter {

    @Inject
    private View view;

    @Inject
    private Event<ResourceDeletedEvent> resourceDeletedEvent;

    @Inject
    private Event<ResourceRenamedEvent> resourceRenamedEvent;

    @Inject
    private Event<ResourceCopiedEvent> resourceCopiedEvent;

    private Path activePath;

    public interface View
            extends
            UberView<GlobalsEditorPresenter> {

        void setContent( List<Global> content );

    }

    @OnStart
    public void onStart( final Path path ) {
        loadContent( path );
    }

    @WorkbenchPartView
    public UberView<GlobalsEditorPresenter> getView() {
        return this.view;
    }

    @WorkbenchPartTitle
    public String getTitle() {
        return GlobalsEditorConstants.INSTANCE.globalsEditorTitle();
    }

    private void loadContent( final Path path ) {
    }

}
