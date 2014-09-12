package org.guvnor.asset.management.backend.command;

import org.kie.api.runtime.process.WorkItem;
import org.kie.internal.executor.api.Command;
import org.kie.internal.executor.api.CommandContext;

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
