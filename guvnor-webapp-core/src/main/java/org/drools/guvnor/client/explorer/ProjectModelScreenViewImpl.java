package org.drools.guvnor.client.explorer;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.Widget;

import javax.inject.Inject;

public class ProjectModelScreenViewImpl
        extends Composite
        implements ProjectModelScreenView {


    private Presenter presenter;

    interface ProjectModelScreenViewImplBinder
            extends
            UiBinder<Widget, ProjectModelScreenViewImpl> {

    }

    private static ProjectModelScreenViewImplBinder uiBinder = GWT.create(ProjectModelScreenViewImplBinder.class);

    @UiField(provided = true)
    KnowledgeBaseConfigurationForm form;

    @UiField
    ListBox kbaseList;

    @Inject
    public ProjectModelScreenViewImpl(KnowledgeBaseConfigurationForm form) {
        this.form = form;
        initWidget(uiBinder.createAndBindUi(this));
    }

    @Override
    public void setPresenter(Presenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public void addKnowledgeBaseConfiguration(String kbaseName) {
        kbaseList.addItem(kbaseName);
    }

    @UiHandler("kbaseList")
    public void handleChange(ChangeEvent event) {
        presenter.onKBaseSelection(kbaseList.getValue(kbaseList.getSelectedIndex()));
    }

    @Override
    public void showForm(KnowledgeBaseConfiguration knowledgeBaseConfiguration) {
        form.setConfig(knowledgeBaseConfiguration);
    }
}
