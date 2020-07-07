package ru.job4j.grabber;

import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.Date;
import java.util.Properties;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

public class PsqlStoreTest {
    private Properties cfg = new Properties();
    private PsqlStore store;
    private Post post1 = new Post("name1", "text1", "link1", new Date());
    private Post post2 = new Post("name2", "text2", "link2", new Date());
    private Post post3 = new Post("name3", "text3", "link3", new Date());

    @Before
    public void init() throws SQLException {
        try (InputStream in = PsqlStore.class.getClassLoader()
                .getResourceAsStream("app.properties")) {
            cfg.load(in);
        } catch (IOException e) {
            e.printStackTrace();
        }
        store = new PsqlStore(cfg);
        store.initConnectionRollback();
    }

    @Test
    public void whenSaveAndGet() throws Exception {
        int oldSize = store.getAll().size();
        store.save(post1);
        store.save(post2);
        store.save(post3);
        assertThat(store.getAll().size(), is(oldSize + 3));
        store.close();
    }

    @Test
    public void whenFindById() throws Exception {
        store.save(post1);
        store.save(post2);
        store.save(post3);
        assertThat(store.findById(post2.getId()).getName(), is("name2"));
        store.close();
    }

    @Test
    public void whenGetLastDate() throws Exception {
        store.save(post1);
        store.save(post2);
        post3 = new Post("name3", "text3", "link3", new Date());
        store.save(post3);
        assertEquals(store.getLastDate(), post3.getCreated());
        store.close();
    }
}
