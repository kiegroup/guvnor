package org.guvnor.asset.management.backend.model;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

public class CommitInfo implements Serializable {

	private static final long serialVersionUID = -6255022381087425142L;
	private String commitId;
    private String message;
    private String author;
    private Date commitDate;
    private List<String> files;

    public CommitInfo(String commitId, String message, String author, Date commitDate, List<String> files) {
        this.commitId = commitId;
        this.message = message;
        this.author = author;
        this.commitDate = commitDate;
        this.files = files;
    }

    public String getCommitId() {
        return commitId;
    }

    public void setCommitId(String commitId) {
        this.commitId = commitId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public Date getCommitDate() {
        return commitDate;
    }

    public void setCommitDate(Date commitDate) {
        this.commitDate = commitDate;
    }

    public List<String> getFiles() {
        return files;
    }

    public void setFiles(List<String> files) {
        this.files = files;
    }

    @Override
    public String toString() {
        return "CommitInfo{" +
                "commitId='" + commitId + '\'' +
                ", message='" + message + '\'' +
                ", author='" + author + '\'' +
                ", commitDate=" + commitDate +
                ", files=" + files +
                '}';
    }

}
