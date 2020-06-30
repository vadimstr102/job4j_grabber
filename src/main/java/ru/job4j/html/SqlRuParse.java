package ru.job4j.html;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.text.DateFormatSymbols;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class SqlRuParse {
    public static void main(String[] args) throws Exception {
        int numPage = 5;
        for (int i = 1; i <= numPage; i++) {
            Document doc = Jsoup.connect("https://www.sql.ru/forum/job-offers/" + i).get();
            Elements row = doc.select(".postslisttopic");
            for (Element td : row) {
                Element href = td.child(0);
                Element date = td.parent().child(5);
                System.out.println(href.attr("href"));
                System.out.println(href.text());
                System.out.println(stringToDate(date.text()));
                System.out.println();
            }
        }
    }

    private static Date stringToDate(String date) throws ParseException {
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

    private static DateFormatSymbols myDateFormatSymbols = new DateFormatSymbols() {
        @Override
        public String[] getMonths() {
            return new String[]{"янв", "фев", "мар", "апр", "май", "июн",
                    "июл", "авг", "сен", "окт", "ноя", "дек"};
        }
    };
}
