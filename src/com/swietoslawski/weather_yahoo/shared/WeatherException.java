package com.swietoslawski.weather_yahoo.shared;

public class WeatherException extends Exception {

	private static final long serialVersionUID = 1L;
	
	public WeatherException() {
		super();
	}
	
	public WeatherException(String string) {
		super(string);
	}
}
