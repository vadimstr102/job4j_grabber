package ru.job4j.grabber;

import java.sql.*;
import java.util.ArrayList;
import java.util.Date;
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

    public void initConnectionRollback() throws SQLException {
        cnn = ConnectionRollback.create(cnn);
    }

    @Override
    public void save(Post post) {
        try (PreparedStatement st = cnn.prepareStatement(
                "insert into post (name, text, link, created) values (?, ?, ?, ?)",
                Statement.RETURN_GENERATED_KEYS
        )) {
            st.setString(1, post.getName());
            st.setString(2, post.getText());
            st.setString(3, post.getLink());
            st.setTimestamp(4, new Timestamp(post.getCreated().getTime()));
            st.executeUpdate();
            try (ResultSet generatedKeys = st.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    post.setId(generatedKeys.getString(1));
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void saveAll(List<Post> posts) {
        try (PreparedStatement st = cnn.prepareStatement(
                "insert into post (name, text, link, created) values (?, ?, ?, ?)",
                Statement.RETURN_GENERATED_KEYS
        )) {
            cnn.setAutoCommit(false);
            for (Post post : posts) {
                st.setString(1, post.getName());
                st.setString(2, post.getText());
                st.setString(3, post.getLink());
                st.setTimestamp(4, new Timestamp(post.getCreated().getTime()));
                st.addBatch();
                st.executeBatch();
                try (ResultSet generatedKeys = st.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        post.setId(generatedKeys.getString(1));
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            cnn.setAutoCommit(true);
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
                            new Date(rs.getTimestamp("created").getTime())
                    );
                    post.setId(String.valueOf(rs.getInt("id")));
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
                    post.setId(String.valueOf(rs.getInt("id")));
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
    public Date getLastDate() {
        Date date = null;
        try (PreparedStatement st = cnn.prepareStatement(
                "select * from post order by created desc limit 1"
        )) {
            try (ResultSet rs = st.executeQuery()) {
                if (rs.next()) {
                    date = new Date(
                            rs.getTimestamp("created").getTime()
                    );
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return date;
    }

    @Override
    public void close() throws Exception {
        if (cnn != null) {
            cnn.close();
        }
    }
}
