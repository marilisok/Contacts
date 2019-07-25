package commands.util;

import java.sql.Date;
import java.text.SimpleDateFormat;

import java.text.ParseException;

public class GeneralUtil {
    public static Date stringToDate(String stringDate) {
        Date date = null;
        if(stringDate != null && ! "".equals(stringDate)) {
            try {
                SimpleDateFormat formatFrom = new SimpleDateFormat("yyyy-MM-dd");
                date = new Date(formatFrom.parse(stringDate).getTime());


            } catch (ParseException e) {
                throw new RuntimeException("Error while convert string to date", e);
            }
        }
        return date;
    }
}
