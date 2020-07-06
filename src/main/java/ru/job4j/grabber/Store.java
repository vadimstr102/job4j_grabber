package ru.job4j.grabber;

import java.util.Date;
import java.util.List;

public interface Store {
    void save(Post post);

    void saveAll(List<Post> posts);

    List<Post> getAll();

    Post findById(String id);

    Date getLastDate();
}
