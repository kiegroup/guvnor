package org.drools.repository.security;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.Property;
import javax.jcr.PropertyIterator;
import javax.jcr.RepositoryException;
import javax.jcr.Value;

import org.drools.repository.RulesRepository;

/**
 * Deals with storing permissions for data and so on.
 * @author Michael Neale
 */
public class PermissionManager {

    private RulesRepository repository;

    public PermissionManager(RulesRepository repo) {
        this.repository = repo;
    }

    /**
     * Save the users permissions.
     * @param userName = obviously, the user name.
     * @param perms - a map of the role type to the targets that it applies to.
     * eg: package.admin => PACKAGE UUID
     * analyst => category path
     * ADMIN => empty (no list needed for admin)
     * @throws RepositoryException
     */
    public void updateUserPermissions(String userName, Map<String, List<String>> perms) throws RepositoryException {
    	Node permsNode = getUserPermissionNode(userName);
    	for (Iterator<String> iterator = perms.keySet().iterator(); iterator.hasNext();) {
			String perm = iterator.next();
			List<String> targets = perms.get(perm);
			if (targets == null) targets = new ArrayList<String>();
			permsNode.setProperty(perm, targets.toArray(new String[targets.size()]));
		}
    	this.repository.save();
    }

	private Node getUserPermissionNode(String userName)
			throws RepositoryException {
		Node root = this.repository.getSession().getRootNode();
    	Node permsNode = getNode(getNode(getNode(root, "user_info"), userName), "permissions");
		return permsNode;
	}


    /**
     * obtain a mapping of permissions for a given user.
     * @throws RepositoryException
     */
    public Map<String, List<String>> retrieveUserPermissions(String userName) throws RepositoryException {
    	Node permsNode = getUserPermissionNode(userName);
    	PropertyIterator it = permsNode.getProperties();
    	Map<String, List<String>> result = new HashMap<String, List<String>>(10);
    	while (it.hasNext()) {
    		Property p = (Property) it.next();
    		String name = p.getName();
    		if (!name.startsWith("jcr")) {
	    		Value[] vs = p.getValues();
	    		List<String> perms = new ArrayList<String>();
	    		for (int i = 0; i < vs.length; i++) {
					perms.add(vs[i].getString());
				}
	    		result.put(name, perms);
    		}
    	}
    	return result;
    }

    /**
     * Gets or creates a node.
     */
	private Node getNode(Node node, String name) throws RepositoryException {
		Node permsNode;
		if (!node.hasNode(name)) {
    		permsNode = node.addNode(name);
    	} else {
    		permsNode = node.getNode(name);
    	}
		return permsNode;
	}






}
