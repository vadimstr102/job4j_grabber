package ru.job4j.grabber;

import java.util.Date;

public class Post {
    private String id;
    private String name;
    private String text;
    private String link;
    private Date created;

    public Post(String name, String link) {
        this.name = name;
        this.link = link;
    }

    public Post(String name, String text, String link, Date created) {
        this.name = name;
        this.text = text;
        this.link = link;
        this.created = created;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public String getText() {
        return text;
    }

    public String getLink() {
        return link;
    }

    public Date getCreated() {
        return created;
    }
}
