package org.drools.repository;

import org.drools.repository.security.PermissionManager;
import static org.drools.repository.security.PermissionManager.getNode;
import static org.drools.repository.security.PermissionManager.getUserInfoNode;

import javax.jcr.Value;
import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.NodeIterator;
import javax.jcr.InvalidItemStateException;
import javax.jcr.AccessDeniedException;
import javax.jcr.ItemExistsException;
import javax.jcr.version.VersionException;
import javax.jcr.nodetype.ConstraintViolationException;
import javax.jcr.nodetype.NoSuchNodeTypeException;
import javax.jcr.lock.LockException;

/**
 * Manage access to misc. user info that we might want to store. 
 * @author Michael Neale
 */
public class UserInfo {
    Node userInfoNode;

    /**
     * Use the current sessions userName to get to the info node.
     */
    public UserInfo(RulesRepository repo) throws RepositoryException {
        init(repo, repo.getSession().getUserID());
    }


    UserInfo() {}

    /**
     * Use the given userName to select the node.
     */
    public UserInfo(RulesRepository repo, String userName) throws RepositoryException {
        init(repo, userName);
    }

    void init(RulesRepository repo, String userName) throws RepositoryException {
        this.userInfoNode = getUserInfoNode(userName, repo);
    }

    public void setProperty(String fileName, String propertyName, Val value) throws RepositoryException {
        Node inboxNode = getNode(userInfoNode, fileName, "nt:file");
        if (inboxNode.hasNode("jcr:content")) {
            inboxNode.getNode("jcr:content").setProperty(propertyName, value.value);
        } else {
            inboxNode.addNode("jcr:content", "nt:unstructured").setProperty(propertyName, value.value);
        }
    }

    public Val getProperty(String fileName, String propertyName) throws RepositoryException {
        Node inboxNode = getNode(userInfoNode, fileName, "nt:file");

        if (inboxNode.hasNode("jcr:content")) {
            if (inboxNode.getNode("jcr:content").hasProperty(propertyName)) {
                return new Val(inboxNode.getNode("jcr:content").getProperty(propertyName).getString());
            } else {
                return new Val("");
            }
        } else {
            inboxNode.addNode("jcr:content", "nt:unstructured"); //needed to make it consistent on save
            return new Val("");
        }

    }


    public static class Val {
        public String value;
        public Val(String s) {
            this.value = s;
        }
    }

    /**
     * Do something for each user.
     * @param c
     */
    public static void eachUser(RulesRepository repository, Command c) throws RepositoryException {
        NodeIterator nit = PermissionManager.getUsersRootNode(PermissionManager.getRootNode(repository)).getNodes();
        while (nit.hasNext()) {
            c.process(nit.nextNode());
        }
    }


    public static interface Command {
        public void process(Node userNode) throws RepositoryException;
    }


    /**
     * Persists the change (if not in a transaction of course, if in a transaction, it will wait until the boundary is hit,
     * as per JCR standard.
     * @throws RepositoryException
     */
    public void save() throws RepositoryException {
         userInfoNode.getParent().getParent().save();
         //userInfoNode.getParent().save();
    }

}
