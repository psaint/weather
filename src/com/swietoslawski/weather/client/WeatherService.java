package com.swietoslawski.weather.client;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import com.swietoslawski.weather.shared.WeatherWrapper;
import com.swietoslawski.weather.shared.WeatherException;

// This needs to point to the servlet pat defined as mappings in web.xml
@RemoteServiceRelativePath("weatherService")
public interface WeatherService extends RemoteService {
	
	WeatherWrapper getWeather(String city) throws WeatherException;
}