package com.dreamteam.iot;

import java.io.IOException;
import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.app.TimePickerDialog.OnTimeSetListener;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.DatePicker;
import android.widget.ListView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.dreamteam.iot.alarm.alarm_adapter;
import com.dreamteam.iot.alarmManager.AlarmManagerHelper;
import com.dreamteam.iot.settings.SettingsActivity;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

@TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
public class MainActivity extends Activity implements OnTimeSetListener, OnDateSetListener,OnItemClickListener {

	
	/*
	 * Java variables
	 */
	private final String TAG = "MainActivity IOT";
	private ArrayList<alarm_item> alarms;
	private int year,month,day,hour,minute;
	private boolean selected;  // var for showing if just one time dialog appeared  (because in this app it appears twice)
	private ArrayList<Integer> delay_offsets; 
	
	
	/*
	 * Android variables
	 */
	private ListView list ;
	private CustomTimePickerDialog timeDialog = null;
	private DatePickerDialog dateDialog =null ;
	private AlarmManager alarm_manager; 
	private Calendar calendar ; 
	private ArrayList<PendingIntent> intent_list;
	
	
	
	/*
	 * Project variables
	 */
	private alarm_adapter adapter;
	private Gson gson ;
	private SharedPreferences prefs;
	private static String IP ;
	
	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
	     
		
		alarm_manager =(AlarmManager)this.getSystemService(Context.ALARM_SERVICE);
		calendar = Calendar.getInstance();
		
		intent_list = new ArrayList<PendingIntent>();
		
		this.delay_offsets = new ArrayList<Integer>();
		
//		Intent intent = new Intent(MainActivity.this , AlarmManagerHelper.class);
//		
//		PendingIntent register = PendingIntent.getBroadcast(getBaseContext(), 0, intent,0);
//		intent_list.add(register);
//		
//		calendar.set(Calendar.YEAR,2014);
//		calendar.set(Calendar.MONTH, 10);
//		calendar.set(Calendar.DAY_OF_MONTH, 2);
//		calendar.set(Calendar.HOUR_OF_DAY,13);
//		calendar.set(Calendar.MINUTE,27);
//		calendar.set(Calendar.SECOND, 0);
//		
//		
//		alarm_manager.set(AlarmManager.RTC,calendar.getTimeInMillis(), register);
//		
//		SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss");
//		
//	     Log.d(TAG ,"Time format is :" +formatter.format(calendar.getTime()));
//		
		
		/*
		 * Retrieve the IP from the preferences
		 */
		IP = PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getString("ip", null);
//		Toast.makeText(getApplicationContext(), "Ip of the Raspberry is "+IP, Toast.LENGTH_LONG).show();
		
		
		
		list = (ListView)findViewById(R.id.alarms_list);
		list.setOnItemClickListener(this);
		
		
		/*
		 * Check to see if the alarms are stored already
		 * and get them if they are saved into the phone
		 */
		prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
		gson = new Gson();
	    String json = prefs.getString("alarms",null);
	    Type type = new TypeToken<ArrayList<alarm_item>>(){}.getType();
	    alarms =  gson.fromJson(json, type); 
		
		if(alarms == null){
//			Toast.makeText(getApplicationContext(), "Alarms are not stored into memory ", Toast.LENGTH_LONG).show();
			alarms = new ArrayList<alarm_item>();
		}
		else
//			Toast.makeText(getApplicationContext(), "Alarms are stored into memory ", Toast.LENGTH_LONG).show();
		
		
		adapter = new alarm_adapter(getApplicationContext(), alarms);
		
		
		
		list.setAdapter(adapter);
		
