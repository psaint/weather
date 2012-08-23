package com.swietoslawski.weather.shared;

import java.io.Serializable;

public class WeatherWrapper implements Serializable {

	private static final long serialVersionUID = 1L;

	// Three main types of object returned in weather forecast
	private ForecastInformation forecastInformation;
	private CurrentConditions currentConditions;
	private Forecast[] forecastConditions;
	
	@SuppressWarnings("unused")
	private WeatherWrapper() {}
	
	public WeatherWrapper(ForecastInformation forecastInformation,
			CurrentConditions currentConditions,
			Forecast[] forecastConditions) {
		super();
		this.forecastInformation = forecastInformation;
		this.currentConditions = currentConditions;
		this.forecastConditions = forecastConditions;
	}


	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	public ForecastInformation getForecastInformation() {
		return forecastInformation;
	}
	
	public CurrentConditions getCurrentConditions() {
		return currentConditions;
	}

	public Forecast[] getForecastConditions() {
		return forecastConditions;
	}
}
