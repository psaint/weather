package com.swietoslawski.weather.shared;

import java.io.Serializable;

/**
 * Wrapper class for weather's node forecast_information
 * @author peter.swietoslawski
 *
 */
public class ForecastInformation implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private String city;
	private String forecast_date;
	
	@SuppressWarnings("unused")
	private ForecastInformation() {}
	
	public ForecastInformation(String city, String forecast_date) {
		super();
		this.city = city;
		this.forecast_date = forecast_date;
	}
	
	public String getCity() {
		return city;
	}
	
	public String getForecast_date() {
		return forecast_date;
	}
}
