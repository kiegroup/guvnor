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

import java.util.HashMap;
import java.util.Map;

import com.google.gwt.user.client.Command;
import org.guvnor.structure.repositories.Repository;

public class UpdateRepositoryCmd implements Command {

    private Repository repository;
    private RepositoriesPresenter presenter;
    private Map<String, Object> data = new HashMap<String, Object>();

    public UpdateRepositoryCmd(Repository repository, RepositoriesPresenter presenter) {
        this.repository = repository;
        this.presenter = presenter;
    }

    public void add(String name, Object value) {
        this.data.put(name, value);
    }

    @Override
    public void execute() {
        presenter.updateRepository(repository, data);
    }
}
