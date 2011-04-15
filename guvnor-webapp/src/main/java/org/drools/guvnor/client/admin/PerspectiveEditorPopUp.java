/*
 * Copyright 2011 JBoss Inc
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 */

package org.drools.guvnor.client.admin;

import org.drools.guvnor.client.admin.PerspectiveEditorPopUpView.Presenter;
import org.drools.guvnor.client.rpc.IFramePerspectiveConfiguration;
import org.drools.guvnor.client.util.SaveCommand;

public class PerspectiveEditorPopUp implements Presenter {

    private final PerspectiveEditorPopUpView view;
    private SaveCommand saveCommand;
    private String uuid;

    public PerspectiveEditorPopUp(PerspectiveEditorPopUpView view) {
        this.view = view;
        view.setPresenter(this);
    }

    public void show(SaveCommand saveCommand) {
        this.saveCommand = saveCommand;
        view.show();
    }

    public void onSave() {
        String name = view.getName();
        String url = view.getUrl();

        if (isNullOrEmpty(name)) {
            view.showNameCanNotBeEmptyWarning();
        } else if (isNullOrEmpty(url)) {
            view.showUrlCanNotBeEmptyWarning();
        } else {
            save(name, url);
        }
    }

    public void onCancel() {
        view.setName("");
        view.setUrl("");
        view.hide();
    }

    private void save(String name, String url) {
        IFramePerspectiveConfiguration newConfiguration = new IFramePerspectiveConfiguration();
        newConfiguration.setUuid(uuid);
        newConfiguration.setName(name);
        newConfiguration.setUrl(url);

        saveCommand.save(newConfiguration);
    }

    private boolean isNullOrEmpty(String name) {
        return name == null || name.length() ==0;
    }

    public void setConfiguration(IFramePerspectiveConfiguration iFramePerspectiveConfiguration) {
        uuid = iFramePerspectiveConfiguration.getUuid();
        view.setName(iFramePerspectiveConfiguration.getName());
        view.setUrl(iFramePerspectiveConfiguration.getUrl());
    }

    public void hide() {
        view.hide();
    }
}
