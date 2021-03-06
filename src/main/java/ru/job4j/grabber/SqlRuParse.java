package ru.job4j.grabber;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import static ru.job4j.grabber.DateUtils.stringToDate;

public class SqlRuParse implements Parse {
    private static final Logger LOG = LoggerFactory.getLogger(SqlRuParse.class.getName());

    @Override
    public List<Post> list(String link) throws IOException {
        List<Post> result = new ArrayList<>();
        Document doc = Jsoup.connect(link).get();
        Elements row = doc.select(".postslisttopic");
        for (int i = 3; i < row.size(); i++) {
            Element href = row.get(i).child(0);
            String name = href.text();
            String postLink = href.attr("href");
            result.add(new Post(name, postLink));
        }
        return result;
    }

    @Override
    public Post detail(String link) throws IOException, ParseException {
        Document doc = Jsoup.connect(link).get();
        Elements messages = doc.select(".msgBody");
        Element msg = messages.get(1);
        String text = msg.html();
        Elements footers = doc.select(".msgFooter");
        Element footer = footers.first();
        String date = footer.text().split(" \\[")[0];
        String name = msg.parent().parent().child(0).child(0).text();
        return new Post(name, text, link, stringToDate(date));
    }

    public static void main(String[] args) throws Exception {
        SqlRuParse sqlRuParse = new SqlRuParse();
        List<Post> posts = new ArrayList<>();
        int numPage = 1;
        for (int i = 1; i <= numPage; i++) {
            posts.addAll(sqlRuParse.list("https://www.sql.ru/forum/job-offers/" + i));
        }
        LOG.debug("Number of posts: {}", posts.size());
        for (Post post : posts) {
            LOG.debug(
                    "{}{}{}",
                    post.getName(),
                    System.lineSeparator(),
                    post.getLink()
            );
            Post postDetail = sqlRuParse.detail(post.getLink());
            LOG.debug(
                    "{}{}{}",
                    postDetail.getText(),
                    System.lineSeparator(),
                    postDetail.getCreated()
            );
        }
    }
}
