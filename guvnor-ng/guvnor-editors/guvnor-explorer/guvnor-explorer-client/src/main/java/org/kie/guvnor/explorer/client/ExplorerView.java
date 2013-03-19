package org.kie.guvnor.explorer.client;

import org.kie.guvnor.explorer.model.ExplorerContent;
import org.uberfire.client.mvp.UberView;

/**
 * Explorer View definition
 */
public interface ExplorerView extends
                              UberView<ExplorerPresenter> {

    void setContent( ExplorerContent content );

}
