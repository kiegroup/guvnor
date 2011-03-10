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
        return name == null || name.isEmpty();
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
