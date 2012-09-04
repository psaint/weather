package com.swietoslawski.weatherbox.client;

import java.util.List;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.swietoslawski.weatherbox.shared.City;
import com.swietoslawski.weatherbox.shared.Weather;

public interface WeatherServiceAsync {
	
	void findCityLike(String name, AsyncCallback<List<City>> callback);
	void getWeatherFor(City city, AsyncCallback<List<Weather>> asyncCallback);
	
}
