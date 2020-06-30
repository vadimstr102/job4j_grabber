package ru.job4j.html;

import java.util.Date;

public class Post {
    private String name;
    private String text;
    private String link;
    private Date created;

    public Post(String name, String text, String link, Date created) {
        this.name = name;
        this.text = text;
        this.link = link;
        this.created = created;
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