		unRegisterAlarms(alarms);
		registerAlarms(alarms);
		
		
		
//		/*
//		 * Actualizarea alarmelor in Raspberry Pi
//		 * deoarece cand intrii din Activity-ul de
//		 * configurare a alarmelor nu poti face
//		 * astfel incat alarmele modificate sa fie si 
//		 * pe Raspberry
//		 * 
//		 * decat daca faci asta in OnActivityResult() 
//		 */
//		
		
		
//		
//		new DeleteAllAsyncTask().execute();
//		
//		for(alarm_item item : alarms){
//			new PostAsyncTask(item).execute();
//		}
		
		
		
		
	
	}

	
	
	
	
	
	/*
	 * creating an AsyncTask which handles the network operations
	 */
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.main, menu);
		return super.onCreateOptionsMenu(menu);
	}


	



	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		
		switch (item.getItemId()) {
		case com.dreamteam.iot.R.id.action_add:
			Log.d(TAG, "Add button pressed ");
			
			
			final Calendar calendar = Calendar.getInstance();
			int day = calendar.get(Calendar.DAY_OF_MONTH);
			int month = calendar.get(Calendar.MONTH);
			int year = calendar.get(Calendar.YEAR);
			
			final DatePickerDialog dateDialog ;
			
			dateDialog = new CustomDatePickerDialog(this, this, year, month, day);
			dateDialog.setTitle("Select the day");
			dateDialog.show();
			
			return true;

			/*
			 * send the alarms to the raspberry
			 */
		case com.dreamteam.iot.R.id.action_sync:
			Toast.makeText(getApplicationContext(), "Syncing with the Raspberry Pi", Toast.LENGTH_SHORT).show();
			
			syncDataWithRaspberry(); 
			return true;
			
		case com.dreamteam.iot.R.id.action_run:
			RunAsyncTask task = new RunAsyncTask();
			task.execute();
			return true;
		
		case com.dreamteam.iot.R.id.settings:
			Intent settings_intent = new Intent(getApplicationContext(),SettingsActivity.class);
			startActivity(settings_intent);
		default:
			return super.onOptionsItemSelected(item);	
		}
	}



	@Override
	protected void onResume() {
		syncDataWithRaspberry();
		IP = PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getString("ip", null);
		super.onResume();
	}




	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		
		if(requestCode == 200 ){
			

			if(resultCode == RESULT_OK){
				
				alarm_item alarm = (alarm_item)data.getExtras().get("alarm");
				int position = data.getExtras().getInt("position");
				
				
				/*
				 * Remove the alarm and then add it modified to the arrayList 
				 * Based on the sorting algorithm it will place it where it belongs 
				 * 
				 * GREAT SUCCESS !!
				 */
				
				unRegisterAlarms(alarms);
				
				alarms.remove(position);
				alarms = DateOrder.addInList(alarms, alarm.year, alarm.month, alarm.day, alarm.hour, alarm.minute); // static method for inserting the
				
				registerAlarms(alarms);
				//alarm where it belongs is the list
				
				
				/*
				 * Memorize the alarms into shared preferences just after they are modified
				 */
				String json = gson.toJson(alarms);
				PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit().putString("alarms", json).commit();
				adapter.notifyDataSetChanged();
//				list.setAdapter(adapter);
				
				
			}
		}
	}






	@Override
	public void onDateSet(DatePicker view, int year, int monthOfYear,
			int dayOfMonth) {
		Log.d(TAG, "Date selected ");
		
		/*
		 * memorize the values received from the datePickerDialog ,
		 * now show timePickerDialog in order to set the hour and minute 
		 * of the alarms and then onTimeSetCallback is called  
		 * 
		 */
		this.year = year;
		this.month = monthOfYear +1;
		this.day = dayOfMonth;
		
		 
		/*
		 * Set the global calendar variable and then create a pending intent in order
		 * to start the alarm later 
		 */
		calendar.set(Calendar.YEAR, year);
		calendar.set(Calendar.MONTH, month);
		calendar.set(Calendar.DAY_OF_MONTH, day);
		
		
		
		/*
		 * only if no dialog appeared then we show the first one and block the 
		 * second one which randomly appears
		 */
		
		
		if(!selected){
			timeDialog = new CustomTimePickerDialog(this, this, hour, minute, DateFormat.is24HourFormat(this)){
				
			};
			timeDialog.show();
		}
		
		
		selected = false; 
	}
	
	
	
	@Override
	public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
		
		Log.d(TAG, "Time selected ");
		
	 /*
	  * memorize the hour and the minute the user wanted the alarm 
	  * now create an alarm_item and add it to the arrayList in order 
	  * for it to populate the listView
	  * 
	  * I want to save the values to the sharedPreferences of the app
	  */
		
		
		this.hour = hourOfDay;
		this.minute = minute;
		
		
		//add the item to the right position in the alarms vector in order
		//for the alarms to be in order

		alarms = DateOrder.addInList(alarms, year, month, day ,hour , this.minute);
		
		unRegisterAlarms(alarms);
		registerAlarms(alarms);
				
		
		
		/*
		 * now store the alarms int1o memory in order to retrieve them when the 
		 * application starts up 
		 */
		
		String json = gson.toJson(alarms);
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
		prefs.edit().putString("alarms", json).commit();
		
		
		
		adapter.notifyDataSetChanged();
		
	}
	
	
	
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		
		/*
		 * t
	    // Create a calendar object that will convert the date and time value in milliseconds to date. 
	     Calendar calendar2 = Calendar.getInstance();
	     calendar2.setTimeInMillis(calendar.getTimeInMillis());his means that the remove button was pressed so 
		 * we need to handle the deletion
		 */
		if(id == 102){
			Gson gson = new Gson();
			
			unRegisterAlarms(alarms);
			alarms.remove(position);
			registerAlarms(alarms);
			
			String json = gson.toJson(alarms);
			PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit().putString("alarms", json).commit();
			adapter.notifyDataSetChanged();
			
			DeleteAsyncTask task = new DeleteAsyncTask(position);
			task.execute();
			return;
			
		}else if(id == 103){
			
			
			/*
			 * click that was generated using the adapter 
			 * more specifically the "perform click method "
			 * set on every view of the list_item_layout
			 * except the remove button which returns a
			 * different code (102) 
			 */
			Intent intent = new Intent(getApplicationContext(),AlarmItemActivity.class);
			intent.putExtra("position", position);
			intent.putExtra("alarms", alarms);
			startActivityForResult(intent, 200);
			overridePendingTransition( R.anim.slide_in, R.anim.slide_out );
			
			return;
		}else{
			Intent intent = new Intent(getApplicationContext(),AlarmItemActivity.class);
			intent.putExtra("alarms", alarms);
			intent.putExtra("position", position);
			startActivityForResult(intent, 200);
			return;
		}
	}
	
	
	
	
	
	private void unRegisterAlarms(ArrayList<alarm_item> alarms ){
		
		if(intent_list == null){
			intent_list = new ArrayList<PendingIntent>();
		}
		
		int size = intent_list.size();
		
		for(int i=0 ; i < size ; i++){
			alarm_manager.cancel(intent_list.get(i));
		}
		
	}
	
	
	
	private void registerAlarms(ArrayList<alarm_item> alarms){
		
		Calendar calendar = Calendar.getInstance();
		
		if(intent_list == null){
			intent_list = new ArrayList<PendingIntent>();
		}
		
		
		
		for(int i = 0 ; i < alarms.size() ; i++){

			alarm_item item = alarms.get(i);
			
			Calendar calendar2 = new GregorianCalendar(item.year, item.month -1, item.day, item.hour, item.minute);
			
			
			long cuur = System.currentTimeMillis();
			long cuur_alarm = calendar2.getTimeInMillis();
			
			if(System.currentTimeMillis()  >= calendar2.getTimeInMillis() ){
				/*
				 * No need to schedule outdated alarms so we check to see if they are
				 * outdated comparring to the current system time 
				 */
				continue ;
			}
			
			Intent intent = new Intent(MainActivity.this , AlarmManagerHelper.class);
			intent.putExtra("postition", i);
			PendingIntent register = PendingIntent.getBroadcast(getBaseContext(),i, intent,0);
			intent_list.add(register);
			
			calendar.set(Calendar.YEAR, alarms.get(i).year);
			calendar.set(Calendar.DAY_OF_MONTH, alarms.get(i).day);
			calendar.set(Calendar.HOUR_OF_DAY, alarms.get(i).hour);
			calendar.set(Calendar.MINUTE, alarms.get(i).minute);
			calendar.set(Calendar.SECOND, 0);
			
			alarm_manager.set(AlarmManager.RTC, calendar.getTimeInMillis(), register);
			
			SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss");
			
		     Log.d(TAG ,"Time format is :" +formatter.format(calendar.getTime()));

			
		}
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
			HttpPost post = new HttpPost("http://"+IP+":5000/delete/"+position+"/");
			HttpResponse response = null ;
			
			
			
			MultipartEntity ent = new MultipartEntity();
				
				
				try {
					
					ent.addPart("positie",new StringBody(position+""));
					
					
					
					post.setEntity(ent);
					
					response = client.execute(post);
					
					HttpEntity httpEntity = response.getEntity();
		            answer  = EntityUtils.toString(httpEntity);
					
				
					Log.d(TAG, "Response is "+ answer );
				} catch (ClientProtocolException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
				
			
			return answer;
		}
	}

	
	

