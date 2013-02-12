package org.kie.guvnor.projectconfigscreen.client.forms;

import org.kie.guvnor.configresource.client.resources.i18n.ImportConstants;
import org.kie.guvnor.configresource.client.widget.ImportsWidgetFreeFormatPresenter;
import org.kie.guvnor.metadata.client.resources.i18n.MetadataConstants;
import org.kie.guvnor.metadata.client.widget.MetadataWidget;
import org.kie.guvnor.services.config.model.imports.Imports;
import org.kie.guvnor.services.metadata.model.Metadata;
import org.uberfire.backend.vfs.Path;
import org.uberfire.client.common.MultiPageEditorView;
import org.uberfire.client.common.Page;

import javax.inject.Inject;

public class ProjectConfigScreenViewImpl
        extends MultiPageEditorView
        implements ProjectConfigScreenView {

    private final ImportsWidgetFreeFormatPresenter importsWidget;
    private final MetadataWidget metadataWidget = new MetadataWidget();

    private Presenter presenter;

    @Inject
    public ProjectConfigScreenViewImpl(ImportsWidgetFreeFormatPresenter importsWidget) {
        this.importsWidget = importsWidget;
        addPage(new Page(importsWidget, ImportConstants.INSTANCE.Imports()) {
            @Override
            public void onFocus() {
            }

            @Override
            public void onLostFocus() {
            }
        });

        addPage(new Page(metadataWidget, MetadataConstants.INSTANCE.Metadata()) {
            @Override
            public void onFocus() {
                presenter.onShowMetadata();
            }

            @Override
            public void onLostFocus() {
            }
        });
    }

    @Override
    public void setPresenter(Presenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public void setImports(Path path, Imports imports) {
        importsWidget.setImports(path, imports);
    }

    @Override
    public void setMetadata(Metadata metadata) {
        metadataWidget.setContent(metadata, false);
    }
}
