package com.swietoslawski.weather_yahoo.client;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.swietoslawski.weather_yahoo.shared.WeatherWrapper;

public interface WeatherServiceAsync {

	void getWeatherHtml(String zip, boolean isCelcius,
			AsyncCallback<WeatherWrapper[]> callback);

}
