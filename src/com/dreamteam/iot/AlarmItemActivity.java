package com.dreamteam.iot;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;

import com.google.gson.Gson;

public class AlarmItemActivity extends Activity implements OnDateSetListener ,Serializable{

	/*
	 * Because the AlertDialog doesn't work good on some Android phones we
	 * are implementing this Activity in order to replace the Dialog 
	 * 
	 * Offf !!! 
	 */
	
	private ArrayList<alarm_item> alarms;
	private int year , month ,day ;
	private int position ;
	private int intentRequestCode ;
	EditText hour_EditText;
	EditText minute_EditText;
	
	private Button date_button;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.alarm_item_activity_layout);
		
		
		/*
		 * Reference the UIItems 
		 */
		
		
		hour_EditText = (EditText)findViewById(R.id.hour_EditText);
		minute_EditText = (EditText)findViewById(R.id.minute_EditText);
		Button ok_button =(Button)findViewById(R.id.ok_button);
		Button cancel_button = (Button)findViewById(R.id.cancel_button);
		date_button = (Button)findViewById(R.id.date_button);
		
		alarms = (ArrayList<alarm_item>) getIntent().getExtras().get("alarms");
		position = getIntent().getExtras().getInt("position");
	
		
		hour_EditText.setText(alarms.get(position).getHour()+"");
		minute_EditText.setText(alarms.get(position).getMinute()+"");
		
		day = alarms.get(position).getDay();
		month = alarms.get(position).getMonth();
		year = alarms.get(position).getYear();
		intentRequestCode = alarms.get(position).intentRequestCode;
		
		
		date_button.setText(alarms.get(position).getDay()+" / "+
							alarms.get(position).getMonth()+" / "+
							alarms.get(position).getYear()+" / ");
		
		final DatePickerDialog dateDialog ;
		dateDialog= new DatePickerDialog(this, this, year, month, day);
		
		date_button.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				dateDialog.setTitle("Select the day");
				dateDialog.show();
				
			}
		});
		
		ok_button.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				if(new Integer(hour_EditText.getText().toString()) >=  24){
					hour_EditText.setText("0");
				}
				
				if(new Integer(minute_EditText.getText().toString()) >=  60){
					minute_EditText.setText("0");
				}
				
				alarm_item aux = new alarm_item(year, month, day, new Integer(hour_EditText.getText().toString()),new Integer(minute_EditText.getText().toString()),intentRequestCode);
				
				Intent returnIntent = new Intent();
				returnIntent.putExtra("alarm",aux);
				returnIntent.putExtra("position", position);
				setResult(RESULT_OK,returnIntent);
				overridePendingTransition( R.anim.slide_in, R.anim.slide_out );
				finish();
			}
		});
		
		
		cancel_button.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				Intent returnIntent = new Intent();
				setResult(RESULT_CANCELED,returnIntent);
				finish();
				overridePendingTransition( R.anim.child_slide_in, R.anim.child_slide_out );
				
			}
		});
		
	}

	@Override
	public void onDateSet(DatePicker view, int year, int monthOfYear,
			int dayOfMonth) {
		
		this.day = dayOfMonth;
		this.month = monthOfYear +1;
		this.year = year ;
		
		date_button.setText(day +" / "+month+ " / "+year);
		
	}
	
	

	
	@Override
	protected void onPause() {
		overridePendingTransition(R.anim.child_slide_in, R.anim.child_slide_out);
		super.onPause();
	}




private class DeleteAsyncTask extends AsyncTask<Void, String, String>{

		
		private  final int STOPPED = -1;
		private  final int position;
		HttpClient client ;
		HttpPost post; 
		HttpResponse response;
		HttpEntity entity;
		String answer;
		
		
		
		public DeleteAsyncTask(int position){
			this.position = position;
		}

		protected String doInBackground(Void... params) {
			
			
			HttpClient client = new DefaultHttpClient();
			HttpPost post = new HttpPost("http://192.168.1.108:5000/delete/pozitie");
			HttpResponse response = null ;
			
			
			
			MultipartEntity ent = new MultipartEntity();
				
				
				try {
					StringBuilder builder = new StringBuilder();
					ent.addPart("parameter",new StringBody(position+""));
					
					
					post.setEntity(ent);
					
					response = client.execute(post);
					
					HttpEntity httpEntity = response.getEntity();
		            answer  = EntityUtils.toString(httpEntity);
					
				
					Log.d("AlarmItemActivity", "Response is "+ answer );
				} catch (ClientProtocolException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
				
			
			return answer;
		}
	}


	
	
	
}
