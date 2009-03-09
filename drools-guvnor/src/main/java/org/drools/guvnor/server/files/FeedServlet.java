package org.drools.guvnor.server.files;

import org.drools.guvnor.server.security.PackageNameType;
import org.drools.guvnor.server.security.RoleTypes;
import org.drools.guvnor.server.security.CategoryPathType;
import org.drools.repository.AssetItem;
import org.drools.repository.PackageItem;
import org.drools.repository.AssetPageList;
import org.drools.util.StringUtils;
import org.jboss.seam.contexts.Contexts;
import org.jboss.seam.security.Identity;
import org.apache.jackrabbit.util.ISO8601;
import org.mvel2.templates.TemplateRuntime;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.ServletException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;

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

    /**
 * @author Michael Neale
     */
    public static class AtomFeed {

        private static String TEMPLATE = StringUtils.readFileAsString(new InputStreamReader(AtomFeed.class.getResourceAsStream("/atom-feed-template.xml")));


        private String feedTitle;
        private String feedUpdated;
        private String feedId;
        private String feedAlternate;
        private String feedSelf;
        private String subtitle;
        private Collection<AtomEntry> entries;

        public AtomFeed(String title, Calendar whenUpdate, String feedId, String feedAlternate, String feedSelf, Collection<AtomEntry> entries, String subtitle) {
            this.feedTitle = title;
            this.feedUpdated = ISO8601.format(whenUpdate);
            this.feedId = feedId;
            this.feedAlternate = feedAlternate;
            this.feedSelf = feedSelf;
            this.entries = entries;
            this.subtitle = subtitle;
        }

        public String getAtom() {
            Map m = new HashMap();
            m.put("feed", this);
            return (String) TemplateRuntime.eval(TEMPLATE, m);
        }

        public String getSubtitle() {
            return subtitle;
        }


        public Collection getEntries() { return entries; }

        public String getFeedTitle() { return feedTitle; }

        public String getFeedUpdated() {
            return feedUpdated;
        }

        public String getFeedId() {
            return feedId;
        }

        public String getFeedAlternate() {
            return feedAlternate;
        }

        public String getFeedSelf() {
            return feedSelf;
        }


        public static class AtomEntry {
            private String name;
            private String webURL;
            private String id;
            private String updated;
            private String published;
            private String author;
            private String contributor;
            private String description;
            private String checkinComment;
            private String format;

            public AtomEntry(HttpServletRequest req, AssetItem asset) {
                this.name = asset.getName();
                this.format = asset.getFormat();
                this.webURL = req.getParameter("viewUrl") + "#asset=" + asset.getUUID() + "&nochrome";
                this.id = asset.getUUID() + "&version=" + asset.getVersionNumber();
                this.updated = ISO8601.format(asset.getLastModified());
                this.published = ISO8601.format(asset.getCreatedDate());
                this.author = asset.getCreator();
                this.contributor = asset.getLastContributor();
                this.description = asset.getDescription();
                this.checkinComment = asset.getCheckinComment();
            }

            public String getName() {
                return name;
            }

            public String getFormat() {
                return format;
            }

            public String getWebURL() {
                return webURL;
            }


            public String getId() {
                return id;
            }

            public String getUpdated() {
                return updated;
            }

            public String getPublished() {
                return published;
            }

            public String getAuthor() {
                return author;
            }

            public String getContributor() {
                return contributor;
            }

            public String getDescription() {
                return description;
            }

            public String getCheckinComment() {
                return checkinComment;
            }

        }



    }
}
