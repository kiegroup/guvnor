package org.drools.guvnor.client.admin;

import com.google.gwt.user.client.rpc.SerializationException;
import com.google.gwt.user.client.ui.IsWidget;
import org.drools.guvnor.client.rpc.IFramePerspectiveConfiguration;
import org.drools.guvnor.client.util.SaveCommand;

import java.util.Collection;

public interface PerspectivesManagerView extends IsWidget{

    interface Presenter {

        void onAddNewPerspective();

        void onEditPerspective() throws SerializationException;

        void onRemovePerspective();

    }

    void setPresenter(Presenter presenter);

    String getSelectedPerspectiveUuid();

    void addPerspective(String uuid, String name);

    void openPopUp(SaveCommand<IFramePerspectiveConfiguration> saveCommand);

    void openPopUp(SaveCommand<IFramePerspectiveConfiguration> capture, IFramePerspectiveConfiguration iFramePerspectiveConfiguration);

    void closePopUp();

    void removePerspective(String uuid);

    Collection<String> getListOfPerspectiveNames();

    void showNameTakenError(String name);

    void showNoSelectedPerspectiveError();
}
