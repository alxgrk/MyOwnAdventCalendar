package com.alxgrk.myownadventcalendar.date;

import org.junit.Test;

import java.util.Calendar;

import static org.junit.Assert.*;

/**
 * Created by alex on 19.11.16.
 */
public class SimpleDateTest {

    @Test
    public void testFrom() throws Exception {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DATE, 1);
        calendar.set(Calendar.MONTH, Calendar.JANUARY);
        calendar.set(Calendar.YEAR, 2000);

        SimpleDate actual = SimpleDate.from(calendar);

        assertEquals(1, actual.getDay());
        assertEquals(1, actual.getMonth());
        assertEquals(2000, actual.getYear());
    }

    @Test
    public void testSetFrom() throws Exception {
        SimpleDate actual = new SimpleDate(1, 1, 2000);
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DATE, 1);
        calendar.set(Calendar.MONTH, Calendar.JANUARY);
        calendar.set(Calendar.YEAR, 2000);

        actual.setFrom(calendar);

        assertEquals(1, actual.getDay());
        assertEquals(1, actual.getMonth());
        assertEquals(2000, actual.getYear());
    }

    @Test
    public void testApplyOnCalendar() throws Exception {
        SimpleDate date = new SimpleDate(1, 1, 2000);
        Calendar actual = Calendar.getInstance();

        date.applyOn(actual);

        assertEquals(actual.get(Calendar.DATE), date.getDay());
        assertEquals(actual.get(Calendar.MONTH) + 1, date.getMonth());
        assertEquals(actual.get(Calendar.YEAR), date.getYear());
    }

    @Test
    public void testSame() throws Exception {
        SimpleDate one = new SimpleDate(1, 1, 2000);
        SimpleDate another = new SimpleDate(1, 1, 2000);
        
        assertTrue(one.same(another));

        another.setDay(2);
        assertFalse(one.same(another));

        another.setDay(1);
        another.setMonth(2);
        assertFalse(one.same(another));

        another.setMonth(1);
        another.setYear(1999);
        assertFalse(one.same(another));
    }

    @Test
    public void testBefore() throws Exception {
        SimpleDate one = new SimpleDate(1, 1, 2000);
        SimpleDate another = new SimpleDate(2, 1, 2000);

        assertTrue(one.before(another));

        another.setDay(1);
        assertFalse(one.before(another));

        another.setMonth(2);
        assertTrue(one.before(another));

        another.setYear(1999);
        assertFalse(one.before(another));
    }

    @Test
    public void testAfter() throws Exception {
        SimpleDate one = new SimpleDate(1, 1, 2000);
        SimpleDate another = new SimpleDate(2, 1, 2000);

        assertFalse(one.after(another));

        another.setDay(1);
        assertFalse(one.after(another));

        another.setMonth(2);
        assertFalse(one.after(another));

        another.setYear(1999);
        assertTrue(one.after(another));
    }

    @Test
    public void testDifferenceInDays_after() throws Exception {
        SimpleDate one = new SimpleDate(1, 1, 2000);
        SimpleDate another = new SimpleDate(2, 1, 2000);

        int actual = one.differenceInDays(another);

        assertEquals(1, actual);
    }

    @Test
    public void testDifferenceInDays_same() throws Exception {
        SimpleDate one = new SimpleDate(1, 1, 2000);
        SimpleDate another = new SimpleDate(1, 1, 2000);

        int actual = one.differenceInDays(another);

        assertEquals(0, actual);
    }

    @Test
    public void testDifferenceInDays_greater() throws Exception {
        SimpleDate one = new SimpleDate(1, 1, 2000);
        SimpleDate another = new SimpleDate(31, 12, 1999);

        int actual = one.differenceInDays(another);

        assertEquals(1, actual);
    }
}