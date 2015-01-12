/**
 * Created by Zhenyi Wang.
 * Last modified on 12 Jan 2015.
 */

package com.wangzhenyi.miracleinsperation;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

/**
 * Used to present an insperation input by the user
 */
public class Insperation {
	private static final String DEFAULT_DATE_FORMAT = "yyyy-MM-dd\n HH:mm";
	private int id;
	public final static int ID_UNIMPLEMENTED = -1;
	private Date date;
	private String content;
    private int deleted;

    public static final int NOT_DELETED = 0;
    public static final int DELETED = 1;


    /**
     * Constructor with the content as parameter, use current date as default
     * @param content content
     */
    public Insperation(String content) {
        this(ID_UNIMPLEMENTED, new Date(), content);
    }

    /**
     * Constructor with both content and date as parameters
     * @param date creation date
     * @param content content of record
     */
    public Insperation(Date date, String content) {
        this(ID_UNIMPLEMENTED, date, content);
    }

    /**
     * Constructor with id, content and date as parameters
     * @param id id of record
     * @param date creation date
     * @param content content of record
     */
    public Insperation(int id, Date date, String content) {
        this(id, date, content, NOT_DELETED);
    }

    /**
     * Constructor with id, content, date and deleted state as parameters
     * @param id id of record
     * @param date creation date
     * @param content content of record
     * @param deleted delete state
     */
    public Insperation(int id, Date date, String content,int deleted) {
        setId(id);
        setDate(date);
        setContent(content);
        setDeleted(deleted);
    }

    public int getDeleted() {
        return deleted;
    }

    public void setDeleted(int deleted) {
        this.deleted = deleted;
    }

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

    /**
     * Returns a string representing the date, with a specific format e.g.
     * default "yyyy-MM-dd HH:mm"
     *
     *  @param formatStr the format string to represent date
     * @return a string representation of the date
     */
    @SuppressLint("SimpleDateFormat")
    public String getFormatedDate(String formatStr) {
        //TODO consider Local when format date in Insperation.java
        return Util.formatDate(date, formatStr);
    }

    /**
     * Returns a String representation of the date with the default format
     * @return default format of date
     */
	public String getFormatedDate() {
		return getFormatedDate(DEFAULT_DATE_FORMAT);
	}

    /**
     * Converts to a string representation
     * @return a string representation of the record
     */
	public String toString() {
        return getId() + "," + getDate().getTime() + "," +getContent() + "," + getDeleted();
	}

    /**
     * Converts the record to a string used to be copied to the clip board
     * formatted as
     *  [yyyy-MM-dd HH:mm]
     *  CONTENT
     * @return a formatted representation of the record to be copied
     */
	public String toStringToCopy() {
		return getFormatedDate("yyyy-MM-dd HH:mm") + "\n" + content;
	}

}
