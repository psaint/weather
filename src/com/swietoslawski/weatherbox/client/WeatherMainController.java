package com.swietoslawski.weatherbox.client;


import java.util.ArrayList;
import java.util.List;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.RootPanel;
import com.swietoslawski.weatherbox.shared.City;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class WeatherMainController implements EntryPoint {
		
	/**
	 * Create a remote service proxy to talk to the server-side Weather service.
	 */
	private final WeatherServiceAsync weatherService = GWT.create(WeatherService.class);
	
	private List<City> cities = new ArrayList<City>(8);

	/**
	 * This is the entry point method.
	 */
	public void onModuleLoad() {
		
		// Create Add Weathercast Dialog widget and bind it to controller
		AddWeatherCast add_weather = new AddWeatherCast(this);
		
		RootPanel.get().add(add_weather);

	}
	
	public WeatherServiceAsync getWeatherService() {
		return weatherService;
	}
	
	public void addCity(City city) {
		cities.add(city);
	}
}
