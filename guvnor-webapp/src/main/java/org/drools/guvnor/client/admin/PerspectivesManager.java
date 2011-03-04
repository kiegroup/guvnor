package org.drools.guvnor.client.admin;

import com.google.gwt.user.client.rpc.SerializationException;
import org.drools.guvnor.client.admin.PerspectivesManagerView.Presenter;
import org.drools.guvnor.client.common.GenericCallback;
import org.drools.guvnor.client.rpc.ConfigurationServiceAsync;
import org.drools.guvnor.client.rpc.IFramePerspectiveConfiguration;
import org.drools.guvnor.client.util.SaveCommand;

import java.util.Collection;

public class PerspectivesManager implements Presenter {

    private final ConfigurationServiceAsync configurationServiceAsync;
    private final PerspectivesManagerView view;

    public PerspectivesManager(ConfigurationServiceAsync configurationServiceAsync, PerspectivesManagerView view) {
        this.view = view;
        this.view.setPresenter(this);
        this.configurationServiceAsync = configurationServiceAsync;

        loadPerspectives();
    }

    private void loadPerspectives() {
        configurationServiceAsync.loadPerspectiveConfigurations(new GenericCallback<Collection<IFramePerspectiveConfiguration>>() {
            public void onSuccess(Collection<IFramePerspectiveConfiguration> iFramePerspectiveConfigurations) {
                addPerspectives(iFramePerspectiveConfigurations);
            }
        });
    }

    private void addPerspectives(Collection<IFramePerspectiveConfiguration> iFramePerspectiveConfigurations) {
        for (IFramePerspectiveConfiguration iFramePerspectiveConfiguration : iFramePerspectiveConfigurations) {
            view.addPerspective(iFramePerspectiveConfiguration.getUuid(), iFramePerspectiveConfiguration.getName());
        }
    }

    public void onAddNewPerspective() {
        view.openPopUp(getSaveCommand());
    }

    public void onEditPerspective() throws SerializationException {
        String selectedPerspectiveUuid = view.getSelectedPerspectiveUuid();
        if (selectedPerspectiveUuid == null) {
            view.showNoSelectedPerspectiveError();
        } else {
            loadPerspective(selectedPerspectiveUuid);
        }
    }

    public void onRemovePerspective() {
        String selectedPerspectiveUuid = view.getSelectedPerspectiveUuid();
        if (selectedPerspectiveUuid == null) {
            view.showNoSelectedPerspectiveError();
        } else {
            deletePerspective(selectedPerspectiveUuid);
        }
    }

    private void savePerspective(final IFramePerspectiveConfiguration iFramePerspectiveConfiguration) {
        configurationServiceAsync.save(iFramePerspectiveConfiguration, new GenericCallback<String>() {
            public void onSuccess(String uuid) {
                if (isAnUpdate(iFramePerspectiveConfiguration)) {
                    view.removePerspective(uuid);
                }
                view.addPerspective(uuid, iFramePerspectiveConfiguration.getName());
            }
        });
    }

    private boolean isAnUpdate(IFramePerspectiveConfiguration iFramePerspectiveConfiguration) {
        return iFramePerspectiveConfiguration.getUuid() != null;
    }

    private void loadPerspective(String selectedPerspectiveUuid) throws SerializationException {
        configurationServiceAsync.load(selectedPerspectiveUuid, new GenericCallback<IFramePerspectiveConfiguration>() {
            public void onSuccess(IFramePerspectiveConfiguration result) {
                view.openPopUp(getSaveCommand(), result);
            }
        });
    }

    private void deletePerspective(final String selectedPerspectiveUuid) {
        configurationServiceAsync.remove(selectedPerspectiveUuid, new GenericCallback<Void>() {
            public void onSuccess(Void result) {
                view.removePerspective(selectedPerspectiveUuid);
            }
        });
    }

    private SaveCommand<IFramePerspectiveConfiguration> getSaveCommand() {
        return new SaveCommand<IFramePerspectiveConfiguration>() {
            public void save(IFramePerspectiveConfiguration iFramePerspectiveConfiguration) {
                if (perspectivesListContainsName(iFramePerspectiveConfiguration.getName())) {
                    view.showNameTakenError(iFramePerspectiveConfiguration.getName());
                } else {
                    savePerspective(iFramePerspectiveConfiguration);
                    view.closePopUp();
                }
            }
        };
    }

    private boolean perspectivesListContainsName(String name) {
        return view.getListOfPerspectiveNames().contains(name);
    }
}
