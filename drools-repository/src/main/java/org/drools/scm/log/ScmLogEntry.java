package org.drools.scm.log;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ScmLogEntry {
    private String author;
    private Date   date;
    private String message;
    private List list;
    
    public ScmLogEntry(String author,
                       Date date,
                       String message) {
        super();
        this.author = author;
        this.date = date;
        this.message = message;
        this.list = new ArrayList();
    }
    
    public String getAuthor() {
        return author;
    }

    public Date getDate() {
        return date;
    }

    public String getMessage() {
        return message;
    }
    
    public void addAction(ScmLogEntryItem item) {
        this.list.add( item );
    }
    
    public List getAction() {
        return this.list;
    }
    
    
    
    public static class Add implements ScmLogEntryItem {
        private char   actionType = 'A';
        private char   pathType;        
        private String path;
        private long   revision;

        public Add(char type,
                   String path,
                   long revision) {
            super();
            this.pathType = type;
            this.path = path;
            this.revision = revision;
        }

        public String getPath() {
            return path;
        }

        public long getRevision() {
            return revision;
        }

        public char getPathType() {
            return pathType;
        }
        
        public char getActionType() {
            return this.actionType;
        }
    }

    public static class Delete implements ScmLogEntryItem {
        private char   actionType = 'D';
        private char   pathType;
        private String path;
        private long   revision;

        public Delete(char type,
                      String path,
                      long revision) {
            super();
            this.pathType = type;
            this.path = path;
            this.revision = revision;
        }

        public String getPath() {
            return path;
        }

        public long getRevision() {
            return revision;
        }

        public char getPathType() {
            return pathType;
        }
        
        public char getActionType() {
            return this.actionType;
        }
    }

    public static class Copy implements ScmLogEntryItem {
        private char   actionType = 'C';
        private char   pathType;
        private String fromPath;
        private long   fromRevision;
        private String toPath;
        private long   toRevision;

        public Copy(char type,
                    String fromPath,
                    long fromRevision,
                    String toPath,
                    long toRevision) {
            super();
            this.pathType = type;
            this.fromPath = fromPath;
            this.fromRevision = fromRevision;
            this.toPath = toPath;
            this.toRevision = toRevision;
        }

        public String getFromPath() {
            return fromPath;
        }

        public long getFromRevision() {
            return fromRevision;
        }

        public String getToPath() {
            return toPath;
        }

        public long gettoRevision() {
            return toRevision;
        }

        public char getPathType() {
            return pathType;
        }
        
        public char getActionType() {
            return this.actionType;
        }

    }

    /**
     * 
     * @author mproctor
     *
     */
    public static class Update implements ScmLogEntryItem {
        private char   actionType = 'U';
        private char   pathType;
        private String path;
        private long   revision;

        public Update(char type,
                      String path,
                      long revision) {
            super();
            this.pathType = type;
            this.path = path;
            this.revision = revision;
        }

        public String getPath() {
            return path;
        }

        public long getRevision() {
            return revision;
        }

        public char getPathType() {
            return pathType;
        }
        
        public char getActionType() {
            return this.actionType;
        }
    }

    /**
     * The Entry has been deleted and another of the same path added in the same transaction.
     * @author mproctor
     *
     */
    public static class Replaced implements ScmLogEntryItem {
        private char   actionType = 'R';
        private char   pathType;
        private String path;
        private long   revision;

        public Replaced(char type,
                        String path,
                        long revision) {
            super();
            this.pathType = type;
            this.path = path;
            this.revision = revision;
        }

        public String getPath() {
            return path;
        }

        public long getRevision() {
            return revision;
        }

        public char getPathType() {
            return pathType;
        }
        
        public char getActionType() {
            return this.actionType;
        }
    }
    
}
