package com.dreamteam.iot;

import java.io.Serializable;

public class alarm_item implements Serializable{
	
	/*
	 * 
	 * class for remembering an alarm item which is then inserted into a listView 
	 */
	
	public int year, month ,day, hour, minute,intentRequestCode;
	
	public alarm_item(int year , int month ,int day ,int hour,int minute,int intentRequestCode){
		
		this.year = year;
		this.month = month;
		this.day = day ;
		this.hour = hour;
		this.minute = minute ;
		this.day = day;
		this.intentRequestCode = intentRequestCode ; 
		
	}
	
	

	public int getYear() {
		return year;
	}

	public void setYear(int year) {
		this.year = year;
	}

	public int getMonth() {
		return month;
	}

	public void setMonth(int month) {
		this.month = month;
	}

	public int getDay() {
		return day;
	}

	public void setDay(int day) {
		this.day = day;
	}

	public int getHour() {
		return hour;
	}

	public void setHour(int hour) {
		this.hour = hour;
	}

	public int getMinute() {
		return minute;
	}

	public void setMinute(int minute) {
		this.minute = minute;
	}
	
	
	
}
