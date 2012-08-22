package com.swietoslawski.weather_yahoo.shared;

import java.io.Serializable;

public class WeatherWrapper implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String city;
	private String temp;
	private String condition;
	private String day;
	
	public WeatherWrapper() {}
	
	public WeatherWrapper(String city, String temp, String condition, String day) {
		this.city = city;
		this.temp = temp;
		this.condition = condition;
		this.day = day;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	public String getCity() {
		return city;
	}

	public String getTemp() {
		return temp;
	}

	public String getCondition() {
		return condition;
	}

	public String getDay() {
		return day;
	}
}
