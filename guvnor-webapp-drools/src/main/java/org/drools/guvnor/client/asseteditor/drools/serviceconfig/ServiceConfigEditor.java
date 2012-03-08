/*
 * Copyright 2012 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

/*Every That is commented in relate to de attribute data is because a NEP*/
package org.drools.guvnor.client.asseteditor.drools.serviceconfig;

import java.util.ArrayList;
import java.util.Collection;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.TabLayoutPanel;
import com.google.gwt.user.client.ui.Widget;
import org.drools.guvnor.client.asseteditor.EditorWidget;
import org.drools.guvnor.client.asseteditor.RuleViewer;
import org.drools.guvnor.client.asseteditor.SaveEventListener;
import org.drools.guvnor.client.common.DirtyableComposite;
import org.drools.guvnor.client.common.ErrorPopup;
import org.drools.guvnor.client.common.FormStylePopup;
import org.drools.guvnor.client.explorer.ClientFactory;
import org.drools.guvnor.client.explorer.ClosableLabel;
import org.drools.guvnor.client.messages.Constants;
import org.drools.guvnor.client.rpc.ArtifactDependenciesService;
import org.drools.guvnor.client.rpc.ArtifactDependenciesServiceAsync;
import org.drools.guvnor.client.rpc.Asset;
import org.drools.guvnor.client.rpc.MavenArtifact;
import org.drools.guvnor.client.widgets.drools.explorer.ArtifactDependenciesExplorerWidget;
import org.drools.guvnor.client.widgets.drools.explorer.ArtifactDependenciesReadyCommand;

