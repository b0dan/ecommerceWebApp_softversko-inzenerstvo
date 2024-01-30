package com.fict.eCommerceWebApp.utils;

import com.fict.eCommerceWebApp.services.util.BeanUtil;
import org.ocpsoft.prettytime.PrettyTime;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;

public class DateTimeUtil {
    public String getPrettyTime(LocalDateTime dateForConvert) {
        PrettyTime pt = BeanUtil.getBean(PrettyTime.class);
        return pt.format(convertToDateViaInstant(dateForConvert));
    }

    public static String getFormattedTimeStamp(LocalDateTime date) {
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy - HH:mm:ss");
            String formattedString = date.format(formatter);
            return formattedString;
        } catch (Exception e) {
            return "";
        }
    }

    public static Date convertToDateViaInstant(LocalDateTime dateToConvert) {
        return java.util.Date.from(dateToConvert.atZone(ZoneId.systemDefault()).toInstant());
    }

    public static LocalDateTime convertToLocalDateTimeViaInstant(Date dateToConvert) {
        return dateToConvert.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime();
    }

    public static LocalDateTime convertToLocalDateTimeViaMillisecond(Date dateToConvert) {
        return Instant.ofEpochMilli(dateToConvert.getTime())
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime();
    }

    public static String getKRDate(LocalDate date) {
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            String formattedString = date.format(formatter);
            return formattedString;
        } catch (Exception e) {
            return "";
        }
    }

    public static String getKRDate(LocalDateTime date) {
        return getKRDate(date.toLocalDate());
    }

    public static String getKRDate(Date date) {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(date);
    }

    public static Date getDateFromString(String dateAsString) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        try {
            Date date = formatter.parse(dateAsString);
            return date;
        } catch (Exception e) {
            return null;
        }
    }
}