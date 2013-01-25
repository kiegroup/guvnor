package org.kie.guvnor.explorer.client.widget;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import org.kie.commons.validation.PortablePreconditions;
import org.kie.guvnor.explorer.client.ExplorerPresenter;
import org.kie.guvnor.explorer.client.resources.Resources;
import org.kie.guvnor.explorer.client.resources.images.ImageResources;
import org.uberfire.backend.vfs.Path;

/**
 * A widget representing a RepositoryWidget file
 */
public class FileWidget extends Composite {

    private final Anchor anchor = new Anchor();
    private final HorizontalPanel container = new HorizontalPanel();
    private final Image icon = new Image( ImageResources.INSTANCE.fileIcon() );

    private final Path path;
    private final String caption;
    private final ExplorerPresenter presenter;

    public FileWidget( final Path path,
                       final ExplorerPresenter presenter ) {
        this( path,
              path.getFileName(),
              presenter );
    }

    public FileWidget( final Path path,
                       final String caption,
                       final ExplorerPresenter presenter ) {
        PortablePreconditions.checkNotNull( "path",
                                            path );
        PortablePreconditions.checkNotNull( "caption",
                                            caption );
        PortablePreconditions.checkNotNull( "presenter",
                                            presenter );
        this.path = path;
        this.caption = caption;
        this.presenter = presenter;

        container.add( icon );
        container.add( anchor );
        container.setStyleName( Resources.INSTANCE.CSS().item() );
        anchor.setText( caption );
        initWidget( container );

        anchor.addClickHandler( new ClickHandler() {

            @Override
            public void onClick( ClickEvent event ) {
                presenter.openResource( path );
            }
        } );
    }

}
