package ru.job4j.html;

import java.util.Date;

public class Post {
    private String text;
    private Date created;

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
}
