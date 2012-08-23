package com.swietoslawski.weather.shared;

import java.text.SimpleDateFormat;
import java.util.Date;

public class TestThrowaway {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		Date date = new Date();
		SimpleDateFormat df = new SimpleDateFormat("E");
		String weekday = df.format(date);
		
		System.out.println(weekday);	
	}

}
