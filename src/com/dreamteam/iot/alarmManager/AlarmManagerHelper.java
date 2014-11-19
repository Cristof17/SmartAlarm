package com.dreamteam.iot.alarmManager;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.concurrent.ExecutionException;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.entity.BasicHttpEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

public class AlarmManagerHelper extends BroadcastReceiver {

	private String IP ;
	private volatile boolean is_pressed; 
	private int number_of_thread_calls = 0;
	
	
	@Override
	public void onReceive(Context context, Intent intent) {
		
		int id = intent.getExtras().getInt("position");
		Toast.makeText(context, "Alarm is making a strage sound  "+ id + " \n\n stop it ...." ,Toast.LENGTH_SHORT).show();
		Log.d("ALARM_MANAGER_HELPER", "Alarm with position "+ id + " goes off");
		
		IP = PreferenceManager.getDefaultSharedPreferences(context).getString("ip", null);
		
		RunAsyncTask task = new RunAsyncTask(); 
		task.execute();
		Log.d("ALARM_MANAGER_HELPER", "RunAsyncTask started from Broadcast Receiver ");
		
		
		
		/*
		 * if the raspberry doesn't reply after 20 seconds give up 
		 * creating threads
		 */
		while(!is_pressed && number_of_thread_calls < 20){
			
			new SnoozeAsyncTask().execute();
			Log.d("ALARM_MANAGER_HELPER", "Running while() loop "); 
			
			try {
				
				Thread.sleep(1000);
				
			} catch (InterruptedException e) {
				
				e.printStackTrace();
			}
		}
		
		
		 
	}
	
	
	
	
	
	
	private class RunAsyncTask extends AsyncTask<Void, String, String>{

		
		private  final int STOPPED = -1;
		HttpClient client ;
		HttpGet post; 
		HttpResponse response;
		HttpEntity entity;
		String answer;
		
	

	 protected final String doInBackground(Void... params) {
			
			
			HttpClient client = new DefaultHttpClient();
			post = new HttpGet("http://"+IP+":5000/on/");
			HttpResponse response = null ;
			
				try{
					

					response = client.execute(post);
					
					HttpEntity httpEntity = response.getEntity();
		            answer  = EntityUtils.toString(httpEntity);
					
				
					Log.d("ALARM_MANAGER_HELPER", "Response from Run is  "+ answer );
				} catch (ClientProtocolException e) {
					
					e.printStackTrace();
					
				} catch (IOException e) {
					
					e.printStackTrace();
					
				}
				
			
			return answer;
		}
	}


	public class SnoozeAsyncTask extends AsyncTask<Void, Boolean, Boolean>{

		
		private HttpEntity entity ;
		private HttpGet get ;
		private HttpResponse response ;
		private HttpClient client;  
		private Integer raspuns ;
		private Boolean bool ;
		
		
		
		@Override
		protected Boolean doInBackground(Void... params) {	
			
			
			get = new HttpGet("http://"+IP+":5000/snooze/");
			entity = new BasicHttpEntity();
			client = new DefaultHttpClient();
		
			try {
				
				number_of_thread_calls ++;
				
				response = client.execute(get);
				
				
				String response_string = EntityUtils.toString(entity);
				
				is_pressed = new Boolean(response_string);
				
				
				Log.d("ALARM_MANAGER_HELPER", "Response from SnoozeAsyncTask " + response_string );
				
			} catch (ClientProtocolException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			return bool;
			
		}
		
		
	}
	
	
	
	
	
}
