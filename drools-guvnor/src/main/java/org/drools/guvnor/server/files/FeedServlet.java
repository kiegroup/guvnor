package org.drools.guvnor.server.files;

import org.drools.guvnor.server.security.PackageNameType;
import org.drools.guvnor.server.security.RoleTypes;
import org.drools.guvnor.server.security.CategoryPathType;
import org.drools.guvnor.server.util.Discussion;
import org.drools.guvnor.client.rpc.DiscussionRecord;
import org.drools.repository.AssetItem;
import org.drools.repository.PackageItem;
import org.drools.repository.AssetPageList;
import org.drools.core.util.StringUtils;
import org.jboss.seam.contexts.Contexts;
import org.jboss.seam.security.Identity;
import org.jboss.seam.security.AuthorizationException;
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

    private static final String VIEW_URL = "viewUrl";

    @Override
    protected void doGet(final HttpServletRequest request, final HttpServletResponse response) throws ServletException, IOException {
        try {
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
            } else if (url.indexOf("feed/discussion")  > -1) {
                doAuthorizedAction(request, response, new A() {
                    public void a() throws Exception {
                        doDiscussionFeed(request, response);
                    }
                });
            }
        } catch (AuthorizationException e) {
            response.setHeader("WWW-Authenticate", "BASIC realm=\"users\"");
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
        }

    }

    private void doDiscussionFeed(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String assetName = request.getParameter("assetName");
        String packageName = request.getParameter("package");
        AssetItem asset = getFileManager().getRepository().loadPackage(packageName).loadAsset(assetName);
        checkPackageReadPermission(asset.getPackageName());

        List<AtomFeed.AtomEntry> entries = new ArrayList<AtomFeed.AtomEntry>();
        entries.add(new AtomFeed.AtomEntry(request,  asset));
        List<DiscussionRecord> drs = new Discussion().fromString(asset.getStringProperty(Discussion.DISCUSSION_PROPERTY_KEY));
        for (DiscussionRecord dr : drs) {
            entries.add(new AtomFeed.AtomEntry(request, asset, dr));
        }
        AtomFeed feed = new AtomFeed("Discussion of: " + packageName + "/" + assetName,
                Calendar.getInstance(),
                request.getServerName() + "/" + packageName + "/" + assetName,
                request.getParameter(VIEW_URL),
                request.getRequestURL().toString(), entries, "A list of updated discussion content.");
        response.setContentType("application/atom+xml");
        response.getOutputStream().print(feed.getAtom());
    }


    private void doCategoryFeed(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String cat = request.getParameter("name");
        String status = request.getParameter("status");
        checkCategoryPermission(cat);
        AssetPageList pg = getFileManager().getRepository().findAssetsByCategory(cat, false, 0, -1);
        Iterator<AssetItem> it = pg.assets.iterator();
        List<AtomFeed.AtomEntry> entries = new ArrayList<AtomFeed.AtomEntry>();
        buildEntries(request, entries, it, status);
        AtomFeed feed = new AtomFeed("Category: " + cat, Calendar.getInstance(), request.getServerName() + cat, request.getParameter(VIEW_URL), request.getRequestURL().toString(), entries, "Guvnor category of items: " + cat);
        response.setContentType("application/atom+xml");
        response.getOutputStream().print(feed.getAtom());
    }

    void checkCategoryPermission(String cat) {
        if ( Contexts.isSessionContextActive() ) {
            Identity.instance().checkPermission(  new CategoryPathType( cat ),
                                                 RoleTypes.ANALYST_READ );
        }
    }

    private void doPackageFeed(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String packageName = request.getParameter("name");
        checkPackageReadPermission(packageName);

        PackageItem pkg = getFileManager().getRepository().loadPackage(packageName);

        List<AtomFeed.AtomEntry> entries = new ArrayList<AtomFeed.AtomEntry>();
        Iterator<AssetItem> it = pkg.getAssets();
        buildEntries(request, entries, it, request.getParameter("status"));

        AtomFeed feed = new AtomFeed("Knowledge package: " + pkg.getName(), pkg.getLastModified(), pkg.getUUID(), request.getParameter(VIEW_URL), request.getRequestURL().toString(), entries, pkg.getDescription());
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


    void checkPackageReadPermission(String packageName) {
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


            /**
             * We are creating an entry for each asset.
             */
            public AtomEntry(HttpServletRequest req, AssetItem asset) {
                this.name = asset.getName();
                this.format = asset.getFormat();
                //Escape & with %26 to make generated XML safe. 
                this.webURL = req.getParameter(VIEW_URL) + "#asset=" + asset.getUUID() + "%26nochrome";
                //Each history version of asset has its unique UUID. &version is not needed here. Plus &version
                //is not being parsed on the server side 
                //this.id = asset.getUUID() + "&version=" + asset.getVersionNumber();
                this.id = asset.getUUID();
                this.updated = ISO8601.format(asset.getLastModified());
                this.published = ISO8601.format(asset.getCreatedDate());
                this.author = asset.getCreator();
                this.contributor = asset.getLastContributor();
                this.description = asset.getDescription();
                this.checkinComment = asset.getCheckinComment();
            }

            /** We are creating entries for each discussion record */
            public AtomEntry(HttpServletRequest req, AssetItem asset, DiscussionRecord discussionRec) {
                this.name = asset.getName();
                this.format = asset.getFormat();
                this.webURL = req.getParameter(VIEW_URL) + "#asset=" + asset.getUUID() + "%26nochrome";
                this.id = asset.getUUID() + "&discussion=" + discussionRec.timestamp + "&author=" + discussionRec.author;
                Calendar c = Calendar.getInstance();
                c.setTime(new Date(discussionRec.timestamp));
                this.updated = ISO8601.format(c);
                this.published = ISO8601.format(c);
                this.author = discussionRec.author;
                this.contributor = asset.getLastContributor();
                this.description = "Discussion comment was added by: " + discussionRec.author;
                this.checkinComment = discussionRec.note;

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
