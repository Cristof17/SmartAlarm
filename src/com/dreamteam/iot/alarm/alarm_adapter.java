package com.dreamteam.iot.alarm;

import java.util.ArrayList;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.dreamteam.iot.R;
import com.dreamteam.iot.alarm_item;

public class alarm_adapter extends BaseAdapter{

	private ArrayList<alarm_item> alarms ;
	private Context context ;
	
	
	public alarm_adapter(Context context , ArrayList<alarm_item> alarms ){
		this.context = context;
		this.alarms = alarms;
	}
	
	
	
	
	
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return alarms.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return alarms.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}


	@Override
	public View getView(final int position, View convertView, final ViewGroup parent) {	
	
		/*
		 * here we check to see if the convertView is null this means that the 
		 * textFields were identified once so no need to identify them again
		 * this is why we have a viewHolder .Quite Clever !! 
		 */
		
		
		
			
			LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = inflater.inflate(com.dreamteam.iot.R.layout.item_layout,parent,false);
		
			alarm_item alarma = alarms.get(position);
			
			if(convertView == null){
//				Toast.makeText(context, "convertView is null", Toast.LENGTH_LONG).show();
			}
			
			else{
				
				
				TextView hour_field = (TextView)convertView.findViewById(R.id.h_text_field);
				hour_field.setOnClickListener(new View.OnClickListener() {
					
					@Override
					public void onClick(View v) {
						((ListView) parent).performItemClick(v,position,103);
					}
				});
				
				TextView minute_field = (TextView)convertView.findViewById(R.id.m_text_field);
				minute_field.setOnClickListener(new View.OnClickListener() {
					
					@Override
					public void onClick(View v) {
						((ListView) parent).performItemClick(v,position,103);
					}
				});
				
				TextView day_field = (TextView)convertView.findViewById(R.id.d_text_field);
				day_field.setOnClickListener(new View.OnClickListener() {
					
					@Override
					public void onClick(View v) {
						((ListView) parent).performItemClick(v,position,103);
					}
				});
				
				TextView month_field = (TextView)convertView.findViewById(R.id.mo_text_field);
				month_field.setOnClickListener(new View.OnClickListener() {
					
					@Override
					public void onClick(View v) {
						((ListView) parent).performItemClick(v,position,103);
					}
				});
				
				TextView year_field = (TextView)convertView.findViewById(R.id.y_text_field);
				year_field.setOnClickListener(new View.OnClickListener() {
					
					@Override
					public void onClick(View v) {
						((ListView) parent).performItemClick(v,position,103);
					}
				});
				
				ImageButton removeButton = (ImageButton)convertView.findViewById(R.id.remove_button);
				removeButton.setOnClickListener(new View.OnClickListener() {
					
					@Override
					public void onClick(View v) {
						((ListView) parent).performItemClick(v,position,102);
						
					}
				});
				
				hour_field.setText(alarms.get(position).getHour()+"");
				minute_field.setText(alarms.get(position).getMinute()+"");
				day_field.setText(alarms.get(position).getDay()+"");
				month_field.setText(alarms.get(position).getMonth()+"");
				year_field.setText(alarms.get(position).getYear()+"");
				
			}
//			
		
		return convertView;
		
	}
	
	

	
}
