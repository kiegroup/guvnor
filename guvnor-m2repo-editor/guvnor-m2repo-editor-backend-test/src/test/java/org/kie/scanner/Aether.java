package org.kie.scanner;

import java.util.ArrayList;
import java.util.Collection;

import org.eclipse.aether.RepositorySystem;
import org.eclipse.aether.RepositorySystemSession;
import org.eclipse.aether.repository.ArtifactRepository;

public class Aether {

	private static Aether instance;

	public static Aether getAether() {
		if (instance == null) {
			instance = new Aether();
		}
		return instance;
	}

	private RepositorySystem system;
	private RepositorySystemSession session;
	private Collection<ArtifactRepository> repositories = new ArrayList<ArtifactRepository>();

	public RepositorySystem getSystem() {
		return system;
	}

	public RepositorySystemSession getSession() {
		return session;
	}

	public void setSystem(RepositorySystem system) {
		this.system = system;
	}

	public void setSession(RepositorySystemSession session) {
		this.session = session;
	}

	public Collection<ArtifactRepository> getRepositories() {
		return repositories;
	}

}
