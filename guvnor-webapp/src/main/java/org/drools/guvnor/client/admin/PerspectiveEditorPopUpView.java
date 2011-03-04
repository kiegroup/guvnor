package org.drools.guvnor.client.admin;

interface PerspectiveEditorPopUpView {

    interface Presenter {

        void onSave();

        void onCancel();
    }

    void show();

    void hide();

    void setPresenter(Presenter presenter);

    void setName(String s);

    String getName();

    void setUrl(String s);

    String getUrl();

    void showNameCanNotBeEmptyWarning();

    void showUrlCanNotBeEmptyWarning();
}
