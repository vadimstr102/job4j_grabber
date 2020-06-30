package ru.job4j.grabber;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.*;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class PsqlStore implements Store, AutoCloseable {
    private Connection cnn;

    public PsqlStore(Properties cfg) {
        try {
            Class.forName(cfg.getProperty("jdbc.driver"));
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
        try {
            cnn = DriverManager.getConnection(
                    cfg.getProperty("jdbc.url"),
                    cfg.getProperty("jdbc.username"),
                    cfg.getProperty("jdbc.password")
            );
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void save(Post post) {
        try (PreparedStatement st = cnn.prepareStatement(
                "insert into post (name, text, link, created) values (?, ?, ?, ?)"
        )) {
            st.setString(1, post.getName());
            st.setString(2, post.getText());
            st.setString(3, post.getLink());
            st.setTimestamp(4, new Timestamp(post.getCreated().getTime()));
            st.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<Post> getAll() {
        List<Post> list = new ArrayList<>();
        try (PreparedStatement st = cnn.prepareStatement("select * from post")) {
            try (ResultSet rs = st.executeQuery()) {
                while (rs.next()) {
                    Post post = new Post(
                            rs.getString("name"),
                            rs.getString("text"),
                            rs.getString("link"),
                            rs.getDate("created")
                    );
                    list.add(post);
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    @Override
    public Post findById(String id) {
        Post post = null;
        try (PreparedStatement st = cnn.prepareStatement("select * from post where id=?")) {
            st.setInt(1, Integer.parseInt(id));
            try (ResultSet rs = st.executeQuery()) {
                if (rs.next()) {
                    post = new Post(
                            rs.getString("name"),
                            rs.getString("text"),
                            rs.getString("link"),
                            rs.getDate("created")
                    );
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return post;
    }

    @Override
    public void close() throws Exception {
        if (cnn != null) {
            cnn.close();
        }
    }

    public static void main(String[] args) throws IOException, ParseException {
        Properties cfg = new Properties();
        try (FileInputStream in = new FileInputStream("src/main/resources/app.properties")) {
            cfg.load(in);
        } catch (IOException e) {
            e.printStackTrace();
        }
        PsqlStore store = new PsqlStore(cfg);
        SqlRuParse sqlRuParse = new SqlRuParse();
        List<Post> posts = sqlRuParse.list("https://www.sql.ru/forum/job-offers/1");
        for (Post post : posts) {
            post = sqlRuParse.detail(post.getLink());
            store.save(post);
        }
        System.out.println(store.getAll().size());
        System.out.println(store.findById("16").getLink());
    }
}