public class ServiceConfigEditor extends DirtyableComposite
        implements EditorWidget, SaveEventListener {

    // UI
    interface ServiceConfigEditorBinder extends UiBinder<Widget, ServiceConfigEditor> {

    }

    private static ServiceConfigEditorBinder uiBinder = GWT.create(ServiceConfigEditorBinder.class);

    @UiField
    protected Button btnDownloadWar;

    @UiField
    protected Button btnArtifacts;

    @UiField
    protected TabLayoutPanel tabPanel;

    final ArtifactDependenciesServiceAsync mavenArtifactsAsync = (ArtifactDependenciesServiceAsync) GWT.create(ArtifactDependenciesService.class);

    final private Asset asset;
    final private String assetUUID;
    final private String assetName;
    private ServiceConfig config;

    private Collection<MavenArtifact> serviceArtifacts = null;

    public ServiceConfigEditor(final Asset a, final RuleViewer v, final ClientFactory clientFactory, final EventBus eventBus) {
        this(a, clientFactory);
    }

    public ServiceConfigEditor(final Asset asset, final ClientFactory clientFactory) {
        this.asset = asset;
        this.assetUUID = asset.getUuid();
        this.assetName = asset.getName();
        this.config = (ServiceConfig) asset.getContent();

        this.initWidget(uiBinder.createAndBindUi(this));

        for (final ServiceKBaseConfig activeKbase : config.getKbases()) {
            addKBasePainel(activeKbase, clientFactory);
        }

        final HorizontalPanel panel = new HorizontalPanel();
        final Anchor linkNewKBase = new Anchor("[+]");
        linkNewKBase.setStyleName("serviceTab");

        panel.add(linkNewKBase);

        tabPanel.add(new HTML(""), panel);

        tabPanel.addSelectionHandler(new SelectionHandler<Integer>() {
            public void onSelection(SelectionEvent<Integer> integerSelectionEvent) {
                if (integerSelectionEvent.getSelectedItem() == tabPanel.getWidgetCount() - 1) {
                    final ServiceKBaseConfig newKbase = new ServiceKBaseConfig(config.getNextKBaseName());
                    newKbase.addKsession(new ServiceKSessionConfig(newKbase.getNextKSessionName()));
                    config.addKBase(newKbase);
                    addKBasePainel(newKbase, clientFactory);
                    tabPanel.remove(tabPanel.getWidgetCount() - 2);
                    tabPanel.add(new HTML(""), panel);
                    tabPanel.selectTab(tabPanel.getWidgetCount() - 2);
                }
            }
        });

        tabPanel.selectTab(0);

        this.loadContent();
    }

    private void addKBasePainel(final ServiceKBaseConfig kbase, final ClientFactory clientFactory) {

        final ClosableLabel closableLabel = new ClosableLabel(kbase.getName());

        final UpdateTabEvent updateTabEvent = new UpdateTabEvent() {
            public void onUpdate(String newName) {
                closableLabel.updateTitle(newName);
            }
        };

        tabPanel.add(new KBaseConfigPanel(config, kbase, updateTabEvent, asset.getMetaData().getModuleUUID(), asset.getMetaData().getModuleName(), clientFactory), closableLabel);

        closableLabel.addCloseHandler(new CloseHandler<ClosableLabel>() {
            public void onClose(CloseEvent<ClosableLabel> closableLabelCloseEvent) {
                if (tabPanel.getWidgetCount() <= 2) {
                    Window.alert(Constants.INSTANCE.CantDeleteKBase());
                    return;
                }
                if (!Window.confirm(Constants.INSTANCE.ConfirmDeleteKBase())) {
                    return;
                }
                for (int i = 0; i < tabPanel.getWidgetCount(); i++) {
                    if (tabPanel.getWidget(i) instanceof KBaseConfigPanel) {
                        final KBaseConfigPanel editor = (KBaseConfigPanel) tabPanel.getWidget(i);
                        if (editor.getKBase().getName().equals(kbase.getName())) {
                            config.removeKBase(kbase.getName());
                            tabPanel.remove(i);
                            break;
                        }
                    }
                }
            }
        });
    }

    private void loadContent() {
        mavenArtifactsAsync.getDependencies(new AsyncCallback<Collection<MavenArtifact>>() {
            public void onFailure(final Throwable e) {
                ErrorPopup.showMessage(e.getMessage());
            }

            public void onSuccess(Collection<MavenArtifact> result) {
                serviceArtifacts = new ArrayList<MavenArtifact>(result);
            }
        });
    }

    public void onSave() {
        for (int i = 0; i < tabPanel.getWidgetCount(); i++) {
            if (tabPanel.getWidget(i) instanceof KBaseConfigPanel) {
                final KBaseConfigPanel kbaseEditor = (KBaseConfigPanel) tabPanel.getWidget(i);
                kbaseEditor.onSave();
            }
        }
        asset.setContent(config);
    }

    public void onAfterSave() {
    }

    @UiHandler("btnArtifacts")
    public void setupMavenArtifacts(final ClickEvent e) {

        final ArtifactDependenciesExplorerWidget widget =
                new ArtifactDependenciesExplorerWidget(assetName, serviceArtifacts, config.getExcludedArtifacts());

        final InternalPopup popup = new InternalPopup(widget.asWidget(), Constants.INSTANCE.ManageDependenciesEllipsis());

        popup.addOkButtonClickHandler(new ClickHandler() {

            public void onClick(ClickEvent event) {
                try {
                    widget.processExcludedArtifacts(new ArtifactDependenciesReadyCommand() {
                        public void onSuccess(Collection<MavenArtifact> excludedItems) {
                            config.setExcludedArtifacts(excludedItems);
                        }

                        public void onFailure(Throwable cause) {
                            ErrorPopup.showMessage(cause.getMessage());
                        }
                    });

                } catch (Exception e) {
                    ErrorPopup.showMessage(e.getMessage());
                }
                popup.hide();
            }
        });
        popup.show();
    }

    @UiHandler("btnDownloadWar")
    public void downloadWarFile(ClickEvent e) {
        Window.open(GWT.getModuleBaseURL() + "serviceWarBuilderAndDownloadHandler?uuid=" + assetUUID, "service download", "");
    }

    private class InternalPopup extends FormStylePopup {

        private final Button ok = new Button(Constants.INSTANCE.OK());

        public InternalPopup(final Widget content, final String title) {
            setTitle(title);

            final HorizontalPanel hor = new HorizontalPanel();
            final Button cancel = new Button(Constants.INSTANCE.Cancel());

            hor.add(ok);
            hor.add(cancel);

            addRow(content);
            addRow(hor);

            cancel.addClickHandler(new ClickHandler() {
                public void onClick(final ClickEvent event) {
                    hide();
                }
            });
        }

        public void addOkButtonClickHandler(final ClickHandler clickHandler) {
            ok.addClickHandler(clickHandler);
        }
    }

    public static interface UpdateTabEvent {

        public void onUpdate(final String newName);
    }
}