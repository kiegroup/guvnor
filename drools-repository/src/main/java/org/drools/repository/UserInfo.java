package org.drools.repository;

import org.drools.repository.security.PermissionManager;
import static org.drools.repository.security.PermissionManager.getNode;
import static org.drools.repository.security.PermissionManager.getUserInfoNode;

import javax.jcr.Value;
import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.NodeIterator;

/**
 * Manage access to misc. user info that we might want to store. 
 * @author Michael Neale
 */
public class UserInfo {
    Node userInfoNode;
    //private RulesRepository repository;
    //String userName;

    public UserInfo(RulesRepository repo) throws RepositoryException {
        init(repo, repo.getSession().getUserID());
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
            return new Val(inboxNode.getNode("jcr:content").getProperty(propertyName).getString());
        } else {
            return new Val("");
        }

    }


    public static class Val {
        String value;
        public Val(String s) {
            this.value = s;
        }
    }

    /**
     * Do something for each user.
     * @param c
     */
    public void eachUser(RulesRepository repository, Command c) throws RepositoryException {
        NodeIterator nit = PermissionManager.getUsersRootNode(PermissionManager.getRootNode(repository)).getNodes();
        while (nit.hasNext()) {
            c.process(nit.nextNode());
        }
    }


    public static interface Command {
        public void process(Node userNode) throws RepositoryException;
    }

}
