package com.swietoslawski.weather.shared;

import java.io.Serializable;

public class Forecast implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	/* Forecast */
	private String day_of_week;
	private String low;
	private String high;
	private String icon;
	private String condition;
	
	@SuppressWarnings("unused")
	private Forecast() {}
	
	public Forecast(String day_of_week, String low, String high, String icon,
			String condition) {
		this.day_of_week = day_of_week;
		this.low = low;
		this.high = high;
		this.icon = icon;
		this.condition = condition;
	}

	public String getDay_of_week() {
		return day_of_week;
	}

	public String getLow() {
		return low;
	}

	public String getHigh() {
		return high;
	}

	public String getIcon() {
		return icon;
	}

	public String getCondition() {
		return condition;
	}
}
