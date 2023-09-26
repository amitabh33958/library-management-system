package com.luv2code.springbootlibrary.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class DateUtils {

    public static Long calculateDifferenceInTime(String returnDate) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        long differenceInTime;
        try {
            Date d1 = sdf.parse(returnDate);
            Date d2 = sdf.parse(LocalDate.now().toString());
            TimeUnit time = TimeUnit.DAYS;
            differenceInTime = time.convert(d1.getTime() - d2.getTime(), TimeUnit.MILLISECONDS);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
        return differenceInTime;
    }
}
