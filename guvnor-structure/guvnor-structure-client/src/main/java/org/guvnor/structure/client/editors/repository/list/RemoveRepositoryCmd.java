/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/

package org.guvnor.structure.client.editors.repository.list;

import com.google.gwt.user.client.Command;
import org.guvnor.structure.repositories.Repository;

public class RemoveRepositoryCmd implements Command {

    private Repository repository;
    private RepositoriesPresenter presenter;

    public RemoveRepositoryCmd(Repository repository,
                               RepositoriesPresenter presenter) {
        this.repository = repository;
        this.presenter = presenter;
    }

    @Override
    public void execute() {
        presenter.removeRepository(repository);
    }
}
