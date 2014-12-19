package com.wangzhenyi.miracleinsperation;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * Used to present an insperation inputed by the user
 */
public class Insperation {
	private static final String DEFAULT_DATE_FORMAT = "yyyy-MM-dd\n HH:mm";
	private int id;
	
	public long getId() {
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

	private Date date;
	private String content;

	/**
	 * Constructor with the content as parameter, use current date as default
	 */
	public Insperation(String content) {
		this(new Date(), content);
	}

	/**
	 * Constructor with both content and date as parameter
	 */
	public Insperation(Date date, String content) {
		setDate(date);
		setContent(content);
	}

	public Insperation(int id, Date date, String content) {
		this(date,content);
		setId(id);
	}

	/* Returns a String representation of the date with the default format */
	private String getFormatedDate() {
		return getFormatedDate(DEFAULT_DATE_FORMAT);
	}
	
	/**
	 * Returns a string representing the date, with a specific format e.g.
	 * default "yyyy-MM-dd HH:mm"
	 */
	private String getFormatedDate(String formatStr) {
		SimpleDateFormat format = new SimpleDateFormat(formatStr);
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
		newDateForInnovation.setText(getFormatedDate());

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

	/**
	 * Tests
	 */
	public static void main(String[] args) {
		Insperation mi = new Insperation("hi");
		System.out.println(mi);

		DateFormat dateFormat2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date myDate2;
		try {
			myDate2 = dateFormat2.parse("2010-09-13 22:36:01");
			Insperation mi2 = new Insperation(myDate2, "hi2");
			System.out.println(mi2);
		} catch (ParseException e) {
			e.printStackTrace();
		}
	}

}
