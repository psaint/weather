package com.swietoslawski.weatherbox.server;

import java.util.List;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.swietoslawski.weatherbox.client.WeatherService;
import com.swietoslawski.weatherbox.shared.City;
import com.swietoslawski.weatherbox.shared.Weather;

public class WeatherServiceImpl extends RemoteServiceServlet implements WeatherService {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	
	public WeatherServiceImpl() {}

	public List<City> findCityLike(String name) {
		WeatherController weather = new WeatherController();
		
		List<City> cities = weather.findCity(name);
		
		return cities;
	}
	
	public List<Weather> getWeatherFor(String country, String state, String city) {
		WeatherController weather = new WeatherController();		
				
		weather.extractWeatherFor(country, state, city);
		List<Weather> weather_cast = weather.getWeatherCast();
		
		return weather_cast;
	}
}
