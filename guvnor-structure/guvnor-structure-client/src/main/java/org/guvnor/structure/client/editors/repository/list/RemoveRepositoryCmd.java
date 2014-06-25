package org.guvnor.structure.client.editors.repository.list;

import com.google.gwt.user.client.Command;
import org.guvnor.structure.repositories.Repository;

public class RemoveRepositoryCmd implements Command {

    private Repository repository;
    private RepositoriesPresenter presenter;

    public RemoveRepositoryCmd(Repository repository, RepositoriesPresenter presenter) {
        this.repository = repository;
        this.presenter = presenter;
    }

    @Override
    public void execute() {
        presenter.removeRepository(repository);
    }
}
