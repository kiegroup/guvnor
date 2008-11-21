package org.drools.repository;

import java.util.ArrayList;
import java.util.List;

public class LifeCycle {   
	private String name;
	private String description;
	private List<Phase> phases = new ArrayList<Phase>();	
	
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

	public List<Phase> getPhases() {
		return phases;
	}

	public void setPhases(List<Phase> phases) {
		this.phases = phases;
	}
	
	public void addPhase(Phase phase) {
		phases.add(phase);
	}
	
}
