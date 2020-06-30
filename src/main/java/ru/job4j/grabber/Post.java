package ru.job4j.grabber;

import java.util.Date;

public class Post {
    private String name;
    private String link;
    private String text;
    private Date created;

    public Post(String name, String link) {
        this.name = name;
        this.link = link;
    }

    public Post(String text, Date created) {
        this.text = text;
        this.created = created;
    }

    public String getText() {
        return text;
    }

    public Date getCreated() {
        return created;
    }

    public String getName() {
        return name;
    }

    public String getLink() {
        return link;
    }
}
