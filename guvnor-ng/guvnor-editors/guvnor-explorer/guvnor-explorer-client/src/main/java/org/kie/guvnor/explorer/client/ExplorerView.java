package org.kie.guvnor.explorer.client;

import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.RequiresResize;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import org.uberfire.backend.Root;

/**
 * The ExplorerPresenter's view implementation
 */
public class ExplorerView extends Composite implements ExplorerPresenter.View,
                                                       RequiresResize {

    private ExplorerPresenter presenter;

    private final VerticalPanel container = new VerticalPanel();

    public ExplorerView() {
        initWidget( container );
    }

    @Override
    public void init( final ExplorerPresenter presenter ) {
        this.presenter = presenter;
    }

    @Override
    public void setFocus() {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void reset() {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void removeIfExists( Root root ) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void addNewRoot( Root root ) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void onResize() {
        final Widget p = getParent();
        if ( p != null ) {
            final int width = p.getOffsetWidth();
            final int height = p.getOffsetHeight();
            setPixelSize( width,
                          height );
        }
    }

}
