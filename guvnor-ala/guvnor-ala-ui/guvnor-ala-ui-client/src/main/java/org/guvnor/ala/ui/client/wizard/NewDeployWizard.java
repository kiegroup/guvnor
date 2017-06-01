/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.guvnor.ala.ui.client.wizard;

import java.util.Collection;
import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import org.guvnor.ala.ui.client.events.RefreshRuntimeEvent;
import org.guvnor.ala.ui.client.wizard.pipeline.SelectPipelinePagePresenter;
import org.guvnor.ala.ui.client.wizard.source.SourceConfigurationPagePresenter;
import org.guvnor.ala.ui.model.PipelineKey;
import org.guvnor.ala.ui.model.Provider;
import org.guvnor.ala.ui.model.Source;
import org.guvnor.ala.ui.service.RuntimeService;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.uberfire.workbench.events.NotificationEvent;

import static org.guvnor.ala.ui.client.resources.i18n.GuvnorAlaUIConstants.NewDeployWizard_PipelineStartErrorMessage;
import static org.guvnor.ala.ui.client.resources.i18n.GuvnorAlaUIConstants.NewDeployWizard_PipelineStartSuccessMessage;
import static org.guvnor.ala.ui.client.resources.i18n.GuvnorAlaUIConstants.NewDeployWizard_Title;

@ApplicationScoped
public class NewDeployWizard
        extends AbstractMultiPageWizard {

    private final SelectPipelinePagePresenter selectPipelinePage;
    private final SourceConfigurationPagePresenter sourceConfigPage;

    private final Caller<RuntimeService> runtimeService;

    private final Event<RefreshRuntimeEvent> refreshRuntimeEvent;

    private Provider provider;

    @Inject
    public NewDeployWizard(final SelectPipelinePagePresenter selectPipelinePage,
                           final SourceConfigurationPagePresenter sourceConfigPage,
                           final TranslationService translationService,
                           final Caller<RuntimeService> runtimeService,
                           final Event<NotificationEvent> notification,
                           final Event<RefreshRuntimeEvent> refreshRuntimeEvent) {
        super(translationService,
              notification);
        this.selectPipelinePage = selectPipelinePage;
        this.sourceConfigPage = sourceConfigPage;
        this.runtimeService = runtimeService;
        this.refreshRuntimeEvent = refreshRuntimeEvent;
    }

    @PostConstruct
    public void init() {
        pages.add(selectPipelinePage);
        pages.add(sourceConfigPage);
    }

    public void start(final Provider provider,
                      final Collection<PipelineKey> pipelines) {
        this.provider = provider;
        clear();
        selectPipelinePage.setup(pipelines);
        sourceConfigPage.setup();
        super.start();
    }

    @Override
    public String getTitle() {
        return translationService.getTranslation(NewDeployWizard_Title);
    }

    @Override
    public int getPreferredHeight() {
        return 550;
    }

    @Override
    public int getPreferredWidth() {
        return 800;
    }

    @Override
    public void complete() {
        final PipelineKey pipeline = selectPipelinePage.getPipeline();
        final String runtime = sourceConfigPage.getRuntime();
        final Source source = sourceConfigPage.buildSource();

        runtimeService.call((Void aVoid) -> onPipelineStartSuccess(),
                            (message, throwable) -> onPipelineStartError()).createRuntime(provider.getKey(),
                                                                                          runtime,
                                                                                          source,
                                                                                          pipeline);
    }

    private void onPipelineStartSuccess() {
        notification.fire(new NotificationEvent(translationService.getTranslation(NewDeployWizard_PipelineStartSuccessMessage),
                                                NotificationEvent.NotificationType.SUCCESS));
        NewDeployWizard.super.complete();
        refreshRuntimeEvent.fire(new RefreshRuntimeEvent(provider.getKey()));
    }

    private boolean onPipelineStartError() {
        notification.fire(new NotificationEvent(translationService.getTranslation(NewDeployWizard_PipelineStartErrorMessage),
                                                NotificationEvent.NotificationType.ERROR));
        NewDeployWizard.this.pageSelected(0);
        NewDeployWizard.this.start();
        return false;
    }

    private void clear() {
        selectPipelinePage.clear();
        sourceConfigPage.clear();
    }
}
