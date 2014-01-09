package com.marklogic.hello;

import java.util.Date;

public class Entry {
    private String id;
    private String author;
    private String title;
    private String description;
    private Date submissionDate;

    public Entry() {
        super();
    }

    public Entry(String title) {
        super();
        this.title = title;
    }

    public String getId() {
        return id;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String text) {
        this.description = text;
    }

    public Date getSubmissionDate() {
        return submissionDate;
    }

    public void setSubmissionDate(Date submissionDate) {
        this.submissionDate = submissionDate;
    }

}
