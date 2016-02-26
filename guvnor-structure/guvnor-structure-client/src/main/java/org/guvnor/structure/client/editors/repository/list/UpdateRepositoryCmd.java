/*
 * Copyright 2015 JBoss Inc
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
import org.guvnor.structure.client.editors.context.GuvnorStructureContext;
import org.guvnor.structure.repositories.Repository;

public class UpdateRepositoryCmd implements Command {

    private Repository repository;
    private String branch;
    private GuvnorStructureContext context;

    public UpdateRepositoryCmd( Repository repository, GuvnorStructureContext context ) {
        this.repository = repository;
        this.context = context;
    }

    public void setBranch( final String branch ) {
        this.branch = branch;
    }

    @Override
    public void execute() {
        context.changeBranch( repository.getAlias(),
                              branch);
    }
}
