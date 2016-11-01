package dv;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateFormattingTest {

    private static final Logger log = LoggerFactory.getLogger(DateFormattingTest.class);

    @Test
    public void simpleDateFormat() throws ParseException {
        SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");

        Date d = f.parse("2016-10-25T21:00:00.000Z");

        log.info("Parsed date: {}", d);
    }
}
