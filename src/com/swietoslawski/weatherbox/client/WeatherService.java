package com.swietoslawski.weatherbox.client;

import java.util.List;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import com.swietoslawski.weatherbox.shared.City;
import com.swietoslawski.weatherbox.shared.Weather;

//This needs to point to the servlet path defined as mappings in web.xml
@RemoteServiceRelativePath("weather")
public interface WeatherService extends RemoteService {
	
	List<City> findCityLike(String name);
	List<Weather> getWeatherFor(String country, String state, String city);
	
}
