/*
 * Copyright 2010 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.guvnor.server.files;

import org.drools.core.util.StringUtils;
import org.drools.guvnor.client.rpc.DiscussionRecord;
import org.drools.guvnor.server.repository.Preferred;
import org.drools.guvnor.server.security.CategoryPathType;
import org.drools.guvnor.server.security.ModuleNameType;
import org.drools.guvnor.server.security.RoleType;
import org.drools.guvnor.server.util.Discussion;
import org.drools.guvnor.server.util.ISO8601;
import org.drools.repository.AssetItem;
import org.drools.repository.AssetItemPageResult;
import org.drools.repository.ModuleItem;
import org.drools.repository.RulesRepository;
import org.jboss.seam.security.AuthorizationException;
import org.jboss.seam.security.Identity;
import org.mvel2.templates.TemplateRuntime;

import javax.inject.Inject;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;

public class FeedServlet extends RepositoryServlet {

    private static final String VIEW_URL = "viewUrl";

    @Inject @Preferred
    private RulesRepository rulesRepository;

    @Inject
    private Identity identity;

    @Override
    protected void doGet(final HttpServletRequest request,
                         final HttpServletResponse response) throws ServletException,
            IOException {
        try {
            String url = request.getRequestURI();
            if (url.contains("feed/package")) {
                doAuthorizedAction(request,
                        response,
                        new Command() {
                            public void execute() throws Exception {
                                doPackageFeed(request,
                                        response);
                            }
                        });
            } else if (url.contains("feed/category")) {
                doAuthorizedAction(request,
                        response,
                        new Command() {
                            public void execute() throws Exception {
                                doCategoryFeed(request,
                                        response);
                            }
                        });
            } else if (url.contains("feed/discussion")) {
                doAuthorizedAction(request,
                        response,
                        new Command() {
                            public void execute() throws Exception {
                                doDiscussionFeed(request,
                                        response);
                            }
                        });
            }
        } catch (AuthorizationException e) {
            response.setHeader("WWW-Authenticate",
                    "BASIC realm=\"users\"");
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
        }

    }

    private void doDiscussionFeed(HttpServletRequest request,
                                  HttpServletResponse response) throws IOException {
        String assetName = request.getParameter("assetName");
        String packageName = request.getParameter("package");
        AssetItem asset = rulesRepository.loadModule(packageName).loadAsset(assetName);
        checkPackageReadPermission(asset.getModuleName());

        List<AtomFeed.AtomEntry> entries = new ArrayList<AtomFeed.AtomEntry>();
        entries.add(new AtomFeed.AtomEntry(request,
                asset));
        List<DiscussionRecord> drs = new Discussion().fromString(asset.getStringProperty(Discussion.DISCUSSION_PROPERTY_KEY));
        for (DiscussionRecord dr : drs) {
            entries.add(new AtomFeed.AtomEntry(request,
                    asset,
                    dr));
        }
        AtomFeed feed = new AtomFeed("Discussion of: " + packageName + "/" + assetName,
                Calendar.getInstance(),
                request.getServerName() + "/" + packageName + "/" + assetName,
                request.getParameter(VIEW_URL),
                request.getRequestURL().toString(),
                entries,
                "A list of updated discussion content.");
        response.setContentType("application/atom+xml; charset=UTF-8");
        response.getWriter().print(feed.getAtom());
    }

    private void doCategoryFeed(HttpServletRequest request,
                                HttpServletResponse response) throws IOException {
        String cat = request.getParameter("name");
        String status = request.getParameter("status");
        checkCategoryPermission(cat);
        AssetItemPageResult pg = rulesRepository.findAssetsByCategory(cat,
                false,
                0,
                -1);
        Iterator<AssetItem> it = pg.assets.iterator();
        List<AtomFeed.AtomEntry> entries = new ArrayList<AtomFeed.AtomEntry>();
        buildEntries(request,
                entries,
                it,
                status);
        AtomFeed feed = new AtomFeed("Category: " + cat,
                Calendar.getInstance(),
                request.getServerName() + cat,
                request.getParameter(VIEW_URL),
                request.getRequestURL().toString(),
                entries,
                "Guvnor category of items: " + cat);
        response.setContentType("application/atom+xml; charset=UTF-8");
        response.getWriter().print(feed.getAtom());
    }

    void checkCategoryPermission(String cat) {
        identity.checkPermission(new CategoryPathType(cat),
                RoleType.ANALYST_READ.getName());
    }

    private void doPackageFeed(HttpServletRequest request,
                               HttpServletResponse response) throws IOException {
        String packageName = request.getParameter("name");
        checkPackageReadPermission(packageName);

        ModuleItem pkg = rulesRepository.loadModule(packageName);

        List<AtomFeed.AtomEntry> entries = new ArrayList<AtomFeed.AtomEntry>();
        Iterator<AssetItem> it = pkg.getAssets();
        buildEntries(request,
                entries,
                it,
                request.getParameter("status"));

        AtomFeed feed = new AtomFeed("Knowledge package: " + pkg.getName(),
                pkg.getLastModified(),
                pkg.getUUID(),
                request.getParameter(VIEW_URL),
                request.getRequestURL().toString(),
                entries,
                pkg.getDescription());
        response.setContentType("application/atom+xml; charset=UTF-8");
        response.getWriter().print(feed.getAtom());
    }

    private void buildEntries(HttpServletRequest request,
                              List<AtomFeed.AtomEntry> entries,
                              Iterator<AssetItem> it,
                              String status) {
        while (it.hasNext()) {
            AssetItem as = it.next();
            if (!as.isArchived() && !as.getDisabled()) {
                if (status == null || status.equals("*") || as.getStateDescription().equals(status)) {
                    entries.add(new AtomFeed.AtomEntry(request,
                            as));
                }
            }
        }
    }

    void checkPackageReadPermission(String packageName) {
        identity.checkPermission(new ModuleNameType(packageName),
                RoleType.PACKAGE_READONLY.getName());
    }

    public static class AtomFeed {

        private static final String TEMPLATE = StringUtils.readFileAsString(new InputStreamReader(AtomFeed.class.getResourceAsStream("/atom-feed-template.xml")));

        private final String feedTitle;
        private final String feedUpdated;
        private final String feedId;
        private final String feedAlternate;
        private final String feedSelf;
        private final String subtitle;
        private final Collection<AtomEntry> entries;

        public AtomFeed(String title,
                        Calendar whenUpdate,
                        String feedId,
                        String feedAlternate,
                        String feedSelf,
                        Collection<AtomEntry> entries,
                        String subtitle) {
            this.feedTitle = title;
            this.feedUpdated = ISO8601.format(whenUpdate);
            this.feedId = feedId;
            this.feedAlternate = feedAlternate;
            this.feedSelf = feedSelf;
            this.entries = entries;
            this.subtitle = subtitle;
        }

        public String getAtom() {
            Map<String, AtomFeed> m = new HashMap<String, AtomFeed>();
            m.put("feed",
                    this);
            return (String) TemplateRuntime.eval(TEMPLATE,
                    m);
        }

        public String getSubtitle() {
            return subtitle;
        }

        @SuppressWarnings("rawtypes")
        public Collection getEntries() {
            return entries;
        }

        public String getFeedTitle() {
            return feedTitle;
        }

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
            private final String name;
            private final String webURL;
            private final String id;
            private final String updated;
            private final String published;
            private final String author;
            private final String contributor;
            private final String description;
            private final String checkinComment;
            private final String format;

            /**
             * We are creating an entry for each asset.
             */
            public AtomEntry(HttpServletRequest req,
                             AssetItem asset) {
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

            /**
             * We are creating entries for each discussion record
             */
            public AtomEntry(HttpServletRequest req,
                             AssetItem asset,
                             DiscussionRecord discussionRec) {
                this.name = asset.getName();
                this.format = asset.getFormat();
                this.webURL = req.getParameter(VIEW_URL) + "#asset=" + asset.getUUID() + "%26nochrome";
                this.id = asset.getUUID();
                Calendar c = Calendar.getInstance();
                c.setTime(new Date(discussionRec.timestamp));
                this.updated = ISO8601.format(c);
                this.published = ISO8601.format(c);
                this.author = discussionRec.author;
                this.contributor = asset.getLastContributor();
                this.description = "Discussion comment was added by: " + discussionRec.author;
                String noteLines[] = discussionRec.note.split("\\r?\\n");
                String wn = "";
                for (int i = 0; i < noteLines.length; i++) {
                    wn += noteLines[i];
                    if (i != noteLines.length - 1) {
                        wn += "<br/>";
                    }
                }
                this.checkinComment = wn;

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
