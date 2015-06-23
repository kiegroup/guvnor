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

package org.guvnor.asset.management.backend.command;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.guvnor.asset.management.backend.AssetManagementRuntimeException;
import org.kie.internal.executor.api.Command;
import org.kie.internal.executor.api.CommandContext;
import org.kie.internal.executor.api.ExecutionResults;


public class CompositeCommand extends AbstractCommand {

    @Override
    public ExecutionResults execute(CommandContext commandContext) throws Exception {
        try {
            String commands = (String) getParameter(commandContext, "Commands");
            ClassLoader cl = (ClassLoader) getParameter(commandContext, "ClassLoader");
            if (cl == null) {
                cl = this.getClass().getClassLoader();
            }
            String[] commandsList = commands.split(",");
            ExecutionResults results = new ExecutionResults();
            Map<String, Object> data = new HashMap<String, Object>();
            for (String cmd : commandsList) {
                Class<?> forName = Class.forName(cmd.trim(), true, cl);
                Command newInstance = (Command) forName.newInstance();
                ExecutionResults execute = newInstance.execute(commandContext);
                Set<String> keySet = execute.keySet();
                for (String key : keySet) {
                    data.put(key, execute.getData(key));
                    //I'm adding the results as part of the context for the next commands execution
                    commandContext.getData().put(key, execute.getData(key));
                }
            }
            results.setData(data);
            return results;
        } catch (Throwable e) {
            throw new AssetManagementRuntimeException(e);
        }

    }

}
