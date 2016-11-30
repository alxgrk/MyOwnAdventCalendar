package com.alxgrk.myownadventcalendar.date;

import org.junit.Before;
import org.junit.Test;

import java.util.Calendar;

import static org.junit.Assert.*;

/**
 * Created by alex on 19.11.16.
 */
public class DateUtilsTest {

    private DateUtils uut;

    @Before
    public void setUp() throws Exception {
        uut = new DateUtils();
    }

    @Test
    public void testGetToday() throws Exception {
        Calendar calendar = Calendar.getInstance();

        SimpleDate actual = uut.getToday();

        assertEquals(calendar.get(Calendar.DATE), actual.getDay());
        assertEquals(calendar.get(Calendar.MONTH) + 1, actual.getMonth());
        assertEquals(calendar.get(Calendar.YEAR), actual.getYear());
    }

    @Test
    public void testDaysToChristmas_smaller() throws Exception {
        int expected = 1;
        SimpleDate today = new SimpleDate(23, 12, 2000);

        int actual = uut.daysUntilChristmas(today);

        assertEquals(expected, actual);
    }

    @Test
    public void testDaysToChristmas_same() throws Exception {
        int expected = 0;
        SimpleDate today = new SimpleDate(24, 12, 2000);

        int actual = uut.daysUntilChristmas(today);

        assertEquals(expected, actual);
    }

    @Test
    public void testDaysToChristmas_greater() throws Exception {
        int expected = 364;
        SimpleDate today = new SimpleDate(25, 12, 2000);

        int actual = uut.daysUntilChristmas(today);

        assertEquals(expected, actual);
    }
}