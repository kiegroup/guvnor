package org.drools.repository;

import junit.framework.TestCase;
import org.drools.repository.security.PermissionManager;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;

/**
 * @author Michael Neale
 */
public class UserInfoTest extends TestCase {
    public void testPersistence() throws Exception {
        RulesRepository repo = RepositorySessionUtil.getRepository();
        PermissionManager pm = new PermissionManager(repo);
        Map<String, List<String>> perms = new HashMap<String, List<String>>() {{
            put("package.admin", new ArrayList<String>() {{add("1234567890");}});
            put("package.developer", new ArrayList<String>() {{add("1"); add("2");}});
            put("analyst", new ArrayList<String>() {{add("HR");}});
            put("admin", new ArrayList<String>());
        }};
        pm.updateUserPermissions("wankle", perms);
        Map<String, List<String>> perms_ = pm.retrieveUserPermissions("wankle");


        UserInfo info = new UserInfo(repo);
        info.setProperty("inbox", "something", new UserInfo.Val("boo"));

        assertEquals("boo", info.getProperty("inbox", "something").value);
        info.setProperty("inbox", "something", new UserInfo.Val("boo2"));

        assertEquals("boo2", info.getProperty("inbox", "something").value);

        info.setProperty("inbox", "another", new UserInfo.Val("boo"));
        assertEquals("boo", info.getProperty("inbox", "another").value);


        info = new UserInfo(repo);
        info.init(repo, "wankle");
        info.setProperty("inbox", "something", new UserInfo.Val("boo"));

        assertEquals("boo", info.getProperty("inbox", "something").value);
        info.setProperty("inbox", "something", new UserInfo.Val("boo2"));

        assertEquals("boo2", info.getProperty("inbox", "something").value);

        info.setProperty("inbox", "another", new UserInfo.Val("boo"));
        assertEquals("boo", info.getProperty("inbox", "another").value);

        assertEquals(perms_.size(), pm.retrieveUserPermissions("wankle").size());


        pm.updateUserPermissions("wankle", perms_);
        info = new UserInfo(repo);
        assertEquals("boo", info.getProperty("inbox", "another").value);

        pm.removeUserPermissions("wankle");
        info = new UserInfo(repo);
        assertEquals("boo", info.getProperty("inbox", "another").value);
        

        info = new UserInfo(repo);
        info.init(repo, "meee");
        info.setProperty("inbox", "whee", new UserInfo.Val("boo"));

        assertEquals("boo", info.getProperty("inbox", "whee").value);


        info = new UserInfo(repo);
        assertEquals("boo", info.getProperty("inbox", "another").value);




    }

    public void testIterateOverUsers() throws Exception {
        RulesRepository repo = RepositorySessionUtil.getRepository();



        final List<String> names = new ArrayList<String>();

        UserInfo uf = new UserInfo(repo);
        uf.init(repo, "michael");
        uf.setProperty("random", "property", new UserInfo.Val("hi"));
        uf.eachUser(repo, new UserInfo.Command() {
            public void process(Node userNode) throws RepositoryException {
                names.add(userNode.getName());
            }
        });

        assertTrue(names.size() > 0) ;
        assertTrue(names.contains("michael"));


    }

}
