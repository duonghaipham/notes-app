package com.example.mynote;

public class Note {
    private String title;
    private String content;
    private String dateModified;
    private String tags;

    public Note() {
        title = "";
        content = "";
        dateModified = "";
        tags = "";
    }

    public Note(String title, String content, String dateModified, String tags) {
        this.title = title;
        this.content = content;
        this.dateModified = dateModified;
        this.tags = tags;
    }

    public Note(Note note) {
        title = note.title;
        content = note.content;
        dateModified = note.dateModified;
        tags = note.tags;
    }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    public String getDateModified() { return dateModified; }
    public void setDateModified(String dateModified) { this.dateModified = dateModified; }
    public String getTags() { return tags; }
    public void setTags(String tags) { this.tags = tags; }

}
