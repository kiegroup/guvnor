/**
 * 
 */
package org.drools.guvnor.client.modeldriven;

import java.util.Collections;
import java.util.Set;

import org.drools.guvnor.client.modeldriven.FactTypeFilter;

public class SetFactTypeFilter implements FactTypeFilter {

	private static final long serialVersionUID = 501L;
	private final Set<String> validFacts;

	public SetFactTypeFilter() {
		validFacts = Collections.emptySet();
	}
	
	public SetFactTypeFilter(Set<String> validFacts) {
		this.validFacts = validFacts;
	}

	public boolean filter(String originalFact) {
	    return !validFacts.contains(originalFact);
	}
}