package org.drools.guvnor.server.files;

import org.drools.guvnor.server.ServiceImplementation;
import org.drools.guvnor.server.security.PackageNameType;
import org.drools.guvnor.server.security.RoleTypes;
import org.drools.guvnor.server.security.CategoryPathType;
import org.drools.repository.AssetItem;
import org.drools.repository.PackageItem;
import org.drools.repository.AssetPageList;
import org.drools.repository.RulesRepository;
import org.jboss.seam.contexts.Contexts;
import org.jboss.seam.security.Identity;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.ServletException;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;
import java.util.Calendar;

/**
 * @author Michael Neale
 */
public class FeedServlet extends RepositoryServlet {

    @Override
    protected void doGet(final HttpServletRequest request, final HttpServletResponse response) throws ServletException, IOException {
        String url = request.getRequestURI();
        if (url.indexOf("feed/package") > -1) {
            doAuthorizedAction(request, response, new A() {
                public void a() throws Exception {
                    doPackageFeed(request, response);
                }
            });
        } else if (url.indexOf("feed/category") > -1) {
            doAuthorizedAction(request, response, new A() {
                public void a() throws Exception {
                    doCategoryFeed(request, response);
                }
            });
        } 
    }



    private void doCategoryFeed(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String cat = request.getParameter("name");
        String status = request.getParameter("status");
        checkCategoryPermission(cat);
        AssetPageList pg = getFileManager().repository.findAssetsByCategory(cat, false, 0, -1);
        Iterator<AssetItem> it = pg.assets.iterator();
        List<AtomFeed.AtomEntry> entries = new ArrayList<AtomFeed.AtomEntry>();
        buildEntries(request, entries, it, status);
        AtomFeed feed = new AtomFeed("Category: " + cat, Calendar.getInstance(), request.getServerName() + cat, request.getServletPath(), request.getRequestURI(), entries, "Guvnor category of items: " + cat);
        response.setContentType("application/atom+xml");
        response.getOutputStream().print(feed.getAtom());
    }

    private void checkCategoryPermission(String cat) {
        if ( Contexts.isSessionContextActive() ) {
            Identity.instance().checkPermission(  new CategoryPathType( cat ),
                                                 RoleTypes.ANALYST_READ );
        }
    }

    private void doPackageFeed(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String packageName = request.getParameter("name");
        checkPackageReadPermission(packageName);

        PackageItem pkg = getFileManager().repository.loadPackage(packageName);

        List<AtomFeed.AtomEntry> entries = new ArrayList<AtomFeed.AtomEntry>();
        Iterator<AssetItem> it = pkg.getAssets();
        buildEntries(request, entries, it, request.getParameter("status"));

        AtomFeed feed = new AtomFeed("Knowledge package: " + pkg.getName(), pkg.getLastModified(), pkg.getUUID(), request.getServletPath(), request.getRequestURI(), entries, pkg.getDescription());
        response.setContentType("application/atom+xml");
        response.getOutputStream().print(feed.getAtom());
    }

    private void buildEntries(HttpServletRequest request, List<AtomFeed.AtomEntry> entries, Iterator<AssetItem> it, String status) {
        while(it.hasNext()) {
            AssetItem as = it.next();
            if (!as.isArchived() && !as.getDisabled())  {
                if (status == null || status.equals("*") || as.getStateDescription().equals(status)) {
                    entries.add(new AtomFeed.AtomEntry(request, as));
                }
            }
        }
    }


    private void checkPackageReadPermission(String packageName) {
        if ( Contexts.isSessionContextActive() ) {
            Identity.instance().checkPermission(  new PackageNameType( packageName ),
                                                 RoleTypes.PACKAGE_READONLY);
        }
    }
}
