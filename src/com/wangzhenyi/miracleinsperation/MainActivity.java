package com.wangzhenyi.miracleinsperation;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

public class MainActivity extends ActionBarActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	/* Called when the add button is clicked */
	public void addInnovation(View view) {
//		EditText innovationEditText = (EditText) findViewById(R.id.edit_innovation); 
//		TextView textView = (TextView) findViewById(R.id.text_view);
//		String innovation = innovationEditText.getText().toString();
//		textView.setText(innovation);
//		innovationEditText.setText("");
//		innovationEditText.clearFocus();
		
		// Get input
		EditText innovationEditText = (EditText) findViewById(R.id.edit_innovation); 
		String innovation = innovationEditText.getText().toString();
		
		// Format date
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		String date =format.format(new Date());
		
		// If input is empty then do nothing
		if(isEmpty(innovation))
			return;
		
		// Create new view 
		TextView newInnovation = new TextView(this);
		TextView newDateForInnovation = new TextView(this);
		
		// Assign text to new view
		newInnovation.setText(innovation);
		newDateForInnovation.setText("--" + date + "--");
		
		// Assign padding, and random background colour
		Random rnd = new Random(); 
		int colour = Color.argb(255, rnd.nextInt(100) + 155, rnd.nextInt(100) +155, rnd.nextInt(100) + 155); 

		newDateForInnovation.setTextColor(Color.argb(255,100,100,100));
		newDateForInnovation.setTextSize(10);
		newDateForInnovation.setBackgroundColor(colour);
		newDateForInnovation.setPadding(15, 15, 15, 0);
		
		newInnovation.setBackgroundColor(colour);
		newInnovation.setPadding(15, 5, 15, 25);
		
		
		// Add new view
		LinearLayout innovationList = (LinearLayout) findViewById(R.id.innovation_list);
		innovationList.addView(newDateForInnovation);
		innovationList.addView(newInnovation);
		
		
		// Clear input
		innovationEditText.setText("");
	}

	private boolean isEmpty(String str) {
		return str==null || str.equals("");
	}
}
