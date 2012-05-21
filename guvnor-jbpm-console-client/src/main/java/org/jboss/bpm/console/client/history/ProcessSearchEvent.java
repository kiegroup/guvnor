/**
 * 
 */
package org.jboss.bpm.console.client.history;

/**
 * @author Jeff Yu
 * @date Mar 17, 2011
 */
public class ProcessSearchEvent {
	
	private String definitionKey;
	
	private String key;
	
	private String status;
	
	private long startTime;
	
	private long endTime;

	public String getDefinitionKey() {
		return definitionKey;
	}

	public void setDefinitionKey(String definitionKey) {
		this.definitionKey = definitionKey;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public long getStartTime() {
		return startTime;
	}

	public void setStartTime(long startTime) {
		this.startTime = startTime;
	}

	public long getEndTime() {
		return endTime;
	}

	public void setEndTime(long endTime) {
		this.endTime = endTime;
	}
	
	

}
