package org.kie.guvnor.globals.client.editor;

import java.util.List;

import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Label;
import org.kie.guvnor.globals.model.Global;

/**
 * The GlobalsEditorPresenter's view implementation
 */
public class GlobalsEditorView extends Composite implements GlobalsEditorPresenter.View {

    private GlobalsEditorPresenter presenter;

    public GlobalsEditorView() {
        initWidget( new Label( "Editor to go here!" ) );
    }

    @Override
    public void init( final GlobalsEditorPresenter presenter ) {
        this.presenter = presenter;
    }

    @Override
    public void setContent( final List<Global> content ) {
    }

}
