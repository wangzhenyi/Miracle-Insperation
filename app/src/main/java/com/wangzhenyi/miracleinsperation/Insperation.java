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
 * Used to present an insperation inputed by the user
 */
public class Insperation {
	private static final String DEFAULT_DATE_FORMAT = "yyyy-MM-dd\n HH:mm";
	private int id;
	public final static int ID_UNIMPLEMENTED = -1;
	private Date date;
	private String content;
	
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
	 * Constructor with the content as parameter, use current date as default
	 */
	public Insperation(String content) {
		this(ID_UNIMPLEMENTED, new Date(), content);
	}

	/**
	 * Constructor with both content and date as parameter
	 */
	public Insperation(Date date, String content) {
		this(ID_UNIMPLEMENTED, date, content);
	}

	public Insperation(int id, Date date, String content) {
		setId(id);
		setDate(date);
		setContent(content);
	}

	/* Returns a String representation of the date with the default format */
	public String getFormatedDate() {
		return getFormatedDate(DEFAULT_DATE_FORMAT);
	}
	
	/**
	 * Returns a string representing the date, with a specific format e.g.
	 * default "yyyy-MM-dd HH:mm"
	 */
	@SuppressLint("SimpleDateFormat")
	public String getFormatedDate(String formatStr) {
		SimpleDateFormat format = new SimpleDateFormat(formatStr); //TODO consider local
		String dateStr = format.format(date);
		return dateStr;
	}

	/**
	 * Convert to a string representation
	 */
	public String toString() {
		String formatedDate = getFormatedDate(DEFAULT_DATE_FORMAT);
		return "[" + formatedDate + "]" + content;
	}

	/**
	 * Returns a View representation to be displayed LinearLayout with 
	 * two views inside
	 */
	@SuppressLint("NewApi")
	public View toView(Context context) {
		// LinearLayout 
		LinearLayout viewRtn = new LinearLayout(context);

		// set sizes, width wrap content, height wrap content
		LayoutParams size = new LayoutParams(
				LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		
		viewRtn.setLayoutDirection(LinearLayout.VERTICAL); //TODO api>=17
		viewRtn.setLayoutParams(size);
		
		// Create new views
		TextView newContentForInnovation = new TextView(context);
		TextView newDateForInnovation = new TextView(context);
		
		// Assign text to new view
		newContentForInnovation.setText(getContent());
		newDateForInnovation.setText(getFormatedDate()+"  "+getId());

		// Assign padding, and random background colour
		Random rnd = new Random();
		int colour = Color.argb(255, rnd.nextInt(100) + 155,
				rnd.nextInt(100) + 155, rnd.nextInt(100) + 155);

		newDateForInnovation.setTextColor(Color.argb(255, 100, 100, 100));
		newDateForInnovation.setTextSize(10);
		newDateForInnovation.setBackgroundColor(colour);
		newDateForInnovation.setPadding(15, 15, 15, 0);

		newContentForInnovation.setBackgroundColor(colour);
		newContentForInnovation.setPadding(15, 5, 15, 25);

		viewRtn.addView(newDateForInnovation);
		viewRtn.addView(newContentForInnovation);
		
		return viewRtn;
	}

	public String toCopyString() {
		return getFormatedDate() + "\n" + getContent();
	}

	public String toStringToCopy() {
		return getFormatedDate("yyyy-MM-dd HH:mm") + "\n" + content;
	}

}