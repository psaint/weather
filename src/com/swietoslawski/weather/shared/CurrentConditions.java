package com.swietoslawski.weather.shared;

import java.io.Serializable;

/**
 * Wrapper class for weather's node current_conditions
 * @author peter.swietoslawski
 *
 */
public class CurrentConditions implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private String condition;
	private String temp_f;
	private String temp_c;
	private String humidity;
	private String icon;
	private String wind_condition;
	
	@SuppressWarnings("unused")
	private CurrentConditions() {}
	
	public CurrentConditions(String condition, String temp_f, String temp_c,
			String humidity, String icon, String wind_condition) {
		this.condition = condition;
		this.temp_f = temp_f;
		this.temp_c = temp_c;
		this.humidity = humidity;
		this.icon = icon;
		this.wind_condition = wind_condition;
	}

	public String getCondition() {
		return condition;
	}

	public String getTemp_f() {
		return temp_f;
	}

	public String getTemp_c() {
		return temp_c;
	}

	public String getHumidity() {
		return humidity;
	}

	public String getIcon() {
		return icon;
	}

	public String getWind_condition() {
		return wind_condition;
	}
	
}
