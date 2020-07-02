package ru.job4j.grabber;

import java.text.DateFormatSymbols;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class DateUtils {
    private static final DateFormatSymbols MY_DATE_FORMAT_SYMBOLS = new DateFormatSymbols() {
        @Override
        public String[] getMonths() {
            return new String[]{"янв", "фев", "мар", "апр", "май", "июн",
                    "июл", "авг", "сен", "окт", "ноя", "дек"};
        }
    };

    public static Date stringToDate(String date) throws ParseException {
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
        SimpleDateFormat sdfIn = new SimpleDateFormat("dd MMM yy, HH:mm", MY_DATE_FORMAT_SYMBOLS);
        return sdfIn.parse(date);
    }
}
