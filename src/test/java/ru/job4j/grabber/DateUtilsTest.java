package ru.job4j.grabber;

import org.junit.Before;
import org.junit.Test;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

import static org.junit.Assert.assertEquals;

public class DateUtilsTest {
    private SimpleDateFormat sdf;
    private Calendar calendar;

    @Before
    public void init() {
        sdf = new SimpleDateFormat("dd MMMM yyyy, HH:mm", Locale.ENGLISH);
        calendar = new GregorianCalendar();
    }

    @Test
    public void whenDateIsYesterday() throws ParseException {
        String s = "вчера, 11:11";
        calendar.set(Calendar.HOUR_OF_DAY, 11);
        calendar.set(Calendar.MINUTE, 11);
        calendar.add(Calendar.DAY_OF_MONTH, -1);
        Date date = DateUtils.stringToDate(s);
        assertEquals(sdf.format(date), sdf.format(calendar.getTime()));
    }

    @Test
    public void whenDateIsNow() throws ParseException {
        String s = "сегодня, 00:02";
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 2);
        Date date = DateUtils.stringToDate(s);
        assertEquals(sdf.format(date), sdf.format(calendar.getTime()));
    }

    @Test
    public void whenDateIsValid() throws ParseException {
        String s = "22 июн 20, 19:15";
        calendar.set(Calendar.DAY_OF_MONTH, 22);
        calendar.set(Calendar.MONTH, 5);
        calendar.set(Calendar.YEAR, 2020);
        calendar.set(Calendar.HOUR_OF_DAY, 19);
        calendar.set(Calendar.MINUTE, 15);
        Date date = DateUtils.stringToDate(s);
        assertEquals(sdf.format(date), sdf.format(calendar.getTime()));
    }

    @Test(expected = ParseException.class)
    public void whenDateIsNotValid() throws ParseException {
        String s = "22 июня 20, 19:15";
        calendar.set(Calendar.DAY_OF_MONTH, 22);
        calendar.set(Calendar.MONTH, 5);
        calendar.set(Calendar.YEAR, 2020);
        calendar.set(Calendar.HOUR_OF_DAY, 19);
        calendar.set(Calendar.MINUTE, 15);
        Date date = DateUtils.stringToDate(s);
        assertEquals(sdf.format(date), sdf.format(calendar.getTime()));
    }
}