private class RunAsyncTask extends AsyncTask<Void, String, String>{

		
		private  final int STOPPED = -1;
		HttpClient client ;
		HttpGet post; 
		HttpResponse response;
		HttpEntity entity;
		String answer;
		
	

		protected String doInBackground(Void... params) {
			
			
			HttpClient client = new DefaultHttpClient();
			post = new HttpGet("http://"+IP+":5000/on/");
			HttpResponse response = null ;
			
				try{
					
					response = client.execute(post);
					
					HttpEntity httpEntity = response.getEntity();
		            answer  = EntityUtils.toString(httpEntity);
					
				
					Log.d(TAG, "Response from Run is  "+ answer );
				} catch (ClientProtocolException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
				
			
			return answer;
		}
	}

	

private class DeleteAllAsyncTask extends AsyncTask<Void, String, String>{

		
		private  final int STOPPED = -1;
		HttpClient client ;
		HttpPost post; 
		HttpResponse response;
		HttpEntity entity;
		String answer;
		
		
		
		

		protected String doInBackground(Void... params) {
			
			
			HttpClient client = new DefaultHttpClient();
			HttpPost post = new HttpPost("http://"+IP+":5000/delete/all/");
			HttpResponse response = null ;
			
			
			
			MultipartEntity ent = new MultipartEntity();
				
				
				try {	
					
					post.setEntity(ent);
					
					response = client.execute(post);
					
					HttpEntity httpEntity = response.getEntity();
		            answer  = EntityUtils.toString(httpEntity);
					
				
					Log.d(TAG, "Response is "+ answer );
				} catch (ClientProtocolException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
				
			
			return answer;
		}
	}

	
	
	
	private class PostAsyncTask extends AsyncTask<Void, String, String>{

		
		private  final int STOPPED = -1;
		HttpClient client ;
		HttpPost post; 
		HttpResponse response;
		HttpEntity entity;
		String answer;
		private alarm_item alarma ;
		
