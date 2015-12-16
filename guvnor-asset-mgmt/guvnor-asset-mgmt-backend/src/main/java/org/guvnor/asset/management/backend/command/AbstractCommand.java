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

package org.guvnor.asset.management.backend.command;

import org.kie.api.runtime.process.WorkItem;
import org.kie.api.executor.Command;
import org.kie.api.executor.CommandContext;

public abstract class AbstractCommand implements Command {

	protected Object getParameter(CommandContext commandContext, String parameterName) {
		if (commandContext.getData(parameterName) != null) {
			return commandContext.getData(parameterName);
		}
		WorkItem workItem = (WorkItem) commandContext.getData("workItem");
		if (workItem != null) {
			return workItem.getParameter(parameterName);
		}
		return null;
	}

}
