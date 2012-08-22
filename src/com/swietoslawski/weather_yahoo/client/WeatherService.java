package com.swietoslawski.weather_yahoo.client;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import com.swietoslawski.weather_yahoo.shared.WeatherException;
import com.swietoslawski.weather_yahoo.shared.WeatherWrapper;

// This needs to point to the servlet pat defined as mappings in web.xml
@RemoteServiceRelativePath("weatherService")
public interface WeatherService extends RemoteService {
	WeatherWrapper[] getWeatherHtml(String zip, boolean isCelcius) throws WeatherException;
}
