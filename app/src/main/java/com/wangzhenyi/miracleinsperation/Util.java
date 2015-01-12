/**
 * Created by Zhenyi Wang.
 * Last modified on 08 Jan 2015.
 */

package com.wangzhenyi.miracleinsperation;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * This class is used as a collection of useful methods
 */
public class Util {


    /**
     * Formats a string with the default timezone
     * @param date the date to be converted
     * @param format the format
     * @return the formatted date
     */
    public static String formatDate(Date date, String format) {
        //TODO consider local when format date
        return new SimpleDateFormat(format).format(date);
    }
}
