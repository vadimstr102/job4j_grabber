package ru.job4j.grabber;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.text.DateFormatSymbols;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class SqlRuParse implements Parse {
    private DateFormatSymbols myDateFormatSymbols = new DateFormatSymbols() {
        @Override
        public String[] getMonths() {
            return new String[]{"янв", "фев", "мар", "апр", "май", "июн",
                    "июл", "авг", "сен", "окт", "ноя", "дек"};
        }
    };

    private Date stringToDate(String date) throws ParseException {
        if (date.contains("сегодня") || date.contains("вчера")) {
            Calendar calendar = new GregorianCalendar();
            int hour = Integer.parseInt(date.split(" ")[1].split(":")[0]);
            int minute = Integer.parseInt(date.split(" ")[1].split(":")[1]);
            calendar.set(Calendar.HOUR_OF_DAY, hour);
            calendar.set(Calendar.MINUTE, minute);
            if (date.contains("вчера")) {
                calendar.add(Calendar.DAY_OF_MONTH, -1);
            }
            return calendar.getTime();
        }
        SimpleDateFormat sdfIn = new SimpleDateFormat("dd MMM yy, HH:mm", myDateFormatSymbols);
        return sdfIn.parse(date);
    }

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
        String text = msg.text();
        Elements footers = doc.select(".msgFooter");
        Element footer = footers.first();
        String date = footer.text().split(" \\[")[0];
        return new Post(text, stringToDate(date));
    }

    public static void main(String[] args) throws Exception {
        SqlRuParse sqlRuParse = new SqlRuParse();
        List<Post> posts = new ArrayList<>();
        int numPage = 1;
        for (int i = 1; i <= numPage; i++) {
            posts.addAll(sqlRuParse.list("https://www.sql.ru/forum/job-offers/" + i));
        }
        System.out.println(posts.size());
        for (Post post : posts) {
            System.out.println(post.getName() + "\r\n" + post.getLink());
            Post postDetail = sqlRuParse.detail(post.getLink());
            System.out.println(postDetail.getText() + "\r\n" + postDetail.getCreated());
        }
    }
}
