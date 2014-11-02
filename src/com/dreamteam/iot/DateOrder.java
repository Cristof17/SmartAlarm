package com.dreamteam.iot;

import java.util.ArrayList;

import android.widget.Toast;

public class DateOrder {

public static ArrayList<alarm_item> addInList(ArrayList<alarm_item> alarms, int year, int month , int day ,int hour ,int minute){
		
		if(alarms.size() == 0 ){
			alarms.add(new alarm_item(year, month, day, hour, minute,0));
			return alarms;
		}else {
			
			for (int i = 0 ; i < alarms.size() ; i++){
				
				if(year < alarms.get(i).year){
					alarms.add(i, new alarm_item(year, month, day, hour, minute,i));
					return alarms;
				}else if(year == alarms.get(i).year){
					if(month < alarms.get(i).month){
						alarms.add(i, new alarm_item(year, month, day, hour, minute,i));
						return alarms;
					}else if(month == alarms.get(i).month){
						if(day < alarms.get(i).day){
							alarms.add(i, new alarm_item(year, month, day, hour, minute,i));
							return alarms;
						}else if (day == alarms.get(i).day){
							if(hour < alarms.get(i).hour){
								alarms.add(i, new alarm_item(year, month, day, hour, minute,i));
								return alarms;
							}else if (hour == alarms.get(i).hour){
								if(minute < alarms.get(i).minute){
									alarms.add(i, new alarm_item(year, month, day, hour, minute,i));
									return alarms;
								}else if(minute == alarms.get(i).minute){
									return alarms;
								}else{
									continue;
								}
							}else{
								continue;
							}
						}else{
							continue;
						}
					}else{
						continue;
					}
					
				}else{
					continue;
				}
			}
		}
		
		alarms.add(new alarm_item(year, month, day, hour, minute,alarms.size()));
		return alarms;
	}

	
}
