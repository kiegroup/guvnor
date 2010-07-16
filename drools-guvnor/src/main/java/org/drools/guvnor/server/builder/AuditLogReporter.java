/**
 * Copyright 2010 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
