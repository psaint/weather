package com.swietoslawski.weather.client;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import com.swietoslawski.weather.shared.WeatherException;
import com.swietoslawski.weather.shared.JSO.Weather;

// This needs to point to the servlet pat defined as mappings in web.xml
@RemoteServiceRelativePath("weatherService")
public interface WeatherService extends RemoteService {
	
	Weather getWeather(String city) throws WeatherException;
}