package org.drools.guvnor.server.files;

import junit.framework.TestCase;
import org.drools.repository.RulesRepository;
import org.drools.repository.PackageItem;
import org.drools.repository.AssetItem;
import org.drools.guvnor.server.util.TestEnvironmentSessionHelper;
import org.drools.guvnor.server.ServiceImplementation;
import org.apache.util.Base64;
import org.jboss.seam.security.AuthorizationException;

import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.util.Map;
import java.util.HashMap;

/**
 * @author Michael Neale
 */
public class FeedServletTest extends TestCase {

    public void testPackageFeed() throws Exception {
        RulesRepository repo = new RulesRepository( TestEnvironmentSessionHelper.getSession( true ) );
        PackageItem pkg = repo.createPackage("testPackageFeed", "");
        AssetItem asset = pkg.addAsset("asset1", "desc");
        asset.updateFormat("drl");
        asset.checkin("");

        Map<String, String> headers = new HashMap<String, String>() {
            {
                put("Irrelevant", "garbage");
            }
        };

        MockHTTPRequest req = new MockHTTPRequest("/org.foo/feed/package?name=...", headers);
        FeedServlet fs = new FeedServlet();
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        MockHTTPResponse res = new MockHTTPResponse(out);
        fs.doGet(req, res);
        assertEquals(HttpServletResponse.SC_UNAUTHORIZED, res.errorCode);

        //try again with bad password
        headers = new HashMap<String, String>() {
            {
                put("Authorization", "BASIC " + new String(Base64.encode("test:password".getBytes())));
            }
        };
        req = new MockHTTPRequest("/org.foo/feed/package", headers, new HashMap<String, String>() {
            {
                put("name", "testPackageFeed");
                put("viewUrl", "http://foo.bar");
            }
        });
        fs = new FeedServlet();
        out = new ByteArrayOutputStream();
        res = new MockHTTPResponse(out);
        fs.doGet(req, res);


        String r = new String(out.toByteArray());
        assertNotNull(r);

        assertTrue(r.indexOf("asset1") > -1);


        req = new MockHTTPRequest("/org.foo/feed/package", headers, new HashMap<String, String>() {
            {
                put("name", "testPackageFeed");
                put("viewUrl", "http://foo.bar");
                put("status", "Foo");
            }
        });
        fs = new FeedServlet();
        out = new ByteArrayOutputStream();
        res = new MockHTTPResponse(out);
        fs.doGet(req, res);

        r = new String(out.toByteArray());
        assertNotNull(r);

        assertFalse(r.indexOf("asset1.drl") > -1);

        req = new MockHTTPRequest("/org.foo/feed/package", headers, new HashMap<String, String>() {
            {
                put("name", "testPackageFeed");
                put("viewUrl", "http://foo.bar");
                put("status", "Draft");
            }
        });
        fs = new FeedServlet();
        out = new ByteArrayOutputStream();
        res = new MockHTTPResponse(out);
        fs.doGet(req, res);

        r = new String(out.toByteArray());
        assertNotNull(r);
        assertTrue(r.indexOf("asset1") > -1);

    }

    public void testCategoryFeed() throws Exception {
        RulesRepository repo = new RulesRepository( TestEnvironmentSessionHelper.getSession( true ) );
        PackageItem pkg = repo.createPackage("testCategoryFeed", "");
        repo.loadCategory("/").addCategory("testCategoryFeedCat", "");
        AssetItem asset = pkg.addAsset("asset1", "desc");
        asset.updateFormat("drl");
        asset.updateCategoryList(new String[] {"testCategoryFeedCat"});
        asset.checkin("");


        //try again with bad password
        HashMap<String, String> headers = new HashMap<String, String>() {
            {
                put("Authorization", "BASIC " + new String(Base64.encode("test:password".getBytes())));
            }
        };
        MockHTTPRequest req = new MockHTTPRequest("/org.foo/feed/category", headers, new HashMap<String, String>() {
            {
                put("name", "testCategoryFeedCat");
                put("viewUrl", "http://foo.bar");
            }
        });
        MockFeedServlet fs = new MockFeedServlet();
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        MockHTTPResponse res = new MockHTTPResponse(out);
        fs.doGet(req, res);

        String r = new String(out.toByteArray());
        assertNotNull(r);

        assertTrue(r.indexOf("asset1") > -1);
        assertTrue(r.indexOf("http://foo.bar") > -1);


        req = new MockHTTPRequest("/org.foo/feed/category", headers, new HashMap<String, String>() {
            {
                put("name", "testCategoryFeedCat");
                put("viewUrl", "http://foo.bar");
                put("status", "*");
            }
        });
        fs = new MockFeedServlet();
        out = new ByteArrayOutputStream();
        res = new MockHTTPResponse(out);
        fs.doGet(req, res);

        r = new String(out.toByteArray());
        assertNotNull(r);

        assertTrue(r.indexOf("asset1") > -1);
        assertTrue(r.indexOf("http://foo.bar") > -1);


        fs = new MockFeedServlet();
        fs.throwAuthException = true;
        out = new ByteArrayOutputStream();
        res = new MockHTTPResponse(out);
        fs.doGet(req, res);

        assertEquals(HttpServletResponse.SC_UNAUTHORIZED, res.errorCode);


    }



    public void testDiscussionFeed() throws Exception {
        RulesRepository repo = new RulesRepository( TestEnvironmentSessionHelper.getSession( true ) );
        PackageItem pkg = repo.createPackage("testDiscussionFeed", "");
        AssetItem asset = pkg.addAsset("asset1", "desc");
        asset.updateFormat("drl");
        asset.checkin("");

        ServiceImplementation impl = new ServiceImplementation();
        impl.repository = repo;
        impl.addToDiscussionForAsset(asset.getUUID(), "This is a comment");
        impl.addToDiscussionForAsset(asset.getUUID(), "This is another comment");

        Map<String, String> headers = new HashMap<String, String>() {
            {
                put("Irrelevant", "garbage");
            }
        };

        MockHTTPRequest req = new MockHTTPRequest("/org.foo/feed/discussion?package=...", headers);
        FeedServlet fs = new FeedServlet();
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        MockHTTPResponse res = new MockHTTPResponse(out);
        fs.doGet(req, res);
        assertEquals(HttpServletResponse.SC_UNAUTHORIZED, res.errorCode);

        headers = new HashMap<String, String>() {
            {
                put("Authorization", "BASIC " + new String(Base64.encode("test:password".getBytes())));
            }
        };


        req = new MockHTTPRequest("/org.foo/feed/discussion", headers, new HashMap<String, String>() {
            {
                put("package", "testDiscussionFeed");
                put("assetName", "asset1");
            }
        });
        fs = new FeedServlet();
        out = new ByteArrayOutputStream();
        res = new MockHTTPResponse(out);
        fs.doGet(req, res);

        String r = new String(out.toByteArray());
        assertNotNull(r);
        assertTrue(r.indexOf("This is a comment") > -1);
        assertTrue(r.indexOf("This is another comment") > r.indexOf("This is a comment"));
        System.err.println(r);



    }


    class MockFeedServlet extends FeedServlet {
        boolean throwAuthException = false;
        @Override
        void checkCategoryPermission(String cat) {
            if (throwAuthException) throw new AuthorizationException("NO");
            super.checkCategoryPermission(cat);    //To change body of overridden methods use File | Settings | File Templates.
        }

        @Override
        void checkPackageReadPermission(String packageName) {
            if (throwAuthException) throw new AuthorizationException("NO");
            super.checkPackageReadPermission(packageName);    //To change body of overridden methods use File | Settings | File Templates.
        }
    }
    
}