		public PostAsyncTask(alarm_item alarma){
			this.alarma = alarma ;
		}

		protected String doInBackground(Void... params) {
			
			
			HttpClient client = new DefaultHttpClient();
			HttpPost post = new HttpPost("http://"+IP+":5000/add/");
			HttpResponse response = null ;
			
			
			
			MultipartEntity ent = new MultipartEntity();
				
				
				try {
					ent.addPart("descriere",new StringBody("Description"));
					ent.addPart("ora",new StringBody(alarma.getHour()+""));
					ent.addPart("minut",new StringBody(alarma.getMinute()+""));
					ent.addPart("zi",new StringBody(alarma.getDay()+""));
					ent.addPart("luna",new StringBody(alarma.getMonth()+""));
					ent.addPart("an",new StringBody(alarma.getYear()+""));
					
					post.setEntity(ent);
					
					response = client.execute(post);
					
					HttpEntity httpEntity = response.getEntity();
		            answer  = EntityUtils.toString(httpEntity);
					
				
					Log.d(TAG, "Response is "+ answer );
				} catch (ClientProtocolException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
				
			
			return answer;
		}
	}


	
	
	
	private class CustomTimePickerDialog extends TimePickerDialog{
		public CustomTimePickerDialog(Context context , OnTimeSetListener listener ,int hour ,int minute,boolean is24){
			super(context,listener,hour,minute,is24);
		}

		@Override
		protected void onStop() {
			
			/*
			 * the show() method is called in onCreate and onStop() so the view appears twice
			 * on the screen . I created my own implementation of the onStop in order to
			 * eliminate the show() method call .
			 * 
			 * So this method implementation should do nothing
			 */
//			   super.onStop(); // THIS IS THE PROBLEM HERE . THIS LINE SHOULD BE COMMENTED
		}
		
		
		
	}


	private class CustomDatePickerDialog extends DatePickerDialog{

		private boolean appeared ; 
		/*
		 * variable for telling us if the dialog already appeared in order not to 
		 * appear twice 
		 */
		
		public CustomDatePickerDialog(Context context,
				OnDateSetListener callBack, int year, int monthOfYear,
				int dayOfMonth) {
			super(context, callBack, year, monthOfYear, dayOfMonth);

			
		}

		@Override
		protected void onCreate(Bundle savedInstanceState) {
			Log.d(TAG, "Dialog appeared");
			super.onCreate(savedInstanceState);
			this.appeared = false ;
		}

		@Override
		public void onDateChanged(DatePicker view, int year, int month, int day) {
			super.onDateChanged(view, year, month, day);
		}

		@Override
		protected void onStop() {
			// TODO Auto-generated method stub
//			super.onStop();
		}
		
		
		
		
	}


	

	private void syncDataWithRaspberry(){
		
		new DeleteAllAsyncTask().execute();
		
		for(alarm_item item : alarms){
			/*
			 * send every alarm to the raspberry
			 */
			PostAsyncTask task = new PostAsyncTask(item);
			task.execute();
			
			
		}
	}
	
}
