package org.drools.repository;


public interface RepositoryFilter {
	boolean accept(Object artifact, String action);
}
