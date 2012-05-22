/**
 * 
 */
package org.jboss.bpm.console.server.plugin;

import java.util.List;
import java.util.Set;

import org.jboss.bpm.console.client.model.HistoryProcessInstanceRef;
import org.jboss.bpm.console.client.model.ProcessDefinitionRef;

/**
 * @author Jeff Yu
 * @date Mar 17, 2011
 */
public interface ProcessHistoryPlugin {
	
	List<HistoryProcessInstanceRef> getHistoryProcessInstances(String definitionkey, String status,
																long starttime, long endtime, String correlationkey);

    List<ProcessDefinitionRef> getProcessDefinitions();

    List<String> getProcessInstanceKeys(String definitionId);

    List<String> getActivityKeys(String instanceId);

    List<String> getAllEvents(String instanceId);

    Set<String> getCompletedInstances(String definitionId, long timestamp, String timespan);

    Set<String> getFailedInstances(String definitionId, long timestamp, String timespan);

    Set<String> getTerminatedInstances(String definitionId, long timestamp, String timespan);

    String getCompletedInstances4Chart(String definitionId, String timespan);

    String getFailedInstances4Chart(String definitionId, String timespan);

	
}
