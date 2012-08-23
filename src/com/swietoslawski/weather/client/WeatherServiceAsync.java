package com.swietoslawski.weather.client;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.swietoslawski.weather.shared.WeatherWrapper;

public interface WeatherServiceAsync {

	void getWeather(String city,
			AsyncCallback<WeatherWrapper> callback);

}
