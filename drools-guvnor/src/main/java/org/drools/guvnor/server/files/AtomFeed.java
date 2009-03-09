package org.drools.guvnor.server.files;

import org.apache.jackrabbit.util.ISO8601;
import org.drools.repository.AssetItem;
import org.drools.guvnor.client.common.HTMLFileManagerFields;
import org.drools.util.StringUtils;
import org.mvel2.MVEL;
import org.mvel2.templates.TemplateRuntime;

import javax.servlet.http.HttpServletRequest;
import java.util.*;
import java.io.InputStreamReader;

/**
 * @author Michael Neale
 */
public class AtomFeed {

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

        public AtomEntry(HttpServletRequest req, AssetItem asset) {
            this.name = asset.getName() + "." + asset.getFormat();
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


