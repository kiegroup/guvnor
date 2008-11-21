package org.drools.repository;

public class Phase {   
	private String name;
	private String description;
	private boolean isInitialPhase;
	private String nextPhase;
	
	public boolean isInitialPhase() {
		return isInitialPhase;
	}

	public void setInitialPhase(boolean isInitialPhase) {
		this.isInitialPhase = isInitialPhase;
	}

	public String getNextPhase() {
		return nextPhase;
	}

	public void setNextPhase(String nextPhase) {
		this.nextPhase = nextPhase;
	}

	public String getName() {
		return name;
	}	
	
	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
	
}
