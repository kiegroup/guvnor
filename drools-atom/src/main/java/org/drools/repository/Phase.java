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
