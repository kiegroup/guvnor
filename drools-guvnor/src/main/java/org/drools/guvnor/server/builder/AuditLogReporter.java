package org.drools.guvnor.server.builder;

import java.util.ArrayList;
import java.util.List;

import org.drools.WorkingMemory;
import org.drools.audit.WorkingMemoryInMemoryLogger;
import org.drools.audit.event.ActivationLogEvent;
import org.drools.audit.event.LogEvent;

/**
 * Add a little web specific reporting functionality to the audit logger.
 *
 */
public class AuditLogReporter extends WorkingMemoryInMemoryLogger {

	public AuditLogReporter(WorkingMemory wm) {
		super(wm);
	}
	
	public List<String[]> buildReport() {
		List<LogEvent> evs = this.getLogEvents();
		int resultSize = Math.min(1000, evs.size());
		List<String[]> ls = new ArrayList<String[]>(resultSize);
		for (int i = 0; i < resultSize; i++) {
			mapLogEvent(ls, evs.get(i));
		}
		return ls;
	}

	private void mapLogEvent(List<String[]> ls, LogEvent logEvent) {
		switch (logEvent.getType()) {
		case LogEvent.ACTIVATION_CANCELLED:
		case LogEvent.ACTIVATION_CREATED:
			break;
		case LogEvent.BEFORE_ACTIVATION_FIRE:
			ActivationLogEvent ae = (ActivationLogEvent) logEvent;
			String msg = "FIRING rule: [" + ae.getRule() + "] activationId:" + ae.getActivationId() + " declarations: " + ae.getDeclarations() + (ae.getRuleFlowGroup() == null ? "" : " ruleflow-group: " + ae.getRuleFlowGroup());
			ls.add(new String[] {Integer.toString(logEvent.getType()), msg});
			break;
		default:
			ls.add(new String[] {Integer.toString(logEvent.getType()), logEvent.toString()});
			break;
		}
		
	}
}
