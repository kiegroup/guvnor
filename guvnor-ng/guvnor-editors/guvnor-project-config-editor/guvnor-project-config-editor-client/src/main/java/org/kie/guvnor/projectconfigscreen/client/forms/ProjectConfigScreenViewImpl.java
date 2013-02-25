package org.kie.guvnor.projectconfigscreen.client.forms;

import org.drools.guvnor.models.commons.shared.imports.Imports;
import org.kie.guvnor.commons.service.metadata.model.Metadata;
import org.kie.guvnor.configresource.client.resources.i18n.ImportConstants;
import org.kie.guvnor.configresource.client.widget.unbound.ImportsWidgetPresenter;
import org.kie.guvnor.metadata.client.resources.i18n.MetadataConstants;
import org.kie.guvnor.metadata.client.widget.MetadataWidget;
import org.uberfire.backend.vfs.Path;
import org.uberfire.client.common.MultiPageEditorView;
import org.uberfire.client.common.Page;

import javax.inject.Inject;

public class ProjectConfigScreenViewImpl
        extends MultiPageEditorView
        implements ProjectConfigScreenView {

    private final ImportsWidgetPresenter importsWidget;
    private final MetadataWidget metadataWidget = new MetadataWidget();

    private Presenter presenter;

    @Inject
    public ProjectConfigScreenViewImpl( final ImportsWidgetPresenter importsWidget ) {
        this.importsWidget = importsWidget;
        addPage( new Page( importsWidget, ImportConstants.INSTANCE.Imports() ) {
            @Override
            public void onFocus() {
            }

            @Override
            public void onLostFocus() {
            }
        } );

        addPage( new Page( metadataWidget, MetadataConstants.INSTANCE.Metadata() ) {
            @Override
            public void onFocus() {
                presenter.onShowMetadata();
            }

            @Override
            public void onLostFocus() {
            }
        } );
    }

    @Override
    public void setPresenter( final Presenter presenter ) {
        this.presenter = presenter;
    }

    @Override
    public void setImports( final Path path,
                            final Imports imports ) {
        importsWidget.setContent( imports,
                                  false );
    }

    @Override
    public void setMetadata( final Metadata metadata ) {
        metadataWidget.setContent( metadata,
                                   false );
    }
}
